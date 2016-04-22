package com.xmliu.itravel.ui;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bugtags.library.Bugtags;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xmliu.itravel.Constants;
import com.xmliu.itravel.R;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.FileUtils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by xmliu on 2016/1/31.
 */
public class SettingActivity extends ToolbarActivity {
    private RelativeLayout shakeLayout;
    private RelativeLayout aboutLayout;
    private RelativeLayout pushLayout;
    private RelativeLayout fontLayout;
    private RelativeLayout lawLayout;
    private RelativeLayout cacheLayout;

//    private SlideSwitch shakeSwitch;
//    private SlideSwitch pushSwitch;
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
        fontLayout = (RelativeLayout) findViewById(R.id.setting_font_layout);
        lawLayout = (RelativeLayout) findViewById(R.id.setting_law_layout);
        cacheLayout = (RelativeLayout) findViewById(R.id.setting_cache_layout);
//        shakeSwitch = (SlideSwitch) findViewById(R.id.setting_feedback_shake);
//        pushSwitch = (SlideSwitch) findViewById(R.id.setting_push_switch);
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
//            shakeSwitch.setState(true);
        }else {
//            shakeSwitch.setState(false);
        }

        shakeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Bugtags.currentInvocationEvent() == Bugtags.BTGInvocationEventShake){
//                    shakeSwitch.setState(false);
                    Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventNone); //静默模式，只收集 Crash 信息（如果允许）
                }else {
//                    shakeSwitch.setState(true);
                    Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventShake); // 通过摇一摇呼出 Bugtags
                }
            }
        });
        fontLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_setting_font,null);
                MaterialDialog.Builder builder = new MaterialDialog.Builder(SettingActivity.this)
                        .customView(view, false);
                DiscreteSeekBar seekBar = (DiscreteSeekBar) view.findViewById(R.id.setting_font_seekbar);
                final TextView textView = (TextView) view.findViewById(R.id.setting_font_text);
                Button submitBtn = (Button) view.findViewById(R.id.setting_font_submit);
                seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                    @Override
                    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                        textView.setTextSize(value);
                    }

                    @Override
                    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

                    }
                });

                MaterialDialog dialog = builder.build();
                dialog.show();
            }
        });
        lawLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, SettingnLawActivity.class));
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

            }
        });

//        shakeSwitch.setSlideListener(new SlideSwitch.SlideListener() {
//            @Override
//            public void open() {
//                Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventShake); // 通过摇一摇呼出 Bugtags
//            }
//
//            @Override
//            public void close() {
//                Bugtags.setInvocationEvent(Bugtags.BTGInvocationEventNone); //静默模式，只收集 Crash 信息（如果允许）
//            }
//        });


    }

}
