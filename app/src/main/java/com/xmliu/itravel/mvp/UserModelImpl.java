/*
 *  *
 *  *************************************************************************
 *  *
 *  *  Copyright (C) 2015 XMLIU diyangxia@163.com.
 *  *
 *  *                       All rights reserved.
 *  *
 *  **************************************************************************
 */

package com.xmliu.itravel.mvp;

import com.xmliu.itravel.BaseApplication;
import com.xmliu.itravel.bean.UserBean;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Date: 2016/2/25 16:49
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class UserModelImpl implements UserModel {

    @Override
    public void loadUserData(String username, final OnUserListener listener) {
        BmobQuery<UserBean> bmobQuery = new BmobQuery<UserBean>();
        bmobQuery.addWhereEqualTo("username", username);
        bmobQuery.findObjects(BaseApplication.getInstance().getApplicationContext(), new FindListener<UserBean>() {
            @Override
            public void onSuccess(List<UserBean> object) {

                if (object.size() > 0) {
                    listener.onSuccess(object.get(0));
                } else {
                    listener.onError("查询失败");
                }

            }

            @Override
            public void onError(int code, String msg) {
                listener.onError(msg);
//                CommonUtils.showToast(UserInfoEditActivity.this, "查询失败，用户不存在," + msg);
            }
        });
    }


}