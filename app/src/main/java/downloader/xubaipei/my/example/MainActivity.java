package downloader.xubaipei.my.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

import my.xubaipei.downloader.AndroidDownloader;

public class MainActivity extends AppCompatActivity implements AndroidDownloader.CallBack {

    AndroidDownloader mDownloader;
    Button mBtn = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url= "http://itestmobileticket.fangte.com:8086/AppUpdate/AndroidFile/20181224/MobileAssistant_1.apk";
        File file = getExternalFilesDir(null);
        String path = file.getPath();
        mDownloader = new AndroidDownloader(url,path,3);
        mBtn= (Button)findViewById(R.id.btn);
    }


    public void download(View view){
        mDownloader.download(this);
        view.setEnabled(false);
    }

    @Override
    public void onProgress(int progress) {
        Log.i("xubaipei",String.valueOf(progress));
    }

    @Override
    public void onError(Throwable e) {
        Log.e("xubaipei",e.toString());
        mBtn.setEnabled(true);
    }

    @Override
    public void onFinish(String path) {
        Log.e("xubaipei",path);
        mBtn.setEnabled(true);
    }
}

