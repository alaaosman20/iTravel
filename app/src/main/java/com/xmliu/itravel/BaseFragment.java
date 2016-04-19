package com.xmliu.itravel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xmliu.itravel.utils.NetWorkUtils;

/**
 * Date: 2016/1/8-13-13:25
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class BaseFragment extends Fragment {

    protected BaseApplication mApplication;
    protected Context mContext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mApplication = (BaseApplication) activity.getApplication();
        NetWorkUtils.isNetworkAvailable(activity);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
