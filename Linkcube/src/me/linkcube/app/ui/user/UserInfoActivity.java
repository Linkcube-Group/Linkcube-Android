package me.linkcube.app.ui.user;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import me.linkcube.app.R;
import me.linkcube.app.common.ui.DialogActivity;
import me.linkcube.app.common.util.FormatUtils;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.UserEntity;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.sync.core.ASmackUtils;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserInfoActivity extends DialogActivity implements OnClickListener {

	private LinearLayout userInfoNicknameLl;
	private LinearLayout userInfoGenderLl;
	private LinearLayout userInfoBirthdayLl;
	private LinearLayout userInfoPersonStateLl;

	private Button actionbarFirstBtn;

	private ImageView userAvatorIv;
	private TextView userInfoNicknameTv;
	private TextView userInfoGenderTv;
	private TextView userInfoBirthdayTv;
	private TextView userInfoPersonStateTv;
	private TextView userInfoLinkcubeIDTv;// 连酷id，以后使用
	private TextView userInfoEmailTv;
	private Button updateUserInfoBtn;
	private int IMAGE_CODE = 2;
	private int CHANGE_NICKNAME = 3;
	private int CHANGE_PERSON_STATE = 4;
	private String TAG = "UserInfoActivity";
	private String[] isMale;
	private VCard vCard;
	private Calendar calendar;
	private Dialog dialog;
	Bitmap bitmap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info_activity);

		configureActionBar(R.string.user_info);

		initView();

		initData();
	}

	private void initView() {
		userInfoNicknameLl = (LinearLayout) findViewById(R.id.user_info_nickname_ll);
		userInfoNicknameLl.setOnClickListener(this);
		userInfoBirthdayLl = (LinearLayout) findViewById(R.id.user_info_birthday_ll);
		userInfoBirthdayLl.setOnClickListener(this);
		userInfoGenderLl = (LinearLayout) findViewById(R.id.user_info_gender_ll);
		userInfoGenderLl.setOnClickListener(this);
		userInfoPersonStateLl = (LinearLayout) findViewById(R.id.user_info_person_state_ll);
		userInfoPersonStateLl.setOnClickListener(this);
		userAvatorIv = (ImageView) findViewById(R.id.user_info_avator_iv);
		// TODO 修改头像部分，已实现，网络问题暂不添加
		// userAvatorIv.setOnClickListener(this);
		userInfoNicknameTv = (TextView) findViewById(R.id.user_info_nickname_tv);
		userInfoGenderTv = (TextView) findViewById(R.id.user_info_gender_tv);
		userInfoBirthdayTv = (TextView) findViewById(R.id.user_info_birthday_tv);
		userInfoPersonStateTv = (TextView) findViewById(R.id.user_info_person_state_tv);
		userInfoLinkcubeIDTv = (TextView) findViewById(R.id.user_info_linkcubeid_tv);
		userInfoEmailTv = (TextView) findViewById(R.id.user_info_email_tv);
		updateUserInfoBtn = (Button) findViewById(R.id.update_user_info_btn);
		updateUserInfoBtn.setOnClickListener(this);

		actionbarFirstBtn = (Button) actionbarView
				.findViewById(R.id.actionbar_first_btn);
		actionbarFirstBtn.setVisibility(View.VISIBLE);
		actionbarFirstBtn.setText(R.string.change_pwd);
		actionbarFirstBtn.setOnClickListener(this);

	}

	private void initData() {
		isMale = new String[] { getResources().getString(R.string.female),
				getResources().getString(R.string.male) };
		calendar = Calendar.getInstance();

		Thread thread = new Thread() {

			@Override
			public void run() {
				vCard = new VCard();
				try {
					vCard.load(ASmackManager.getInstance().getXMPPConnection());
					Timber.d("get vcard.");
				} catch (XMPPException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();

		UserEntity userEntity = UserManager.getInstance().getUserInfo();

		Timber.d("userEntity.getNickName:" + userEntity.getNickName());
		userInfoNicknameTv.setText(userEntity.getNickName());
		if (userEntity.getUserGender().equals("女")) {
			userInfoGenderTv.setText(getResources().getString(R.string.female));
		}else{
			userInfoGenderTv.setText(getResources().getString(R.string.male));
		}

		userInfoBirthdayTv.setText(userEntity.getBirthday());
		userInfoPersonStateTv.setText(userEntity.getPersonState());
		userInfoEmailTv.setText(ASmackUtils.userNameDecode(ASmackUtils
				.deleteServerAddress(userEntity.getJID())));
		if (userEntity.getUserAvatar() == null) {
			userAvatorIv.setImageResource(UserManager.getInstance()
					.setUserDefaultAvatar(userEntity.getUserGender()));
		} else {
			Drawable userAvatar = FormatUtils.Bytes2Drawable(userEntity
					.getUserAvatar());
			bitmap = FormatUtils.drawable2Bitmap(userAvatar);
			userAvatorIv.setImageDrawable(userAvatar);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_first_btn:
			startActivity(new Intent(UserInfoActivity.this,
					ChangePwdActivity.class));
			break;
		case R.id.user_info_avator_iv:
			getAlbumPicture();
			break;
		case R.id.user_info_nickname_ll:
			Intent nickNameIntent = new Intent(UserInfoActivity.this,
					UpdateUserInfoActivity.class);
			nickNameIntent.putExtra("information", userInfoNicknameTv.getText()
					.toString());
			nickNameIntent.putExtra("requestCode", CHANGE_NICKNAME);
			startActivityForResult(nickNameIntent, CHANGE_NICKNAME);
			break;
		case R.id.user_info_gender_ll:

			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.gender))
					.setSingleChoiceItems(isMale, 0,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									userInfoGenderTv.setText(isMale[which]);
									dialog.dismiss();
									vCard.setField(Const.VCard.GENDER,
											userInfoGenderTv.getText()
													.toString());
									updateUserInfoBtn
											.setVisibility(View.VISIBLE);
									if (bitmap == null) {
										Timber.d(isMale[which]);
										if (isMale[which].equals(getResources()
												.getString(R.string.male))) {
											userAvatorIv
													.setImageResource(R.drawable.avatar_male_default);
											userAvatorIv.invalidate();
										} else {

											userAvatorIv
													.setImageResource(R.drawable.avatar_female_default);
											userAvatorIv.invalidate();
										}
									}
								}

							}).show();

			break;
		case R.id.user_info_birthday_ll:

			DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker datePicker, int year,
						int month, int dayOfMonth) {
					// Calendar月份是从0开始,所以month要加1
					userInfoBirthdayTv.setText(year + "-" + (month + 1) + "-"
							+ dayOfMonth);
					vCard.setField(Const.VCard.BIRTHDAY, userInfoBirthdayTv
							.getText().toString());
					updateUserInfoBtn.setVisibility(View.VISIBLE);
				}
			};

			dialog = new DatePickerDialog(this, dateListener,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
			dialog.show();
			break;
		case R.id.user_info_person_state_ll:
			Intent personStateIntent = new Intent(UserInfoActivity.this,
					UpdateUserInfoActivity.class);
			personStateIntent.putExtra("information", userInfoPersonStateTv
					.getText().toString());
			personStateIntent.putExtra("requestCode", CHANGE_PERSON_STATE);
			startActivityForResult(personStateIntent, CHANGE_PERSON_STATE);
			break;
		case R.id.update_user_info_btn:
			try {
				vCard.save(ASmackManager.getInstance().getXMPPConnection());
			} catch (XMPPException e) {
				e.printStackTrace();
			}
			UserManager.getInstance().saveUserInfo(this, vCard);
			this.finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 从相册获取图片并上传
	 */
	private void getAlbumPicture() {
		Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
		getAlbum.setType("image/*");
		startActivityForResult(getAlbum, IMAGE_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			Log.i(TAG, "get wrong");
			return;
		}
		updateUserInfoBtn.setVisibility(View.VISIBLE);
		try {
			vCard.load(ASmackManager.getInstance().getXMPPConnection());
		} catch (XMPPException e) {
			e.printStackTrace();
		}

		if (requestCode == IMAGE_CODE) {
			ContentResolver resolver = getContentResolver();
			Uri uri = data.getData();
			try {
				bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
				// userAvatorIv.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			userAvatorIv.setImageBitmap(bitmap);
			byte[] userAvatar = FormatUtils.Bitmap2Bytes(bitmap);
			vCard.setAvatar(userAvatar);
		} else if (requestCode == CHANGE_NICKNAME) {
			Timber.d(data.getStringExtra("returnInfo"));
			String nickName = data.getStringExtra("returnInfo");
			userInfoNicknameTv.setText(nickName);
			vCard.setNickName(nickName);
		} else if (requestCode == CHANGE_PERSON_STATE) {
			Timber.d(data.getStringExtra("returnInfo"));
			String personState = data.getStringExtra("returnInfo");
			userInfoPersonStateTv.setText(personState);
			vCard.setField(Const.VCard.PERSONSTATE, personState);
		}
	}

}
