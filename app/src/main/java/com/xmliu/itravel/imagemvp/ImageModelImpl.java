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

import com.xmliu.itravel.BaseApplication;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Date: 2016/2/25 16:49
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class ImageModelImpl implements ImageModel {

    @Override
    public void uploadImg(String imgPath, final OnImageListener listener) {
        final File file = new File(imgPath);
        final BmobFile imageFile = new BmobFile(file);
        imageFile.uploadblock(BaseApplication.getInstance().getApplicationContext(), new UploadFileListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess(imageFile);
//                insertImage(new ImageBean(imageFile.getUrl(), System.currentTimeMillis() + "", imageFile));
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError(s);
            }
        });
    }


}