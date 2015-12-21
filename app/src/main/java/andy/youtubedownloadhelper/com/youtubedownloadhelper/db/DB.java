package andy.youtubedownloadhelper.com.youtubedownloadhelper.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Comparator;

import andy.spiderlibrary.db.Column;
import andy.spiderlibrary.db.Table;
import andy.spiderlibrary.utils.Log;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.SongItem;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.Video;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.Youtube;


/**
 * Created by andyli on 2015/8/1.
 */
public class DB {
    public static final String DATABASE_NAME = "youtubeDB";
    public static final int DATAVERSION = 1;

    DBHelper dbHelper;
    public static class DBHelper extends SQLiteOpenHelper{

        public DBHelper(Context context ) {
            super(context, DATABASE_NAME, null, DATAVERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            dropTable(Youtube.class);
            dropTable(Video.class);
            onCreate(db);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d( DATABASE_NAME+" onCreate start");
            try {
                db.execSQL(createTable(Youtube.class));
                db.execSQL(createTable(Video.class));
                db.execSQL(createTable(SongItem.class));
            }catch (Exception e){
                Log.exception(e);
            }
            Log.d(DATABASE_NAME + " onCreate end");
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            super.onDowngrade(db, oldVersion, newVersion);

        }


    }

    Cursor cursor;
    public DB(Context context){
        if(dbHelper==null){
            dbHelper = new DBHelper(context);
        }
    }
    public static String dropTable(Class<?> cls){
        try {
            String sql = "";
           String DROP_TABLE = "DROP TABLE IF EXISTS ";
            String tableName = "";
            Table table = cls.getAnnotation(Table.class);
            tableName = table.tableName();

            sql += DROP_TABLE +tableName;
            Log.d(sql);
            return sql;
        }catch (Exception e){
            Log.exception(e);
        }

        return null;
    }

    public static String createTable(Class<?> cls){
        try {
            String sql = "";
            String fieldSql = "";
            String tableName = "";
            Field[] fields = cls.getDeclaredFields();
            Table table = cls.getAnnotation(Table.class);
            tableName = table.tableName();
            Arrays.sort(fields, new Comparator<Field>() {
                @Override
                public int compare(Field f1, Field f2) {
                    if(f1.isAnnotationPresent(Column.class)
                            &&f2.isAnnotationPresent(Column.class)){
                        Column co1 = f1.getAnnotation(Column.class);
                        Column co2 = f2.getAnnotation(Column.class);
                        return co1.index()-co2.index();
                    }else{
                        return 0;
                    }

                }
            });
            for(int i = 0;i<fields.length;i++){
                Field a = fields[i];
                if(a.isAnnotationPresent(Column.class)){
                    Column col = a.getAnnotation(Column.class);
                    fieldSql+= (i==0?"":" ,")+col.name()+" "+col.type();
                }
            }
            sql += " create table "+tableName+" ( "+fieldSql+" )";
            Log.d(sql);
            return sql;
        }catch (Exception e){
            Log.exception(e);
        }

        return null;
    }
    public static void checkDBVersion(){

    }

    public SQLiteDatabase getWriteDateBase(){
            return dbHelper.getWritableDatabase();
    }
    public SQLiteDatabase getReadableDatabase(){
        return dbHelper.getReadableDatabase();
    }

    public void closeCursor(Cursor cursor){
        try {
            if (cursor != null) {
                cursor.close();
            }
        }catch (Exception e){
            Log.exception(e);
        }
    }
}
