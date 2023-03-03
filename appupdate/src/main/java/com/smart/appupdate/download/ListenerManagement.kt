package com.smart.appupdate.download

/**
 * @author JoeYe
 * @date 2023/2/6 17:34
 */
class ListenerManagement private constructor(){

    var downloadStatusListeners: MutableList<DownloadStatusListenerAdapter> = mutableListOf<DownloadStatusListenerAdapter>()

    companion object {
        internal var instance: ListenerManagement? = null

        @Synchronized
        fun get(): ListenerManagement {
            if (null == instance) instance = ListenerManagement()
            return instance as ListenerManagement
        }
    }


}