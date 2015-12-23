package youtubedownloadhelper.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by andyli on 2015/8/22.
 */
public class PlayerPerference {
    static PlayerPerference instance;
    Context context;

    public PlayerPerference(Context context) {
       this.context = context;
    }
    public static PlayerPerference getInstance(Context context){
        if(instance==null){
            instance = new PlayerPerference(context);
        }
        return instance;
    }
    public SharedPreferences getSharedPreferences(){
        return context.getSharedPreferences("PlayerPerference" ,Context.MODE_PRIVATE);
    }
    public String getId(){
         return getSharedPreferences().getString("youtubeId", "");
    }
    public void setId(String id){
        SharedPreferences.Editor  editor = getSharedPreferences().edit();
        editor.putString("youtubeId", id);
        editor.commit();
    }
}
