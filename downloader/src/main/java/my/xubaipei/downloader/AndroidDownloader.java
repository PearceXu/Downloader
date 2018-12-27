package my.xubaipei.downloader;

import android.os.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static my.xubaipei.downloader.LogUtil.log;
import static my.xubaipei.downloader.Message.MSG_CHILD_TASK_SUCCESS;
import static my.xubaipei.downloader.Message.MSG_CONNECT;
import static my.xubaipei.downloader.Message.MSG_DONWLOAD;
import static my.xubaipei.downloader.Message.MSG_TASK_CANCEL;
import static my.xubaipei.downloader.Message.MSG_TASK_ERROR;
import static my.xubaipei.downloader.Message.MSG_TASK_MERGE_FILE;
import static my.xubaipei.downloader.Message.MSG_TASK_PROGRESS;
import static my.xubaipei.downloader.Message.MSG_TASK_START;
import static my.xubaipei.downloader.Message.MSG_TASK_SUCCESS;


/**
 * Created by xubaipei on 2018/12/17.
 */

public class AndroidDownloader implements Handler.Callback{
    String mUrl;
    String mPath;
    MessageQueen mHandler;
    public final String TAG = "AndroidDownloader";
    CallBack mCallBack = null;
    int mTaskCount = 3;
    List<DownloadInfo> mChildFilePath;
    long mContentLenght = 0;
    long mAllTaskLenght = 0;
    int mChildSuccessTimes = 0;
    Handler mAndroidHandler;
    List<DownloadThread> mThreads = null;

    @Override
    public boolean handleMessage(android.os.Message message) {
        switch (message.what) {
            case MSG_TASK_PROGRESS:
                int progress = (int)message.obj;
                if (mCallBack != null){
                    mCallBack.onProgress(progress);
                }
                break;
            case MSG_TASK_SUCCESS:
                if (mCallBack != null){
                    mCallBack.onFinish(mPath);
                }
                break;
            case MSG_TASK_ERROR:
                Throwable e = (Throwable) message.obj;
                if (mCallBack != null){
                    mCallBack.onError(e);
                }
                break;
        }

        return false;
    }

    public AndroidDownloader(final String mUrl, final String path, int threadCount) {
        mAndroidHandler = new Handler(this);
        mTaskCount = threadCount;
        mThreads = new ArrayList<>();
        this.mUrl = mUrl;
        this.mPath = getDestPath(mUrl,path);
        mHandler = new MessageQueen(){
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what){
                    case MSG_CONNECT:
                        log("--MSG_CONNECT");
                        try {
                            HttpURLConnection connection = connect(mUrl);
                            int code = connection.getResponseCode();
                            if (code != 200){
                                connection.getInputStream().close();
                                mHandler.sendMessage(Message.obtainMessage(MSG_TASK_ERROR,new Exception("http error code:"+code)));
                                return true;
                            }
                            long contentLength = connection.getContentLength();
                            if (contentLength <= 0){
                                connection.getInputStream().close();
                                mHandler.sendMessage(Message.obtainMessage(MSG_TASK_ERROR,new Exception("content lenght <= 0")));
                                break;
                            }
                            mContentLenght = contentLength;
                            mHandler.sendMessage(Message.obtainMessage(MSG_TASK_START,
                                    contentLength));
                            connection.getInputStream().close();
                        }catch (Exception e){
                            e.printStackTrace();
                            mHandler.sendMessage(Message.obtainMessage(MSG_TASK_ERROR,e));
                        }
                        break;
                    case MSG_DONWLOAD:
                        log("--MSG_DONWLOAD");
                        DownloadInfo info = (DownloadInfo) message.args[0];
                        if (info == null){
                            info = new DownloadInfo();
                            info.url = mUrl;
                            info.path = mPath;
                        }
                        DownloadThread thread = new DownloadThread(info.url,
                                info.path,info.mOffset,info.contentLenght,mHandler);
                        thread.start();
                        mThreads.add(thread);
                        break;
                    case MSG_CHILD_TASK_SUCCESS:
                        mChildSuccessTimes++;
                        log("mChildSuccessTimes:"+mChildSuccessTimes);
                        if (mChildSuccessTimes == mChildFilePath.size()){
                            log("--MSG_CHILD_TASK_SUCCESS time:"+mChildSuccessTimes);
                            Message msg = Message.obtainMessage(MSG_TASK_MERGE_FILE);
                            mHandler.sendMessage(msg);
                        }
                        break;
                    case MSG_TASK_ERROR:
                        for (int i = 0; i < mThreads.size();i++){
                            try {
                                mThreads.get(i).interrupt();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        mAndroidHandler.sendMessage(android.os.Message.obtain(mAndroidHandler,
                                MSG_TASK_ERROR,message.args[0]));
                        break;
                    case MSG_TASK_PROGRESS:
                        log("MSG_TASK_PROGRESS");
                        long increase = (long)message.args[0];
                        mAllTaskLenght += increase;
                        int progress = (int)(mAllTaskLenght * 100 /mContentLenght);
                        log("MSG_TASK_PROGRESS"+"progress:"+progress);
                        mAndroidHandler.sendMessage(android.os.Message.obtain(mAndroidHandler,
                                MSG_TASK_PROGRESS,progress));
                        break;
                    case MSG_TASK_START:
                        log("--MSG_TASK_START");
                        long contentLengh = (long)message.args[0];
                        log("--MSG_TASK_START:"+contentLengh);
                        long perTaskQuantity = contentLengh / mTaskCount;
                        long lastPerTaskQuantity = perTaskQuantity + contentLengh % perTaskQuantity;
                        try {
                            File file = new File(mPath);
                            if (!file.exists()){
                                file.getParentFile().mkdirs();
                                file.createNewFile();
                            }
                            FileInputStream inputStream = new FileInputStream(file);
                            long size = inputStream.available();
                            if (size == contentLengh){
                                mHandler.sendMessage(Message.obtainMessage(MSG_TASK_SUCCESS));
                                break;
                            }
                            long startPosition = 0;
                            for (int i = 0;i < mTaskCount;i++){
                                String childPath = String.format(Locale.US,"%s_%s",mPath,
                                        i);
                                DownloadInfo downloadInfo  = new DownloadInfo();
                                downloadInfo.url = mUrl;
                                downloadInfo.path = childPath;
                                downloadInfo.mOffset = startPosition;
                                downloadInfo.contentLenght = i == mTaskCount -1?
                                        lastPerTaskQuantity:perTaskQuantity;
                                mChildFilePath.add(downloadInfo);
                                startPosition += downloadInfo.contentLenght;
                                Message msg = Message.obtainMessage(MSG_DONWLOAD,downloadInfo);
                                mHandler.sendMessage(msg);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            mHandler.sendMessage(Message.obtainMessage(MSG_TASK_ERROR,e));
                        }
                        break;
                    case MSG_TASK_MERGE_FILE:
                        log("---------------------MSG_TASK_MERGE_FILE:");
                        try {
                            RandomAccessFile randomAccessFile = new RandomAccessFile(mPath,"rwd");
                            int offset = 0;
                            byte[] data  = new byte[1024];
                            for (DownloadInfo childInfo :mChildFilePath){
                                FileInputStream fileInputStream = new FileInputStream(childInfo.path);
                                offset = 0;
                                while (offset != -1){
                                    offset = 0;
                                    offset = fileInputStream.read(data);
                                    if (offset != -1){
                                        randomAccessFile.write(data,0,offset);
                                    }
                                }
                                fileInputStream.close();
                            }
                            randomAccessFile.close();
                            for (DownloadInfo childInfo :mChildFilePath){
                                File file = new File(childInfo.path);
                                file.delete();
                            }
                            mHandler.sendMessage(Message.obtainMessage(MSG_TASK_SUCCESS));
                        }catch (Exception e){
                            e.printStackTrace();
                            mHandler.sendMessage(Message.obtainMessage(MSG_TASK_ERROR,e));
                        }
                        break;
                    case MSG_TASK_SUCCESS:
                        log("file download success !!!");
                        mAndroidHandler.sendEmptyMessage(MSG_TASK_SUCCESS);
                        break;
                }
                return super.handleMessage(message);
            }
        };
        initDownloader();
    }

    private void initDownloader(){
        mChildFilePath = new ArrayList<>();
    }

    public void download(CallBack callBack){
        mCallBack = callBack;
        mHandler.sendMessage(Message.obtainMessage(MSG_CONNECT));
    }

    public void stop(){
        if (mThreads == null){
            return;
        }
        for (int i = 0; i < mThreads.size();i++){
            try {
                mThreads.get(i).interrupt();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        mThreads.clear();
    }

    private static String getDestPath(String url,String dest){
        File destFile = new File(dest);
        if (destFile.isDirectory()){
            String name = prase(url);
            String path = "";
            path = String.format(Locale.US,"%s%s%s",dest,File.separator,name);
            return path;
        }
        return dest;
    }

    private static String prase(String content){
        String name = "";
        String[] list = content.split("/");
        if (list.length == 0){
            return name;
        }
        name = list[list.length - 1];
        return name;
    }
    private static HttpURLConnection connect(String url)throws IOException {
        URL localURL = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)localURL.openConnection();
        return connection;
    }

    public class DownloadInfo{
        String url;
        String path;
        long mOffset = 0;
        long contentLenght = 0;
    }
    public interface CallBack{
        void onProgress(int progress);
        void onError(Throwable e);
        void onFinish(String path);
    }
}
