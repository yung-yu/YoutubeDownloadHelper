package youtubedownloadhelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.WindowManager;

/**
 * Created by andyli on 2016/8/20.
 */
public class LiveService extends Service {
    private final static String TAG = "LiveService";
    private  Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        LiveVideoManager.getInstance(this).open(LiveVideoManager.videoId);

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
