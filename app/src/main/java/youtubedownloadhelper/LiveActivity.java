package youtubedownloadhelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import youtubedownloadhelper.dbinfo.Youtube;
import youtubedownloadhelper.download.DownLoadActivity;
import youtubedownloadhelper.youtube.YoutubeloadPaser;

/**
 * Created by andyli on 2016/10/16.
 */

public class LiveActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Bundle bd = intent.getExtras();
        if (type != null && action != null && type.equals("text/plain") && Intent.ACTION_SEND.equals(action)) {

            LiveVideoManager.setVideoId(YoutubeloadPaser.getVideoId(intent.getStringExtra(Intent.EXTRA_TEXT)));
            if (!TextUtils.isEmpty(LiveVideoManager.getVideoId())) {
                stopService(new Intent(LiveActivity.this, LiveService.class));
                startService(new Intent(LiveActivity.this, LiveService.class));
            }
        }
        finish();
    }
}
