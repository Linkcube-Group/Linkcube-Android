package me.linkcube.app.ui.main.single;

import me.linkcube.app.R;
import me.linkcube.app.core.entity.UserEntity;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.util.FormatUtils;
import me.linkcube.app.widget.ViewUtils;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 状态栏控件
 * 
 * @author orange
 * 
 */
public class SingleStatusBarView extends LinearLayout {

	private ImageButton avatarBtn;

	private TextView userNameTv;

	private ImageButton settingBtn;

	private Button connectIndicatorBtn;

	private Button resetBtn;

	private OnSingleStatusBarClickListener mListener;

	public SingleStatusBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SingleStatusBarView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.single_status_bar_view, this,
				true);
		avatarBtn = (ImageButton) view.findViewById(R.id.avatar_btn);
		settingBtn = (ImageButton) view.findViewById(R.id.setting_btn);
		userNameTv = (TextView) view.findViewById(R.id.user_name_tv);
		connectIndicatorBtn = (Button) view
				.findViewById(R.id.connect_indicator_btn);
		resetBtn = (Button) view.findViewById(R.id.reset_btn);
		avatarBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserManager.getInstance().isAuthenticated()) {
					mListener.showUserInfoActivity();
				} else {
					mListener.showLoginActivity();
				}
			}
		});
		settingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.showMoreSettingActivity();
			}
		});

		connectIndicatorBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.showBluetoothSettingActivity();
			}
		});

		/*
		 * resetBtn.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { mListener.resetToy(); } });
		 */
		resetBtn.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					resetBtn.setBackgroundResource(R.drawable.btn_reset_click);
					mListener.resetToy();
					break;
				case MotionEvent.ACTION_UP:
					resetBtn.setBackgroundResource(R.drawable.btn_reset_normal);
				default:
					break;
				}
				return false;
			}
		});

	}

	public void setUserInfo(UserEntity userEntity) {
		if (userEntity == null) {
			avatarBtn.setImageResource(R.drawable.avatar_male_default);
			userNameTv.setText("请登录");
			return;
		} else {
			Drawable avatar = null;
			if (userEntity.getUserAvatar() == null) {
				avatarBtn.setImageResource(UserManager.getInstance()
						.setUserDefaultAvatar(userEntity.getUserGender()));
			} else {
				avatar = FormatUtils.Bytes2Drawable(userEntity.getUserAvatar());
				avatarBtn.setImageDrawable(avatar);
			}
			userNameTv.setText(userEntity.getNickName());
		}
	}

	public void setBluetoothState(boolean success) {
		int resId = success ? R.drawable.btn_connect_indicator_on
				: R.drawable.btn_connect_indicator_off;
		connectIndicatorBtn.setBackgroundResource(resId);
		ViewUtils.setInvisible(resetBtn, !success);
	}

	public void setOnSingleStatusBarClickListener(
			OnSingleStatusBarClickListener listener) {
		this.mListener = listener;
	}
}
