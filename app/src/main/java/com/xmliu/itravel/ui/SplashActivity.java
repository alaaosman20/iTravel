package com.xmliu.itravel.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.xmliu.itravel.BaseActivity;
import com.xmliu.itravel.Constants;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.bean.VersionBean;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.FileUtils;
import com.xmliu.itravel.utils.LogUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * 启动页
 * Created by Admin on 2015/11/24.
 */
public class SplashActivity extends BaseActivity {


    private String currentVersionName;
    private String latestVersionName;
    private String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_splash);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Constants.Screen.width = metrics.widthPixels;
        Constants.Screen.height = metrics.heightPixels;
        Constants.Screen.density = metrics.density;
        FileUtils.initDir();

        mHandler = new Handler();

        currentVersionName = CommonUtils.getSysVersionName(SplashActivity.this);
        // 检测版本更新
        checkVersion();


    }

    private void afterVersionCheck(){
        UserBean userInfo = BmobUser.getCurrentUser(this, UserBean.class);

        if (userInfo != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    SplashActivity.this.finish();
                }
            }, 1500);
        } else {
            boolean isFirstTime = mApplication.mSharedPreferences.getBoolean("isFirstTime", true);
            if (isFirstTime) {
                // 如果是第一次进入应用，停留3秒进入引导页面
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                        SplashActivity.this.finish();
                    }
                }, 3000);
            } else {
                // 否则，停留3秒进入登陆页面
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        SplashActivity.this.finish();
                    }
                }, 3000);
            }
        }
    }
    private void checkVersion(){

        BmobQuery<VersionBean> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("type", 1);
        bmobQuery.findObjects(SplashActivity.this, new FindListener<VersionBean>() {
            @Override
            public void onSuccess(List<VersionBean> object) {
                // TODO Auto-generated method stub

                if (object.size() > 0) {
                    LogUtil.i("SplashActivity", "版本更新成功"+object.get(0).getPath());
                    downloadUrl = object.get(0).getPath();
                    latestVersionName = object.get(0).getVersion();
                    if(latestVersionName.toLowerCase().contains("v")){
                        latestVersionName = latestVersionName.replace("v","");
                    }
                    if(currentVersionName.equals(latestVersionName)){
                        CommonUtils.showToast(SplashActivity.this, "当前为最新版本");
                        afterVersionCheck();
                    }else {
                        showVersionUpdateDialog();
                    }
                } else {
                    CommonUtils.showToast(SplashActivity.this, "版本更新失败");
                    afterVersionCheck();
                }

            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                CommonUtils.showToast(SplashActivity.this, "版本更新失败" + msg);
                afterVersionCheck();
            }
        });

    }

    private void showVersionUpdateDialog(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("版本更新")
                .content("当前版本：" + currentVersionName + "\n新版本："
                        + latestVersionName + "\n是否更新？")
                .positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        Uri uri = Uri.parse(downloadUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        afterVersionCheck();
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        afterVersionCheck();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

}
