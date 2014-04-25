package me.linkcube.app.core.user;


import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.ChatEntity;
import me.linkcube.app.core.entity.ChatMsgEntity;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableChat;
import me.linkcube.app.sync.core.ASmackUtils;
import me.linkcube.app.util.TimeUtils;

public class UserUtils {

	/**
	 * 设置聊天界面显示的记录对象
	 * 
	 * @param body
	 * @param nickName
	 * @param from
	 * @return
	 */
	public static ChatMsgEntity setChatMsgEntity(String body, String nickName,
			boolean from) {
		ChatMsgEntity entity = new ChatMsgEntity();
		entity.setDate(TimeUtils.getNowTime());
		entity.setName(nickName);
		entity.setMsgType(from);
		entity.setText(body);
		Timber.i("messageresult:" + body);
		return entity;
	}

	/**
	 * 向对方发送普通消息
	 * 
	 * @param body
	 */
	public static ChatMsgEntity sendToFriendMsg(String body, String friendName) {
		ChatMsgEntity entity = new ChatMsgEntity();
		if (body.length() > 0) {
			entity.setDate(TimeUtils.getNowTime());
			entity.setName(UserManager.getInstance().getUserInfo()
					.getNickName());
			entity.setMsgType(false);
			entity.setText(body);

			// 保存到数据库
			ChatEntity chatEntity = new ChatEntity();
			chatEntity.setUserName(ASmackUtils.getRosterName());
			chatEntity.setFriendName(friendName);
			chatEntity.setFriendNickname(FriendManager.getInstance().getFriendNicknameByFriendName(friendName));
			chatEntity.setMsgFlag("send");
			chatEntity.setMessage(body);
			chatEntity.setMsgTime(TimeUtils.getNowDateAndTime());

			PersistableChat perChat = new PersistableChat();
			DataManager.getInstance().insert(perChat, chatEntity);

		}
		return entity;
	}
}
