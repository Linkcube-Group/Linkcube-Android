package me.linkcube.app.core.toy;

import me.linkcube.app.LinkcubeApplication;
import me.linkcube.app.core.Timber;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.RemoteException;

public class ShakeSensor implements SensorEventListener {

	private int ShakeThreshHold[] = { 0, 500, 800, 1000, 2000, 4000, 5000, 8000 };

	private int KShakeToySpeed[] = { 2, 5, 10, 15, 20, 26, 30, 34 };

	private int KShakeSpeed[][] = { { 2, 3, 6, 11, 16, 22, 26, 28 },
			{ 2, 3, 8, 13, 18, 24, 28, 30 }, { 2, 5, 10, 15, 20, 26, 30, 34 },
			{ 2, 7, 12, 17, 22, 28, 32, 36 }, { 2, 9, 14, 19, 24, 30, 34, 38 } };
	private int shakeSensi = 2;

	private String cmdBuffer = "";

	private long lastSampleTime, lastCacheTime;

	private float last_x, last_y, last_z;

	private SensorManager sensorManager;

	private ASmackRequestCallBack iCallBack;

	private boolean ismultiGame = false;

	private static float MAXVALUE = 200;

	// private Boolean sendSignBoolean = false;

	private ShakeSpeedCache mQueue = new ShakeSpeedCache();

	public ShakeSensor(Context context) {
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
	}

	/**
	 * 注册传感器
	 */
	public boolean registerListener() {
		if (sensorManager != null) {
			Sensor sensor = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if (sensor != null) {
				if (sensorManager.registerListener(this, sensor,
						SensorManager.SENSOR_DELAY_GAME)) {
					lastCacheTime = java.lang.System.currentTimeMillis();
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 反注册传感器
	 */
	public void unRegisterListener() {
		if (sensorManager != null)
			sensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		Timber.d("sensor x=%f,y=%f,z=%f", event.values[0], event.values[1],
				event.values[2]);

		float speed;

		long curTime = java.lang.System.currentTimeMillis();

		if ((curTime - lastSampleTime) > 10) {
			long diffTime = (curTime - lastSampleTime);
			lastSampleTime = curTime;
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime
					* 10000;
			int currentSpeed = 0;
			ShakeSpeedData tempAvg = mQueue.addElement(x, y, z);
			
			if (tempAvg != null) {
				float length = (int) Math.sqrt(Math.pow(x- last_x, 2) + Math.pow(y- last_y, 2)
						+ Math.pow(z- last_z, 2)) * 10;
				// String currentValue = tempAvg.getX() + "," + tempAvg.getY() +
				// "," + tempAvg.getZ() + "," + length + "," +
				// getDangWei(length);
				Timber.d("length:"+length);
				currentSpeed = getDangWei(length);
				// 如果当前向量和的长度大于MAXVALUE，则更新MAXVALUE
				if (length > MAXVALUE) {
					MAXVALUE = length;
				}
				Timber.d("currentSpeed:" + currentSpeed);
				try {
					if (!ismultiGame) {
						LinkcubeApplication.toyServiceCall.cacheShake(
								currentSpeed, false);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

			 Timber.d("shake speed = %f", speed);

			// int spd = 0;
			// while (spd < ShakeThreshHold.length
			// && speed >= ShakeThreshHold[spd]) {
			// spd++;
			// }
			//
			// int toyspeed = KShakeSpeed[shakeSensi][spd - 1];
			//
			// char spdcode = (char) ('@' + toyspeed);
			// if ((curTime - lastCacheTime) > 2000) {
			// shakeListener.onShake();
			// lastCacheTime = curTime;
			// cmdBuffer = "";
			// }
			// cmdBuffer += spdcode;
			if (ismultiGame) {
				iCallBack.responseSuccess((long) speed);
			}

			last_x = x;
			last_y = y;
			last_z = z;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * 根据输入的float数值，返回对应的档位
	 * 
	 * @param input
	 *            ：获得的加速度和的模
	 * @return 当前加速度对应的档位
	 */
	private int getDangWei(float input) {
		int result = 0;

		if (input < MAXVALUE * 0.5) {
			if (input < MAXVALUE * 0.2) {
				if (input < MAXVALUE * 0.1) {
					result = 5;
				} else {
					result = 10;
				}
			} else {
				if (input < MAXVALUE * 0.3) {
					result = 15;
				} else {
					if (input < MAXVALUE * 0.4) {
						result = 20;
					} else {
						result = 25;
					}
				}
			}
		} else {
			if (input < MAXVALUE * 0.7) {
				if (input < MAXVALUE * 0.6) {
					result = 25;
				} else {
					result = 30;
				}
			} else {
				if (input < MAXVALUE * 0.9) {
					if (input < MAXVALUE * 0.8) {
						result = 35;
					} else {
						result = 40;
					}
				} else if (input < MAXVALUE) {
					result = 45;
				} else {
					result = 50;
				}
			}
		}
		return result;
	}

	public ASmackRequestCallBack getiCallBack() {
		return iCallBack;
	}

	public void setiCallBack(ASmackRequestCallBack iCallBack) {
		this.iCallBack = iCallBack;
	}

	public boolean isIsmultiGame() {
		return ismultiGame;
	}

	public void setIsMultiGame(boolean ismultiGame) {
		this.ismultiGame = ismultiGame;
	}

}
