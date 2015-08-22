package andy.youtubedownloadhelper.com.youtubedownloadhelper.media;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.text.TextUtils;
import android.widget.Toast;
import com.andylibrary.utils.Log;
import java.io.IOException;

import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.SongItem;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.utils.AndroidUtils;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.utils.SystemContent;

/**
 * Created by andyli on 2015/8/11.
 */
public class PlayService extends Service implements AudioManager.OnAudioFocusChangeListener ,
        MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener,
        MediaPlayer.OnSeekCompleteListener,MediaPlayer.OnBufferingUpdateListener
        ,MediaPlayer.OnPreparedListener{
    public static final String BUNDLE_CMD = "playservicecmd";
    public static final int MEDIAPLAYER_START = 0;
    public static final int MEDIAPLAYER_PAUSE = 1;
    public static final int MEDIAPLAYER_STOP = 2;
    public static final int MEDIAPLAYER_CHANGESONG = 3;
    private AudioManager mAm;
    private Context context;
    private MediaPlayer player = null;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mAm = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bd = intent.getExtras();
        if(bd != null){
            int cmd = bd.getInt(BUNDLE_CMD);
            switch (cmd){
                case MEDIAPLAYER_START:
                    if(!play()){
                        AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_PLAYBACK_ERROR);
                    }
                    break;
                case MEDIAPLAYER_PAUSE:
                    pause();
                    break;
                case MEDIAPLAYER_STOP:
                    stop();
                    break;
                case MEDIAPLAYER_CHANGESONG:
                    if(!changeSong()){
                        AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_PLAYBACK_ERROR);
                    }
                    break;
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public boolean requestFocus() {
        int result = mAm.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }


    public void resume() {
        if (player != null&&!player.isPlaying()) {
            player.start();
            AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_START);
        }
    }
    public boolean changeSong(){
        if(player != null&&player.isPlaying()){
            player.reset();
            player.release();
            player = null;
        }
        return play();
    }
    public void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
            AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_PAUSE);
        }

    }

    public boolean play() {
        if (requestFocus()) {
            if (player == null) {
                try {
                    player = new MediaPlayer();
                    SongItem song = PlayerManager.getInstance(context).getCurrentSongItem();
                    if(song!=null){
                        if(song.isLocal()){
                            if(!TextUtils.isEmpty(song.getFile())){
                                player.setDataSource(context,Uri.parse(song.getFile()));
                            }else{
                                return false;
                            }
                        }else {
                            if (!TextUtils.isEmpty(song.getUrl())) {
                                player.setDataSource(song.getUrl());
                            } else {
                                return false;
                            }
                        }
                    }else{
                        return false;
                    }
                    player.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
                    player.setOnCompletionListener(this);
                    player.setOnErrorListener(this);
                    player.setOnPreparedListener(this);
                    player.setOnSeekCompleteListener(this);
                    player.setOnBufferingUpdateListener(this);
                    player.prepare();
                    PlayerManager.getInstance(context).setMediaPlayer(player);
                } catch (IOException e) {
                    Log.exception(e);
                    return false;
                } catch (IllegalStateException e){
                    Log.exception(e);
                    return false;
                }
            }
            if (!player.isPlaying()) {
                player.start();
            }
            AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_START);
            return true;
        }
        return false;
    }
    public void stop() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
            AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_STOP);
        }
    }


    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d("onAudioFocusChange :"+focusChange);
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            pause();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            resume();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            mAm.abandonAudioFocus(this);
            //stop();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        Log.d("onBufferingUpdate :" + i);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d("onCompletion ");
        AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_PLAYBACK_COMPLETED);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Log.d("onError ");
        AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_PLAYBACK_ERROR);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("onPrepared ");
        AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_PLAYBACK_PREPARED);
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        Log.d("onSeekComplete ");
    }
}
