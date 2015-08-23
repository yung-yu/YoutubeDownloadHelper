package andy.youtubedownloadhelper.com.youtubedownloadhelper.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import com.andylibrary.utils.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import andy.youtubedownloadhelper.com.youtubedownloadhelper.MainActivity;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.R;

/**
 * Created by andyli on 2015/8/23.
 */
public class NotifyManager {
    private static NotifyManager instance = null;

    public static NotifyManager getInstance(){
        if(instance == null){
            instance = new NotifyManager();
        }
        return instance;
    }

    public  void showMediaNotification(Context context,Bitmap bmp,String title){

        RemoteViews remoteView = null;
        Intent intent = null;
        PendingIntent pendingIntent = null;
        Notification notification = new Notification();
        NotificationManager notificationManager = (NotificationManager)(context.getSystemService(Context.NOTIFICATION_SERVICE));
        remoteView = new RemoteViews(context.getPackageName(), R.layout.notification);
        intent = new Intent(context,MainActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0 ,intent , PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setImageViewBitmap(R.id.imageView9, bmp);
        remoteView.setTextViewText(R.id.textView5,title);
        notification.icon = R.mipmap.ic_launcher;
        notification.contentView = remoteView;
        notification.contentIntent = pendingIntent;
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(1, notification);
    }

    public void showMediaNotification(Context context,String  bmpurl,String title){
            new sendNotification(context).execute(bmpurl,title);
    }
    public  void cancelMediaNotification(Context context){
        NotificationManager notificationManager = (NotificationManager)(context.getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.cancel(1);
    }

    private class sendNotification extends AsyncTask<String, Void, Bitmap> {

        Context ctx;
        String message;

        public sendNotification(Context context) {
            super();
            this.ctx = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            message = params[1] ;
            try {

                in = new URL(params[0]).openStream();
                Bitmap bmp = BitmapFactory.decodeStream(in);
                return bmp;

            } catch (MalformedURLException e) {
                Log.exception(e);
            } catch (IOException e) {
                Log.exception(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);
            try {
               showMediaNotification(ctx,result,message);

            } catch (Exception e) {
                Log.exception(e);
            }
        }
    }

}
