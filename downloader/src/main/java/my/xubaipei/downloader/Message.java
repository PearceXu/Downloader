package my.xubaipei.downloader;

/**
 * Created by xubaipei on 2018/12/17.
 */

public class Message {
    public static final int MSG_CONNECT = 0x01;
    public static final int MSG_DONWLOAD = 0x02;
    public static final int MSG_CHILD_TASK_SUCCESS = 0x03;
    public static final int MSG_TASK_ERROR = 0x04;
    public static final int MSG_TASK_PROGRESS = 0x05;
    public static final int MSG_TASK_START = 0x06;
    public static final int MSG_TASK_MERGE_FILE = 0x07;
    public static final int MSG_TASK_SUCCESS= 0x08;



    public int what;
    public String desc;
    Object[] args;

    public Message(int what) {
        this.what = what;
    }

    public Message(int what, Object[] args) {
        this.what = what;
        this.args = args;
    }

    public Message(int what, String desc, Object[] args) {
        this.what = what;
        this.desc = desc;
        this.args = args;
    }

    public static my.xubaipei.downloader.Message obtainMessage(int what, Object... args){
        String desc = "";
        switch (what){
            case MSG_CONNECT:
                desc = "MSG_CONNECT";
                break;
            case MSG_DONWLOAD:
                desc = "MSG_DONWLOAD";
                break;
            case MSG_CHILD_TASK_SUCCESS:
                desc = "MSG_CHILD_TASK_SUCCESS";
                break;
            case MSG_TASK_ERROR:
                desc = "MSG_TASK_ERROR";
                break;
            case MSG_TASK_PROGRESS:
                desc = "MSG_TASK_PROGRESS";
                break;
            case MSG_TASK_START:
                desc = "MSG_TASK_START";
                break;
            case MSG_TASK_MERGE_FILE:
                desc = "MSG_TASK_MERGE_FILE";
                break;
            case MSG_TASK_SUCCESS:
                desc = "MSG_TASK_SUCCESS";
                break;
        }
        return new my.xubaipei.downloader.Message(what,desc,args);
    }
}
