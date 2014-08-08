package me.linkcube.client;

import java.util.ArrayList;

import com.oplibs.services.IOnChatServiceCall;
import com.oplibs.services.XMPPSettings;
import com.oplibs.services.XMPPUtils;
import com.oplibs.support.Intents;
import com.oplibs.support.WaveUtils;
import com.oplibs.syncore.SyncMgr;
import com.oplibs.syncore.UserRoster;

import me.linkcube.toy.IToyServiceCall;
import me.linkcube.toy.ShakeSensor;
import me.linkcube.toy.ShakeSensor.onShakeListener;

import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/*
 * 
 * 重要
 * 最重要的Activity,整个应用的核心枢纽，相当于应用的Main()
 * 功能：
 * 1，聊天服务：IOnChatServiceCall
 * 2，玩具服务：IToyServiceCall
 * 
 */

public class MainTabActivity extends Activity implements onShakeListener{
	
	
	private String TAG="MainTabActivity";
	private RadioButton btnMode1;
	private RadioButton btnMode2;
	private RadioButton btnMode3;
	private RadioButton btnMode4;
	private RadioButton btnMode5;
	private RadioButton btnMode6;
	private RadioButton btnMode7;
	private Button btnModeStop;
	
	private Button btnAdd;
	private Button btnMinus;
	private TextView tvSpd;
	private int curSpeed=1;
	
	private OnClickListener spdClick = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(v==btnAdd)
			{
				if(curSpeed<40)
				{
					curSpeed++;
				}
				tvSpd.setText(curSpeed+"");

			}
			else if(v==btnMinus)
			{
				if(curSpeed>1)
				{
					curSpeed--;
				}
				tvSpd.setText(curSpeed+"");
			}
			try {
				toyServiceCall.CacheToySpeed(curSpeed, false);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
		     
   /*
   * chatsevice
   * 
   */
	private IOnChatServiceCall chatServiceCall;
	
	private final int KCapSize=512;
	
	private ServiceConnection chatService = new ServiceConnection()
	{
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			chatServiceCall = null;			
			SyncMgr syncmgr = SyncMgr.GetInstance();
			syncmgr.SetIOnChatServiceCall(chatServiceCall);
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			chatServiceCall = (IOnChatServiceCall)service;
			LinkcubeApplication.getApp(MainTabActivity.this).chatServiceCall = (IOnChatServiceCall)service;
			SyncMgr syncmgr = SyncMgr.GetInstance();
			syncmgr.SetIOnChatServiceCall(chatServiceCall);
		}
	};

	//ToyService
	private IToyServiceCall toyServiceCall;
	private ServiceConnection toyService = new ServiceConnection()
	{
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			toyServiceCall = null;
			LinkcubeApplication.getApp(MainTabActivity.this).toyServiceCall = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			LinkcubeApplication.getApp(MainTabActivity.this).toyServiceCall = (IToyServiceCall)service;
			toyServiceCall = (IToyServiceCall)service;
			mShakeSensor.SetHandle(toyServiceCall);
		}
	};

	
	PowerManager pMgr=null;
	WakeLock mWakelock=null;
	
	private ViewPager mTabPager;	

	private ImageView mTabMain,mTabAddr,mTabFriends,mTabSetting;
	private ImageView ivDeviceStatus;
	
	//Handles for userinfo view;
	private Button btnConnect;
	private ImageView ivAvatar;
	private TextView tvNickname;
	private ImageView ivView1;
	private ImageView ivView2;
	private TextView tvAge;
	private TextView tvNickName;
	private TextView tvView4;
	private TextView tvJID;
	private TextView tvView5;
	private TextView tvPS;
	private TextView tvAstrology;
	//private UserRoster curRoster = null;
	
	//显示个人信息
	private void ShowPersonalInfo(boolean islogin, String remotejid)
	{
		SyncMgr syncmgr = SyncMgr.GetInstance();
		//curRoster = new UserRoster(syncmgr.signedUser);
		if(islogin)
		{
			btnConnect.setVisibility(View.VISIBLE);
			ivAvatar.setVisibility(View.VISIBLE);
			tvNickname.setVisibility(View.VISIBLE);
			ivView1.setVisibility(View.VISIBLE);
			ivView2.setVisibility(View.VISIBLE);
			tvAge.setVisibility(View.VISIBLE);
			tvNickName.setVisibility(View.VISIBLE);
			tvView4.setVisibility(View.VISIBLE);
			tvJID.setVisibility(View.VISIBLE);
			tvView5.setVisibility(View.VISIBLE);
			tvAstrology.setVisibility(View.VISIBLE);
			
			if(remotejid!=null)
			{
				UserRoster roster = syncmgr.GetRosterInfo(remotejid);
				roster.JID = remotejid;
				tvPS.setText(roster.PS);
				tvNickname.setText(roster.NickName);
				tvAge.setText(roster.UserAge()+"");
				tvJID.setText(XMPPUtils.GetDisplayID(roster.JID));
			}
			
		}
		else
		{
			btnConnect.setVisibility(View.INVISIBLE);
			ivAvatar.setVisibility(View.INVISIBLE);
			tvNickname.setVisibility(View.INVISIBLE);
			ivView1.setVisibility(View.INVISIBLE);
			ivView2.setVisibility(View.INVISIBLE);
			tvAge.setVisibility(View.INVISIBLE);
			tvNickName.setVisibility(View.INVISIBLE);
			tvView4.setVisibility(View.INVISIBLE);
			tvJID.setVisibility(View.INVISIBLE);
			tvView5.setVisibility(View.INVISIBLE);
			tvAstrology.setVisibility(View.INVISIBLE);
			tvPS.setText("请登录，开始惊喜吧！");
		}
	}
	
	//Function to process login.
	private IntentFilter connectionFailedFilter = new IntentFilter();
	private IntentFilter loggedInFilter = new IntentFilter();
	private IntentFilter loggedFailedInFilter = new IntentFilter();
	//private BroadcastReceiver loggedInReceiver = new ActivityCoordinator.LoggedInBroadcastReceiver();
	//private BroadcastReceiver loggedInFailedReceiver = new ActivityCoordinator.LoggedInFailedReceiver();
	//private BroadcastReceiver connectFailedReceiver = new ActivityCoordinator.ConnectFailedReceiver();
	
	//Source for remote connection
	private GameBroadcastReceiver connectionBR = new GameBroadcastReceiver();
	private ServerBroadcastReceiver serverBR = new ServerBroadcastReceiver();
	/*private BroadcastReceiver connectFailedReceiver =  new BroadcastReceiver() 
	{
    	@Override
    	public void onReceive(Context context, Intent intent) 
    	{
			//User user = User.GetUser();
			//user.SendMessageToUser("test2", "msg");
			Toast.makeText(MainTabActivity.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
			ActivityCoordinator.NextUI = -1;
			return;
    	}
    };
    */
    
    /*
    private BroadcastReceiver connectReceiver =  new BroadcastReceiver() 
	{
    	@Override
    	public void onReceive(Context context, Intent intent) 
    	{
			//User user = User.GetUser();
			//user.SendMessageToUser("test2", "msg");
			Toast.makeText(MainTabActivity.this, "登录中！", Toast.LENGTH_SHORT).show();
			try {
				chatServiceCall.LoginServer();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
    	}
    };
    */
	//Variables for downsampling the sound;
	
	//
	private Visualizer mVisualizer=null;
	private Visualizer.OnDataCaptureListener voiceCapture = new Visualizer.OnDataCaptureListener()
	{
		@Override
		public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate)
		{
			// TODO Auto-generated method stub
			long sound = WaveUtils.CalWaveLevel(waveform);
			try {
				toyServiceCall.SetWave(sound);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return;
			
		}

		@Override
		public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate)
		{
			long waveng = WaveUtils.CalFFTLevel(fft);
			
			Log.d(TAG,"WaveEng:"+waveng);
			try {
				toyServiceCall.SetWave(waveng);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return;
		}
	};

	//Variabls for main window
	//private ImageButton ibShaken,ibVoiced;
	private ImageButton ibShakenAdd,ibShakenMinus,ibVoicedAdd,ibVoicedMinus;
	private LinearLayout llShaken,llVoiced;
	private ToggleButton tbIsShaken;
	private ToggleButton tbIsVoiced;
	private ImageView[] shakenImages;
	private ImageView[] voicedImages;
	
	private ShakeSensor mShakeSensor;
	
	private EditText etSound;
	private EditText etSpeed;
	private Button btnSet;

	private OnClickListener connectClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v){
			GameMgr gamemgr = GameMgr.GetInstance(MainTabActivity.this);
			
			gamemgr.disConnection(GameMgr.connectedJID, "test1");
		}
	};
	
	//开始震动
	protected boolean startShaken()
	{
		mShakeSensor.chatServiceCall=chatServiceCall;
		if(mShakeSensor.registerListener())
		{
			tbIsShaken.setChecked(true);
			//ibShaken.setBackgroundResource(R.drawable.bg_shaken_clicked);
			llShaken.setBackgroundResource(R.drawable.bg_shaken_clicked);
			setShakenSensi(shakenSensi);
			return true;
		}
		return false;
	}
	//结束震动
	protected boolean endShaken()
	{
		if(toyServiceCall!=null)
		{
			/*try {
				toyServiceCall.CacheToySpeed(1);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		mShakeSensor.unRegisterListener();
		tbIsShaken.setChecked(false);
		llShaken.setBackgroundResource(R.drawable.bg_shaken_released);
		setShakenSensi(-1);
		return true;
	}
	//开始声音
	protected boolean startVoiced()
	{
		if(mVisualizer!=null)
        {
			tbIsVoiced.setChecked(true);
			//ibVoiced.setBackgroundResource(R.drawable.bg_voiced_clicked);
			llVoiced.setBackgroundResource(R.drawable.bg_voiced_clicked);
			mVisualizer.setEnabled(true);
        }
		setVoicedSensi(voicedSensi);
		return true;
	}
	//结束声音
	protected boolean endVoiced()
	{
		if(mVisualizer!=null)
		{
			mVisualizer.setEnabled(false);
		}
		tbIsVoiced.setChecked(false);
		//ibVoiced.setBackgroundResource(R.drawable.bg_voiced_released);
		setVoicedSensi(-1);
		llVoiced.setBackgroundResource(R.drawable.bg_voiced_released);
		return true;
	}
	//更新设备状态:主要指是否连接
	protected void UpdateDevStatus()
	{
		boolean isconnected=false;
		if(toyServiceCall!=null)
		{
			try {
				isconnected = toyServiceCall.IsToyConnected();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(isconnected)
		{
			ivDeviceStatus.setBackgroundResource(R.drawable.ic_connected);
		}
		else
		{
			ivDeviceStatus.setBackgroundResource(R.drawable.ic_disconnected);
		}
	}
	
    
	//重要！
	//七星图，根据选择具体的七种模式中的一种进行游戏
	private OnClickListener setModeClick = new OnClickListener()
	{
		@Override
		public void onClick(View v){
			SyncMgr syncmgr=SyncMgr.GetInstance();
			if(tcstate == ToyControlState.tcs_voiced)
			{
				endVoiced();
			}
			if(tcstate == ToyControlState.tcs_shaken)
			{
				endShaken();
			}

			if(v==btnMode1)
			{
				//btmgr.SetMode(1);
				try {
					tcstate = ToyControlState.tcs_mode;
					if(syncmgr.bIsRemoted)
					{
						
						//当为远程连接的时候;syncmgr.SetRemoteToyMode("m:"+1);
						//当为单机游戏的时候;toyServiceCall.CacheToyMode(1);
						syncmgr.SetRemoteToyMode("m:"+1);
						//chatServiceCall.SendMessageDirectToUser("test2@itrek-pc", "m:"+1);
					}
					else
					{
						toyServiceCall.CacheToyMode(1);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(v==btnMode2)
			{
				try {
					tcstate = ToyControlState.tcs_mode;
					if(syncmgr.bIsRemoted)
					{
						syncmgr.SetRemoteToyMode("m:"+2);
						//chatServiceCall.SendMessageDirectToUser("test2@itrek-c","m:"+2);
					}
					else
					{
						toyServiceCall.CacheToyMode(2);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(v==btnMode3)
			{
				try {
					tcstate = ToyControlState.tcs_mode;
					if(syncmgr.bIsRemoted)
					{
						syncmgr.SetRemoteToyMode("m:"+3);
						//chatServiceCall.SendMessageDirectToUser("test2@itrek-pc", "m:"+3);
					}
					else
					{
						toyServiceCall.CacheToyMode(3);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(v==btnMode4)
			{
				try {
					tcstate = ToyControlState.tcs_mode;
					if(syncmgr.bIsRemoted)
					{
						syncmgr.SetRemoteToyMode("m:"+4);
						//chatServiceCall.SendMessageDirectToUser("test2@itrek-pc", "m:"+4);
					}
					else
					{
						toyServiceCall.CacheToyMode(4);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(v==btnMode5)
			{
				try {
					tcstate = ToyControlState.tcs_mode;
					if(syncmgr.bIsRemoted)
					{
						syncmgr.SetRemoteToyMode("m:"+5);
						//chatServiceCall.SendMessageDirectToUser("test2@itrek-pc", "m:"+5);
					}
					else
					{
						toyServiceCall.CacheToyMode(5);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(v==btnMode6)
			{
				try {
					tcstate = ToyControlState.tcs_mode;
					if(syncmgr.bIsRemoted)
					{
						syncmgr.SetRemoteToyMode("m:"+6);
						//chatServiceCall.SendMessageDirectToUser("test2@itrek-pc", "m:"+6);
					}
					else
					{
						toyServiceCall.CacheToyMode(6);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(v==btnMode7)
			{
				try {
					tcstate = ToyControlState.tcs_mode;
					if(syncmgr.bIsRemoted)
					{
						syncmgr.SetRemoteToyMode("m:"+7);
						//chatServiceCall.SendMessageDirectToUser("test2@itrek-pc", "m:"+7);
					}
					else
					{
						toyServiceCall.CacheToyMode(7);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(v==btnModeStop)
			{
				tcstate = ToyControlState.tcs_idle;
				resetModeButton();
				try{
					if(syncmgr.bIsRemoted)
					{
						syncmgr.SetRemoteToyMode("m:"+8);
						//chatServiceCall.SendMessageDirectToUser("test2@itrek-pc", "m:"+7);
					}
					else
					{
						toyServiceCall.CacheToyMode(8);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	};
	
	
	enum ToyControlState{
		tcs_idle,
		tcs_voiced,
		tcs_shaken,
		tcs_mode
	};
	
	private void resetModeButton()
	{
		btnMode1.setChecked(false);
		btnMode2.setChecked(false);
		btnMode3.setChecked(false);
		btnMode4.setChecked(false);
		btnMode5.setChecked(false);
		btnMode6.setChecked(false);
		btnMode7.setChecked(false);
	}
	private ToyControlState tcstate;
	
	
	/*
	 * 重要
	 * 声音和摇动之间的切换，将七星图中模式全部关闭
	 */
	private OnClickListener checkShake = new OnClickListener()
	{
		@Override
		public void onClick(View v){
			boolean curshaken = tbIsShaken.isChecked();
			boolean curvoiced = tbIsVoiced.isChecked();

			UpdateDevStatus();

			//if(v==ibShaken)
			if(v==llShaken)
			{
				if(curshaken)
				{
					tcstate = ToyControlState.tcs_idle;
					resetModeButton();
					endShaken();
				}
				else
				{
					if(curvoiced)
					{
						endVoiced();
					}
					tcstate = ToyControlState.tcs_shaken;
					
					resetModeButton();
					startShaken();
				}
			}
			
			//else if(v==ibVoiced)
			else if(v==llVoiced)
			{
				if(curvoiced)
				{
					tcstate = ToyControlState.tcs_idle;
					resetModeButton();
					endVoiced();
				}
				else
				{
					if(curshaken)
					{
						endShaken();
					}
					tcstate = ToyControlState.tcs_voiced;
					resetModeButton();
					startVoiced();
				}
			}
		}
	 };
	 
	private int shakenSensi = 2;
	private int voicedSensi = 2;
	
	//改变传感器的灵敏度，震动传感器和声音传感器的灵敏度之间的调节，增加或者减少灵敏度，同时控制玩具游戏的灵敏度设置。
	private OnClickListener changeSensi = new OnClickListener()
	{
		@Override
		public void onClick(View v){
			if(tcstate==ToyControlState.tcs_shaken)
			{
				if(v==ibShakenAdd)
				{
					if(shakenSensi<4)
					{
						shakenSensi++;
					}
				}
				else if(v==ibShakenMinus)
				{
					if(shakenSensi>0)
					{
						shakenSensi--;
					}
				}
				setShakenSensi(shakenSensi);
				try {
					toyServiceCall.SetShakenSensi(shakenSensi);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(tcstate==ToyControlState.tcs_voiced)
			{
				if(v==ibVoicedAdd)
				{
					if(voicedSensi<4)
					{
						voicedSensi++;
					}
				}
				else if(v==ibVoicedMinus)
				{
					if(voicedSensi>0)
					{
						voicedSensi--;
					}
				}
				setVoicedSensi(voicedSensi);
				try {
					toyServiceCall.SetVoiceSensi(voicedSensi);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	private void setShakenSensi(int n)
	{
		if(n>-2&&n<5)
		{
			for(int nc=0;nc < 5;nc++)
			{
				if(nc<=n)
					shakenImages[nc].setVisibility(View.VISIBLE);
				else
					shakenImages[nc].setVisibility(View.GONE);
			}
		}
	}
	private void setVoicedSensi(int n)
	{
		if(n>-2&&n<5)
		{
			for(int nc=0;nc < 5;nc++)
			{
				if(nc<=n)
					voicedImages[nc].setVisibility(View.VISIBLE);
				else
					voicedImages[nc].setVisibility(View.GONE);
			}
		}
	} 	
	
	public class TabOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			SyncMgr syncmgr = SyncMgr.GetInstance();
			GameMgr gamemgr = GameMgr.GetInstance(MainTabActivity.this);
			switch (arg0) {
			case 0:
			{
				boolean bIsRemoted = syncmgr.IsAuthenticated();
				bIsRemoted = gamemgr.IsRemoted();
				ShowPersonalInfo(gamemgr.IsRemoted(),gamemgr.GetRemoteJID());
			}
				break;
			case 1:
				break;
			case 2:
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
	
	//绑定UI处理，将页面上全部的时间处理函数全部绑定到页面上。
	protected void BindUIHandle()
	{
		mTabPager = (ViewPager)findViewById(R.id.tabpager);
		mTabPager.setOnPageChangeListener(new TabOnPageChangeListener());
        
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
        
        //Set views of defferent pages
        LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.activity_userinfo, null);
        View view2 = mLi.inflate(R.layout.activity_toymain, null);
        View view3 = mLi.inflate(R.layout.activity_predefmode, null);
        
        //Adding subviews
        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);

        PagerAdapter mTabPagerAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(views.get(position));
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(views.get(position));
				return views.get(position);
			}
		};
		
		mTabPager.setAdapter(mTabPagerAdapter);

		mTabPager.setCurrentItem(1);

		ivAvatar=(ImageView)view1.findViewById(R.id.im_avatar);
		tvNickname=(TextView)view1.findViewById(R.id.tv_nickname);
		ivView1=(ImageView)view1.findViewById(R.id.imageView1);
		ivView2=(ImageView)view1.findViewById(R.id.iv_gendre);
		tvAge=(TextView)view1.findViewById(R.id.tv_age);
		tvNickName=(TextView)view1.findViewById(R.id.tv_nickname);
		tvView4=(TextView)view1.findViewById(R.id.textView4);
		tvJID=(TextView)view1.findViewById(R.id.tv_jid);
		tvView5=(TextView)view1.findViewById(R.id.textView5);
		tvPS=(TextView)view1.findViewById(R.id.tv_ps);
		tvAstrology = (TextView)view1.findViewById(R.id.tv_astrology);
		btnConnect = (Button)view1.findViewById(R.id.btn_connectfriend);
		btnConnect.setOnClickListener(connectClickListener);
		//ibShaken = (ImageButton)view2.findViewById(R.id.ib_shaken);
		//ibShaken.setOnClickListener(checkShake);
		//ibVoiced = (ImageButton)view2.findViewById(R.id.ib_voiced);
		//ibVoiced.setOnClickListener(checkShake);
		
		llShaken = (LinearLayout)view2.findViewById(R.id.ll_shaken);
		llShaken.setOnClickListener(checkShake);
		llVoiced = (LinearLayout)view2.findViewById(R.id.ll_voidced);
		llVoiced.setOnClickListener(checkShake);
		//BindMainView()
		ivDeviceStatus = (ImageView)view2.findViewById(R.id.iv_devicestatus);
		tbIsShaken = (ToggleButton)view2.findViewById(R.id.tb_shaken);
		tbIsShaken.setChecked(false);
		//ibShaken.setBackgroundResource(R.drawable.bg_shaken_released);
		llShaken.setBackgroundResource(R.drawable.bg_shaken_released);
		tbIsVoiced = (ToggleButton)view2.findViewById(R.id.tb_voiced);
		tbIsVoiced.setChecked(false);
		//ibVoiced.setBackgroundResource(R.drawable.bg_voiced_released);
		llVoiced.setBackgroundResource(R.drawable.bg_voiced_released);

		//ibShakenAdd,ibShakenMinus,ibVoicedAdd,ibVoicedMinus
		ibShakenAdd = (ImageButton)view2.findViewById(R.id.ib_shaken_add);
		ibShakenAdd.setOnClickListener(changeSensi);
		ibShakenMinus = (ImageButton)view2.findViewById(R.id.ib_shaken_minus);
		ibShakenMinus.setOnClickListener(changeSensi);
		ibVoicedAdd = (ImageButton)view2.findViewById(R.id.ib_voidced_add);
		ibVoicedAdd.setOnClickListener(changeSensi);
		ibVoicedMinus = (ImageButton)view2.findViewById(R.id.ib_voidced_minus);
		ibVoicedMinus.setOnClickListener(changeSensi);

		
		shakenImages = new ImageView[5];
		shakenImages[0] = (ImageView)view2.findViewById(R.id.iv_shaken_sen0);
		shakenImages[1] = (ImageView)view2.findViewById(R.id.iv_shaken_sen1);
		shakenImages[2] = (ImageView)view2.findViewById(R.id.iv_shaken_sen2);
		shakenImages[3] = (ImageView)view2.findViewById(R.id.iv_shaken_sen3);
		shakenImages[4] = (ImageView)view2.findViewById(R.id.iv_shaken_sen4);
		voicedImages = new ImageView[5];
		voicedImages[0] = (ImageView)view2.findViewById(R.id.iv_voiced_sen0);
		voicedImages[1] = (ImageView)view2.findViewById(R.id.iv_voiced_sen1);
		voicedImages[2] = (ImageView)view2.findViewById(R.id.iv_voiced_sen2);
		voicedImages[3] = (ImageView)view2.findViewById(R.id.iv_voiced_sen3);
		voicedImages[4] = (ImageView)view2.findViewById(R.id.iv_voiced_sen4);
		
		setShakenSensi(-1);
		setVoicedSensi(-1);
		
		btnMode1 = (RadioButton)view3.findViewById(R.id.rb_mode1);
        btnMode1.setOnClickListener(setModeClick);
        btnMode2 = (RadioButton)view3.findViewById(R.id.rb_mode2);
        btnMode2.setOnClickListener(setModeClick);
        btnMode3 = (RadioButton)view3.findViewById(R.id.rb_mode3);
        btnMode3.setOnClickListener(setModeClick);
        btnMode4 = (RadioButton)view3.findViewById(R.id.rb_mode4);
        btnMode4.setOnClickListener(setModeClick);
        btnMode5 = (RadioButton)view3.findViewById(R.id.rb_mode5);
        btnMode5.setOnClickListener(setModeClick);
        btnMode6 = (RadioButton)view3.findViewById(R.id.rb_mode6);
        btnMode6.setOnClickListener(setModeClick);
        btnMode7 = (RadioButton)view3.findViewById(R.id.rb_mode7);
        btnMode7.setOnClickListener(setModeClick);
        btnModeStop = (Button)view3.findViewById(R.id.btn_stop);
        btnModeStop.setOnClickListener(setModeClick);
        
        //	private int ShakeThreshHold[]={0, 500,800,1000,2000,4000,5000,8000};
    	//	private int KShakeToySpeed[]={2, 5, 10, 15, 20, 26, 30, 34};

    	etSound = (EditText)view2.findViewById(R.id.editTextSound);
    	etSound.setText("0,500,800,1000,2000,4000,5000,8000");
    	etSpeed = (EditText)view2.findViewById(R.id.editTextSpeed);
    	etSpeed.setText("2,5,10,15,20,26,30,34");
    	btnSet = (Button)view2.findViewById(R.id.btn_set);
    	btnSet.setOnClickListener(settingClick);

    	btnAdd= (Button)view2.findViewById(R.id.btn_add);
    	btnAdd.setOnClickListener(spdClick);
    	btnMinus = (Button)view2.findViewById(R.id.btn_minus);
    	btnMinus.setOnClickListener(spdClick);
    	tvSpd = (TextView)view2.findViewById(R.id.tv_speed);
    	
		UpdateDevStatus();
	}
	
	private OnClickListener settingClick = new OnClickListener()
	{
		@Override
		public void onClick(View v){
			String soundstr = etSound.getText().toString();      
			String[] soundArray = soundstr.split(",");

			for (int i = 0; i < soundArray.length; i++)
			{      
				 int sound = Integer.parseInt(soundArray[i]);
				 try {
					toyServiceCall.SetWaveMode(i, sound);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}  
			
			String speedstr = etSpeed.getText().toString();
			String[] speedArray = speedstr.split(",");
			for (int i = 0; i < speedArray.length; i++)
			{      
				 int speed = Integer.parseInt(speedArray[i]);
				 try {
					toyServiceCall.SetShakeMode(i, speed);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}  
		}

	};

	protected void bindCommunicationReciver()
	{
		connectionFailedFilter.addAction(Intents.ConnectFailed);
		loggedInFilter.addAction(Intents.LoggedIn);
		loggedFailedInFilter.addAction(Intents.LoggedInFailed);

	}
	protected void BindServiceListener()
	{
		mShakeSensor = new ShakeSensor(this.getApplicationContext());
		mShakeSensor.setOnShakeListener(this);
		
		if(mVisualizer==null)
    	{
			mVisualizer = new Visualizer(0); 
			mVisualizer.setCaptureSize(KCapSize);
			mVisualizer.setDataCaptureListener(voiceCapture, Visualizer.getMaxCaptureRate() / 16, false, true);
    	}
	
        XMPPSettings.CreateInstance(PreferenceManager.getDefaultSharedPreferences(this));
        
        //OnChat service
        Intent intent = new Intent(this, com.oplibs.services.OnChatService.class);
        this.startService(intent);    
		bindService(intent, chatService, Context.BIND_AUTO_CREATE);
        //Toy service
        Intent toyintent = new Intent(this, me.linkcube.toy.ToyService.class);
        this.startService(toyintent);    
		bindService(toyintent, toyService, Context.BIND_AUTO_CREATE);

		bindCommunicationReciver();
		
        GameMgr gamemgr = GameMgr.GetInstance(MainTabActivity.this);
        gamemgr.StartUICMDListener();
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_tab);
		
        LinkcubeApplication.getApp(MainTabActivity.this).gHeight=this.getWindowManager().getDefaultDisplay().getHeight();
        LinkcubeApplication.getApp(MainTabActivity.this).gWidth=this.getWindowManager().getDefaultDisplay().getWidth();
        
        BindServiceListener();
        BindUIHandle();
        

	}
	
	@Override
	public void onResume(){
        super.onResume();  
        mTabPager.setCurrentItem(1);
        GameMgr gamemgr = GameMgr.GetInstance(MainTabActivity.this);
        gamemgr.StartUICMDListener();
        
        pMgr = ((PowerManager) getSystemService(POWER_SERVICE));  
        mWakelock = pMgr.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "Linkcube");  
        mWakelock.acquire(); 
        
        tcstate = ToyControlState.tcs_idle;
        UpdateDevStatus();
        
		serverBR.Init(MainTabActivity.this);
		serverBR.EnableListener();
		
	      //Source for remote connection
        //Source for remote connection
        connectionBR.Init(MainTabActivity.this);
	       connectionBR.EnableListener();
	}
    
	
	@Override
	public void onPause(){  
        super.onPause();
        
        serverBR.DisableListener();
        connectionBR.DisableListener();
        
        GameMgr gamemgr = GameMgr.GetInstance(MainTabActivity.this);
        gamemgr.StopUICMDListener();
        
        if(null != mWakelock){  
            mWakelock.release();  
        }
    }

	@Override
	public void onStop(){  
        endVoiced();
        endShaken();
        super.onStop();
    }  
	@Override
    public void onDestroy(){  
        endVoiced();
        endShaken();
        super.onDestroy();  
    } 


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main_tab, menu);
		return true;
	}

	
	/*
	 * TabOnClickListener	 重要
	 * 
	 * 导航的作用：可以根据需要进行相应的切换
	 * 具体：
	 * 0：当前主页面
	 * 1：个人界面
	 * 2：朋友界面
	 * 3: 设置界面
	 */
	public class TabOnClickListener implements View.OnClickListener {
		private int index = 0;

		public TabOnClickListener(int i) {
			index = i;
		}
		@Override
		public void onClick(View v) {
			SyncMgr syncmgr = SyncMgr.GetInstance();
			
			//ActivityCoordinator.NextUI = index;
			ServerBroadcastReceiver.NextUI = index;
			if(tcstate==ToyControlState.tcs_voiced||tcstate==ToyControlState.tcs_shaken)
			{
				Toast.makeText(MainTabActivity.this, "请先关闭玩具控制！", Toast.LENGTH_SHORT).show();
				return;
			}
			if(index==0)
			{
				mTabPager.setCurrentItem(1);				
			}
			else if(index==1)
			{
				//try {
					if(!syncmgr.IsAuthenticated())
					{
						//ActivityCoordinator.askLoginOrCreate(MainTabActivity.this,LinkDefines.REQ_LoginOrCreate);
						ServerBroadcastReceiver.askLoginOrCreate(MainTabActivity.this, LinkDefines.REQ_LoginOrCreate);
					}
					else
					{
						Intent intent = new Intent (MainTabActivity.this,PersonalActivity.class);			
						startActivity(intent);					
					}
			}
			else if(index==2)
			{
				//try {
					if(!syncmgr.IsAuthenticated())
					{
						//ActivityCoordinator.askLoginOrCreate(MainTabActivity.this,LinkDefines.REQ_LoginOrCreate);
						ServerBroadcastReceiver.askLoginOrCreate(MainTabActivity.this,LinkDefines.REQ_LoginOrCreate);
					}
					else
					{
						Intent intent = new Intent (MainTabActivity.this,FriendsActivity.class);			
						startActivity(intent);			
					}
			}
			else
			{
				Intent intent = new Intent (MainTabActivity.this,SettingActivity.class);			
				startActivity(intent);	
			}
		}
	};
	
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
						/*
						Intent intent = new Intent (MainTabActivity.this,LoginActivity.class);			
						startActivityForResult(intent, LinkDefines.REQ_Login);
						*/
						ServerBroadcastReceiver.startLoginAccount(MainTabActivity.this, LinkDefines.REQ_Login);
						break;
					}
				case LinkDefines.RES_CreateAccount:
					{
						//ActivityCoordinator.startCreateAccount(MainTabActivity.this,LinkDefines.REQ_CreateAccount);
						//ActivityCoordinator.NextUI = -1;
						ServerBroadcastReceiver.startCreateAccount(MainTabActivity.this,LinkDefines.REQ_CreateAccount);
						ServerBroadcastReceiver.NextUI = -1;
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
					//ActivityCoordinator.startLoginAccount(MainTabActivity.this,LinkDefines.REQ_Login);
					ServerBroadcastReceiver.startLoginAccount(MainTabActivity.this, LinkDefines.REQ_Login);
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
					ServerBroadcastReceiver.Login2Server(MainTabActivity.this,data);
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
			GameMgr gamemgr=GameMgr.GetInstance(MainTabActivity.this);
			
			gamemgr.ProcessUserConfirmation(resultCode);
			/*
			switch(resultCode)
			{
			case LinkDefines.RES_No:
				//enableRemoted(true);
				break;
			case LinkDefines.RES_Yes:
				break;
			}
			*/
		}
			break;
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
	
	
	@Override
	public void onShake() {
		{
			/*
			try {
			chatServiceCall.SendMessageToUser("test2@itrek-pc", "haha!");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		}
		// TODO Auto-generated method stub
	}

}