package youtubedownloadhelper.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

/**
 * Created by andyli on 2017/6/11.
 */

public class AndroidUtils {

	public static void playVideo(Context context, String filePath) {
		try {
			mediaScan(context, filePath);
			Uri contentUri = Uri.parse(filePath);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(contentUri, "video/mp4");
			context.startActivity(intent);
		}catch (ActivityNotFoundException e){
			e.printStackTrace();
		}
	}

	public static void playMusic(Context context, String filePath) {
		try {
			Log.d("playMusic",filePath);
			mediaScan(context, filePath);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri data = Uri.parse(filePath.startsWith("file://")?filePath:"file://"+filePath);
			intent.setDataAndType(data,"audio/*");
			context.startActivity(intent);
		}catch (ActivityNotFoundException e){
			e.printStackTrace();
		}
	}

	public static void mediaScan(Context context, String path){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			final Uri contentUri = Uri.parse(path);
			scanIntent.setData(contentUri);
			context.sendBroadcast(scanIntent);
		} else {
			final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
			context.sendBroadcast(intent);
		}
	}
}
