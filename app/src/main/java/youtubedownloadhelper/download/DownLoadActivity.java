package youtubedownloadhelper.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import youtubedownloadhelper.R;
import youtubedownloadhelper.object.Video;
import youtubedownloadhelper.object.Youtube;
import youtubedownloadhelper.Preferences.SharePerferenceHelper;
import youtubedownloadhelper.download.adapter.DownloadAdapter;
import youtubedownloadhelper.mp3.Mp3Helper;
import youtubedownloadhelper.utils.AndroidUtils;
import youtubedownloadhelper.youtube.YotubeItag;
import youtubedownloadhelper.youtube.YoutubePaserTask;

/**
 * Created by andy on 2015/3/8.
 */
public class DownLoadActivity extends Activity implements AdapterView.OnItemClickListener {
    private final static String TAG = "DownLoadActivity";
    public final static String BUNDLE_KEY_YOUTUBE_ID = "BUNDLE_KEY_YOUTUBE_ID";
    public final static int NOTFTY_ADAPTER = 0;
    public final static int DOWNLOAD_FINISH = 1;
    private TextView tv_title;
    private ImageView iv_title;
    private Context context;
    private Youtube curYoutube;
    private ListView listView;
    private DownloadAdapter downloadAdapter;
    private LinearLayout download_page;
    private RelativeLayout loading_Page;

    private String curVideoId;
    private ImageButton reload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mp3Helper.getInstance().init(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_download);
        context = this;
        tv_title = (TextView) findViewById(R.id.textView);
        iv_title = (ImageView) findViewById(R.id.imageView);
        reload = (ImageButton) findViewById(R.id.reload);
        listView = (ListView) findViewById(R.id.listview);
        download_page = (LinearLayout) findViewById(R.id.download_page);
        loading_Page = (RelativeLayout) findViewById(R.id.loading_Page);
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownLoadActivity.this.finish();
            }
        });
        reload.setEnabled(false);
        reload.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(curVideoId)) {
                    reload.setEnabled(false);
                    youtubeParser(curVideoId);
                }
            }
        });
        downloadAdapter = new DownloadAdapter(this);
        listView.setAdapter(downloadAdapter);
        listView.setOnItemClickListener(this);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Bundle bd = intent.getExtras();
        if (type != null && action != null && type.equals("text/plain") && Intent.ACTION_SEND.equals(action)) {
            curVideoId = intent.getStringExtra(Intent.EXTRA_TEXT);
            youtubeParser(curVideoId);
        } else if (bd != null) {
            if (bd.containsKey(BUNDLE_KEY_YOUTUBE_ID)) {
                curVideoId = intent.getStringExtra(Intent.EXTRA_TEXT);
                youtubeParser(curVideoId);
            }
        }
    }

    public void youtubeParser(String sharedText) {
        if (!TextUtils.isEmpty(sharedText)) {
            download_page.setVisibility(View.GONE);
            loading_Page.setVisibility(View.VISIBLE);

            new YoutubePaserTask(this, new YoutubePaserTask.CallBack() {
                @Override
                public void success(Youtube youtube) {
                    download_page.setVisibility(View.VISIBLE);
                    loading_Page.setVisibility(View.GONE);
                    curYoutube = youtube;
                    tv_title.setText(curYoutube.getTitle());
                    displayImageUrl(iv_title, curYoutube.getImgeUrl());
                    showDownloadList(curYoutube.getVideoList());
                    reload.setEnabled(true);
                }

                @Override
                public void onfail(String Message) {
                    Toast.makeText(DownLoadActivity.this, Message, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).execute(sharedText);


        }
    }


    private void displayImageUrl(final ImageView iv, final String imageUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
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
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }).start();

    }

    private void showDownloadList(ArrayList<Video> list) {
        if (list == null || list.size() == 0) {
            Toast.makeText(DownLoadActivity.this, "找不到影片", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        downloadAdapter.setList(list);
        downloadAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick " + position);
        if (!downloadAdapter.isDownloading(position)) {
            Video video = downloadAdapter.getItem(position);
            if (video.isDownlaod()) {
                AndroidUtils.playVideo(this, video.getLocalFilePath());
            } else {
                downloadVideo(position, video);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case NOTFTY_ADAPTER:
                    if(downloadAdapter != null){
                        downloadAdapter.notifyDataSetChanged();
                    }
                    break;
                case DOWNLOAD_FINISH:
                    if(downloadAdapter != null){
                         Video video = downloadAdapter.getItem(msg.arg1);
                        downloadAdapter.removeDownloadCache(msg.arg1);
                        downloadAdapter.notifyDataSetChanged();


                    }

                    break;
            }
        }
    };

    public void downloadVideo(final int position, final Video video) {
        String fileName = curYoutube.getTitle().trim();
        fileName = fileName.length()>15?fileName.substring(0,15):fileName;
        final DownloadDialog downloadDialog = new DownloadDialog(context, fileName+"_"+YotubeItag.getVideoDescribe(video.getItag())
                                                        + "." + YotubeItag.getVideoType(video.getItag()));
        downloadDialog.setOnFileSelectListener(new DownloadDialog.OnFileSelectListener() {
            @Override
            public void onFileSelected(String filePah) {
                SharePerferenceHelper.getInstance(context).setString("path", filePah);
                DownloadTask task = new DownloadTask(DownLoadActivity.this, handler, position);
                downloadAdapter.putDownloadCache(position, task);
                task.execute(video, filePah, downloadDialog.getCurrentFileName());
            }
        });
        downloadDialog.create().show();
    }


}
