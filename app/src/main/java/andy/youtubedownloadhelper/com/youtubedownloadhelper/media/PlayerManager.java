package andy.youtubedownloadhelper.com.youtubedownloadhelper.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.text.TextUtils;

import com.andylibrary.utils.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import andy.youtubedownloadhelper.com.youtubedownloadhelper.Preferences.PlayerPerference;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.R;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.SongItem;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.utils.AndroidUtils;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.utils.SystemContent;

/**
 * Created by andyli on 2015/8/22.
 */
public class PlayerManager {

    private static PlayerManager instance;
    private Context context;

    private MediaPlayer mediaPlayer;
    private ArrayList<SongItem> songList;
    private int index = 0;
    public PlayerManager(Context context) {
        this.context = context;
    }
    public static PlayerManager getInstance(Context context){
           if(instance==null){
               instance = new PlayerManager(context);
           }
        return instance;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public ArrayList<SongItem> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<SongItem> songList) {
        this.songList = songList;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public int  getIndex(){
        return index;
    }
    public SongItem getCurrentSongItem(){
        if(songList!=null){
            index =  PlayerPerference.getInstance(context).getIndex();
            if(index<songList.size()){
                return songList.get(index);
            }
        }
        return null;
    }
    //下一首歌
    public void nextSong(){
        if(songList!=null){
            index++;
            if(index<songList.size()){
                PlayerPerference.getInstance(context).setIndex(index);
            }else if(index==songList.size()){
                index=0;
                PlayerPerference.getInstance(context).setIndex(0);
            }
        }
    }
    //上一首歌
    public void preSong(){
        if(songList!=null){
            index--;
            if(index<songList.size()&&index>=0){
                PlayerPerference.getInstance(context).setIndex(index);
            }else if(index<0){
                index=songList.size()-1;
                PlayerPerference.getInstance(context).setIndex(songList.size()-1);
            }
        }
    }
    public  boolean isLoop(){
        return true;
    }
    public  boolean isPlay(){
        try {
            return mediaPlayer != null && mediaPlayer.isPlaying() ? true : false;
        }catch (Exception e){
            Log.exception(e);
            mediaPlayer = null;
        }
        return false;
    }



}


