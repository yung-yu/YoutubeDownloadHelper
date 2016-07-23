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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import youtubedownloadhelper.R;

import youtubedownloadhelper.youtube.YotubeItag;

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    private final static String TAG = "DownloadTask";
    private DownLoadActivity.MyViewHolder vh;

    private Activity context = null;
    public final Integer DOWNLOAD_SUCCESS = 0;
    public final Integer DOWNLOAD_FAIL = 1;
    public DownLoadActivity.DownloadAdapter adapter;
    public int curProgress = 0;

    public DownloadTask(Activity c , DownLoadActivity.DownloadAdapter adapter  ){
        this.context = c;
        this.adapter = adapter;

    }
    protected void onPreExecute() {
        super.onPreExecute();
        adapter.notifyDataSetChanged();
    }


    @Override
    protected Integer doInBackground(String... params) {
        int count;
        String filePath = "";
        try {
            URL url = new URL(params[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            int lenghtOfFile = conection.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream(),8192);

            File file = new File(params[1],params[2]);
            if(file.exists()) {
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
            return DOWNLOAD_FAIL;
        }
        this.vh.video.setLocalFilePath(filePath);

        return DOWNLOAD_SUCCESS;
    }


    @Override
    protected void onProgressUpdate(final Integer... progress) {
        super.onProgressUpdate(progress);
        final int progressIndex = progress[0];
        if(curProgress !=progressIndex) {
            curProgress = progressIndex;
            Log.d(TAG, "download progress :"+curProgress);
            if(adapter != null){
                adapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    protected void onPostExecute(Integer s) {
        super.onPostExecute(s);
        if(s.equals(DOWNLOAD_SUCCESS)) {
            Toast.makeText(context, context.getString(R.string.download_success), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, context.getString(R.string.download_fail), Toast.LENGTH_SHORT).show();
        }
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }
}
