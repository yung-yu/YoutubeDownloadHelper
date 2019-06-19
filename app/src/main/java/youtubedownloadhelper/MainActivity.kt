package youtubedownloadhelper


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


import youtubedownloadhelper.download.DownLoadActivity


class MainActivity : AppCompatActivity(), View.OnClickListener {

	internal lateinit var editText: EditText
	internal lateinit var download: Button
	internal lateinit var uninstall: Button



	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		editText = findViewById(R.id.editText) as EditText
		download = findViewById(R.id.download) as Button
		uninstall = findViewById(R.id.uninstall) as Button
		download.setOnClickListener(this)
		uninstall.setOnClickListener(this)
		editText.setText("jiZOPmvUlY0")
	}

	override fun onResume() {
		super.onResume()
	}


	override fun onDestroy() {
		super.onDestroy()

	}

	override fun onClick(v: View) {
		if (v.id == R.id.download) {
			download()
		} else if (v.id == R.id.uninstall) {
			unistall()
		}
	}

	private fun unistall() {
		val packageUri = Uri.parse("package:" + this.getPackageName())
		val it = Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri)
		startActivity(it)
	}

	private fun download() {
		val youtubeId = editText.text.toString()
		if (!TextUtils.isEmpty(youtubeId)) {
			val it = Intent()
			it.putExtra(DownLoadActivity.BUNDLE_KEY_YOUTUBE_ID, youtubeId)
			it.setClass(this, DownLoadActivity::class.java!!)
			startActivity(it)
		} else {
			Toast.makeText(this, R.string.pls_input_legal_videoId, Toast.LENGTH_SHORT).show()
		}
	}

}
