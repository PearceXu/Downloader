# Downloader
Downloader in java 多线程下载器，支持断点下载，进度回传

# 集成
    maven { url "https://jitpack.io" }
    implementation 'com.github.PearceXu:Downloader:1.0.11'
# Android用法
```java
        String dest = "/sdcard/Android/data/com.example.xubaipei/files/";
        String url = "http://10.98.0.232:9004/AppUpdate/H5File/20181211/web.zip";
        AndroidDownloader downloader = new AndroidDownloader(url,dest,1);
        downloader.download(new AndroidDownloader.CallBack() {
            @Override public void onProgress(int progress) {
                System.out.println(progress);
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                System.out.println(e.toString());
            }
            @Override
            public void onFinish(String path) {
                System.out.println(path);
            }
        });
```
# java用法
```java
String dest = "C:\\Users\\xubp\\Desktop\\video";
String url = "http://10.98.0.232:9004/AppUpdate/H5File/20181211/web.zip";
Downloader downloader = new Downloader(url,dest,1);
 downloader.download(new Downloader.CallBack() {
     @Override
     public void onProgress(int progress) {
         System.out.println(progress);
     }

     @Override
     public void onError(Throwable e) {
         e.printStackTrace();
         System.out.println(e.toString());
     }

     @Override
     public void onFinish(String path) {
         System.out.println(path);
     }
 });
 ```

