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
    }
}