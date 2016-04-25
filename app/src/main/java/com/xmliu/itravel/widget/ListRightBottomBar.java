package com.xmliu.itravel.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

import com.xmliu.itravel.Constants;
import com.xmliu.itravel.R;


/**
 *
 */
public class ListRightBottomBar extends PopupWindow implements OnClickListener {

    private Button mSelectFromAlbumBtn = null;
    private Button mSelectFromCameraBtn = null;
    private Button mCancelBtn = null;
    private View mContentView = null;

    private onSelectModeBtnCallBack onSelectModeBtnCallBack;

    public ListRightBottomBar() {
        super();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ListRightBottomBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @param context
     * @param attrs
     */
    public ListRightBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context
     */
    public ListRightBottomBar(Context context, int height, boolean isSelf) {
        super(context);
        mContentView = LayoutInflater.from(context).inflate(R.layout.popupwindow_note_options, null);
        this.setContentView(mContentView);
        this.setWidth(Constants.Screen.width / 3);
        this.setHeight(Constants.Screen.height / 20);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        this.setAnimationStyle(R.style.popup_Animation_weixin);
        initViews(isSelf);
        initEvents();
    }

    /**
     * @param width
     * @param height
     */
    public ListRightBottomBar(int width, int height) {
        super(width, height);
    }

    /**
     * @param contentView
     * @param width
     * @param height
     * @param focusable
     */
    public ListRightBottomBar(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    /**
     * @param contentView
     * @param width
     * @param height
     */
    public ListRightBottomBar(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    /**
     * @param contentView
     */
    public ListRightBottomBar(View contentView) {
        super(contentView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_photo_mode_from_album_btn:
                if (onSelectModeBtnCallBack != null) {
                    onSelectModeBtnCallBack.onFromAlbumBtnClick();
                }
                break;

            case R.id.select_photo_mode_from_camera_btn:
                if (onSelectModeBtnCallBack != null) {
                    onSelectModeBtnCallBack.onFromCameraBtnClick();
                }
                break;

            case R.id.select_photo_mode_cancel_btn:
                if (onSelectModeBtnCallBack != null) {
                    onSelectModeBtnCallBack.onSelectCancelBtnClick();
                }
                break;
        }
        dismiss();
    }

    public void setOnSelectModeBtnClickListener(onSelectModeBtnCallBack callBack) {
        onSelectModeBtnCallBack = callBack;
    }

    public interface onSelectModeBtnCallBack {

        void onFromCameraBtnClick();

        void onFromAlbumBtnClick();

        void onSelectCancelBtnClick();
    }

    private void initViews(boolean isUserSelf) {
        mSelectFromAlbumBtn = (Button) mContentView.findViewById(R.id.select_photo_mode_from_album_btn);
        mSelectFromCameraBtn = (Button) mContentView.findViewById(R.id.select_photo_mode_from_camera_btn);
        mCancelBtn = (Button) mContentView.findViewById(R.id.select_photo_mode_cancel_btn);
        if (isUserSelf) {
            mSelectFromCameraBtn.setVisibility(View.VISIBLE);
            mCancelBtn.setVisibility(View.GONE);
        } else {
            mSelectFromCameraBtn.setVisibility(View.GONE);
            mCancelBtn.setVisibility(View.VISIBLE);
        }

    }

    private void initEvents() {
        // TODO Auto-generated method stub
        mSelectFromAlbumBtn.setOnClickListener(this);
        mSelectFromCameraBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
    }

}
