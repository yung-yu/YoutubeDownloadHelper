package youtubedownloadhelper.media;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;

import andy.spiderlibrary.utils.Log;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import youtubedownloadhelper.dbDao.SongItemDao;
import youtubedownloadhelper.dbinfo.SongItem;
import youtubedownloadhelper.dbinfo.Youtube;
import youtubedownloadhelper.utils.AndroidUtils;
import youtubedownloadhelper.utils.NotifyManager;
import youtubedownloadhelper.utils.SystemContent;
import youtubedownloadhelper.youtube.YoutubeloadPaser;

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
    private YoutubeloadPaser youtubeloadPaser;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mAm = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        youtubeloadPaser = new YoutubeloadPaser(context,null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bd = intent.getExtras();
        if(bd != null){
            int cmd = bd.getInt(BUNDLE_CMD);
            switch (cmd){
                case MEDIAPLAYER_START:
                    readyPlay(false, new OnReadyPlayCallBack() {
                        @Override
                        public void onSuccess(SongItem song, boolean isChangeSong) {
                            if(!start()){
                                AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_PLAYBACK_ERROR);
                            }
                        }

                        @Override
                        public void onFailed() {
                            AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_PLAYBACK_ERROR);
                        }
                    });
                    break;
                case MEDIAPLAYER_PAUSE:
                    pause();
                    break;
                case MEDIAPLAYER_STOP:
                    stop();
                    break;
                case MEDIAPLAYER_CHANGESONG:
                    changeSong(new OnReadyPlayCallBack() {
                        @Override
                        public void onSuccess(SongItem song, boolean isChangeSong) {
                            if(!start()){
                                AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_PLAYBACK_ERROR);
                            }else{
                                AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_CHANGESONG);
                            }
                        }

                        @Override
                        public void onFailed() {
                            AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_PLAYBACK_ERROR);
                        }
                    });

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
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
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
    public void changeSong(OnReadyPlayCallBack callBack){
        if(player != null&&player.isPlaying()){
            player.reset();
            player.release();
            player = null;
        }
        readyPlay(true,callBack);
    }



    public void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
            AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_PAUSE);
        }

    }
    public interface  OnReadyPlayCallBack{
        void  onSuccess(SongItem song,boolean isChangeSong);
        void  onFailed();
    }
    public  boolean exists(String URLName){
        try {
            HttpURLConnection.setFollowRedirects(false);

            HttpURLConnection con =  (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            Log.exception(e);
            return false;
        }
    }
    public boolean  start(){
        if (requestFocus()) {
            if (player == null) {
                try {
                    player = new MediaPlayer();
                    SongItem song = PlayerManager.getInstance(context).getCurrentSongItem();
                    if(song!=null){
                        Log.d("play song :"+song.getName());
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
//                    NotifyManager.getInstance().showMediaNotification(context,
//                            "http://img.youtube.com/vi/"+song.getYoutubeId()+"/0.jpg",
//                            song.getName());
                } catch (IOException e) {
                    Log.exception(e);
                    player = null;
                    return false;
                } catch (IllegalStateException e){
                    Log.exception(e);
                    player = null;
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
    public void readyPlay(final boolean isChangeSong , final OnReadyPlayCallBack callBack) {
         new Thread(new Runnable() {
             @Override
             public void run() {
                 SongItem song = PlayerManager.getInstance(context).getCurrentSongItem();
                 if(song!=null){
                     if(exists(song.getUrl())) {
                         if (callBack != null)
                             callBack.onSuccess(song, isChangeSong);
                         return;
                     }else{
                         String url = "https://www.youtube.com/watch?v="+song.getYoutubeId();
                         Youtube youtube = youtubeloadPaser.getYoutube(url, song.getYoutubeId());
                         if(youtube!=null) {
                             SongItem tmpsong =  SongItemDao.getInstance(context).getSongItem(youtube);
                             ContentValues values = new ContentValues();
                             values.put("url", tmpsong.getUrl());
                             song.setUrl(tmpsong.getUrl());
                             SongItemDao.getInstance(context).updateSongItem(values, youtube.getYoutubeId());

                         }
                         if (callBack != null)
                             callBack.onSuccess(song, isChangeSong);
                         return;
                     }
                 }
                 if(callBack!=null  )
                     callBack.onFailed();
             }
         }).start();

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
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d("onCompletion ");
        if(PlayerManager.getInstance(context).isLoop()){
            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }
            PlayerManager.getInstance(context).nextSong();
            changeSong(new OnReadyPlayCallBack() {
                @Override
                public void onSuccess(SongItem song, boolean isChangeSong) {
                    if (!start()) {
                        AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_PLAYBACK_ERROR);
                    } else {
                        AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_CHANGESONG);
                    }
                }

                @Override
                public void onFailed() {
                    AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_PLAYBACK_ERROR);
                }
            });
        }else{
            AndroidUtils.sendBroadCastToPlayer(context, null, SystemContent.MEDIAPLAYER_PLAYBACK_COMPLETED);
        }
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
