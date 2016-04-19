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

package com.xmliu.timerdata.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Date: 2016/1/26 14:17
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: RecyclerView通用ViewHolder
 */
public class RVHolder extends RecyclerView.ViewHolder {

    private RecyclerHolder viewHolder;
    public RVHolder(View itemView) {
        super(itemView);
        viewHolder= RecyclerHolder.getViewHolder(itemView);
    }
    public RecyclerHolder getViewHolder() {
        return viewHolder;
    }
}
