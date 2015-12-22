package andy.youtubedownloadhelper.com.youtubedownloadhelper.dbDao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import andy.spiderlibrary.utils.Log;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.db.DB;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.Youtube;

/**
 * Created by andyli on 2015/7/25.
 */
public class YoutubeDao {

    Context context;
    static YoutubeDao dao;
    public YoutubeDao(Context context) {
         this.context = context;
    }

    public static YoutubeDao getInstance(Context c){
           if(dao==null){
               dao = new YoutubeDao(c);
           }
           return dao;
    }
    public boolean checkExistYoubId( Youtube youtube){
        DB db = new DB(context);
        Cursor cursor = db.getReadableDatabase().query(
                "youtube",null,"youtubeId = \""+youtube.getYoutubeId()+"\"",null,null,null,null);
        boolean isExist = false;
        isExist = cursor.getCount()>0;
        db.closeCursor(cursor);
        return  isExist;
    }
   public boolean checkExistYoubId( DB db ,Youtube youtube){
       Cursor cursor = db.getReadableDatabase().query(
               "youtube",null,"youtubeId = \""+youtube.getYoutubeId()+"\"",null,null,null,null);
       boolean isExist = false;
       isExist = cursor.getCount()>0;
       db.closeCursor(cursor);
       return  isExist;
   }
    public boolean addYoutube(Youtube youtube){
          DB db = new DB(context);
            ContentValues values ;
          if(!checkExistYoubId(db,youtube)){
              values =  new ContentValues();
              values.put("youtubeId", youtube.getYoutubeId());
              values.put("youtubeUrl", youtube.getYoutubeUrl());
              values.put("title", youtube.getTitle());
              values.put("imgeUrl", youtube.getImgeUrl());
              values.put("lastUpdateDate", System.currentTimeMillis());
              VideoDao.getInstance(context).addVideos(youtube.getVideoList());
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

    public ArrayList<Youtube> getYoutubes(){
        DB db = new DB(context);
        ArrayList<Youtube> youtubeList = new ArrayList<Youtube>();
        Cursor cursor = db.getReadableDatabase().query(
                "youtube", null, null, null, null,null,"lastUpdateDate DESC");
        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            Youtube youtube = new Youtube(cursor);
            youtube.setVideoList( VideoDao.getInstance(context).getVideos(youtube.getYoutubeId()));
            youtubeList.add(youtube);
        }
        db.closeCursor(cursor);
        return youtubeList;
    }

    public void deleteYotube (String youtubeId){
        DB db = new DB(context);
        db.getWriteDateBase().delete("youtube","youtubeId = \""+youtubeId+"\"",null);
        VideoDao.getInstance(context).deleteVideos(youtubeId);
        SongItemDao.getInstance(context).clear(youtubeId);
    }
    public Youtube getYoutube(String youtubeId){
        DB db = new DB(context);
        Youtube youtube = null;
        String where = "youtubeId = \""+youtubeId+"\"";
        Cursor cursor = db.getReadableDatabase().query(
                "youtube", null, where, null, null,null,"lastUpdateDate DESC");
        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            if(cursor.getString(0).equals(youtubeId)) {
                youtube = new Youtube();
                youtube.setYoutubeId(cursor.getString(0));
                youtube.setYoutubeUrl(cursor.getString(1));
                youtube.setTitle(cursor.getString(2));
                youtube.setImgeUrl(cursor.getString(3));
                youtube.setLastUpdateDate(cursor.getInt(4));
                youtube.setVideoList(VideoDao.getInstance(context).getVideos(cursor.getString(0)));
                break;
            }
        }
        db.closeCursor(cursor);
        return youtube;
    }
}
