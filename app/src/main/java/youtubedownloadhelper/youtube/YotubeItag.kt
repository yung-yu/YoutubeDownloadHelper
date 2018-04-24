package youtubedownloadhelper.youtube

/**
 * Created by andyli on 2015/8/2.
 */
object YotubeItag {
	//    5 FLV 320 x 240
	//    17 3GP 176 x 144
	//    18 MP4 480 x 360
	//    22 MP4 1280 x 720
	//    34 FLV 480 x 360
	//    35 FLV 640 x 480
	//    36 3GP 320 x 240
	//    37 MP4 1920 x 1080
	//    38 MP4 2048 x 1080
	//    43 WEB 480 x 360
	//    44 WEB 640 x 480
	//    45 WEB 1280 x 720
	//    46 WEB 1920 x 1080
	//    82 MP4 480 x 360 3D
	//    83 MP4 640 x 480 3D
	//    84 MP4 1280 x 720 3D
	//    85 MP4 1920 x 1080 3D
	//    100 WEB 480 x 360 3D
	//    101 WEB 640 x 480 3D
	//    102 WEB 1280 x 720 3D
	//    133 MP4 320 x 240 VO
	//    134 MP4 480 x 360 VO
	//    135 MP4 640 x 480 VO
	//    136 MP4 1280 x 720 VO
	//    137 MP4 1920 x 1080 VO
	//    139 MP4 Low bitrate AO
	//    140 MP4 Med bitrate AO
	//    141 MP4 Hi bitrate AO
	//    160 MP4 256 x 144 VO


	fun getVideoType(itag: Int): String? {
		var type: String? = null
		when (itag) {
			5, 34, 35 -> type = "flv"
			17, 36 -> type = "3gp"
			18, 22, 37, 38, 82, 83, 84, 85, 133, 134, 135, 136, 137, 139, 140, 141, 160 -> type = "mp4"
			43, 44, 45, 46, 100, 101, 102 -> type = "web"
		}
		return type
	}

	fun getVideoDescribe(itag: Int): String? {
		var msg: String? = null
		when (itag) {
			5 -> msg = "FLV 320 x 240"
			34 -> msg = "FLV 480 x 360"
			35 -> msg = "FLV 640 x 480"
			17 -> msg = "3GP 176 x 144"
			36 -> msg = "3GP 320 x 240"
			18 -> msg = "MP4 480 x 360"
			22 -> msg = "MP4 1280 x 720"
			37 -> msg = "MP4 1920 x 1080"
			38 -> msg = "MP4 2048 x 1080"
			82 -> msg = "MP4 480 x 360 3D"
			83 -> msg = "MP4 640 x 480 3D"
			84 -> msg = "MP4 1280 x 720 3D"
			85 -> msg = "MP4 1920 x 1080 3D"
			133 -> msg = "MP4 320 x 240 VO"
			134 -> msg = "MP4 480 x 360 VO"
			135 -> msg = "MP4 640 x 480 VO"
			136 -> msg = "MP4 1280 x 720 VO"
			137 -> msg = "MP4 1920 x 1080 VO"
			139 -> msg = "MP4 Low bitrate AO"
			140 -> msg = "MP4 Med bitrate AO"
			141 -> msg = "MP4 Hi bitrate AO"
			160 -> msg = "MP4 256 x 144 VO"
			43 -> msg = "WEB 480 x 360"
			44 -> msg = "WEB 640 x 480"
			45 -> msg = "WEB 1280 x 720"
			46 -> msg = "WEB 1920 x 1080"
			100 -> msg = "WEB 480 x 360 3D"
			101 -> msg = "WEB 640 x 480 3D"
			102 -> msg = "WEB 1280 x 720 3D"
		}
		return msg
	}
}
