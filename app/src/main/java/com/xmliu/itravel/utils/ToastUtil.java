/*************************************************************************
 *  
 *  Copyright (C) 2015 Suzhou Xmliu Information Technology co., LTD.
 * 
 *                       All rights reserved.
 *
 *************************************************************************/
package com.xmliu.itravel.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 2015年9月9日 下午5:30:23
 * 
 * xmliu@raipeng.com
 * 
 * V1.0
 * 
 * 防止Toast多次快速点击显示时间过长
 * 
 * diyangxia
 * 
 */
public class ToastUtil {
	private static String oldMsg;
	protected static Toast toast = null;
	private static long oneTime = 0;
	private static long twoTime = 0;

	public static void showToastOld(Context context, String s) {
		if (toast == null) {
			toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
			toast.show();
			oneTime = System.currentTimeMillis();
		} else {
			twoTime = System.currentTimeMillis();
			if (s.equals(oldMsg)) {
				if (twoTime - oneTime > Toast.LENGTH_SHORT) {
					toast.show();
				}
			} else {
				oldMsg = s;
				toast.setText(s);
				toast.cancel();
				toast.show();
			}
		}
		oneTime = twoTime;
	}

	public static void showToast(Context context, String s) {
		if (toast == null) {
			toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
		} else {
			toast.setText(s);
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		toast.show();
	}


	public static void showToast(Context context, int resId) {
		showToast(context, context.getString(resId));
	}

}
