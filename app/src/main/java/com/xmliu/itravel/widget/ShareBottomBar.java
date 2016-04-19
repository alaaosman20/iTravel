package com.xmliu.itravel.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.xmliu.itravel.Constants;
import com.xmliu.itravel.R;


/**
 *
 */
public class ShareBottomBar extends PopupWindow implements OnClickListener {

	private LinearLayout mSinaLayout;
	private LinearLayout mWechatLayout;
	private LinearLayout mCircleLayout;
	private LinearLayout mQQLayout;
	private LinearLayout mQzoneLayout;
	private LinearLayout mSmsLayout;
	private Button mCancelBtn = null;
	private View mContentView = null;

	private onShareModeBtnCallBack onShareModeBtnCallBack;

	public ShareBottomBar() {
		super();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ShareBottomBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ShareBottomBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 */
	public ShareBottomBar(Context context) {
		super(context);
		mContentView = LayoutInflater.from(context).inflate(R.layout.popupwindow_select_share, null);
		this.setContentView(mContentView);
		this.setWidth(Constants.Screen.width);
		this.setHeight((int) context.getResources().getDimension(R.dimen.height_80_160));
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.setTouchable(true);
		this.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.light_black)));
		this.setAnimationStyle(R.style.popup_Animation);
		initViews();
		initEvents();
	}

	/**
	 * @param width
	 * @param height
	 */
	public ShareBottomBar(int width, int height) {
		super(width, height);
	}

	/**
	 * @param contentView
	 * @param width
	 * @param height
	 * @param focusable
	 */
	public ShareBottomBar(View contentView, int width, int height, boolean focusable) {
		super(contentView, width, height, focusable);
	}

	/**
	 * @param contentView
	 * @param width
	 * @param height
	 */
	public ShareBottomBar(View contentView, int width, int height) {
		super(contentView, width, height);
	}

	/**
	 * @param contentView
	 */
	public ShareBottomBar(View contentView) {
		super(contentView);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.share_bottom_sina_layout:
				if (onShareModeBtnCallBack != null) {
					onShareModeBtnCallBack.onFromSinaClick();
				}
				break;
			case R.id.share_bottom_wechat_layout:
				if (onShareModeBtnCallBack != null) {
					onShareModeBtnCallBack.onFromWechatClick();
				}
				break;
			case R.id.share_bottom_comments_layout:
				if (onShareModeBtnCallBack != null) {
					onShareModeBtnCallBack.onFromCircleClick();
				}
				break;
			case R.id.share_bottom_qq_layout:
				if (onShareModeBtnCallBack != null) {
					onShareModeBtnCallBack.onFromQQClick();
				}
				break;
			case R.id.share_bottom_qzone_layout:
				if (onShareModeBtnCallBack != null) {
					onShareModeBtnCallBack.onFromQzoneClick();
				}
				break;
			case R.id.share_bottom_sms_layout:
				if (onShareModeBtnCallBack != null) {
					onShareModeBtnCallBack.onFromSmsClick();
				}
				break;
			case R.id.select_photo_mode_cancel_btn:
				if (onShareModeBtnCallBack != null) {
					onShareModeBtnCallBack.onSelectCancelBtnClick();
				}
				break;
		}
		dismiss();
	}

	public void setOnShareModeBtnClickListener(onShareModeBtnCallBack callBack) {
		onShareModeBtnCallBack = callBack;
	}

	public interface onShareModeBtnCallBack {

		void onFromSinaClick();
		void onFromWechatClick();
		void onFromCircleClick();
		void onFromQQClick();
		void onFromQzoneClick();
		void onFromSmsClick();

		void onSelectCancelBtnClick();
	}

	private void initViews() {
		mSinaLayout = (LinearLayout) mContentView.findViewById(R.id.share_bottom_sina_layout);
		mQQLayout = (LinearLayout) mContentView.findViewById(R.id.share_bottom_qq_layout);
		mQzoneLayout = (LinearLayout) mContentView.findViewById(R.id.share_bottom_qzone_layout);
		mWechatLayout = (LinearLayout) mContentView.findViewById(R.id.share_bottom_wechat_layout);
		mCircleLayout = (LinearLayout) mContentView.findViewById(R.id.share_bottom_comments_layout);
		mSmsLayout = (LinearLayout) mContentView.findViewById(R.id.share_bottom_sms_layout);
		mCancelBtn = (Button) mContentView.findViewById(R.id.select_photo_mode_cancel_btn);
	}

	private void initEvents() {
		mSinaLayout.setOnClickListener(this);
		mQQLayout.setOnClickListener(this);
		mQzoneLayout.setOnClickListener(this);
		mWechatLayout.setOnClickListener(this);
		mCircleLayout.setOnClickListener(this);
		mSmsLayout.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
	}

}
