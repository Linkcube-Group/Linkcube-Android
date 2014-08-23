package me.linkcube.app.core.entity;

/**
 * 用户个人信息表
 * 
 * @author Rodriguez-xin
 * 
 */
public class UserEntity {

	private String JID;

	private String nickName;

	private byte[] userAvatar;

	private String linkcubeId;

	private String personState;

	private String userAge;

	private String birthday;

	private String astrology;

	private String userGender;

	public UserEntity() {

	}

	public String getJID() {
		return JID;
	}

	public void setJID(String jID) {
		JID = jID;
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

	public String getLinkcubeId() {
		return linkcubeId;
	}

	public void setLinkcubeId(String linkcubeId) {
		this.linkcubeId = linkcubeId;
	}

	public String getUserGender() {
		return userGender;
	}

	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}

}
