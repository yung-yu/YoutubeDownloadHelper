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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public final class DownloadTask extends AsyncTask<String, String, String> {

    private ProgressDialog dialog;

    private Context context = null;

    public DownloadTask(Context c ){
        this.context = c;

        dialog = new ProgressDialog(context);
    }
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage(context.getString(R.string.download_wait));
        dialog.setIndeterminate(false);
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();
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

                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

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

    protected void onProgressUpdate(String... progress) {
        dialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        dialog.cancel();
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }
}
