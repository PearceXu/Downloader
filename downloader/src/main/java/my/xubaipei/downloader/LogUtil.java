package my.xubaipei.downloader;

import android.util.Log;


/**
 * author ： xubaipei
 * create date： 2018-12-18
 */
public class LogUtil {
    public static void log(String message){
        if (!BuildConfig.DEBUG){
            return;
        }
        Log.i("AndroidDownloader",message);
    }
}
