package youtubedownloadhelper.download;

/**
 * Created by andy on 2015/2/28.
 */
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import youtubedownloadhelper.R;

import youtubedownloadhelper.dbinfo.Video;
import youtubedownloadhelper.youtube.YotubeItag;

public class DownloadTask extends AsyncTask<Object, Integer, Integer> {
    private final static String TAG = "DownloadTask";
    private Activity context = null;
    public final Integer DOWNLOAD_SUCCESS = 0;
    public final Integer DOWNLOAD_FAIL = 1;
    private Handler handler;
    public int curProgress = 0;
    private int position;

    public DownloadTask(Activity c , Handler handler,int position){
        this.context = c;
        this.handler = handler;
        this.position = position;

    }
    protected void onPreExecute() {
        super.onPreExecute();
        if(handler != null){
            handler.sendEmptyMessage(DownLoadActivity.NOTFTY_ADAPTER);
        }

    }


    @Override
    protected Integer doInBackground(Object... params) {
        int count;
        String filePath = "";
        Video video = (Video) params[0];
        try {
            URL url = new URL(video.getVideoUrl());
            URLConnection conection = url.openConnection();
            conection.connect();
            int lenghtOfFile = conection.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream(),8192);

            File file = new File((String)params[1],(String)params[2]);
            if(file.exists()&& file.length() == lenghtOfFile) {
                video.setLocalFilePath(filePath);
                return DOWNLOAD_SUCCESS;
            }else if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            filePath = file.getAbsolutePath();

            OutputStream output = new FileOutputStream(file.getAbsolutePath());
            byte data[] = new byte[1024];
            long total = 0;
            onProgressUpdate(0);
            while ((count = input.read(data)) != -1) {
                total += count;

                onProgressUpdate((int) ((total * 100) / lenghtOfFile));

                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        }catch (IOException e) {
            File file = new File(filePath);
            if(file.exists()){
                file.delete();
            }
            return DOWNLOAD_FAIL;
        }
        video.setLocalFilePath(filePath);
        return DOWNLOAD_SUCCESS;
    }


    @Override
    protected void onProgressUpdate(final Integer... progress) {
        super.onProgressUpdate(progress);
        final int progressIndex = progress[0];
        if(curProgress !=progressIndex) {
            curProgress = progressIndex;
            Log.d(TAG, "download progress :"+curProgress);
            if(handler != null) {
                handler.sendEmptyMessage(DownLoadActivity.NOTFTY_ADAPTER);
            }
        }

    }

    @Override
    protected void onPostExecute(Integer s) {
        super.onPostExecute(s);
        if(handler != null) {
            handler.obtainMessage(DownLoadActivity.DOWNLOAD_FINISH, position ,0).sendToTarget();
        }
        if(s.equals(DOWNLOAD_SUCCESS)) {

            Toast.makeText(context, context.getString(R.string.download_success), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, context.getString(R.string.download_fail), Toast.LENGTH_SHORT).show();
        }

    }
}
