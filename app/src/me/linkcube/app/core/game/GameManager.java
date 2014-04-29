package me.linkcube.app.core.game;

import java.util.ArrayList;
import java.util.List;

import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.ChatMsgEntity;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.util.TimeUtils;
import static me.linkcube.app.core.Const.GameInviteMsg.*;

public class GameManager {

	public static GameManager instance = null;

	private boolean isPlaying = false;

	private FriendEntity playMate = null;

	private List<ChatMsgEntity> gameInviteMsgs = new ArrayList<ChatMsgEntity>();

	public static GameManager getInstance() {
		if (instance == null) {
			synchronized (GameManager.class) {
				if (instance == null) {
					instance = new GameManager();
					return instance;
				}
			}
		}
		return instance;
	}

	private GameManager() {

	}

	/**
	 * 判断用户是否在游戏中
	 * 
	 * @return
	 */
	public boolean isPlaying() {
		return isPlaying;
	}

	public FriendEntity getPlayMate() {
		return playMate;
	}

	public void addGameInviteMsgs(String from, String body) {
		/*
		 * ChatMsgEntity chatMsgEntity=new ChatMsgEntity();
		 * chatMsgEntity.setName(from);
		 * chatMsgEntity.setDate(TimeUtils.getNowDateAndTime());
		 * chatMsgEntity.setText(body); gameInviteMsgs.add(chatMsgEntity);
		 */

		int position = -1;
		ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
		for (int i = 0; i < gameInviteMsgs.size(); i++) {
			chatMsgEntity = gameInviteMsgs.get(i);
			if (chatMsgEntity.getName().equals(from)) {
				position = i;
				break;
			}
		}
		if (position != -1) {
			gameInviteMsgs.remove(position);
			chatMsgEntity.setText(GAME_INVITE_BOTH);

		} else {
			chatMsgEntity.setText(body);
		}
		chatMsgEntity.setName(from);
		chatMsgEntity.setDate(TimeUtils.getNowDateAndTime());
		gameInviteMsgs.add(chatMsgEntity);
		Timber.d("chatMsgEntity:" + chatMsgEntity.getText());
	}

	public ChatMsgEntity getGameInviteMsgs(String friendName) {
		Timber.i(gameInviteMsgs.size() + "---33size()");
		if (gameInviteMsgs != null && !gameInviteMsgs.isEmpty()) {
			for (ChatMsgEntity chatMsgEntity : gameInviteMsgs) {
				if (chatMsgEntity.getName().equals(friendName)) {
					Timber.i(chatMsgEntity.getName() + "已找到");
					return chatMsgEntity;
				}
			}
			return null;
		}
		return null;
	}

	public void clearGameInviteMsg() {
		gameInviteMsgs.clear();
	}

	public void delGameInviteMsg(String friendName) {
		Timber.i(gameInviteMsgs.size() + "---size()");
		/*
		 * for (ChatMsgEntity chatMsgEntity : gameInviteMsgs) {
		 * if(chatMsgEntity.getName().equals(friendName)){
		 * gameInviteMsgs.remove(chatMsgEntity);
		 * Timber.i(chatMsgEntity.getName()+"已删除"); } }
		 */
		List<Integer> positions = new ArrayList<Integer>();
		ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
		for (int i = 0; i < gameInviteMsgs.size(); i++) {
			chatMsgEntity = gameInviteMsgs.get(i);
			if (chatMsgEntity.getName().equals(friendName)) {
				positions.add(i);
			}
		}

		for (int position : positions) {
			Timber.i(gameInviteMsgs.get(position).getName() + "已删除");
			gameInviteMsgs.remove(position);
		}

		Timber.i(gameInviteMsgs.size() + "---22size()");
	}
}
