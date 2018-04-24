package youtubedownloadhelper.download

/**
 * Created by andy on 2015/2/28.
 */
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.net.URLConnection

import android.content.Context
import android.os.AsyncTask
import android.os.Handler
import android.widget.Toast

import youtubedownloadhelper.R

import youtubedownloadhelper.`object`.Video

class DownloadTask(c: Context, private val handler: Handler?, private val position: Int) : AsyncTask<Any, Int, Int>() {
	private var context: Context? = null
	val DOWNLOAD_SUCCESS = 0
	val DOWNLOAD_FAIL = 1
	var curProgress = 0

	init {
		this.context = c

	}

	override fun onPreExecute() {
		super.onPreExecute()
		handler?.sendEmptyMessage(DownLoadActivity.NOTFTY_ADAPTER)

	}


	override fun doInBackground(vararg params: Any): Int? {
		var count: Int
		var filePath = ""
		val video = params[0] as Video
		try {
			val url = URL(video.videoUrl)
			val conection = url.openConnection()
			conection.connect()
			val lenghtOfFile = conection.contentLength

			val input = BufferedInputStream(url.openStream(), 8192)

			val file = File(params[1] as String, params[2] as String)
			if (file.exists() && file.length() == lenghtOfFile.toLong()) {
				video.localFilePath = filePath
				return DOWNLOAD_SUCCESS
			} else if (file.exists()) {
				file.delete()
			}
			file.createNewFile()
			filePath = file.absolutePath

			val output = FileOutputStream(file.absolutePath)
			val data = ByteArray(1024)
			var total: Long = 0
			onProgressUpdate(0)
			count = input.read(data)
			while (count != -1&& !isCancelled) {
				total += count.toLong()

				onProgressUpdate((total * 100 / lenghtOfFile).toInt())

				output.write(data, 0, count)
				count = input.read(data)
			}
			output.flush()
			output.close()
			input.close()
		} catch (e: IOException) {
			val file = File(filePath)
			if (file.exists()) {
				file.delete()
			}
			return DOWNLOAD_FAIL
		} finally {
			if (isCancelled) {
				val file = File(params[1] as String, params[2] as String)
				if (file.exists()) {
					file.delete()
				}
			} else {
				video.localFilePath = filePath
			}
		}

		return DOWNLOAD_SUCCESS
	}


	 override fun onProgressUpdate(vararg values: Int?) {
		super.onProgressUpdate(*values)
		if (!this.curProgress.equals(values.get(0))) {
			curProgress = values.get(0)!!
			handler?.sendEmptyMessage(DownLoadActivity.NOTFTY_ADAPTER)
		}

	}

	override fun onPostExecute(s: Int?) {
		super.onPostExecute(s)
		handler?.obtainMessage(DownLoadActivity.DOWNLOAD_FINISH, position, 0)?.sendToTarget()
		if (s == DOWNLOAD_SUCCESS) {
			Toast.makeText(context, context!!.getString(R.string.download_success), Toast.LENGTH_SHORT).show()
		} else {
			Toast.makeText(context, context!!.getString(R.string.download_fail), Toast.LENGTH_SHORT).show()
		}

	}

	companion object {
		private val TAG = "DownloadTask"
	}
}
