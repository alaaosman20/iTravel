/*************************************************************************
 *  
 *  Copyright (C) 2013 SuZhou Xmliu Information Technology co., LTD.
 * 
 *                       All rights reserved.
 *
 *************************************************************************/
package com.xmliu.itravel.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.ui.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

/**
 * @date: 2013-10-28 下午2:16:20
 * 
 * @version: V1.0
 * 
 * @description: 公用工具类
 * 
 */
public class CommonUtils {


	public static MaterialDialog materialDialog;
	// 按钮pressed后的状态
	public final static float[] BT_PRESSED = new float[] { 1, 0, 0, 0, -50, 0,
			1, 0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0 };

	// 按钮初始的状态
	public final static float[] BT_FORMER = new float[] { 1, 0, 0, 0, 0, 0, 1,
			0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

	private static long lastClickTime;

	public static String getHttpStringWithGet(String urlStr) {
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
//			urlConn.setDoInput(true);
//			urlConn.setDoOutput(true);
			urlConn.setRequestMethod("GET");
			urlConn.setUseCaches(false);
			urlConn.setConnectTimeout(5000);
			urlConn.setReadTimeout(50000);
			urlConn.connect();

			buffer = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream()));
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.i("TAG", e.getMessage());
			JSONObject obj = new JSONObject();
			try {
				obj.put("success", "false");
				obj.put("reason", "无法连接到服务器");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			return obj.toString();
		} finally {
			try {
				if (buffer != null)
					buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static boolean isInstallApp(Context context,String packageName) {
		Intent weiboIntent = new Intent(Intent.ACTION_SEND);
		weiboIntent.setType("text/plain");
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> matches = pm.queryIntentActivities(weiboIntent,
				PackageManager.MATCH_DEFAULT_ONLY);
		ResolveInfo info = null;
		for (ResolveInfo each : matches) {
			String pkgName = each.activityInfo.applicationInfo.packageName;
			if (packageName.equals(pkgName)) {
				info = each;
				break;
			}
		}
		if (info == null) {
			return false;
		} else {
			return true;
		}

	}

	public static void showExitDialog(final Context context){
		MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
//                    .backgroundColorRes(R.color.white)
				.title("应用提示")
				.content("确定退出当前登录账号" + UserBean.getCurrentUser(context).getUsername() + "吗？")
				.positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(MaterialDialog dialog, DialogAction which) {
						dialog.dismiss();
						UserBean.logOut(context);
						AppManager.getInstance().killAllActivity();
						context.startActivity(new Intent(context, LoginActivity.class));
					}
				})
				.negativeText("取消")
				.onNegative(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(MaterialDialog dialog, DialogAction which) {
						dialog.dismiss();
					}
				});

		MaterialDialog dialog = builder.build();
		dialog.show();
	}

	public synchronized static boolean isFastClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 1000) {
			return true;
		}
		lastClickTime = time;
		return false;
	}


	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param context
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param context
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	// 单位转化
	public static Float dip2px(float scale, float dpValue) {
		return (float) (dpValue * scale + 0.5f);
	}

	public static Float px2dip(float scale, float pxValue) {
		return (float) (pxValue / scale + 0.5f);
	}

	public static void showProgressDialog(Context context){
		materialDialog = new MaterialDialog.Builder(context)
				.title("系统提示")
				.content("加载中...")
				.cancelable(false)
				.progress(true, 0).show();
	}
	public static void showProgressDialog(Context context,String content){
		materialDialog = new MaterialDialog.Builder(context)
				.title("系统提示")
				.content(content)//"加载中..."
				.cancelable(false)
				.progress(true, 0).show();
	}
	public static void hideProgressDialog(){
		materialDialog.dismiss();
	}


	/**
	 * 当View被按下时，修改其Alpha值，使得按钮变暗
	 * 
	 * @param v
	 */
	public static void onViewStateChanged(View v) {
		v.setOnTouchListener(listener);
	}

	/**
	 * 当按钮按下时的touch事件,使按钮变暗
	 */
	private static OnTouchListener listener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_PRESSED));
				v.setBackgroundDrawable(v.getBackground());
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_FORMER));
				v.setBackgroundDrawable(v.getBackground());
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BT_FORMER));
				v.setBackgroundDrawable(v.getBackground());
			}
			return false;
		}
	};


	/**
	 * 显示Toast消息
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showToast(Context context, String msg) {
		ToastUtil.showToast(context, msg);
	}

	/**
	 * 显示单个按钮的AlertDialog
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 */
	public static void showAlert(Context context, String title, String msg) {
		Builder builder = new Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton("确定", null);
		builder.create();
		builder.show();
	}

	/**
	 * 显示两个按钮的AlertDialog
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param positiveText
	 * @param onPositiveClickListener
	 * @param negativeText
	 * @param onNegativeClickListener
	 * @return
	 */
	protected AlertDialog showAlertDialog(Context context, String title,
			String message, String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new Builder(context)
				.setTitle(title).setMessage(message)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener)
				.show();
		return alertDialog;
	}

	/**
	 * 获取UUID
	 * 
	 * @return
	 */
	public static String newRandomUUID() {
		String uuidRaw = UUID.randomUUID().toString();
		return uuidRaw.replaceAll("-", "");
	}

	/**
	 * 获取状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return context.getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 
	 * @param activity
	 * @return > 0 success; <= 0 fail
	 */
	public static int getStatusHeight(Activity activity) {
		int statusHeight = 0;
		Rect localRect = new Rect();
		activity.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass
						.getField("status_bar_height").get(localObject)
						.toString());
				statusHeight = activity.getResources()
						.getDimensionPixelSize(i5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}

	/**
	 * 获取系统版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getSysVersionName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager()
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_CONFIGURATIONS).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;
	}

}
