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

package com.xmliu.itravel.imagemvp;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Date: 2016/2/25 16:46
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public interface OnImageListener {
    void onSuccess(BmobFile bmobFile);

    void onError(String reason);
}
