package youtubedownloadhelper.download.adapter.viewholder

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import youtubedownloadhelper.R
import youtubedownloadhelper.`object`.Video

/**
 * Created by andyli on 2017/6/11.
 */
class MyViewHolder(itemView: View) {
	lateinit var bt: Button
	lateinit var progressBar: ProgressBar
	lateinit var video: Video
	lateinit var text: TextView

	init {
		bt = itemView.findViewById<View>(R.id.button) as Button
		progressBar = itemView.findViewById<View>(R.id.progressBar) as ProgressBar
		text = itemView.findViewById<View>(R.id.text) as TextView
	}
}
