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
 * Date: 2016/2/25 16:59
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public interface UserView {

    void showLoading();
    void hideLoading();
    void showError(String reason);
    void setNoteInfo(UserBean noteInfo);
}
