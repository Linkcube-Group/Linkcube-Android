package me.linkcube.app.ui.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.linkcube.app.R;
import me.linkcube.app.core.entity.ChatMsgEntity;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.entity.UserEntity;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableFriend;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.util.FormatUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.*;

public class ChatMsgViewAdapter extends BaseAdapter {

	private final static int IMVT_COM_MSG = 0;

	private final static int IMVT_TO_MSG = 1;

	private List<ChatMsgEntity> collection;

	private Context context;

	private LayoutInflater mInflater;

	private String friendName;


	public ChatMsgViewAdapter(Context _context,
			List<ChatMsgEntity> _collection, String _friendName) {
		context = _context;
		this.collection = _collection;
		mInflater = LayoutInflater.from(_context);
		friendName = _friendName;
	}

	public int getCount() {
		return collection.size();
	}

	public Object getItem(int position) {
		return collection.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		ChatMsgEntity entity = collection.get(position);

		if (entity.getMsgType()) {
			return IMVT_COM_MSG;
		} else {
			return IMVT_TO_MSG;
		}

	}

	public int getViewTypeCount() {
		return 2;
	}

	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {

		ChatMsgEntity entity = collection.get(position);
		boolean isComMsg = entity.getMsgType();

		ViewHolder viewHolder = null;
		if (convertView == null) {

			viewHolder = new ViewHolder();
			if (isComMsg) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_left, null);
				viewHolder.userAvatarIv = (ImageView) convertView
						.findViewById(R.id.chat_friend_avatar_iv);
				viewHolder.userAvatarIv.setImageDrawable(getFriendAvatar());
			} else {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_right, null);

				viewHolder.userAvatarIv = (ImageView) convertView
						.findViewById(R.id.chat_user_avatar_iv);
				viewHolder.userAvatarIv.setImageDrawable(getUserAvatar());
			}
			viewHolder.sendTimeTv = (TextView) convertView
					.findViewById(R.id.tv_sendtime);
			viewHolder.userNameTv = (TextView) convertView
					.findViewById(R.id.tv_username);
			viewHolder.MessageTv = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);
			viewHolder.isComMsg = isComMsg;

			viewHolder.delAfterReadTimeTv = (TextView) convertView
					.findViewById(R.id.del_after_read_time_tv);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.sendTimeTv.setText(entity.getDate());
		viewHolder.userNameTv.setText(entity.getName());
		viewHolder.MessageTv.setText(entity.getText());
		if(entity.getCountDown()==0){
			viewHolder.delAfterReadTimeTv.setText("");
		}else{
			viewHolder.delAfterReadTimeTv.setText(entity.getCountDown() + "");
		}
		
		return convertView;
	}

	static class ViewHolder {
		public ImageView userAvatarIv;
		public TextView sendTimeTv;
		public TextView userNameTv;
		public TextView MessageTv;
		public TextView delAfterReadTimeTv;
		public boolean isComMsg = true;
	}

	private Drawable getFriendAvatar() {
		List<FriendEntity> friendEntities = new ArrayList<FriendEntity>();
		PersistableFriend perFriend = new PersistableFriend();
		Drawable friendAvtar = null;
		try {
			friendEntities = DataManager.getInstance().query(
					perFriend,
					USER_JID + "=? and " + FRIEND_JID + "=?",
					new String[] { ASmackUtils.getUserJID(),
							ASmackUtils.getFriendJid(friendName) }, null, null,
					null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (friendEntities.get(0).getUserAvatar() == null) {
			friendAvtar = context.getResources().getDrawable(
					UserManager.getInstance().setUserDefaultAvatar(
							friendEntities.get(0).getUserGender()));
		} else {
			friendAvtar = FormatUtils.Bytes2Drawable(friendEntities.get(0)
					.getUserAvatar());
		}

		return friendAvtar;
	}

	private Drawable getUserAvatar() {
		Drawable userAvatar = null;
		UserEntity userEntity = UserManager.getInstance().getUserInfo();
		if (userEntity.getUserAvatar() == null) {
			userAvatar = context.getResources().getDrawable(
					UserManager.getInstance().setUserDefaultAvatar(
							userEntity.getUserGender()));
		} else {
			userAvatar = FormatUtils.Bytes2Drawable(userEntity.getUserAvatar());
		}
		return userAvatar;
	}


}
