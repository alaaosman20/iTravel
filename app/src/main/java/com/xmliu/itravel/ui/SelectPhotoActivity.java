package com.xmliu.itravel.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.xmliu.itravel.Constants;
import com.xmliu.itravel.R;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.FileUtils;
import com.xmliu.itravel.utils.LogUtil;
import com.xmliu.itravel.utils.selecphoto.AlbumHelper;
import com.xmliu.itravel.utils.selecphoto.ImageBucket;
import com.xmliu.itravel.utils.selecphoto.ImageItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SelectPhotoActivity extends ToolbarActivity {

    //	private static final int REQUEST_CODE_ALBUM = 500;
    private static final int REQUEST_CODE_CAPTURE = 600;

    private List<ImageBucket> mListData = new ArrayList<>();
    private List<ImageItem> mListDetail = new ArrayList<>();

    private ImageView mIcDrop = null;
    private PopupWindow mPopup = null;
    private View popView;
    private ListView mPhotoAlbum_lv = null;
    private LinearLayout mPopupLayout;

    private GridView mPhoto_gv = null;
    private AlbumAdapter mAlbumAdapter;
    private List<String> mSelect = new ArrayList<String>();
//    private Button mAll_btn = null;
    private TextView mTitleTV = null;
    private int totalCount = 0;
    private String mImagePath = null;

    private int num = 0;//0代表选取多张，1代表选取一张，2代表选取既从本地相册又从云相册选择
    private TextView mCount_tv = null;
    private Button mSubmit_btn = null;
    private int totalptotoNum = 0;//最多上传张数
    private Handler mHandler;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        toolbar.setTitle("选择图片");
        initView();

        PauseOnScrollListener listener = new PauseOnScrollListener(
                ImageLoader.getInstance(), true, true);
        mPhoto_gv.setOnScrollListener(listener);
        mPhotoAlbum_lv.setOnScrollListener(listener);
        AlbumHelper.getHelper().init(this);

        mHandler = new Handler(getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                    case Constants.Tags.LOAD_DATA_SUCCESS:
                        mPhotoAlbum_lv.setAdapter(new PhoneAlbumAdapter());
                        ImageItem zero = new ImageItem();
                        zero.imageId = "1234";
                        mListDetail.add(0, zero);
                        if (mListData.size() > 0) {
                            mListDetail.addAll(mListData.get(0).bucketList);
                            mTitleTV.setText(mListData.get(0).bucketName);
                        }
                        mAlbumAdapter = new AlbumAdapter();
                        mPhoto_gv.setAdapter(mAlbumAdapter);
                        break;
                    default:
                        break;
                }
            }
        };
        if (FileUtils.hasSDCard()) {
            new Thread(new Runnable() {
                public void run() {
                    mListData = AlbumHelper.getHelper().getImagesBucketList(false);
                    mHandler.sendEmptyMessage(Constants.Tags.LOAD_DATA_SUCCESS);
                }
            }).start();
        } else {
            CommonUtils.showToast(SelectPhotoActivity.this, "请检查SD卡是否挂载");
            finish();
        }
        mPopupLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mPopup == null) {
                    mPopup = new PopupWindow(popView,
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT, false);
                }
                popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int popupWidth = popView.getMeasuredWidth();
                int popupHeight =  popView.getMeasuredHeight();
                int[] location = new int[2];
                mPopup.setAnimationStyle(R.style.popdown_Animation);
                mPopup.setBackgroundDrawable(new BitmapDrawable());// 点击以外区域以及back键时隐藏popupwindow
                mPopup.setOutsideTouchable(true);
                mPopup.update();
                mPopup.setTouchable(true);
                mPopup.setFocusable(true);
                v.getLocationOnScreen(location);
                mPopup.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2,
                        location[1] - popupHeight);
//                mPopup.showAtLocation(v, Gravity.BOTTOM,0,0);
//                mPopup.showAsDropDown(mTopLayout);
            }
        });
        mSubmit_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSelect.size() != 0) {
                    String imagePath = mListDetail.get(Integer
                            .parseInt(mSelect.get(0))).imagePath;
                    Intent data1 = new Intent();
                    data1.putExtra("imagePath", imagePath);
                    setResult(RESULT_OK, data1);
                    SelectPhotoActivity.this.finish();
                } else {
                    CommonUtils.showToast(SelectPhotoActivity.this, "您还没选择图片");
                }
            }
        });

        mPhotoAlbum_lv.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                mSelect.clear();
                mTitleTV.setText(mListData.get(arg2).bucketName);
                mListDetail.clear();
                ImageItem zero = new ImageItem();
                zero.imageId = "1234";
                mListDetail.add(0, zero);
                mListDetail.addAll(mListData.get(arg2).bucketList);
                Log.i("TAG", "size===>" + mListDetail.size());
                mAlbumAdapter.notifyDataSetChanged();
                mPopup.dismiss();
                if (num != 1) {
                    initCount(0);
                }
            }
        });
        mPhoto_gv.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == 0) {
                    mSelect.clear();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            getCameraTempFile());
                    startActivityForResult(intent, REQUEST_CODE_CAPTURE);
                } else {
                    boolean numformat = (num == 0 || num == 2);
                    if (totalCount <= totalptotoNum && numformat) {
                        if (mSelect.contains(String.valueOf(arg2))) {
                            mSelect.remove(String.valueOf(arg2));
                        } else {
                            mSelect.add(String.valueOf(arg2));
                        }
//                        if (mSelect.size() == mListDetail.size() - 1) {
//                            mAll_btn.setText("反选");
//                        } else {
//                            mAll_btn.setText("全选");
//                        }
                        if (num != 1) {
                            initCount(mSelect.size());
                        }

                    } else {
                        mSelect.clear();
                        mSelect.add(String.valueOf(arg2));
                    }
                    mAlbumAdapter.notifyDataSetChanged();
                }
            }
        });

//        mAll_btn.setOnClickListener(new OnClickListener() {
//
//            public void onClick(View v) {
//                if (mSelect.size() == mListDetail.size() - 1) {
//                    mSelect.clear();
//                } else if (mSelect.size() > 0) {
//                    for (int i = 1; i < mListDetail.size(); i++) {
//                        if (mSelect.contains(String.valueOf(i))) {
//                            continue;
//                        }
//                        mSelect.add(String.valueOf(i));
//                    }
//                } else {
//                    for (int i = 1; i < mListDetail.size(); i++) {
//                        mSelect.add(String.valueOf(i));
//                    }
//                }
//                if (mSelect.size() == mListDetail.size() - 1) {
//                    mAll_btn.setText("反选");
//                } else {
//                    mAll_btn.setText("全选");
//                }
//                if (num != 1) {
//                    initCount(mSelect.size());
//                }
//                mAlbumAdapter.notifyDataSetChanged();
//            }
//        });
    }

    private void initCount(int count) {
        if (totalCount > totalptotoNum) {
            for (int j = count - 1; j >= count - totalCount + totalptotoNum; j--) {
                mSelect.remove(j);
            }
            CommonUtils.showToast(SelectPhotoActivity.this, "一次最多能上传" + totalptotoNum + "张照片");
        }
        if (totalCount > 0) {
            mCount_tv.setText("已选择" + totalCount + "张");
        } else {
            mCount_tv.setText("暂未选择");
        }

    }

    private void initView() {
        num = this.getIntent().getIntExtra("num", 0);
        totalptotoNum = this.getIntent().getIntExtra("totalptotoNum", 0);
        mIcDrop = (ImageView) findViewById(R.id.ic_drop);
        mPhoto_gv = (GridView) findViewById(R.id.album_detail_grid);
//        mAll_btn = (Button) findViewById(R.id.common_right_btn);
        mTitleTV = (TextView) findViewById(R.id.common_title_txt);
        mPopupLayout = (LinearLayout) findViewById(R.id.popup_layout);
        popView = View.inflate(SelectPhotoActivity.this,
                R.layout.popupwindow_selectphoto_listview, null);
        mPhotoAlbum_lv = (ListView) popView.findViewById(R.id.common_listview);
        mCount_tv = (TextView) findViewById(R.id.album_phone_detail_count);
        mSubmit_btn = (Button) findViewById(R.id.album_phone_detail_confirm);

//		if (num == 1) {
//			mAll_btn.setVisibility(View.GONE);
//			mCount_tv.setVisibility(View.GONE);
//		} else {
//			mAll_btn.setVisibility(View.VISIBLE);
//			mCount_tv.setVisibility(View.VISIBLE);
//
//		}
    }

    private class AlbumAdapter extends BaseAdapter {

        public int getCount() {
            return mListDetail.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            int padding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 4, SelectPhotoActivity.this
                            .getResources().getDisplayMetrics());
            if (convertView == null) {
                convertView = LayoutInflater.from(SelectPhotoActivity.this)
                        .inflate(R.layout.item_select_photo_gridview, null);
                holder = new ViewHolder();
                holder.photo = (ImageView) convertView
                        .findViewById(R.id.album_item_photo);
                holder.select = (ImageView) convertView
                        .findViewById(R.id.album_item_select);
                LayoutParams params = new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.height = (Constants.Screen.width - padding) / 3;
                params.width = params.height;
                holder.photo.setLayoutParams(params);
                holder.select.setLayoutParams(params);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                resetHolder(holder);
            }
            if (position == 0) {
                holder.select.setVisibility(View.GONE);
                holder.photo.setScaleType(ScaleType.CENTER_INSIDE);
                holder.photo.setImageResource(R.mipmap.camera);
                holder.photo.setBackgroundColor(ContextCompat.getColor(SelectPhotoActivity.this, R.color.grey));
            } else {
                holder.photo.setBackgroundColor(ContextCompat.getColor(SelectPhotoActivity.this, R.color.transparent));
                holder.photo.setScaleType(ScaleType.CENTER_CROP);
                ImageLoader.getInstance().displayImage(
                        "file://" + mListDetail.get(position).imagePath,
                        holder.photo);
                if (mSelect.contains(String.valueOf(position))) {
                    holder.select.setVisibility(View.VISIBLE);
                } else {
                    holder.select.setVisibility(View.GONE);
                }
            }
            return convertView;
        }

        class ViewHolder {
            ImageView photo;
            ImageView select;
        }

        void resetHolder(ViewHolder holder) {
            holder.photo.setImageBitmap(null);
        }
    }

    private Uri getCameraTempFile() {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(Constants.Path.ImageCacheDir);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            CommonUtils.showToast(SelectPhotoActivity.this,
                    "无法保存上传的图片，请检查SD卡是否挂载");
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String captureName = "camera_" + timeStamp + ".jpg";
        mImagePath = Constants.Path.ImageCacheDir + captureName;
        File protraitFile = new File(mImagePath);
        return Uri.fromFile(protraitFile);
    }

    private class PhoneAlbumAdapter extends BaseAdapter {
        int imageSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 80, getResources()
                        .getDisplayMetrics());

        public int getCount() {
            return mListData.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            ImageBucket item = mListData.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(SelectPhotoActivity.this)
                        .inflate(R.layout.item_select_photo_listview,
                                null);
                holder = new ViewHolder();
                holder.photo = (ImageView) convertView
                        .findViewById(R.id.phonealbum_item_photo);
                holder.name = (TextView) convertView
                        .findViewById(R.id.phonealbum_item_name);
                holder.count = (TextView) convertView
                        .findViewById(R.id.phonealbum_item_count);

                LayoutParams params = new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.width = imageSize;
                params.height = imageSize;
                holder.photo.setScaleType(ScaleType.FIT_XY);
                holder.photo.setLayoutParams(params);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                resetHolder(holder);
            }
            if (null != item.bucketList && !item.bucketList.isEmpty()) {
                ImageLoader.getInstance().displayImage(
                        "file://" + item.bucketList.get(0).imagePath,
                        holder.photo);

            }
            holder.name.setText(item.bucketName);
            holder.count.setText("(" + item.count + ")");
            return convertView;
        }

        class ViewHolder {
            ImageView photo;
            TextView name;
            TextView count;
        }

        void resetHolder(ViewHolder holder) {
            holder.photo.setImageBitmap(null);
            holder.name.setText("");
            holder.count.setText("");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//			if (requestCode == REQUEST_CODE_ALBUM) {
//				setResult(RESULT_OK);
//				finish();
//			}
            if (requestCode == REQUEST_CODE_CAPTURE) {
                // 拍照返回
                LogUtil.i("TAG", "called by capture");
                Intent i = new Intent();
                i.putExtra("cameraPath", mImagePath);
                setResult(REQUEST_CODE_CAPTURE, i);
                finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
