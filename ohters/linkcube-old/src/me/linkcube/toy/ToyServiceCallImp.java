package me.linkcube.toy;

/*
 * 重要
 * 
 * 玩具service
 * 
 * 
 */
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import me.linkcube.client.LinkDefines;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ToyServiceCallImp extends android.os.Binder implements IToyServiceCall{
	
	final private String TAG="ToyServiceCallImp:";

	final public int Toy_Success=0;
	final public int Toy_Lost=1;
	final public int Toy_BadCommand=2;
	
	//private ArrayList<String> targetIDs=new ArrayList<String>();
	private String cmdBuffer="";

	private static Boolean bIsRemoted=false;

	private BluetoothDevice curDevice=null;
	private BluetoothSocket curSocket=null;
	//private Context appContext = null;
	private byte[] engineData={0x25,0x1,0x1,0x0,0x0,0x0,0x0,0x27};
	private byte[] modeData={0x25,0x1,0x0,0x0,0x1,0x0,0x0,0x27};

	
	private int ShakeThreshHold[]={0, 500,800,1000,2000,4000,5000,8000};
	private int KShakeToySpeed[]={2, 5, 10, 15, 20, 26, 30, 34};
	private int KShakeSpeed[][]={{2,3,6,11,16,22,26,28},
									{2,3,8,13,18,24,28,30},
									{2,5,10,15,20,26,30,34},
									{2,7,12,17,22,28,32,36},
									{2,9,14,19,24,30,34,38}};
	
	//private int WaveThreshHold[]={0,64,96,115,128,144,160,176,256};
	//private int WaveThreshHold[]={0,200,400,600,800,1000,1200,1400,1600};
	private int WaveThreshHold[]={0,700,890,980,1050,1130,1220,1450};
	//private int KToySpeed[]={1, 5, 10, 15, 20, 25, 32, 40};
	private int KWaveToySpeed[]={2, 5, 10, 15, 20, 26, 30, 34};
	private int KWaveSpeed[][]={{2,3,7,11,15,20,22,25},
								{2,4,8,13,18,24,28,30},
								{2,5,10,15,20,25,30,35},
								{6,10,15,20,25,30,34,37},
								{10,15,20,25,30,35,38,40}};
//	private BtState btState;
	public int curSpeed=-1;
	public int curMode=-1;
	
	private int shakeSensi=2;
	private int voiceSensi=2;
	
	private android.content.Context appContext;
	
	private final ArrayList<String> stringSpeedQueue=new ArrayList<String>();
	Queue<String> bufferQueue = new LinkedList<String>();
	private boolean processThreadRunning=false;
	//private ProcessThread processSpeed = new ProcessThread();
	private ProcessThread processThread = null;
	class ProcessThread extends Thread
	{
		private String PopupSpeed()
		{
			String packet;
			
			synchronized(stringSpeedQueue)
			{
				if (stringSpeedQueue.size() <= 0)
				{
					return null;
				}
				
				packet = stringSpeedQueue.get(0);
				
				stringSpeedQueue.remove(0);
				return packet;
			}	
		}
		@Override
		public void run()
		{
			while (processThreadRunning)
			{
				String cmdbuffer;
				cmdbuffer = PopupSpeed();
				
				if(cmdbuffer==null)
				{
					try {
						sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					int nc=0;
					int length=cmdbuffer.length();
					while(nc<length)
					{
						int spc = cmdbuffer.charAt(nc)-'@';
						nc++;
						setLocalToySpeed(spc);
						try {
							sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			stringSpeedQueue.clear();
		}
	};
	
	private IntentFilter cmdFilter;
	private BroadcastReceiver cmdBR=  new BroadcastReceiver()
	{
		/*
		 * 模式或者速度控制玩具
		 */
		private boolean processCmd(String jid, String cmdstring)
		{
			
			String[] cmddata = cmdstring.split(":");
			char cmd = cmddata[0].charAt(0);
			switch(cmd)
			{
			//Somebody want to connect you;
			case 'm':
				{
					if(processThreadRunning==true)
					{
						processThreadRunning=false;
						try {
							processThread.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							processThread.destroy();
							e.printStackTrace();
						}
						processThread = null;
						synchronized(stringSpeedQueue)
						{
							stringSpeedQueue.clear();
						}	
						//processThread.stop();
					}
					int mode = cmddata[1].charAt(0)-'0';
					Log.i("ToyService Get Remote mode:",cmddata[1]);
					setLocalToyMode(mode);
				}
				break;
				//Somebody want to disconnect you;
			case 's':
				{
					int speed = cmddata[1].charAt(0)-'@';
					Log.i("ToyService Get Remote speed:",cmddata[1]);
					setLocalToySpeed(speed);
					
					if(processThreadRunning==false)
					{
						processThread = new ProcessThread();
						processThread.start();
						processThreadRunning=true;
					}
					synchronized(stringSpeedQueue)
					{
						if(stringSpeedQueue.size()<10)
						{
							stringSpeedQueue.add(cmddata[1]);
						}
					}	
					//UserRoster friend = syncmgr.GetFriendRoster(jid);
				}
			default:
				break;
			}
			return true;
		}
		
		
		/*
		 * 
		 * 获取后解析处理命令
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
    	@Override
    	public void onReceive(Context context, Intent intent) {
			Bundle bundle=intent.getExtras();  //data为B中回传的Intent
			String fromJID=bundle.getString(LinkDefines.Data_From);
			String cmdbuf=bundle.getString(LinkDefines.Data_CMD);
			Log.i("Toy Processing Command:", cmdbuf);
			if(fromJID!=null&&cmdbuf!=null)
			{
				String []cmds = ParserUtil.GetCmds(cmdbuf);
				if(cmds.length>0)
				{
					for(int nc=0;nc<cmds.length;nc++)
					{
						processCmd(fromJID, cmds[nc]);
					}
				}
			}

    		return;
    	}
    };

    /*
     * toy服务实现构造函数，初始化sensorManager
     * 
     */
	public ToyServiceCallImp(android.content.Context con)
	{
		appContext = con;
		
        cmdFilter = new IntentFilter();
        cmdFilter.addAction(com.oplibs.support.Intents.MessageReceived);

    	SensorManager sensorManager = (SensorManager) appContext.getSystemService(Context.SENSOR_SERVICE);
	}
	
	/*
	 * 绑定
	 * (non-Javadoc)
	 * @see android.os.IInterface#asBinder()
	 */
	@Override
	public IBinder asBinder() {
		// TODO Auto-generated method stub
		return this;
	}

	/*
	 * 是否连接
	 * (non-Javadoc)
	 * @see me.linkcube.toy.IToyServiceCall#IsConnected(java.lang.String, java.lang.String)
	 */
	@SuppressLint("NewApi")
	@Override
	public boolean IsConnected(String name, String macaddr)
			throws RemoteException {
		// TODO Auto-generated method stub
		if(curDevice==null||curSocket==null)
		{
			return false;
		}
		String currentmac = curDevice.getAddress();
		if(macaddr.equalsIgnoreCase(currentmac))
		{
//			if(curSocket.isConnected())
				if(true)
			{
			        OutputStream tmpOut = null;  
			        try {  
			            tmpOut = curSocket.getOutputStream();  
			        } catch (IOException e) {  
			        	return false;
			        }  
			  
			        byte[] checkData={0x25,0x3,0x0,0x0,0x0,0x0,0x0,0x28};
			        try {
						tmpOut.write(checkData);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					} 
			        return true;
			}
		}
		return false;
	}
	
	/*
	 * 连接设备
	 */
	private boolean ConnectDevice()
	{
		//UUID btuuid ='{00001101-0000-1000-8000-00805F9B34FB}';
		UUID suuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//new UUID("00001101-0000-1000-8000-00805F9B34FB");
		BluetoothAdapter adapter= BluetoothAdapter.getDefaultAdapter();
		if(curDevice==null)
		{
			return false;
		}
		if(curSocket!=null)
		{
			try
			{
				curSocket.close();
				curSocket=null;
	        }
	        catch (IOException e)
	        {
	        }
		}
		
		BluetoothSocket tmp = null;
		try 
		{
		    tmp = curDevice.createInsecureRfcommSocketToServiceRecord(suuid);
		} catch (IOException e) {
		    return false;
		}			
		
		curSocket = tmp;
		
		try {
			curSocket.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				curSocket.close();
				curSocket=null;
	        }
	        catch (IOException e2) {
	        	return false;
	        }
	        
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * 获得设备
	 */
	private BluetoothDevice getDevice(String name, String mac)
	{
		BluetoothAdapter adapter= BluetoothAdapter.getDefaultAdapter();
        if(adapter!=null)
        {
        	if(!adapter.isEnabled())
        	{  
        		return null;
        	}
            Set<BluetoothDevice> devices = adapter.getBondedDevices();
            if(devices.size()>0)
            {  
            	for(Iterator<BluetoothDevice> iterator =devices.iterator();iterator.hasNext();)
            	{  
            		BluetoothDevice bluetoothDevice = iterator.next();      
            		String dname=bluetoothDevice.getName();
            		String dmac=bluetoothDevice.getAddress();
            		//if(dname.contains(name))
            		if(dmac.equalsIgnoreCase(mac))
            		{
            			if(bluetoothDevice.getAddress().equalsIgnoreCase(mac));
            			{
            				return bluetoothDevice;
            			}
            		}
            	}
            	
            }  
        }

		return null;
	}

	/*
	 * 连接玩具
	 * (non-Javadoc)
	 * @see me.linkcube.toy.IToyServiceCall#ConnectToy(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean ConnectToy(String name, String macaddr)
			throws RemoteException {
		// TODO Auto-generated method stub
		BluetoothAdapter adapter= BluetoothAdapter.getDefaultAdapter();
		curDevice = null;
        //判断BluetoothAdapter师傅哦为空，如果为空，则表明没有蓝牙设备  
        if(adapter!=null)
        {
        	if(!adapter.isEnabled())
        	{  
        		////Create an intent to startup bluetooth;  
        		//Intent intent = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);  
        		//appContext.startActivity(intent);
        		return false;
        	}
            Set<BluetoothDevice> devices = adapter.getBondedDevices();
            if(devices.size()>0)
            {  
            	for(Iterator<BluetoothDevice> iterator =devices.iterator();iterator.hasNext();)
            	{  
            		BluetoothDevice bluetoothDevice = iterator.next();      
            		String dname=bluetoothDevice.getName();
            		System.out.println(bluetoothDevice.getAddress());
            		System.out.println(dname);
            		if(dname.contains(name))
            		{
            			if(bluetoothDevice.getAddress().equalsIgnoreCase(macaddr));
            			{
            				curDevice = bluetoothDevice;
            				return ConnectDevice();
            			}
            		}
            	}
            	
            }  
        }

		return false;
	}

	/*
	 * 发送命令
	 */
	private int sendCommand(byte[] data)
	 {
			//InputStream tmpIn = null;  
	        OutputStream tmpOut = null;  
	        try {  
	            //tmpIn = curSocket.getInputStream();  
	            tmpOut = curSocket.getOutputStream();  
	        } catch (IOException e) {  
	            Log.e(TAG, "Bluetooth outputstream sockets not created", e);
	        	return Toy_Lost;
	        }  
	  
	        try {
				tmpOut.write(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Toy_Lost;
			} 
	        return Toy_Success;
	 }

	/*
	 * 设置当前玩具速度
	 */
	@SuppressLint("NewApi")
	private int setLocalToySpeed(int speed){
		// TODO Auto-generated method stub
		if(speed>40||speed<1)
			 return Toy_BadCommand;
		
		if((curSocket==null))
//				||!curSocket.getRemoteDevice().  .isConnected())
		 {
			 return Toy_Lost;
		 }
		 curSpeed = speed;
		 byte spd = (byte)speed;
		 byte crc = (byte) (0x26+spd);
		 engineData[2] = spd;
		 engineData[7] = crc;
		 return sendCommand(engineData);
	}

	/*
	 * 设置当前玩具模式
	 */
	@SuppressLint("NewApi")
	private int setLocalToyMode(int mode) {
		// TODO Auto-generated method stub
		Log.i("LoacalToyMode:",mode+"");
		 if(mode>8||mode<0)
			 return Toy_BadCommand;
		 
		 if(mode==8)
		 {
			 return setLocalToySpeed(1);
		 }
		 if((curSocket==null)) //||!curSocket.isConnected())
		 {
			 return Toy_Lost;
		 }
		 if(mode==0)
		 {
			 setLocalToySpeed(1);
		 }

		 byte newmode = (byte)mode;
		 byte crc = (byte) (0x26+mode);
		 modeData[4] = newmode;
		 modeData[7] = crc;
		 return sendCommand(modeData);
	}
	
	/*
	 * 提高？添加 远程速度
	 */
	private void addRemoteSpeed(int speed)
	{
		if(cmdBuffer.length()>0)
		{
			cmdBuffer+=(","+speed);
		}
		else
		{
			cmdBuffer += speed;
		}
	}
	
	
	/*
	 * Cache？缓存玩具速度
	 * (non-Javadoc)
	 * @see me.linkcube.toy.IToyServiceCall#CacheToySpeed(int, boolean)
	 */
	@Override
	public int CacheToySpeed(int speed,boolean overtime) throws RemoteException {
		// TODO Auto-generated method stub
		Log.i("Cache Local Speed:",""+speed);
		return setLocalToySpeed(speed);
		//return 0;
	}

	/*
	 * Cache?缓存玩具模式
	 * (non-Javadoc)
	 * @see me.linkcube.toy.IToyServiceCall#CacheToyMode(int)
	 */
	@Override
	public int CacheToyMode(int mode) throws RemoteException {
		// TODO Auto-generated method stub
		Log.i("Cache Local Mode:",""+mode);
		return setLocalToyMode(mode);
		//return 0;
	}


	@Override
	public boolean CloseToy(String name, String macaddr) throws RemoteException {
		// TODO Auto-generated method stub
		if(curDevice==null||curSocket==null)
		{
			return false;
		}
		try
		{
			curSocket.close();
			curSocket=null;
        }
        catch (IOException e2)
        {
        	return false;
        }
		return true;
	}

	@Override
	public int CacheShake(long shkspd,boolean overtime) throws RemoteException {
		// TODO Auto-generated method stub
		int spd;
		for(spd=0;(spd<ShakeThreshHold.length)&&(shkspd>=ShakeThreshHold[spd]);spd++)
		{
		}
		
		CacheToySpeed(KShakeSpeed[shakeSensi][spd-1],overtime);
		return 0;
	}

	//声音监控相关的功能
	@Override
	public int SetWave(long waveng) throws RemoteException {
		// TODO Auto-generated method stub
		int spd;
		for(spd=0;(spd<WaveThreshHold.length)&&(waveng>=WaveThreshHold[spd]);spd++)
		{
			
		}
		
		CacheToySpeed(KWaveSpeed[voiceSensi][spd-1],false);
		return 0;
	}

	/*
	 * 设置wave模式
	 * (non-Javadoc)
	 * @see me.linkcube.toy.IToyServiceCall#SetWaveMode(int, int)
	 */
	@Override
	public boolean SetWaveMode(int index, int val) throws RemoteException {
		// TODO Auto-generated method stub
		if(index<WaveThreshHold.length)
		{
			//WaveThreshHold[index]=val;
			ShakeThreshHold[index]=val;
		}
		return false;
	}

	/*
	 * 
	 * 设置震动模式
	 * @see me.linkcube.toy.IToyServiceCall#SetShakeMode(int, int)
	 */
	@Override
	public boolean SetShakeMode(int index, int val) throws RemoteException {
		// TODO Auto-generated method stub
		if(index<KWaveToySpeed.length)
		{
			//KWaveToySpeed[index]=val;
			KShakeToySpeed[index]=val;
		}
		return false;
	}

	/*
	 * 开启远程模式
	 * @see me.linkcube.toy.IToyServiceCall#EnableToyRemoteMode(boolean, java.lang.String)
	 */
	@Override
	public boolean EnableToyRemoteMode(boolean enable, String jid)
			throws RemoteException {
		// TODO Auto-generated method stub
		if(bIsRemoted!=enable)
		{
			if(enable)
			{
				Log.i("Toy:","Begin remote mode!");
				appContext.registerReceiver(cmdBR,cmdFilter);
			}
			else
			{
				Log.i("Toy:","Stop remote mode!");
				appContext.unregisterReceiver(cmdBR);
			}
		}
		cmdBuffer="";
		bIsRemoted = enable;
		return true;
	}

	/*
	 * 设置震动Sensi
	 * @see me.linkcube.toy.IToyServiceCall#SetShakenSensi(int)
	 */
	@Override
	public void SetShakenSensi(int v) throws RemoteException {
		// TODO Auto-generated method stub
		shakeSensi = v;
	}
	
	@Override
	public void SetVoiceSensi(int v) throws RemoteException {
		// TODO Auto-generated method stub
		voiceSensi = v;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean IsToyConnected() throws RemoteException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		if(curDevice==null||curSocket==null)
		{
			return false;
		}
		if(true)//curSocket.isConnected())
		{
			//InputStream tmpIn = null;  
	        OutputStream tmpOut = null;  
	        try {  
	            //tmpIn = curSocket.getInputStream();  
	            tmpOut = curSocket.getOutputStream();  
	        } catch (IOException e) {  
	            Log.i(TAG, "temp sockets not created", e);
	        	return false;
	        }  
	  
	        byte[] checkData={0x25,0x3,0x0,0x0,0x0,0x0,0x0,0x28};
	        try {
				tmpOut.write(checkData);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} 
	        return true;
		}
		return false;
	}

}
