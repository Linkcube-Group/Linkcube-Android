package me.linkcube.app.ui.user;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import me.linkcube.app.R;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.ui.BaseActivity;
import me.linkcube.app.ui.main.MainActivity;
import me.linkcube.app.util.FormatUtils;
import me.linkcube.app.widget.CWClearEditText;

public class InitUserInfoActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener, OnTouchListener {

	private ImageView initUserAvatarIv;
	private CWClearEditText initUserNickNameEt;
	private RadioGroup initUserInfoGenderRg;
	private RadioButton initUserGenderMaleRb;
	private CWClearEditText initUserBirthdayEt;
	private CWClearEditText initUserPersonStateEt;
	private Button saveInitUserInfoBtn;

	private VCard vCard;
	private Calendar calendar;
	private int IMAGE_CODE = 2;
	private Bitmap bitmap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.init_user_info_activity);

		configureActionBar(R.string.register);

		initView();

		initData();
	}

	private void initView() {
		initUserAvatarIv = (ImageView) findViewById(R.id.init_user_info_avator_iv);
		initUserAvatarIv.setOnClickListener(this);
		initUserNickNameEt = (CWClearEditText) findViewById(R.id.init_user_info_nickname_et);
		initUserInfoGenderRg = (RadioGroup) findViewById(R.id.init_user_info_gender_rg);
		initUserInfoGenderRg.setOnCheckedChangeListener(this);
		initUserGenderMaleRb = (RadioButton) findViewById(R.id.init_user_info_gender_rb_male);
		initUserBirthdayEt = (CWClearEditText) findViewById(R.id.init_user_info_birthday_et);
		initUserBirthdayEt.setOnTouchListener(this);
		initUserPersonStateEt = (CWClearEditText) findViewById(R.id.init_user_info_person_state_tv);
		saveInitUserInfoBtn = (Button) findViewById(R.id.save_init_user_info_btn);
		saveInitUserInfoBtn.setOnClickListener(this);
	}

	private void initData() {
		calendar = Calendar.getInstance();
		vCard = new VCard();
		initUserNickNameEt.setText(ASmackUtils.userNameDecode(ASmackUtils
				.getRosterName()));

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.init_user_info_avator_iv:
			getAlbumPicture();
			break;
		case R.id.save_init_user_info_btn:
			String userGender;
			if (initUserGenderMaleRb.isChecked())
				userGender = "男";
			else {
				userGender = "女";
			}
			try {
				vCard.load(ASmackManager.getInstance().getXMPPConnection());
				if (initUserNickNameEt.getText().toString() == null) {
					vCard.setNickName(ASmackUtils.userNameDecode(ASmackUtils
							.getRosterName()));
				} else {
					vCard.setNickName(initUserNickNameEt.getText().toString());
				}
				vCard.setField(Const.VCard.GENDER, userGender);
				if (initUserPersonStateEt.getText().toString() == null) {
					vCard.setField(Const.VCard.BIRTHDAY, "1990-01-01");
				} else {
					vCard.setField(Const.VCard.BIRTHDAY, initUserBirthdayEt
							.getText().toString());
				}
				vCard.setField(Const.VCard.PERSONSTATE, initUserPersonStateEt
						.getText().toString());
				if (bitmap != null) {
					byte[] userAvatar = FormatUtils.Bitmap2Bytes(bitmap);
					vCard.setAvatar(userAvatar);
				}
				vCard.save(ASmackManager.getInstance().getXMPPConnection());
			} catch (XMPPException e) {
				e.printStackTrace();
			}
			// 保存用户信息
			UserManager.getInstance().saveUserInfo(mActivity, vCard);

			Intent intent = new Intent(InitUserInfoActivity.this,
					MainActivity.class);
			startActivity(intent);
			InitUserInfoActivity.this.finish();
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
			Timber.d("get wrong");
			return;
		}

		if (requestCode == IMAGE_CODE) {

			ContentResolver resolver = getContentResolver();
			Uri uri = data.getData();
			try {
				bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
				initUserAvatarIv.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (bitmap == null) {
			switch (group.getCheckedRadioButtonId()) {
			case R.id.init_user_info_gender_rb_male:
				Timber.d("init_user_info_gender_rb_male");
				initUserAvatarIv
						.setImageResource(R.drawable.avatar_male_default);
				break;
			case R.id.init_user_info_gender_rb_female:
				Timber.d("init_user_info_gender_rb_female");
				initUserAvatarIv
						.setImageResource(R.drawable.avatar_female_default);
				break;

			default:
				break;
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		super.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			switch (v.getId()) {
			case R.id.init_user_info_birthday_et:
				Timber.d("onTouch");
				DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker datePicker, int year,
							int month, int dayOfMonth) {
						// Calendar月份是从0开始,所以month要加1
						initUserBirthdayEt.setText(year + "-" + (month + 1)
								+ "-" + dayOfMonth);
					}
				};

				Dialog dialog = new DatePickerDialog(this, dateListener,
						calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH),
						calendar.get(Calendar.DAY_OF_MONTH));
				dialog.show();
				break;

			default:
				break;
			}
		}

		return false;
	}

}
