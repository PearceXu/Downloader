package my.xubaipei.downloader;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception{
        assertEquals(4, 2 + 2);
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
        Thread.sleep(1000 * 60 * 60);
    }
}