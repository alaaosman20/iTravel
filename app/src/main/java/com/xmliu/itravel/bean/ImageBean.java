package com.xmliu.itravel.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Date: 2016/1/13-15-15:53
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class ImageBean extends BmobObject {
    private String image;
    private String id;
    private BmobFile file;

    public ImageBean() {
    }

    public ImageBean(String image, String id, BmobFile file) {
        this.image = image;
        this.id = id;
        this.file = file;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BmobFile getFile() {
        return file;
    }

    public void setFile(BmobFile file) {
        this.file = file;
    }
}