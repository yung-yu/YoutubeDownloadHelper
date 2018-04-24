package youtubedownloadhelper.download.adapter


import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import java.util.HashMap
import youtubedownloadhelper.R
import youtubedownloadhelper.`object`.Video
import youtubedownloadhelper.download.DownloadTask
import youtubedownloadhelper.download.adapter.viewholder.MyViewHolder
import youtubedownloadhelper.youtube.YotubeItag

class DownloadAdapter(private val context: Context) : BaseAdapter() {
	private var list: List<Video>? = null

	private val taskCache = HashMap<Int, DownloadTask?>()

	fun setList(list: List<Video>) {
		this.list = list
	}

	override fun getCount(): Int {
		return if (list != null) list!!.size else 0
	}

	override fun getItem(position: Int): Video? {
		return if (list != null && position < list!!.size) list!![position] else null
	}

	override fun getItemId(position: Int): Long {
		return 0
	}

	fun putDownloadCache(position: Int, task: DownloadTask) {
		taskCache[position] = task
	}

	fun removeDownloadCache(positon: Int) {
		taskCache[positon] = null
	}

	fun isDownloading(position: Int): Boolean {
		return taskCache[position] != null && !taskCache[position]!!.isCancelled()
	}

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		var convertView = convertView
		val holder: MyViewHolder?
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.downloaditem, null)
			holder = MyViewHolder(convertView!!)
			convertView.tag = holder
		} else {
			holder = convertView.tag as MyViewHolder
		}
		val video = getItem(position)
		if (holder != null && video != null) {
			holder.video = video

			val task = taskCache[position]
			if (task != null && !task.isCancelled) {
				holder.bt.visibility = View.VISIBLE

				holder.bt.tag = task
				holder.bt.setOnClickListener { v ->
					val task = v.tag as DownloadTask
					if (task != null && !task.isCancelled) {
						task.cancel(true)
						taskCache.remove(position)
						notifyDataSetChanged()
					}
				}
				holder.progressBar.visibility = View.VISIBLE
				holder.progressBar.progress = task.curProgress
				holder.text.text = (YotubeItag.getVideoDescribe(video.itag)
						+ "-(" + task.curProgress + "/100)")
			} else {
				holder.progressBar.visibility = View.GONE
				holder.bt.visibility = View.GONE
				holder.text.text = YotubeItag.getVideoDescribe(video.itag)!! + if (video.isDownlaod) "-播放" else ""
			}

		}
		return convertView
	}
}
