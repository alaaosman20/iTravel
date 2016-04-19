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

import com.xmliu.itravel.bean.UserBean;

/**
 * Date: 2016/2/25 16:58
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class UserPresenterImpl implements UserPresenter,OnUserListener {

    private UserView userView;
    private UserModel userModel;

    public UserPresenterImpl(UserView userView) {
        this.userView = userView;
        userModel = new UserModelImpl();
    }

    @Override
    public void getUser(String username) {
        userView.showLoading();
        userModel.loadUserData(username, this);
    }

    @Override
    public void onSuccess(UserBean userBean) {
        userView.hideLoading();
        userView.setNoteInfo(userBean);
    }

    @Override
    public void onError(String reason) {
        userView.hideLoading();
        userView.showError(reason);
    }

}
