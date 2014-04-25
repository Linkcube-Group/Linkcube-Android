package me.linkcube.app.core.entity;

/**
 * 存储聊天信息表
 * 
 * @author Rodriguez-xin
 * 
 */
public class ChatEntity {

	private String userName;// 当前用户的名字

	private String friendName;// 好友的名字
	
	private String friendNickname;

	private String msgFlag;// 信息标识

	private String message;// 相互之间的消息

	private String msgTime;// 发送消息的时间

	public ChatEntity() {

	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}
	
	public String getFriendNickname() {
		return friendNickname;
	}

	public void setFriendNickname(String friendNickname) {
		this.friendNickname = friendNickname;
	}
	
	public String getMsgFlag() {
		return msgFlag;
	}

	public void setMsgFlag(String msgFlag) {
		this.msgFlag = msgFlag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMsgTime() {
		return msgTime;
	}

	public void setMsgTime(String msgTime) {
		this.msgTime = msgTime;
	}
	
}
