package youtubedownloadhelper.youtube;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import youtubedownloadhelper.dbinfo.Video;
import youtubedownloadhelper.dbinfo.Youtube;

/**
 * Created by andy on 2015/3/8.
 */
public class YoutubeloadPaser extends AsyncTask<String, String, Youtube> {

    public static final String VIDEO_INFO_URL = "http://www.youtube.com/get_video_info?video_id=";
    public static final String VIDEO_INFO_PARM = "&asv=2&el=detailpage&hl=en_US";
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


    Context context;
    ProgressDialog progressBar;
    CallBack callBack;
    public YoutubeloadPaser(Context context, CallBack callBack){

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
        //Log.d(values[0]);
    }

    @Override
    protected Youtube doInBackground(String... params) {
        String reference = params[0];

        String youtubeId = getVideoId(reference);
        if(TextUtils.isEmpty(youtubeId))
            return null;
        return getYoutube(reference,youtubeId);

    }

    public  Youtube getYoutube(String reference,String youtubeId){
        String url = VIDEO_INFO_URL+youtubeId+VIDEO_INFO_PARM;
        Youtube youtube = new Youtube();
        youtube.setYoutubeUrl(reference);
        youtube.setYoutubeId(youtubeId);

        try {
            URL  mUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            String response = "";
            if (connection.getResponseCode() == 200) {

                Document doc = Jsoup.parse(connection.getInputStream(), "utf8", url);
                String[] Parms = doc.text().split("&");
                String url_encoded_fmt_stream_map = null;
                ArrayList<Video> urls = new ArrayList<Video>();
                String title = null;
                String thumbnail_url =null;
                for(String p : Parms){
                    String key = p.substring(0, p.indexOf('='));
                    String value = p.substring(p.indexOf('=') + 1);
                    if (key.equals("url_encoded_fmt_stream_map")) {
                        value = decode(value);
                        for (String videoUrl : value.split("url=")) {
                            Video video = new Video();
                            String decodeU = decode(videoUrl);
                            videoUrl = getCorrectURL(decodeU);

                            if (!videoUrl.toLowerCase().startsWith("http") && !isValid(videoUrl)) {
                                continue;
                            }
                            video.setYoutubeId(youtubeId);
                            video.setVideoUrl(videoUrl);
                            Integer itag = getItag(videoUrl);
                            String type = YotubeItag.getVideoType(itag);
                            String videoType = YotubeItag.getVideoDescribe(itag);
                            video.setItag(itag);
                            if(type!=null&&videoType!=null&&!type.equals("WEB")&&!type.equals("FLV")
                                    &&!(videoUrl.isEmpty()||type.isEmpty()||videoType.isEmpty()||itag.equals("-1"))) {
                                urls.add(video);
                            }

                        }
                    } else if (key.equals("title")) {
                        title = value.replace("+", "%20");
                        title = decode(title);
                    }
                }
                youtube.setTitle(title);
                thumbnail_url = "http://img.youtube.com/vi/"+youtubeId+"/0.jpg";
                youtube.setImgeUrl(thumbnail_url);
                if(urls.size()==0){
                    return null;
                }
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

        if(reference.startsWith("https://www.youtube.com/watch?v=")){
            videoId = reference.substring("https://www.youtube.com/watch?v=".length());
        }else if(reference.startsWith("https://m.youtube.com/watch?v=")){
            videoId = reference.substring("https://m.youtube.com/watch?v=".length());
        }
        else if(reference.contains("http://youtu.be/")){
            videoId = reference.substring(reference.lastIndexOf("/")+1);
        }else if(reference.contains("https://youtu.be/")){
            videoId = reference.substring(reference.lastIndexOf("/")+1);
        }
        if(!TextUtils.isEmpty(videoId)&&videoId.contains("&")){
            videoId = videoId.substring(0,videoId.indexOf("&"));
        }

        return videoId;
    }
    public boolean isValid(String url) {
        return url.contains("signature=") && url.contains("factor=");
    }
    public  String decode(String s) {
        try {
            return URLDecoder.decode(s, "utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }
    private  String getCorrectURL(String input) {
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
                try {
                    return Integer.valueOf(value);
                }catch (NumberFormatException e){

                    break;
                }
            }
        }
        return -1;
    }


}
