package com.xmliu.itravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.ImageBean;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.imagemvp.ImagePresenter;
import com.xmliu.itravel.imagemvp.ImagePresenterImpl;
import com.xmliu.itravel.imagemvp.UploadImageView;
import com.xmliu.itravel.mvp.UserPresenter;
import com.xmliu.itravel.mvp.UserPresenterImpl;
import com.xmliu.itravel.mvp.UserView;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.LogUtil;
import com.xmliu.itravel.utils.StringUtils;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by xmliu on 2016/1/31.
 */
public class UserInfoEditActivity extends ToolbarActivity implements UserView,UploadImageView {

    private CircleImageView avatarIV;
    private ImageView userbgIV;
    private TextView nicknameTV;
    private TextView genderTV;
    private TextView areaTV;
    private MaterialEditText signatureET;

    private RelativeLayout avatarLayout;
    private RelativeLayout userbgLayout;
    private RelativeLayout nicknameLayout;
    private RelativeLayout genderLayout;
    private RelativeLayout areaLayout;

    private String nicknameStr;
    private String avatarUrl;
    private String userbgUrl;
    private String areaStr;
    private String signatureStr;
    private int gender = 1; // 默认是男，2代表女

    private static final int REQUEST_CODE_ALBUM = 200;
    private static final int REQUEST_CODE_CAPTURE = 600;

    private int fromWhere = 0; // 1 头像； 2 背景
    private String newImgStr;

    private UserPresenter userPresenter;
    private ImagePresenter imagePresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        toolbar.setTitle("用户资料编辑");

        avatarIV = (CircleImageView) findViewById(R.id.user_info_avatar_iv);
        userbgIV = (ImageView) findViewById(R.id.user_info_userbg_iv);
        signatureET = (MaterialEditText) findViewById(R.id.user_info_signature_et);
        nicknameTV = (TextView) findViewById(R.id.user_info_nickname_tv);
        genderTV = (TextView) findViewById(R.id.user_info_gender_tv);
        areaTV = (TextView) findViewById(R.id.user_info_area_tv);
        areaLayout = (RelativeLayout) findViewById(R.id.user_info_area_layout);
        genderLayout = (RelativeLayout) findViewById(R.id.user_info_gender_layout);
        nicknameLayout = (RelativeLayout) findViewById(R.id.user_info_nickname_layout);
        userbgLayout = (RelativeLayout) findViewById(R.id.user_info_userbg_layout);
        avatarLayout = (RelativeLayout) findViewById(R.id.user_info_avatar_layout);

        userPresenter = new UserPresenterImpl(this);
        imagePresenter = new ImagePresenterImpl(this);

        userPresenter.getUser(UserBean.getCurrentUser(UserInfoEditActivity.this).getUsername());

        avatarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromWhere = 1;
                startActivityForResult(
                        new Intent(UserInfoEditActivity.this, SelectPhotoActivity.class).putExtra("num", 1),
                        REQUEST_CODE_ALBUM);
            }
        });
        userbgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromWhere = 2;
                startActivityForResult(
                        new Intent(UserInfoEditActivity.this, SelectPhotoActivity.class).putExtra("num", 1),
                        REQUEST_CODE_ALBUM);
            }
        });

        nicknameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(UserInfoEditActivity.this)
                        .title("请输入昵称")
//                        .content("请输入昵称")
                        .inputType(InputType.TYPE_CLASS_TEXT )
                        .input("昵称，不少于8个字", nicknameStr, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                nicknameTV.setText(input);
                                nicknameStr=input.toString();
                            }
                        }).show();
            }
        });

        areaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(UserInfoEditActivity.this)
                        .title("请输入地区")
//                        .content("请输入地区")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("地区，不少于8个字",areaStr , new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                areaTV.setText(input);
                                areaStr=input.toString();
                            }
                        }).show();
            }
        });

        genderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] array = new String[]{"男","女"};
                new MaterialDialog.Builder(UserInfoEditActivity.this)
                        .title("选择性别")
                        .items(array)
                        .itemsCallbackSingleChoice(gender-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                genderTV.setText(text.toString());
                                gender = which+1;
//                                CommonUtils.showToast(UserInfoEditActivity.this,which + text.toString());
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                return true;
                            }
                        })
                        .positiveText("选择")
                        .show();
            }
        });
    }

    private void UpdateInfo() {
        UserBean newUser = new UserBean();
        newUser.setAvatar(avatarUrl);
        newUser.setBgurl(userbgUrl);
        newUser.setNickname(nicknameStr);
        newUser.setGender(gender);
        newUser.setArea(areaStr);
        newUser.setSignature(signatureStr);
        BmobUser bmobUser = UserBean.getCurrentUser(UserInfoEditActivity.this);
        newUser.update(this, bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                CommonUtils.showToast(UserInfoEditActivity.this, "更新用户信息成功");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(int code, String msg) {
                CommonUtils.showToast(UserInfoEditActivity.this, "更新用户信息失败," + msg);
            }
        });
    }

    private void insertImage(BmobObject bmobObject) {

        bmobObject.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                LogUtil.i(TAG, "图片保存成功");
                CommonUtils.hideProgressDialog();
                if (fromWhere == 1) {
                    avatarUrl = newImgStr;
                    ImageLoader.getInstance().displayImage(avatarUrl, avatarIV);
                } else if (fromWhere == 2) {
                    userbgUrl = newImgStr;
                    ImageLoader.getInstance().displayImage(userbgUrl, userbgIV);
                }
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.i(TAG, "图片保存失败" + s);
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
        } else if (id == R.id.action_save) {
            areaStr = areaTV.getText().toString();
            signatureStr = signatureET.getText().toString();
            UpdateInfo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ALBUM && data!= null && resultCode == RESULT_OK) {
            // 相册返回
            LogUtil.e(TAG, "REQUEST_CODE_ALBUM HAS BEEN CALLED");
            imagePresenter.uploadImage(data.getStringExtra("imagePath"));
        }
        if (resultCode == REQUEST_CODE_CAPTURE) {
            // 拍照返回
            LogUtil.e(TAG, "REQUEST_CODE_CAPTURE HAS BEEN CALLED");
            Bundle b = data.getExtras(); // data为B中回传的Intent
            imagePresenter.uploadImage(b.getString("cameraPath"));// str即为回传的值
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return true;
    }

    @Override
    public void showLoading() {
        CommonUtils.showProgressDialog(this,"数据加载中");
    }

    @Override
    public void hideLoading() {
        CommonUtils.hideProgressDialog();
    }

    @Override
    public void showError(String reason) {

        CommonUtils.showToast(this, reason);
    }

    @Override
    public void setImageInfo(BmobFile imageFile) {
        newImgStr = imageFile.getFileUrl(UserInfoEditActivity.this);
        insertImage(new ImageBean(imageFile.getUrl(), System.currentTimeMillis() + "", imageFile));
    }

    @Override
    public void setNoteInfo(UserBean userBean) {
        avatarUrl = userBean.getAvatar();
        ImageLoader.getInstance().displayImage(avatarUrl, avatarIV);
        userbgUrl = userBean.getBgurl();
        ImageLoader.getInstance().displayImage(userbgUrl, userbgIV);
        nicknameStr = userBean.getNickname();
        if (StringUtils.isBlank(nicknameStr)) {
            nicknameTV.setText("你的昵称");
        } else {
            nicknameTV.setText(nicknameStr);
        }
        gender = userBean.getGender();
        if (gender == 1) {
            genderTV.setText("男");
        } else {
            genderTV.setText("女");
        }
        areaStr = userBean.getArea();
        if (StringUtils.isBlank(areaStr)) {
            areaTV.setText("江苏 苏州");
        } else {
            areaTV.setText(areaStr);
        }
        signatureStr = userBean.getSignature();
        if (StringUtils.isBlank(signatureStr)) {
            signatureET.setText("");
        } else {
            signatureET.setText(signatureStr);
        }
    }
}
