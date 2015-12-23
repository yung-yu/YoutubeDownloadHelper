package youtubedownloadhelper.dbDao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


import java.util.ArrayList;

import andy.spiderlibrary.utils.Log;
import youtubedownloadhelper.db.DB;
import youtubedownloadhelper.dbinfo.Video;

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
                "video",null,"youtubeId = \""+video.getYoutubeId()+"\" and itag = "+video.getItag(),null,null,null,null);
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
            values.put("itag", video.getItag());
            values.put("localFilePath", video.getLocalFilePath());
            values.put("lastUpdateDate", System.currentTimeMillis());
            try {
                db.getWriteDateBase().insertOrThrow("video", null, values);
            }catch (Exception e){
                Log.exception(e);
            }
            return true;
        }else{
            return false;
        }
    }
    public boolean updateVideo(Video video){
        DB db = new DB(context);
        ContentValues values ;
        if(!checkExistVideo(db, video)){
            values =  new ContentValues();
            values.put("youtubeId", video.getYoutubeId());
            values.put("videoUrl", video.getVideoUrl());
            values.put("itag", video.getItag());
            values.put("localFilePath", video.getLocalFilePath());
            values.put("lastUpdateDate", System.currentTimeMillis());
            String where = "youtubeId = \""+video.getYoutubeId()+"\" and itag = \""+video.getItag()+"\"";
            try {
                db.getWriteDateBase().update("video", values, where, null);
            }catch (Exception e){
                Log.exception(e);
            }
            return true;
        }else{
            return false;
        }
    }
    public boolean addVideos(ArrayList<Video>  videos){
            DB db = new DB(context);
        ContentValues values ;
            if(videos!=null&&videos.size()>0){
                for(Video video:videos){
                    if(!checkExistVideo(db, video)) {
                        values = new ContentValues();
                        values.put("youtubeId", video.getYoutubeId());
                        values.put("videoUrl", video.getVideoUrl());
                        values.put("itag", video.getItag());
                        values.put("localFilePath", video.getLocalFilePath());
                        values.put("lastUpdateDate", System.currentTimeMillis());
                        try {
                            db.getWriteDateBase().insertOrThrow("video", null, values);
                        } catch (Exception e) {
                            Log.exception(e);
                            return false;
                        }
                    }

            }
            }
          return true;
    }
    public ArrayList<Video> getVideos (String youtubeId){
        DB db = new DB(context);
        ArrayList<Video> videoList = new ArrayList<Video>();
        Cursor cursor = db.getReadableDatabase().query(
                "video", null, "youtubeId = \"" + youtubeId + "\"", null, null,null,"itag DESC");
        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            videoList.add(new Video(cursor));
        }
        db.closeCursor(cursor);
        return videoList;
    }
    public void deleteVideos (String youtubeId){
        DB db = new DB(context);
        db.getWriteDateBase().delete("video", "youtubeId = \"" + youtubeId + "\"", null);
    }
}
