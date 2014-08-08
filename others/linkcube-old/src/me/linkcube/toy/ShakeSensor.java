package me.linkcube.toy;

//import android.bluetooth.BluetoothSocket;
import com.oplibs.services.IOnChatServiceCall;
import com.oplibs.syncore.SyncMgr;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.RemoteException;
import android.util.Log;


/*
 *
 * 摇一摇 相关
 * 传感器注册、开启服务，根据传感器监控到的变化相应改变等
 * 
 */

public class ShakeSensor implements SensorEventListener
{
	private static final String TAG = ShakeSensor.class.getSimpleName();  
    
	//public BluetoothSocket commandSocket;
	private int ShakeThreshHold[]={0, 500,800,1000,2000,4000,5000,8000};
	private int KShakeToySpeed[]={2, 5, 10, 15, 20, 26, 30, 34};
	private int KShakeSpeed[][]={{2,3,6,11,16,22,26,28},
									{2,3,8,13,18,24,28,30},
									{2,5,10,15,20,26,30,34},
									{2,7,12,17,22,28,32,36},
									{2,9,14,19,24,30,34,38}};
	private int shakeSensi=2;
	
    //private static final double SHAKE_SHRESHOLD = 2000d;  
	//private Context mContext;
	private IToyServiceCall toyServiceCall;
	
	private String cmdBuffer="";
	
	private long lastSampleTime;
	private long lastCacheTime;
	private float last_x;  
	private float last_y;  
	private float last_z; 
	
	public float deviceSpeed; 
	public IOnChatServiceCall chatServiceCall=null;
     
	private SensorManager sensorManager;  
	private onShakeListener shakeListener;  
     
	public ShakeSensor(Context context){  
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}  
	
	public void SetHandle(IToyServiceCall icall)
	{
		toyServiceCall = icall;
	}
       
   /** 
    * 注册传感器 
    */  
	public boolean registerListener()
	{  
		if (sensorManager != null)
		{  
			Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);  
			if (sensor != null)
			{  
				if(sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME))
				{
					lastCacheTime = java.lang.System.currentTimeMillis();
				}
				//LogUtil.log(TAG, "registerListener: succeed!!");  
				return true;  
			}  
		}  
		return false;  
	}  
       
   /** 
    * 反注册传感器 
    */  
	public void unRegisterListener()
	{  
		if (sensorManager != null)  
			sensorManager.unregisterListener(this);  
	}  
     
	//传感器监控
	public void setOnShakeListener(onShakeListener listener)
	{  
		shakeListener = listener;  
	}  
 
	@Override
	public void onSensorChanged(SensorEvent event)
	{  
		//BTToyMgr mgr = BTToyMgr.getInstance();
		SyncMgr syncmgr = SyncMgr.GetInstance();
		
		float speed;
		//if (event.sensor.getType() == SensorManager.SENSOR_ACCELEROMETER) {   
		long curTime = java.lang.System.currentTimeMillis();  
		if ((curTime - lastSampleTime) > 10)
		{  
			long diffTime = (curTime - lastSampleTime);  
			lastSampleTime = curTime;
			float x = event.values[0];  
			float y = event.values[1];  
			float z = event.values[2];  
			speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;  

			//mgr.SetSpeed(ToySpeed[nc]);
			//Log.i("Shake:", speed+"");
			try {
				//if(toyServiceCall.IsRemoted())
				if(syncmgr.bIsRemoted)
				{
					int spd;
					for(spd=0;(spd<ShakeThreshHold.length)&&(speed>=ShakeThreshHold[spd]);spd++)
					{
					}
					
					int toyspeed = KShakeSpeed[shakeSensi][spd-1];
					
					char spdcode = (char) ('@'+toyspeed);
					if((curTime-lastCacheTime)>2000)
					{
						//toyServiceCall.CacheShake((long)speed, true);
						shakeListener.onShake();
						lastCacheTime = curTime;
						syncmgr.SetRemoteToyMode("s:"+cmdBuffer);
						cmdBuffer="";
					}
					cmdBuffer+=spdcode;
					Log.i("Cache remote speed:",""+speed);
				}
				else
				{
					toyServiceCall.CacheShake((long)speed, false);
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			last_x = x;  
			last_y = y;  
			last_z = z;
			deviceSpeed = speed;
		}  
	}  

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{  
		// TODO Auto-generated method stub   
	}  
     
   /** 
    *  
    * @author Nono 
    * 
    */  
	public interface onShakeListener
	{
		public void onShake();  
	}  
}
