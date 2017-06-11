package youtubedownloadhelper.download.adapter.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import youtubedownloadhelper.R;
import youtubedownloadhelper.object.Video;

/**
 * Created by andyli on 2017/6/11.
 */
public class MyViewHolder {
	public Button bt;
	public ImageView mp3Button;
	public ProgressBar progressBar;
	public Video video;
	public TextView text;

	public MyViewHolder(View itemView) {
		bt = (Button) itemView.findViewById(R.id.button);
		progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
		text = (TextView) itemView.findViewById(R.id.text);
		mp3Button = (ImageView) itemView.findViewById(R.id.mp3button);
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}
}
