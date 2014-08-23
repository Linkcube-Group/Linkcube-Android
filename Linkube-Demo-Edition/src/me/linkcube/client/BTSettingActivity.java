package me.linkcube.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.linkcube.client.BTSettingActivity.DeviceListAdapter.DeviceCellViewHolder;
import me.linkcube.toy.*;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 
 * 设置界面
 * 
 */

public class BTSettingActivity extends Activity {
	
	private IToyServiceCall toyServiceCall;
	
	private ImageView ivSwitch;
	
	private ImageView btnRefresh;
	private ImageView btnReturn;
	
	private ProgressBar pbUpdating;
	
	AnsyConnect asynCall = null;

	private ListView lvDevList;
	protected DeviceListAdapter devlistAdapter = null;

	private ListView lvubDevList;
	protected DeviceListAdapter ubDevlistAdapter = null;
	
	//ArrayList<BluetoothDevice> unPariedDevList = new ArrayList<BluetoothDevice>();
	ArrayList<BluetoothDevice> pariedDevList = new ArrayList<BluetoothDevice>();
	
	private BluetoothDevice dev2bond=null;

	public final String[] KToyName={"Mars","Venus","OBDII"};
    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver btdeviceDiscovery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	for(int nc=0;nc<KToyName.length;nc++)
                	{
                		if(device.getName().equalsIgnoreCase(KToyName[nc]))
                		{
                			ubDevlistAdapter.addNew(device);
                        	ubDevlistAdapter.notifyDataSetChanged();
                			break;
                		}
                	}
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	stopRefreshing();
            	Toast.makeText(BTSettingActivity.this, "搜索蓝牙设备完毕！", Toast.LENGTH_SHORT).show();
            	ubDevlistAdapter.notifyDataSetChanged();
            }
            else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
            {
            	
            }
        }
    };
    
    private final BroadcastReceiver bluetoothEnablement = new BroadcastReceiver()
    {
        @Override
		public void onReceive(Context context, Intent intent)
        {
	        String stateExtra = BluetoothAdapter.EXTRA_STATE;
	        int state = intent.getIntExtra(stateExtra, -1);
	        switch(state) {
		    case BluetoothAdapter.STATE_TURNING_ON:
	            break;
	        case BluetoothAdapter.STATE_ON:
	        	try {
					UpdateDeviceListUI(true);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            break;
	        case BluetoothAdapter.STATE_TURNING_OFF:
	            break;
	        case BluetoothAdapter.STATE_OFF:
	        	try {
					UpdateDeviceListUI(false);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            break;
	        }
        }
    };

 
	private Button.OnClickListener returnLastUIListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			ResetBTAdapter();
			Intent intent = new Intent();
			setResult(0,intent);
			finish();
		}
	};

	private Button.OnClickListener refreshBtDeviceListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			//unPariedDevList.clear();
			ResetBTAdapter();
			ubDevlistAdapter.ResetListData();
			ubDevlistAdapter.notifyDataSetChanged();
			startRefreshing();
			doDiscovery();
		}
	};
	
	protected void doDiscovery()
	{
        // Request discover from BluetoothAdapter
        BluetoothAdapter madapter = BluetoothAdapter.getDefaultAdapter();
        madapter.startDiscovery();
    }

	public boolean BondToy(BluetoothDevice dev) throws RemoteException
	{
		// TODO Auto-generated method stub
		Method createBondMethod;
		try {
			createBondMethod = dev.getClass().getMethod("createBond");
			try {
				createBondMethod.invoke(dev);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void connectToyByActionButton(View v)
	{
		DeviceCellViewHolder holder = (DeviceCellViewHolder)v.getTag();
		//int ind = holder.ind;
		BluetoothDevice btdev=holder.btDev;
		DeviceListAdapter adapter = (DeviceListAdapter)lvDevList.getAdapter();
		//Map.Entry<String, String> entry = adapter.getEntry(ind);
		/*BluetoothDevice dev=adapter.devList.get(ind);
		String devname = dev.getName();
		String macaddr = dev.getAddress();
		*/
		try {
			if(toyServiceCall.IsConnected(btdev.getName(), btdev.getAddress()))
			{
				Toast.makeText(BTSettingActivity.this, "该玩具已经连接！", Toast.LENGTH_SHORT).show();
			}
			else
			{
				//btmgr.CloseCurrentConnection();
				//devlistAdapter.notifyDataSetChanged();

				startRefreshing();
				asynCall = new AnsyConnect(btdev.getName(),btdev.getAddress(),holder.tvDevStatus);
				asynCall.execute(btdev.getName());
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void bondToyByActionButton(View v)
	{
		DeviceCellViewHolder holder = (DeviceCellViewHolder)v.getTag();
		//int ind = holder.ind;
		//ind2Bond = holder.ind;
		//BluetoothDevice btdev = holder.btDev;
		dev2bond = holder.btDev;
		//ListView lv = (ListView)parent;
		DeviceListAdapter adapter = (DeviceListAdapter)lvubDevList.getAdapter();
		//BluetoothDevice dev = adapter.devList.get(ind);
		//name2Bond = dev2bond.getName();
		//mac2Bond = dev2bond.getAddress();
		
		//devtobeBond = adapter.devList.get(ind);
		
		Intent intent = new Intent(BTSettingActivity.this,InfoDlgActivity.class);
		String Info = "是否要绑定该设备？"; 
		intent.putExtra(LinkDefines.Data_Nickname, Info);
		intent.putExtra("infodetail", "");
		startActivityForResult(intent,0);
	}
	
	private Button.OnClickListener connectDeviceListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			View pv = (View) v.getParent().getParent();
			if(pv==lvDevList)
			{
				ResetBTAdapter();
				connectToyByActionButton(v);
			}
			else if(pv==lvubDevList)
			{
				ResetBTAdapter();
				bondToyByActionButton(v);
			}
		}
	};
	
	private Button.OnClickListener enableBtListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			BTToyMgr btmgr = BTToyMgr.getInstance();
			boolean targetstate = !btmgr.IsBlueToothEnabled();
			//	targetstate = btmgr.IsBlueToothEnabled();
			ResetBTAdapter();
			
			btmgr.SetBTEnabled(targetstate);
			try {
				UpdateDeviceListUI(btmgr.IsBlueToothEnabled());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_btsetting);

        IntentFilter btWatch = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothEnablement, btWatch);

        IntentFilter devWatch = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        devWatch.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        devWatch.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(btdeviceDiscovery, devWatch);
        
        toyServiceCall = LinkcubeApplication.getApp(BTSettingActivity.this).toyServiceCall;

        try {
			BindUIHandle();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	@Override
	public void onResume(){
        super.onResume();  
        ResetBTAdapter();
        ubDevlistAdapter.ResetListData();
        ubDevlistAdapter.notifyDataSetChanged();
	}
    
	
	@Override
	public void onPause(){  
		ResetBTAdapter();
		//ubDevlistAdapter.ResetListData();
		//ubDevlistAdapter.notifyDataSetChanged();
        super.onPause();
    }
	
    protected void ResetBTAdapter()
    {
    	BluetoothAdapter madapter = BluetoothAdapter.getDefaultAdapter();
        if (madapter!=null&&madapter.isDiscovering()) {
        	madapter.cancelDiscovery();
        }
    }
    @Override
    protected void onDestroy()
    {
    	ResetBTAdapter();

        unregisterReceiver(btdeviceDiscovery);
        unregisterReceiver(bluetoothEnablement);
    	super.onDestroy();
    }
    
    private void startRefreshing()
    {
    	pbUpdating.setVisibility(View.VISIBLE);
    }
    
    private void stopRefreshing()
    {
    	pbUpdating.setVisibility(View.INVISIBLE);
    }
    
    protected void BindUIHandle() throws RemoteException
    {
    	btnRefresh = (ImageView)findViewById(R.id.ibtn_refresh);
    	btnRefresh.setOnClickListener(refreshBtDeviceListener);
    	btnReturn = (ImageView)findViewById(R.id.ibtn_return);
    	btnReturn.setOnClickListener(returnLastUIListener);

    	lvDevList = (ListView)findViewById(R.id.lv_devicelist);
    	lvDevList.setDividerHeight(0);
    	devlistAdapter = new DeviceListAdapter(this,null);
    	devlistAdapter.ntype=0;
    	lvDevList.setAdapter(devlistAdapter);
    	
    	lvubDevList = (ListView)findViewById(R.id.lv_ubdevicelist);
    	lvubDevList.setDividerHeight(0); 
    	ubDevlistAdapter = new DeviceListAdapter(this,null);
    	devlistAdapter.ntype=1;
    	lvubDevList.setAdapter(ubDevlistAdapter);

    	pbUpdating = (ProgressBar)findViewById(R.id.pb_updatedevlist);
    	stopRefreshing();

    	ivSwitch = (ImageView)findViewById(R.id.imageView2);
    	ivSwitch.setOnClickListener(enableBtListener);
		
    	BTToyMgr btmgr = BTToyMgr.getInstance();
    	UpdateDeviceListUI(btmgr.IsBlueToothEnabled());    	
    }

    private void RefreshBondDeviceList()
    {
    	BTToyMgr btmgr = BTToyMgr.getInstance();
    	List<BluetoothDevice> devicelist = btmgr.UpdateBondDevList();
		devlistAdapter.ResetListData();
		if(devicelist!=null)
		{
			for(int nd=0;nd<devicelist.size();nd++)
    		{
    			devlistAdapter.addNew(devicelist.get(nd));
    		}
	
		}
    }
    protected void UpdateDeviceListUI(boolean isbteanbled) throws RemoteException
	{
    	//BTToyMgr btmgr = BTToyMgr.getInstance();
    	
    	if(isbteanbled)
    	{
    		ivSwitch.setBackgroundResource(R.drawable.bg_setting_btn_on);
    		btnRefresh.setVisibility(View.VISIBLE);
    		
    		RefreshBondDeviceList();
    		devlistAdapter.notifyDataSetChanged();
    	}
    	else
    	{
    		ivSwitch.setBackgroundResource(R.drawable.bg_setting_btn_off);
    		btnRefresh.setVisibility(View.INVISIBLE);
    		devlistAdapter.ResetListData();
    		devlistAdapter.notifyDataSetChanged();
    	}
		stopRefreshing();

    	return;
	}
    
 
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		case LinkDefines.RES_No:
			{
				//Bundle b=data.getExtras();  //data为B中回传的Intent
			}
			break;
		case LinkDefines.RES_Yes:
		{
			//if(ind2Bond<ubDevlistAdapter.devList.size())
			{
				try {
					//BluetoothDevice dev = ubDevlistAdapter.devList.get(ind2Bond);
					if(this.BondToy(dev2bond))
					{
						ubDevlistAdapter.RemoveDev(dev2bond);
						ubDevlistAdapter.notifyDataSetChanged();
						devlistAdapter.addNew(dev2bond);
						devlistAdapter.notifyDataSetChanged();
						//ind2Bond = -1;
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);  
	}

    class DeviceListAdapter extends BaseAdapter
    {
        private LayoutInflater mInflater;
        public int ntype;
        public List<BluetoothDevice> devList = new ArrayList<BluetoothDevice>();
        public Map devMap = new HashMap<String,String>();

        public DeviceListAdapter(Context context, Map map)
        {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
        	return devList.size();
        }

        @Override
        public Object getItem(int position)
        {
        	return devList.size();
        }

        @Override
        public long getItemId(int position)
        {
        	return position;
        }

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			DeviceCellViewHolder holder;
			//BTToyMgr btmgr=BTToyMgr.getInstance();
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.listcell_btdevice, null);
                holder = new DeviceCellViewHolder();
                holder.tvDevName = (TextView) convertView.findViewById(R.id.tv_devname);
                holder.tvDevStatus = (TextView) convertView.findViewById(R.id.tv_devstatus);
                holder.actionButton = (Button) convertView.findViewById(R.id.btn_btdevaction);
                holder.actionButton.setOnClickListener(connectDeviceListener);
                //holder.ind = position;
                holder.btDev = devList.get(position);
                holder.actionButton.setTag(holder);
                convertView.setTag(holder);
            }
            else
            {
                holder = (DeviceCellViewHolder) convertView.getTag();
            }
            convertView = convertView;

            holder.tvDevName.setText(devList.get(position).getName());
            if(ntype==1)
            {
            	try {
            		if(toyServiceCall.IsConnected(devList.get(position).getName().toString(), devList.get(position).getAddress().toString()))
					{
						holder.tvDevStatus.setText(getString(R.string.str_btdev_conntted));
					}
					else
					{
						holder.tvDevStatus.setText(getString(R.string.str_btdev_disconntted));
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            else
            {
            	holder.tvDevStatus.setText(getString(R.string.str_btdev_unpaired));
            }
            return convertView;
        }

		public boolean ResetListData()
		{
			devList.clear();
			//notifyDataSetChanged();
			return true;
		}

		public boolean RemoveDev(BluetoothDevice dev)
		{
			for(int nc=0;nc<devList.size();nc++)
			{
				if(devList.get(nc)==dev)
				{
					devList.remove(nc);
					break;
				}
			}
			
			return true;
		}
		
		@SuppressWarnings("unchecked")
		public boolean addNew(BluetoothDevice dev)
		{
			devList.add(dev);
			return true;
		}

		public Map.Entry<String, String> getEntry(int index)
		{
			Iterator<Map.Entry<String, String>> it = devMap.entrySet().iterator();
			if(index<devMap.size()&&index>=0)
			{
				int i=0;
				while (it.hasNext())
				{
					Map.Entry<String, String> entry = it.next();
					if(i==index)
					{
						return entry;
					}
					i++;
					//System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
				}
			}
			return null;
		}
        //Define an item object, which is used to process the controls.
        public class DeviceCellViewHolder {
                TextView tvDevName;
                TextView tvDevStatus;
                Button actionButton;
                BluetoothDevice btDev;
                //int ind;
        }
    }
    
    class AnsyConnect extends AsyncTask<String, String, TextView>{
    	
    	String devname=null;
    	String macaddr=null;
    	private boolean bResult = false;
    	ProgressBar pbConnectting = null;
    	//ToggleButton tbResult = null;
    	TextView statusView;
        
        public AnsyConnect(String strings, String mac, TextView res) {
            super();
            devname = strings;
            macaddr = mac;
            statusView = res;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

		@Override
		protected TextView doInBackground(String... params) {
			// TODO Auto-generated method stub
			//BTToyMgr btmgr = BTToyMgr.getInstance();
			//btmgr.ScanBTToy(params[0]);
			//bResult=btmgr.ConnectDevice();
			try {
				bResult=toyServiceCall.ConnectToy(devname,macaddr);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				bResult = false;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(TextView result)
		{
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //tbResult.setChecked(bResult);
            if(bResult)
            {
            	//statusView.setText("已连接");
            	statusView.setText(getString(R.string.str_btdev_conntted));
            	Toast.makeText(BTSettingActivity.this, "连接玩具成功！", Toast.LENGTH_SHORT).show();
            }
            else
            {
            	statusView.setText(getString(R.string.str_btdev_disconntted));
            	Toast.makeText(BTSettingActivity.this, "连接玩具失败，请重试！", Toast.LENGTH_SHORT).show();
            }
        	stopRefreshing();
        }

    }
}
