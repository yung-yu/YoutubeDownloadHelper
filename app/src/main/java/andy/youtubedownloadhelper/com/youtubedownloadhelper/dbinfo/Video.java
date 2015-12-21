package andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo;


import android.database.Cursor;

import andy.spiderlibrary.db.Column;
import andy.spiderlibrary.db.Table;


/**
 * Created by andyli on 2015/7/25.
 */
@Table(tableName = "video")
public class Video  {
    @Column(name = "Id" ,type = "integer primary key AUTOINCREMENT" , index = 0)
    private int id ;
    @Column(name = "youtubeId" ,type = "text", index = 1)
    private String youtubeId ;
    @Column(name = "videoUrl" ,type = "text", index = 2)
    private String videoUrl ;
    @Column(name = "itag" , type = "integer", index = 3)
    private int itag;
    @Column(name = "localFilePath" , type = "text", index = 4)
    private String localFilePath;
    @Column(name = "lastUpdateDate",type = "integer", index = 5 )
    private long lastUpdateDate;

    public Video(){

    }
    public Video(Cursor cursor){
        this.setId(cursor.getInt(0));
        this.setYoutubeId(cursor.getString(1));
        this.setVideoUrl(cursor.getString(2));
        this.setItag(cursor.getInt(3));
        this.setLocalFilePath(cursor.getString(4));
        this.setLastUpdateDate(cursor.getLong(5));
    }
    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItag() {
        return itag;
    }

    public void setItag(int videoType) {
        this.itag = videoType;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
