package andy.youtubedownloadhelper.com.youtubedownloadhelper.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.andylibrary.utils.Log;

import andy.youtubedownloadhelper.com.youtubedownloadhelper.R;

/**
 * Created by andyli on 2015/7/25.
 */
public class AndroidUtils {
    public static void startFragment(FragmentManager fragmentManager,Fragment fragment,Bundle bundle, boolean isImed){
     try {
         FragmentTransaction ft = fragmentManager.beginTransaction();
         ft.replace(R.id.container, fragment);
         if(bundle!=null)
             fragment.setArguments(bundle);
         ft.commit();
     }catch (Exception e){
         e.printStackTrace();
         Log.exception(e);
     }
    }
}
