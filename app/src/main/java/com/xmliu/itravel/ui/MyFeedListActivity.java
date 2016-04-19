package com.xmliu.itravel.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.FeedbackBean;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.utils.BaseRecyclerViewAdapter;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.DividerItemDecoration;
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
public class MyFeedListActivity extends ToolbarActivity {

    private int start = 0; // 当前页数
    private int limit = 5; // 为每页显示数据数目
    private int totalCount = 50;
    private TextView mNoneTV;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<FeedbackBean> mListData = new ArrayList<>();
    private List<FeedbackBean> mListDataMore = new ArrayList<>();

    private FeedAdapter myFeedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);

        toolbar.setTitle("反馈记录");

        mRecyclerView = (RecyclerView) findViewById(R.id.myfeed_list_recycleView);
        mNoneTV = (TextView) findViewById(R.id.myfeed_list_none_tv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);

        // RecycleView初始化配置
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //设置Item增加、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                MyFeedListActivity.this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setColorSchemeResources(R.color.blue,
                R.color.orange,
                R.color.red);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                start = 0;
                limit = 5;
                loadList();
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalCount = manager.getItemCount();
                    if (lastVisibleItem == (totalCount - 1) && isSlidingToLast) {
                        LogUtil.i(TAG, "loading more");
                        calculate();
                        mListDataMore.clear();
                        loadListMore();
                        //http://www.denghaojie.cn/android-recyclerview-load-more-672.html
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    isSlidingToLast = true;
                }else {
                    isSlidingToLast=false;
                }
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
        bmobQuery.addWhereEqualTo("author", BmobUser.getCurrentUser(MyFeedListActivity.this, UserBean.class));
        bmobQuery.findObjects(MyFeedListActivity.this, new FindListener<FeedbackBean>() {
            @Override
            public void onSuccess(List<FeedbackBean> object) {
                CommonUtils.hideProgressDialog();
                if (object.size() > 0) {
                    mNoneTV.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    LogUtil.i("SplashActivity", "数据大小为" + object.size());
                    mListData.clear();
                    mListData.addAll(object);
                    myFeedAdapter = new FeedAdapter(MyFeedListActivity.this, mListData);
                    mRecyclerView.setAdapter(myFeedAdapter);
                    swipeRefreshLayout.setRefreshing(false);
                    myFeedAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            FeedbackBean item = mListData.get(position);

                        }
                    });
                } else {
                    mNoneTV.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onError(int code, String msg) {
                CommonUtils.hideProgressDialog();
                CommonUtils.showToast(MyFeedListActivity.this, "查询失败" + msg);
            }
        });

    }

    private void loadListMore() {

        BmobQuery<FeedbackBean> bmobQuery = new BmobQuery<>();
        // 忽略前start条数据（即第一页数据结果）
        bmobQuery.setSkip(start);
        // 返回limit条数据，如果不加上这条语句，默认返回10条数据
        bmobQuery.setLimit(limit);
        bmobQuery.addWhereEqualTo("author", BmobUser.getCurrentUser(MyFeedListActivity.this, UserBean.class));
        // 按createdAt字段降序排列
        bmobQuery.order("-createdAt");
        bmobQuery.findObjects(MyFeedListActivity.this, new FindListener<FeedbackBean>() {
            @Override
            public void onSuccess(List<FeedbackBean> object) {
                CommonUtils.hideProgressDialog();
                if (object.size() > 0) {
                    LogUtil.i("TAG", "分页数据大小为" + object.size());
                    mListDataMore.addAll(object);
                    mListData.addAll(mListDataMore);
                    myFeedAdapter.notifyDataSetChanged();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    CommonUtils.showToast(MyFeedListActivity.this,
                            "数据全部加载完毕");
                }

            }

            @Override
            public void onError(int code, String msg) {
                CommonUtils.hideProgressDialog();
                CommonUtils.showToast(MyFeedListActivity.this, "查询失败" + msg);
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
