package youtubedownloadhelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by andyli on 2016/8/20.
 */
public class LiveService extends Service {
    private final static String TAG = "LiveService";
    private Context context;
    public static final String INTENT_VIDEO_ID = "INTENT_VIDEO_ID";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String videoId = intent.getStringExtra(INTENT_VIDEO_ID);
        if (!TextUtils.isEmpty(videoId)) {
            LiveVideoManager.getInstance(this).open(videoId);
            startNotify(videoId);
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LiveVideoManager.getInstance(this).close();

    }


    public void startNotify(String videoId) {
        Intent intent = new Intent(LiveService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(LiveService.this, 0, intent, 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(LiveService.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Youtube 影片")
                        .setContentText("Playing~~")
                        .setContentIntent(pendingIntent);
        Notification notification = mBuilder.build();
        startForeground(9999, notification);
    }

}
