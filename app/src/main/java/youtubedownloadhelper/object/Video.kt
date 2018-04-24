package youtubedownloadhelper.`object`


import android.database.Cursor
import android.text.TextUtils

import java.io.File


/**
 * Created by andyli on 2015/7/25.
 */
class Video {

	var id: Int = 0

	var youtubeId: String? = null

	var videoUrl: String? = null

	var itag: Int = 0

	var localFilePath: String? = null

	var lastUpdateDate: Long = 0

	val isDownlaod: Boolean
		get() {
			if (!TextUtils.isEmpty(localFilePath)) {
				val file = File(localFilePath!!)
				return file.exists()
			}
			return false
		}


	constructor() {

	}

	constructor(cursor: Cursor) {
		this.id = cursor.getInt(0)
		this.youtubeId = cursor.getString(1)
		this.videoUrl = cursor.getString(2)
		this.itag = cursor.getInt(3)
		this.localFilePath = cursor.getString(4)
		this.lastUpdateDate = cursor.getLong(5)
	}
}
