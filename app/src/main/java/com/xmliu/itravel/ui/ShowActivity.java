package com.xmliu.itravel.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.ImageBean;
import com.xmliu.itravel.widget.AbViewPager;
import com.xmliu.itravel.widget.SmoothImageView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 *
 * 从原位置放大，再回到原位置缩小
 * 显示多图或单图，可放缩
 */
public class ShowActivity extends Activity {

    SmoothImageView imageView;
    int mLocationX;
    int mLocationY;
    int mPosition;
    int mWidth;
    int mHeight;
    private String mDatas;
    private List<String> imageList = new ArrayList<>();
    private List<ImageBean> imageBeanList = new ArrayList<>();
    private DisplayImageOptions mNormalImageOptions;


    private FrameLayout mImagesLayout;
    private AbViewPager viewPager;
    private ImageView[] tips;
    private SmoothImageView[] mImageViews;
    private ViewGroup group;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        mDatas = getIntent().getStringExtra("images");

        mPosition = getIntent().getIntExtra("position", -1);
        mLocationX = getIntent().getIntExtra("locationX", 0);
        mLocationY = getIntent().getIntExtra("locationY", 0);
        mWidth = getIntent().getIntExtra("width", 0);
        mHeight = getIntent().getIntExtra("height", 0);

        mNormalImageOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Config.RGB_565).cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();


        if(mPosition == -1){
            //说明是单张图
            imageList.add(mDatas);
            imageView = new SmoothImageView(this);
            imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
            imageView.transformIn();
            imageView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            imageView.setScaleType(ScaleType.FIT_CENTER);
            setContentView(imageView);
            if (!imageList.get(0).startsWith("http://")) {
                ImageLoader.getInstance().displayImage(
                        imageList.get(0),
                        imageView, mNormalImageOptions, new ImageLoadingListener() {

                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {
                                // TODO Auto-generated method stub

                            }
                        });
            } else {
                ImageLoader.getInstance().displayImage(
                        imageList.get(0),
                        imageView, mNormalImageOptions);
            }
            imageView.setOnTransformListener(new SmoothImageView.TransformListener() {
                @Override
                public void onTransformComplete(int mode) {
                    if (mode == 2) {
                        finish();
                    }
                }
            });
            imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v2) {
                    imageView.transformOut();
                }
            });
        }else{
            // 多图展示，viewpager
            setContentView(R.layout.activity_large_image);

            mImagesLayout = (FrameLayout) findViewById(R.id.large_image_frame);
            viewPager = (AbViewPager) findViewById(R.id.large_image_viewPager);
            group = (ViewGroup) findViewById(R.id.large_image_viewGroup);

            Type listType = new TypeToken<List<ImageBean>>(){}.getType();
            Gson gson = new Gson();
            imageBeanList =  gson.fromJson(mDatas,listType);
            if (imageBeanList.size() == 1) {
                group.setVisibility(View.GONE);
            } else {
                group.setVisibility(View.VISIBLE);
            }
            tips = new ImageView[imageBeanList.size()];
            for (int i = 0; i < tips.length; i++) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(10, 10));
                tips[i] = imageView;
                if (i == mPosition) {
                    tips[i].setBackgroundResource(R.mipmap.guide_dot_press);
                } else {
                    tips[i].setBackgroundResource(R.mipmap.guide_dot_normal);
                }

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                layoutParams.leftMargin = 5;
                layoutParams.rightMargin = 5;
                group.addView(imageView, layoutParams);
            }
            // 将图片装载到数组中
            mImageViews = new SmoothImageView[imageBeanList.size()];
            for (int i = 0; i < mImageViews.length; i++) {
                final SmoothImageView smoothImageView = new SmoothImageView(this);
                mImageViews[i] = smoothImageView;
                smoothImageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
                smoothImageView.transformIn();
                smoothImageView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
                smoothImageView.setScaleType(ScaleType.FIT_CENTER);
                    ImageLoader.getInstance().displayImage(
                            imageBeanList.get(i).getImage(),
                            smoothImageView,mNormalImageOptions);
                smoothImageView.setOnTransformListener(new SmoothImageView.TransformListener() {
                    @Override
                    public void onTransformComplete(int mode) {
                        if (mode == 2) {
                            finish();
                        }
                    }
                });
                smoothImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float v, float v2) {
                        smoothImageView.transformOut();
                    }
                });
            }

            // 设置Adapter
            viewPager.setAdapter(new TopAdapter());
            // 设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
            // viewPager.setCurrentItem((mImageViews.length) * 100);
            viewPager.setCurrentItem(mPosition);
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {
                    // TODO Auto-generated method stub
                    for (int i = 0; i < tips.length; i++) {
                        if (i == arg0 % mImageViews.length) {
                            tips[i].setBackgroundResource(R.mipmap.guide_dot_press);
                        } else {
                            tips[i].setBackgroundResource(R.mipmap.guide_dot_normal);
                        }
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                    // TODO Auto-generated method stub

                }
            });
        }




    }
    public class TopAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageBeanList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            // ((ViewPager) container).removeView(mImageViews[position
            // % mImageViews.length]);

        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(View container, int position) {

            try {
                View view = mImageViews[position % mImageViews.length];
                view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        ShowActivity.this.finish();
                        overridePendingTransition(0, R.anim.fade_out);
                    }
                });
                ((ViewPager) container).addView(mImageViews[position
                        % mImageViews.length], 0);
            } catch (Exception e) {
                // handler something
            }

            return mImageViews[position % mImageViews.length];
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
}
