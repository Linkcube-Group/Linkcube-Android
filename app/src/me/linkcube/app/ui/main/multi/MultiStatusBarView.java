package me.linkcube.app.ui.main.multi;

import me.linkcube.app.R;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.entity.UserEntity;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.util.FormatUtils;
import me.linkcube.app.util.RegexUtils;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MultiStatusBarView extends LinearLayout {

	private ImageButton userAvatarBtn, targetAvatarBtn;

	private TextView userNameTv, targetNameTv;

	private Button connectIndicatorBtn, settingBtn;

	private OnMultiStatusBarClickListener mListener;

	public MultiStatusBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MultiStatusBarView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.multi_status_bar_view, this,
				true);
		userNameTv = (TextView) view.findViewById(R.id.user_name_tv);
		userAvatarBtn = (ImageButton) view.findViewById(R.id.user_avatar_btn);
		targetAvatarBtn = (ImageButton) view
				.findViewById(R.id.target_avatar_btn);
		targetNameTv = (TextView) view.findViewById(R.id.target_name_tv);
		connectIndicatorBtn = (Button) view
				.findViewById(R.id.connect_indicator_btn);
		settingBtn = (Button) view.findViewById(R.id.setting_btn);
		userAvatarBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.showUserInfoActivity();
			}
		});
		targetAvatarBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserManager.getInstance().isMultiPlaying()) {
					mListener.showTargetInfoActivity();
				}

			}
		});

		connectIndicatorBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.showBluetoothSettingActivity();
			}
		});

		settingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.showSettingActivity();
			}
		});

	}

	/**
	 * 设置连接指示器状态
	 * 
	 * @param success
	 */
	public void setIndicatorState(boolean success) {
		int resId = success ? R.drawable.btn_connect_indicator_on
				: R.drawable.btn_connect_indicator_off;
		connectIndicatorBtn.setBackgroundResource(resId);
	}

	public void setUserInfo(UserEntity userEntity) {
		if (userEntity == null) {
			return;
		} else {
			Drawable avatar = null;
			if (userEntity.getUserAvatar() == null) {
				userAvatarBtn.setImageResource(UserManager.getInstance()
						.setUserDefaultAvatar(userEntity.getUserGender()));
			} else {
				avatar = FormatUtils.Bytes2Drawable(userEntity.getUserAvatar());
				userAvatarBtn.setImageDrawable(avatar);
			}

			userNameTv
					.setText(RegexUtils.cutUserName(userEntity.getNickName()));
		}

	}

	public void setTargetInfo(FriendEntity friendEntity) {

		if (friendEntity == null) {
			String Gender = UserManager.getInstance().getUserInfo()
					.getUserGender();
			if (Gender.equals("男")) {
				targetAvatarBtn
						.setImageResource(R.drawable.avatar_female_default);
			} else {
				targetAvatarBtn
						.setImageResource(R.drawable.avatar_male_default);
			}
			return;
		}
		Drawable avatar = null;
		if (friendEntity.getUserAvatar() == null) {
			targetAvatarBtn.setImageResource(UserManager.getInstance()
					.setUserDefaultAvatar(friendEntity.getUserGender()));
		} else {
			avatar = FormatUtils.Bytes2Drawable(friendEntity.getUserAvatar());
			targetAvatarBtn.setImageDrawable(avatar);
		}
		targetNameTv
				.setText(RegexUtils.cutUserName(friendEntity.getNickName()));
	}

	public void setMultiStatusBarClickListener(
			OnMultiStatusBarClickListener listener) {
		this.mListener = listener;
	}

	public interface OnMultiStatusBarClickListener {

		void showUserInfoActivity();

		void showTargetInfoActivity();

		void showSettingActivity();

		void showBluetoothSettingActivity();

	}
}
