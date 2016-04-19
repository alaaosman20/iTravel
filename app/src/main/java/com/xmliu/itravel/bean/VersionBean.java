package com.xmliu.itravel.bean;

import cn.bmob.v3.BmobObject;

/**
 * Date: 2016/1/12-17-17:59
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class VersionBean extends BmobObject {
    String path;
    String version;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
