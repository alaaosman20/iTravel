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

package com.xmliu.itravel.ui;

import android.content.Context;
import android.widget.ImageView;

import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.ImageBean;
import com.xmliu.itravel.utils.BaseRecyclerViewAdapter;
import com.xmliu.itravel.utils.ImageUtils;
import com.xmliu.itravel.utils.RecyclerHolder;

import java.util.List;

/**
 * Date: 2016/3/7 10:09
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class GridImageAdapter  extends BaseRecyclerViewAdapter {

    private Context mContext;

    public GridImageAdapter(Context context, List<?> list) {
        super(context, list);
        mContext =context;
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.item_main_recycleview_image;
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, final int position) {
        final ImageBean item = (ImageBean) list.get(position);
        final ImageView imageView = holder.getImageView(R.id.recycleview_image_item_iv);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageUtils.displayImage(mContext,item.getImage(), imageView);
    }
}
