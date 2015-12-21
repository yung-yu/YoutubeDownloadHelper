package andy.youtubedownloadhelper.com.youtubedownloadhelper.media;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import andy.spiderlibrary.utils.Log;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.R;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.Youtube;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.utils.AndroidUtils;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.utils.SystemContent;

/**
 * Created by andyli on 2015/8/12.
 */
public class MediaPlayerFragment extends Fragment  implements SeekBar.OnSeekBarChangeListener {
    public final static String MEDIAPLAYER_ACTION = "PLAYERACTION";
    Activity activity;
    ImageView imageView;
    ArrayList<Youtube> list;
    Youtube youtube;

    Messenger messenger;
    boolean mBound;
    ImageView play , stop, next,previous;
    PlayActionReciver playActionReciver;
    SeekBar playSeekBar;
    TimerTask mTimerTask;
    boolean isChanging = false;
    TextView title;
    TextView curTime;
    TextView maxTime;
    @Override
    public void onAttach(Activity activity) {
        Log.d("MediaPlayerFragment onAttach");
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.mediaplayerfragment,null);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bd = getArguments();
        play  = (ImageView) view.findViewById(R.id.imageView4);
        stop  = (ImageView) view.findViewById(R.id.imageView5);
        previous  = (ImageView) view.findViewById(R.id.imageView6);
        next  = (ImageView) view.findViewById(R.id.imageView7);
        playSeekBar = (SeekBar) view.findViewById(R.id.seekBar);
        title = (TextView) view.findViewById(R.id.textView3);
        curTime = (TextView) view.findViewById(R.id.curtime);
        maxTime = (TextView) view.findViewById(R.id.maxtime);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeUI();
               if(PlayerManager.getInstance(activity).isPlay())
                   AndroidUtils.sendToPlayService(activity,null,PlayService.MEDIAPLAYER_PAUSE);
                else
                   AndroidUtils.sendToPlayService(activity, null, PlayService.MEDIAPLAYER_START);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeUI();
                AndroidUtils.sendToPlayService(activity, null, PlayService.MEDIAPLAYER_STOP);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeUI();
                PlayerManager.getInstance(activity).nextSong();
                AndroidUtils.sendToPlayService(activity, null, PlayService.MEDIAPLAYER_CHANGESONG);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeUI();
                PlayerManager.getInstance(activity).preSong();
                AndroidUtils.sendToPlayService(activity, null, PlayService.MEDIAPLAYER_CHANGESONG);
            }
        });

        play.setImageResource(PlayerManager.getInstance(activity).isPlay() ? R.drawable.player_pause : R.drawable.player_play);
        playSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(PlayerManager.getInstance(activity).isPlay()) {
            title.setText(PlayerManager.getInstance(activity).getCurrentSongItem().getName());
            title.setSelected(true);
            trackerSong();
        }else{
            if(PlayerManager.getInstance(activity).getMediaPlayer()!=null){
                title.setText(PlayerManager.getInstance(activity).getCurrentSongItem().getName());
                title.setSelected(true);
                trackerSong();
            }
        }
    }

    public void closeUI(){
        play.setEnabled(false);
        stop .setEnabled(false);
        previous.setEnabled(false);
        next.setEnabled(false);
        playSeekBar.setEnabled(false);
    }
    public void openUI(){
        play.setEnabled(true);
        stop .setEnabled(true);
        previous.setEnabled(true);
        next.setEnabled(true);
        playSeekBar.setEnabled(true);
    }
    public void trackerSong(){
        if(PlayerManager.getInstance(activity).getMediaPlayer()==null)
            return;
        try {
            playSeekBar.setMax(PlayerManager.getInstance(activity).getMediaPlayer().getDuration());
            playSeekBar.setProgress(PlayerManager.getInstance(activity).getMediaPlayer().getCurrentPosition());
            curTime.setText(getTimeStr(PlayerManager.getInstance(activity).getMediaPlayer().getCurrentPosition()));
            maxTime.setText(getTimeStr(PlayerManager.getInstance(activity).getMediaPlayer().getDuration()));
        }catch (IllegalStateException e){
            Log.exception(e);
        }
        Timer mTimer = new Timer();
        if(mTimerTask!=null)
            mTimerTask.cancel();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if(isChanging)
                    return;
                try {
                    if(PlayerManager.getInstance(activity).isPlay()) {
                        playSeekBar.setProgress(PlayerManager.getInstance(activity).getMediaPlayer().getCurrentPosition());
                        activity.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   curTime.setText(getTimeStr(PlayerManager.getInstance(activity).getMediaPlayer().getCurrentPosition()));
                               }
                        });

                    }
                }catch (IllegalStateException e){
                    Log.exception(e);
                }
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);
    }
    public void stopTrackSong(){
        if(mTimerTask!=null)
            mTimerTask.cancel();
        curTime.setText(getTimeStr(0));
        playSeekBar.setProgress(0);
    }
    @Override
    public void onStart() {
        super.onStart();

        playActionReciver = new PlayActionReciver();
        IntentFilter intentFilter = new IntentFilter(MediaPlayerFragment.MEDIAPLAYER_ACTION);
        LocalBroadcastManager.getInstance(activity).registerReceiver(playActionReciver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopTrackSong();
        if(playActionReciver!=null)
             LocalBroadcastManager.getInstance(activity).unregisterReceiver(playActionReciver);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if(isChanging) {
            if( PlayerManager.getInstance(activity).isPlay())
                PlayerManager.getInstance(activity).getMediaPlayer().seekTo(i);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isChanging=true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isChanging=false;
    }

    public class PlayActionReciver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bd = intent.getExtras();
            if(bd!=null) {
                int cmd = bd.getInt(PlayerBroadcastReceiver.BUNDLEKEY_PLAYERCMD);
                switch (cmd) {
                    case SystemContent.MEDIAPLAYER_START:
                        Log.d("MEDIAPLAYER_START");
                        openUI();
                        play.setImageResource(R.drawable.player_pause);
                        title.setText(PlayerManager.getInstance(activity).getCurrentSongItem().getName());
                        title.setSelected(true);
                        trackerSong();
                        break;
                    case SystemContent.MEDIAPLAYER_PAUSE:
                        Log.d("MEDIAPLAYER_PAUSE");
                        openUI();
                        play.setImageResource(R.drawable.player_play);
                        break;
                    case SystemContent.MEDIAPLAYER_STOP:
                        Log.d("MEDIAPLAYER_STOP");
                        stopTrackSong();
                        openUI();
                        play.setImageResource(R.drawable.player_play);
                        break;
                    case SystemContent.MEDIAPLAYER_PLAYBACK_COMPLETED:
                        Log.d("MEDIAPLAYER_PLAYBACK_COMPLETED");
                        stopTrackSong();
                        if(!PlayerManager.getInstance(activity).isLoop()) {
                            openUI();
                        }
                        break;
                    case SystemContent.MEDIAPLAYER_PLAYBACK_ERROR:
                        Log.d("MEDIAPLAYER_PLAYBACK_ERROR");
                        closeUI();
                        PlayerManager.getInstance(activity).nextSong();
                        AndroidUtils.sendToPlayService(activity, null, PlayService.MEDIAPLAYER_CHANGESONG);
                        break;
                    case SystemContent.MEDIAPLAYER_PLAYBACK_PREPARED:
                        Log.d("MEDIAPLAYER_PLAYBACK_PREPARED");
                        openUI();
                        play.setImageResource(R.drawable.player_pause);
                        title.setText(PlayerManager.getInstance(activity).getCurrentSongItem().getName());
                        title.setSelected(true);
                        trackerSong();
                        break;
                }

            }
        }
    }
    public String getTimeStr(int misScond){
        return new SimpleDateFormat("mm:ss").format(new Date(misScond));
    }
}
