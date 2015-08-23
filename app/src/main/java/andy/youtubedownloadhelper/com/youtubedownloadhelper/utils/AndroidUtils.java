package andy.youtubedownloadhelper.com.youtubedownloadhelper.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.andylibrary.utils.Log;

import andy.youtubedownloadhelper.com.youtubedownloadhelper.R;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.media.PlayService;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.media.PlayerBroadcastReceiver;

/**
 * Created by andyli on 2015/7/25.
 */
public class AndroidUtils {

    public static void startFragment(FragmentManager fm,int weightId,Class<? extends Fragment> cls,Bundle bundle, boolean isaddToBackStack){
     try {
             Fragment f  = fm.findFragmentByTag(cls.getName());
            if(f==null) {
                f = cls.newInstance();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(weightId, f, cls.getName());
                if (isaddToBackStack) {
                    ft.addToBackStack(cls.getName());
                }
                if (bundle != null)
                    f.setArguments(bundle);
                ft.commit();
            }else{
                fm.popBackStackImmediate(cls.getName(),0);
            }


     }catch (Exception e){
         e.printStackTrace();
         Log.exception(e);
     }
    }
    public static void startFragment(FragmentManager fm,int weightId,Class<? extends Fragment> cls,Bundle bundle){
        try {
            Fragment  f = cls.newInstance();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(weightId, f, cls.getName());
                ft.addToBackStack(null);

            if (bundle != null)
                f.setArguments(bundle);
            ft.commit();

        }catch (Exception e){
            e.printStackTrace();
            Log.exception(e);
        }
    }
    public static void finish(FragmentManager fm){
        try{
            if(fm!=null)
                fm.popBackStack();
        }catch (Exception e){
            Log.exception(e);
        }
    }

    public static void sendBroadCastToPlayer(Context context,Bundle bd,int cmd){
          try{
              Intent it = new Intent(PlayerBroadcastReceiver.ACTION_NAME);
              if(bd!=null){
                  bd.putInt(PlayerBroadcastReceiver.BUNDLEKEY_PLAYERCMD, cmd);
                  it.putExtras(bd);
              }else {
                  it.putExtra(PlayerBroadcastReceiver.BUNDLEKEY_PLAYERCMD, cmd);
              }
              context.sendBroadcast(it);
          }catch (Exception e){
              Log.exception(e);
          }
    }
    public static boolean isNetworkConnected(Context context){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static void sendToPlayService(Context context,Bundle bd,int cmd){
        try{
            Intent it = new Intent(context,PlayService.class);
            if(bd!=null){
                bd.putInt(PlayService.BUNDLE_CMD, cmd);
                it.putExtras(bd);
            }else {
                it.putExtra(PlayService.BUNDLE_CMD, cmd);
            }
            context.startService(it);
        }catch (Exception e){
            Log.exception(e);
        }
    }
}
