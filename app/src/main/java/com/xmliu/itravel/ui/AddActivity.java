package com.xmliu.itravel.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.widget.Button;
import com.rey.material.widget.CheckBox;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.ImageBean;
import com.xmliu.itravel.bean.NoteBean;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.utils.BaseRecyclerViewAdapter;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.LogUtil;
import com.xmliu.itravel.utils.RecyclerHolder;
import com.xmliu.itravel.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by xmliu on 2016/1/24.
 */
public class AddActivity extends ToolbarActivity {

    private static final int REQUEST_CODE_ALBUM = 200;
    private static final int REQUEST_CODE_CAPTURE = 600;
    private static final int REQUEST_CODE_LOCATION = 800;
    private String newImgStr = null;

    private Button submitBtn;
    private EditText contentET;
    private CheckBox mPermissionCB;
    private TextView mPermissionTV;
    private TextView addressTV;
    private LinearLayout addressLayout;

    private RecyclerView gridRecycleview;
    private AddImageAdapter addAdapter;
    private List<ImageBean> mListData = new ArrayList<>();

    private double latitude = 0;
    private double longitude = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);
        toolbar.setTitle("发布状态");

        gridRecycleview = (RecyclerView) findViewById(R.id.id_add_recycleView);
        submitBtn = (Button) findViewById(R.id.id_addnote_submit_btn);
        contentET = (EditText) findViewById(R.id.id_addnote_content_et);
        addressTV = (TextView) findViewById(R.id.id_addnote_address_tv);
        addressLayout = (LinearLayout) findViewById(R.id.id_addnote_address_layout);
        mPermissionTV = (TextView) findViewById(R.id.id_addnote_permission_tip);
        mPermissionCB = (CheckBox) findViewById(R.id.id_addnote_permission);

        mPermissionCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPermissionTV.setText("公开");
                } else {
                    mPermissionTV.setText("私密");
                }
            }
        });


        addressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddActivity.this,SelectLocationActivity.class),REQUEST_CODE_LOCATION);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = contentET.getText().toString().trim();
                String address = addressTV.getText().toString().trim();
                boolean isopen = mPermissionCB.isChecked();
                if (StringUtils.isBlank(content)) {
                    CommonUtils.showToast(AddActivity.this, "内容不能为空");
                } else {
                    CommonUtils.showProgressDialog(AddActivity.this);
                    onAddNote(content, address, isopen);
                }
            }
        });

        // RecycleView初始化配置
        gridRecycleview.setLayoutManager(new GridLayoutManager(AddActivity.this, 3));
        //设置Item增加、移除动画
        gridRecycleview.setItemAnimator(new DefaultItemAnimator());

        if (mListData.size() == 0) {
            ImageBean bean = new ImageBean();
            bean.setId("" + System.currentTimeMillis());
            mListData.add(bean);
        }
        addAdapter = new AddImageAdapter(AddActivity.this, mListData);
        gridRecycleview.setAdapter(addAdapter);
        addAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("TAG", "点击率" + mListData.get(position).getImage());
            }
        });
    }

    public class AddImageAdapter extends BaseRecyclerViewAdapter {


        public AddImageAdapter(Context context, List<?> list) {
            super(context, list);
        }

        @Override
        public int onCreateViewLayoutID(int viewType) {
            return R.layout.item_note_add_gridview;
        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, int position) {
            ImageBean item = (ImageBean) list.get(position);
            final int pos = position;
            ImageView deleteBtn = holder.getImageView(R.id.product_edit_grid_delete);
            ImageView image = holder.getImageView(R.id.product_edit_grid_image);
            ImageView addIV = holder.getImageView(R.id.product_edit_grid_add_iv);

            if (position == list.size() || StringUtils.isEmpty(item.getImage())) {
                image.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.GONE);
                addIV.setVisibility(View.VISIBLE);
            } else {
                image.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);
                addIV.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(item.getImage(), image);
            }

            addIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(
                            new Intent(AddActivity.this, SelectPhotoActivity.class).putExtra("num", 1),
                            REQUEST_CODE_ALBUM);
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(pos);
                    if(list.size() == 8){

                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void onAddNote(String content, String address, boolean isopen) {
        UserBean author = BmobUser.getCurrentUser(this,UserBean.class);
        NoteBean noteBean = new NoteBean();
        noteBean.setContent(content);
        noteBean.setAddress(address);
        noteBean.setIsopen(isopen);
        noteBean.setAuthor(author);
        noteBean.setLatitude(latitude);
        noteBean.setLongitude(longitude);
        noteBean.setAgreeNum(0);
        noteBean.setCommentNum(0);
        mListData.remove(mListData.size() - 1);
        noteBean.setImageList(mListData);
        noteBean.save(AddActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                CommonUtils.hideProgressDialog();
                CommonUtils.showToast(AddActivity.this, "发表成功");
                setResult(RESULT_OK);
                finish();

            }

            @Override
            public void onFailure(int i, String s) {
                CommonUtils.hideProgressDialog();
                CommonUtils.showToast(AddActivity.this, "提交失败");

            }
        });
    }

    private void uploadImage(String mImagePath) {
        CommonUtils.showProgressDialog(AddActivity.this, "正在上传图片...");
        final File file = new File(mImagePath);
        final BmobFile imageFile = new BmobFile(file);
        imageFile.uploadblock(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                LogUtil.i(TAG, "图片上传成功，地址：" + imageFile.getFileUrl(AddActivity.this));
                newImgStr = imageFile.getFileUrl(AddActivity.this);
                insertObject(new ImageBean(imageFile.getUrl(), System.currentTimeMillis() + "", imageFile));
            }

            @Override
            public void onFailure(int i, String s) {
                CommonUtils.hideProgressDialog();
                CommonUtils.showToast(AddActivity.this, "图片上传失败");
                LogUtil.i(TAG, i + "图片上传失败" + s);

            }
        });

    }

    private void insertObject(BmobObject bmobObject) {

        bmobObject.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                LogUtil.i(TAG, "图片保存成功");
                CommonUtils.hideProgressDialog();

                mListData.remove(mListData.size() - 1);

                ImageBean data = new ImageBean();
                data.setImage(newImgStr);
                mListData.add(data);

                ImageBean bean = new ImageBean();
                bean.setId("" + System.currentTimeMillis());
                mListData.add(bean);

                if (mListData.size() == 10) {
                    // 如果已添加9张图片，直接去掉加号
                    mListData.remove(mListData.size() - 1);
                }
                addAdapter.notifyDataSetChanged();
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
        if(requestCode == REQUEST_CODE_LOCATION && resultCode == RESULT_OK){

            String addressStr = data.getStringExtra("aName");
            latitude = data.getDoubleExtra("lat",0);
            longitude = data.getDoubleExtra("lon",0);
            addressTV.setText(addressStr);

        }
    }
}
