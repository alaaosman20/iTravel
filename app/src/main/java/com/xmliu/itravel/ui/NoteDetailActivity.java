package com.xmliu.itravel.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xmliu.itravel.Constants;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.CommentBean;
import com.xmliu.itravel.bean.ImageBean;
import com.xmliu.itravel.bean.NoteBean;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.utils.BaseRecyclerViewAdapter;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.DateUtils;
import com.xmliu.itravel.utils.DividerItemDecoration;
import com.xmliu.itravel.utils.FullyLinearLayoutManager;
import com.xmliu.itravel.utils.LogUtil;
import com.xmliu.itravel.utils.RecyclerHolder;
import com.xmliu.itravel.utils.StringUtils;
import com.xmliu.itravel.widget.ListRightBottomBar;
import com.xmliu.itravel.widget.ShareBottomBar;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by xmliu on 2016/1/30.
 */
public class NoteDetailActivity extends ToolbarActivity {

    private String contentStr;
    private String addressStr;
    private String imagesStr;
    private String timeStr;
    private String noteId;
    private List<ImageBean> imageList = new ArrayList<>();

    private ListRightBottomBar listRightBottomBar = null;
    private TextView emptyTV;
    private TextView mNameTV;
    private TextView mContentV;
    private TextView mAgreeNumTV;
    private TextView mCommentNumTV;
    private ImageView bottomPop;
    private ImageView mAgreeNumIV;
    private CircleImageView mAvatarIV;
    private RecyclerView commentsView;

    private NoteDetailListAdapter adapter;
    private List<CommentBean> commentList = new ArrayList<>();
    private ShareBottomBar shareBottomBar = null;

    // 分享内容
    private String shareTitleStr = "好旅分享";
    private String shareContentStr = "分享内容";
    private String shareImagePath = Constants.Path.SD_PATH + "/null.jpg";
    private String shareImageUrl = "http://file.bmob.cn/M03/AB/3A/oYYBAFbKnfeAYw8iAABbKWCZdAo368.jpg";
    private String shareWebpage = "http://blog.csdn.net/diyangxia";

    private String userId;
    private String longitude;
    private String latitude;

    private int commentNum;
    private int agreeNum;
    private boolean isAgree = false;
    private LinearLayout agreeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        toolbar.setTitle("正文");

        userId = this.getIntent().getStringExtra("userId");
        noteId = this.getIntent().getStringExtra("noteId");
        contentStr = this.getIntent().getStringExtra("content");
        addressStr = this.getIntent().getStringExtra("address");
        imagesStr = this.getIntent().getStringExtra("images");
        latitude = this.getIntent().getStringExtra("latitude");
        longitude = this.getIntent().getStringExtra("longitude");
        timeStr = this.getIntent().getStringExtra("time");

        bottomPop = (ImageView) findViewById(R.id.recycleview_main_popup);
        mAgreeNumIV = (ImageView) findViewById(R.id.id_note_detail_agree_iv);
        mAvatarIV = (CircleImageView) findViewById(R.id.recycleview_main_avatar);
        mNameTV = (TextView) findViewById(R.id.recycleview_main_username);
        mCommentNumTV = (TextView) findViewById(R.id.id_note_detail_comment_tip);
        mAgreeNumTV = (TextView) findViewById(R.id.id_note_detail_agree_tv);
        TextView mTimeTV = (TextView) findViewById(R.id.recycleview_main_time);
        TextView mAddressTV = (TextView) findViewById(R.id.recycleview_main_address);
        LinearLayout mAddressLayout = (LinearLayout) findViewById(R.id.recycleview_main_address_layout);
        LinearLayout bottomLayout = (LinearLayout) findViewById(R.id.recycleview_main_bottom_layout);
        LinearLayout commentLayout = (LinearLayout) findViewById(R.id.id_note_detail_comment_layout);
        LinearLayout mAgreeLayout = (LinearLayout) findViewById(R.id.id_note_detail_agree_layout);
        mContentV = (TextView) findViewById(R.id.recycleview_main_content);
        emptyTV = (TextView) findViewById(R.id.id_note_detail_comment_empty);

        RecyclerView mImageRV = (RecyclerView) findViewById(R.id.recycleview_item_recycleView);
        commentsView = (RecyclerView) findViewById(R.id.id_note_detail_comment_recyclerview);
        shareBottomBar = new ShareBottomBar(this);

        boolean isUserSelf = false;
        int height;
        if(isUserSelf){
            height = (int) getResources().getDimension(R.dimen.height_58_160);
        }else{
            height = (int) getResources().getDimension(R.dimen.height_40_160);
        }
        height = (int)getResources().getDimension(R.dimen.height_8_160);
        listRightBottomBar = new ListRightBottomBar(this,height,isUserSelf);
        bottomLayout.setVisibility(View.GONE);


        if (StringUtils.isBlank(addressStr)) {
            mAddressLayout.setVisibility(View.GONE);
        } else {
            mAddressLayout.setVisibility(View.VISIBLE);
            mAddressTV.setText(addressStr);
        }
        mAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoteDetailActivity.this, MapDetailActivity.class)
                        .putExtra("latitude", latitude)
                        .putExtra("longitude", longitude)
                        .putExtra("address", addressStr));
            }
        });

        mAgreeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.i(TAG, " 点击赞layout ");
                if(isAgree) {
                    CommonUtils.showToast(NoteDetailActivity.this,"您已点过赞");
                }else{
                    addAgree(mAgreeNumIV,mAgreeNumTV,agreeNum);
//                    UserBean user = BmobUser.getCurrentUser(NoteDetailActivity.this,UserBean.class);
//                    NoteBean note = new NoteBean();
//                    note.setObjectId(noteId);
//                    BmobRelation relation = new BmobRelation();
//                    relation.add(user);
//                    note.setAgrees(relation);
//                    note.update(NoteDetailActivity.this, new UpdateListener() {
//                        @Override
//                        public void onSuccess() {
//                            LogUtil.i(TAG, " 赞数+1");
//                            mAgreeNumTV.setText("" + (agreeNum + 1));
//                            updateNoteAgree();
//                        }
//
//                        @Override
//                        public void onFailure(int i, String s) {
//                            LogUtil.i(TAG, " 赞数+1失败" + s);
//                        }
//                    });
                }
            }
        });

        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(NoteDetailActivity.this)
                        .title("请输入评论内容")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("写评论...", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                addComment(input.toString());
                            }
                        }).show();
            }
        });

        mAvatarIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoteDetailActivity.this, IndexPageActivity.class).putExtra("userId", userId));
            }
        });
        bottomPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomPopup(v);
            }
        });
        mTimeTV.setText(DateUtils.CompareWithNow(timeStr));
        mContentV.setText(contentStr);
        if (!StringUtils.isBlank(imagesStr)) {
            Type focusType = new TypeToken<List<ImageBean>>() {
            }.getType();
            imageList = new Gson().fromJson(imagesStr, focusType);
            if (imageList.size() == 0) {
                mImageRV.setVisibility(View.GONE);
            } else {
                mImageRV.setVisibility(View.VISIBLE);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(NoteDetailActivity.this, 3);
                // RecycleView初始化配置
                mImageRV.setLayoutManager(gridLayoutManager);
                //设置Item增加、移除动画
                mImageRV.setItemAnimator(new DefaultItemAnimator());
                int gHeight = 0;
                int gSize = imageList.size();
                if (gSize == 1 || gSize == 2 || gSize == 3) {
                    gHeight = (int) getResources().getDimension(R.dimen.width_40_160);
                } else if (gSize == 4 || gSize == 5 || gSize == 6) {
                    gHeight = (int) getResources().getDimension(R.dimen.width_80_160);
                } else if (gSize == 7 || gSize == 8 || gSize == 9) {
                    gHeight = (int) getResources().getDimension(R.dimen.width_120_160);
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, gHeight);
                mImageRV.setLayoutParams(params);
                GridImageAdapter demoAdapter = new GridImageAdapter(NoteDetailActivity.this, imageList);
                mImageRV.setAdapter(demoAdapter);
                demoAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        LogUtil.i("TAG", "点击率" + imageList.get(position).getImage());

                        Intent intent = new Intent(NoteDetailActivity.this, ShowActivity.class);
                        intent.putExtra("images", new Gson().toJson(imageList));
                        intent.putExtra("position", position);
                        int[] location = new int[2];
                        view.getLocationOnScreen(location);
                        intent.putExtra("locationX", location[0]);
                        intent.putExtra("locationY", location[1]);

                        intent.putExtra("width", view.getWidth());
                        intent.putExtra("height", view.getHeight());
                        startActivity(intent);
                        overridePendingTransition(0, 0);


                    }
                });
            }
        }


        initAuthor();
        loadAgree();
        loadComment();
    }

    /**
     * 添加多对多关联
     *
     * @param
     */
    private void addAgree( final ImageView agreeIV, final TextView agreeTV, final int agreeNum) {
        final Animation scaleAnimation = new ScaleAnimation(0.5f, 1.2f, 0.5f, 1.2f);
        scaleAnimation.setDuration(500);
        final UserBean user = BmobUser.getCurrentUser(this, UserBean.class);
        // 再添加赞之前需要先判断用户是否已点过赞，如果是仅提示用户即可。
        BmobQuery<UserBean> query = new BmobQuery<>();
        NoteBean post = new NoteBean();
        post.setObjectId(noteId);
        //agrees是NoteBean表中的字段，用来存储所有喜欢该帖子的用户
        query.addWhereRelatedTo("agrees", new BmobPointer(post));
//        query.include("author");
        query.findObjects(this, new FindListener<UserBean>() {

            @Override
            public void onSuccess(List<UserBean> object) {
                LogUtil.i(TAG, " 有多少用户 " + object.size());
                boolean isAgree = false;
                for (int i = 0; i < object.size(); i++) {
                    if (object.get(i).getObjectId().equals(user.getObjectId())) {
                        CommonUtils.showToast(NoteDetailActivity.this, "您已点过赞");
                        isAgree = true;
                        break;
                    }
                }
                if (!isAgree) {
                    final NoteBean note = new NoteBean();
                    note.setObjectId(noteId);
                    BmobRelation relation = new BmobRelation();
                    relation.add(user);
                    note.setAgrees(relation);
                    note.update(NoteDetailActivity.this, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            LogUtil.i(TAG, " 赞数+1");
                            agreeTV.setText("" + (agreeNum + 1));
                            agreeIV.startAnimation(scaleAnimation);
                            updateNoteAgree();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            LogUtil.i(TAG, " 赞数+1失败" + s);
                        }
                    });
                }
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                LogUtil.i(TAG, "查询失败：" + code + "-" + msg);
            }
        });
    }

    private void addComment(String contentStr){
        UserBean user = BmobUser.getCurrentUser(NoteDetailActivity.this,UserBean.class);
        NoteBean note = new NoteBean();
        note.setObjectId(noteId);
        final CommentBean commentBean = new CommentBean();
        commentBean.setContent(contentStr);
        commentBean.setNote(note);
        commentBean.setUser(user);
        commentBean.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                LogUtil.i("TAG", " 添加评论成功后，让notebean表的commentNum加1");
                updateNoteComment();
                loadComment();
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.i("TAG", " 评论 onFailure");
            }
        });

    }
    /**
     * 更新notebean表
     *
     */
    private void updateNoteAgree() {
        NoteBean noteBean = new NoteBean();
        noteBean.setObjectId(noteId);
        noteBean.increment("agreeNum", 1);// 原子计数器
        noteBean.setIsopen(true);
        noteBean.update(NoteDetailActivity.this, new UpdateListener() {
            @Override
            public void onSuccess() {
                LogUtil.i(TAG, " 原子计数器赞数+1");
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.i(TAG, " 原子计数器赞数+1失败" + s);
            }
        });

    }
    private void updateNoteComment(){
        NoteBean noteBean = new NoteBean();
        noteBean.setObjectId(noteId);
        noteBean.increment("commentNum", 1);// 原子计数器
        noteBean.setIsopen(true);
        noteBean.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                LogUtil.i("TAG", " 评论数+1");
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtil.i("TAG", " 评论数+1失败" + s);
            }
        });

    }

    private void loadAgree(){
        LogUtil.i(TAG, "查询赞数开始");
        final UserBean user = BmobUser.getCurrentUser(this,UserBean.class);
        BmobQuery<UserBean> query = new BmobQuery<>();
        NoteBean post = new NoteBean();
        post.setObjectId(noteId);
        //agrees是NoteBean表中的字段，用来存储所有喜欢该帖子的用户
        query.addWhereRelatedTo("agrees", new BmobPointer(post));
        query.findObjects(this, new FindListener<UserBean>() {

            @Override
            public void onSuccess(List<UserBean> object) {
                LogUtil.i(TAG, "查询个数：" + object.size());
                agreeNum = object.size();
                mAgreeNumTV.setText("" + object.size());
                for (int i = 0; i < object.size(); i++) {
                    if (object.get(i).getObjectId().equals(user.getObjectId())) {
                        isAgree = true;
                        break;
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                LogUtil.i(TAG, "查询失败：" + code + "-" + msg);
            }
        });
    }


    private void loadComment(){

        BmobQuery<CommentBean> query = new BmobQuery<>();
        NoteBean noteBean = new NoteBean();
        noteBean.setObjectId(noteId);
        query.addWhereEqualTo("note", new BmobPointer(noteBean));
        query.include("user,note.author");
        // 按createdAt字段降序排列
        query.order("-createdAt");
        query.findObjects(this, new FindListener<CommentBean>() {
            @Override
            public void onSuccess(List<CommentBean> list) {
                LogUtil.i("TAG"," 查询评论 onSuccess" + list.size());
                commentNum = list.size();
                mCommentNumTV.setText("所有评论("+commentNum+")");
                if(list.size() == 0){
                    emptyTV.setVisibility(View.VISIBLE);
                    commentsView.setVisibility(View.GONE);
                }else {
                    emptyTV.setVisibility(View.GONE);
                    commentsView.setVisibility(View.VISIBLE);
                    commentList.clear();
                    commentList.addAll(list);
                    initCommentView();
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.i("TAG"," 查询评论 onError " + s);
            }
        });


    }

    /**
     * @param v
     */
    private void showBottomPopup(View v) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1f;
        getWindow().setAttributes(lp);
        listRightBottomBar.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        listRightBottomBar.showAtLocation(v, Gravity.NO_GRAVITY, location[0] - listRightBottomBar.getWidth(), location[1]);
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

    private void initAuthor() {
        BmobQuery<UserBean> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("objectId", userId);
        bmobQuery.findObjects(NoteDetailActivity.this, new FindListener<UserBean>() {
            @Override
            public void onSuccess(List<UserBean> object) {
                // TODO Auto-generated method stub

                if (object.size() > 0) {
                    UserBean userBean = object.get(0);
                    String avatarUrl = userBean.getAvatar();
                    ImageLoader.getInstance().displayImage(avatarUrl, mAvatarIV);
                    String nicknameStr = userBean.getNickname();
                    if (StringUtils.isBlank(nicknameStr)) {
                        mNameTV.setText("你的昵称");
                    } else {
                        mNameTV.setText(nicknameStr);
                    }
                } else {
                    CommonUtils.showToast(NoteDetailActivity.this, "查询失败，用户不存在");
                }

            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                CommonUtils.showToast(NoteDetailActivity.this, "查询失败，用户不存在," + msg);
            }
        });
    }

    private void initCommentView() {

        // RecycleView初始化配置
        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
        layoutManager.setOrientation(FullyLinearLayoutManager.VERTICAL);
        commentsView.setLayoutManager(layoutManager);
        //设置Item增加、移除动画
        commentsView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        commentsView.addItemDecoration(new DividerItemDecoration(
                NoteDetailActivity.this, DividerItemDecoration.VERTICAL_LIST));

        adapter = new NoteDetailListAdapter(NoteDetailActivity.this, commentList);
        // 设置Adapter
        commentsView.setAdapter(adapter);
//        swipeRefreshLayout.setRefreshing(false);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



            }
        });
    }

    public class NoteDetailListAdapter extends BaseRecyclerViewAdapter {


        public NoteDetailListAdapter(Context context, List<?> list) {
            super(context, list);
        }

        @Override
        public int onCreateViewLayoutID(int viewType) {
            return R.layout.item_note_detail_comment_recyclerview;
        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, int position) {

            final Animation scaleAnimation = new ScaleAnimation(0.5f, 1.2f, 0.5f, 1.2f);
            scaleAnimation.setDuration(500);

            final CommentBean item = (CommentBean) list.get(position);

            CircleImageView userIV = holder.getCircleImageView(R.id.notedetail_comment_item_avatar);
            TextView contentTV = holder.getTextView(R.id.notedetail_comment_item_content);
            TextView timeTV = holder.getTextView(R.id.notedetail_comment_item_time);
            TextView nameTV = holder.getTextView(R.id.notedetail_comment_item_name);

            contentTV.setText(item.getContent());
            timeTV.setText(item.getCreatedAt());
            nameTV.setText(item.getUser().getNickname());
            ImageLoader.getInstance().displayImage(item.getUser().getAvatar(), userIV);
        }
    }


    public class GridImageAdapter extends BaseRecyclerViewAdapter {

        public GridImageAdapter(Context context, List<?> list) {
            super(context, list);
        }

        @Override
        public int onCreateViewLayoutID(int viewType) {
            return R.layout.item_main_recycleview_image;
        }

        @Override
        public void onBindViewHolder(RecyclerHolder holder, int position) {
            ImageBean item = (ImageBean) list.get(position);
            ImageLoader.getInstance().displayImage(item.getImage(), holder.getImageView(R.id.recycleview_image_item_iv));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_share) {
            LogUtil.i(TAG, "点击了分享按钮");
            ShareSDK.initSDK(this);
            shareContentStr = contentStr;
            if(imageList.size() != 0) {
                shareImageUrl = imageList.get(0).getImage();
            }else{
                shareImageUrl = "http://file.bmob.cn/M03/AB/3A/oYYBAFbKnfeAYw8iAABbKWCZdAo368.jpg";
            }
            showSharePopup(toolbar);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_detail, menu);

        return true;
    }

    /**
     * @param v
     */
    private void showSharePopup(View v) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        shareBottomBar.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        shareBottomBar.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        shareBottomBar
                .setOnShareModeBtnClickListener(new ShareBottomBar.onShareModeBtnCallBack() {
                    @Override
                    public void onFromSinaClick() {
                        weiboShare();
                        shareBottomBar.dismiss();
                    }

                    @Override
                    public void onFromWechatClick() {
                        wechatShare();
                        shareBottomBar.dismiss();
                    }

                    @Override
                    public void onFromCircleClick() {
                        circleShare();
                        shareBottomBar.dismiss();
                    }

                    @Override
                    public void onFromQQClick() {
                        qqShare();
                        shareBottomBar.dismiss();
                    }

                    @Override
                    public void onFromQzoneClick() {
                        qzoneShare();
                        shareBottomBar.dismiss();
                    }

                    @Override
                    public void onFromSmsClick() {
                        smsShare();
                        shareBottomBar.dismiss();
                    }

                    @Override
                    public void onSelectCancelBtnClick() {

                        shareBottomBar.dismiss();
                    }
                });

    }

    private void smsShare() {

        ShortMessage.ShareParams sp = new ShortMessage.ShareParams();
        sp.setText(shareContentStr);
        Platform sms =  ShareSDK.getPlatform(ShortMessage.NAME);
        sms.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.i(TAG, "dx分享成功咯");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.i(TAG, "dx分享失败咯" + i + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.i(TAG, "dx分享取消咯");
            }
        });
        sms.share(sp);
    }

    private void circleShare() {

        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setText(shareContentStr);
        sp.setTitle(shareTitleStr);
        sp.setUrl(shareWebpage);

        File imageFile = new File(shareImagePath);
        if(imageFile.exists()) {
            sp.setImagePath(imageFile.getPath());
        }else {
            sp.setImageUrl(shareImageUrl);
        }
        Platform wechatMoments =  ShareSDK.getPlatform(WechatMoments.NAME);
        wechatMoments.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.i(TAG, "wxc分享成功咯");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.i(TAG, "wxc分享失败咯" + i + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.i(TAG, "wxc分享取消咯");
            }
        });
        wechatMoments.share(sp);
    }

    private void wechatShare() {
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setText(shareContentStr);
        sp.setTitle(shareTitleStr);
        sp.setUrl(shareWebpage);

        File imageFile = new File(shareImagePath);
        if(imageFile.exists()) {
            sp.setImagePath(imageFile.getPath());
        }else {
            sp.setImageUrl(shareImageUrl);
        }
        Platform wechat =  ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.i(TAG, "wx分享成功咯");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.i(TAG, "wx分享失败咯" + i + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.i(TAG, "wx分享取消咯");
            }
        });
        wechat.share(sp);
    }
    private void weiboShare() {
        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
        sp.setText(shareContentStr);
        File imageFile = new File(shareImagePath);
        if(imageFile.exists()) {
            sp.setImagePath(imageFile.getPath());
        }else {
            sp.setImageUrl(shareImageUrl);
        }

        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.i(TAG, "wb分享成功咯");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.i(TAG, "wb分享失败咯" + i + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.i(TAG, "wb分享取消咯");
            }
        });
        // 执行图文分享
        weibo.share(sp);
    }
    private void qqShare() {
        QQ.ShareParams qqSP = new QQ.ShareParams();
        qqSP.setTitle(shareTitleStr);
//        qqSP.setTitleUrl("http://blog.csdn.net/diyangxia"); // 标题的超链接
        qqSP.setText(shareContentStr);
        qqSP.setImageUrl(shareImageUrl);
        qqSP.setSite(getResources().getString(R.string.app_name));
        qqSP.setSiteUrl(shareWebpage);

        Platform qqPlatform = ShareSDK.getPlatform(QQ.NAME);
        qqPlatform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.i(TAG, "qq分享成功咯");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.i(TAG, "qq分享失败咯");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.i(TAG, "qq分享取消咯");
            }
        });
        // 执行图文分享
        qqPlatform.share(qqSP);
    }
    private void qzoneShare() {
        QZone.ShareParams sp = new QZone.ShareParams();
        sp.setTitle(shareTitleStr);
//        sp.setTitleUrl("http://blog.csdn.net/diyangxia"); // 标题的超链接
        sp.setText(shareContentStr);
        sp.setImageUrl(shareImageUrl);
        sp.setSite(getResources().getString(R.string.app_name));
        sp.setSiteUrl(shareWebpage);

        Platform qzone = ShareSDK.getPlatform(QZone.NAME);
        qzone.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.i(TAG, "qz分享成功咯");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.i(TAG, "qz分享失败咯");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.i(TAG, "qz分享取消咯");
            }
        });
        // 执行图文分享
        qzone.share(sp);
    }
}
