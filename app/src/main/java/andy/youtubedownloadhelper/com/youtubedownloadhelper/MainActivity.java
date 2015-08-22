package andy.youtubedownloadhelper.com.youtubedownloadhelper;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import com.andylibrary.utils.Log;

import andy.youtubedownloadhelper.com.youtubedownloadhelper.list.YoutubeListFragment;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.media.MediaPlayerFragment;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.media.PlayService;
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy");
    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.KEYCODE_BACK){
            Log.d("onKeyDown back");
            new AlertDialog.Builder(this)
                    .setMessage("是否要離開")
                    .setNegativeButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MainActivity.this.stopService(new Intent(MainActivity.this, PlayService.class));
                            finish();
                        }
                    })
                    .setPositiveButton("取消",null)
                    .create().show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
