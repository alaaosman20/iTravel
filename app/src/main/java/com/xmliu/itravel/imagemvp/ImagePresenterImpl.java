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
 * Date: 2016/2/25 16:58
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class ImagePresenterImpl implements ImagePresenter,OnImageListener {

    private UploadImageView uploadView;
    private ImageModel userModel;

    public ImagePresenterImpl(UploadImageView uploadView) {
        this.uploadView = uploadView;
        userModel = new ImageModelImpl();
    }

    @Override
    public void onSuccess(BmobFile bmobFile) {
        uploadView.hideLoading();
        uploadView.setImageInfo(bmobFile);
    }

    @Override
    public void onError(String reason) {
        uploadView.hideLoading();
        uploadView.showError(reason);
    }

    @Override
    public void uploadImage(String imagePath) {
        uploadView.showLoading();
        userModel.uploadImg(imagePath, this);
    }
}
