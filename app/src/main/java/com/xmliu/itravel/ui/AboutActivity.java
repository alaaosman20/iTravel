package com.xmliu.itravel.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.VersionBean;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.LogUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by xmliu on 2016/1/30.
 */
public class AboutActivity extends ToolbarActivity implements View.OnClickListener{

    private RelativeLayout versionLayout;
    private RelativeLayout marketLayout;
    private RelativeLayout feedbackLayout;
    private RelativeLayout guideLayout;
    private RelativeLayout meLayout;
    private TextView protocolTV;
    private TextView versionTV;

    private String currentVersionName;
    private String latestVersionName;
    private String downloadUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar.setTitle("关于");

        versionLayout = (RelativeLayout) findViewById(R.id.aboutus_version_layout);
        marketLayout = (RelativeLayout) findViewById(R.id.aboutus_market_layout);
        feedbackLayout = (RelativeLayout) findViewById(R.id.aboutus_feedback_layout);
        guideLayout = (RelativeLayout) findViewById(R.id.aboutus_guide_layout);
        meLayout = (RelativeLayout) findViewById(R.id.aboutus_me_layout);
        protocolTV = (TextView) findViewById(R.id.aboutus_protocol_tv);
        versionTV = (TextView) findViewById(R.id.aboutus_version_tv);

        currentVersionName = CommonUtils.getSysVersionName(AboutActivity.this);
        versionTV.setText("当前版本："+currentVersionName);
        mHandler = new Handler();

        versionLayout.setOnClickListener(this);
        marketLayout.setOnClickListener(this);
        feedbackLayout.setOnClickListener(this);
        guideLayout.setOnClickListener(this);
        meLayout.setOnClickListener(this);
        protocolTV.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.aboutus_version_layout:
                checkVersion();
                break;
            case R.id.aboutus_market_layout:
                Uri uri = Uri.parse("market://details?id="+getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.aboutus_feedback_layout:
                startActivity(new Intent(AboutActivity.this, FeedbackActivity.class));
                break;
            case R.id.aboutus_guide_layout:
                startActivity(new Intent(AboutActivity.this, GuideActivity.class).putExtra("fromabout",true));
                break;
            case R.id.aboutus_me_layout:
                
                break;
            case R.id.aboutus_protocol_tv:
                startActivity(new Intent(AboutActivity.this, RegisterProtocolActivity.class));
                break;


        }
    }

    private void checkVersion(){

        BmobQuery<VersionBean> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("type", 1);
        bmobQuery.findObjects(AboutActivity.this, new FindListener<VersionBean>() {
            @Override
            public void onSuccess(List<VersionBean> object) {
                // TODO Auto-generated method stub

                if (object.size() > 0) {
                    LogUtil.i("AboutActivity", "版本更新成功" + object.get(0).getPath());
                    downloadUrl = object.get(0).getPath();
                    latestVersionName = object.get(0).getVersion();
                    if(latestVersionName.toLowerCase().contains("v")){
                        latestVersionName = latestVersionName.replace("v","");
                    }
                    if(currentVersionName.equals(latestVersionName)){
                        CommonUtils.showToast(AboutActivity.this, "当前为最新版本");
                    }else {
                        showVersionUpdateDialog();
                    }
                } else {
                    CommonUtils.showToast(AboutActivity.this, "版本更新失败");
                }

            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                CommonUtils.showToast(AboutActivity.this, "版本更新失败" + msg);
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

}
