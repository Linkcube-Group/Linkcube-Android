package me.linkcube.app.core.user;

import static me.linkcube.app.core.Const.DeleteAfterRead.COUNT_DOWN;
import me.linkcube.app.common.util.TimeUtils;
import me.linkcube.app.core.entity.ChatEntity;
import me.linkcube.app.core.entity.ChatMsgEntity;
import me.linkcube.app.core.persistable.DataManager;
import me.linkcube.app.core.persistable.PersistableChat;
import me.linkcube.app.sync.core.ASmackUtils;

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
		entity.setCountDown(COUNT_DOWN);
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
			entity.setDate(TimeUtils.getNowDateAndTime());
			entity.setName(UserManager.getInstance().getUserInfo()
					.getNickName());
			entity.setMsgType(false);
			entity.setText(body);
			entity.setCountDown(COUNT_DOWN);

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
	/**
	 * 删除阅后即焚消息
	 * 
	 * @param body
	 */
	public static ChatMsgEntity deleteMsgAfterRead(String body, String friendName, ChatMsgEntity readAfterEntity) {
		ChatMsgEntity entity = new ChatMsgEntity();
		if (body.length() > 0) {
			entity.setDate(readAfterEntity.getDate());
			entity.setName(UserManager.getInstance().getUserInfo()
					.getNickName());
			entity.setMsgType(readAfterEntity.getMsgType());
			entity.setText(body);

			// 从数据库中更新
			ChatEntity chatEntity = new ChatEntity();
			chatEntity.setUserName(ASmackUtils.getRosterName());
			chatEntity.setFriendName(friendName);
			chatEntity.setFriendNickname(FriendManager.getInstance().getFriendNicknameByFriendName(friendName));
			if(readAfterEntity.getMsgType()==true)
			chatEntity.setMsgFlag("get");
			else{
				chatEntity.setMsgFlag("send");
			}
			chatEntity.setMessage(readAfterEntity.getText());
			chatEntity.setMsgTime(readAfterEntity.getDate());
			chatEntity.setIsAfterRead(1);
			PersistableChat perChat = new PersistableChat();
			//DataManager.getInstance().deleteOne(perChat, chatEntity);
			
			DataManager.getInstance().update(perChat, chatEntity);

		}
		return entity;
	}
}
