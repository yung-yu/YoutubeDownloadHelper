package youtubedownloadhelper.manager

import android.content.Context
import android.widget.ImageView

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType

import youtubedownloadhelper.R

/**
 * Created by andyli on 2015/7/27.
 */
class ImageManager(context: Context) {
	internal var imageLoader: ImageLoader

	init {
		imageLoader = ImageLoader.getInstance()
		val config = ImageLoaderConfiguration.Builder(context)
				.memoryCacheExtraOptions(480, 800)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(LRULimitedMemoryCache(50 * 1024 * 1024))
				.memoryCacheSize(50 * 1024 * 1024)
				.diskCacheFileNameGenerator(Md5FileNameGenerator())
				.diskCacheSize(200 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.diskCacheFileCount(200)
				.build()
		imageLoader.init(ImageLoaderConfiguration.createDefault(context))
	}

	fun displayImage(iv: ImageView, url: String, defautIcon: Int) {
		imageLoader.displayImage(url, iv)

	}

	companion object {
		internal var imageManager: ImageManager? = null

		fun getInstance(context: Context): ImageManager {
			if (imageManager == null) {
				imageManager = ImageManager(context)

			}
			return imageManager as ImageManager
		}
	}
}
