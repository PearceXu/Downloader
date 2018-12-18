package my.xubaipei.downloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

import static my.xubaipei.downloader.LogUtil.log;

/**
 * Created by xubaipei on 2018/12/17.
 */

public class DownloadThread extends Thread {
    String mUrl;
    String mDest;
    MessageQueen mHandler;
    long mOffset = 0;
    long mFileOffset = 0;
    long mTaskLenght = 0;

    public DownloadThread(String mUrl, String mDest,long offset,long taskLenght,MessageQueen mQueen) {
        this.mUrl = mUrl;
        this.mDest = mDest;
        this.mHandler = mQueen;
        mOffset = offset;
        mTaskLenght  = taskLenght;
    }

    @Override
    public void run() {
        try {
            File destFile =  new File(mDest);
            if (!destFile.exists()){
                destFile.getParentFile().mkdirs();
                destFile.createNewFile();
            }else{
                InputStream inputStream = new FileInputStream(destFile);
                mFileOffset = inputStream.available();
            }
            if (mFileOffset >= mTaskLenght){
                mHandler.sendMessage(Message.obtainMessage(Message.MSG_TASK_PROGRESS,mFileOffset,mDest));
                mHandler.sendMessage(Message.obtainMessage(Message.MSG_CHILD_TASK_SUCCESS));
                return;
            }
            URLConnection connection = connect(mUrl);
            InputStream inputStream = connection.getInputStream();
            RandomAccessFile fos = new RandomAccessFile(mDest,"rwd");
            mOffset = inputStream.skip(mOffset);
            fos.seek(mFileOffset);
            mHandler.sendMessage(Message.obtainMessage(Message.MSG_TASK_PROGRESS,mFileOffset,mDest));
            log(Thread.currentThread().getName()+"start position:"+mOffset);
            byte[] cache = new byte[2048];
            int offset = 0;
            long remaindLenght =  mTaskLenght - mFileOffset;
            while ((offset = inputStream.read(cache)) != -1 || remaindLenght !=0){
                int writeLen = offset;
                if (remaindLenght < offset){
                    writeLen = (int)remaindLenght;
                }
                fos.write(cache,0,writeLen);
                remaindLenght -= writeLen;
                long len = writeLen;
                mHandler.sendMessage(Message.obtainMessage(Message.MSG_TASK_PROGRESS,len,mDest));
            }
            mHandler.sendMessage(Message.obtainMessage(Message.MSG_CHILD_TASK_SUCCESS));
            fos.close();
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
            mHandler.sendMessage(Message.obtainMessage(Message.MSG_TASK_ERROR,e.toString()));
        }
    }

    private static URLConnection connect(String url)throws IOException {
        URL localURL = new URL(url);
        URLConnection connection = localURL.openConnection();
        connection.setReadTimeout(15 * 1000);
        connection.setConnectTimeout(15 * 1000);
        connection.connect();
        return connection;
    }
}
