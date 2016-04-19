package com.xmliu.itravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xmliu.itravel.BaseActivity;
import com.xmliu.itravel.Constants;
import com.xmliu.itravel.R;

/**
 * 引导页
 * Created by Admin on 2015/11/24.
 */
public class GuideActivity extends BaseActivity {

    private int mImageIds[] = new int[]{R.mipmap.guide01,
            R.mipmap.guide02, R.mipmap.guide03, R.mipmap.guide04};
    private ViewGroup guideGroup;

    private ImageView[] guideTips;
    private boolean fromabout = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_guide);
        fromabout = this.getIntent().getBooleanExtra("fromabout",false);
        init();
        mApplication.mSharedPreferences.edit().putBoolean("isFirstTime", false)
                .commit();
    }

    private void init() {

        ViewPager mPager = (ViewPager) findViewById(R.id.guide_pager);
        guideGroup = (ViewGroup) findViewById(R.id.guideGroup);

        guideTips = new ImageView[mImageIds.length];
        for (int i = 0; i < guideTips.length; i++) {

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = Constants.Screen.width / 12;
            imageView.setLayoutParams(params);

            guideTips[i] = imageView;
            if (i == 0) {
                guideTips[i].setBackgroundResource(R.mipmap.guide_dot_press);
            } else {
                guideTips[i].setBackgroundResource(R.mipmap.guide_dot_normal);
            }
            guideGroup.addView(imageView);

        }

        mPager.setAdapter(new MyPagerAdapter());
        mPager.setCurrentItem(0, true);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                for (int i = 0; i < guideTips.length; i++) {
                    if (i == arg0 % guideTips.length) {
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
            (container).removeView((View) object);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mImageIds.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = getLayoutInflater();
            View guideView = inflater.inflate(R.layout.item_guide, container, false);
            ImageView image = (ImageView) guideView
                    .findViewById(R.id.guide_item_image);
            Button startBtn = (Button) guideView
                    .findViewById(R.id.guide_start_btn);

            image.setBackgroundResource(mImageIds[position]);

            if (position == mImageIds.length - 1) {
                startBtn.setVisibility(View.VISIBLE);
                guideGroup.setVisibility(View.GONE);
                startBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!fromabout) {
                            startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                        }
                        finish();
                    }
                });
            } else {
                startBtn.setVisibility(View.GONE);
                guideGroup.setVisibility(View.VISIBLE);
            }

            (container).addView(guideView, 0);
            return guideView;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

    }

}
