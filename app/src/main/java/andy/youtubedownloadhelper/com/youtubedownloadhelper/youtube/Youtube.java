package andy.youtubedownloadhelper.com.youtubedownloadhelper.youtube;

import java.util.List;

/**
 * Created by andy on 2015/3/9.
 */
public class Youtube {
    String title;
    String thumbnail_url;
    List<Video> videoList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public List<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
    }
}
