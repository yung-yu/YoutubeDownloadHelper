package youtubedownloadhelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.WindowManager;

/**
 * Created by andyli on 2016/8/20.
 */
public class LiveService extends Service {
    private final static String TAG = "LiveService";
    private  Context context;
    public static final String INTENT_VIDEO_ID = "INTENT_VIDEO_ID";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String videoId = intent.getStringExtra(INTENT_VIDEO_ID);
        if(!TextUtils.isEmpty(videoId)) {
            LiveVideoManager.getInstance(this).open(videoId);
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





}
