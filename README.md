## 一个接入简单，并持续维护的Android内置应用升级框架
| 功能  |
|:----------|
| 内置文件下载    |
| 支持md5校验    |
| 支持强制更新    |
| 支持各版本通知栏显示下载详情(开发中)    |
| 支持自定义下载框样式(开发中)    |


## 一、使用依赖

#### 1、在project下的build.gradle添加

```
allprojects {
    repositories {
		...
        maven { url 'https://jitpack.io' }
    }
}
```

#### 2、直接引入
```
dependencies {
	 implementation 'com.github.androidcwy:SmartAppUpdate:Tag'
}
```

## 二、基础使用
```
           AppInfo appInfo = new AppInfo()
                    .apkUrl(data.newAppUrl)
                    .apkMD5(data.md5)
                    .autoInstall(true)
                    .apkSize(data.newAppSize)
                    .content(data.newAppUpdateDesc)
                    .newVersionText(data.newAppVersionName)
                    .isUserAction(isUserAction)
                    .forceUpgrade(forceUpgrade);
            AppUpdate appUpdate = new AppUpdate(appInfo);
            appUpdate.show((FragmentActivity) context);
```


## 三、其他
#### 作者持续更新【开发中】需求，如果你有通用需求，欢迎通过issue提给我。