package youtubedownloadhelper;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import youtubedownloadhelper.download.DownLoadActivity;


public class MainActivity extends YouTubeBaseActivity
        implements View.OnClickListener {

    EditText editText;
    Button download;
    Button uninstall;
    private Button playButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        download = (Button) findViewById(R.id.download);
        uninstall = (Button) findViewById(R.id.uninstall);
        playButton = (Button) findViewById(R.id.play);
        download.setOnClickListener(this);
        uninstall.setOnClickListener(this);
        playButton.setOnClickListener(this);

        editText.setText("YW3e51bkRcw");

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

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.download) {
            download();
        } else if (v.getId() == R.id.uninstall) {
            unistall();
        } else if (v.getId() == R.id.play) {
            play();
        }
    }

    private void unistall() {
        Uri packageUri = Uri.parse("package:"+this.getPackageName());
        Intent it = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
        startActivity(it);
    }

    private void download() {
        String youtubeId = editText.getText().toString();
        if (!TextUtils.isEmpty(youtubeId)) {
            Intent it = new Intent();
            it.putExtra(DownLoadActivity.BUNDLE_KEY_YOUTUBE_ID, youtubeId);
            it.setClass(this, DownLoadActivity.class);
            startActivity(it);
        }else{
            Toast.makeText(this, "請輸入合法的youtube Id", Toast.LENGTH_SHORT).show();
        }
    }
    private void play() {
        String youtubeId = editText.getText().toString();
        if (!TextUtils.isEmpty(youtubeId)) {
            Intent it = new Intent();
            it.putExtra(LiveActivity.BUNDLE_KEY_YOUTUBE_ID, youtubeId);
            it.setClass(this, LiveActivity.class);
            startActivity(it);
        }else{
            Toast.makeText(this, "請輸入合法的youtube Id", Toast.LENGTH_SHORT).show();
        }
    }
}
