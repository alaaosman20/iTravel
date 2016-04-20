package com.xmliu.itravel.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.bugtags.library.Bugtags;
import com.xmliu.itravel.Constants;
import com.xmliu.itravel.bean.CrashBean;
import com.xmliu.itravel.bean.UserBean;

import java.lang.Thread.UncaughtExceptionHandler;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * @date: 2015年6月16日 下午5:21:01
 * @email: xmliu@raipeng.com
 * @version: V1.0
 * @description: 异常捕捉类，发生异常后直接退出App 并把错误日志发送到Bmob服务器
 * @author: diyangxia
 */
public class CrashExceptionHandler implements UncaughtExceptionHandler {

    private String TAG = CrashExceptionHandler.class.getSimpleName();
    private Context act = null;

    public CrashExceptionHandler(Context act) {
        this.act = act;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LogUtil.e(TAG, "uncaughtException ---->" + ex.getMessage());
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(act, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG)
                        .show();
                Looper.loop();
            }
        }.start();

        handleException(ex);
    }

    private void handleException(Throwable ex) {
//        sendCrashReport(ex); // 发送给bmob
        Bugtags.sendException(ex); // 发送给bugtags
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        AppManager.getInstance().killAllActivity();
        System.exit(0);
    }

    private void sendCrashReport(Throwable ex) {
        String exceptionReason = ex.getMessage();
        LogUtil.e(TAG, "exceptionReason" + exceptionReason);

        StringBuffer exceptionStr = new StringBuffer();
        StackTraceElement[] elements = ex.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            exceptionStr.append(elements[i].toString());
        }
        String stackTrace = exceptionStr.toString();
        LogUtil.e(TAG, "stackTrace -----> " + stackTrace);

        /**
         * SDK: 18 ---> Android 4.3 SDK: 17 ---> Android 4.2.2 SDK: 16 --->
         * Android 4.1.2 SDK: 15 ---> Android 4.0.3 SDK: 14 ---> Android 4.0
         * SDK: 13 ---> Android 3.2 SDK: 12 ---> Android 3.1 SDK: 11 --->
         * Android 3.0 SDK: 10 ---> Android 2.3.3 SDK: 8 ---> Android 2.2 SDK: 7
         * ---> Android 2.1 SDK: 4 ---> Android 1.6 SDK: 3 ---> Android 1.5
         */

        String osVersion = "";
        int SDK = android.os.Build.VERSION.SDK_INT;
        switch (SDK) {
            case 23:
                osVersion = "Android 6.0";
                break;
            case 22:
                osVersion = "Android 5.1.1";
                break;
            case 21:
                osVersion = "Android 5.0.1";
                break;
            case 20:
                osVersion = "Android 4.4W.2";
                break;
            case 19:
                osVersion = "Android 4.4.2";
                break;
            case 18:
                osVersion = "Android 4.3.1";
                break;

            case 17:
                osVersion = "Android 4.2.2";
                break;

            case 16:
                osVersion = "Android 4.1.2";
                break;

            case 15:
                osVersion = "Android 4.0.3";
                break;

            case 14:
                osVersion = "Android 4.0";
                break;

            case 13:
                osVersion = "Android 3.2";
                break;

            case 12:
                osVersion = "Android 3.1";
                break;

            case 11:
                osVersion = "Android 3.0";
                break;

            case 10:
                osVersion = "Android 2.3.3";
                break;

            case 8:
                osVersion = "Android 2.2";
                break;

            case 7:
                osVersion = "Android 2.1";
                break;

            case 4:
                osVersion = "Android 1.6";
                break;

            case 3:
                osVersion = "Android 1.5";
                break;

            default:
                break;
        }
        System.out.println("PhoneInformation ----> osVersion -----> "
                + osVersion);

        String phoneModel = android.os.Build.PRODUCT + "_"
                + android.os.Build.BRAND + "_" + android.os.Build.MODEL;
        System.out.println("PhoneInformation ----> phoneModel -----> "
                + phoneModel);

        // IMEI
        TelephonyManager tm = (TelephonyManager) act
                .getSystemService(Context.TELEPHONY_SERVICE);
        String token = tm.getDeviceId();
        System.out.println("PhoneInformation ----> token -----> " + token);

        PackageManager pm = act.getPackageManager();
        PackageInfo nPackageInfo;
        String appName = "";
        String appVersionCode = "";

        try {
            nPackageInfo = pm.getPackageInfo(act.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            appName = nPackageInfo.applicationInfo.loadLabel(pm).toString();
            System.out.println("PhoneInformation ----> appName -----> "
                    + appName);

            appVersionCode = nPackageInfo.versionCode + "";
            System.out.println("PhoneInformation ----> appVersionCode -----> "
                    + appVersionCode);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String screenSize = Constants.Screen.width + "*"
                + Constants.Screen.height;
        System.out.println("PhoneInformation ----> screenSize -----> "
                + screenSize);

        if (checkNetworkInfo()) {
            onSubmitCrash(osVersion,
                    phoneModel, token, appName, appVersionCode, "",
                    exceptionReason, stackTrace, screenSize);

        }

    }

    private void onSubmitCrash(String osVersion, String phoneModel,
                               String token, String appName, String appVersionNo,
                               String exceptionName, String exceptionReason,
                               String stackTrace, String screenSize) {
        CrashBean crashBean = new CrashBean();
        crashBean.setAuthor(BmobUser.getCurrentUser(act, UserBean.class));
        crashBean.setOsVersion(osVersion);
        crashBean.setPhoneModel(phoneModel);
        crashBean.setToken(token);
        crashBean.setAppName(appName);
        crashBean.setAppVersionNo(appVersionNo);
        crashBean.setExceptionName(exceptionName);
        crashBean.setExceptionReason(exceptionReason);
        crashBean.setStackTrace(stackTrace);
        crashBean.setScreenSize(screenSize);

        crashBean.save(act, new SaveListener() {
            @Override
            public void onSuccess() {
                LogUtil.d(TAG, "我们会尽快处理您提交的崩溃");
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.d(TAG, "提交失败:" + s);
            }
        });
    }

    private boolean checkNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) act
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isAvailable()) {
            return false;
        } else {
            return true;
        }
    }

    private class QueryCrashEntity {

        private String osVersion;
        private String phoneModel;
        private String token;
        private String appName;
        private String appVersionNo;
        private String exceptionName;
        private String exceptionReason;
        private String stackTrace;
        private String screenSize;

        public QueryCrashEntity(String osVersion, String phoneModel,
                                String token, String appName, String appVersionNo,
                                String exceptionName, String exceptionReason,
                                String stackTrace, String screenSize) {
            super();
            this.osVersion = osVersion;
            this.phoneModel = phoneModel;
            this.token = token;
            this.appName = appName;
            this.appVersionNo = appVersionNo;
            this.exceptionName = exceptionName;
            this.exceptionReason = exceptionReason;
            this.stackTrace = stackTrace;
            this.screenSize = screenSize;
        }
    }
}
