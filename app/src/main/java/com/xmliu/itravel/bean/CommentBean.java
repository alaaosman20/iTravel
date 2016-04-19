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

package com.xmliu.itravel.bean;

import cn.bmob.v3.BmobObject;

/**
 * Date: 2016/3/8 8:58
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class CommentBean extends BmobObject {

    private String content;
    private UserBean user;
    private NoteBean note;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public NoteBean getNote() {
        return note;
    }

    public void setNote(NoteBean note) {
        this.note = note;
    }
}
