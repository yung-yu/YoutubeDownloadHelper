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
import android.view.View;
import android.widget.Toast;


import andy.spiderlibrary.utils.Log;
import youtubedownloadhelper.R;
import youtubedownloadhelper.dbDao.VideoDao;
import youtubedownloadhelper.youtube.YotubeItag;

public  class DownloadTask extends AsyncTask<String, Integer, Integer> {

    private DownLoadActivity.ViewHolder vh;

    private Activity context = null;
    public final Integer DOWNLOAD_SUCCESS = 0;
    public final Integer DOWNLOAD_FAIL = 1;
    public DownloadTask(Activity c , DownLoadActivity.ViewHolder vh){
        this.context = c;
        this.vh = vh;

    }
    protected void onPreExecute() {
        super.onPreExecute();
        vh.bt.setSelected(true);
        vh.bt.setEnabled(false);
        vh.progressBar.setVisibility(View.VISIBLE);

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
            vh.progressBar.setMax(100);
            vh.progressBar.setProgress(0);
            InputStream input = new BufferedInputStream(url.openStream(),8192);

            File file = new File(params[1],params[2]);
            if(file.exists()) {
                file.delete();
            }
            file.createNewFile();
            filePath = file.getAbsolutePath();
            Log.d("下載儲存位置 :" + filePath);
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
            Log.exception(e);
            return DOWNLOAD_FAIL;

        }
        this.vh.video.setLocalFilePath(filePath);
        VideoDao.getInstance(context).updateVideo(this.vh.video);
        return DOWNLOAD_SUCCESS;
    }
    int curP = -1;

    @Override
    protected void onProgressUpdate(final Integer... progress) {
        super.onProgressUpdate(progress);
        final int progressIndex = progress[0];
        if(curP!=progressIndex) {
            curP = progressIndex;
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    vh.progressBar.setProgress(progressIndex);
                    vh.bt.setText(context.getString(R.string.progress) + " - " + progressIndex + " %");
                }
            });

        }
    }

    @Override
    protected void onPostExecute(Integer s) {
        super.onPostExecute(s);
        vh.bt.setSelected(false);
        vh.bt.setText(YotubeItag.getVideoDescribe(vh.video.getItag()));
        vh.bt.setEnabled(true);
        vh.progressBar.setVisibility(View.GONE);
        if(s.equals(DOWNLOAD_SUCCESS)) {
            Toast.makeText(context, context.getString(R.string.download_success), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, context.getString(R.string.download_fail), Toast.LENGTH_SHORT).show();
        }
    }
}
