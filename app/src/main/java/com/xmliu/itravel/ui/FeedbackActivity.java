package com.xmliu.itravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsCallback;
import com.gc.materialdesign.views.ButtonRectangle;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.FeedbackBean;
import com.xmliu.itravel.bean.ImageBean;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.LogUtil;
import com.xmliu.itravel.utils.StringUtils;

import java.io.File;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Date: 2016/1/13-14-14:12
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class FeedbackActivity extends ToolbarActivity {

    private String newImgStr = null;

    private ImageView addImageIV;
    private ImageView imageIV;
    private ImageView deleteIV;
    private ButtonRectangle submitBtn;
    private EditText contentET;
    private EditText phoneET;
    private static final int REQUEST_CODE_ALBUM = 200;
    private static final int REQUEST_CODE_CAPTURE = 600;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_add);

        toolbar.setTitle("意见反馈");

        addImageIV = (ImageView) findViewById(R.id.id_feedback_upload_iv);
        imageIV = (ImageView) findViewById(R.id.id_feedback_display_iv);
        deleteIV = (ImageView) findViewById(R.id.id_feedback_delete_iv);
        submitBtn = (ButtonRectangle) findViewById(R.id.id_feedback_submit_btn);
        contentET = (EditText) findViewById(R.id.id_feedback_content_et);
        phoneET = (EditText) findViewById(R.id.feedback_phone_et);

        addImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(FeedbackActivity.this, SelectPhotoActivity.class).putExtra("num", 1),
                        REQUEST_CODE_ALBUM);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = contentET.getText().toString().trim();
                String phone = phoneET.getText().toString().trim();
                if (StringUtils.isBlank(content)) {
                    CommonUtils.showToast(FeedbackActivity.this, "反馈内容不能为空");
                } else {
                    Bugtags.setUserData("name",phone);
                    Bugtags.setUserData("image",newImgStr);
                    Bugtags.sendFeedback(content);
                    Bugtags.setAfterSendingCallback(new BugtagsCallback() {
                        @Override
                        public void run() {
                            CommonUtils.showToast(FeedbackActivity.this, "反馈成功");
                            finish();
                        }
                    });
//                    onFeedBack(content);
                }
            }
        });

        deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newImgStr = null;
                addImageIV.setVisibility(View.VISIBLE);
                imageIV.setVisibility(View.GONE);
                deleteIV.setVisibility(View.GONE);
            }
        });

    }

    private void onFeedBack(String content) {
        FeedbackBean feedbackBean = new FeedbackBean();
        feedbackBean.setAuthor(BmobUser.getCurrentUser(FeedbackActivity.this, UserBean.class));
        feedbackBean.setContent(content);
        if (!StringUtils.isBlank(newImgStr)) {
            feedbackBean.setImage(newImgStr);
        }
        feedbackBean.save(FeedbackActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                CommonUtils.showToast(FeedbackActivity.this, "感谢您的建议，我们会尽快处理");
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                CommonUtils.showToast(FeedbackActivity.this, "提交失败");

            }
        });
    }

    private void uploadImage(String imagePath) {
        CommonUtils.showProgressDialog(FeedbackActivity.this, "正在上传图片...");
        final File file = new File(imagePath);
        final BmobFile imageFile = new BmobFile(file);
        imageFile.uploadblock(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                LogUtil.i(TAG, "图片上传成功，地址：" + imageFile.getFileUrl(FeedbackActivity.this));
                newImgStr = imageFile.getFileUrl(FeedbackActivity.this);
                insertObject(new ImageBean(imageFile.getUrl(), System.currentTimeMillis() + "", imageFile));
            }

            @Override
            public void onFailure(int i, String s) {
                CommonUtils.hideProgressDialog();
                LogUtil.i(TAG, "图片上传失败" + s);
            }
        });

    }

    private void insertObject(BmobObject bmobObject) {

        bmobObject.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                LogUtil.i(TAG, "图片保存成功");
                CommonUtils.hideProgressDialog();
                addImageIV.setVisibility(View.GONE);
                imageIV.setVisibility(View.VISIBLE);
                deleteIV.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(newImgStr, imageIV);
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.i(TAG, "图片保存失败" + s);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ALBUM && data!= null && resultCode == RESULT_OK) {
            // 相册返回
            LogUtil.e(TAG, "REQUEST_CODE_ALBUM HAS BEEN CALLED");
            uploadImage( data.getStringExtra("imagePath"));
        }
        if (resultCode == REQUEST_CODE_CAPTURE) {
            // 拍照返回
            LogUtil.e(TAG, "REQUEST_CODE_CAPTURE HAS BEEN CALLED");
            Bundle b = data.getExtras(); // data为B中回传的Intent
            uploadImage(b.getString("cameraPath"));// str即为回传的值
        }
    }

}
