package my.xubaipei.downloader;

/**
 * author ： xubaipei
 * create date： 2018-12-18
 */
public class LogUtil {
    static boolean PRINT_LOG = true;
    public static void log(String message){
        if (PRINT_LOG){
            return;
        }
        String name = Thread.currentThread().getName();
        System.out.println("------------name:"+name+"size:"+message);
    }
}
