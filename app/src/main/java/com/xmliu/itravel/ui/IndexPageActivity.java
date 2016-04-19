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

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xmliu.itravel.BaseActivity;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.NoteBean;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.FullyLinearLayoutManager;
import com.xmliu.itravel.utils.LogUtil;
import com.xmliu.itravel.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * toolbaractivity无法和PullToZoomScrollViewEx兼容使用，故独立出来。
 * Created by xmliu on 2016/2/10.
 */
public class IndexPageActivity extends BaseActivity {

    PullToZoomScrollViewEx scrollView;
    private Toolbar toolbar;
    private ImageView userbgIV;
    private ImageView avatarIV;
    private TextView editIV;
    private ImageView genderIV;
    private TextView nameTV;
    private String avatarUrl;
    private String nicknameStr;
    private String userbgUrl;
    private int genderValue;

    private String userId;
    private static final int REQUEST_CODE_EDIT = 100;

    private RecyclerView mRecyclerView;
//    private SwipeRefreshLayout swipeRefreshLayout;
    private NoteListAdapter adapter;
    private List<NoteBean> mListData = new ArrayList<>();
    private boolean isUserSelf = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_page);

        userId = this.getIntent().getStringExtra("userId");

        scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setTitle("个人主页");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View headView = LayoutInflater.from(this).inflate(R.layout.activity_index_page_head, null, false);
        avatarIV = (ImageView) headView.findViewById(R.id.index_head_avatar_iv);
        editIV = (TextView) headView.findViewById(R.id.index_head_edit_tv);


        genderIV = (ImageView) headView.findViewById(R.id.index_head_gender);
        nameTV = (TextView) headView.findViewById(R.id.index_head_name_tv);
        View zoomView = LayoutInflater.from(this).inflate(R.layout.activity_index_page_zoom, null, false);
        userbgIV = (ImageView) zoomView.findViewById(R.id.index_zoom_bg);
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_index_page_content, null, false);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.index_page_recyclerView);
//        swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipe_refresh_widget);
        scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (12.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);

        if(BmobUser.getCurrentUser(IndexPageActivity.this,UserBean.class).getObjectId().equals(userId)){
            isUserSelf = true;
            editIV.setVisibility(View.VISIBLE);
        }else{
            isUserSelf = false;
            editIV.setVisibility(View.GONE);
        }
        editIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(IndexPageActivity.this, UserInfoEditActivity.class), REQUEST_CODE_EDIT);
            }
        });
        loadAuthor();
        initRecyclerview();
        loadList();
    }

    private void loadList() {

        // 查询用户自己所发的状态
        BmobQuery<UserBean> userQuery = new BmobQuery<>();
        userQuery.addWhereEqualTo("objectId", userId);
        userQuery.findObjects(IndexPageActivity.this, new FindListener<UserBean>() {
            @Override
            public void onSuccess(List<UserBean> list) {
                LogUtil.i(TAG, "查到用户=" + list.size());
                if(list.size()!=0) {
                    BmobQuery<NoteBean> mainQuery = new BmobQuery<>();
                    mainQuery.addWhereEqualTo("author", list.get(0));
                    // 按createdAt字段降序排列
                    mainQuery.order("-createdAt");
                    // 返回limit条数据，如果不加上这条语句，默认返回10条数据
//                mainQuery.setLimit(limit);
                    mainQuery.include("author");// 希望在查询帖子信息的同时也把发布人的信息查询出来

                    mainQuery.findObjects(IndexPageActivity.this, new FindListener<NoteBean>() {
                        @Override
                        public void onSuccess(List<NoteBean> object) {
                            LogUtil.i(TAG, "result==>" + object.size());
//                            CommonUtils.hideProgressDialog();
                            if (object.size() > 0) {
                                mListData.clear();
                                mListData.addAll(object);
                                LogUtil.i(TAG, "result==>" + mListData.size());
                                // 创建Adapter，并指定数据集
                                adapter = new NoteListAdapter(IndexPageActivity.this, mListData,false,isUserSelf);
                                // 设置Adapter
                                mRecyclerView.setAdapter(adapter);
//                                swipeRefreshLayout.setRefreshing(false);
                                adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        NoteBean item = mListData.get(position);
                                        startActivity(new Intent(IndexPageActivity.this, NoteDetailActivity.class)
                                                        .putExtra("userId", item.getAuthor().getObjectId())
                                                        .putExtra("address", item.getAddress())
                                                        .putExtra("content", item.getContent())
                                                        .putExtra("time", item.getCreatedAt())
                                                        .putExtra("latitude", item.getLatitude())
                                                        .putExtra("longitude", item.getLongitude())
                                                        .putExtra("images", new Gson().toJson(item.getImageList()))
                                        );
                                    }
                                });
                            } else {
                                CommonUtils.showToast(IndexPageActivity.this, "查询失败");
                            }

                        }

                        @Override
                        public void onError(int code, String msg) {
                            CommonUtils.hideProgressDialog();
                            CommonUtils.showToast(IndexPageActivity.this, "查询失败" + msg);
                        }
                    });
                }else{
                    CommonUtils.showToast(IndexPageActivity.this, "查询不到用户");
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });



    }


    /**
     * 初始化主列表
     */
    private void initRecyclerview() {
        // RecycleView初始化配置
        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
        layoutManager.setOrientation(FullyLinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //设置Item增加、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
    }
    private void loadAuthor() {
        BmobQuery<UserBean> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("objectId", userId);
        bmobQuery.findObjects(IndexPageActivity.this, new FindListener<UserBean>() {
            @Override
            public void onSuccess(List<UserBean> object) {
                // TODO Auto-generated method stub

                if (object.size() > 0) {
                    UserBean userBean = object.get(0);
                    avatarUrl = userBean.getAvatar();
                    userbgUrl = userBean.getBgurl();
                    nicknameStr = userBean.getNickname();
                    genderValue = userBean.getGender();

                    ImageLoader.getInstance().displayImage(avatarUrl, avatarIV);
                    ImageLoader.getInstance().displayImage(userbgUrl, userbgIV);
                    if(StringUtils.isBlank(nicknameStr)){
                        nameTV.setText("你的昵称");
                    }else {
                        nameTV.setText(nicknameStr);
                    }
                    if(genderValue == 1){
                        genderIV.setImageResource(R.mipmap.userinfo_icon_male);
                    }else if(genderValue == 2){
                        genderIV.setImageResource(R.mipmap.userinfo_icon_female);
                    }else{
                        genderIV.setVisibility(View.GONE);
                    }

                } else {
                    CommonUtils.showToast(IndexPageActivity.this, "查询失败，用户不存在");
                }

            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                CommonUtils.showToast(IndexPageActivity.this, "查询失败，用户不存在," + msg);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {

            loadAuthor();
        }
    }
}
