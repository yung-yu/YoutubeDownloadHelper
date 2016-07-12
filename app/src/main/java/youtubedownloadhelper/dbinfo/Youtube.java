package youtubedownloadhelper.dbinfo;


import android.database.Cursor;

import java.util.ArrayList;


/**
 * Created by andyli on 2015/7/25.
 */

public class Youtube  {


    private String youtubeId;


    private String youtubeUrl;


    private String title;


    private String imgeUrl;


    private Integer lastUpdateDate;

    private ArrayList<Video> videoList;

   public Youtube(){

   }
    public Youtube(Cursor cursor){
        this.setYoutubeId(cursor.getString(0));
        this.setYoutubeUrl(cursor.getString(1));
        this.setTitle(cursor.getString(2));
        this.setImgeUrl(cursor.getString(3));
        this.setLastUpdateDate(cursor.getInt(4));
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgeUrl() {
        return imgeUrl;
    }

    public void setImgeUrl(String imgeUrl) {
        this.imgeUrl = imgeUrl;
    }

    public Integer getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Integer lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public ArrayList<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(ArrayList<Video> videoList) {
        this.videoList = videoList;
    }
}
