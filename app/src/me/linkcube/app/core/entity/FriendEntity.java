package me.linkcube.app.core.entity;

import org.jivesoftware.smackx.packet.VCard;

public class FriendEntity {

	private String userJid;

	private String friendJid;

	private String nickName;

	private byte[] userAvatar;

	private String personState;

	private String userAge;

	private String birthday;

	private String astrology;

	private String userGender;
	
	private String isFriend;

	public FriendEntity() {

	}

	public FriendEntity(VCard vCard) {

	}

	public String getUserJid() {
		return userJid;
	}

	public void setUserJid(String userJid) {
		this.userJid = userJid;
	}

	public String getFriendJid() {
		return friendJid;
	}

	public void setFriendJid(String friendJid) {
		this.friendJid = friendJid;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public byte[] getUserAvatar() {
		return userAvatar;
	}

	public void setUserAvatar(byte[] userAvatar) {
		this.userAvatar = userAvatar;
	}

	public String getPersonState() {
		return personState;
	}

	public void setPersonState(String personState) {
		this.personState = personState;
	}

	public String getUserAge() {
		return userAge;
	}

	public void setUserAge(String userAge) {
		this.userAge = userAge;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAstrology() {
		return astrology;
	}

	public void setAstrology(String astrology) {
		this.astrology = astrology;
	}

	public String getUserGender() {
		return userGender;
	}

	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}

	public String getIsFriend() {
		return isFriend;
	}

	public void setIsFriend(String isFriend) {
		this.isFriend = isFriend;
	}

	
}
