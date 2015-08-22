package andy.youtubedownloadhelper.com.youtubedownloadhelper.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.andylibrary.utils.Log;

import andy.youtubedownloadhelper.com.youtubedownloadhelper.utils.SystemContent;

/**
 * Created by andyli on 2015/8/22.
 */
public class PlayerBroadcastReceiver extends BroadcastReceiver {
    public final static String ACTION_NAME = "playerlistener";
    public final static String BUNDLEKEY_PLAYERCMD = "playercmd";
    @Override
    public void onReceive(Context context, Intent intent) {
         Bundle bd = intent.getExtras();
        Intent it = new Intent(MediaPlayerFragment.MEDIAPLAYER_ACTION);
        if(bd!=null){
            int cmd = bd.getInt(BUNDLEKEY_PLAYERCMD);
            switch (cmd){
                case SystemContent.MEDIAPLAYER_START:
                    Log.d("MEDIAPLAYER_START");
                    it.putExtras(bd);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(it);
                    break;
                case SystemContent.MEDIAPLAYER_PAUSE:
                    Log.d("MEDIAPLAYER_PAUSE");
                    it.putExtras(bd);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(it);
                    break;
                case SystemContent.MEDIAPLAYER_STOP:
                    Log.d("MEDIAPLAYER_STOP");
                    it.putExtras(bd);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(it);
                    break;
                case SystemContent.MEDIAPLAYER_PLAYBACK_COMPLETED:
                    Log.d("MEDIAPLAYER_PLAYBACK_COMPLETED");
                    it.putExtras(bd);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(it);
                    break;
                case SystemContent.MEDIAPLAYER_PLAYBACK_ERROR:
                    Log.d("MEDIAPLAYER_PLAYBACK_ERROR");
                    it.putExtras(bd);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(it);
                    break;
                case SystemContent.MEDIAPLAYER_PLAYBACK_PREPARED:
                    Log.d("MEDIAPLAYER_PLAYBACK_PREPARED");
                    it.putExtras(bd);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(it);
                    break;
            }
        }
    }
}
