package com.xmliu.itravel;

import android.os.Environment;

/**
 * Created by Admin on 2015/12/20.
 */
public class Constants {

    public static final String SHARED_PREFERENCE_NAME = "iTravel";

    /**
     * 屏幕参数
     */
    public static class Screen {
        public static int height = 800;
        public static int width = 480;
        public static float density = 1.5f;
    }

    /**
     * 标记
     */
    public static class Tags {

        public static final int LOAD_DATA_SUCCESS = 0x5001;
        public static final int LOAD_DATA_FAILED = 0x5002;
        public static final int SAVE_DATA_SUCCESS = 0X5003;
        public static final int SAVE_DATA_FAILED = 0X5004;
        public static final int UPLOAD_IMAGE_SUCCESS = 0X5005;
        public static final int UPLOAD_IMAGE_FAILED = 0X5006;
        public static final int TAG_FIRST_SUCCESS = 0X5007;
        public static final int TAG_FIRST_FAILED = 0X5008;
        public static final int TAG_SECOND_SUCCESS = 0X5009;
        public static final int TAG_SECOND_FAILED = 0X5010;

    }

    /**
     * 路径定义
     */
    public static class Path {
        public static final String SD_PATH = Environment
                .getExternalStorageDirectory().getAbsolutePath();
        public static final String ImageLoaderDir = "/Xmliu/iTravel/Cache/";

        public static final String ImageCacheDir = SD_PATH + "/Xmliu/iTravel/Cache/";
        public static final String ImageDownloadDir = SD_PATH
                + "/Xmliu/iTravel/Images/Download/";
        public static final String ImageCompressDir = SD_PATH
                + "/Xmliu/iTravel/Images/Compress/";
        public static final String ImageCameraDir = SD_PATH
                + "/Xmliu/iTravel/Images/Camera/";
        public static final String ImageCrop = SD_PATH
                + "/Xmliu/iTravel/Images/Crop/";
        public static final String DBPath = SD_PATH
                + "/Xmliu/iTravel/DBCache/";

    }
}
