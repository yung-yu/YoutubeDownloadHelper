package youtubedownloadhelper.dbinfo;


import android.database.Cursor;
import android.text.TextUtils;

import java.io.File;


/**
 * Created by andyli on 2015/7/25.
 */
public class Video  {

    private int id ;

    private String youtubeId ;

    private String videoUrl ;

    private int itag;

    private String localFilePath;

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

    public boolean isDownlaod(){
        if(!TextUtils.isEmpty(localFilePath)){
            File file = new File(localFilePath);
            return file.exists();
        }
        return false;
    }
}
