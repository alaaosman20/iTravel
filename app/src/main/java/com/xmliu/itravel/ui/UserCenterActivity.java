package com.xmliu.itravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.widget.Button;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.LogUtil;
import com.xmliu.itravel.utils.StringUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by xmliu on 2016/1/31.
 */
public class UserCenterActivity extends ToolbarActivity {
    private String avatarUrl;
    private String nicknameStr;
    private String signatureStr;
    private String userbgUrl;
    private String userId;
    private int genderValue;
    private Button logoutTV;
    private TextView nicknameTV;
    private TextView signatureTV;
    private CircleImageView avatarIV;
    private RelativeLayout mIndexpageLayout;
    private LinearLayout mPasswordLayout;

    private static final int REQUEST_CODE_EDIT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        toolbar.setTitle("用户中心");

        avatarUrl = this.getIntent().getStringExtra("avatar");
        nicknameStr = this.getIntent().getStringExtra("nickname");
        signatureStr = this.getIntent().getStringExtra("signature");
        userbgUrl = this.getIntent().getStringExtra("userbg");
        userId = this.getIntent().getStringExtra("userId");
        genderValue = this.getIntent().getIntExtra("gender", -1);

        logoutTV = (Button) findViewById(R.id.id_user_center_logout_tv);
        nicknameTV = (TextView) findViewById(R.id.user_center_nickname_tv);
        signatureTV = (TextView) findViewById(R.id.user_center_signature_tv);
        avatarIV = (CircleImageView) findViewById(R.id.user_center_avatar_iv);
        mIndexpageLayout = (RelativeLayout) findViewById(R.id.user_center_indexpage_layout);
        mPasswordLayout = (LinearLayout) findViewById(R.id.user_center_password_layout);

        ImageLoader.getInstance().displayImage(avatarUrl, avatarIV);
        if (StringUtils.isBlank(nicknameStr)) {
            nicknameTV.setText("你的昵称");
        } else {
            nicknameTV.setText(nicknameStr);
        }
        if (StringUtils.isBlank(signatureStr)) {
            signatureTV.setText("我的个性签名");
        } else {
            signatureTV.setText(signatureStr);
        }

        logoutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showExitDialog(UserCenterActivity.this);
            }
        });
        mIndexpageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CommonUtils.showToast(UserCenterActivity.this, "进入个人主页（可编辑资料）");
                startActivity(new Intent(UserCenterActivity.this, IndexPageActivity.class)
                        .putExtra("userId", userId)
                );
                LogUtil.i("UserCenterActivity", "onclick");
            }
        });
        mPasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserCenterActivity.this, PasswrdActivity.class));
            }
        });
    }

    private void loadUserData() {
        // 先查询手机号是否已注册
        BmobQuery<UserBean> bmobQuery = new BmobQuery<>();
        //查询mobile叫mPhoneStr的数据
        bmobQuery.addWhereEqualTo("username", UserBean.getCurrentUser(UserCenterActivity.this).getUsername());
        bmobQuery.findObjects(UserCenterActivity.this, new FindListener<UserBean>() {
            @Override
            public void onSuccess(List<UserBean> object) {
                // TODO Auto-generated method stub

                if (object.size() > 0) {
                    UserBean userBean = object.get(0);
                    avatarUrl = userBean.getAvatar();
                    ImageLoader.getInstance().displayImage(avatarUrl, avatarIV);
                    nicknameStr = userBean.getNickname();
                    if (StringUtils.isBlank(nicknameStr)) {
                        nicknameTV.setText("你的昵称");
                    } else {
                        nicknameTV.setText(nicknameStr);
                    }
                    signatureStr = userBean.getSignature();
                    if (StringUtils.isBlank(signatureStr)) {
                        signatureTV.setText("个性签名");
                    } else {
                        signatureTV.setText(signatureStr);
                    }
                    genderValue = userBean.getGender();
                    userbgUrl = userBean.getBgurl();
                } else {
                    CommonUtils.showToast(UserCenterActivity.this, "查询失败，用户不存在");
                }

            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                CommonUtils.showToast(UserCenterActivity.this, "查询失败，用户不存在," + msg);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_edit) {
            startActivityForResult(new Intent(this, UserInfoEditActivity.class), REQUEST_CODE_EDIT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_center, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {

            loadUserData();
        }
    }
}
