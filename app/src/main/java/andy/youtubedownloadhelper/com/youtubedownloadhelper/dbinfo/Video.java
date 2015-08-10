package andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo;


import annotation.db.Column;
import annotation.db.Table;

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
    @Column(name = "videoType" , type = "integer", index = 3)
    private int videoType;
    @Column(name = "localFilePath" , type = "text", index = 4)
    private String localFilePath;
    @Column(name = "lastUpdateDate",type = "integer", index = 5 )
    private long lastUpdateDate;

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

    public int getVideoType() {
        return videoType;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
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
