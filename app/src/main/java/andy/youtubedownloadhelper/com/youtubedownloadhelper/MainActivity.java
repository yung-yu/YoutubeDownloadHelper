package andy.youtubedownloadhelper.com.youtubedownloadhelper;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import andy.youtubedownloadhelper.com.youtubedownloadhelper.list.YoutubeListFragment;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.media.MediaPlayerFragment;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.utils.AndroidUtils;


public class MainActivity extends AppCompatActivity {
   Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AndroidUtils.startFragment(getSupportFragmentManager(), R.id.container, YoutubeListFragment.class, null, false);
        AndroidUtils.startFragment(getSupportFragmentManager(), R.id.playerconatainer, MediaPlayerFragment.class, null, false);
    }

}
