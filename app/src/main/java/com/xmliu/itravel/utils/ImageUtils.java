package com.xmliu.itravel.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.xmliu.itravel.R;

import java.io.File;

/**
 * Date: 2016/1/13-15-15:33
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class ImageUtils {

    public static DisplayImageOptions initDisplayOptions() {
        DisplayImageOptions.Builder displayImageOptionsBuilder = new DisplayImageOptions.Builder();
        displayImageOptionsBuilder.imageScaleType(ImageScaleType.EXACTLY);
        displayImageOptionsBuilder.cacheInMemory(true);
        displayImageOptionsBuilder.cacheOnDisc(true);
        displayImageOptionsBuilder.showImageForEmptyUri(R.mipmap.image_loading);
        displayImageOptionsBuilder.showImageOnFail(R.mipmap.image_loading);
        displayImageOptionsBuilder.showStubImage(R.mipmap.image_loading);
        displayImageOptionsBuilder.bitmapConfig(Bitmap.Config.RGB_565);

        return displayImageOptionsBuilder.build();
    }

    public static void initImageLoader(Context context, String cacheDisc) {
        // 获取本地缓存的目录，该目录在SDCard的根目录下
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, cacheDisc);
        // 实例化Builder
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
        // 设置线程数量
        builder.threadPoolSize(3);
        // 设定线程等级比普通低一点
        builder.threadPriority(Thread.NORM_PRIORITY);
        // 设定内存缓存为弱缓存
        builder.memoryCache(new WeakMemoryCache());
        // 设定内存图片缓存大小限制，不设置默认为屏幕的宽高
        builder.memoryCacheExtraOptions(480, 800);
        // 设定只保存同一尺寸的图片在内存
        builder.denyCacheImageMultipleSizesInMemory();
        // 设定缓存到SDCard目录的文件命名
        builder.discCacheFileNameGenerator(new Md5FileNameGenerator());
        // 设定网络连接超时 timeout: 10s 读取网络连接超时read timeout: 60s
        builder.imageDownloader(new BaseImageDownloader(context, 10000, 60000));
        // 设置ImageLoader的配置参数
        builder.defaultDisplayImageOptions(initDisplayOptions());

        // 初始化ImageLoader
        ImageLoader.getInstance().init(builder.build());
    }
}
