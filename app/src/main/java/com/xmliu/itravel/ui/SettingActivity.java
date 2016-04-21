package com.xmliu.itravel.ui;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bugtags.library.Bugtags;
import com.gc.materialdesign.views.Switch;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xmliu.itravel.Constants;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.SettingBean;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.FileUtils;
import com.xmliu.itravel.utils.LogUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by xmliu on 2016/1/31.
 */
public class SettingActivity extends ToolbarActivity {
    private RelativeLayout shakeLayout;
    private RelativeLayout aboutLayout;
    private RelativeLayout pushLayout;
    private RelativeLayout cacheLayout;
    private RelativeLayout imageLayout;

    private Switch shakeSwitch;
    private Switch pushSwitch;
    private Switch imageSwitch;
    private TextView mCacheSizeTV;
    private String cacheSize;
    private File fileDir = new File(Constants.Path.ImageCacheDir);
    private File cropfileDir = new File(Constants.Path.ImageCrop);
    private File compressfileDir = new File(Constants.Path.ImageCompressDir);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mHandler = new Handler();
        toolbar.setTitle("系统设置");

        shakeLayout = (RelativeLayout) findViewById(R.id.setting_feedback_layout);
        aboutLayout = (RelativeLayout) findViewById(R.id.setting_about_layout);
        pushLayout = (RelativeLayout) findViewById(R.id.setting_push_layout);
        cacheLayout = (RelativeLayout) findViewById(R.id.setting_cache_layout);
        imageLayout = (RelativeLayout) findViewById(R.id.setting_image_layout);
        shakeSwitch = (Switch) findViewById(R.id.setting_feedback_shake);
        pushSwitch = (Switch) findViewById(R.id.setting_push_switch);
        imageSwitch = (Switch) findViewById(R.id.setting_image_switch);
        mCacheSizeTV  = (TextView) findViewById(R.id.setting_cache_size);

        if(fileDir.exists()) {
            long fileSize = FileUtils.getFileSizes(fileDir) + FileUtils.getFileSizes(cropfileDir) + FileUtils.getFileSizes(compressfileDir);

            DecimalFormat df = new DecimalFormat("#.##");
            double fileS = fileSize / 1048576.0;

            cacheSize = df.format(fileS);
            mCacheSizeTV.setText(" " + cacheSize + " MB");
        }else{
            mCacheSizeTV.setText(" " + 0.0 + " MB");
        }


        if(Bugtags.currentInvocationEvent() == Bugtags.BTGInvocationEventShake){
            shakeSwitch.setChecked(true);
        }else {
            shakeSwitch.setChecked(false);
        }

        initImageSwitch();

        shakeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Bugtags.currentInvocationEvent() == Bugtags.BTGInvocationEventShake){
                    shakeSwitch.setChecked(false);
                    Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventNone); //静默模式，只收集 Crash 信息（如果允许）
                }else {
                    shakeSwitch.setChecked(true);
                    Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventShake); // 通过摇一摇呼出 Bugtags
                }
            }
        });
        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,AboutActivity.class));
            }
        });
        cacheLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileDir.exists()) {

                    MaterialDialog.Builder builder = new MaterialDialog.Builder(SettingActivity.this)
                            .title("应用提示")
                            .content("确定要清除缓存吗？")
                            .positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                    CommonUtils.showProgressDialog(SettingActivity.this, "正在清除");
                                    ImageLoader.getInstance().clearMemoryCache();
                                    FileUtils.deleteFile(fileDir);
                                    FileUtils.deleteFile(cropfileDir);
                                    FileUtils.deleteFile(compressfileDir);
                                    mHandler.postDelayed(new Runnable() {

                                        @Override
                                        public void run() {

                                            mCacheSizeTV.setText(0.0 + "MB");
                                            cacheSize = "0.0";
                                            CommonUtils.showToast(SettingActivity.this, "缓存已清空");
                                            CommonUtils.hideProgressDialog();
                                        }
                                    }, 2000);
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

                } else {
                    CommonUtils.showToast(SettingActivity.this, "缓存已清空");
                }
            }
        });
        pushLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pushSwitch.isCheck()) {
                    pushSwitch.setChecked(false);
                }else{
                    pushSwitch.setChecked(true);
                }
                CommonUtils.showToast(SettingActivity.this, "暂未开放");
            }
        });
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SettingBean settingBean = new SettingBean();
                settingBean.setAuthor(BmobUser.getCurrentUser(SettingActivity.this, UserBean.class));
                if (imageSwitch.isCheck()) {
                    imageSwitch.setChecked(false);
                    settingBean.setGprsImageEnable(true);
                } else {
                    imageSwitch.setChecked(true);
                    settingBean.setGprsImageEnable(false);
                }
                settingBean.save(SettingActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        LogUtil.i(TAG,"设置成功");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        LogUtil.i(TAG,"设置失败");
                    }
                });

            }
        });
        shakeSwitch.setOncheckListener(new Switch.OnCheckListener() {
            @Override
            public void onCheck(Switch view, boolean check) {
                if (check) {
                    Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventShake); // 通过摇一摇呼出 Bugtags
                } else {
                    Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventNone); //静默模式，只收集 Crash 信息（如果允许）
                }
            }
        });

    }

    private void initImageSwitch(){
        BmobQuery<SettingBean> bmobQuery = new BmobQuery<>();
        UserBean userBean = BmobUser.getCurrentUser(SettingActivity.this,UserBean.class);
        bmobQuery.addWhereEqualTo("author", userBean);
        bmobQuery.findObjects(SettingActivity.this, new FindListener<SettingBean>() {
            @Override
            public void onSuccess(List<SettingBean> object) {
                if (object.size() > 0) {
                    LogUtil.i(TAG, "result" + object.get(0).isGprsImageEnable());
                    boolean isGprsloadImage =  object.get(0).isGprsImageEnable();
                    if(isGprsloadImage){
                        imageSwitch.setChecked(false);
                    }else{
                        imageSwitch.setChecked(true);
                    }

                } else {
                    LogUtil.i(TAG, "查询失败，数据为空");
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtil.i(TAG, "查询失败");

            }
        });
    }

}
