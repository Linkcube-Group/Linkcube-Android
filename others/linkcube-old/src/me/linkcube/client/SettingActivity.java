package me.linkcube.client;

import com.oplibs.services.IOnChatServiceCall;
import com.oplibs.syncore.SyncMgr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;


/*
 * 
 * 更新、购买等
 * 
 */
public class SettingActivity extends Activity{
	
	private IOnChatServiceCall chatServiceCall;
	private Button loginButton;
	private Button btnGotoBuy;
	
	enum SettingActivityStatus
	{
		Custom_Normal,
		Custom_Refreshing
	};
	
	private ViewPager mTabPager;	
	private ImageView mTabMain,mTabAddr,mTabFriends,mTabSetting;

	//Function to process login.
	
	private ServerBroadcastReceiver serverBR = new ServerBroadcastReceiver();
	
	private Button.OnClickListener gotobuyListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			String url = "http://www.dreamore.com/projects/12606.html";
			Intent intent  = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
		}
	};
	private Button.OnClickListener loginoutCliskListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			SyncMgr syncmgr = SyncMgr.GetInstance();
			if(syncmgr.IsAuthenticated())
			{
				//Logout action
				syncmgr.LogoutServer();
				loginButton.setText("登录");
			}
			else
			{
				ServerBroadcastReceiver.askLoginOrCreate(SettingActivity.this, LinkDefines.REQ_LoginOrCreate);
				//syncmgr.reconnect();
				//Login action
			}
		}
	};
	
	public class TabOnClickListener implements View.OnClickListener {
		private int index = 0;

		public TabOnClickListener(int i) {
			index = i;
		}
		@Override
		public void onClick(View v) {
			SyncMgr syncmgr = SyncMgr.GetInstance();
			
			//ActivityCoordinator.NextUI  = index;
			ServerBroadcastReceiver.NextUI = index;
			if(index==0)
			{
				finish();		
			}
			else if(index==1)
			{
				//try {
					if(!syncmgr.IsAuthenticated())
					{
						//ActivityCoordinator.askLoginOrCreate(SettingActivity.this,LinkDefines.REQ_LoginOrCreate);
						ServerBroadcastReceiver.askLoginOrCreate(SettingActivity.this, LinkDefines.REQ_LoginOrCreate);
					}
					else
					{
						Intent intent = new Intent (SettingActivity.this,PersonalActivity.class);			
						startActivity(intent);					
					}
					/*
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			}
			else if(index==2)
			{
				//try {
					if(!syncmgr.IsAuthenticated())
					{
						//ActivityCoordinator.askLoginOrCreate(SettingActivity.this,LinkDefines.REQ_LoginOrCreate);
						ServerBroadcastReceiver.askLoginOrCreate(SettingActivity.this,LinkDefines.REQ_LoginOrCreate);
					}
					else
					{
						Intent intent = new Intent (SettingActivity.this,FriendsActivity.class);			
						startActivity(intent);			
					}
					/*
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
			}
		}
	};
	public class TabPageOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			}
			animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
			animation.setDuration(150);
			//mTabImg.startAnimation(animation);
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
	protected void BindUIHandle()
	{
		mTabPager = (ViewPager)findViewById(R.id.tabpager);
        mTabPager.setOnPageChangeListener(new TabPageOnPageChangeListener());
        
        //mTabPager.setOnTouchListener(touchListener);
        
        mTabMain = (ImageView) findViewById(R.id.img_weixin);
        mTabAddr = (ImageView) findViewById(R.id.img_address);
        mTabFriends = (ImageView) findViewById(R.id.img_friends);
        mTabSetting = (ImageView) findViewById(R.id.img_settings);
        //mTabImg = (ImageView) findViewById(R.id.img_tab_now);
        
        mTabMain.setOnClickListener(new TabOnClickListener(0));
        mTabAddr.setOnClickListener(new TabOnClickListener(1));
        mTabFriends.setOnClickListener(new TabOnClickListener(2));
        mTabSetting.setOnClickListener(new TabOnClickListener(3));
        
        mTabMain.setImageDrawable(getResources().getDrawable(R.drawable.tab_linkcube_released));
        mTabAddr.setImageDrawable(getResources().getDrawable(R.drawable.tab_personalinfo_released));
        mTabFriends.setImageDrawable(getResources().getDrawable(R.drawable.tab_friends_released));
        mTabSetting.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_clicked));
        
        loginButton = (Button)findViewById(R.id.btn_loginout);
        loginButton.setOnClickListener(loginoutCliskListener);
        SyncMgr syncmgr = SyncMgr.GetInstance();
        if(syncmgr.IsAuthenticated())
        {
        	loginButton.setText("退出当前帐号");
        }
        else
        {
        	loginButton.setText("登录");
        }
        
        btnGotoBuy = (Button)findViewById(R.id.btn_gobuy);
        btnGotoBuy.setOnClickListener(gotobuyListener);
	}
	
	protected void UpdateUI(SettingActivityStatus status)
	{
		switch(status)
		{
		case Custom_Normal:
			break;
		case Custom_Refreshing:
			break;
		}
	}

	protected void BindServiceListener()
	{
		
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        
        BindServiceListener();
        BindUIHandle();
        chatServiceCall=LinkcubeApplication.getApp(this).chatServiceCall;
    }
    
    @Override
	public void onResume(){
        super.onResume();  
        mTabPager.setCurrentItem(1);
        GameMgr gamemgr = GameMgr.GetInstance(SettingActivity.this);
        gamemgr.StartUICMDListener();
        
		serverBR.Init(SettingActivity.this);
		serverBR.EnableListener();
	}
    
	
	@Override
	public void onPause(){  
        super.onPause();
        
        serverBR.DisableListener();
        
        GameMgr gamemgr = GameMgr.GetInstance(SettingActivity.this);
        gamemgr.StopUICMDListener();
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode)
		{
		case LinkDefines.REQ_LoginOrCreate:
			{
				switch (resultCode) 
				{ //resultCode为回传的标记，我在B中回传的是RESULT_OK
				case LinkDefines.RES_Login:
					{
						ServerBroadcastReceiver.startLoginAccount(SettingActivity.this, LinkDefines.REQ_Login);
						break;
					}
				case LinkDefines.RES_CreateAccount:
					{
						//ActivityCoordinator.startCreateAccount(SettingActivity.this,LinkDefines.REQ_CreateAccount);
						ServerBroadcastReceiver.startCreateAccount(SettingActivity.this,LinkDefines.REQ_CreateAccount);
						break;
					}
				default:
					break;
				}
				break;
			}
		case LinkDefines.REQ_CreateAccount:
		{
			switch (resultCode)
			{ //resultCode为回传的标记，我在B中回传的是RESULT_OK
				case LinkDefines.RES_Yes:
					ServerBroadcastReceiver.startLoginAccount(SettingActivity.this,LinkDefines.REQ_Login);
					ServerBroadcastReceiver.NextUI  = -1;
					break;
				default:
					break;
			}
			break;
		}
		case LinkDefines.REQ_Login:
			{
				switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
				case LinkDefines.RES_Yes:
				{
					ServerBroadcastReceiver.Login2Server(SettingActivity.this,data);
				}
				break;
				case LinkDefines.RES_No:
				{
					//Toast.makeText(MainTabActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
					break;
				}
				default:
					break;
				}

				break;
			}
		case LinkDefines.REQ_ShowInfo:
		{
			GameMgr gamemgr=GameMgr.GetInstance(SettingActivity.this);
			
			gamemgr.ProcessUserConfirmation(resultCode);
		}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);  
	}
	public void GoToBTSetting(View v)
	{  
		Intent intent = new Intent (SettingActivity.this,BTSettingActivity.class);			
		startActivity(intent);	
	}
	
	public void GoToBuy(View v)
	{
		Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.dreamore.com/projects/12606.html"));
		it.setClassName("com.Android.browser", "com.android.browser.BrowserActivity");
		startActivity(it);
	}
	
	public void GoToPersonalInfo(View v)
	{
		Intent intent = new Intent (SettingActivity.this,PersonalInfoEditActivity.class);			
		startActivity(intent);	
	}
	
	public void GoToAboutUS(View v)
	{
		Intent intent = new Intent (SettingActivity.this,AboutUsActivity.class);			
		startActivity(intent);	
	}
	
	public void GoToUpgrade(View v)
	{
		Intent intent = new Intent(SettingActivity.this,InfoDlgActivity.class);
		String Info = "已经是最新版本！"; 
		intent.putExtra("Info", Info);
		startActivityForResult(intent,0);
	}
	
	public void GoToTutorial(View v)
	{
		Intent intent = new Intent(SettingActivity.this,TutorialActivity.class);
		startActivity(intent);
	}
}
