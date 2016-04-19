package com.xmliu.itravel.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by Admin on 2015/11/24.
 * BmobUser有默认的username和password字段使用，可以objectedId用作用户id
 */
public class UserBean extends BmobUser {

    /**
     * 验证码，只作记录用
     */
    Integer code;
    /**
     * 头像地址
     */
    String avatar;
    /**
     * 用户背景图片
     */
    String bgurl;
    /**
     * 昵称
     */
    String nickname;
    /**
     * 性别：男 女
     */
    Integer gender;
    /**
     * 所在地区
     */
    String area;
    /**
     * 个性签名
     */
    String signature;
    /**
     * 极光推送id
     */
    Integer rid;

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBgurl() {
        return bgurl;
    }

    public void setBgurl(String bgurl) {
        this.bgurl = bgurl;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}