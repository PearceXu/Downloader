package my.xubaipei.downloader;

/**
 * author ： xubaipei
 * create date： 2018-12-24
 */
public class HttpException extends Exception {
    int code;
    String dest;

    public HttpException(int code, String dest) {
        this.code = code;
        this.dest = dest;
    }
}
