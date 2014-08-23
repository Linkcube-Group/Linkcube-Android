package me.linkcube.app.sync.user;

import me.linkcube.app.core.Timber;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackManager;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smackx.packet.VCard;

import android.app.DownloadManager.Request;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
/**
 * 用户注册类
 * @author Rodriguez-xin
 *
 */
public class UserRegister {

	private String TAG="UserRegister";
	private ASmackRequestCallBack userRegisterCallBack;
	private String username;
	private String passWord;
	public UserRegister(String jid,String pwd,ASmackRequestCallBack iCallBack){
		userRegisterCallBack=iCallBack;
		username=jid;
		passWord=pwd;
		calluserRegister();
	}
	/**
	 * 用户注册回调方法
	 * @param jidToLogin
	 * @param pswToLogin
	 */
	private void calluserRegister(){
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what==0){
					userRegisterCallBack.responseSuccess(null);
				}else{
					userRegisterCallBack.responseFailure(msg.what);
				}
			}
		};
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				int reflag=-1;
				try {
					reflag=userRegister(username,passWord,null);
				} catch (Exception e) {
					// TODO: handle exception
				}
				Message msg=new Message();
				msg.what=reflag;
				handler.sendMessage(msg);
			}
		});
		thread.start();
	}
	/**
	 * 用户注册
	 * @param username 用户名(不是jid，是"@"前面的部分)
	 * @param password 密码
	 * @return '-1' 网络错误 , '0' 注册成功, '1' 服务器无相应, '2' 账号已经存在,'3' 注册失败
	 */
	private int userRegister(String username,String password,String nickname){
		/*if(!isNetConnected()){
			Log.i(TAG, "register error,can't connected to server..");
			return -1;
		}*/
		if(! ASmackManager.getInstance().getXMPPConnection().isConnected())
			return -1;
		Registration reg=new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo( ASmackManager.getInstance().getXMPPConnection().getServiceName());
		// 注意这里参数是username，不是jid，是"@"前面的部分。
		reg.setUsername(username);
		reg.setPassword(password);
		
		reg.addAttribute("android", "linkcube_createUser_android");// addAttribute不能为空，否则会报错。
		PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()),
				new PacketTypeFilter(IQ.class));
		PacketCollector collector= ASmackManager.getInstance().getXMPPConnection().createPacketCollector(filter);
		ASmackManager.getInstance().getXMPPConnection().sendPacket(reg);
		IQ result=(IQ)collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		
		collector.cancel();
		if(result==null){
			Log.e(TAG, "no response from server.");
			return 1;
		}else if(result.getType()==IQ.Type.RESULT){
			Log.i(TAG, "regist success.");
			
			return 0;
		}else if(result.getType()==IQ.Type.ERROR){
			if(result.getError().toString().equalsIgnoreCase("conflict(409)")){
				Log.e(TAG, "this username has registed.");//"IQ.Type.ERROR"+result.getError().toString()
				return 2;
			}
		}else {  
            Log.e("regist", "IQ.Type.ERROR: " 
                    + result.getError().toString()); 
            return 3;  
        }
		return 3;
	}
}
