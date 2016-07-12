package youtubedownloadhelper.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


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
        }
    }
    public static void finish(FragmentManager fm){
        try{
            if(fm!=null)
                fm.popBackStack();
        }catch (Exception e){
        }
    }


    public static boolean isNetworkConnected(Context context){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


   public static boolean isNetWorkOK(Context context){

           ConnectivityManager cm =
                   (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

           NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
           boolean isConnected = activeNetwork != null &&
                   activeNetwork.isConnectedOrConnecting();
           return isConnected;
   }

}
