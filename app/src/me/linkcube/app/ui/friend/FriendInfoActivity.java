package me.linkcube.app.ui.friend;

import java.util.ArrayList;
import java.util.List;

import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.*;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import me.linkcube.app.R;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableFriend;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.ui.DialogActivity;
import me.linkcube.app.ui.chat.ChatActivity;
import me.linkcube.app.util.FormatUtils;

public class FriendInfoActivity extends DialogActivity implements
		OnClickListener {

	private ImageView friendInfoAvatarIv;
	private TextView friendNicknameTv;
	private ImageView friendGenderIv;
	private TextView friendAgeTv;
	private TextView friendEmailIdTv;// 连酷id
	private TextView friendPersonStateTv;
	private Button sendMsgBtn;

	private String friendName;

	private String MALE = "男";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_info_activity);
		configureActionBar(R.string.detail_info);

		initView();
		initData();
	}

	private void initView() {
		friendInfoAvatarIv = (ImageView) findViewById(R.id.friend_info_avatar_iv);
		friendGenderIv = (ImageView) findViewById(R.id.friend_gender_iv);
		friendNicknameTv = (TextView) findViewById(R.id.friend_nickname_tv);
		friendAgeTv = (TextView) findViewById(R.id.friend_age_tv);
		friendEmailIdTv = (TextView) findViewById(R.id.friend_email_tv);
		friendPersonStateTv = (TextView) findViewById(R.id.friend_person_state_tv);
		sendMsgBtn = (Button) findViewById(R.id.send_msg_btn);
		sendMsgBtn.setOnClickListener(this);
	}

	private void initData() {
		Bundle bundle = getIntent().getExtras();
		friendName = bundle.getString("friendname");
		Timber.d("nickName:" + friendName);
		// 从数据库中查询出好友信息显示
		List<FriendEntity> friendEntities = new ArrayList<FriendEntity>();
		PersistableFriend perFriend = new PersistableFriend();
		try {
			friendEntities = DataManager.getInstance().query(
					perFriend,
					FRIEND_JID + "=? and " + USER_JID + "=? ",
					new String[] { ASmackUtils.getFriendJid(friendName),
							ASmackUtils.getUserJID() }, null, null, null);
			FriendEntity friendEntity = new FriendEntity();
			friendEntity = friendEntities.get(0);
			friendNicknameTv.setText(friendEntity.getNickName());
			friendAgeTv.setText(friendEntity.getUserAge());
			friendPersonStateTv.setText(friendEntity.getPersonState());
			String friendName = ASmackUtils.deleteServerAddress(friendEntity
					.getFriendJid());
			friendEmailIdTv.setText(ASmackUtils.userNameDecode(friendName));
			if (friendEntity.getUserGender().equals(MALE)) {
				friendGenderIv.setBackgroundResource(R.drawable.ic_male);
			} else {
				friendGenderIv.setBackgroundResource(R.drawable.ic_female);
			}
			Drawable friendAvatar = null;
			if (friendEntity.getUserAvatar() == null) {
				friendAvatar = getResources().getDrawable(
						UserManager.getInstance().setUserDefaultAvatar(
								friendEntity.getUserGender()));
			} else {
				friendAvatar = FormatUtils.Bytes2Drawable(friendEntity
						.getUserAvatar());
			}

			friendInfoAvatarIv.setImageDrawable(friendAvatar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send_msg_btn:
			Intent intent = new Intent(FriendInfoActivity.this,
					ChatActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("friendName", friendName);
			intent.putExtras(bundle);
			startActivity(intent);
			this.finish();
			break;
		default:
			break;
		}
	}
}
