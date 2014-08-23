package com.oplibs.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import me.linkcube.client.LinkDefines;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.filter.*;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.*;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.*;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import com.oplibs.support.*;
import com.oplibs.syncore.*;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/*
 * 重要
 * 聊天服务实现
 * 
 * VCard包含用户信息 昵称、性别、年龄、头像等
 * XMPPConnection 使用了XMPP协议基于XML,
 * Vcard封装用户信息
 * 登陆、连接、发消息等
 * 
 */
public class OnChatServiceCallImp extends android.os.Binder implements
		IOnChatServiceCall {
	private int nextCmd = 0;

	private android.content.Context appContext;

	private XMPPConnection connection;
	private String jidToLogin;
	private String pwdToLogin;
	private String nickNameToLogin;

	private VCard personalCard = new VCard();
	private VCard friendCard = new VCard();

	private FileTransferManager fileTransferManager;
	public final int KMaxCacheCmds = 10;
	private final ArrayList<Message> msgQueue;

	private Boolean sendMessageThreadRunning;
	private Boolean sending;

	//广播某消息
	public void BroadcaseCmdIntent(String action, String name, String msg) {
		Intent intent = new Intent(action);
		intent.putExtra(LinkDefines.Data_From, name);
		intent.putExtra(LinkDefines.Data_CMD, msg);
		this.appContext.sendBroadcast(intent);
	}

	// 消息监听 msgListener
	private PacketListener msgListener = new PacketListener() {
		@Override
		public void processPacket(Packet packet) {
			Message message = (Message) packet;
			BroadcaseCmdIntent(Intents.MessageReceived, message.getFrom(),
					message.getBody());
			Log.e("Get message from:", message.getFrom());
		}
	};

	private SyncPresenceListener presenceListener = null;
	private SyncConnectionListener connectionListener = null;

	private ArrayList<String> msgList;

	//登陆服务器
	private boolean loginServer() {
		try {
			connection.login(jidToLogin, pwdToLogin, "OnCall");
		} catch (XMPPException e) {
			LogHelper.LogException(
					"Could not login user '" + SyncMgr.GetInstance() + "'", e);
			connection.disconnect();
			notifyLoggedInFailed();
			return false;
		}

		try {
			if (connection.isConnected()) {
				ServiceDiscoveryManager discover = new ServiceDiscoveryManager(
						connection);

				fileTransferManager = new FileTransferManager(connection);
				fileTransferManager
						.addFileTransferListener(new SyncFileTransferListener());
			}
		} catch (Exception e) {
			// NotifyLoggedInFailed(e);
			LogHelper.LogException(e);
			return false;
		}

		try {
			Roster roster = connection.getRoster();
			roster.addRosterListener(new SyncRosterListener());

			// Thread.sleep(1000); // let the roster do its job
			// sendRoster();

			OfflineMessageManager offlineMessageManager = new OfflineMessageManager(
					connection);

			// setPresenceState(state, type, mode);

			try {
				offlineMessageManager.deleteMessages();
			} catch (XMPPException e) {
				// NotifyLoggedInFailed(e);
				LogHelper.LogException(
						"The offline messages couldn't be deleted.", e);
			}
		} catch (Exception e) {
			LogHelper.LogException(e);
			// NotifyLoggedInFailed(e);
			return false;
		}
		return true;
	}

	//登陆线程
	class LoginThread extends Thread {
		@Override
		public void run() {
			if (loginServer()) {
				updateCurRoster();
				sendMessageThreadRunning = true;
				messageThread.start();
				notifyLoggedIn();
			}
		}
	};

	//连接线程
	class ConnectThread extends Thread {
		@Override
		public void run() {
			if (connection == null) {
				return;
			}

			try {
				connection.connect();
				try {
					// connection.addPacketListener(new SyncMessageListener(),
					// new PacketTypeFilter(Message.class));
					connection.addPacketListener(msgListener,
							new PacketTypeFilter(Message.class));
					connection.addPacketListener(presenceListener,
							new PacketTypeFilter(Presence.class));
					connection.addConnectionListener(connectionListener);
					notifyConnectted();
				} catch (IllegalStateException e) {
					connection.disconnect();
					notifyDisConnectted(null);
				}
				return;
			} catch (Exception exception) {
				notifyDisConnectted(exception);
				return;
			}
			// if(makeConnection2Server())
			// {
			/*
			 * try { //connection.addPacketListener(new SyncMessageListener(),
			 * new PacketTypeFilter(Message.class));
			 * connection.addPacketListener(msgListener, new
			 * PacketTypeFilter(Message.class));
			 * connection.addPacketListener(presenceListener, new
			 * PacketTypeFilter(Presence.class));
			 * connection.addConnectionListener(connectionListener); }
			 * catch(IllegalStateException e) { connection.disconnect();
			 * NotifyLoggedInFailed(e); return; }
			 */

			/*
			 * try { connection.login(useridToLogin, pwdToLogin, "OnCall"); }
			 * catch (XMPPException e) {
			 * LogHelper.LogException("Could not login user '" +
			 * SyncMgr.GetInstance() + "'", e); connection.disconnect();
			 * NotifyLoggedInFailed(e); return; }
			 * 
			 * try { if (connection.isConnected()) { ServiceDiscoveryManager
			 * discover = new ServiceDiscoveryManager(connection);
			 * 
			 * fileTransferManager = new FileTransferManager(connection);
			 * fileTransferManager.addFileTransferListener(new
			 * SyncFileTransferListener()); } } catch(Exception e) {
			 * NotifyLoggedInFailed(e); LogHelper.LogException(e); return; }
			 * 
			 * try { Roster roster = connection.getRoster();
			 * roster.addRosterListener(new SyncRosterListener());
			 * 
			 * //Thread.sleep(1000); // let the roster do its job
			 * //sendRoster();
			 * 
			 * OfflineMessageManager offlineMessageManager = new
			 * OfflineMessageManager(connection);
			 * 
			 * //setPresenceState(state, type, mode);
			 * 
			 * try { offlineMessageManager.deleteMessages(); } catch
			 * (XMPPException e) { NotifyLoggedInFailed(e);
			 * LogHelper.LogException
			 * ("The offline messages couldn't be deleted.", e); } }
			 * catch(Exception e) { LogHelper.LogException(e);
			 * NotifyLoggedInFailed(e); return; }
			 */
			/*
			 * if(loginServer()) { updateCurRoster(); sendMessageThread.start();
			 * notifyLoggedIn(); }
			 */
			// }
		}
	};

	// private Thread connectThread;

	//消息线程
	class MessageThread extends Thread {
		private Message PopupMessage() {
			Message packet;

			synchronized (msgQueue) {
				if (msgQueue.size() <= 0) {
					return null;
				}

				packet = msgQueue.get(0);

				msgQueue.remove(0);
				return packet;
			}
		}

		@Override
		public void run() {
			while (sendMessageThreadRunning) {
				Message packet = PopupMessage();

				synchronized (connection) {
					if (connection != null && packet != null) {
						try {
							connection.sendPacket(packet);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private Thread messageThread = null;
	private Thread loginThread = null;
	private Thread connectThread = null;

	//构造函数开启listener
	public OnChatServiceCallImp(android.content.Context ctx) {
		appContext = ctx;

		msgQueue = new ArrayList<Message>();

		sendMessageThreadRunning = false;
		sending = false;

		presenceListener = new SyncPresenceListener();
		connectionListener = new SyncConnectionListener();

		msgList = new ArrayList<String>();
	}

	//配置ProviderManager，通信协议的一部分
	private void configurePM(ProviderManager pm) {
		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			Log.w("OnCall",
					"Can't load class for org.jivesoftware.smackx.packet.Time");
		}

		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",
				new RosterExchangeProvider());

		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",
				new MessageEventProvider());

		// Chat State
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
				new XHTMLExtensionProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());

		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());

		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());

		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
				new MUCUserProvider());

		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
				new MUCAdminProvider());

		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
				new MUCOwnerProvider());

		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());

		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}

		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());

		// Offline Message Indicator
		pm.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());

		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());

		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());

		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.SessionExpiredError());
	}

	/*
	 * 配置连接
	 * 设置延迟、XMPP设置、连接配置、安全模式、是否重连
	 * 最终new一个XMPP连接
	 */
	private void configConnection() {
		SmackConfiguration.setPacketReplyTimeout(15000);

		XMPPSettings settings = XMPPSettings.GetInstance();
		ConnectionConfiguration cc = new ConnectionConfiguration(
				settings.GetServerAddress(), settings.GetServerPort());

		// ProviderManager.getInstance().addIQProvider("vCard ", "vcard-temp",
		// new org.jivesoftware.smackx.provider.VCardProvider());
		configurePM(ProviderManager.getInstance());

		cc.setSecurityMode(SecurityMode.disabled);
		cc.setSASLAuthenticationEnabled(false);
		cc.setReconnectionAllowed(true);

		Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);

		connection = new XMPPConnection(cc);
	}

	// sendMessageThreadRunning = true;

	//断开和服务器的连接
	@Override
	public void DisconnectToServer() {
		sendMessageThreadRunning = false;

		try {
			if (messageThread != null) {
				messageThread.join();
			}
		} catch (InterruptedException e) {
			messageThread.destroy();
			LogHelper.LogException(e);
		}
		messageThread = null;

		try {
			if (loginThread != null) {
				loginThread.join();
			}
		} catch (InterruptedException e) {
			loginThread.destroy();
			LogHelper.LogException(e);
		}
		loginThread = null;

		try {
			if (connectThread != null) {
				connectThread.join();
			}
		} catch (InterruptedException e) {
			connectThread.destroy();
			LogHelper.LogException(e);
		}
		connectThread = null;

		if (connection != null) {
			synchronized (connection) {
				if (connection != null) {
					connection.disconnect();
				}

				connection = null;
			}
		}
	}

	//获取binder
	@Override
	public IBinder asBinder() {
		return this;
	}

	//发送信息给用户
	@Override
	public void SendMessageToUser(String jid, String message)
			throws RemoteException {
		if (!jid.contains("@")) {
			jid = jid + XMPPSettings.HostName;
		}
		Message packet = new Message(jid, Message.Type.chat);
		packet.setBody(message);
		synchronized (msgQueue) {
			if (msgQueue.size() < KMaxCacheCmds) {
				Log.i("ChatService pool message:", jid + " " + message);
				msgQueue.add(packet);
			}
		}
	}

	//广播某action
	@Override
	public void BroadcaseIntent(String action) {
		Intent intent = new Intent(action);
		this.appContext.sendBroadcast(intent);
	}

	//是否需要重新连接
	private boolean IsNeedReconnect() {
		if (connection == null) {
			return true;
		}

		XMPPSettings settings = XMPPSettings.GetInstance();

		if (connection.getHost().compareTo(settings.GetServerAddress()) != 0) {
			return true;
		}

		if (connection.getPort() != settings.GetServerPort()) {
			return true;
		}

		return false;
	}

	//更新用户Roster
	private boolean updateCurRoster() {
		String u = connection.getUser();
		if (StringUtils.IsStringNullOrEmpty(u)) {
			return false;
		}

		Roster roster = connection.getRoster();
		SyncMgr user = SyncMgr.GetInstance();
		user.SetRoster(roster);
		return true;
	}

	//通知登陆失败
	private void NotifyLoggedInFailed(Exception exception) {
		LogHelper.LogException("Counldn't connect to server", exception);
		Intent intent = new Intent(Intents.ConnectFailed);
		intent.putExtra("Reason", exception.getMessage());
		appContext.sendBroadcast(intent);
	}

	//通知登陆
	private void notifyLoggedIn() {
		Intent loggedIn = new Intent(Intents.LoggedIn);
		appContext.sendBroadcast(loggedIn);
	}

	//通知登陆失败
	private void notifyLoggedInFailed() {
		Intent loggedIn = new Intent(Intents.LoggedInFailed);
		appContext.sendBroadcast(loggedIn);
	}

	//通知连接成功
	private void notifyConnectted() {
		Intent loggedIn = new Intent(Intents.ConnectSucceed);
		appContext.sendBroadcast(loggedIn);
	}

	//通知断开连接
	private void notifyDisConnectted(Exception exception) {
		// Intent loggedIn = new Intent(Intents.ConnectFailed);
		// context.sendBroadcast(loggedIn);
		Intent intent = new Intent(Intents.ConnectFailed);
		if (exception != null) {
			LogHelper.LogException("Counldn't connect to server", exception);
			intent.putExtra("Reason", exception.getMessage());
		} else {
			// LogHelper.LogException("Counldn't connect to server", exception);
			LogHelper.LogMessage("Add packet listener failed!");
			intent.putExtra("Reason", "Add packet listener failed!");
		}

		appContext.sendBroadcast(intent);
	}

	//通知注册成功
	private void notifyRegisterSucceed(String info) {
		Intent registerSucceed = new Intent(Intents.RegisterSucceed);
		registerSucceed.putExtra(Intents.RegisterReason, info);
		appContext.sendBroadcast(registerSucceed);
	}

	//通知注册失败
	private void notifyRegisterFailed(String info) {
		Intent registerFailed = new Intent(Intents.RegisterFailed);
		registerFailed.putExtra(Intents.RegisterReason, info);
		appContext.sendBroadcast(registerFailed);
	}

	//发送否？
	@Override
	public boolean IsSending() {
		return sending;
	}

	//连接否？
	@Override
	public boolean IsConnectted() throws RemoteException {
		// TODO Auto-generated method stub
		if (connection != null) {
			return connection.isConnected();
		}
		return false;
	}

	//授权否？
	@Override
	public boolean IsAuthenticated() throws RemoteException {
		// TODO Auto-generated method stub
		if (connection != null) {
			return connection.isAuthenticated();
		}
		return false;
	}

	//创建新用户
	@Override
	public boolean CreateNewUser(String userid, String password, String nickname)
			throws RemoteException {
		// TODO Auto-generated method stub
		return RegisterAccount(userid, password, nickname);
	}

	
	//连接Receiver 登陆或者注册
	private BroadcastReceiver connectReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				switch (nextCmd) {
				case Intents.KActionLogin:
					LoginServer();
					appContext.unregisterReceiver(connectReceiver);
					nextCmd = Intents.KActionNull;
					break;
				case Intents.KActionRegister:
					RegisterAccount(jidToLogin, pwdToLogin, nickNameToLogin);
					appContext.unregisterReceiver(connectReceiver);
					nextCmd = Intents.KActionNull;
					break;
				}

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	//注册Receiver,使能连接命令
	private void enableConnectionCmd() {
		// Monitor Connection failed
		IntentFilter connectionFilter = new IntentFilter();
		connectionFilter.addAction(Intents.ConnectSucceed);
		appContext.registerReceiver(connectReceiver, connectionFilter);
	}

	//获取用户Avater buffer
	public byte[] GetUserAvatar() {
		if (personalCard == null) {
			return null;
		}
		byte[] avatarbuf = personalCard.getAvatar();
		if (avatarbuf == null) {
			return null;
		}
		broadCastAvatarBuf(avatarbuf);
		return avatarbuf;
	}

	//Broadcast 某Avater buffer
	protected void broadCastAvatarBuf(byte[] buf) {
		Intent intent = new Intent(Intents.AvatarUpdated);
		intent.putExtra(LinkDefines.Data_AvatarBuf, buf);
		this.appContext.sendBroadcast(intent);
	}

	//注册账户
	private boolean RegisterAccount(String jid, String password, String nickname) {
		if (connection == null) {
			return false;
		}
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(connection.getServiceName());
		reg.setUsername(jid);// 注意这里createAccount注册时，参数是username，不是jid，是“@”前面的部分。
		reg.setPassword(password);
		// reg.setAttributes("first", "testfirstname");
		// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！

		PacketFilter filter = new AndFilter(new PacketIDFilter(
				reg.getPacketID()), new PacketTypeFilter(IQ.class));
		PacketCollector collector = connection.createPacketCollector(filter);
		connection.sendPacket(reg);

		IQ result = (IQ) collector.nextResult(SmackConfiguration
				.getPacketReplyTimeout());
		// Stop queuing results
		collector.cancel();// 停止请求results（是否成功的结果）
		if (result == null) {

		} else if (result.getType() == IQ.Type.ERROR) {
			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				Log.e("xmppMainRegiter", "IQ.Type.ERROR: "
						+ result.getError().toString());
				// Toast.makeText(xmppMain.this, “这个账号已经存在”,
				// Toast.LENGTH_SHORT).show();
				notifyRegisterFailed("该用户已经存在，请选择其他用户名！");
			} else {
				Log.e("xmppMainRegiter", "IQ.Type.ERROR: "
						+ result.getError().toString());
				// Toast.makeText(xmppMain.this, “注册失败”,
				// Toast.LENGTH_SHORT).show();
				notifyRegisterFailed("注册失败，请重试！");
			}
			return false;
		} else if (result.getType() == IQ.Type.RESULT) {
			// setToText(R.id.userid, registerUserName.getText().toString());
			// setToText(R.id.password, registerPassword.getText().toString());
			// Toast.makeText(xmppMain.this, “恭喜你注册成功”,
			// Toast.LENGTH_SHORT).show();
			notifyRegisterSucceed("注册成功！");
			/*
			 * try { personalCard.load(connection);
			 * personalCard.setNickName(nickNameToLogin);
			 * personalCard.save(connection); } catch (XMPPException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */
		}
		return true;
	}

	/*
	 * @Override public boolean UpdateAccountInfo(Map attributes) throws
	 * RemoteException { // TODO Auto-generated method stub VCard vCard = new
	 * VCard(); try { vCard.load(connection); } catch (XMPPException e1) { //
	 * TODO Auto-generated catch block e1.printStackTrace(); } String mail =
	 * vCard.getEmailHome(); vCard.setFirstName("kir");
	 * vCard.setLastName("max"); vCard.setEmailHome("foo@gmail.bar");
	 * //vCard.setJabberId("jabber@id.org");
	 * vCard.setJabberId(connection.getUser());
	 * vCard.setOrganization("Jetbrains, s.r.o"); vCard.setNickName("KIR");
	 * 
	 * vCard.setField("TITLE", "Mr"); vCard.setAddressFieldHome("STREET",
	 * "Some street"); vCard.setAddressFieldWork("CTRY", "US");
	 * vCard.setPhoneWork("FAX", "3443233");
	 * 
	 * try { vCard.save(connection); } catch (XMPPException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } return false; /*
	 * AccountManager am = connection.getAccountManager();
	 * 
	 * Object[] i=am.getAccountAttributes().toArray(); // create a hashmap for
	 * the attributes HashMap map=new HashMap(); // copy existing attributes
	 * into map //while(i.hasNext()) for(int nc=0;nc<i.length;nc++) { String
	 * name=(String)i[nc];//(String) i.next();
	 * map.put(name,am.getAccountAttribute(name)); } // alter an attribute in
	 * the map // you''d normally check it exists first
	 * map.put("email","mynewemaill@address.com"); // create a registration
	 * packet Registration reg=new Registration(); reg.setType(IQ.Type.SET); //
	 * we''re setting the attributes reg.setFrom(connection.getUser()); // set
	 * the from address to be from this user String user = connection.getUser();
	 * //reg.setUsername(StringUtils.parseBareAddress(connection.getUser()));
	 * reg.setUsername("test1"); reg.setTo(connection.getHost()); // set to
	 * address to the user''s server reg.setAttributes(map); // set the
	 * attributes connection.sendPacket(reg); // send the packet return true; //
	 * alter an attribute in the map // you''d normally check it exists first
	 * //map.put("email","mynewemaill@address.com"); // create a registration
	 * packet /* Registration reg=new Registration(); reg.setType(IQ.Type.SET);
	 * // we''re setting the attributes reg.setFrom(connection.getUser()); //
	 * set the from address to be from this user
	 * //reg.setUsername(StringUtils.parseBareAddress(connection.getUser()));
	 * reg.setUsername("test1"); reg.addAttribute("email", "gogog@gmail.com");
	 * reg.setTo(connection.getHost()); // set to address to the user''s server
	 * connection.sendPacket(reg); // send the packet
	 * 
	 * PacketFilter filter = new AndFilter(new
	 * PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));
	 * PacketCollector collector = connection.createPacketCollector(filter);
	 * connection.sendPacket(reg); IQ result = (IQ)
	 * collector.nextResult(SmackConfiguration .getPacketReplyTimeout()); //
	 * Stop queuing results collector.cancel();// 停止请求results（是否成功的结果） if
	 * (result == null) { return true; } else if (result.getType() ==
	 * IQ.Type.RESULT) { return true; } else { // if (result.getType() ==
	 * IQ.Type.ERROR) if
	 * (result.getError().toString().equalsIgnoreCase("conflict(409)")) { return
	 * false; } else { return false; } }
	 */
	// }

	//默认的用户类封装 peronalCard
	protected boolean CheckCardDefault() {
		boolean bneedset = false;
		String value = personalCard.getNickName();
		if (value == null) {
			bneedset = true;
			personalCard.setNickName("维纳斯");
		}

		value = personalCard.getOrganization();
		if (value == null) {
			bneedset = true;
			personalCard.setOrganization("连我，一起游戏哦～～");
		}

		value = personalCard.getField(ServicesDefines.KStrBirthDate);
		if (value == null) {
			bneedset = true;
			personalCard.setField(ServicesDefines.KStrBirthDate, "1990-01-01");
		}

		value = personalCard.getField(ServicesDefines.KStrAstrology);
		if (value == null) {
			bneedset = true;
			personalCard.setField(ServicesDefines.KStrAstrology, "魔羯座");
		}

		value = personalCard.getField(ServicesDefines.KStrGendre);
		if (value == null) {
			bneedset = true;
			personalCard.setField(ServicesDefines.KStrGendre, "女");
		}

		value = personalCard.getField(ServicesDefines.KStrConnectCode);
		if (value == null) {
			bneedset = true;
			personalCard.setField(ServicesDefines.KStrConnectCode, "123456");
		}

		if (bneedset) {
			try {
				personalCard.save(connection);
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	//更新设置（其实代码未完成更新，只是获取）
	boolean updateSettingString() {
		String jid = personalCard.getJabberId();
		// XMPPSettings.Domain = UserRoster.GetDomain(jid);
		return false;
	}

	//序列化账户信息
	@Override
	public boolean SerializeAccountInfo(boolean upload) throws RemoteException {
		// TODO Auto-generated method stub
		if (upload) {
			try {
				personalCard.save(connection);
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} else {
			try {
				personalCard.load(connection);
				updateSettingString();
				CheckCardDefault();
				GetUserAvatar();
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	//获得账户信息条目，具体信息的用户名、昵称、组织、生日、星座等一条
	@Override
	public String GetAccountInfoItem(int key) throws RemoteException {
		// TODO Auto-generated method stub
		switch (key) {
		case ServicesDefines.KEY_JabberID:
			return connection.getUser();
		case ServicesDefines.KEY_Nickname:
			return personalCard.getNickName();
			// return null;
		case ServicesDefines.KEY_PS:
			return personalCard.getOrganization();
		case ServicesDefines.KEY_BIRTHDATE:
			return personalCard.getField(ServicesDefines.KStrBirthDate);
		case ServicesDefines.KEY_ASTROLOGY:
			return personalCard.getField(ServicesDefines.KStrAstrology);
		case ServicesDefines.KEY_GENDRE:
			return personalCard.getField(ServicesDefines.KStrGendre);
		case ServicesDefines.KEY_CONNECTCODE:
			return personalCard.getField(ServicesDefines.KStrConnectCode);
			// return null;
		default:
			return null;
		}
	}

	
	//设定账户中某条信息
	@Override
	public boolean SetAccountInfoItem(int key, String value)
			throws RemoteException {
		// TODO Auto-generated method stub
		switch (key) {
		case ServicesDefines.KEY_JabberID:
			return false;
		case ServicesDefines.KEY_Nickname:
			personalCard.setNickName(value);
			return true;
		case ServicesDefines.KEY_PS:
			personalCard.setOrganization(value);
			return true;
		case ServicesDefines.KEY_BIRTHDATE:
			personalCard.setField(ServicesDefines.KStrBirthDate, value);
			return true;
		case ServicesDefines.KEY_ASTROLOGY:
			personalCard.setField(ServicesDefines.KStrAstrology, value);
			return true;
		case ServicesDefines.KEY_GENDRE:
			personalCard.setField(ServicesDefines.KStrGendre, value);
			return true;
		case ServicesDefines.KEY_CONNECTCODE:
			personalCard.setField(ServicesDefines.KStrConnectCode, value);
			return true;
		default:
			return true;
		}
	}

	//同步Roster信息
	@Override
	public boolean SyncRosterInfo(String jid) throws RemoteException {
		// TODO Auto-generated method stub
		if (!jid.contains("@")) {
			jid += XMPPSettings.HostName;
		}
		try {
			friendCard.load(connection, jid);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	//获取Roster信息条目
	@Override
	public String GetRosterInfoItem(int key) throws RemoteException {
		// TODO Auto-generated method stub
		switch (key) {
		case ServicesDefines.KEY_JabberID:
			return friendCard.getJabberId();
		case ServicesDefines.KEY_Nickname:
			return friendCard.getNickName();
		case ServicesDefines.KEY_PS:
			return friendCard.getOrganization();
		case ServicesDefines.KEY_BIRTHDATE:
			return friendCard.getField(ServicesDefines.KStrBirthDate);
		case ServicesDefines.KEY_ASTROLOGY:
			return friendCard.getField(ServicesDefines.KStrAstrology);
		case ServicesDefines.KEY_GENDRE:
			return friendCard.getField(ServicesDefines.KStrGendre);
		case ServicesDefines.KEY_CONNECTCODE:
			return friendCard.getField(ServicesDefines.KStrConnectCode);
		default:
			return null;
		}
	}

	//获取消息
	@Override
	public String GetMsg() throws RemoteException {
		// TODO Auto-generated method stub
		if (msgList.size() > 0) {
			String msg = msgList.get(0);
			msgList.remove(0);
			return msg;
		}
		return null;
	}

	//将消息直接发送给用户
	@Override
	public void SendMessageDirectToUser(String user, String message)
			throws RemoteException {
		// TODO Auto-generated method stub
		Message packet = new Message(user, Message.Type.chat);
		packet.setBody(message);
		Log.i("SyngMgr send message:", user + " " + message);
		synchronized (connection) {
			if (connection != null && packet != null) {
				try {
					connection.sendPacket(packet);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

	}

	//初始化连接
	public boolean InitConnection() throws RemoteException {
		if (connection == null) {
			configConnection();
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	//连接服务器
	@Override
	public void ConnectToServer(int nextcmd) throws RemoteException {
		// TODO Auto-generated method stub

		InitConnection();

		if (connectThread == null) {
			connectThread = new ConnectThread();
		}

		if (loginThread == null) {
			loginThread = new LoginThread();
		}

		if (messageThread == null) {
			messageThread = new MessageThread();
		}

		nextCmd = nextcmd;
		switch (nextCmd) {
		case Intents.KActionLogin:
		case Intents.KActionRegister:
			enableConnectionCmd();
			break;
		}
		connectThread.start();
	}

	//用户登陆服务器
	@Override
	public void LoginServer() throws RemoteException {
		// TODO Auto-generated method stub
		loginThread.start();
	}

	//设置用户信息
	@Override
	public void SetUserInfo(String jid, String pwd, String nickname)
			throws RemoteException {
		// TODO Auto-generated method stub
		jidToLogin = jid;
		pwdToLogin = pwd;
		nickNameToLogin = nickname;
	}

	//更新Avater，关于个人信息
	@Override
	public boolean UpdateAvatar(String imgpath) throws RemoteException {
		// TODO Auto-generated method stub
		File file = new File(imgpath);
		byte[] bytes = null;

		// bytes = getFileBytes(f);

		BufferedInputStream bis = null;
		try {
			try {
				bis = new BufferedInputStream(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int len = (int) file.length();
			bytes = new byte[len];
			int readBytes = 0;
			try {
				readBytes = bis.read(bytes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (readBytes != bytes.length) {
				return false;
			}
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// String encodedImage = StringUtils.encodeBase64(bytes);
		String encodedImage = android.util.Base64.encodeToString(bytes,
				Base64.DEFAULT);
		personalCard.setAvatar(bytes, encodedImage);
		personalCard.setEncodedImage(encodedImage);
		personalCard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"
				+ encodedImage + "</BINVAL>", true);

		try {
			personalCard.save(connection);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	//获得Roster信息
	@Override
	public String[] GetRosterInfo(String jid) throws RemoteException {
		// TODO Auto-generated method stub
		String[] ret = new String[] { "", "", "", "", "", "", "" };
		VCard queryCard = new VCard();
		try {
			if (jid != null) {
				queryCard.load(connection, jid);
			}
			ret[ServicesDefines.KEY_JabberID] = queryCard.getJabberId();
			ret[ServicesDefines.KEY_Nickname] = queryCard.getNickName();
			ret[ServicesDefines.KEY_PS] = queryCard.getOrganization();
			ret[ServicesDefines.KEY_BIRTHDATE] = queryCard
					.getField(ServicesDefines.KStrBirthDate);
			ret[ServicesDefines.KEY_ASTROLOGY] = queryCard
					.getField(ServicesDefines.KStrAstrology);
			ret[ServicesDefines.KEY_GENDRE] = queryCard
					.getField(ServicesDefines.KStrGendre);
			ret[ServicesDefines.KEY_CONNECTCODE] = queryCard
					.getField(ServicesDefines.KStrConnectCode);
			return ret;
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	//通过ID查找用户
	@Override
	public String SearchByID(String jid) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			String ids = "";
			UserSearchManager search = new UserSearchManager(connection);
			Form searchForm = search.getSearchForm(XMPPSettings.SearchAddr);
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", jid);
			ReportedData data = search.getSearchResults(answerForm,
					XMPPSettings.SearchAddr);

			Iterator<Row> it = data.getRows();
			int nc = 0;
			Row row = null;
			ids = jid;
			while (it.hasNext()) {
				row = it.next();
				// ansS+=row.getValues("Username").next().toString()+"\n";
				Log.i("Username", row.getValues("Username").next().toString());
				ids = ids + ";" + row.getValues("Username").next().toString();
				nc++;
			}
			return ids;
		} catch (Exception e) {
			Log.e("User Search:", e.getMessage());
		}

		return null;

	}

	//添加好友
	@Override
	public boolean AddFriend(String jid) throws RemoteException {
		// TODO Auto-generated method stub
		Roster roster = connection.getRoster();
		try {
			roster.createEntry(jid, jid, new String[] { "GroupAll" });
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
		return true;
	}

	//处理Presence,一次请求（个人理解 zzh）
	@Override
	public boolean ProcessSubscribe(int state, String from, String to)
			throws RemoteException {
		// TODO Auto-generated method stub
		Presence presence;
		if (state != 0)
			presence = new Presence(Presence.Type.subscribed);
		else
			presence = new Presence(Presence.Type.unsubscribe);
		// 同意是subscribed 拒绝是unsubscribe
		presence.setTo(to);// 接收方jid
		presence.setFrom(from);// 发送方jid
		connection.sendPacket(presence);// connection是你自己的XMPPConnection链接

		/*
		 * if(state!=0) presence = new Presence(Presence.Type.subscribe); else
		 * presence = new Presence(Presence.Type.unsubscribe); //同意是subscribed
		 * 拒绝是unsubscribe presence.setTo(from);//接收方jid
		 * presence.setFrom(to);//发送方jid
		 * connection.sendPacket(presence);//connection是你自己的XMPPConnection链接
		 * 
		 * if(state!=0) presence = new Presence(Presence.Type.available);
		 * //同意是subscribed 拒绝是unsubscribe presence.setTo(from);//接收方jid
		 * presence.setFrom(to);//发送方jid
		 * connection.sendPacket(presence);//connection是你自己的XMPPConnection链接
		 */
		return true;
	}

	//删除好友
	@Override
	public boolean RemoveFriend(String jid) throws RemoteException {
		// TODO Auto-generated method stub
		Roster roster = connection.getRoster();
		try {
			RosterEntry entry = roster.getEntry(jid);
			if (entry == null) {
				jid += XMPPSettings.HostName;
				entry = roster.getEntry(jid);
			}
			if (entry != null) {
				roster.removeEntry(entry);
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
