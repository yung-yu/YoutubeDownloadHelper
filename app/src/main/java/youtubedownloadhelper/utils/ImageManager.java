package youtubedownloadhelper.utils;

import android.content.Context;
import android.widget.ImageView;

import andy.spiderlibrary.utils.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import youtubedownloadhelper.R;

/**
 * Created by andyli on 2015/7/27.
 */
public class ImageManager {
    static ImageManager imageManager;
    ImageLoader imageLoader;
    public ImageManager(Context context) {
        imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) //即保存的每个缓存文件的最大长宽
                .threadPriority(Thread.NORM_PRIORITY - 2) //线程池中线程的个数
                .denyCacheImageMultipleSizesInMemory() //禁止缓存多张图片
                .memoryCache(new LRULimitedMemoryCache(50 * 1024 * 1024)) //缓存策略
                .memoryCacheSize(50 * 1024 * 1024) //设置内存缓存的大小
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //缓存文件名的保存方式
                .diskCacheSize(200 * 1024 * 1024) //磁盘缓存大小
                .tasksProcessingOrder(QueueProcessingType.LIFO) //工作队列
                .diskCacheFileCount(200) //缓存的文件数量
                .build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    public static ImageManager getInstance(Context context){
           if(imageManager==null){
               imageManager = new ImageManager(context);

           }
        return imageManager;
    }

    public void displayImage(ImageView iv,String url){
        displayImage(iv,url,R.drawable.youtube35);

    }
    public void displayImage(ImageView iv,String url,int defautIcon){
        Log.d("image url :" + url);
        imageLoader.displayImage(url, iv);

    }
}
