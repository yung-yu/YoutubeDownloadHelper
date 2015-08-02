package andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo;


import annotation.db.Column;
import annotation.db.Table;

/**
 * Created by andyli on 2015/7/25.
 */
@Table(tableName = "youtube")
public class Youtube  {

    @Column(name = "youtubeId" ,type = "text primary key")
    private String youtubeId;

    @Column(name = "youtubeUrl" ,type = "text")
    private String youtubeUrl;

    @Column(name = "title" ,type = "text")
    private String title;

    @Column(name = "imgeUrl" ,type = "text")
    private String imgeUrl;

    @Column(name = "lastUpdateDate" ,type = "integer")
    private Integer lastUpdateDate;

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


}
