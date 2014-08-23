package com.oplibs.syncore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import android.os.RemoteException;
import com.oplibs.services.IOnChatServiceCall;
import com.oplibs.services.ServicesDefines;
import com.oplibs.services.XMPPSettings;
import com.oplibs.support.*;

/*
 * 
 * 同步消息
 * 
 * 用户Roster 和相应service
 * 
 */
public class SyncMgr
{
	private static SyncMgr syncmgr;
	private IOnChatServiceCall serviceCall;

	//private static String UserName = "UserName";
	private static String UserID = "UserName";
	private static String Password = "Password";
	private static String Resource = "Resource";
	
	public boolean bIsRemoted=false; 
	ArrayList<String> targetIDs=new ArrayList<String>();

	private Roster curRoster;
	public UserRoster signedUser = new UserRoster();

	private Object[] friendsRosterArray=null;
	public List<UserRoster> friendsArray=new ArrayList<UserRoster>();
	
	public static String GroupAll = "GroupAll";
	
	public static SyncMgr GetInstance()
	{
		if (syncmgr == null)
		{
			syncmgr = new SyncMgr();
		}
		
		return syncmgr;
	}
	
	public void SetIOnChatServiceCall(IOnChatServiceCall call)
	{
		serviceCall = call;
	}
	
	public boolean UpdateAccount()
	{
		try {
			serviceCall.SerializeAccountInfo(false);
			signedUser.JID=serviceCall.GetAccountInfoItem(ServicesDefines.KEY_JabberID);
			signedUser.NickName=serviceCall.GetAccountInfoItem(ServicesDefines.KEY_Nickname);
			signedUser.PS=serviceCall.GetAccountInfoItem(ServicesDefines.KEY_PS);
			signedUser.birthDate = serviceCall.GetAccountInfoItem(ServicesDefines.KEY_BIRTHDATE);
			signedUser.connectCode = serviceCall.GetAccountInfoItem(ServicesDefines.KEY_CONNECTCODE);
			signedUser.userGendre = serviceCall.GetAccountInfoItem(ServicesDefines.KEY_GENDRE);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public int FindEntry(String jid)
	{
		for(int nc=0;nc<friendsArray.size();nc++)
		{
			if(jid.contains("@"))
			{
				jid = jid.split("@")[0];
			}
			String strToCheck = friendsArray.get(nc).JID;
			if(strToCheck.contains("@"))
			{
				strToCheck = jid.split("@")[0];
			}
			if(jid.equals(strToCheck))
			{
				return nc;
			}
		}
		return -1;
	}
	
	public String GetUserName()
	{
		//return XMPPSettings.GetInstance().GetStringValue(UserName);
		return UserID;
	}

	public String GetPassWord()
	{
		//return XMPPSettings.GetInstance().GetStringValue(Password);
		return Password;
	}
	
	public String GetResource()
	{
		return XMPPSettings.GetInstance().GetStringValue(Resource);		
	}
	
	public void Login(String jid,String pwd)
	{
		if (serviceCall == null)
		{
			return;
		}
		
		try
		{
			serviceCall.SetUserInfo(jid, pwd, null);
			if(!serviceCall.IsConnectted())
			{
				serviceCall.ConnectToServer(Intents.KActionLogin);
			}
			else
			{
				serviceCall.LoginServer();
			}
		} 
		catch (RemoteException exception)
		{
			LogHelper.LogException(exception);
		}			
	}
	
	public UserRoster GetRoster(String jid)
	{
		UserRoster roster = new UserRoster();
		roster.JID = jid;
		try {
			serviceCall.SyncRosterInfo(jid);
			//info.UserID=serviceCall.GetRosterInfoItem(ServicesDefines.KEY_JabberID);
			roster.NickName=serviceCall.GetRosterInfoItem(ServicesDefines.KEY_Nickname);
			roster.PS=serviceCall.GetRosterInfoItem(ServicesDefines.KEY_PS);
			//roster.PS=serviceCall.GetRosterInfoItem(ServicesDefines.KEY_PS);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return roster;
	}
	public Boolean SyncFriendsList()
	{
        Collection<RosterEntry> entries = this.GetGroupEntries("GroupAll");
        
        if (entries == null)
        {
        	HashSet<RosterEntry> allEntries = new HashSet<RosterEntry>();
        	entries = Collections.unmodifiableCollection(allEntries);
        }
        
        friendsRosterArray = entries.toArray();
        friendsArray.clear();
        for(int nc=0;nc<friendsRosterArray.length;nc++)
        {
        	UserRoster info=new UserRoster();
			RosterEntry ent = (RosterEntry)friendsRosterArray[nc];
			info.JID = ent.getUser();
			
			try {
				serviceCall.SyncRosterInfo(info.JID);
				//info.UserID=serviceCall.GetRosterInfoItem(ServicesDefines.KEY_JabberID);
				info.NickName=serviceCall.GetRosterInfoItem(ServicesDefines.KEY_Nickname);
				info.PS=serviceCall.GetRosterInfoItem(ServicesDefines.KEY_PS);
				info.birthDate = serviceCall.GetRosterInfoItem(ServicesDefines.KEY_BIRTHDATE);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//info.NickName = ent.getName();
			
			friendsArray.add(info);
        }
        return friendsArray.size()>0;
	}
	
	public Object[] GetFriendsArray()
	{
		return friendsRosterArray;
	}
	
	public void SetGroupToEntries(HashMap<RosterGroup, Collection<RosterEntry>> g2e)
	{
	}
	
	public void SetGroups(Collection<RosterGroup> gs)
	{
	}
	
	public void SetRoster(Roster r)
	{
		curRoster = r;
		//jabberID = curRoster
	}
	
	public Collection<RosterGroup> GetGroups()
	{
		if (curRoster == null)
		{
			return null;
		}
		
		return curRoster.getGroups();
	}

	public RosterGroup GetGroup(String name)
	{
		if (curRoster == null)
		{
			return null;
		}
		
		if (StringUtils.IsStringNullOrEmpty(name))
		{
			return null;
		}
		
		return curRoster.getGroup(name);		
	}

	public Collection<RosterEntry> GetAllEntries()
	{
		if (curRoster == null)
		{
			return null;
		}
		
		return curRoster.getEntries();
	}

	public Collection<RosterEntry> GetGroupEntries(String groupName)
	{
		if (curRoster == null)
		{
			return null;
		}
		
		if (StringUtils.IsStringNullOrEmpty(groupName))
		{
			return null;
		}
		
		if (groupName.compareTo(GroupAll) == 0)
		{
			return curRoster.getEntries();
		}

		RosterGroup group = curRoster.getGroup(groupName);
		
		if (group == null)
		{
			return null;
		}
		
		return group.getEntries();
	}
	
	public RosterEntry GetEntry(String name)
	{
		if (curRoster == null)
		{
			return null;
		}
		
		if (StringUtils.IsStringNullOrEmpty(name))
		{
			return null;
		}
		
		return curRoster.getEntry(name);
	}
	
	public Boolean BroadCastCmd(String message)
	{
		return true;
	}
	
	public boolean EnableRemoted(boolean enable, String jid) {
		// TODO Auto-generated method stub
		if(enable)
		{
			targetIDs.clear();
			targetIDs.add(jid);
		}
		else
		{
			targetIDs.clear();
		}
		bIsRemoted = enable;
		return true;
	}
	
	public Boolean SetRemoteToyMode(String message)
	{
		try
		{
			for(int nc=0;nc<targetIDs.size();nc++)
			{
				String name = targetIDs.get(nc);
				this.serviceCall.SendMessageToUser(name, message);
				//this.serviceCall.SendMessageDirectToUser(targetIDs.get(nc), message);
			}
		}
		catch (RemoteException e)
		{
			LogHelper.LogException(e);
		}
		return true;
	}
	public Boolean SendMessageToUser(String jid, String message)
	{
		try
		{
			this.serviceCall.SendMessageToUser(jid, message);
		}
		catch (RemoteException e)
		{
			LogHelper.LogException(e);
		}
		return true;
	}

	public void BroadcaseIntent(String action)
	{
		try
		{
			this.serviceCall.BroadcaseIntent(action);
		}
		catch (RemoteException e)
		{
			LogHelper.LogException(e);
		}
	}
	
	public void ProcessSubscribe(int state, String from, String to)
	{
		try
		{
			this.serviceCall.ProcessSubscribe(state, from, to);
		}
		catch (RemoteException e)
		{
			LogHelper.LogException(e);
		}
	}
	
	public void LogoutServer()
	{
		try
		{
			this.serviceCall.DisconnectToServer();
		}
		catch (RemoteException e)
		{
			LogHelper.LogException(e);
		}
	}
	
	public Boolean IsSending()
	{
		try
		{
			this.serviceCall.IsSending();
		}
		catch (RemoteException e)
		{
			LogHelper.LogException(e);
		}
		
		return false;
	}
	
	public Boolean CreateNewAccount(String jid, String password, String nickname)
	{
		if (serviceCall == null)
		{
			return false;
		}
		
		try
		{
			serviceCall.SetUserInfo(jid, password, nickname);
			if(!serviceCall.IsConnectted())
			{
				serviceCall.ConnectToServer(Intents.KActionRegister);
			}
			else
			{
				serviceCall.CreateNewUser(jid, password, nickname);
			}
		} 
		catch (RemoteException exception)
		{
			LogHelper.LogException(exception);
		}			
		
		return false;
	}
	
	
	public boolean ChangePersonalCard(UserRoster roster)
	{
		//chatServiceCall.
		try {
			serviceCall.SetAccountInfoItem(ServicesDefines.KEY_Nickname,roster.NickName);
			serviceCall.SetAccountInfoItem(ServicesDefines.KEY_PS,roster.PS);
			serviceCall.SetAccountInfoItem(ServicesDefines.KEY_BIRTHDATE, roster.birthDate);
			serviceCall.SetAccountInfoItem(ServicesDefines.KEY_ASTROLOGY, roster.astrology);
			serviceCall.SetAccountInfoItem(ServicesDefines.KEY_GENDRE, roster.userGendre);
			serviceCall.SetAccountInfoItem(ServicesDefines.KEY_CONNECTCODE, roster.connectCode);
			serviceCall.SerializeAccountInfo(true);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean UploadAvatar(String path)
	{
		//chatServiceCall.
		try {
			serviceCall.UpdateAvatar(path);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	public boolean IsAuthenticated()
	{
		boolean bret=false;
		try {
			bret = serviceCall.IsAuthenticated();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bret = false;
		}
		return bret;
	}
	
	public boolean IsRemotedByID(String jid)
	{
		if(bIsRemoted)
		{
			if((targetIDs.size()>0)&&(jid.equals(targetIDs.get(0))))
			{
				return true;
			}
		}
		return false;
	}
	
	public UserRoster GetRosterInfo(String jid)
	{
		UserRoster roster = new UserRoster();
		try {
			String[] values=null;
			if(jid.contains("@"))
			{
				values = serviceCall.GetRosterInfo(jid);
			}
			else
			{
				values = serviceCall.GetRosterInfo(jid+XMPPSettings.HostName);
			}
			if(values!=null&&values.length>ServicesDefines.KEY_CONNECTCODE)
			{
				//roster.JID = values[ServicesDefines.KEY_JabberID];
				roster.JID = jid;
				roster.NickName = values[ServicesDefines.KEY_Nickname];
				roster.PS = values[ServicesDefines.KEY_PS];
				roster.birthDate = values[ServicesDefines.KEY_BIRTHDATE];
				roster.astrology = values[ServicesDefines.KEY_ASTROLOGY];
				roster.userGendre = values[ServicesDefines.KEY_GENDRE];
				roster.connectCode = values[ServicesDefines.KEY_CONNECTCODE];
				return roster;
			}
			return null;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String SearchById(String jid)
	{
		try {
			return serviceCall.SearchByID(jid);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean AddFriend(String jid)
	{
		int nc = FindEntry(jid);
		if(nc>=0)
		{
			return false;
		}
		try {
			return serviceCall.AddFriend(jid);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean RemoveUser(String jid)  
    {  

        try {
			return serviceCall.RemoveFriend(jid);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}          
    }  
}
