package youtubedownloadhelper.youtube

import android.content.Context
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log


import org.jsoup.Jsoup


import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.ArrayList

import youtubedownloadhelper.`object`.Video
import youtubedownloadhelper.`object`.Youtube

/**
 * Created by andy on 2015/3/8.
 */
class YoutubePaserTask(internal var context: Context, internal var callBack: CallBack?) : AsyncTask<String, String, Youtube>() {

	interface CallBack {
		fun success(youtube: Youtube)
		fun onfail(Message: String)
	}

	override fun onPreExecute() {
		super.onPreExecute()
	}

	override fun onPostExecute(youtube: Youtube?) {
		super.onPostExecute(youtube)
		if (callBack != null) {
			if (youtube != null) {
				callBack!!.success(youtube)
			} else {
				callBack!!.onfail("無效 URL")
			}

		}
	}

	override fun onProgressUpdate(vararg values: String) {
		super.onProgressUpdate(*values)
	}

	override fun doInBackground(vararg params: String): Youtube? {
		val reference = params[0]

		val youtubeId = getVideoId(reference)
		Log.d(TAG, "youtube id $youtubeId")
		return if (TextUtils.isEmpty(youtubeId)) null else getYoutube(reference, youtubeId)

	}

	fun getYoutube(reference: String, youtubeId: String): Youtube? {
		val url = VIDEO_INFO_URL + youtubeId + VIDEO_INFO_PARM
		val youtube = Youtube()
		youtube.youtubeUrl = reference
		youtube.youtubeId = youtubeId
		Log.d(TAG, "url $url")
		try {
			val mUrl = URL(url)
			val connection = mUrl.openConnection() as HttpURLConnection
			connection.readTimeout = 15000
			connection.connectTimeout = 15000
			connection.requestMethod = "GET"
			connection.doInput = true
			connection.doOutput = true
			connection.connect()

			val response = ""
			Log.d(TAG, "$connection.responseCode")
			if (connection.responseCode == 200) {

				val doc = Jsoup.parse(connection.inputStream, "utf8", url)
				Log.d(TAG, "rep ${doc.text()}")
				val Parms = doc.text().split("&".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
				val url_encoded_fmt_stream_map: String? = null
				val urls = ArrayList<Video>()
				var title: String? = null
				var thumbnail_url: String? = null
				for (p in Parms) {
					val key = p.substring(0, p.indexOf('='))
					var value: String? = p.substring(p.indexOf('=') + 1)
					if (key == "url_encoded_fmt_stream_map") {
						value = decode(value)
						for (url in value!!.split("url=".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()) {
							val video = Video()
							val decodeU = decode(url)
							val videoUrl = getCorrectURL(decodeU!!)
							Log.d(TAG, " video $videoUrl")
							if (!(videoUrl.toLowerCase().startsWith("http") && videoUrl.toLowerCase().startsWith("https"))&& !isValid(videoUrl)) {
								continue
							}

							video.youtubeId = youtubeId
							video.videoUrl = videoUrl
							val itag = getItag(videoUrl)
							val type = YotubeItag.getVideoType(itag)
							val videoType = YotubeItag.getVideoDescribe(itag)
							video.itag = itag
							if (type != null && videoType != null &&
									!(videoUrl.isEmpty() || type.isEmpty() || videoType.isEmpty() || itag.equals("-1"))) {
								urls.add(video)
							}

						}
					} else if (key == "title") {
						title = value!!.replace("+", "%20")
						title = decode(title)
					}
				}
				youtube.title = title
				thumbnail_url = "https://img.youtube.com/vi/$youtubeId/0.jpg"
				youtube.imgeUrl = thumbnail_url
				if (urls.size == 0) {
					return null
				}
				youtube.videoList = urls
				return youtube
			}
		} catch (e: IOException) {
			e.printStackTrace()
		}

		return null
	}

	fun isValid(url: String): Boolean {
		return url.contains("signature=") && url.contains("factor=")
	}

	private fun decode(s: String?): String? {
		try {
			return URLDecoder.decode(s, "utf8")
		} catch (e: UnsupportedEncodingException) {
			e.printStackTrace()
		}

		return s
	}

	private fun getCorrectURL(input: String): String {
		val builder = StringBuilder(input.substring(0, input.indexOf('?') + 1))
		val params = input.substring(input.indexOf('?') + 1).split("&".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
		val keys = ArrayList<String>()
		var first = true
		for ( param in params) {
			var key = param
			try {
				key = param.substring(0, param.indexOf('='))
			} catch (ex: Exception) {
				println(param)
			}

			if (keys.contains(key) || BAD_KEYS.contains(key)) {
				continue
			}
			keys.add(key)
			if (key == "sig") {
				builder.append(if (first) "" else "&").append("signature=").append(param.substring(4))
			} else {
				var parmTmp:String = param;
				if (parmTmp.contains(",quality=")) {
					parmTmp = remove(param, ",quality=", "_end_")
				}
				if (parmTmp.contains(",type=")) {
					parmTmp = remove(param, ",type=", "_end_")
				}
				if (parmTmp.contains(",fallback_host")) {
					parmTmp = remove(param, ",fallback_host", ".com")
				}
				builder.append(if (first) "" else "&").append(parmTmp)
			}
			if (first)
				first = false
		}
		return builder.toString()
	}

	private fun remove(text: String, start: String, end: String): String {
		val l = text.indexOf(start)
		return text.replace(text.substring(l, if (end == "_end_") text.length else text.indexOf(end, l)), "")
	}

	private fun getItag(url: String): Int {
		val parms = url.split("&".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
		for (p in parms) {
			val key = p.substring(0, p.indexOf('='))
			val value = p.substring(p.indexOf('=') + 1)
			if (key == "itag") {
				try {
					return Integer.valueOf(value)
				} catch (e: NumberFormatException) {

					break
				}

			}
		}
		return -1
	}

	companion object {
		private val TAG = "YoutubePaserTask"
		val VIDEO_INFO_URL = "https://www.youtube.com/get_video_info?video_id="
		val VIDEO_INFO_PARM = "&=3&el=detailpage&hl=en_US"
		val BAD_KEYS = ArrayList<String>()

		init {
			BAD_KEYS.add("stereo3d")
			BAD_KEYS.add("type")
			BAD_KEYS.add("fallback_host")
			BAD_KEYS.add("quality")

		}

		fun getVideoId(reference: String): String {
			var videoId: String? = null

			if (reference.startsWith("https://www.youtube.com/watch?v=")) {
				videoId = reference.substring("https://www.youtube.com/watch?v=".length)
			} else if (reference.startsWith("https://m.youtube.com/watch?v=")) {
				videoId = reference.substring("https://m.youtube.com/watch?v=".length)
			} else if (reference.contains("http://youtu.be/")) {
				videoId = reference.substring(reference.lastIndexOf("/") + 1)
			} else if (reference.contains("https://youtu.be/")) {
				videoId = reference.substring(reference.lastIndexOf("/") + 1)
			} else if (!TextUtils.isEmpty(videoId) && videoId!!.contains("&")) {
				videoId = videoId.substring(0, videoId.indexOf("&"))
			} else {
				videoId = reference
			}

			return videoId
		}
	}


}
