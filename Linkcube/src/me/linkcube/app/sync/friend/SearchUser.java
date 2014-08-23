package me.linkcube.app.sync.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackManager;

import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.os.Handler;
import android.os.Message;
/**
 * 查找用户类
 * @author Rodriguez-xin
 *
 */
public class SearchUser {

	private ASmackRequestCallBack searchUserCallBack;
	private String userName;
	public SearchUser(String username,ASmackRequestCallBack iCallBack){
		userName=username;
		searchUserCallBack=iCallBack;
		callSearchUsers();
	}
	
	private void callSearchUsers(){
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.obj==null){
					searchUserCallBack.responseFailure(-1);
				}
				else if (msg.obj!=null) {
					searchUserCallBack.responseSuccess(msg.obj);
				}
			}
		};
		Thread thread=new Thread(){
			@Override
			public void run() {
				List<Map<String, String>> userList = new ArrayList<Map<String,String>>();
				userList = searchUser(userName);
				Message msg=new Message();
				msg.obj=userList;
				handler.sendMessage(msg);
			}
		};
		thread.start();
	}
	/**
	 * 查询用户
	 * @param userName
	 * @return
	 */
	private List<Map<String, String>> searchUser(String userName){
		if(! ASmackManager.getInstance().getXMPPConnection().isConnected())
			return null;
		Map<String, String> user=null;
		List<Map<String, String>> results=new ArrayList<Map<String,String>>();
		try {
			new ServiceDiscoveryManager( ASmackManager.getInstance().getXMPPConnection());
			UserSearchManager usm=new UserSearchManager( ASmackManager.getInstance().getXMPPConnection());
			Form searchForm=usm.getSearchForm("search."+ ASmackManager.getInstance().getXMPPConnection().getServiceName());
			Form answerForm=searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", userName);
			ReportedData data=usm.getSearchResults(answerForm, "search."+ ASmackManager.getInstance().getXMPPConnection().getServiceName());
			Iterator<Row> iterator=data.getRows();
			Row row=null;
			while (iterator.hasNext()) {
				user=new HashMap<String, String>();
				row=iterator.next();
				user.put("Username",row.getValues("Username").next().toString());
				user.put("Email",row.getValues("Email").next().toString());
				results.add(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
}
