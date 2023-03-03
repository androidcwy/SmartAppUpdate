package com.smart.appupdate.download

import com.smart.appupdate.utils.LogUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * @author JoeYe
 * @date 2023/1/18 14:27
 */
@Suppress("BlockingMethodInNonBlockingContext")
class HttpDownloadManager() {

    companion object{
        const val READ_TIMEOUT = 30000
        const val CONNECT_TIMEOUT = 30000
        const val TAG = "HttpDownloadManager"

        val sInstance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpDownloadManager()
        }
    }
    private var isDownloading: Boolean = false
    private var shutdown: Boolean = false

    fun downloadApk(dirPath: String, apkUrl: String, apkName: String): Flow<DownloadStatus> {
        if (isDownloading){
            return flow {
                emit(DownloadStatus.Error(IllegalStateException("Currently downloading!!!")))
            }
        }
        isDownloading = true

        trustAllHosts()
        shutdown = false
        File(dirPath, apkName).let {
            if (it.exists()) {
                it.delete()
            }
        }
        return flow {
            emit(DownloadStatus.Start)
            connectToDownload(dirPath, apkUrl, apkName, this)
        }.catch {
            emit(DownloadStatus.Error(it))
        }.onCompletion {
            isDownloading = false
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun connectToDownload(
        dirPath: String,
        apkUrl: String,
        apkName: String,
        flow: FlowCollector<DownloadStatus>
    ) {
        val con = URL(apkUrl).openConnection() as HttpURLConnection
        con.apply {
            requestMethod = "GET"
            readTimeout = READ_TIMEOUT
            connectTimeout = CONNECT_TIMEOUT
            setRequestProperty("Accept-Encoding", "identity")
        }
        if (con.responseCode == HttpURLConnection.HTTP_OK) {
            val inStream = con.inputStream
            val length = con.contentLength
            var len: Int
            var progress = 0
            val buffer = ByteArray(1024 * 2)
            val file = File(dirPath, apkName)
            file.let {
                if (it.exists()) it.delete() else it.parentFile?.mkdirs()
                it.createNewFile()
            }

            FileOutputStream(file).use { out ->
                while (inStream.read(buffer).also { len = it } != -1 && !shutdown) {
                    out.write(buffer, 0, len)
                    progress += len
                    flow.emit(DownloadStatus.Downloading(length, progress))
                }
                out.flush()
            }
            inStream.close()
            if (shutdown) {
                flow.emit(DownloadStatus.Cancel)
            } else {
                flow.emit(DownloadStatus.Done(file))
            }
        } else if (con.responseCode == HttpURLConnection.HTTP_MOVED_PERM
            || con.responseCode == HttpURLConnection.HTTP_MOVED_TEMP
        ) {
            con.disconnect()
            val locationUrl = con.getHeaderField("Location")
            LogUtil.d(
                TAG,
                "The current url is the redirect Url, the redirected url is $locationUrl"
            )
            connectToDownload(dirPath, locationUrl, apkName, flow)
        } else {
            val e = SocketTimeoutException("Error: Http response code = ${con.responseCode}")
            flow.emit(DownloadStatus.Error(e))
        }
        con.disconnect()
    }

    /**
     * fix https url (SSLHandshakeException) exception
     */
    private fun trustAllHosts() {
        val manager: TrustManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                LogUtil.d(TAG, "checkClientTrusted")
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                LogUtil.d(TAG, "checkServerTrusted")
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }


        }
        try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf(manager), SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
        } catch (e: Exception) {
            LogUtil.e(TAG, "trustAllHosts error: $e")
        }
    }

    fun cancelIfRunning(){
        shutdown = true
    }

}