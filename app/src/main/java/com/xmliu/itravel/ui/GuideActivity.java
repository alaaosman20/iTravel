package com.xmliu.itravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rey.material.widget.Button;
import com.xmliu.itravel.BaseActivity;
import com.xmliu.itravel.R;

/**
 * 引导页
 * Created by Admin on 2015/11/24.
 */
public class GuideActivity extends BaseActivity {

    private static final int mImageIds[] = {R.mipmap.guide01,
            R.mipmap.guide02, R.mipmap.guide03, R.mipmap.guide04};
    private ViewGroup guideGroup;

    private ImageView[] guideTips;
    private boolean fromabout = false;
    private ViewPager mPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        fromabout = this.getIntent().getBooleanExtra("fromabout",false);
        mApplication.mSharedPreferences.edit().putBoolean("isFirstTime", false)
                .commit();
    }

    private void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_guide);

        mPager = (ViewPager) findViewById(R.id.guide_pager);
        guideGroup = (ViewGroup) findViewById(R.id.guideGroup);

        guideTips = new ImageView[mImageIds.length];
        for (int i = 0; i < guideTips.length; i++) {

            ImageView imageView = new ImageView(this);
            guideTips[i] = imageView;
            if (i == 0) {
                guideTips[i].setBackgroundResource(R.mipmap.guide_dot_press);
            } else {
                guideTips[i].setBackgroundResource(R.mipmap.guide_dot_normal);
            }
            int width = (int) getResources().getDimension(R.dimen.width_3_160);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                    width,width));
            layoutParams.leftMargin = 15;
            layoutParams.rightMargin = 15;
            guideGroup.addView(imageView,layoutParams);

        }

        mPager.setAdapter(new MyPagerAdapter());
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                for (int i = 0; i < guideTips.length; i++) {
                    if (i == arg0 % mImageIds.length) {
                        guideTips[i].setBackgroundResource(R.mipmap.guide_dot_press);
                    } else {
                        guideTips[i].setBackgroundResource(R.mipmap.guide_dot_normal);
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

    // 左右滑动的图片适配器继承PagerAdapter
    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            ((ViewPager)container).removeView((View) object);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mImageIds.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = getLayoutInflater();
            View guideView = inflater.inflate(R.layout.item_guide, null);
            ImageView image = (ImageView) guideView
                    .findViewById(R.id.guide_item_image);
            ImageView text = (ImageView) guideView
                    .findViewById(R.id.guide_item_text);
            Button startBtn = (Button) guideView
                    .findViewById(R.id.guide_start_btn);

            image.setImageResource(mImageIds[position]);

            if (position == mImageIds.length - 1) {
                startBtn.setVisibility(View.VISIBLE);
                startBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!fromabout) {
                            startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                        }
                        GuideActivity.this.finish();
                    }
                });
            } else {
                startBtn.setVisibility(View.GONE);
            }

            ((ViewPager) container).addView(guideView, 0);
            return guideView;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

    }

}
