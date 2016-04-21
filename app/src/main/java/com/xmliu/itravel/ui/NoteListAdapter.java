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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
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

import com.google.gson.Gson;
import com.xmliu.itravel.R;
import com.xmliu.itravel.bean.NoteBean;
import com.xmliu.itravel.bean.UserBean;
import com.xmliu.itravel.utils.BaseRecyclerViewAdapter;
import com.xmliu.itravel.utils.CommonUtils;
import com.xmliu.itravel.utils.DateUtils;
import com.xmliu.itravel.utils.ImageUtils;
import com.xmliu.itravel.utils.LogUtil;
import com.xmliu.itravel.utils.RecyclerHolder;
import com.xmliu.itravel.utils.StringUtils;
import com.xmliu.itravel.widget.ListRightBottomBar;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Date: 2016/3/7 10:01
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class NoteListAdapter extends BaseRecyclerViewAdapter {


    private Context mContext;
    private List<NoteBean> mListData;
    private ListRightBottomBar listRightBottomBar = null;
    private boolean isAvatarClick = true; // 头像是否可以点击，在状态详情或者个人主页时便不再可点击
    private boolean isUserSelf = false;
    private String TAG = NoteListAdapter.class.getSimpleName();

    public NoteListAdapter(Context context, List<?> list, boolean avatarClick,boolean userSelf) {
        super(context, list);
        isAvatarClick = avatarClick;
        isUserSelf = userSelf;
        mContext = context;
        mListData = (List<NoteBean>) list;
        int height;
//        if(isUserSelf){
//            height = (int) context.getResources().getDimension(R.dimen.height_55_160);
//        }else{
//            height = (int) context.getResources().getDimension(R.dimen.height_40_160);
//        }
        height = (int) context.getResources().getDimension(R.dimen.height_8_160);
        listRightBottomBar = new ListRightBottomBar(context,height,isUserSelf);
        LogUtil.i("TAG", "mListData" + mListData.size());
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.item_main_recycleview;
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, final int position) {

        final NoteBean item = (NoteBean) list.get(position);


        RecyclerView gridRecycleview = holder.getRecycleview(R.id.recycleview_item_recycleView);
        TextView contentTV = holder.getTextView(R.id.recycleview_main_content);
        TextView timeTV = holder.getTextView(R.id.recycleview_main_time);
        TextView nameTV = holder.getTextView(R.id.recycleview_main_username);
        CircleImageView avatarIV = holder.getCircleImageView(R.id.recycleview_main_avatar);

        TextView addressTV = holder.getTextView(R.id.recycleview_main_address);
        ImageView popupIV = holder.getImageView(R.id.recycleview_main_popup);
        final LinearLayout agreeLayout = holder.getLinearLayout(R.id.recycleview_main_agree_layout);
        final TextView agreeTV = holder.getTextView(R.id.recycleview_main_agree_text);
        final ImageView agreeIV = holder.getImageView(R.id.recycleview_main_agree_image);

        final LinearLayout commentLayout = holder.getLinearLayout(R.id.recycleview_main_comment_layout);
        final TextView commentTV = holder.getTextView(R.id.recycleview_main_comment_text);
        LinearLayout addressLayout = holder.getLinearLayout(R.id.recycleview_main_address_layout);
        LinearLayout secretLayout = holder.getLinearLayout(R.id.recycleview_main_secret_layout);

        if (isAvatarClick) {
            avatarIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, IndexPageActivity.class)
                            .putExtra("userId", item.getAuthor().getObjectId()));
                }
            });
        }
        agreeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAgree(item.getObjectId(), agreeIV, agreeTV, item.getAgreeNum());
            }
        });
        if (item.getAgreeNum() == 0) {
            agreeTV.setText("赞");
        } else {
            agreeTV.setText("" + item.getAgreeNum());
        }
        if (item.getCommentNum() == 0) {
            commentTV.setText("评论");
        } else {
            commentTV.setText("" + item.getCommentNum());
        }

        popupIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomPopup(v,item.getObjectId());
            }
        });


        if (item.getImageList() == null || item.getImageList().size() == 0) {
            gridRecycleview.setVisibility(View.GONE);
        } else {
            gridRecycleview.setVisibility(View.VISIBLE);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
            // RecycleView初始化配置
            gridRecycleview.setLayoutManager(gridLayoutManager);
            //设置Item增加、移除动画
            gridRecycleview.setItemAnimator(new DefaultItemAnimator());
            int gHeight = 0;
            int gSize = item.getImageList().size();
            if (gSize == 1 || gSize == 2 || gSize == 3) {
                gHeight = (int) mContext.getResources().getDimension(R.dimen.width_40_160);
            } else if (gSize == 4 || gSize == 5 || gSize == 6) {
                gHeight = (int) mContext.getResources().getDimension(R.dimen.width_80_160);
            } else if (gSize == 7 || gSize == 8 || gSize == 9) {
                gHeight = (int) mContext.getResources().getDimension(R.dimen.width_120_160);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, gHeight);
            params.topMargin =  (int) mContext.getResources().getDimension(R.dimen.width_3_160);
            gridRecycleview.setLayoutParams(params);
            GridImageAdapter demoAdapter = new GridImageAdapter(mContext, item.getImageList());
            gridRecycleview.setAdapter(demoAdapter);
            demoAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mContext, ShowActivity.class);
                    intent.putExtra("images", new Gson().toJson(item.getImageList()));
                    intent.putExtra("position", position);
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    intent.putExtra("locationX", location[0]);
                    intent.putExtra("locationY", location[1]);

                    intent.putExtra("width", view.getWidth());
                    intent.putExtra("height", view.getHeight());
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(0, 0);
                }
            });
        }

        timeTV.setText(DateUtils.CompareWithNow(item.getCreatedAt()));
        if (StringUtils.isBlank(item.getContent())) {
            contentTV.setVisibility(View.GONE);
        } else {
            contentTV.setVisibility(View.VISIBLE);
            contentTV.setText(item.getContent());
        }
        if (StringUtils.isBlank(item.getAddress())) {
            addressLayout.setVisibility(View.GONE);
        } else {
            addressLayout.setVisibility(View.VISIBLE);
            addressTV.setText(item.getAddress());
        }
        if (item.getIsopen()) {
            secretLayout.setVisibility(View.GONE);
        } else {
            secretLayout.setVisibility(View.VISIBLE);
        }
        if (!StringUtils.isBlank(item.getAuthor().getNickname())) {
            nameTV.setText(item.getAuthor().getNickname());
        }
        if (!StringUtils.isBlank(item.getAuthor().getAvatar())) {
            ImageUtils.displayImage(mContext,item.getAuthor().getAvatar(), avatarIV);
        }
        addressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, MapDetailActivity.class)
                        .putExtra("latitude", mListData.get(position).getLatitude())
                        .putExtra("longitude", mListData.get(position).getLongitude())
                        .putExtra("address", mListData.get(position).getAddress()));
            }
        });
    }

    /**
     * 添加多对多关联
     *
     * @param noteId
     */
    private void addAgree(final String noteId, final ImageView agreeIV, final TextView agreeTV, final int agreeNum) {
        final Animation scaleAnimation = new ScaleAnimation(0.5f, 1.2f, 0.5f, 1.2f);
        scaleAnimation.setDuration(500);
        final UserBean user = BmobUser.getCurrentUser(mContext, UserBean.class);
        // 再添加赞之前需要先判断用户是否已点过赞，如果是仅提示用户即可。
        BmobQuery<UserBean> query = new BmobQuery<>();
        NoteBean post = new NoteBean();
        post.setObjectId(noteId);
        //agrees是NoteBean表中的字段，用来存储所有喜欢该帖子的用户
        query.addWhereRelatedTo("agrees", new BmobPointer(post));
//        query.include("author");
        query.findObjects(mContext, new FindListener<UserBean>() {

            @Override
            public void onSuccess(List<UserBean> object) {
                LogUtil.i(TAG, " 有多少用户 " + object.size());
                boolean isAgree = false;
                for (int i = 0; i < object.size(); i++) {
                    if (object.get(i).getObjectId().equals(user.getObjectId())) {
                        CommonUtils.showToast(mContext, "您已点过赞");
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
                    note.update(mContext, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            LogUtil.i(TAG, " 赞数+1");
                            agreeTV.setText("" + (agreeNum + 1));
                            agreeIV.startAnimation(scaleAnimation);
                            updateNoteBean(noteId);
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

    /**
     * 更新notebean表
     *
     * @param noteId
     */
    private void updateNoteBean(String noteId) {
        NoteBean noteBean = new NoteBean();
        noteBean.setObjectId(noteId);
        noteBean.increment("agreeNum", 1);// 原子计数器
        noteBean.setIsopen(true);
        noteBean.update(mContext, new UpdateListener() {
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

    private void deleteNoteBean(final String noteId){
        NoteBean noteBean = new NoteBean();
        noteBean.setObjectId(noteId);
        noteBean.delete(mContext, new DeleteListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                LogUtil.i("NoteListAdapter", "删除成功");
                for (int i = 0; i < mListData.size(); i++) {
                    if(mListData.get(i).getObjectId().equals(noteId)){
                        mListData.remove(i);
                        break;
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                LogUtil.i("NoteListAdapter","删除失败："+msg);
            }
        });
    }

    /**
     * @param v
     */
    private void showBottomPopup(View v,final String nid) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = 1f;//0.7f
        ((Activity) mContext).getWindow().setAttributes(lp);
        listRightBottomBar.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
                lp.alpha = 1f;
                ((Activity) mContext).getWindow().setAttributes(lp);
            }
        });
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        listRightBottomBar.showAtLocation(v, Gravity.NO_GRAVITY, location[0] - listRightBottomBar.getWidth(), location[1]);
//        listRightBottomBar.showAtLocation(v, Gravity.NO_GRAVITY, location[0]+v.getWidth(), location[1]);

        listRightBottomBar
                .setOnSelectModeBtnClickListener(new ListRightBottomBar.onSelectModeBtnCallBack() {

                    @Override
                    public void onSelectCancelBtnClick() {
                        listRightBottomBar.dismiss();
                    }

                    @Override
                    public void onFromCameraBtnClick() {
                        listRightBottomBar.dismiss();
                        if(!isUserSelf) {
                            // 执行删除状态方法
                            deleteNoteBean(nid);
                        }
                    }

                    @Override
                    public void onFromAlbumBtnClick() {
                        listRightBottomBar.dismiss();
                    }
                });

    }
}
