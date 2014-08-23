package me.linkcube.client;

import com.oplibs.services.XMPPUtils;
import com.oplibs.syncore.SyncMgr;
import com.oplibs.syncore.UserRoster;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * 朋友信息界面
 * 
 * 
 */

public class FriendInfoActivity extends Activity {
	
	private ImageView ibtnReturn;
	private TextView tvNickname;
	private TextView tvPS;
	private TextView tvJID;
	private TextView tvAge;
	private TextView tvAstrology;
	private Button btnConnect;
	
	String friendID="";
	String friendName = "";
	String friendPS = "";
	String friendAge = "";
	
	//Source for remote connection
	private GameBroadcastReceiver connectionBR = new GameBroadcastReceiver();
	
	
	public IntentFilter connectionFilter = new IntentFilter();
	private BroadcastReceiver connectionReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			GameMgr gamemgr=GameMgr.GetInstance(context);
			if(gamemgr.IsConnectedByID(friendID))
			{
				btnConnect.setText("断开游戏");
			}
			else
			{
				btnConnect.setText("连我，开始游戏");
			}
		}
	};
	
	UserRoster friendRoster = new UserRoster();
	
	private OnClickListener btnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			SyncMgr syncmgr=SyncMgr.GetInstance();
			if(v==ibtnReturn)
			{
				finish();
			}
			else if(v==btnConnect)
			{
				GameMgr gamemgr = GameMgr.GetInstance(FriendInfoActivity.this);
				//if(gamemgr.GetUserState()!=UserState.us_idle)
				if(gamemgr.IsConnectedByID(friendID))
				{
					//Toast.makeText(FriendInfoActivity.this, "正在连接中，请先断开！", Toast.LENGTH_SHORT).show();
					gamemgr.disConnection(friendID, syncmgr.signedUser.NickName);
					Log.i("Disconnect:"+friendID+":"+friendName, friendID);
					UpdateBtnTitle();
				}
				else
				{
					Intent intent = new Intent (FriendInfoActivity.this,ConnectCodeActivity.class);			
					startActivityForResult(intent, LinkDefines.REQ_Connectcode);	
				}
			}
				
		}
	};

	protected void BindUIHandle()
	{
		ibtnReturn = (ImageView)findViewById(R.id.ibtn_return);
		ibtnReturn.setOnClickListener(btnClickListener);
		
		tvNickname = (TextView)findViewById(R.id.tv_nickname);
		tvPS = (TextView)findViewById(R.id.tv_PS);
		tvJID = (TextView)findViewById(R.id.tv_jid);
		tvAge = (TextView)findViewById(R.id.tv_age);
		tvAstrology = (TextView)findViewById(R.id.tv_astrology);
		
		btnConnect = (Button)findViewById(R.id.btn_connectfriend);
		btnConnect.setOnClickListener(btnClickListener);
	}
	
	protected void GetInitData()
	{
		SyncMgr syncmgr = SyncMgr.GetInstance();
        Intent intent = getIntent();
		Bundle bundle=intent.getExtras();  //data为B中回传的Intent
		friendID = bundle.getString(LinkDefines.Data_JID);
		if(friendID.contains("@"))
		{
			int pos = friendID.indexOf("@");
			friendID=friendID.substring(0,pos);
		}
		
		UserRoster roster = syncmgr.GetRosterInfo(friendID);
		
		tvNickname.setText(roster.NickName);
		tvJID.setText(XMPPUtils.GetDisplayID(roster.JID));
		tvPS.setText(roster.PS);
		tvAge.setText(roster.UserAge()+"");
		tvAstrology.setText(roster.UserAstrology());
	}
	
	protected void UpdateUI()
	{
		
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.text);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendinfo);
        BindUIHandle();
        GetInitData();
        
        connectionFilter.addAction(LinkCubeIntents.LinkAccept);
    }
	
	protected void UpdateBtnTitle()
	{
        //SyncMgr syncmgr = SyncMgr.GetInstance();
        //if(syncmgr.IsRemotedByID(friendID))
		GameMgr gamemgr = GameMgr.GetInstance(this);
        if(gamemgr.IsConnectedByID(friendID))
        {
        	btnConnect.setText("断开连接");
        }
        else
        {
        	btnConnect.setText("连我，一起游戏");
        }
	}
    
	@Override
	public void onResume(){
        super.onResume();  
       UpdateBtnTitle();
        
      //Source for remote connection
       GameMgr gamemgr = GameMgr.GetInstance(FriendInfoActivity.this);
       gamemgr.StartUICMDListener();
       //Source for remote connection
       connectionBR.Init(FriendInfoActivity.this);
       connectionBR.EnableListener();
       
       registerReceiver(connectionReceiver, connectionFilter);
    }
	
	@Override
	public void onPause(){  
        super.onPause();
        GameMgr gamemgr = GameMgr.GetInstance(FriendInfoActivity.this);
        
        unregisterReceiver(connectionReceiver);
        //Source for remote connection
        gamemgr.StopUICMDListener();
        connectionBR.DisableListener();
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode)
		{
		case LinkDefines.REQ_Connectcode:
			{
				switch (resultCode) 
				{ //resultCode为回传的标记，我在B中回传的是RESULT_OK
				case LinkDefines.RES_Yes:
					{
						GameMgr gamemgr = GameMgr.GetInstance(FriendInfoActivity.this);
						SyncMgr syncmgr = SyncMgr.GetInstance();
						Bundle bundle=data.getExtras();  //data为B中回传的Intent
						String connectcode = bundle.getString(LinkDefines.Data_Connectcode);
						gamemgr.askConnection(friendID,syncmgr.signedUser.NickName,connectcode);	
						ProgressDialog.startProgressDialog(FriendInfoActivity.this,"等待对方确认！","断开");
						break;
					}
				case LinkDefines.RES_No:
					{
						break;
					}
				default:
					break;
				}
				break;
			}
		//Source for remote connection
		case LinkDefines.REQ_LocalConfirmation:
		{
			connectionBR.ProcessConfirmation(resultCode, data);
			break;
		}
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);  
	}
}
