package com.xmliu.itravel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonFloat;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.NoteBean;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.utils.AppManager;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.LogUtil;
import com.xmliu.itravel.utils.StringUtils;
import com.xmliu.itravel.widget.ListRightBottomBar;

import java.util.ArrayList;
import java.util.List;

import c.b.BP;
import c.b.PListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 主页
 * Created by Admin on 2015/11/24.
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 201;
    private int start = 0; // 当前页数
    private int limit = 5; // 为每页显示数据数目
    private int totalCount;
    private List<NoteBean> mListData = new ArrayList<>();
    private List<NoteBean> mListDataMore = new ArrayList<>();
    private String avatarUrl;
    private String userbgUrl;
    private String nicknameStr;
    private String signatureStr;
    private String userId;
    private int genderValue;

    private NoteListAdapter adapter;

    private Toolbar toolbar;
    private ButtonFloat mEditBtn;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DrawerLayout drawerLayout;
    private CircleImageView avatarIV;
    private LinearLayout mapLayout;
    private LinearLayout guideLayout;
    private ImageView userBgIV;
    private ImageView logoutIV;

    private TextView nicknameTV;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout settingLayout;
    private ListRightBottomBar listRightBottomBar = null;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppManager.getInstance().addActivity(this);

        mEditBtn = (ButtonFloat) findViewById(R.id.main_edit_buttonfloat);
        mRecyclerView = (RecyclerView) findViewById(R.id.aboutUs_recycleView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayou);
        avatarIV = (CircleImageView) findViewById(R.id.main_drawerlayout_avatar);
        logoutIV = (ImageView) findViewById(R.id.main_drawerlayout_logout);
        guideLayout = (LinearLayout) findViewById(R.id.main_drawerlayout_guide);
        mapLayout = (LinearLayout) findViewById(R.id.main_drawerlayout_map);
        userBgIV = (ImageView) findViewById(R.id.main_drawerlayout_bg);
        nicknameTV = (TextView) findViewById(R.id.main_drawerlayout_nickname);

        settingLayout = (LinearLayout) findViewById(R.id.drawer_id_setting_layout);

        int height = (int) getResources().getDimension(R.dimen.height_40_160);
        listRightBottomBar = new ListRightBottomBar(this, height, false);

        BP.init(this, "07b752a64abdf34127887ada169d9709");
        initToolBar();
        initDrawerLayout();
        initRecyclerview();

        CommonUtils.showProgressDialog(this);
//        CommonUtils.showProgressDialog(this, "正在加载数据...");// 首次加载显示加载框
        loadList();

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
                        calculate();
                        loadListMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    isSlidingToLast = true;
                } else {
                    isSlidingToLast = false;
                }
            }
        });


        mapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapDistrictActivity.class));
            }
        });
        guideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MusicMainActivity.class));
            }
        });
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, AddActivity.class), REQUEST_CODE_ADD);
            }
        });
        avatarIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserCenterActivity.class)
                                .putExtra("nickname", nicknameStr)
                                .putExtra("signature", signatureStr)
                                .putExtra("avatar", avatarUrl)
                                .putExtra("userId", userId)
                                .putExtra("userbg", userbgUrl)
                                .putExtra("gender", genderValue)
                );
            }
        });
        settingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });
        logoutIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.showExitDialog(MainActivity.this);
            }
        });
    }

    private void addAgree(final String noteId) {
        final UserBean user = BmobUser.getCurrentUser(this, UserBean.class);

        // 再添加赞之前需要先判断用户是否已点过赞，如果是仅提示用户即可。

        BmobQuery<UserBean> query = new BmobQuery<>();
        NoteBean post = new NoteBean();
        post.setObjectId(noteId);
        //agrees是NoteBean表中的字段，用来存储所有喜欢该帖子的用户
        query.addWhereRelatedTo("agrees", new BmobPointer(post));
        query.findObjects(this, new FindListener<UserBean>() {

            @Override
            public void onSuccess(List<UserBean> object) {
                for (int i = 0; i < object.size(); i++) {
                    if (object.get(i).equals(user)) {
                        CommonUtils.showToast(MainActivity.this, "您已点过赞");
                        break;
                    }
                }
                final NoteBean note = new NoteBean();
                note.setObjectId(noteId);
                BmobRelation relation = new BmobRelation();
                relation.add(user);
                note.setAgrees(relation);
                note.update(MainActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        LogUtil.i(TAG, " 赞数+1");
                        updateNoteBean(noteId);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        LogUtil.i(TAG, " 赞数+1失败" + s);
                    }
                });
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                Log.i(TAG, "查询失败：" + code + "-" + msg);
            }
        });


    }


    private void queryAgree(String noteId) {
        BmobQuery<UserBean> query = new BmobQuery<>();
        NoteBean post = new NoteBean();
        post.setObjectId(noteId);
        //agrees是NoteBean表中的字段，用来存储所有喜欢该帖子的用户
        query.addWhereRelatedTo("agrees", new BmobPointer(post));
        query.findObjects(this, new FindListener<UserBean>() {

            @Override
            public void onSuccess(List<UserBean> object) {
                // TODO Auto-generated method stub
                Log.i(TAG, "查询个数：" + object.size());
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                Log.i(TAG, "查询失败：" + code + "-" + msg);
            }
        });
    }

    private void updateNoteBean(String noteId) {
        NoteBean noteBean = new NoteBean();
        noteBean.setObjectId(noteId);
        noteBean.increment("agreeNum", 1);// 原子计数器
        noteBean.setIsopen(true);
        noteBean.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                LogUtil.i(TAG, " 赞数+1");
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.i(TAG, " 赞数+1失败" + s);
            }
        });

    }

    /**
     * 初始化主列表
     */
    private void initRecyclerview() {
        // RecycleView初始化配置
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //设置Item增加、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        mRecyclerView.addItemDecoration(new MainDividerItemDecoration(
//                MainActivity.this, MainDividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
    }

    /**
     * 初始化侧滑菜单，即抽屉
     */
    private void initDrawerLayout() {
        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.action_settings, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // 先查询手机号是否已注册
                BmobQuery<UserBean> bmobQuery = new BmobQuery<UserBean>();
                //查询mobile叫mPhoneStr的数据
                bmobQuery.addWhereEqualTo("username", UserBean.getCurrentUser(MainActivity.this).getUsername());
                bmobQuery.findObjects(MainActivity.this, new FindListener<UserBean>() {
                    @Override
                    public void onSuccess(List<UserBean> object) {
                        // TODO Auto-generated method stub

                        if (object.size() > 0) {
                            avatarUrl = object.get(0).getAvatar();
                            ImageLoader.getInstance().displayImage(avatarUrl, avatarIV);
                            userbgUrl = object.get(0).getBgurl();
                            ImageLoader.getInstance().displayImage(userbgUrl, userBgIV);
                            nicknameStr = object.get(0).getNickname();
                            signatureStr = object.get(0).getSignature();
                            genderValue = object.get(0).getGender();
                            userId = object.get(0).getObjectId();
                            if (StringUtils.isBlank(nicknameStr)) {
                                nicknameTV.setText("你的昵称");
                            } else {
                                nicknameTV.setText(nicknameStr);
                            }

                        } else {
                            CommonUtils.showToast(MainActivity.this, "查询失败，用户不存在");
                        }

                    }

                    @Override
                    public void onError(int code, String msg) {
                        // TODO Auto-generated method stub
                        CommonUtils.showToast(MainActivity.this, "查询失败，用户不存在," + msg);
                    }
                });
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * 初始化toolbar
     */
    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("首页");
            toolbar.setNavigationIcon(R.mipmap.common_back_normal);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        /*自定义的一些操作*/
        toolbar.setContentInsetsRelative(0, 0);
    }

    /**
     * @param v
     */
    private void showBottomPopup(View v) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        listRightBottomBar.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        listRightBottomBar.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        listRightBottomBar
                .setOnSelectModeBtnClickListener(new ListRightBottomBar.onSelectModeBtnCallBack() {

                    @Override
                    public void onSelectCancelBtnClick() {
                        listRightBottomBar.dismiss();
                    }

                    @Override
                    public void onFromCameraBtnClick() {
                        listRightBottomBar.dismiss();

                    }

                    @Override
                    public void onFromAlbumBtnClick() {
                        listRightBottomBar.dismiss();
                    }
                });

    }

    private void calculate() {
        int current = adapter.getItemCount();
        if (current + limit < totalCount) {
            start = current;
        } else {
            start = current;
            limit = totalCount - current;
        }
    }

    private void loadList() {

        // 查询用户自己所发的私密状态
        BmobQuery<NoteBean> secretQuery = new BmobQuery<>();
        secretQuery.addWhereEqualTo("author", BmobUser.getCurrentUser(MainActivity.this, UserBean.class));
        secretQuery.addWhereEqualTo("isopen", false);

        // 查询所有权限为公开的状态
        BmobQuery<NoteBean> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("isopen", true);

        //最后组装完整的and条件
        List<BmobQuery<NoteBean>> andQuerys = new ArrayList<>();
        andQuerys.add(secretQuery);
        andQuerys.add(bmobQuery);

        BmobQuery<NoteBean> countQuery = new BmobQuery<>();
        countQuery.or(andQuerys);
        countQuery.findObjects(MainActivity.this, new FindListener<NoteBean>() {
            @Override
            public void onSuccess(List<NoteBean> list) {
                totalCount = list.size();
                LogUtil.i(TAG, "数据总量=" + totalCount);
            }

            @Override
            public void onError(int i, String s) {
                totalCount = 0;
            }
        });

        BmobQuery<NoteBean> mainQuery = new BmobQuery<>();
        mainQuery.or(andQuerys);
        // 按createdAt字段降序排列
        mainQuery.order("-createdAt");
        // 返回limit条数据，如果不加上这条语句，默认返回10条数据
        mainQuery.setLimit(limit);
        mainQuery.include("author");// 希望在查询帖子信息的同时也把发布人的信息查询出来

        mainQuery.findObjects(MainActivity.this, new FindListener<NoteBean>() {
            @Override
            public void onSuccess(List<NoteBean> object) {
                LogUtil.i(TAG, "result==>" + new Gson().toJson(object));
                CommonUtils.hideProgressDialog();
                if (object.size() > 0) {
                    mListData.clear();
                    mListData.addAll(object);
                    // 创建Adapter，并指定数据集
                    adapter = new NoteListAdapter(MainActivity.this, mListData, true, false);
                    // 设置Adapter
                    mRecyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            NoteBean item = mListData.get(position);
                            startActivity(new Intent(MainActivity.this, NoteDetailActivity.class)
                                            .putExtra("userId", item.getAuthor().getObjectId())
                                            .putExtra("noteId", item.getObjectId())
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
                    CommonUtils.showToast(MainActivity.this, "查询失败");
                }

            }

            @Override
            public void onError(int code, String msg) {
                CommonUtils.hideProgressDialog();
                CommonUtils.showToast(MainActivity.this, "查询失败" + msg);
            }
        });

    }


    private void loadListMore() {
        LogUtil.i(TAG, "loading more" + start + limit);
        mListDataMore.clear();
        // 查询用户自己所发的私密状态
        BmobQuery<NoteBean> secretQuery = new BmobQuery<>();
        secretQuery.addWhereEqualTo("author", BmobUser.getCurrentUser(MainActivity.this, UserBean.class));
        secretQuery.addWhereEqualTo("isopen", false);

        // 查询所有权限为公开的状态
        BmobQuery<NoteBean> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("isopen", true);

        //最后组装完整的and条件
        List<BmobQuery<NoteBean>> andQuerys = new ArrayList<>();
        andQuerys.add(secretQuery);
        andQuerys.add(bmobQuery);

        BmobQuery<NoteBean> mainQuery = new BmobQuery<>();
        mainQuery.or(andQuerys);
        // 按createdAt字段降序排列
        mainQuery.order("-createdAt");
        // 忽略前start条数据（即第一页数据结果）
        mainQuery.setSkip(start);
        // 返回limit条数据，如果不加上这条语句，默认返回10条数据
        mainQuery.setLimit(limit);
        mainQuery.include("author");// 希望在查询帖子信息的同时也把发布人的信息查询出来

        mainQuery.findObjects(MainActivity.this, new FindListener<NoteBean>() {
            @Override
            public void onSuccess(List<NoteBean> object) {
                CommonUtils.hideProgressDialog();
                LogUtil.i(TAG, "loading more size =" + object.size());
                if (object.size() > 0) {
                    mListDataMore.addAll(object);
                    mListData.addAll(mListDataMore);
                    adapter.notifyDataSetChanged();
                    //        adapter.notifyItemInserted(); // 建议使用这种更新adapter方式，带position参数，带动画效果
                    if (adapter.getItemCount() >= totalCount) {
                        Toast.makeText(MainActivity.this, "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int code, String msg) {
                CommonUtils.hideProgressDialog();
                CommonUtils.showToast(MainActivity.this, "查询失败" + msg);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.action_alipay) {
            //是否安装了支付宝
            if (CommonUtils.isInstallApp(this, "com.eg.android.AlipayGphone")) {
                BP.pay(MainActivity.this, "请我喝杯水", "水乃生命之源", 0.02, true, new PListener() {
                    @Override
                    public void orderId(String s) {
                        LogUtil.i(TAG, "alipay orderId:" + s);
                    }

                    @Override
                    public void succeed() {
                        CommonUtils.showToast(MainActivity.this, "谢谢打赏");
                        LogUtil.i(TAG, "alipay success");
                    }

                    @Override
                    public void fail(int i, String s) {
                        CommonUtils.showToast(MainActivity.this, "打赏失败，不要气馁，再试一次");
                        LogUtil.i(TAG, "fail code=" + i + ";reason:" + s);
                    }

                    @Override
                    public void unknow() {
                        LogUtil.i(TAG, "alipay unknow");
                    }
                });
            } else {
                CommonUtils.showToast(this, "您还未安装支付宝");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK) {
            loadList();
        }
    }

    @Override
    public void onBackPressed() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("应用提示")
                .content("确定退出" + getResources().getString(R.string.app_name) + "吗?")
                .positiveText("确定").onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        AppManager.getInstance().killAllActivity();
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();

    }

}
