package youtubedownloadhelper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.List;

import youtubedownloadhelper.youtube.YoutubePaserTask;

/**
 * Created by andyli on 2016/10/16.
 */

public class LiveActivity extends Activity {
    public final static String BUNDLE_KEY_YOUTUBE_ID = "BUNDLE_KEY_YOUTUBE_ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Bundle bd = intent.getExtras();
        if (type != null && action != null && type.equals("text/plain") && Intent.ACTION_SEND.equals(action)) {
                String video_URL = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (!TextUtils.isEmpty(video_URL)) {
                Intent it = new Intent(LiveActivity.this, LiveService.class);
                it.putExtra(LiveService.INTENT_VIDEO_ID, YoutubePaserTask.getVideoId(video_URL));
                startService(it);
            }

        }else if (bd.containsKey(BUNDLE_KEY_YOUTUBE_ID)) {

            String video_ID = bd.getString(BUNDLE_KEY_YOUTUBE_ID);
            if (!TextUtils.isEmpty(video_ID)) {
                Intent it = new Intent(LiveActivity.this, LiveService.class);
                it.putExtra(LiveService.INTENT_VIDEO_ID, video_ID);
                startService(it);
            }

        }
        finish();
    }
}
