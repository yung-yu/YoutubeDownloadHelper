package youtubedownloadhelper.download

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast


import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

import youtubedownloadhelper.R
import youtubedownloadhelper.`object`.Video
import youtubedownloadhelper.`object`.Youtube
import youtubedownloadhelper.download.adapter.DownloadAdapter
import youtubedownloadhelper.utils.AndroidUtils
import youtubedownloadhelper.youtube.YotubeItag
import youtubedownloadhelper.youtube.YoutubePaserTask

/**
 * Created by andy on 2015/3/8.
 */
class DownLoadActivity : Activity(), AdapterView.OnItemClickListener {
	private var tv_title: TextView? = null
	private var iv_title: ImageView? = null
	private var context: Context? = null
	private var curYoutube: Youtube? = null
	private var listView: ListView? = null
	private var downloadAdapter: DownloadAdapter? = null
	private var download_page: LinearLayout? = null
	private var loading_Page: RelativeLayout? = null

	private var curVideoId: String? = null
	private var reload: ImageButton? = null


	private val handler = object : Handler(Looper.getMainLooper()) {
		override fun handleMessage(msg: Message) {
			super.handleMessage(msg)
			when (msg.what) {
				NOTFTY_ADAPTER -> if (downloadAdapter != null) {
					downloadAdapter!!.notifyDataSetChanged()
				}
				DOWNLOAD_FINISH -> if (downloadAdapter != null) {
					val video = downloadAdapter!!.getItem(msg.arg1)
					downloadAdapter!!.removeDownloadCache(msg.arg1)
					downloadAdapter!!.notifyDataSetChanged()


				}
			}
		}
	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		setContentView(R.layout.activity_download)
		context = this
		tv_title = findViewById<View>(R.id.textView) as TextView
		iv_title = findViewById<View>(R.id.imageView) as ImageView
		reload = findViewById<View>(R.id.reload) as ImageButton
		listView = findViewById<View>(R.id.listview) as ListView
		download_page = findViewById<View>(R.id.download_page) as LinearLayout
		loading_Page = findViewById<View>(R.id.loading_Page) as RelativeLayout
		findViewById<View>(R.id.button4).setOnClickListener { this@DownLoadActivity.finish() }
		reload!!.isEnabled = false
		reload!!.setOnClickListener {
			if (!TextUtils.isEmpty(curVideoId)) {
				reload!!.isEnabled = false
				youtubeParser(curVideoId)
			}
		}
		downloadAdapter = DownloadAdapter(this)
		listView!!.adapter = downloadAdapter
		listView!!.onItemClickListener = this
		val intent = intent
		val action = intent.action
		val type = intent.type
		val bd = intent.extras
		if (type != null && action != null && type == "text/plain" && Intent.ACTION_SEND == action) {
			curVideoId = intent.getStringExtra(Intent.EXTRA_TEXT)
			youtubeParser(curVideoId)
		} else if (bd != null) {
			if (bd.containsKey(BUNDLE_KEY_YOUTUBE_ID)) {
				curVideoId = intent.getStringExtra(Intent.EXTRA_TEXT)
				youtubeParser(curVideoId)
			}
		}
	}

	fun youtubeParser(sharedText: String?) {
		if (!TextUtils.isEmpty(sharedText)) {
			download_page!!.visibility = View.GONE
			loading_Page!!.visibility = View.VISIBLE

			YoutubePaserTask(this, object : YoutubePaserTask.CallBack {
				override fun success(youtube: Youtube) {
					download_page!!.visibility = View.VISIBLE
					loading_Page!!.visibility = View.GONE
					curYoutube = youtube
					tv_title!!.text = curYoutube!!.title
					displayImageUrl(iv_title, curYoutube!!.imgeUrl)
					showDownloadList(curYoutube!!.videoList)
					reload!!.isEnabled = true
				}

				override fun onfail(Message: String) {
					Toast.makeText(this@DownLoadActivity, Message, Toast.LENGTH_SHORT).show()
					finish()
				}
			}).execute(sharedText)


		}
	}


	private fun displayImageUrl(iv: ImageView?, imageUrl: String?) {
		Thread(Runnable {
			try {
				val url = URL(imageUrl)
				val connection = url.openConnection() as HttpURLConnection
				connection.doInput = true
				connection.connect()
				val input = connection.inputStream
				val bitmap = BitmapFactory.decodeStream(input)
				runOnUiThread { iv!!.setImageBitmap(bitmap) }
			} catch (e: IOException) {
				e.printStackTrace()
				return@Runnable
			}
		}).start()

	}

	private fun showDownloadList(list: ArrayList<Video>?) {
		if (list == null || list.size == 0) {
			Toast.makeText(this@DownLoadActivity, "找不到影片", Toast.LENGTH_SHORT).show()
			finish()
			return
		}
		downloadAdapter!!.setList(list)
		downloadAdapter!!.notifyDataSetChanged()
	}

	override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
		Log.d(TAG, "onItemClick $position")
		if (!downloadAdapter!!.isDownloading(position)) {
			val video = downloadAdapter!!.getItem(position)
			if (video!!.isDownlaod) {
				AndroidUtils.playVideo(this, video.localFilePath!!)
			} else {
				downloadVideo(position, video)
			}
		}
	}

	public override fun onStart() {
		super.onStart()
	}

	public override fun onStop() {
		super.onStop()

	}

	fun downloadVideo(position: Int, video: Video) {
		var fileName = curYoutube!!.title!!.trim { it <= ' ' }
		fileName = if (fileName.length > 15) fileName.substring(0, 15) else fileName
		fileName = (fileName + "_" + YotubeItag.getVideoDescribe(video.itag)
				+ "." + YotubeItag.getVideoType(video.itag))
		val task = DownloadTask(this@DownLoadActivity, handler, position)
		downloadAdapter!!.putDownloadCache(position, task)
		task.execute(video, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath, fileName)
	}

	companion object {
		private val TAG = "DownLoadActivity"
		val BUNDLE_KEY_YOUTUBE_ID = "BUNDLE_KEY_YOUTUBE_ID"
		val NOTFTY_ADAPTER = 0
		val DOWNLOAD_FINISH = 1
	}


}
