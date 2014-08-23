package me.linkcube.app.sync.core;

import me.linkcube.app.core.Timber;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import android.content.Context;

public class ReconnectionListener {

	private static ReconnectionListener reconnectionListener;
	private static boolean isreconnectionListener = false;
	private XMPPConnection connection;
	private ReconnectionCallBack reconnectionCallBack;
	private int reconnectCount=0;

	public static ReconnectionListener getInstance() {
		if (reconnectionListener == null) {
			reconnectionListener = new ReconnectionListener();
		}
		return reconnectionListener;
	}

	/**
	 * 断线重连
	 */
	public void onReconnectionListener() {
		if (isreconnectionListener == false) {
			connection=ASmackManager.getInstance().getXMPPConnection();
			connection.addConnectionListener(new ConnectionListener() {
				
				@Override
				public void reconnectionSuccessful() {
					// 当网络断线了，重新连接上服务器触发的事件
					Timber.e("来自连接监听,conn重连成功");
					reconnectionCallBack.reconnectionSuccessful();
				}

				@Override
				public void reconnectionFailed(Exception arg0) {
					// 重新连接失败
					Timber.e("来自连接监听,conn失败：" + arg0.getMessage());
					reconnectCount++;
					if(3==reconnectCount){
						reconnectionCallBack.reconnectionFailed();
						reconnectCount=0;
					}
					
				}

				@Override
				public void reconnectingIn(int arg0) {
					// 重新连接的动作正在进行的动作，里面的参数arg0是一个倒计时的数字，如果连接失败的次数增多，数字会越来越大，开始的时候是9
					Timber.e("来自连接监听,conn重连中..." + arg0);
					reconnectionCallBack.reconnectingIn();
				}

				@Override
				public void connectionClosedOnError(Exception arg0) {
					if (arg0.getMessage().contains("conflict")) { // 被挤掉线
						/*
						 * log.e("来自连接监听,conn非正常关闭");
						 * log.e("非正常关闭异常:"+arg0.getMessage());
						 * log.e(con.isConnected());
						 */
						// 关闭连接，由于是被人挤下线，可能是用户自己，所以关闭连接，让用户重新登录是一个比较好的选择
						connection = ASmackManager.getInstance()
								.getXMPPConnection();
						// 接下来你可以通过发送一个广播，提示用户被挤下线，重连很简单，就是重新登录
					} else if (arg0.getMessage().contains(
							"Connection timed out")) {// 连接超时不做任何操作，会实现自动重连
					}
				}

				@Override
				public void connectionClosed() {
					Timber.e("来自连接监听,conn正常关闭");
				}
			});
			isreconnectionListener = true;
			Timber.d("添加断线重连监听");
		}
	}
	
	public void setReconnectionCallBack(ReconnectionCallBack reconnectionCallBack) {
		this.reconnectionCallBack = reconnectionCallBack;
	}
	
	
}

