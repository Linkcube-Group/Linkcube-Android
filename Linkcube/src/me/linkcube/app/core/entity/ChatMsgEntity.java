package me.linkcube.app.core.entity;

public class ChatMsgEntity {
	
	private String name;

	private String date;

	private String text;
	
	private int countDown;

	private boolean isComMsg = true;

	public ChatMsgEntity() {
	}

	public ChatMsgEntity(String name, String date, String text, boolean isComMsg) {
		super();
		this.name = name;
		this.date = date;
		this.text = text;
		this.isComMsg = isComMsg;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean getMsgType() {
		return isComMsg;
	}

	public void setMsgType(boolean isComMsg) {
		isComMsg = isComMsg;
	}

	public int getCountDown() {
		return countDown;
	}

	public void setCountDown(int countDown) {
		this.countDown = countDown;
	}
	
	
}
