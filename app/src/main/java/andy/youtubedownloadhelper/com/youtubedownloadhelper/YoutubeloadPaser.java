package andy.youtubedownloadhelper.com.youtubedownloadhelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2015/3/8.
 */
public class YoutubeloadPaser extends AsyncTask<String, String, Youtube> {

    public static final String VIDEO_INFO_URL = "http://www.youtube.com/get_video_info?video_id=";
    public static final ArrayList<String> BAD_KEYS = new ArrayList<String>();
    static{
        BAD_KEYS.add("stereo3d");
        BAD_KEYS.add("type");
        BAD_KEYS.add("fallback_host");
        BAD_KEYS.add("quality");

    }
    public interface CallBack{
        void success(Youtube youtube);
        void onfail(String Message);
    }


    Activity context;
    ProgressDialog progressBar;
    CallBack callBack;
    public YoutubeloadPaser(Activity context, CallBack callBack){

        this.context = context;
        this.callBack = callBack;
        progressBar = new ProgressDialog(context);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setMessage("search waiting...");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(!progressBar.isShowing())
          progressBar.show();
    }

    @Override
    protected void onPostExecute(Youtube youtube) {
        super.onPostExecute(youtube);
        if(callBack!=null){
            if(youtube!=null){
                callBack.success(youtube);
            }else{
                callBack.onfail("無效 URL");
            }

        }
        progressBar.cancel();
    }

    @Override
    protected void onProgressUpdate(final String... values) {
        super.onProgressUpdate(values);
        Log.d("youtubeupdate",values[0]);
    }

    @Override
    protected Youtube doInBackground(String... params) {
        String reference = params[0];
        String videoId = getVideoId(reference);
        if(videoId==null)
            return null;
       String url = VIDEO_INFO_URL+videoId;
        Youtube youtube = new Youtube();
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);

        onProgressUpdate(url);
        try {
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                Document doc = Jsoup.parse(response.getEntity().getContent(), "utf8", url);
                String[] Parms = doc.text().split("&");
                String url_encoded_fmt_stream_map = null;
                List<Video> urls = new ArrayList<Video>();
                String title = null;
                String thumbnail_url =null;
                for(String p : Parms){
                    String key = p.substring(0, p.indexOf('='));
                    String value = p.substring(p.indexOf('=') + 1);
                    if (key.equals("url_encoded_fmt_stream_map")) {
                        value = decode(value);
                        for (String u : value.split("url=")) {
                            Video video = new Video();
                            String decodeU = decode(u);
                            u = getCorrectURL(video,decodeU);

                            if (!u.startsWith("http") && !isValid(u)) {
                                continue;
                            }
                            onProgressUpdate(u);
                            video.setUrl(u);
                            Integer itag = getItag(u);
                            video.setType(getType(itag));
                            video.setVideoType(getVideoType(itag));
                            urls.add(video);
                        }
                    } else if (key.equals("title")) {
                        title = value.replace("+", "%20");
                        title = decode(title);
                        onProgressUpdate(title);
                    } else if(key.equals("thumbnail_url")){
                        thumbnail_url = decode(value);
                        onProgressUpdate(thumbnail_url);
                    }


                }
                youtube.setTitle(title);
                youtube.setThumbnail_url(thumbnail_url);
                youtube.setVideoList(urls);
                return youtube;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public String getVideoId(String reference){
        String videoId = null;
        Log.d("youtube","reference : "+reference);
        if(reference.startsWith("https://www.youtube.com/watch?v=")){
            videoId = reference.substring("https://www.youtube.com/watch?v=".length());
        }else if(reference.startsWith("https://m.youtube.com/watch?v=")){
            videoId = reference.substring("https://m.youtube.com/watch?v=".length());
        }
        else if(reference.contains("http://youtu.be")){
            videoId = reference.substring(reference.lastIndexOf("/")+1);
        }else if(reference.contains("https://youtu.be")){
            videoId = reference.substring(reference.lastIndexOf("/")+1);
        }
        return videoId;
    }
    public boolean isValid(String url) {
        return url.contains("signature=") && url.contains("factor=");
    }
    public  String decode(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }
    private  String getCorrectURL(Video video,String input) {
        StringBuilder builder = new StringBuilder(input.substring(0, input.indexOf('?') + 1));
        String[] params = input.substring(input.indexOf('?') + 1).split("&");
        List<String> keys = new ArrayList<String>();
        boolean first = true;
        for (String param : params) {
            String key = param;
            try {
                key = param.substring(0, param.indexOf('='));
            } catch (Exception ex) {
                System.out.println(param);
            }
            if (keys.contains(key) || BAD_KEYS.contains(key)) {
                continue;
            }
            keys.add(key);
            if (key.equals("sig")) {
                builder.append(first ? "" : "&").append("signature=").append(param.substring(4));
            } else {
                if (param.contains(",quality=")) {
                    param = remove(param, ",quality=", "_end_");
                }
                if (param.contains(",type=")) {
                    param = remove(param, ",type=", "_end_");
                }
                if (param.contains(",fallback_host")) {
                    param = remove(param, ",fallback_host", ".com");
                }
                builder.append(first ? "" : "&").append(param);
            }
            if (first)
                first = false;
        }
        return builder.toString();
    }

    private  String remove(String text, String start, String end) {
        int l = text.indexOf(start);
        return text.replace(text.substring(l, end.equals("_end_") ? text.length() : text.indexOf(end, l)), "");
    }
    private int getItag(String url){
        String[] parms = url.split("&");
        for(String p:parms){
            String key = p.substring(0, p.indexOf('='));
            String value = p.substring(p.indexOf('=') + 1);
            if(key.equals("itag")){
                return Integer.valueOf(value);
            }
        }
        return -1;
    }
   private String getType(Integer itag){
       String result = "";
       switch (itag){
           case 5:
               result = "Low Quality, 240p, FLV, 400x240";
           break;
           case 17:
               result = "Low Quality, 144p, 3GP, 0x0";
           break;
           case 18:
               result = "Medium Quality, 360p, MP4, 480x360";
           break;
           case 22:
               result = "High Quality, 720p, MP4, 1280x720";
           break;
           case 34:
               result = "Medium Quality, 360p, FLV, 640x360";
           break;
           case 35:
               result = "Standard Definition, 480p, FLV, 854x480";
           break;
           case 36:
               result = "Low Quality, 240p, 3GP, 0x0";
           break;
           case 37:
               result = "Full High Quality, 1080p, MP4, 1920x1080";
           break;
           case 38:
               result = "Original Definition, MP4, 4096x3072";
           break;
           case 43:
               result = "Medium Quality, 360p, WebM, 640x360";
           break;
           case 44:
               result = "Standard Definition, 480p, WebM, 854x480";
           break;
           case 45:
               result = "High Quality, 720p, WebM, 1280x720";
           break;
           case 46:
               result = "Full High Quality, 1080p, WebM, 1280x720";
           break;
           case 82:
               result = "Medium Quality 3D, 360p, MP4, 640x360";
           break;
           case 84:
               result = "High Quality 3D, 720p, MP4, 1280x720";
           break;
           case 100:
               result = "Medium Quality 3D, 360p, WebM, 640x360";
           break;
           case 102:
               result = "High Quality 3D, 720p, WebM, 1280x720";
           break;
       }
    return result;

   }
    private String getVideoType(Integer itag){
        String result = "";
        switch (itag){
            case 5:
                result = "fLV";
                break;
            case 17:
                result = "3gp";
                break;
            case 18:
                result = "mp4";
                break;
            case 22:
                result = "mp4";
                break;
            case 34:
                result = "flv";
                break;
            case 35:
                result = "flv";
                break;
            case 36:
                result = "3gp";
                break;
            case 37:
                result = "mp4";
                break;
            case 38:
                result = "mp4";
                break;
            case 43:
                result = "webm";
                break;
            case 44:
                result = "webm";
                break;
            case 45:
                result = "webm";
                break;
            case 46:
                result = "webm";
                break;
            case 82:
                result = "mp4";
                break;
            case 84:
                result = "mp4";
                break;
            case 100:
                result = "webm";
                break;
            case 102:
                result = "webm";
                break;
        }
        return result;

    }
}
