package andy.youtubedownloadhelper.com.youtubedownloadhelper.list;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andylibrary.utils.Log;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import andy.youtubedownloadhelper.com.youtubedownloadhelper.MainActivity;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.R;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbDao.SongItemDao;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbDao.VideoDao;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbDao.YoutubeDao;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.SongItem;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.Video;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.Youtube;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.download.DownLoadActivity;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.media.MediaPlayerFragment;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.media.PlayService;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.media.PlayerBroadcastReceiver;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.media.PlayerManager;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.utils.AndroidUtils;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.utils.ImageManager;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.utils.SystemContent;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.youtube.YoutubeloadPaser;

/**
 * Created by andyli on 2015/7/25.
 */
public class YoutubeListFragment extends Fragment {
    RecyclerView recyclerView;
    YoutubeAdapter youtubeAdapter;
    Activity activity;

    @Override
    public void onAttach(Activity activity) {
        Log.d("YoutubeListFragment onAttach");
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_youtubelist,null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("YoutubeListFragment onViewCreated");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        if( youtubeAdapter == null ){
            youtubeAdapter = new YoutubeAdapter(activity);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(youtubeAdapter);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("YoutubeListFragment onCreate");
        updateYoutubeList();
    }

    PlayActionReciver playActionReciver;
    @Override
    public void onResume() {
        super.onResume();
        playActionReciver = new PlayActionReciver();
        IntentFilter intentFilter = new IntentFilter(MediaPlayerFragment.MEDIAPLAYER_ACTION);
        LocalBroadcastManager.getInstance(activity).registerReceiver(playActionReciver, intentFilter);
    }

    @Override
    public void onPause() {

        Log.d("YoutubeListFragment onPause");
        super.onPause();
        if(playActionReciver!=null)
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(playActionReciver);
    }
    public class PlayActionReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bd = intent.getExtras();
            if(bd!=null) {
                int cmd = bd.getInt(PlayerBroadcastReceiver.BUNDLEKEY_PLAYERCMD);
                switch (cmd) {
                    case SystemContent.MEDIAPLAYER_CHANGESONG:
                        Log.d("MEDIAPLAYER_CHANGESONG");
                        if(youtubeAdapter!=null)
                            youtubeAdapter.notifyItem();
                        break;

                }
            }
        }
    }
    public  boolean exists(String URLName){
        try {
            HttpURLConnection.setFollowRedirects(false);

            HttpURLConnection con =  (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateYoutubeList(){
        if(!AndroidUtils.isNetworkConnected(activity)){
            Toast.makeText(activity,"請開啟網路",Toast.LENGTH_SHORT).show();
           return ;
        }
        AsyncTask<Void ,Void,ArrayList<SongItem> > task =  new AsyncTask<Void ,Void,ArrayList<SongItem>>(){

            @Override
            protected ArrayList<SongItem> doInBackground(Void... voids) {
                ArrayList<Youtube> list = YoutubeDao.getInstance(activity).getYoutubes();
                SongItemDao.getInstance(activity).addSongItems(list);
                ArrayList<SongItem> data = SongItemDao.getInstance(activity).getSongList();
                boolean isDeleteItem = false;
                if(data!=null)
                    for(SongItem item :data){
                        if(!exists(item.getUrl())){
                            YoutubeDao.getInstance(activity).deleteYotube(item.getYoutubeId());
                            String url = "https://www.youtube.com/watch?v="+item.getYoutubeId();
                            Youtube youtube = paser.getYoutube(url, item.getYoutubeId());
                            if(youtube!=null) {
                                YoutubeDao.getInstance(activity).addYoutube(youtube);
                                SongItemDao.getInstance(activity).addSongItem(youtube);
                            }
                            isDeleteItem = true;
                        }
                    }
                data = SongItemDao.getInstance(activity).getSongList();
                PlayerManager.getInstance(activity).setSongList(data);
                if(isDeleteItem){
                    AndroidUtils.sendToPlayService(activity, null, PlayService.MEDIAPLAYER_STOP);
                }
                return data;
            }
            YoutubeloadPaser paser;
            ProgressDialog pd;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                paser = new YoutubeloadPaser(activity,null);
                pd = new ProgressDialog(activity);
                pd.setMessage("準備播放清單");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setCanceledOnTouchOutside(false);
                pd.show();
            }

            @Override
            protected void onPostExecute(ArrayList<SongItem> youtubes) {
                super.onPostExecute(youtubes);
                youtubeAdapter.setData(youtubes);
                youtubeAdapter.notifyDataSetChanged();
                try {
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                }catch (Exception e){
                    Log.exception(e);
                }
            }
        };
        task.execute();

    }


    private  class YoutubeAdapter extends RecyclerView.Adapter<MyHolder>{
        Context context;
        int focusPostion = -1;
        public List<SongItem> getData() {
            return data;
        }

        public void setData(List<SongItem> data) {
            this.data = data;
        }

        public void notifyItem(){
            if(data!=null){
                int index = PlayerManager.getInstance(context).getIndex();
                if(index<data.size()){
                    recyclerView.scrollToPosition(index);
                }
                if(focusPostion!=-1)
                    notifyItemChanged(focusPostion);
                notifyItemChanged(index);
            }
        }
        List<SongItem> data;
        public YoutubeAdapter(Context context){
            this.context = context;
        }
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new MyHolder(LayoutInflater.from(context).inflate(R.layout.fragment_youtube_list_item,null));
        }

        @Override
        public int getItemCount() {
            if(data!=null)
                return data.size();
            return 0;
        }
        public SongItem getItem(int position){
              if(data!=null&&position<data.size()){
                  return data.get(position);
              }
            return null;
        }
        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            SongItem item = getItem(position);
               if(item!=null) {
                   ImageManager.getInstance(context).displayImage(holder.iv_thumbnail, "http://img.youtube.com/vi/"+item.getYoutubeId()+"/0.jpg");
                   holder.tv_title.setText(item.getName());
                   holder.setPostion(position);
                   if(PlayerManager.getInstance(context).getCurrentSongItem()!=null
                           &&PlayerManager.getInstance(context).getCurrentSongItem().getYoutubeId().equals(item.getYoutubeId())){
                       focusPostion = position;
                       holder.view.setBackgroundColor(getResources().getColor(R.color.bt_touch_color));
                   }else{
                       holder.view.setBackgroundColor(Color.WHITE);
                   }
               }
        }



    }
    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                                                                            View.OnLongClickListener{
        TextView tv_title;
        ImageView iv_thumbnail;
        View view;
        CardView cardView;
        int postion = -1;

        public MyHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.textView2);
            iv_thumbnail = (ImageView) itemView.findViewById(R.id.imageView2);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            view = itemView;
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        public int getPostion() {
            return postion;
        }

        public void setPostion(int postion) {
            this.postion = postion;
        }

        @Override
        public void onClick(View view) {
            if(PlayerManager.getInstance(activity).setSong(postion))
                AndroidUtils.sendToPlayService(activity, null, PlayService.MEDIAPLAYER_CHANGESONG);
        }
        @Override
        public boolean onLongClick(View view) {
            new AlertDialog.Builder(activity)
                    .setSingleChoiceItems(new String[]{
                            "開啟網頁",
                            "下載",
                            "刪除",
                    }, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (youtubeAdapter == null)
                                return;
                            SongItem item;
                            item = youtubeAdapter.getItem(getPostion());
                            Intent it;
                            if (item != null)
                            switch (i){
                                    case 0:
                                         it = new Intent(Intent.ACTION_VIEW);
                                        String url = "https://www.youtube.com/watch?v="+item.getYoutubeId();
                                        it.setData(Uri.parse(url));
                                        Log.d("open url :"+it.getData());
                                        activity.startActivity(it);
                                        break;
                                    case 1:
                                         it = new Intent(activity, DownLoadActivity.class);
                                         it.putExtra("youtube","https://www.youtube.com/watch?v="+item.getYoutubeId());
                                         activity.startActivity(it);
                                        break;
                                    case 2:
                                        AndroidUtils.sendToPlayService(activity, null, PlayService.MEDIAPLAYER_STOP);
                                        YoutubeDao.getInstance(activity).deleteYotube(item.getYoutubeId());
                                        updateYoutubeList();
                                        break;
                                }
                            dialogInterface.cancel();
                        }
                    })
                    .setNegativeButton("OK", null).create().show();

            return true;

        }

    }

}
