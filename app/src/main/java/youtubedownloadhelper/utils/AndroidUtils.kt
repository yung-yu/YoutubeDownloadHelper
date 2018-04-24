package youtubedownloadhelper.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log

/**
 * Created by andyli on 2017/6/11.
 */

object AndroidUtils {

	fun playVideo(context: Context, filePath: String) {
		try {
			mediaScan(context, filePath)
			val contentUri = Uri.parse(filePath)
			val intent = Intent(Intent.ACTION_VIEW)
			intent.setDataAndType(contentUri, "video/mp4")
			context.startActivity(intent)
		} catch (e: ActivityNotFoundException) {
			e.printStackTrace()
		}

	}

	fun playMusic(context: Context, filePath: String) {
		try {
			Log.d("playMusic", filePath)
			mediaScan(context, filePath)
			val intent = Intent(Intent.ACTION_VIEW)
			val data = Uri.parse(if (filePath.startsWith("file://")) filePath else "file://$filePath")
			intent.setDataAndType(data, "audio/*")
			context.startActivity(intent)
		} catch (e: ActivityNotFoundException) {
			e.printStackTrace()
		}

	}

	fun mediaScan(context: Context, path: String) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
			val contentUri = Uri.parse(path)
			scanIntent.data = contentUri
			context.sendBroadcast(scanIntent)
		} else {
			val intent = Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()))
			context.sendBroadcast(intent)
		}
	}
}
