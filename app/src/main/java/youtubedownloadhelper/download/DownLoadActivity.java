package youtubedownloadhelper.download;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import youtubedownloadhelper.R;
import youtubedownloadhelper.dbinfo.Video;
import youtubedownloadhelper.dbinfo.Youtube;
import youtubedownloadhelper.Preferences.SharePerferenceHelper;
import youtubedownloadhelper.mp3.Mp3Helper;
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
    private ProgressDialog mProgressDialog;
    private String curVideoId;
    private ImageView reload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mp3Helper.getInstance().init(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.download);
        context = this;
        tv_title = (TextView) findViewById(R.id.textView);
        iv_title = (ImageView) findViewById(R.id.imageView);
        reload = (ImageView) findViewById(R.id.reload);
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
                playVideo(video.getLocalFilePath());
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



    public class MyViewHolder {
        Button bt;
        ImageView mp3Button;
        ProgressBar progressBar;
        Video video;
        TextView text;

        public MyViewHolder(View itemView) {
            bt = (Button) itemView.findViewById(R.id.button);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            text = (TextView) itemView.findViewById(R.id.text);
            mp3Button = (ImageView) itemView.findViewById(R.id.mp3button);
        }

        public Video getVideo() {
            return video;
        }

        public void setVideo(Video video) {
            this.video = video;
        }
    }

    public class DownloadAdapter extends BaseAdapter {
        private List<Video> list;
        private Context context;
        private HashMap<Integer, DownloadTask> taskCache = new HashMap<>();

        public DownloadAdapter(Context context) {
            this.context = context;
        }

        public void setList(List<Video> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        public Video getItem(int position) {
            return list != null && position < list.size() ? list.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void putDownloadCache(int position, DownloadTask task) {
            taskCache.put(position, task);
        }
        public void removeDownloadCache(int positon) {
            taskCache.put(positon, null);
        }
        public boolean isDownloading(int position) {
            return taskCache.get(position) != null && !taskCache.get(position).isCancelled();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MyViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.downloaditem, null);
                holder = new MyViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (MyViewHolder) convertView.getTag();
            }
            Video video = getItem(position);
            holder.mp3Button.setVisibility(View.GONE);
            if (holder != null && video != null) {
                holder.setVideo(video);

                DownloadTask task = taskCache.get(position);
                if (task != null && !task.isCancelled()) {
                    holder.bt.setVisibility(View.VISIBLE);

                    holder.bt.setTag(task);
                    holder.bt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadTask task = (DownloadTask) v.getTag();
                            if (task != null && !task.isCancelled()) {
                                task.cancel(true);
                                taskCache.remove(task);
                                downloadAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.progressBar.setProgress(task.curProgress);
                    holder.text.setText(YotubeItag.getVideoDescribe(video.getItag())
                            +"-("+task.curProgress+"/100)");
                } else {
                    holder.bt.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.GONE);
                    holder.text.setText(YotubeItag.getVideoDescribe(video.getItag())
                            +(video.isDownlaod()?"-播放":""));
                    if(video.isDownlaod()) {
                        holder.mp3Button.setVisibility(View.VISIBLE);
                        holder.mp3Button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                final Video video = downloadAdapter.getItem(position);
                                final File file = new File(video.getLocalFilePath());

                                final DownloadDialog downloadDialog = new DownloadDialog(context, file.getName() + ".mp3");
                                downloadDialog.setOnFileSelectListener(new DownloadDialog.OnFileSelectListener() {
                                    @Override
                                    public void onFileSelected(final String filePah) {
                                        Mp3Helper.getInstance().covertToMp3(DownLoadActivity.this, file.getPath(), filePah + "/" + downloadDialog.getCurrentFileName(), new Mp3Helper.OnCovertListener() {
                                            @Override
                                            public void onStart() {
                                                if (mProgressDialog == null) {
                                                    mProgressDialog = new ProgressDialog(DownLoadActivity.this);
                                                    mProgressDialog.setMessage("mp3轉換中...");
                                                }
                                                mProgressDialog.show();

                                            }

                                            @Override
                                            public void onSuccess() {
                                                if (mProgressDialog != null) {
                                                    mProgressDialog.dismiss();
                                                }
                                                new AlertDialog.Builder(DownLoadActivity.this)
                                                        .setMessage(R.string.mp3_success)
                                                        .setPositiveButton(R.string.Alert_ok, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                playMusic(filePah + "/" + downloadDialog.getCurrentFileName());
                                                                dialog.cancel();
                                                            }
                                                        })
                                                        .setNegativeButton(R.string.alert_cancel, null)
                                                        .create().show();


                                            }

                                            @Override
                                            public void onFailed() {
                                                if (mProgressDialog != null) {
                                                    mProgressDialog.dismiss();
                                                }

                                            }

                                            @Override
                                            public void onProgress(String msg) {
                                                if (mProgressDialog != null) {
                                                    mProgressDialog.setMessage(msg);
                                                }
                                            }

                                        });
                                    }
                                });
                                downloadDialog.create().show();
                            }
                        });
                    }

                }
            }
            return convertView;
        }


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

    public void playVideo(String filePath) {
        Uri contentUri = Uri.parse(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(contentUri, "video/mp4");
        context.startActivity(intent);
    }
    public void playMusic(String filePath) {
        Uri contentUri = Uri.parse(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(contentUri, "audio/*");
        context.startActivity(intent);
    }
}
