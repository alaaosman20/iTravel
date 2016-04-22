/*************************************************************************
 *  
 *  Copyright (C) 2013 SuZhou Xmliu Information Technology co., LTD.
 * 
 *                       All rights reserved.
 *
 *************************************************************************/
package com.xmliu.itravel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.bugtags.library.Bugtags;
import com.xmliu.itravel.utils.CrashExceptionHandler;
import com.xmliu.itravel.utils.ImageUtils;
import com.xmliu.itravel.utils.LogUtil;

import cn.bmob.v3.Bmob;
import cn.smssdk.SMSSDK;

/**
 * @date: 2013-12-30 上午9:41:30
 * 
 * @email: tchen@raipeng.com
 * 
 * @version: V1.0
 * 
 * @description:
 * 
 */
public class BaseApplication extends Application {

	public SharedPreferences mSharedPreferences;
	public static BaseApplication mInstance = null;
//	public List<String> mLocalSdPhotos = new ArrayList<String>();
	@Override
	public void onCreate() {

		super.onCreate();

		mInstance = this;
		mSharedPreferences = getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

		LogUtil.setDebug(true); // true为调试模式，可在控制台看到日志；false为线上模式看不到日志

		ImageUtils.initImageLoader(this, Constants.Path.ImageLoaderDir);

//		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
//		JPushInterface.init(this);     		// 初始化 JPush  //030fe9f62bb

		//在这里初始化 静默模式，只收集 Crash 信息（如果允许）
		Bugtags.start("240e0e9796918ce6a1bc39439a999934", this, Bugtags.BTGInvocationEventNone);

		SMSSDK.initSDK(this, "d900bcefc46f", "a05209f358fee5e09384e12439207df7");

		Bmob.initialize(this, "07b752a64abdf34127887ada169d9709");

		Thread.setDefaultUncaughtExceptionHandler(new CrashExceptionHandler(
				getApplicationContext()));
	}

	public static BaseApplication getInstance() {
		return mInstance;
	}


}
