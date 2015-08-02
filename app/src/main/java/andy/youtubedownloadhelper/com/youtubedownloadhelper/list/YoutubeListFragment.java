package andy.youtubedownloadhelper.com.youtubedownloadhelper.list;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import andy.youtubedownloadhelper.com.youtubedownloadhelper.R;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbDao.YoutubeDao;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.dbinfo.Youtube;
import andy.youtubedownloadhelper.com.youtubedownloadhelper.utils.ImageManager;

/**
 * Created by andyli on 2015/7/25.
 */
public class YoutubeListFragment extends Fragment {
    RecyclerView recyclerView;
    YoutubeAdapter youtubeAdapter;
    Activity activity;

    @Override
    public void onAttach(Activity activity) {
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
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateYoutubeList();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void updateYoutubeList(){
        if(youtubeAdapter==null){
            youtubeAdapter = new YoutubeAdapter(activity);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(youtubeAdapter);
        }
        youtubeAdapter.notifyDataSetChanged();
    }


    private  class YoutubeAdapter extends RecyclerView.Adapter<MyHolder>{
        Context context;

        public List<Youtube> getData() {
            return data;
        }

        public void setData(List<Youtube> data) {
            this.data = data;
        }

        List<Youtube> data;
        public YoutubeAdapter(Context context){
            this.context = context;
        }
        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new MyHolder(LayoutInflater.from(context).inflate(R.layout.fragment_youtube_list_item,null));
        }

        @Override
        public int getItemCount() {
//            if(data!=null)
//                return data.size();
            return 10;
        }
        public Youtube getItem(int position){
              if(data!=null&&position<data.size()){
                  return data.get(position);
              }
            return null;
        }
        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
               Youtube item = getItem(position);
               if(item!=null){
                   ImageManager.getInstance().displayImage(holder.iv_thumbnail,item.getImgeUrl());
                   holder.tv_title.setText(item.getTitle());
               }
        }



    }
    public class MyHolder extends RecyclerView.ViewHolder{
        TextView tv_title;
        ImageView iv_thumbnail;
        public MyHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.textView2);
            iv_thumbnail = (ImageView) itemView.findViewById(R.id.imageView2);
        }
    }
}
