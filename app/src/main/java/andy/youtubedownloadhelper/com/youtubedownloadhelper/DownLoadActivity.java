package andy.youtubedownloadhelper.com.youtubedownloadhelper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by andy on 2015/3/8.
 */
public class DownLoadActivity extends Activity {

    TextView tv_title;
    ImageView iv_title;
    ListView listview;
    DownloadAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download);
        tv_title = (TextView) findViewById(R.id.textView);
        iv_title = (ImageView) findViewById(R.id.imageView);
        listview = (ListView) findViewById(R.id.listView);
        adapter = new DownloadAdapter(this);
        listview.setAdapter(adapter);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {

                new YoutubeloadPaser(this, new YoutubeloadPaser.CallBack(){
                    @Override
                    public void success(Youtube youtube) {
                        tv_title.setText(youtube.getTitle());
                        displayImageUrl(iv_title,youtube.getThumbnail_url());
                        adapter.setData(youtube.getVideoList());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onfail(String Message) {
                        Toast.makeText(DownLoadActivity.this,Message,Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).execute(sharedText);


        }
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
    private class DownloadAdapter extends BaseAdapter{
        Context context;
        List<Video> data;

        public List<Video> getData() {
            return data;
        }

        public void setData(List<Video> data) {
            this.data = data;
        }

        public DownloadAdapter(Context context){
           this.context = context;
        }

        @Override
        public int getCount() {
            if(data!=null)
                return data.size();
            return 0;
        }

        @Override
        public Video getItem(int position) {
            if(data!=null)
                if(position < data.size())
                return data.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        private class ViewHolder{
            Button bt;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if(convertView==null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.downloaditem,null);
                vh.bt = (Button) convertView.findViewById(R.id.button);
                convertView.setTag(vh);
            }
            else{
                vh = (ViewHolder) convertView.getTag();
            }
            Video video = getItem(position);
            vh.bt.setText("載點"+String.valueOf(position+1));
            vh.bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Video video = getItem(position);
                    final DownloadDialog downloadDialog =  new DownloadDialog(context, "tmp.mp4");
                    downloadDialog.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DownloadTask(context).execute(video.getUrl(),downloadDialog.getCurrentFilePath(),downloadDialog.getCurrentFileName());
                        }
                    })
                            .setNegativeButton(R.string.alert_cancel,null).create().show();
                    Toast.makeText(DownLoadActivity.this, video.getUrl(), Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }
    }

}
