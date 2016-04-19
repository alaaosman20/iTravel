package com.xmliu.itravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gc.materialdesign.views.ButtonRectangle;
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
    private ButtonRectangle mGuideLoginBtn;
    private ButtonRectangle mGuideRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadGuide();
    }

    private void loadGuide() {
//        boolean isFirstTime = mApplication.mSharedPreferences.getBoolean("isFirstTime", true);
//        if (isFirstTime) {
        init();
        mApplication.mSharedPreferences.edit().putBoolean("isFirstTime", false)
                .commit();
//        }
//        else {
//            startActivity(new Intent(GuideActivity.this, MainActivity.class));
//            finish();
//            mApplication.mSharedPreferences.edit().putBoolean("isFirstTime", false)
//                    .commit();
//        }
    }

    private void init() {
        setContentView(R.layout.activity_guide);

        ViewPager mPager = (ViewPager) findViewById(R.id.guide_pager);
        guideGroup = (ViewGroup) findViewById(R.id.guideGroup);
        mGuideLoginBtn = (ButtonRectangle) findViewById(R.id.id_guide_login_btn);
        mGuideRegisterBtn = (ButtonRectangle) findViewById(R.id.id_guide_register_btn);
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
        mGuideLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                GuideActivity.this.finish();
            }
        });
        mGuideRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuideActivity.this, RegisterActivity.class).putExtra("fromGuide",true));
                GuideActivity.this.finish();
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
            // TODO Auto-generated method stub
            LayoutInflater inflater = getLayoutInflater();
            View guideView = inflater.inflate(R.layout.item_guide, container, false);
            ImageView image = (ImageView) guideView
                    .findViewById(R.id.guide_item_image);
            image.setBackgroundResource(mImageIds[position]);

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
