package com.xmliu.itravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.CheckBox;
import com.rey.material.widget.EditText;
import com.xmliu.itravel.BaseActivity;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.LogUtil;
import com.xmliu.itravel.utils.StringUtils;

import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 首页
 * Created by Admin on 2015/11/24.
 */
public class RegisterActivity extends BaseActivity {


    private EditText mPhoneET;
    private EditText mCodeET;
    private EditText mPasswordET;

    private Button mResetBtn;
    private Button mSendBtn;
    private CheckBox mPasswordCB;
    private Button mRegBtn;
    private TimeCount time;
    private ImageButton mBackBtn;
    private TextView mBackTV;
    private TextView mTitleTV;
    private boolean isForgetPass = false;

    private LinearLayout mProtocolLayout = null;
    private TextView mProtocolTV;
    private CheckBox mProtocolCB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        LogUtil.e(TAG, "onCreate");
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int event = msg.arg1;
                Object data = msg.obj;
                switch (msg.what) {
                    case SMSSDK.RESULT_COMPLETE:
                        CommonUtils.hideProgressDialog();
                        // 短信注册成功后
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            // 验证验证码成功
                            onRegister();

                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            time.start();// 开始倒计时
                            CommonUtils.showToast(RegisterActivity.this, "验证码已经发送");
                            mPhoneET.setTextColor(getResources().getColor(R.color.grey));
                            mPhoneET.setEnabled(false);
                        }
                        break;
                    case SMSSDK.RESULT_ERROR:
//                        ((Throwable) data).printStackTrace();
                        CommonUtils.hideProgressDialog();
                        try {
                            Throwable throwable = (Throwable) data;
                            throwable.printStackTrace();
                            JSONObject object = new JSONObject(throwable.getMessage());
                            String des = object.optString("detail");//错误描述
                            int status = object.optInt("status");//错误代码
                            if (status > 0 && !TextUtils.isEmpty(des)) {
                                Toast.makeText(RegisterActivity.this, des, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            CommonUtils.showToast(RegisterActivity.this, "验证码验证失败");
                        } catch (Exception e) {
                            //do something
                        }

                        break;
                }
            }
        };

        mCodeET = (EditText) findViewById(R.id.id_code_et);
        mPhoneET = (EditText) findViewById(R.id.id_phone_et);
        mRegBtn = (Button) findViewById(R.id.id_reg_btn);
        mPasswordET = (EditText) findViewById(R.id.id_password_et);
        mResetBtn = (Button) findViewById(R.id.id_reset_btn);
        mSendBtn = (Button) findViewById(R.id.id_send_btn);
        mPasswordCB = (CheckBox) findViewById(R.id.chat_register_password_checkbox);
        mBackBtn = (ImageButton) findViewById(R.id.common_back_btn);
        mBackTV = (TextView) findViewById(R.id.common_back_tv);
        mTitleTV = (TextView) findViewById(R.id.regsiter_title);
        mProtocolTV = (TextView) findViewById(R.id.register_protocol_tv);
        mProtocolLayout = (LinearLayout) findViewById(R.id.register_protocol_checkbox_layout);
        mProtocolCB = (CheckBox) findViewById(R.id.register_protocol_checkbox);

        isForgetPass = this.getIntent().getBooleanExtra("resetpass", false);//从引导页过来

        mBackBtn.setVisibility(View.VISIBLE);
        mBackTV.setVisibility(View.GONE);

        if (isForgetPass) {
            mProtocolLayout.setVisibility(View.GONE);
            mRegBtn.setVisibility(View.GONE);
            mResetBtn.setVisibility(View.VISIBLE);
            mTitleTV.setText("找回密码");
        } else {
            mRegBtn.setVisibility(View.VISIBLE);
            mResetBtn.setVisibility(View.GONE);
            mTitleTV.setText("注册");
        }
        mBackTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mProtocolLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProtocolCB.isChecked()) {
                    mProtocolCB.setChecked(false);
                    mProtocolTV.setTextColor(ContextCompat.getColor(RegisterActivity.this, R.color.grey));
                } else {
                    mProtocolCB.setChecked(true);
                    mProtocolTV.setTextColor(ContextCompat.getColor(RegisterActivity.this, R.color.colorPrimary));
                }
            }
        });
        EventHandler eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.what = result;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        };
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
        time = new TimeCount(60000, 1000);// 构造CountDownr对象

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mPhoneStr = mPhoneET.getText().toString().trim()
                        .replaceAll(" ", "");
                if (StringUtils.isBlank(mPhoneStr)) {
                    CommonUtils.showToast(RegisterActivity.this, "手机号不能为空");
                } else if (!StringUtils.isMobileNumber(mPhoneStr)) {
                    CommonUtils.showToast(RegisterActivity.this, "手机号输入有误");
                } else {
                    // 先查询手机号是否已注册
                    BmobQuery<UserBean> bmobQuery = new BmobQuery<UserBean>();
                    //查询mobile叫mPhoneStr的数据
                    bmobQuery.addWhereEqualTo("username", mPhoneStr);
                    bmobQuery.findObjects(RegisterActivity.this, new FindListener<UserBean>() {
                        @Override
                        public void onSuccess(List<UserBean> object) {
                            // TODO Auto-generated method stub

                            if (object.size() > 0) {
                                Toast.makeText(RegisterActivity.this, "该手机号已注册，请直接登录", Toast.LENGTH_SHORT).show();
                                mCodeET.setText("");
                                mPasswordET.setText("");
                            } else {
                                mSendBtn.setText("获取中...");
                                mSendBtn.setEnabled(false);
                                SMSSDK.getVerificationCode("86", mPhoneET.getText().toString().trim().replaceAll(" ", ""));

                            }

                        }

                        @Override
                        public void onError(int code, String msg) {
                            // TODO Auto-generated method stub
                            Toast.makeText(RegisterActivity.this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            }
        });

        mPasswordCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPasswordCB.setChecked(true);
                    //动态设置密码是否可见
                    mPasswordET
                            .setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());
                } else {
                    mPasswordCB.setChecked(false);
                    mPasswordET
                            .setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());
                }
                mPasswordET.setSelection(mPasswordET.getText().toString().length());//设置光标位置在文本框末尾
            }
        });


//        mPhoneET.addTextChangedListener(new MyTextWatcher(mPhoneET));
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "reging");
                final String mPhoneStr = mPhoneET.getText().toString().trim()
                        .replaceAll(" ", "");
                final String mPasswordStr = mPasswordET.getText().toString().trim();
                final String mCodeStr = mCodeET.getText().toString().trim();

                if (StringUtils.isBlank(mPhoneStr)) {
                    CommonUtils.showToast(RegisterActivity.this, "手机号不能为空");
                } else if (!StringUtils.isMobileNumber(mPhoneStr)) {
                    CommonUtils.showToast(RegisterActivity.this, "手机号输入格式有误");
                } else if (StringUtils.isEmpty(mPasswordStr)) {
                    CommonUtils.showToast(RegisterActivity.this, "密码不能为空");
                } else if (StringUtils.isEmpty(mCodeStr)) {
                    CommonUtils.showToast(RegisterActivity.this, "验证码不能为空");
                } else if (!mProtocolCB.isChecked()) {
                    CommonUtils.showToast(RegisterActivity.this, "您还未同意用户协议");
                } else {
                    CommonUtils.showProgressDialog(RegisterActivity.this, "正在注册");
                    // 先查询手机号是否已注册
                    BmobQuery<UserBean> bmobQuery = new BmobQuery<UserBean>();
                    //查询mobile叫mPhoneStr的数据
                    bmobQuery.addWhereEqualTo("username", mPhoneStr);
                    bmobQuery.findObjects(RegisterActivity.this, new FindListener<UserBean>() {
                        @Override
                        public void onSuccess(List<UserBean> object) {
                            // TODO Auto-generated method stub

                            if (object.size() > 0) {
                                CommonUtils.hideProgressDialog();
                                Toast.makeText(RegisterActivity.this, "该手机号已注册，请直接登录", Toast.LENGTH_SHORT).show();
                                mCodeET.setText("");
                                mPasswordET.setText("");
                            } else {
                                SMSSDK.submitVerificationCode("86", mPhoneET.getText()
                                        .toString().replaceAll(" ", ""), mCodeStr);
                            }

                        }

                        @Override
                        public void onError(int code, String msg) {
                            CommonUtils.hideProgressDialog();
                            Toast.makeText(RegisterActivity.this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "reset");
                final String mPhoneStr = mPhoneET.getText().toString().trim()
                        .replaceAll(" ", "");
                final String mPasswordStr = mPasswordET.getText().toString().trim();
                final String mCodeStr = mCodeET.getText().toString().trim();
                if (StringUtils.isBlank(mPhoneStr)) {
                    CommonUtils.showToast(RegisterActivity.this, "手机号不能为空");
                } else if (!StringUtils.isMobileNumber(mPhoneStr)) {
                    CommonUtils.showToast(RegisterActivity.this, "手机号输入格式有误");
                } else if (StringUtils.isEmpty(mPasswordStr)) {
                    CommonUtils.showToast(RegisterActivity.this, "密码不能为空");
                } else if (StringUtils.isEmpty(mCodeStr)) {
                    CommonUtils.showToast(RegisterActivity.this, "验证码不能为空");
                } else {
                    // 先查询手机号是否已注册
                    BmobQuery<UserBean> bmobQuery = new BmobQuery<UserBean>();
                    //查询mobile叫mPhoneStr的数据
                    bmobQuery.addWhereEqualTo("username", mPhoneStr);
                    bmobQuery.findObjects(RegisterActivity.this, new FindListener<UserBean>() {
                        @Override
                        public void onSuccess(List<UserBean> object) {

                            if (object.size() == 1) {
                                String mPasswordStr = mPasswordET.getText().toString().trim();
                                UserBean userBean = new UserBean();
                                userBean.setPassword(mPasswordStr);
                                userBean.update(RegisterActivity.this, object.get(0).getObjectId(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(RegisterActivity.this, "重置密码成功", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(RegisterActivity.this, "更新失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, "更新失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(int code, String msg) {
                            // TODO Auto-generated method stub
                            Toast.makeText(RegisterActivity.this, "注册失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();

    }

    private void onRegister() {
        final String phone = mPhoneET.getText().toString().trim()
                .replaceAll(" ", "");
        final String password = mPasswordET.getText().toString().trim();
        final String code = mCodeET.getText().toString().trim();
        UserBean userBean = new UserBean();
        userBean.setPassword(password);
        userBean.setUsername(phone);
        userBean.setCode(Integer.parseInt(code));
        userBean.setArea("");
        userBean.setAvatar("http://file.bmob.cn/M02/79/0B/oYYBAFawXZOALjy5AAACPGs5RvU833.png");
        userBean.setBgurl("http://file.bmob.cn/M02/79/0B/oYYBAFawXXqAAX_OAAhPzr3vBNo866.png");
        userBean.setGender(1);
        userBean.setNickname("");
        userBean.setRid(0);
        userBean.setSignature("");
        userBean.signUp(RegisterActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                Log.i("TAG", "reg success");
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                Log.i("TAG", i + "reg fail" + s);
                if (i == 202) {
                    CommonUtils.showToast(RegisterActivity.this, "该用户已注册");
                } else {
                    CommonUtils.showToast(RegisterActivity.this, "注册失败");
                }
            }
        });
    }

    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            mSendBtn.setClickable(true);
            mSendBtn.setText("重新获取");
            // mRegetCodeBtn.setVisibility(View.VISIBLE);
            // mCodeBtn.setVisibility(View.GONE);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            mSendBtn.setClickable(false);
            mSendBtn.setText(millisUntilFinished / 1000 + "s");
        }
    }
}
