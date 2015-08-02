package andy.youtubedownloadhelper.com.youtubedownloadhelper.youtube;

/**
 * Created by andyli on 2015/8/2.
 */
public class YotubeItag {
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


    public static String getVideoType(int itag){
        String type = null;
        switch (itag){
            case 5:
            case 34:
            case 35:
                type = "FLV";
                break;
            case 17:
            case 36:
                type = "3GP";
                break;
            case 18:
            case 22:
            case 37:
            case 38:
            case 82:
            case 83:
            case 84:
            case 85:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 139:
            case 140:
            case 141:
            case 160:
                type = "MP4";
                break;
            case 43:
            case 44:
            case 45:
            case 46:
            case 100:
            case 101:
            case 102:
                type = "WEB";
                break;
        }
        return type;
    }

    public static String  getVideoDescribe(int itag){
         String msg = null;
        switch (itag){
            case 5:
                msg = "FLV 320 x 240";
                break;
            case 34:
                msg = "FLV 480 x 360";
                break;
            case 35:
                msg = "FLV 640 x 480";
                break;
            case 17:
                msg = "3GP 176 x 144";
                break;
            case 36:
                msg = "3GP 320 x 240";
                break;
            case 18:
                msg = "MP4 480 x 360";
                break;
            case 22:
                msg = "MP4 1280 x 720";
                break;
            case 37:
                msg = "MP4 1920 x 1080";
                break;
            case 38:
                msg = "MP4 2048 x 1080";
                break;
            case 82:
                msg = "MP4 480 x 360 3D";
                break;
            case 83:
                msg = "MP4 640 x 480 3D";
                break;
            case 84:
                msg = "MP4 1280 x 720 3D";
                break;
            case 85:
                msg = "MP4 1920 x 1080 3D";
                break;
            case 133:
                msg = "MP4 320 x 240 VO";
                break;
            case 134:
                msg = "MP4 480 x 360 VO";
                break;
            case 135:
                msg = "MP4 640 x 480 VO";
                break;
            case 136:
                msg = "MP4 1280 x 720 VO";
                break;
            case 137:
                msg = "MP4 1920 x 1080 VO";
                break;
            case 139:
                msg = "MP4 Low bitrate AO";
                break;
            case 140:
                msg = "MP4 Med bitrate AO";
                break;
            case 141:
                msg = "MP4 Hi bitrate AO";
                break;
            case 160:
                msg = "MP4 256 x 144 VO";
                break;
            case 43:
                msg = "WEB 480 x 360";
                break;
            case 44:
                msg = "WEB 640 x 480";
                break;
            case 45:
                msg = "WEB 1280 x 720";
                break;
            case 46:
                msg = "WEB 1920 x 1080";
                break;
            case 100:
                msg = "WEB 480 x 360 3D";
                break;
            case 101:
                msg = "WEB 640 x 480 3D";
                break;
            case 102:
                msg = "WEB 1280 x 720 3D";
                break;
        }
        return msg;
    }
}
