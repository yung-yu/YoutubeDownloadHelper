package youtubedownloadhelper.media;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;

import andy.spiderlibrary.utils.Log;
import youtubedownloadhelper.dbDao.YoutubeDao;
import youtubedownloadhelper.dbinfo.Youtube;
import youtubedownloadhelper.youtube.YoutubeloadPaser;

/**
 * Created by andy on 2015/3/8.
 */
public class AddPlayListActivity extends Activity {


    Context context;
    Youtube curYoutube;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = this;
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Bundle bd  = intent.getExtras();
        if (type != null&&action!=null&&type.equals("text/plain")&&Intent.ACTION_SEND.equals(action) ) {
            handleSendText(intent); // Handle text being sent
            return;
        }
        finish();
    }

   public void youtubePaser(String sharedText){
       if (!TextUtils.isEmpty(sharedText)) {
           Log.d("youtube :" + sharedText);
           new YoutubeloadPaser(this, new YoutubeloadPaser.CallBack(){
               @Override
               public void success(Youtube youtube) {
                   curYoutube = youtube;
                   if(curYoutube!=null)
                       if(YoutubeDao.getInstance(context).addYoutube(curYoutube)){
                           Toast.makeText(context,"收藏成功",Toast.LENGTH_SHORT).show();
                       }else{
                           Toast.makeText(context,"已加入收藏",Toast.LENGTH_SHORT).show();
                       }
                   PlayerManager.getInstance(context).setIsUpdate(true);
                   finish();
               }

               @Override
               public void onfail(String Message) {
                   Toast.makeText(AddPlayListActivity.this,Message,Toast.LENGTH_SHORT).show();
                   finish();
               }
           }).execute(sharedText);


       }
   }
    public void handleSendText(Intent intent) {
        youtubePaser(intent.getStringExtra(Intent.EXTRA_TEXT));

    }


}
