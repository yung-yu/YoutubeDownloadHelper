package andy.youtubedownloadhelper.com.youtubedownloadhelper;

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
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

public  class DownloadTask extends AsyncTask<String, String, String> {

    private DownLoadActivity.ViewHolder vh;

    private Activity context = null;

    public DownloadTask(Activity c , DownLoadActivity.ViewHolder vh){
        this.context = c;
        this.vh = vh;

    }
    protected void onPreExecute() {
        super.onPreExecute();
        vh.bt.setSelected(true);
        vh.progressBar.setVisibility(View.VISIBLE);

    }


    @Override
    protected String doInBackground(String... params) {
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
            if(!file.exists())
                file.createNewFile();
            filePath = file.getAbsolutePath();
            OutputStream output = new FileOutputStream(file.getAbsolutePath());
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;

                onProgressUpdate("" + (int) ((total * 100) / lenghtOfFile));

                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        }catch (IOException e) {

            return context.getString(R.string.download_fail)+"\n"+e.toString();

        }

        return context.getString(R.string.download_success);
    }
    @Override
    protected void onProgressUpdate(final String... progress) {
        super.onProgressUpdate(progress);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int progressIndex = Integer.valueOf(progress[0]);
                vh.progressBar.setProgress(progressIndex);
                vh.bt.setText(context.getString(R.string.progress)+" - "+progressIndex+"/100");
            }
        });

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        vh.bt.setSelected(false);
        vh.bt.setText(s+"--"+vh.video.getType());
        vh.progressBar.setVisibility(View.GONE);
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }
}
