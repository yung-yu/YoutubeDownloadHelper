package youtubedownloadhelper.dbDao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import andy.spiderlibrary.utils.Log;
import youtubedownloadhelper.db.DB;
import youtubedownloadhelper.dbinfo.SongItem;
import youtubedownloadhelper.dbinfo.Video;
import youtubedownloadhelper.dbinfo.Youtube;

/**
 * Created by andyli on 2015/8/22.
 */
public class SongItemDao {

    Context context;
    static SongItemDao dao;

    public SongItemDao(Context context) {
        this.context = context;
    }

    public static SongItemDao getInstance(Context c){
        if(dao==null){
            dao = new SongItemDao(c);
        }
        return dao;
    }

    public ArrayList<SongItem> getSongList(){
            DB db = new DB(context);
        ArrayList<SongItem> songlist = new ArrayList<>();
        Cursor cursor = db.getReadableDatabase().query("song" ,null,null,null,null,null,"lastUpdateDate DESC" );
        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            songlist.add(new SongItem(cursor));
        }
        db.closeCursor(cursor);
       return songlist;
    }
    public boolean checkExistSongItem( DB db ,String youtubeId){
        Cursor cursor = db.getReadableDatabase().query(
                "song",null,"youtubeId = \""+youtubeId+"\"",null,null,null,null);
        boolean isExist = false;
        isExist = cursor.getCount()>0;
        db.closeCursor(cursor);
        return  isExist;
    }
    public void addSongItems(ArrayList<Youtube> youtubes){
        DB db = new DB(context);
        ContentValues values ;
        if(youtubes!=null&&youtubes.size()>0) {
            for(Youtube youtube:youtubes) {
                if (youtube != null) {
                    SongItem item = getSongItem(youtube);
                    if (item != null && !checkExistSongItem(db, item.getYoutubeId())) {
                        values = item.getContentValues();
                        try {
                            db.getWriteDateBase().insertOrThrow("song", null, values);
                        } catch (Exception e) {
                            Log.exception(e);
                        }
                    }
                }
            }
        }
    }
    public void addSongItem(Youtube youtube){
        DB db = new DB(context);
        ContentValues values ;
        if(youtube != null){
            SongItem item = getSongItem(youtube);
            if(item!=null&&!checkExistSongItem(db,item.getYoutubeId())) {
                values = item.getContentValues();
                try {
                    db.getWriteDateBase().insertOrThrow("song", null, values);
                } catch (Exception e) {
                    Log.exception(e);
                }
            }
        }
    }
    public void updateSongItem( ContentValues values,String youtubeId){
        DB db = new DB(context);

        if(values!=null&&checkExistSongItem(db,youtubeId)) {
            try {
                db.getWriteDateBase().update("song", values, "youtubeId = \""+youtubeId+"\"",null);
            } catch (Exception e) {
                Log.exception(e);
            }
        }

    }
    public SongItem getSongItem(Youtube youtube){
        SongItem songItem = new SongItem();
        songItem.setName(youtube.getTitle());
        songItem.setYoutubeId(youtube.getYoutubeId());
        songItem.setLastUpdateDate(System.currentTimeMillis());
        ArrayList<Video> list = youtube.getVideoList();
        if(list!=null&&list.size()>0){
              Video video = null;
              for(int i=0;i<list.size();i++){
                  if(video==null){
                      video = list.get(i);
                  }else if(video.getItag()>list.get(i).getItag()){
                      video = list.get(i);
                  }
              }
            if(video!=null){
                songItem.setIsLocal(false);
                songItem.setUrl(video.getVideoUrl());
                return songItem;
            }
        }
        return null;
    }
    public void clearAll(){
        DB db = new DB(context);
        db.getWriteDateBase().delete("song", null, null);
    }
    public void clear(String youtubeId){
        DB db = new DB(context);
        db.getWriteDateBase().delete("song","youtubeId = \"" + youtubeId + "\"",null);
    }
}
