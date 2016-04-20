package com.xmliu.itravel.bean;

import cn.bmob.v3.BmobObject;

/**
 * Date: 2016/4/20 10:23
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class CrashBean extends BmobObject {


    String osVersion;
    String phoneModel;
    String token;
    String appName;
    String appVersionNo;
    String exceptionName;
    String exceptionReason;
    String stackTrace;
    String screenSize;
    UserBean author;

    public UserBean getAuthor() {
        return author;
    }

    public void setAuthor(UserBean author) {
        this.author = author;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppVersionNo() {
        return appVersionNo;
    }

    public void setAppVersionNo(String appVersionNo) {
        this.appVersionNo = appVersionNo;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public String getExceptionReason() {
        return exceptionReason;
    }

    public void setExceptionReason(String exceptionReason) {
        this.exceptionReason = exceptionReason;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }
}
