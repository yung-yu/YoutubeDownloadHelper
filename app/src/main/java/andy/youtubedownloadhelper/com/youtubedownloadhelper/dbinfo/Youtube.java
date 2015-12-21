package andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo;


import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import andy.spiderlibrary.db.Column;
import andy.spiderlibrary.db.Table;

/**
 * Created by andyli on 2015/7/25.
 */
@Table(tableName = "youtube")
public class Youtube  {

    @Column(name = "youtubeId" ,type = "text primary key" , index = 0)
    private String youtubeId;

    @Column(name = "youtubeUrl" ,type = "text", index = 1)
    private String youtubeUrl;

    @Column(name = "title" ,type = "text" , index = 2)
    private String title;

    @Column(name = "imgeUrl" ,type = "text" , index = 3)
    private String imgeUrl;

    @Column(name = "lastUpdateDate" ,type = "integer", index = 4)
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
