package me.linkcube.toy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

/*
 * 
 * 玩具绑定相关
 * 蓝牙列表等
 * 
 */

public class BTToyMgr {

	//玩具状态
	public enum ToyStatus
	{
		Toy_Success,
		Toy_Lost,
		Toy_BadCommand
	};
	
	//默认名称
	public final String[] KToyName={"Mars","Venus","OBDII"};
	
	private static BTToyMgr instance = null;

	//蓝牙设备
	private BluetoothDevice btDevice;;
	
	//句柄
	private Context appContext = null;
	
//	private BtState btState;
	//当前速度和当前模式
	public int curSpeed=-1;
	public int curMode=-1;
	
	protected List<String> pairedNameList = null;
	protected List<String> scannedNameList = null;

	
	public static BTToyMgr getInstance()
	{
		if (instance == null){
			instance = new BTToyMgr();
		}
		return instance;
	 }
	
	//初始化环境 句柄、速度、蓝牙设备
	public boolean InitEnv(Context ctx)
	{
		if(appContext==null)
		{
			appContext = ctx;
			//btState = BtState.BTDevUninitialized;
			curSpeed = 0;
			btDevice = null;
			return true;
		}
		return false;
	}
	
	
	//是否有蓝牙设备:判断适配器是否为null
	public boolean IsHaveBlueTooth()
	{
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter(); 
		if(adapter==null)
			return false;
		else
			return true;
	}
	
	//蓝牙设备是否工作
	public boolean IsBlueToothEnabled()
	{
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		return adapter.isEnabled();
	}
	
	//开启或关闭设备
	public void SetBTEnabled(boolean enabled)
	{
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if(enabled)
		{
			adapter.enable(); 
		}
		else
		{
			adapter.disable();
		}
	}
	
	//更新蓝牙设备列表
	public List<BluetoothDevice> UpdateBondDevList()
	{
		int devicesize;
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

		// Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        devicesize = pairedDevices.size();
        if (devicesize > 0) {
            List<BluetoothDevice> devlist = new ArrayList<BluetoothDevice>();
            //findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
            	int nc=0;
                //mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            	for(nc=0;nc<KToyName.length;nc++)
            	{
            		if(device.getName().equalsIgnoreCase(KToyName[nc]))
            		{
            			devlist.add(device);
            			break;
            		}
            	}
            }
            return devlist;
        }
        else
        {
        	return null;
        }
	}

	//绑定设备
	public boolean BindDevice(BluetoothDevice dev)
	{
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
	
	//扫描玩具
	public boolean ScanBTToy(String btname)
	{
		BluetoothAdapter adapter= BluetoothAdapter.getDefaultAdapter();
		btDevice = null;
        //判断BluetoothAdapter是否为空，如果为空，则表明没有蓝牙设备  
        if(adapter!=null){  
            System.out.println("本机拥有BlueTooth");  
            //调用isEnable方法，判断当前蓝牙是否可用  
                if(!adapter.isEnabled())
                {  
                    //Create an intent to startup bluetooth;  
                    Intent intent = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);  
                    appContext.startActivity(intent);  
                }
            Set<BluetoothDevice> devices = adapter.getBondedDevices();  
                if(devices.size()>0){  
                    for(Iterator<BluetoothDevice> iterator =devices.iterator();iterator.hasNext();){  
                        BluetoothDevice bluetoothDevice = iterator.next();      
                        //打印远程蓝牙设备的地址  
                        String dname=bluetoothDevice.getName();
                        System.out.println(bluetoothDevice.getAddress());
                        System.out.println(dname);
                        if(dname.contains(btname))
                        {
                        	btDevice = bluetoothDevice;
                        	//deviceMac = bluetoothDevice.getAddress();
                        	return true;
                        	//break;
                        }
                    }  
                }  
        }
        else
        {  
            return false;
        }  
	
		return true;
	}
}
