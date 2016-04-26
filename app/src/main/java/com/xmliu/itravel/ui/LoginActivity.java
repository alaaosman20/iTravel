package com.xmliu.itravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rey.material.widget.EditText;
import com.xmliu.itravel.BaseActivity;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.utils.AppManager;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.LogUtil;
import com.xmliu.itravel.utils.StringUtils;

import cn.bmob.v3.listener.SaveListener;

/**
 * 登录页
 * Created by Admin on 2015/11/19.
 */
public class LoginActivity extends BaseActivity {

    private EditText mPhoneET;
    private EditText mPasswordET;
    private Button mLoginBtn;
    private TextView mRegTV;
    private TextView mResetTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LogUtil.e(TAG, "onCreate");

        mPhoneET = (EditText) findViewById(R.id.id_phone_et);
        mPasswordET = (EditText) findViewById(R.id.id_password_et);

//        mPhoneET.setError("手机号码不正确");
        mLoginBtn = (Button) findViewById(R.id.id_login_btn);
        mRegTV = (TextView) findViewById(R.id.id_reg_tv);
        mResetTV = (TextView) findViewById(R.id.id_reset_tv);
//        mPhoneET.addTextChangedListener(new MyTextWatcher(mPhoneET));
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mPhoneStr = mPhoneET.getText().toString().trim()
                        .replaceAll(" ", "");
                String mPasswordStr = mPasswordET.getText().toString().trim();
                if (StringUtils.isBlank(mPhoneStr)) {
                    CommonUtils.showToast(LoginActivity.this, "手机号不能为空");
                } else if (!StringUtils.isMobilePhone(mPhoneStr)) {
                    CommonUtils.showToast(LoginActivity.this, "手机号输入有误");
                } else if (StringUtils.isBlank(mPasswordStr)) {
                    CommonUtils.showToast(LoginActivity.this, "密码不能为空");
                } else {
                    CommonUtils.showProgressDialog(LoginActivity.this, "正在登陆");
                    UserBean bu2 = new UserBean();
                    bu2.setUsername(mPhoneStr);
                    bu2.setPassword(mPasswordStr);
                    bu2.login(LoginActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            CommonUtils.hideProgressDialog();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                            LoginActivity.this.finish();
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            CommonUtils.hideProgressDialog();
                            CommonUtils.showToast(LoginActivity.this, "登录失败，" + msg + "，错误码为" + code);
                        }
                    });
                }
            }
        });

        mRegTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class).putExtra("resetpass", false));
            }
        });
        mResetTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class).putExtra("resetpass", true));
            }
        });

    }

    @Override
    public void onBackPressed() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("应用提示")
                .content("确定退出" + getResources().getString(R.string.app_name) + "吗?")
                .positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        AppManager.getInstance().killAllActivity();
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
