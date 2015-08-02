package andy.youtubedownloadhelper.com.youtubedownloadhelper.utils;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by andyli on 2015/7/27.
 */
public class ImageManager {
    static ImageManager imageManager;
    ImageLoader imageLoader;
    public ImageManager() {
        imageLoader = ImageLoader.getInstance();
    }

    public static ImageManager getInstance(){
           if(imageManager==null){
               imageManager = new ImageManager();
           }
        return imageManager;
    }

    public void displayImage(ImageView iv,String url){
        imageLoader.displayImage(url,iv);
    }
}
