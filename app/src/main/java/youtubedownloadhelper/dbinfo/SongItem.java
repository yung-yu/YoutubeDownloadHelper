package youtubedownloadhelper.dbinfo;

import android.content.ContentValues;
import android.database.Cursor;

import andy.spiderlibrary.db.Column;
import andy.spiderlibrary.db.Table;


/**
 * Created by andyli on 2015/8/22.
 */
@Table(tableName = "song")
public class SongItem {
    @Column(name = "youtubeId" ,type = "text" , index = 0)
    String youtubeId ;
    @Column(name = "name" ,type = "text" , index = 1)
    String name ;
    @Column(name = "islocal" ,type = "integer not null" , index = 2)
    boolean isLocal ;
    @Column(name = "url" ,type = "text" , index = 3)
    String url;
    @Column(name = "file" ,type = "text" , index = 4)
    String file;
    @Column(name = "lastUpdateDate",type = "integer", index = 5 )
    long lastUpdateDate;

    public SongItem(){

    }
    public SongItem(Cursor cursor){
         setYoutubeId(cursor.getString(0));
        setName(cursor.getString(1));
        setIsLocal(cursor.getInt(2) == 1 ? true : false);
        setUrl(cursor.getString(3));
        setFile(cursor.getString(4));
        setLastUpdateDate(cursor.getInt(5));
    }
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put("youtubeId",getYoutubeId());
        values.put("name",getName());
        values.put("islocal",isLocal()?1:0);
        values.put("url",getUrl());
        values.put("file",getFile());
        values.put("lastUpdateDate",getLastUpdateDate());
        return values;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
