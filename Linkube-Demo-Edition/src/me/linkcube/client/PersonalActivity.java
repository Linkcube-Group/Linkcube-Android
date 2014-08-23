package me.linkcube.client;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import com.oplibs.services.XMPPUtils;
import com.oplibs.support.Intents;
import com.oplibs.syncore.SyncMgr;
import me.linkcube.client.SettingActivity.SettingActivityStatus;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;


/*
 * 
 * 个人Activity
 * 
 */

public class PersonalActivity extends Activity{
	enum PersonalActivityStatus
	{
		Custom_Normal,
		Custom_Refreshing
	};
	
	private ViewPager mTabPager;	
	private ImageView mTabMain,mTabAddr,mTabFriends,mTabSetting;
	
	private TextView tvAge,tvAstrology,tvNick,tvJID,tvStatement,tvConnectCode;
	private ImageView ivAvatar;
	private ImageView ivGendre;
	private HashMap<String,String> accountInfo = new HashMap<String,String>();
	private byte[] avatarBuf=null;
	
	//Source for remote connection
	private GameBroadcastReceiver connectionBR = new GameBroadcastReceiver();

	private BroadcastReceiver avatarUpdatedReceiver =  new BroadcastReceiver() 
	{
    	@Override
    	public void onReceive(Context context, Intent intent) 
    	{
    		unregisterReceiver(avatarUpdatedReceiver);
            avatarBuf=intent.getByteArrayExtra(LinkDefines.Data_AvatarBuf);  
            ByteArrayInputStream bais = new ByteArrayInputStream(avatarBuf);  
            Bitmap bitmap = BitmapFactory.decodeStream(bais);
            ivAvatar.setImageBitmap(bitmap);
			return;
    	}
    };
    
	public class TabOnClickListener implements View.OnClickListener {
		private int index = 0;

		public TabOnClickListener(int i) {
			index = i;
		}
		@Override
		public void onClick(View v) {
			if(index==0)
			{
				finish();		
			}
			else if(index==1)
			{
			}
			else if(index==2)
			{
		    	Intent intent = new Intent(PersonalActivity.this, FriendsActivity.class);
		    	startActivity(intent);
		    	finish();
			}
			else if(index==3)
			{
		    	Intent intent = new Intent(PersonalActivity.this, SettingActivity.class);
		    	startActivity(intent);
		    	finish();
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
        mTabAddr.setImageDrawable(getResources().getDrawable(R.drawable.tab_personalinfo_clicked));
        mTabFriends.setImageDrawable(getResources().getDrawable(R.drawable.tab_friends_released));
        mTabSetting.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_released));
        
        tvAge = (TextView)findViewById(R.id.tv_age);
        tvAge.setText("");
        tvAstrology = (TextView)findViewById(R.id.tv_astrology);
        tvAstrology.setText("");
        tvNick = (TextView)findViewById(R.id.tv_nickname);
        //tvNick.setText(syncmgr.GetAccountInfo("name"));
        tvJID = (TextView)findViewById(R.id.tv_jid);
        tvJID.setText("");
        tvStatement = (TextView)findViewById(R.id.tv_statement);
        tvStatement.setText("");
        //tvMail.setText(syncmgr.GetAccountInfo("email"));
        //tvMail.setText(syncmgr.GetAccountInfo("email"));
        tvConnectCode = (TextView)findViewById(R.id.tv_connectcode);
        tvConnectCode.setText("");
        
        ivGendre = (ImageView)findViewById(R.id.iv_gendre);
        //syncmgr.SetAccountInfo(null);
        
        ivAvatar = (ImageView)findViewById(R.id.iv_avatar);
	}
	
	private void startUpdateAvatar()
	{
		IntentFilter avatarUpdatedFilter = new IntentFilter();
		avatarUpdatedFilter.addAction(Intents.AvatarUpdated);
		registerReceiver(avatarUpdatedReceiver, avatarUpdatedFilter);
	}

	protected void UpdatePersonalInfoUI()
	{
		SyncMgr syncmgr = SyncMgr.GetInstance();
		
		syncmgr.UpdateAccount();
		
		tvAge.setText(syncmgr.signedUser.UserAge()+"");
		tvNick.setText(syncmgr.signedUser.NickName);
		tvJID.setText(XMPPUtils.GetDisplayID(syncmgr.signedUser.JID));
		tvAstrology.setText(syncmgr.signedUser.UserAstrology());
		if(syncmgr.signedUser.userGendre.equals("女"))
		{
			ivGendre.setBackgroundResource(R.drawable.bg_female);
		}
		else
		{
			ivGendre.setBackgroundResource(R.drawable.bg_male);
		}
		
		if(syncmgr.signedUser.PS==null||syncmgr.signedUser.PS.length()<1)
		{
			tvStatement.setText("这个人很懒，什么都没有留下！");
		}
		else
		{
			tvStatement.setText(syncmgr.signedUser.PS);
		}
		
		tvConnectCode.setText(syncmgr.signedUser.connectCode);
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
	public void onResume(){
        super.onResume();  
        
      //Source for remote connection
       GameMgr gamemgr = GameMgr.GetInstance(PersonalActivity.this);
       gamemgr.StartUICMDListener();
       //Source for remote connection
       connectionBR.Init(PersonalActivity.this);
       connectionBR.EnableListener();

    }
	
	@Override
	public void onPause(){  
        super.onPause();
        GameMgr gamemgr = GameMgr.GetInstance(PersonalActivity.this);
        
        //Source for remote connection
        gamemgr.StopUICMDListener();
        connectionBR.DisableListener();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_tab_personalinfo);
        
        BindServiceListener();
        BindUIHandle();
        
        UpdatePersonalInfoUI();
        

    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{ //resultCode为回传的标记，我在B中回传的是RESULT_OK
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
	
	public void GoToPersonalInfo(View v)
	{
		Intent intent = new Intent (PersonalActivity.this,PersonalInfoEditActivity.class);			
		intent.putExtra(LinkDefines.Data_AvatarBuf, avatarBuf);
		startActivity(intent);	
	}

}
