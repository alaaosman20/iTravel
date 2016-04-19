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

import com.xmliu.itravel.utils.ImageUtils;
import com.xmliu.itravel.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

		ImageUtils.initImageLoader(this,Constants.Path.ImageLoaderDir);

//		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
//		JPushInterface.init(this);     		// 初始化 JPush  //030fe9f62bb

//		String appId = "900004745"; // 上Bugly(bugly.qq.com)注册产品获取的AppId
//		boolean isDebug = false; // true代表App处于调试阶段，false代表App发布阶段
//		CrashReport.initCrashReport(this, appId, isDebug); // 初始化SDK

		SMSSDK.initSDK(this, "d900bcefc46f", "a05209f358fee5e09384e12439207df7");
		Bmob.initialize(this, "07b752a64abdf34127887ada169d9709");

//		Thread.setDefaultUncaughtExceptionHandler(new CrashExceptionHandler(
//				getApplicationContext()));
	}

	public static BaseApplication getInstance() {
		return mInstance;
	}

	/**
	 * 
	 */
	private void createFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		} else {
			file.mkdirs();
		}
	}

}
