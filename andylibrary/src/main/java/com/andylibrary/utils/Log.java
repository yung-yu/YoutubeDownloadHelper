package com.andylibrary.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by andyli on 2015/7/15.
 */
public class Log {
    static Logger logger;
    private static final String EXCEPTION_START = "*******EXCEPTION START*********";
    private static final String EXCEPTION_END = "*******EXCEPTION END*********";
    private static final String TAG = "youtubelog";
    private static final String LOG_FILE = "debug_%g.log";
    private static final String LOG_FILEPATH = "/youtubedownload/log/";


    private static final boolean IS_DEBUG = true;
    private static SimpleFormatter formatter = new SimpleFormatter(){
        @Override
        public String format(LogRecord r) {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(r.getMillis()))+r.getMessage()+"\n";
        }
    };
    public static String getLogFilePath(){
        String path;
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        return path+LOG_FILEPATH;
    }
     public synchronized static Logger getLogger(){
         if(logger==null){
             logger = Logger.getLogger(TAG+"writer");
             try {
                 File file = new File(getLogFilePath());
                 if(!file.exists()){
                     file.mkdirs();
                 }
                 FileHandler fileHandler =   new FileHandler(getLogFilePath()+LOG_FILE, 1024*1024*4, 8,true);
                 fileHandler.setFormatter(formatter);
                 fileHandler.setLevel(Level.ALL);
                 logger.addHandler(fileHandler);
             } catch (IOException e) {
                 e.printStackTrace();
                 android.util.Log.e(TAG,e.toString());
             }

         }
         return logger;
     }
    public static String getProcess(){
        StackTraceElement[] elements =   Thread.currentThread().getStackTrace();
        String className =  elements[4].getClassName();
        className = className.substring(className.lastIndexOf(".")+1);
        String methodName =  elements[4].getMethodName();

        return "["+className+"]"+"["+methodName+"]"+"["+elements[4].getLineNumber()+"]-";
    }
    public static void d(String msg){
        if(!IS_DEBUG){
            return;
        }
       StackTraceElement[] elements =   Thread.currentThread().getStackTrace();
        try {
            msg = getProcess()+msg;
            android.util.Log.d(TAG, msg);
            getLogger().info(msg);
        }catch (Exception e){
            android.util.Log.e(TAG,e.toString());
        }
    }
    public static void exception(Exception exc){
        if(!IS_DEBUG){
            return;
        }
        try {
            String process = getProcess();
            String msg = process+exc.toString();
            android.util.Log.e(TAG, msg);
            getLogger().warning(process+EXCEPTION_START);
            getLogger().warning(msg);
            getLogger().warning(process+EXCEPTION_END);
        }catch (Exception e){
            android.util.Log.e(TAG,e.toString());
        }
    }
    public static void e(String msg){
        if(!IS_DEBUG){
            return;
        }
        try {
             msg = getProcess()+msg;
            android.util.Log.e(TAG, msg);
            getLogger().warning(msg);
        }catch (Exception e){
            android.util.Log.e(TAG,e.toString());
        }
    }
    public static void i(String msg){
        if(!IS_DEBUG){
            return;
        }
        try {
            msg = getProcess()+msg;
            android.util.Log.i(TAG, msg);
            getLogger().info(msg);
        }catch (Exception e){
            android.util.Log.e(TAG,e.toString());
        }
    }

    public static void v(String msg){
        if(!IS_DEBUG){
            return;
        }
        try {
            msg = getProcess()+msg;
            android.util.Log.v(TAG, msg);
            getLogger().fine(msg);
        }catch (Exception e){
            android.util.Log.e(TAG,e.toString());
        }
    }
    public static void w(String msg){
        if(!IS_DEBUG){
            return;
        }
        try {
            msg = getProcess()+msg;
            android.util.Log.w(TAG, msg);
            getLogger().warning(msg);
        }catch (Exception e){
            android.util.Log.e(TAG,e.toString());
        }
    }

}
