package my.xubaipei.downloader;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception{
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("my.xubaipei.downloader.test", appContext.getPackageName());
        String dest = "/sdcard/Android/data/com.hytch/Tick";
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
