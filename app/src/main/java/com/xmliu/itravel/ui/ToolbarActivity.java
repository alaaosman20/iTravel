package com.xmliu.itravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.bugtags.library.Bugtags;
import com.xmliu.itravel.BaseApplication;
import com.xmliu.itravel.R;
import com.xmliu.itravel.utils.AppManager;
import com.xmliu.itravel.utils.NetWorkUtils;
import com.xmliu.itravel.utils.ToastUtil;
import com.xmliu.itravel.utils.ToolBarHelper;

/**
 * Date: 2016/1/19-14-14:38
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public abstract class ToolbarActivity extends AppCompatActivity {

    private ToolBarHelper mToolBarHelper ;
    public Toolbar toolbar ;
    protected String TAG;
    protected BaseApplication mApplication;
    protected Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (BaseApplication) getApplication();
        NetWorkUtils.networkStateTips(this);
    }

    @Override
    public void setContentView(int layoutResID) {

        mToolBarHelper = new ToolBarHelper(this,layoutResID) ;
        toolbar = mToolBarHelper.getToolBar() ;
        setContentView(mToolBarHelper.getContentView());
        AppManager.getInstance().addActivity(this);
        /*把 toolbar 设置到Activity 中*/
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("首页");
            toolbar.setNavigationIcon(R.mipmap.common_back_normal);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        /*自定义的一些操作*/
        onCreateCustomToolBar(toolbar) ;
        TAG=this.getLocalClassName();

    }

    public void onCreateCustomToolBar(Toolbar toolbar){
        toolbar.setContentInsetsRelative(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            finish();
            return true;
        }else if (id == R.id.action_about) {
            ToastUtil.showToast(this, "关于");
            startActivity(new Intent(this,AboutActivity.class));
            return true;
        }else if (id == R.id.action_share) {
            ToastUtil.showToast(this,"分享");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注：回调 1
        Bugtags.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //注：回调 2
        Bugtags.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //注：回调 3
        Bugtags.onDispatchTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }
}
