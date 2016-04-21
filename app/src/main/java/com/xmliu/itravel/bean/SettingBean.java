package com.xmliu.itravel.bean;

import cn.bmob.v3.BmobObject;

/**
 * Date: 2016/4/21 17:30
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class SettingBean extends BmobObject{

    private boolean gprsImageEnable;
    private UserBean author;

    public UserBean getAuthor() {
        return author;
    }

    public void setAuthor(UserBean author) {
        this.author = author;
    }

    public boolean isGprsImageEnable() {
        return gprsImageEnable;
    }

    public void setGprsImageEnable(boolean gprsImageEnable) {
        this.gprsImageEnable = gprsImageEnable;
    }
}
