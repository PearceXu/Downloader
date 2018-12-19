# Downloader
Downloader in java 多线程下载器，支持断点下载，进度回传

# 集成
    maven { url "https://jitpack.io" }
    implementation 'com.github.PearceXu:Downloader:1.0.7'

# 用法
 String dest = "C:\\Users\\xubp\\Desktop\\video";
 String url = "https://c4b130d63ab50d8b92f23a952cc18836.dd.cdntips.com/download.sj.qq.com/upload/connAssitantDownload/upload/MobileAssistant_1.apk";
 url = "http://10.98.0.232:9004/AppUpdate/H5File/20181211/web.zip";
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
