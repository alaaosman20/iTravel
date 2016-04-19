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

/**
 * Date: 2016/2/25 16:45
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public interface UserModel {

    void loadUserData(String username, OnUserListener listener);
}


