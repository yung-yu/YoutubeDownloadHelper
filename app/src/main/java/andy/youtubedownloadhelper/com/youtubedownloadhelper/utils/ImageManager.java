package andy.youtubedownloadhelper.com.youtubedownloadhelper.utils;

import android.content.Context;
import android.widget.ImageView;

import com.andylibrary.utils.Log;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by andyli on 2015/7/27.
 */
public class ImageManager {
    static ImageManager imageManager;
    ImageLoader imageLoader;
    public ImageManager(Context context) {
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    public static ImageManager getInstance(Context context){
           if(imageManager==null){
               imageManager = new ImageManager(context);

           }
        return imageManager;
    }

    public void displayImage(ImageView iv,String url){
        Log.d("image url :" + url);
        imageLoader.displayImage(url,iv);
    }
}
