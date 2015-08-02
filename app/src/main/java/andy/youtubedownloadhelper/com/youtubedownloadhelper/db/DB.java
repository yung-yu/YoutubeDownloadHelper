package andy.youtubedownloadhelper.com.youtubedownloadhelper.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andylibrary.utils.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.Video;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.Youtube;
import annotation.db.Column;
import annotation.db.Table;

/**
 * Created by andyli on 2015/8/1.
 */
public class DB {
    public static final String DATABASE_NAME = "youtubeDB";
    public static final int DATAVERSION = 1;

    static  SqliteHelper DBHelper;
    public static class SqliteHelper extends SQLiteOpenHelper{

        public SqliteHelper(Context context ) {
            super(context, DATABASE_NAME, null, DATAVERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(createTable(Youtube.class));
            sqLiteDatabase.execSQL(createTable(Video.class));
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            super.onDowngrade(db, oldVersion, newVersion);
        }


    }

    public static SqliteHelper getDBHelper(Context c){
        if(DBHelper==null){
            DBHelper = new SqliteHelper(c);
        }
        return DBHelper;
    }

    public static String createTable(Class<?> cls){
        try {
            String sql = "";
            String fieldSql = "";
            String tableName = "";
            Field[] fields = cls.getDeclaredFields();
            Table table = cls.getAnnotation(Table.class);
            tableName = table.tableName();
            for(int i = fields.length-1;i>=0;i--){
                Field a = fields[i];
                if(a.isAnnotationPresent(Column.class)){
                    Column col = a.getAnnotation(Column.class);
                    fieldSql+= col.name()+" "+col.type()+(i==0?"":" ,");
                }
            }
            sql += "DROP TABLE IF EXISTS "+tableName+" ;";
            sql += " CREATE TABLE "+tableName+" ( "+fieldSql+" );";
            return sql;
        }catch (Exception e){
            Log.exception(e);
        }

        return null;
    }
    public static void checkDBVersion(){

    }
}
