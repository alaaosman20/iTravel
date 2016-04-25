package com.xmliu.itravel.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.xmliu.itravel.Constants;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.VersionBean;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.LogUtil;
import com.xmliu.itravel.widget.ShareBottomBar;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by xmliu on 2016/1/30.
 */
public class AboutActivity extends ToolbarActivity implements View.OnClickListener {

    private RelativeLayout versionLayout;
    private RelativeLayout marketLayout;
    private RelativeLayout feedbackLayout;
    private RelativeLayout shareLayout;
    private RelativeLayout guideLayout;
    private RelativeLayout meLayout;
    private TextView protocolTV;
    private TextView versionTV;

    private String currentVersionName;
    private String latestVersionName;
    private String downloadUrl;

    // 分享内容
    private ShareBottomBar shareBottomBar = null;
    private String shareTitleStr = "应用分享";
    private String shareContentStr = "千万不要使用这款软件，不然你会后悔的，不信你可以试试。";
    private String shareImagePath = Constants.Path.SD_PATH + "/null.jpg";
    private String shareImageUrl = "http://file.bmob.cn/M03/AB/3A/oYYBAFbKnfeAYw8iAABbKWCZdAo368.jpg";
    private String shareWebpage = "http://a.app.qq.com/o/simple.jsp?pkgname=com.xmliu.itravel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar.setTitle("关于");
        versionLayout = (RelativeLayout) findViewById(R.id.aboutus_version_layout);
        marketLayout = (RelativeLayout) findViewById(R.id.aboutus_market_layout);
        feedbackLayout = (RelativeLayout) findViewById(R.id.aboutus_feedback_layout);
        shareLayout = (RelativeLayout) findViewById(R.id.aboutus_share_layout);
        guideLayout = (RelativeLayout) findViewById(R.id.aboutus_guide_layout);
        meLayout = (RelativeLayout) findViewById(R.id.aboutus_me_layout);
        protocolTV = (TextView) findViewById(R.id.aboutus_protocol_tv);
        versionTV = (TextView) findViewById(R.id.aboutus_version_tv);

        currentVersionName = CommonUtils.getSysVersionName(AboutActivity.this);
        versionTV.setText("当前版本：" + currentVersionName);
        mHandler = new Handler();
        shareBottomBar = new ShareBottomBar(this);

        versionLayout.setOnClickListener(this);
        marketLayout.setOnClickListener(this);
        feedbackLayout.setOnClickListener(this);
        guideLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        meLayout.setOnClickListener(this);
        protocolTV.setOnClickListener(this);

    }

    /**
     * @param v
     */
    private void showSharePopup(View v) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        shareBottomBar.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        shareBottomBar.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        shareBottomBar
                .setOnShareModeBtnClickListener(new ShareBottomBar.onShareModeBtnCallBack() {
                    @Override
                    public void onFromSinaClick() {
                        weiboShare();
                        shareBottomBar.dismiss();
                    }

                    @Override
                    public void onFromWechatClick() {
                        wechatShare();
                        shareBottomBar.dismiss();
                    }

                    @Override
                    public void onFromCircleClick() {
                        circleShare();
                        shareBottomBar.dismiss();
                    }

                    @Override
                    public void onFromQQClick() {
                        qqShare();
                        shareBottomBar.dismiss();
                    }

                    @Override
                    public void onFromQzoneClick() {
                        qzoneShare();
                        shareBottomBar.dismiss();
                    }

                    @Override
                    public void onFromSmsClick() {
                        smsShare();
                        shareBottomBar.dismiss();
                    }

                    @Override
                    public void onSelectCancelBtnClick() {

                        shareBottomBar.dismiss();
                    }
                });

    }

    private void smsShare() {

        ShortMessage.ShareParams sp = new ShortMessage.ShareParams();
        sp.setText(shareContentStr);
        Platform sms = ShareSDK.getPlatform(ShortMessage.NAME);
        sms.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.i(TAG, "dx分享成功咯");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.i(TAG, "dx分享失败咯" + i + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.i(TAG, "dx分享取消咯");
            }
        });
        sms.share(sp);
    }

    private void circleShare() {

        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setText(shareContentStr);
        sp.setTitle(shareTitleStr);
        sp.setUrl(shareWebpage);

        File imageFile = new File(shareImagePath);
        if (imageFile.exists()) {
            sp.setImagePath(imageFile.getPath());
        } else {
            sp.setImageUrl(shareImageUrl);
        }
        Platform wechatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
        wechatMoments.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.i(TAG, "wxc分享成功咯");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.i(TAG, "wxc分享失败咯" + i + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.i(TAG, "wxc分享取消咯");
            }
        });
        wechatMoments.share(sp);
    }

    private void wechatShare() {
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setText(shareContentStr);
        sp.setTitle(shareTitleStr);
        sp.setUrl(shareWebpage);

        File imageFile = new File(shareImagePath);
        if (imageFile.exists()) {
            sp.setImagePath(imageFile.getPath());
        } else {
            sp.setImageUrl(shareImageUrl);
        }
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.i(TAG, "wx分享成功咯");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.i(TAG, "wx分享失败咯" + i + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.i(TAG, "wx分享取消咯");
            }
        });
        wechat.share(sp);
    }

    private void weiboShare() {
        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
        sp.setText(shareContentStr);
        File imageFile = new File(shareImagePath);
        if (imageFile.exists()) {
            sp.setImagePath(imageFile.getPath());
        } else {
            sp.setImageUrl(shareImageUrl);
        }

        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.i(TAG, "wb分享成功咯");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.i(TAG, "wb分享失败咯" + i + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.i(TAG, "wb分享取消咯");
            }
        });
        // 执行图文分享
        weibo.share(sp);
    }

    private void qqShare() {
        QQ.ShareParams qqSP = new QQ.ShareParams();
        qqSP.setTitle(shareTitleStr);
//        qqSP.setTitleUrl("http://blog.csdn.net/diyangxia"); // 标题的超链接
        qqSP.setText(shareContentStr);
        qqSP.setImageUrl(shareImageUrl);
        qqSP.setSite(getResources().getString(R.string.app_name));
        qqSP.setSiteUrl(shareWebpage);

        Platform qqPlatform = ShareSDK.getPlatform(QQ.NAME);
        qqPlatform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.i(TAG, "qq分享成功咯");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.i(TAG, "qq分享失败咯");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.i(TAG, "qq分享取消咯");
            }
        });
        // 执行图文分享
        qqPlatform.share(qqSP);
    }

    private void qzoneShare() {
        QZone.ShareParams sp = new QZone.ShareParams();
        sp.setTitle(shareTitleStr);
//        sp.setTitleUrl("http://blog.csdn.net/diyangxia"); // 标题的超链接
        sp.setText(shareContentStr);
        sp.setImageUrl(shareImageUrl);
        sp.setSite(getResources().getString(R.string.app_name));
        sp.setSiteUrl(shareWebpage);

        Platform qzone = ShareSDK.getPlatform(QZone.NAME);
        qzone.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.i(TAG, "qz分享成功咯");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.i(TAG, "qz分享失败咯");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.i(TAG, "qz分享取消咯");
            }
        });
        // 执行图文分享
        qzone.share(sp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aboutus_version_layout:
                checkVersion();
                break;
            case R.id.aboutus_market_layout:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.aboutus_feedback_layout:
                startActivity(new Intent(AboutActivity.this, FeedbackActivity.class));
                break;
            case R.id.aboutus_guide_layout:
                startActivity(new Intent(AboutActivity.this, GuideActivity.class).putExtra("fromabout", true));
                break;
            case R.id.aboutus_share_layout:
                ShareSDK.initSDK(this);
                showSharePopup(v);
                break;
            case R.id.aboutus_me_layout:
                View view = LayoutInflater.from(AboutActivity.this).inflate(R.layout.dialog_about_us, null);
                MaterialDialog.Builder builder = new MaterialDialog.Builder(AboutActivity.this)
                        .customView(view, false);
                final TextView qqTV = (TextView) view.findViewById(R.id.setting_qq_text);
                final TextView csdnTV = (TextView) view.findViewById(R.id.setting_csdn_text);
                final TextView githubTV = (TextView) view.findViewById(R.id.setting_github_text);
                Button submitBtn = (Button) view.findViewById(R.id.setting_me_submit);
                final MaterialDialog dialog = builder.build();
                dialog.show();

                qqTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url11 = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqTV.getText().toString().trim() + "&version=1";
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url11)));
                    }
                });
                csdnTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(csdnTV.getText().toString().trim());
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));

                    }
                });
                githubTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(githubTV.getText().toString().trim());
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                });
                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                break;
            case R.id.aboutus_protocol_tv:
                startActivity(new Intent(AboutActivity.this, RegisterProtocolActivity.class));
                break;


        }
    }

    private void checkVersion() {

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
                    if (latestVersionName.toLowerCase().contains("v")) {
                        latestVersionName = latestVersionName.replace("v", "");
                    }
                    if (currentVersionName.equals(latestVersionName)) {
                        CommonUtils.showToast(AboutActivity.this, "当前为最新版本");
                    } else {
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

    private void showVersionUpdateDialog() {
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
