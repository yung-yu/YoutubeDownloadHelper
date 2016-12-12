package youtubedownloadhelper.mp3;

import android.content.Context;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;


/**
 * Created by andyli on 2016/12/11.
 */

public class Mp3Helper {
	private final static String TAG = "Mp3Helper";
	static Mp3Helper instance;

	public static  Mp3Helper getInstance(){
		if(instance == null){
			instance = new Mp3Helper();
		}
		return  instance;
	}
    public interface OnCovertListener{
		 void onStart();
		 void onSuccess();
		 void onFailed();
		 void onProgress(String msg);
	}

	public Mp3Helper() {

	}

	public void init(Context context){
		FFmpeg ffmpeg = FFmpeg.getInstance(context);
		try {
			ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

				@Override
				public void onStart() {}

				@Override
				public void onFailure() {}

				@Override
				public void onSuccess() {}

				@Override
				public void onFinish() {}
			});
		} catch (FFmpegNotSupportedException e) {
			// Handle if FFmpeg is not supported by device
			Log.e(TAG, e.toString());
		}

	}



	public FFmpeg covertToMp3(Context context, String  source, String target, final OnCovertListener listener){
		String[] cmd = {
		 "-i",
	     source,
		 target
		};
		FFmpeg ffmpeg = FFmpeg.getInstance(context);
		try {
			// to execute "ffmpeg -version" command you just need to pass "-version"
			ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

				@Override
				public void onStart() {
					if(listener != null){
						listener.onStart();
					}
				}

				@Override
				public void onProgress(String message) {
					Log.e(TAG, message);
					if(listener != null){
						listener.onProgress(message);
					}
				}

				@Override
				public void onFailure(String message) {
					Log.e(TAG, message);
					if(listener != null){
						listener.onFailed();
					}

				}

				@Override
				public void onSuccess(String message) {
					Log.e(TAG, message);
					if(listener != null){
						listener.onSuccess();
					}
				}

				@Override
				public void onFinish() {}
			});
			return  ffmpeg;
		} catch (FFmpegCommandAlreadyRunningException e) {
			// Handle if FFmpeg is already running
		}
		return  null;
	}

}
