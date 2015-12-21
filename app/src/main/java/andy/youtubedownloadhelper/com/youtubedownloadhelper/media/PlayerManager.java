package andy.youtubedownloadhelper.com.youtubedownloadhelper.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.text.TextUtils;

import andy.spiderlibrary.utils.Log;

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

    private boolean isUpdate = false;
    private  String youtubeId = "";
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


    public boolean isUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public SongItem getCurrentSongItem(){
        if(songList!=null&&songList.size()>0){
            youtubeId =  PlayerPerference.getInstance(context).getId();
            if(!TextUtils.isEmpty(youtubeId)) {
                for (int i = 0; i < songList.size(); i++) {
                    if (youtubeId.equals(songList.get(i).getYoutubeId())) {
                        return songList.get(i);
                    }
                }
            }else{
                youtubeId = songList.get(0).getYoutubeId();
                PlayerPerference.getInstance(context).setId(youtubeId);
                return songList.get(0);
            }
        }
        return null;
    }

    //下一首歌
    public void nextSong(){
        if(songList!=null){
            int index = -1;
            for(int i = 0;i<songList.size();i++){
                if(youtubeId.equals(songList.get(i).getYoutubeId())){
                    index = i;
                }
            }
            index++;
            if(index<songList.size()){
                PlayerPerference.getInstance(context).setId(songList.get(index).getYoutubeId());
            }else if(index>=songList.size()){
                index=0;
                PlayerPerference.getInstance(context).setId(songList.get(index).getYoutubeId());
            }
        }
    }
    //上一首歌
    public void preSong(){
        if(songList!=null){
            int index = -1;
            for(int i = 0;i<songList.size();i++){
                if(youtubeId.equals(songList.get(i).getYoutubeId())){
                    index = i;
                }
            }
            index++;
            if(index<songList.size()&&index>=0){
                PlayerPerference.getInstance(context).setId(songList.get(index).getYoutubeId());
            }else if(index<0){
                index=songList.size()-1;
                PlayerPerference.getInstance(context).setId(songList.get(index).getYoutubeId());
            }
        }
    }
    public boolean  setSong(String youtubeId){
       this.youtubeId = youtubeId;
       PlayerPerference.getInstance(context).setId(youtubeId);
       return true;

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


