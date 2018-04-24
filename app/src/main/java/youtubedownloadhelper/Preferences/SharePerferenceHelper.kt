package youtubedownloadhelper.Preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor


class SharePerferenceHelper(mContext: Context) {
	val editor: Editor?
		get() = if (mSharedPreferences != null) mSharedPreferences!!.edit() else null

	val isFirstIntstall: Boolean
		get() {
			if (getboolean(FIRST_INSTALL, true)) {
				setboolean(FIRST_INSTALL, false)
				return true
			}
			return false
		}

	init {
		if (mSharedPreferences == null)
			mSharedPreferences = mContext.getSharedPreferences("youtube", Context.MODE_PRIVATE)
	}

	@SuppressLint("CommitPrefEdits")
	fun clear() {
		val mEditor = editor
		if (mEditor != null) {
			mEditor.clear()
			mEditor.commit()
		}
	}

	fun getString(key: String, def: String): String? {
		return if (mSharedPreferences != null) mSharedPreferences!!.getString(key, def) else def
	}

	fun setString(key: String, value: String): Boolean {
		val mEditor = editor
		if (mEditor != null) {
			mEditor.putString(key, value)
			mEditor.commit()
			return true
		}
		return false
	}

	fun getInt(key: String, def: Int): Int {
		return if (mSharedPreferences != null) mSharedPreferences!!.getInt(key, def) else def
	}

	fun setInt(key: String, value: Int): Boolean {
		val mEditor = editor
		if (mEditor != null) {
			mEditor.putInt(key, value)
			mEditor.commit()
			return true
		}
		return false
	}

	fun getboolean(key: String, def: Boolean): Boolean {
		return if (mSharedPreferences != null) {
			mSharedPreferences!!.getBoolean(key, def)
		} else def
	}

	fun setboolean(key: String, value: Boolean): Boolean {
		val mEditor = editor
		if (mEditor != null) {
			mEditor.putBoolean(key, value)
			mEditor.commit()
			return true
		}
		return false
	}

	companion object {
		private val FIRST_INSTALL = "FIRST_INSTALL"
		internal var mSharedPreferences: SharedPreferences? = null
		internal lateinit var instance: SharePerferenceHelper
		fun getInstance(mContext: Context): SharePerferenceHelper {
			if (mSharedPreferences == null)
				instance = SharePerferenceHelper(mContext)
			return instance
		}
	}

}
