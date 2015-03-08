package andy.youtubedownloadhelper.com.youtubedownloadhelper;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by andy on 2015/3/8.
 */
public class YoutubeloadPaser extends AsyncTask<String, String, String> {

    public static final String VIDEO_INFO_URL = "http://www.youtube.com/get_video_info?video_id=";
    TextView tv;
    Activity context;
    public YoutubeloadPaser(Activity context,TextView tv){
        this.tv = tv;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        tv.setText(s);
    }

    @Override
    protected void onProgressUpdate(final String... values) {
        super.onProgressUpdate(values);
        Log.d("youtubeupdate",values[0]);
    }

    @Override
    protected String doInBackground(String... params) {
        String reference = params[0];
        String videoId = null;
        Log.d("youtube","reference : "+reference);
        if(reference.startsWith("https://www.youtube.com/watch?v=")){
               videoId = reference.substring("https://www.youtube.com/watch?v=".length());
        }else if(reference.startsWith("https://m.youtube.com/watch?v=")){
            videoId = reference.substring("https://m.youtube.com/watch?v=".length());
        }
        else if(reference.contains("http://youtu.be")){
            videoId = reference.substring(reference.lastIndexOf("/")+1);
        }
        if(videoId==null)
            return "videoId = null";
       String url = VIDEO_INFO_URL+videoId;
        Log.d("youtube","query url"+url);
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        onProgressUpdate(url);
        try {
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                Document doc = Jsoup.parse(response.getEntity().getContent(), "utf8", url);
                String[] Parms = doc.text().split("&");
                String url_encoded_fmt_stream_map = null;
                for(String parm : Parms){
                    if(parm.startsWith("url_encoded_fmt_stream_map=")){
                       // url_encoded_fmt_stream_map = parm.substring("url_encoded_fmt_stream_map=".length());
                        url_encoded_fmt_stream_map = parm;
                        break;
                    }
                }
                if(url_encoded_fmt_stream_map != null){

                     Parms  = Jsoup.parse(url_encoded_fmt_stream_map).text().split("&");
                    for(String parm : Parms){
                        onProgressUpdate(parm);
                    }
                }

            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return "update success";
    }
}
