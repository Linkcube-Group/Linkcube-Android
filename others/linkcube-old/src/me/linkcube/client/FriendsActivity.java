package me.linkcube.client;

import java.util.ArrayList;
import java.util.List;

import com.oplibs.services.XMPPUtils;
import com.oplibs.syncore.SyncMgr;
import com.oplibs.syncore.UserRoster;

import me.linkcube.client.FriendsActivity.FriendsListAdapter.FriendListCellViewHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

/*
 * 
 * 朋友们列表界面
 * 
 * 
 */

public class FriendsActivity extends Activity{
	enum SettingActivityStatus
	{
		Custom_Normal,
		Custom_Refreshing
	};
	
	private ViewPager mTabPager;	
	private ImageView mTabMain,mTabAddr,mTabFriends,mTabSetting;
	
	private GameBroadcastReceiver connectionBR = new GameBroadcastReceiver();
	
	private Button btnSearch;
	private EditText edSearchID;
	
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
				Intent intent = new Intent(FriendsActivity.this,PersonalActivity.class);
		    	startActivity(intent);
		    	finish();
			}
			else if(index==2)
			{
			}
			else if(index==3)
			{
				Intent intent = new Intent(FriendsActivity.this,SettingActivity.class);
		    	startActivity(intent);
		    	finish();
			}
		}
	};
	public class TabPageOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
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
	
	private ListView lvFriendsList;
	protected FriendsListAdapter friendslistAdapter = null;
	
	private ListView lvSearchList;
	protected FriendsListAdapter searchListAdapter = null;

	private Button.OnClickListener searchClickListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String idtosearch = edSearchID.getText().toString();
			if(idtosearch!=null&&idtosearch.length()>0)
			{
				SyncMgr syncmgr = SyncMgr.GetInstance();
				String ids = syncmgr.SearchById(idtosearch);
				if(ids!=null)
				{
					Log.e("Find ", ids);
				}
				else
				{
					Log.e("Find nothing", "");
				}
				if(ids!=null)
				{
					String[] findIds = ids.split(";");
					if(findIds.length<=1)
					{
						Toast.makeText(FriendsActivity.this, "没有搜索到 用户！", Toast.LENGTH_SHORT).show();
						return;
					}
					for(int nc=1;nc<findIds.length;nc++)
					{
						UserRoster roster = syncmgr.GetRosterInfo(findIds[nc]);
						searchListAdapter.AddUser(roster);
					}
					searchListAdapter.notifyDataSetChanged();
				}
				else
				{
					Toast.makeText(FriendsActivity.this, "没有搜索到 用户！", Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				Toast.makeText(FriendsActivity.this, "请输入要所搜的id！", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	class ActionItem
	{
		public int curIndex;
		public String curJid;
		
		public ActionItem(int index, String jid)
		{
			curIndex = index;
			curJid = jid;
		}
	};
	private OnItemLongClickListener longClickListener = new OnItemLongClickListener()
	{
		@Override
		public boolean onItemLongClick(AdapterView parent, View view, int position, long id)
		{ 
			//ListView lv = (ListView)parent;
			//FriendsListAdapter adapter = (FriendsListAdapter)lv.getAdapter();
			FriendListCellViewHolder holder = (FriendListCellViewHolder)view.getTag();
			if(holder.btnAction.getVisibility()==View.INVISIBLE)
			{
				holder.btnAction.setVisibility(View.VISIBLE);
			}
			return true; 
		 }
	};
	
	private OnItemClickListener friendClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
		{
			// TODO Auto-generated method stub
			Intent intent = new Intent (FriendsActivity.this,FriendInfoActivity.class);	
			UserRoster roster = friendslistAdapter.userList.get(arg2);
			//Bundle bundle = intent.get
			intent.putExtra(LinkDefines.Data_JID, roster.JID);
			startActivity(intent);	
			return;
		}
	};
	
	/*
	private OnItemLongClickListener searchLongClickListener = new OnItemLongClickListener()
	{
		@Override
		public boolean onItemLongClick(AdapterView parent, View view, int position, long id)
		{ 
			ListView lv = (ListView)parent;
			FriendsListAdapter adapter = (FriendsListAdapter)lv.getAdapter();
			FriendListCellViewHolder holder = (FriendListCellViewHolder)view.getTag();
			if(holder.btnAction.getVisibility()==View.INVISIBLE)
			{
				holder.btnAction.setVisibility(View.VISIBLE);
			}
			return true; 
		 }
	};
	*/
	private OnItemClickListener searchItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			// TODO Auto-generated method stub
			SyncMgr syncmgr=SyncMgr.GetInstance();
			UserRoster roster = (UserRoster) searchListAdapter.getItem(arg2);
			if(syncmgr.AddFriend(roster.JID))
			{
				friendslistAdapter.AddUser(roster);
				friendslistAdapter.notifyDataSetChanged();
				searchListAdapter.RemoveUser(arg2);
				searchListAdapter.notifyDataSetChanged();
				Toast.makeText(FriendsActivity.this, "添加好友成功！", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(FriendsActivity.this, "已有该好友！", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private Boolean UpdateUIData()
	{
		SyncMgr syncmgr = SyncMgr.GetInstance();
		syncmgr.SyncFriendsList();
		for( int nc=0;nc<syncmgr.friendsArray.size();nc++)
		{
			friendslistAdapter.AddUser(syncmgr.friendsArray.get(nc));
		}

    	lvFriendsList.setAdapter(friendslistAdapter);
    	lvFriendsList.setOnItemLongClickListener(longClickListener);
    	lvFriendsList.setOnItemClickListener(friendClickListener);
    	
		lvSearchList.setAdapter(searchListAdapter);
		//lvSearchList.setOnItemLongClickListener(searchLongClickListener);
		lvSearchList.setOnItemClickListener(searchItemClickListener);

		return false;
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
        mTabFriends.setImageDrawable(getResources().getDrawable(R.drawable.tab_friends_clicked));
        mTabSetting.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_released));
        
		lvFriendsList = (ListView) findViewById(R.id.lv_friends);
		lvFriendsList.setDividerHeight(0); 
		friendslistAdapter = new FriendsListAdapter(this);
		friendslistAdapter.btnTitle="删除";

		lvSearchList = (ListView) findViewById(R.id.lv_search);
		lvSearchList.setDividerHeight(0);
		searchListAdapter = new FriendsListAdapter(this);
		searchListAdapter.btnTitle="增加";
		
		UpdateUIData();
		
		btnSearch = (Button)findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(searchClickListener);
		edSearchID = (EditText)findViewById(R.id.et_tosearch);
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
        setContentView(R.layout.main_tab_friends);
        
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.rl_friendslayout);
        int height=LinkcubeApplication.getApp(FriendsActivity.this).gHeight;
        int width =LinkcubeApplication.getApp(FriendsActivity.this).gWidth;
        android.widget.FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(width,height);
        rl.setLayoutParams(params1);
        //rl.addView(child, params)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 

        BindServiceListener();
        BindUIHandle();
    }
    
	@Override
	public void onResume(){
        super.onResume();  
        
        //Source for remote connection
        GameMgr gamemgr = GameMgr.GetInstance(FriendsActivity.this);
        gamemgr.StartUICMDListener();
        //Source for remote connection
        connectionBR.Init(FriendsActivity.this);
        connectionBR.EnableListener();
    }
	
	@Override
	public void onPause(){  
        super.onPause();  
        
        GameMgr gamemgr = GameMgr.GetInstance(FriendsActivity.this);
        
        //Source for remote connection
        gamemgr.StopUICMDListener();
        connectionBR.DisableListener();
    }

	@Override
	public void onStop(){  
        super.onStop();
    }  
	@Override
    public void onDestroy(){  
        super.onDestroy();  
    } 

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode)
		{
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
	
	class FriendsListAdapter extends BaseAdapter
    {
		Button.OnClickListener editListener = new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ActionItem item = (ActionItem)v.getTag();
				SyncMgr syncmgr=SyncMgr.GetInstance();
				syncmgr.RemoveUser(item.curJid);
				friendslistAdapter.RemoveUser(item.curJid);
				friendslistAdapter.notifyDataSetChanged();
			}
		};
        private LayoutInflater mInflater;
        public String btnTitle="";
        public List<UserRoster> userList = new ArrayList<UserRoster>();

        public FriendsListAdapter(Context context)
        {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
        	return userList.size();
        }

        @Override
        public Object getItem(int position)
        {
        	return userList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
        	return position;
        }

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			UserRoster roster = userList.get(position);
			FriendListCellViewHolder holder;
            if (convertView == null)
            {
            	
                convertView = mInflater.inflate(R.layout.listcell_friends, null);
                holder = new FriendListCellViewHolder();
                holder.userAvatar = (ImageView) convertView.findViewById(R.id.im_avatar);
                holder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
                holder.tvJID = (TextView) convertView.findViewById(R.id.tv_jid);
                holder.tvAge = (TextView) convertView.findViewById(R.id.tv_age);
                //holder.tvUserDesc = (TextView) convertView.findViewById(R.id.tv_userdesc);
                holder.btnAction = (Button) convertView.findViewById(R.id.btn_friendcellaction);
                holder.btnAction.setVisibility(View.INVISIBLE);
                holder.btnAction.setText(btnTitle);
				ActionItem item = new ActionItem(position,roster.JID);
				holder.btnAction.setTag(item);
				holder.btnAction.setOnClickListener(editListener);
                convertView.setTag(holder);
            }
            else
            {
                holder = (FriendListCellViewHolder) convertView.getTag();
            }

            if(roster!=null)
            {
            	holder.tvAge.setText(roster.UserAge()+"");
            	holder.tvJID.setText("ID:"+XMPPUtils.GetDisplayID(roster.JID));
            	holder.tvNickName.setText(roster.NickName);
            }
            else
            {
            	holder.tvAge.setText("3");
            }
            
            convertView = convertView;

            return convertView;
        }
		
		public void AddUser(UserRoster usr)
		{
			userList.add(usr);
		}
		
		public void RemoveUser(int idx)
		{
			userList.remove(idx);
		}
		
		public void RemoveUser(String jid)
		{
			for(int nc=0;nc<userList.size();nc++)
			{
				if(userList.get(nc).JID.equals(jid))
				{
					userList.remove(nc);
					return;
				}
			}
		}

		/* Define an item object, which is used to process the controls.*/
        public class FriendListCellViewHolder {
                TextView tvNickName;
                TextView tvJID;
                ImageView userAvatar;
                TextView tvAge;
                //TextView tvUserDesc;
                Button btnAction;
        }
    }
}
