package me.linkcube.app.ui.friend;

import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;

import com.actionbarsherlock.view.MenuItem;

import me.linkcube.app.R;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.entity.FriendRequestEntity;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableFriend;
import me.linkcube.app.core.persistable.PersistableFriendRequest;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.sync.friend.AddFriend;
import me.linkcube.app.sync.friend.GetFriendVCard;
import me.linkcube.app.ui.DialogActivity;
import me.linkcube.app.util.FormatUtils;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StrangerInfoActivity extends DialogActivity implements
		OnClickListener {

	private RelativeLayout solveRequestRl;
	private RelativeLayout addStrangerRl;

	private ImageView strangerInfoAvatarIv;
	private TextView strangerNicknameTv;
	private ImageView strangerGenderIv;
	private TextView strangerAgeTv;
	private TextView strangerEmailTv;
	private TextView strangerPersonStateTv;
	private Button acceptStrangerRequestBtn;
	private Button refuseStrangerRequestBtn;
	private Button addStrangerBtn;

	private String strangerName;
	private String friendNickName;
	private String isFriend;
	private String MALE = "男";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stranger_info_activity);
		configureActionBar(R.string.detail_info);
		initView();
		initData();
	}

	private void initView() {
		solveRequestRl = (RelativeLayout) findViewById(R.id.solve_stranger_request_rl);
		addStrangerRl = (RelativeLayout) findViewById(R.id.add_stranger_request_rl);

		strangerInfoAvatarIv = (ImageView) findViewById(R.id.stranger_info_avatar_iv);
		strangerGenderIv = (ImageView) findViewById(R.id.stranger_gender_iv);
		strangerNicknameTv = (TextView) findViewById(R.id.stranger_nickname_tv);
		strangerAgeTv = (TextView) findViewById(R.id.stranger_age_tv);
		strangerEmailTv = (TextView) findViewById(R.id.stranger_email_tv);
		strangerPersonStateTv = (TextView) findViewById(R.id.stranger_person_state_tv);

		acceptStrangerRequestBtn = (Button) findViewById(R.id.accept_stranger_request_btn);
		acceptStrangerRequestBtn.setOnClickListener(this);
		refuseStrangerRequestBtn = (Button) findViewById(R.id.refuse_stranger_request_btn);
		refuseStrangerRequestBtn.setOnClickListener(this);
		addStrangerBtn = (Button) findViewById(R.id.add_stranger_btn);
		addStrangerBtn.setOnClickListener(this);
	}

	private void initData() {
		Bundle bundle = getIntent().getExtras();
		strangerName = bundle.getString("friendname");
		isFriend = bundle.getString("isFriend");

		if (isFriend.equals("fromFriend")) {// 他人加当前用户的界面
			solveRequestRl.setVisibility(View.VISIBLE);
			addStrangerRl.setVisibility(View.INVISIBLE);
		} else if (isFriend.equals("toFriend")) {// 加别人为好友界面
			addStrangerRl.setVisibility(View.VISIBLE);
			solveRequestRl.setVisibility(View.INVISIBLE);
		}

		showProgressDialog("正在获取中。。。");
		new GetFriendVCard(ASmackUtils.getFriendJid(strangerName),
				new ASmackRequestCallBack() {

					@Override
					public void responseSuccess(Object object) {
						dismissProgressDialog();
						try {
							VCard vCard = (VCard) object;
							Timber.d(vCard.toXML());
							if (vCard.getAvatar() == null) {
								if (vCard.getField(Const.VCard.GENDER).equals(
										MALE)) {
									strangerInfoAvatarIv
											.setImageResource(R.drawable.avatar_male_default);
								} else {
									strangerInfoAvatarIv
											.setImageResource(R.drawable.avatar_female_default);
								}
							} else {
								Drawable userAvatar = FormatUtils
										.Bytes2Drawable(vCard.getAvatar());
								strangerInfoAvatarIv
										.setImageDrawable(userAvatar);
							}
							friendNickName = vCard.getNickName();
							strangerNicknameTv.setText(friendNickName);
							String userAge = ASmackUtils.getUserAge(vCard
									.getField(Const.VCard.BIRTHDAY));
							strangerAgeTv.setText(userAge);
							strangerPersonStateTv.setText(vCard
									.getField(Const.VCard.PERSONSTATE));
							strangerEmailTv.setText(ASmackUtils
									.userNameDecode(strangerName));
							if (vCard.getField(Const.VCard.GENDER).equals(MALE)) {
								strangerGenderIv
										.setBackgroundResource(R.drawable.ic_male);
							} else {
								strangerGenderIv
										.setBackgroundResource(R.drawable.ic_female);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void responseFailure(int reflag) {

					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.accept_stranger_request_btn:
			/*try {
				ASmackManager
						.getInstance()
						.getXMPPConnection()
						.getRoster()
						.createEntry(ASmackUtils.getFriendJid(strangerName),
								strangerName, null);
			} catch (XMPPException e1) {
				e1.printStackTrace();
			}*/
			showProgressDialog("正在添加好友");
			new AddFriend(strangerName, strangerName, new ASmackRequestCallBack() {
				
				@Override
				public void responseSuccess(Object object) {
					dismissProgressDialog();
					PersistableFriend perFriend = new PersistableFriend();
					List<FriendEntity> friendEntities = new ArrayList<FriendEntity>();

					try {
						friendEntities = DataManager.getInstance().query(
								perFriend,
								USER_JID + "=? and " + FRIEND_JID + "=? ",
								new String[] { ASmackUtils.getUserJID(),
										ASmackUtils.getFriendJid(strangerName) }, null,
								null, null);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					if (friendEntities != null && !friendEntities.isEmpty()) {
						FriendEntity friendEntity = friendEntities.get(0);
						friendEntity.setIsFriend("both");
					} else {
						// 添加到好友列表
						VCard vCard = new VCard();
						try {
							vCard.load(ASmackManager.getInstance().getXMPPConnection(),
									ASmackUtils.getFriendJid(strangerName));
						} catch (XMPPException e) {
							e.printStackTrace();
						}
						FriendEntity friendEntity = new FriendEntity();
						friendEntity.setUserJid(ASmackUtils.getUserJID());
						friendEntity.setFriendJid(ASmackUtils
								.getFriendJid(strangerName));
						friendEntity.setNickName(vCard.getNickName());
						friendEntity.setUserAvatar(vCard.getAvatar());
						friendEntity.setUserGender(vCard.getField(Const.VCard.GENDER));
						friendEntity.setBirthday(vCard.getField(Const.VCard.BIRTHDAY));
						friendEntity.setPersonState(vCard
								.getField(Const.VCard.PERSONSTATE));

						String userAge = ASmackUtils.getUserAge(vCard
								.getField(Const.VCard.BIRTHDAY));
						friendEntity.setUserAge(userAge);
						friendEntity.setIsFriend("both");
						DataManager.getInstance().insert(perFriend, friendEntity);
					}
					// 添加到好友请求列表
					PersistableFriendRequest perFriendRequest = new PersistableFriendRequest();
					FriendRequestEntity friendRequestEntity = new FriendRequestEntity();
					friendRequestEntity.setFriendName(strangerName);
					friendRequestEntity.setUserName(ASmackUtils.getRosterName());
					friendRequestEntity.setSubscription("both");
					DataManager.getInstance().update(perFriendRequest,
							friendRequestEntity);
					dismissProgressDialog();
					Toast.makeText(StrangerInfoActivity.this,
							"您添加了" + friendNickName + "为好友", Toast.LENGTH_SHORT).show();
					StrangerInfoActivity.this.finish();
				}
				
				@Override
				public void responseFailure(int reflag) {
					Toast.makeText(StrangerInfoActivity.this,
							"添加失败，请重试", Toast.LENGTH_SHORT).show();
					dismissProgressDialog();
				}
			});
			/*
			 * Presence subscribedPresence = new
			 * Presence(Presence.Type.subscribed);
			 * subscribedPresence.setTo(strangerName);
			 * XmppManager.getInstance().
			 * getXMPPConnection().sendPacket(subscribedPresence); Presence
			 * subscribePresence = new Presence(Presence.Type.subscribe);
			 * subscribePresence.setTo(strangerName);
			 * XmppManager.getInstance().getXMPPConnection
			 * ().sendPacket(subscribePresence);
			 */

			break;
		case R.id.refuse_stranger_request_btn:
			Presence refusePresence = new Presence(Presence.Type.unsubscribe);
			refusePresence.setTo(ASmackUtils.getFriendJid(strangerName));
			ASmackManager.getInstance().getXMPPConnection()
					.sendPacket(refusePresence);

			FriendRequestEntity deleteFriendRequestEntity = new FriendRequestEntity();
			deleteFriendRequestEntity.setUserName(ASmackUtils.getRosterName());
			deleteFriendRequestEntity.setFriendName(strangerName);
			PersistableFriendRequest deletePerFriendRequest = new PersistableFriendRequest();
			DataManager.getInstance().delete(deletePerFriendRequest,
					deleteFriendRequestEntity);
			this.finish();
			break;
		case R.id.add_stranger_btn:

			new AddFriend(strangerName, strangerName,
					new ASmackRequestCallBack() {

						@Override
						public void responseSuccess(Object object) {
							Toast.makeText(StrangerInfoActivity.this,
									"好友请求已发送", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void responseFailure(int reflag) {
							Toast.makeText(StrangerInfoActivity.this, "添加好友失败",
									Toast.LENGTH_SHORT).show();
						}
					});
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, SearchFriendActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			this.finish();
			startActivity(intent);
			break;
		default:
			break;
		}
		return false;
	}
}
