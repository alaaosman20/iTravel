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
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.FeedbackBean;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.utils.BaseRecyclerViewAdapter;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.LogUtil;
import com.xmliu.itravel.utils.RecyclerHolder;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Date: 2016/1/13-16-16:45
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: 目前只能添加一张图
 */
public class FeedListActivity extends ToolbarActivity {

    private int start = 0; // 当前页数
    private int limit = 5; // 为每页显示数据数目
    private int totalCount = 50;
    private TextView mNoneTV;
    private PullLoadMoreRecyclerView mRecyclerView;

    private List<FeedbackBean> mListData = new ArrayList<>();
    private List<FeedbackBean> mListDataMore = new ArrayList<>();

    private FeedAdapter myFeedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list2);

        toolbar.setTitle("反馈记录");

        mRecyclerView = (PullLoadMoreRecyclerView) findViewById(R.id.pullLoadMoreRecyclerView);
        mNoneTV = (TextView) findViewById(R.id.myfeed_list_none_tv);

        // RecycleView初始化配置
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLinearLayout();
        mRecyclerView.setFooterViewText("加载更多...");

        mRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                start = 0;
                limit = 5;
                loadList();
            }

            @Override
            public void onLoadMore() {
                LogUtil.i(TAG, "loading more");
                calculate();
                mListDataMore.clear();
                loadListMore();
            }
        });

        CommonUtils.showProgressDialog(this, "正在加载数据...");// 首次加载显示加载框
        loadList();

    }

    private class FeedAdapter extends BaseRecyclerViewAdapter {

        public FeedAdapter(Context context, List<?> list) {
            super(context, list);
        }
        @Override
        public int onCreateViewLayoutID(int viewType) {
            return R.layout.item_feedback_listview;
        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, int position) {
            final FeedbackBean item = (FeedbackBean) list.get(position);
            holder.getTextView(R.id.myfeed_item_content).setText(item.getContent());
            ImageLoader.getInstance().displayImage(item.getImage(),holder.getImageView(R.id.myfeed_item_image));
        }



    }

    private void loadList() {

        BmobQuery<FeedbackBean> bmobQuery = new BmobQuery<>();
        // 首次默认返回5条数据
        bmobQuery.setLimit(5);
        // 按createdAt字段降序排列
        bmobQuery.order("-createdAt");
        bmobQuery.addWhereEqualTo("author", BmobUser.getCurrentUser(FeedListActivity.this, UserBean.class));
        bmobQuery.findObjects(FeedListActivity.this, new FindListener<FeedbackBean>() {
            @Override
            public void onSuccess(List<FeedbackBean> object) {
                CommonUtils.hideProgressDialog();
                if (object.size() > 0) {
                    mNoneTV.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    LogUtil.i(TAG, "数据大小为" + object.size());
                    mListData.clear();
                    mListData.addAll(object);
                    myFeedAdapter = new FeedAdapter(FeedListActivity.this, mListData);
                    mRecyclerView.setAdapter(myFeedAdapter);
//                    mRecyclerView.setRefreshing(false);
                    myFeedAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            FeedbackBean item = mListData.get(position);

                        }
                    });
                    mRecyclerView.setPullLoadMoreCompleted();
                } else {
                    mNoneTV.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onError(int code, String msg) {
                CommonUtils.hideProgressDialog();
                CommonUtils.showToast(FeedListActivity.this, "查询失败" + msg);
            }
        });

    }

    private void loadListMore() {

        BmobQuery<FeedbackBean> bmobQuery = new BmobQuery<>();
        // 忽略前start条数据（即第一页数据结果）
        bmobQuery.setSkip(start);
        // 返回limit条数据，如果不加上这条语句，默认返回10条数据
        bmobQuery.setLimit(limit);
        bmobQuery.addWhereEqualTo("author", BmobUser.getCurrentUser(FeedListActivity.this, UserBean.class));
        // 按createdAt字段降序排列
        bmobQuery.order("-createdAt");
        bmobQuery.findObjects(FeedListActivity.this, new FindListener<FeedbackBean>() {
            @Override
            public void onSuccess(List<FeedbackBean> object) {
                CommonUtils.hideProgressDialog();
                if (object.size() > 0) {
                    LogUtil.i(TAG, "分页数据大小为" + object.size());
                    mListDataMore.addAll(object);
                    mListData.addAll(mListDataMore);
                    myFeedAdapter.notifyDataSetChanged();
                } else {
                    mRecyclerView.setRefreshing(false);
                    CommonUtils.showToast(FeedListActivity.this,
                            "数据全部加载完毕");
                }
                mRecyclerView.setPullLoadMoreCompleted();
            }

            @Override
            public void onError(int code, String msg) {
                CommonUtils.hideProgressDialog();
                CommonUtils.showToast(FeedListActivity.this, "查询失败" + msg);
            }
        });

    }

    private void calculate() {
        int current = myFeedAdapter.getItemCount();
        if (current + limit < totalCount) {
            start = current;
        } else {
            start = current;
            limit = totalCount - current;
        }
    }

}
