package youtubedownloadhelper.download;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import andy.spiderlibrary.utils.Log;
import youtubedownloadhelper.R;
import youtubedownloadhelper.dbDao.YoutubeDao;
import youtubedownloadhelper.dbinfo.Video;
import youtubedownloadhelper.dbinfo.Youtube;
import youtubedownloadhelper.Preferences.sharePerferenceHelper;
import youtubedownloadhelper.youtube.YotubeItag;
import youtubedownloadhelper.youtube.YoutubeloadPaser;

/**
 * Created by andy on 2015/3/8.
 */
public class DownLoadActivity extends Activity {

    TextView tv_title;
    ImageView iv_title;
    LinearLayout video_contain;
    Context context;
    Youtube curYoutube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.download);
        context = this;
        tv_title = (TextView) findViewById(R.id.textView);
        iv_title = (ImageView) findViewById(R.id.imageView);



        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DownLoadActivity.this.finish();
            }
        });
        video_contain = (LinearLayout) findViewById(R.id.video_contain);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Bundle bd  = intent.getExtras();
        if (type != null&&action!=null&&type.equals("text/plain")&&Intent.ACTION_SEND.equals(action) ) {
            handleSendText(intent); // Handle text being sent
        }else if(bd!=null){
              if(bd.containsKey("youtube")){
                  youtubePaser(bd.getString("youtube"));
              }
        }
    }

   public void youtubePaser(String sharedText){
       if (!TextUtils.isEmpty(sharedText)) {
           Log.d("youtube :" + sharedText);
           new YoutubeloadPaser(this, new YoutubeloadPaser.CallBack(){
               @Override
               public void success(Youtube youtube) {
                   curYoutube = youtube;

                   tv_title.setText(curYoutube.getTitle());
                   displayImageUrl(iv_title, curYoutube.getImgeUrl());
                   showDownloadList(curYoutube.getVideoList());
               }

               @Override
               public void onfail(String Message) {
                   Toast.makeText(DownLoadActivity.this,Message,Toast.LENGTH_SHORT).show();
                   finish();
               }
           }).execute(sharedText);


       }
   }
    public void handleSendText(Intent intent) {
        youtubePaser(intent.getStringExtra(Intent.EXTRA_TEXT));

    }

    private void  displayImageUrl(final ImageView iv, final String imageUrl)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(input);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageBitmap(bitmap);
                        }
                    });
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    return ;
                }
            }
        }).start();

    }
    private void showDownloadList(ArrayList<Video> list){
        video_contain.removeAllViews();
        if(list==null||list.size()==0)
            return;
        for(int i=0;i<list.size();i++){
            video_contain.addView(getView(list.get(i)).convertView);
        }
    }
    public class ViewHolder{
        Button bt;
        ProgressBar progressBar;
        View convertView;
        Video video;
    }
    public ViewHolder getView(final Video video) {

        ViewHolder vh = new ViewHolder();

        vh.convertView = LayoutInflater.from(DownLoadActivity.this).inflate(R.layout.downloaditem,null);
        vh.bt = (Button) vh.convertView.findViewById(R.id.button);
        vh.progressBar = (ProgressBar)vh.convertView.findViewById(R.id.progressBar2);
        vh.video = video;
        vh.bt.setText(YotubeItag.getVideoDescribe(video.getItag()));
        vh.bt.setTag(vh);
        vh.bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ViewHolder vh = (ViewHolder) v.getTag();
                final DownloadDialog downloadDialog =  new DownloadDialog(context, curYoutube.getTitle()+"."+YotubeItag.getVideoType(vh.video.getItag()));
                downloadDialog.setPositiveButton(R.string.alert_download, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharePerferenceHelper.getIntent(context).setString("path",downloadDialog.getCurrentFilePath());
                        new DownloadTask(DownLoadActivity.this,vh).execute(vh.video.getVideoUrl(),downloadDialog.getCurrentFilePath(),downloadDialog.getCurrentFileName());
                    }
                }).setNegativeButton(R.string.alert_cancel, null).create().show();

            }
        });
        return vh;
    }

}
