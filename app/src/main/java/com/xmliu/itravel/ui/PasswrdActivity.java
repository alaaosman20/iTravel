package com.xmliu.itravel.ui;

import android.os.Bundle;
import android.view.View;

import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.xmliu.itravel.R;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.StringUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Date: 2016/2/2 18:13
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class PasswrdActivity extends ToolbarActivity {

    private EditText oldpassET;
    private EditText newpassET;
    private EditText newpass2ET;
    private Button submitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        toolbar.setTitle("修改密码");

        oldpassET = (EditText) findViewById(R.id.password_old_et);
        newpassET = (EditText) findViewById(R.id.password_new_et);
        newpass2ET = (EditText) findViewById(R.id.password_new2_et);
        submitBtn = (Button) findViewById(R.id.password_submit_btn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldStr = oldpassET.getText().toString().trim();
                String newStr = newpassET.getText().toString().trim();
                String newStr2 = newpass2ET.getText().toString().trim();
                if(StringUtils.isBlank(oldStr)){
                    CommonUtils.showToast(PasswrdActivity.this, "旧密码不能为空");
                }else  if(StringUtils.isBlank(newStr) || StringUtils.isBlank(oldStr)){
                    CommonUtils.showToast(PasswrdActivity.this, "新密码不能为空");
                }else  if(!StringUtils.isEqual(newStr, newStr2)){
                    CommonUtils.showToast(PasswrdActivity.this, "两次新密码不一致");
                }else if(StringUtils.isEqual(newStr,oldStr)){
                    CommonUtils.showToast(PasswrdActivity.this, "新旧密码不能相同");
                }else{
                    updatePassword(oldStr,newStr);
                }
            }
        });


    }

    private void updatePassword(String oldpass,String newPass){
        BmobUser.updateCurrentUserPassword(this, oldpass, newPass, new UpdateListener() {

            @Override
            public void onSuccess() {

                CommonUtils.showToast(PasswrdActivity.this, "密码修改成功，可以用新密码进行登录啦");
            }

            @Override
            public void onFailure(int code, String msg) {
                CommonUtils.showToast(PasswrdActivity.this,"密码修改失败：" + msg + "(" + code + ")");
            }
        });
    }
}
