package com.xmliu.itravel.bean;

import cn.bmob.v3.BmobObject;

/**
 * Date: 2016/1/13-16-16:15
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class FeedbackBean extends BmobObject {

    String image;
    String content;
    UserBean author;

    public UserBean getAuthor() {
        return author;
    }

    public void setAuthor(UserBean author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}