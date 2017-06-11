package youtubedownloadhelper.manager;

import android.content.Context;
import android.widget.ImageView;

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
                .memoryCacheExtraOptions(480, 800)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LRULimitedMemoryCache(50 * 1024 * 1024))
                .memoryCacheSize(50 * 1024 * 1024)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(200 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheFileCount(200)
                .build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    public static ImageManager getInstance(Context context){
           if(imageManager==null){
               imageManager = new ImageManager(context);

           }
        return imageManager;
    }

    public void displayImage(ImageView iv,String url,int defautIcon){
        imageLoader.displayImage(url, iv);

    }
}
