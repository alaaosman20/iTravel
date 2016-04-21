/*************************************************************************
 *  
 *  Copyright (C) 2013 SuZhou Xmliu Information Technology co., LTD.
 * 
 *                       All rights reserved.
 *
 *************************************************************************/
package com.xmliu.itravel;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.bugtags.library.Bugtags;
import com.xmliu.itravel.utils.AppManager;
import com.xmliu.itravel.utils.NetWorkUtils;

/**
 * @date: 2013-10-28 下午2:07:19
 * 
 * @version: V1.0
 * 
 * @description:
 * 
 */
public abstract class BaseActivity extends Activity {

	protected BaseApplication mApplication;
	protected Handler mHandler;
	protected String TAG;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getInstance().addActivity(this);
		NetWorkUtils.networkStateTips(this);
//		CommonUtils.initSystemBar(this, R.color.top_blue_bg); // 仿IOS沉浸式状态栏
		mApplication = (BaseApplication) getApplication();
		TAG=this.getLocalClassName();
	}
	    
	@Override
	protected void onResume() {
		super.onResume();
		//注：回调 1
		Bugtags.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//注：回调 2
		Bugtags.onPause(this);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		//注：回调 3
		Bugtags.onDispatchTouchEvent(this, event);
		return super.dispatchTouchEvent(event);
	}
}
