package me.linkcube.app.sync.user;

import org.jivesoftware.smack.packet.IQ;

public class FindPassword extends IQ {

	private String username;

	public String getBody() {
		StringBuilder sb = new StringBuilder();
		if (null != username) {
			sb.append("<username>").append(username).append("</username>");
		}
		return sb.toString();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getChildElementXML() {
		StringBuilder localStringBuilder = new StringBuilder();
		localStringBuilder.append("<query xmlns=\"jabber:iq:findPassword\">");
		localStringBuilder.append(getBody());
		localStringBuilder.append("</query>");
		return localStringBuilder.toString();
	}

}
