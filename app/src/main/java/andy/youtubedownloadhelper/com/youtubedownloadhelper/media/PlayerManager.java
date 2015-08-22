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
//        SongItem item = new SongItem();
//        item.setIsLocal(false);
//        item.setName("測試歌曲");
//
//       //  String uri = "android.resource://" + context.getPackageName() + "/"+ R.raw.test;
//        item.setUrl("http://r3---sn-u4o-u2x6.googlevideo.com/videoplayback?fexp=9407992%2C9408206%2C9408710%2C9409069%2C9409205%2C9409249%2C9412914%2C9415365%2C9415485%2C9416023%2C9416126%2C9416898%2C9417577%2C9417707%2C9417930%2C9418153%2C9418203%2C9418449%2C9419031%2C9419444&id=o-ADQeulUyHqG2o07aj_hQ7Nnvg2H30IBwmGk_5a_AZUGv&mn=sn-u4o-u2x6&mm=31&upn=Tiyet86ukWU&ip=49.219.112.13&key=yt5&ms=au&mv=m&mt=1440221547&sparams=dur%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpl%2Csource%2Cupn%2Cexpire&lmt=1439947609262167&expire=1440243260&mime=video%2F3gpp&itag=17&pl=17&dur=265.450&source=youtube&ipbits=0&initcwndbps=1238750&signature=021AB0648308A908B74F97AA542D10781E1058A9.C8461F05FD0C2F080E184813A525BC47CE2679F2&sver=3");
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
        }
        return false;
    }



}


