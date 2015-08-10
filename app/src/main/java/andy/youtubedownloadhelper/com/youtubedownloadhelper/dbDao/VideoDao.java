package andy.youtubedownloadhelper.com.youtubedownloadhelper.dbDao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.andylibrary.utils.Log;

import java.util.ArrayList;

import andy.youtubedownloadhelper.com.youtubedownloadhelper.db.DB;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.Video;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.Youtube;

/**
 * Created by andyli on 2015/8/10.
 */
public class VideoDao {

    Context context;
    static VideoDao dao;

    public VideoDao(Context context) {
        this.context = context;
    }

    public static VideoDao getInstance(Context c){
        if(dao==null){
            dao = new VideoDao(c);
        }
        return dao;
    }
    public boolean checkExistVideo( DB db ,Video video){
        Cursor cursor = db.getReadableDatabase().query(
                "video",null,"youtubeId = \""+video.getYoutubeId()+"\" and videoType = "+video.getVideoType(),null,null,null,null);
        boolean isExist = false;
        isExist = cursor.getCount()>0;
        db.closeCursor(cursor);
        return  isExist;
    }
    public boolean addVideo(Video  video){
        DB db = new DB(context);
        ContentValues values ;
        if(!checkExistVideo(db, video)){
            values =  new ContentValues();
            values.put("youtubeId", video.getYoutubeId());
            values.put("videoUrl", video.getVideoUrl());
            values.put("videoType", video.getVideoType());
            values.put("localFilePath", video.getLocalFilePath());
            values.put("lastUpdateDate", System.currentTimeMillis());
            try {
                db.getWriteDateBase().insertOrThrow("youtube", null, values);
            }catch (Exception e){
                Log.exception(e);
            }
            return true;
        }else{
            return false;
        }
    }
    public ArrayList<Video> getVideos (String youtubeId){
        DB db = new DB(context);
        ArrayList<Video> videoList = new ArrayList<Video>();
        Cursor cursor = db.getReadableDatabase().query(
                "video", null, null, null, null,null,"videoType DESC");
        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            Video video = new Video();
            video.setId(cursor.getInt(0));
            video.setYoutubeId(cursor.getString(1));
            video.setVideoUrl(cursor.getString(2));
            video.setVideoType(cursor.getInt(3));
            video.setLocalFilePath(cursor.getString(4));
            video.setLastUpdateDate(cursor.getLong(5));
            videoList.add(video);
        }
        db.closeCursor(cursor);
        return videoList;
    }
}
