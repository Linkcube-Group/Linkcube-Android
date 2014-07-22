package me.linkcube.app.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import me.linkcube.app.core.Timber;
import me.linkcube.app.core.bluetooth.BluetoothUtils;
import me.linkcube.app.core.bluetooth.DeviceConnectionManager;
import me.linkcube.app.service.IToyServiceCall;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ToyServiceCallImpl extends android.os.Binder implements
		IToyServiceCall {

	private final static int Toy_Success = 0;

	private final static int Toy_Lost = 1;

	private final static int Toy_BadCommand = 2;

	// private boolean isConnected = false;

	private BluetoothDevice curDevice = null;

	private BluetoothSocket curSocket = null;

	private byte[] engineData = { 0x25, 0x1, 0x1, 0x0, 0x0, 0x0, 0x0, 0x27 };

	private byte[] modeData = { 0x25, 0x1, 0x0, 0x0, 0x1, 0x0, 0x0, 0x27 };

	private byte[] checkData = { 0x35, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x35 };

	private int ShakeThreshHold[] = { 5, 10, 15, 20, 25, 30, 35, 40, 45, 50 };

	private int KShakeToySpeed[] = { 2, 7, 14, 21, 28, 35, 42, 49 };

	private int KShakeSpeed[][] = { { 2, 3, 6, 11, 16, 22, 26, 28 },
			{ 2, 3, 8, 13, 18, 24, 28, 30 }, { 2, 5, 10, 15, 20, 26, 30, 34 },
			{ 2, 7, 12, 17, 22, 28, 32, 36 },
			{ 2, 9, 14, 19, 24, 30, 34, 38, 42, 44 } };

	// private int WaveThreshHold[] = { 0, 110, 220, 330, 440, 550, 660, 770,
	// 880,
	// 990, 1100, 1220, 1330, 1440, 1550, 1660, 1770, 1880, 1990, 2100 };

	private int WaveThreshHold[] = { 0, 110, 220, 330, 440, 550, 660, 770, 880,
			990, 1100, 1220, 1330, 1440, 1550, 1660, 1770, 1880, 1990, 2100 };

	private int KWaveToySpeed[] = { 2, 5, 10, 15, 20, 26, 30, 34 };

	private int KWaveSpeed[] = { 1, 2, 4, 6, 8, 15, 17, 19, 21, 24, 27, 30, 33,
			36, 39, 42, 45, 47, 49, 50 };

	private int MicThreshHold[] = { 0, 20, 24, 27, 31, 33, 36, 39, 42, 45 };

	private int MicWaveSpeed[] = { 1, 3, 7, 11, 15, 19, 23, 27, 31, 35 };

	public int curSpeed = -1;
	public int curMode = -1;

	private int shakeSensi = 2;
	private int voiceSensi = 2;

	@Override
	public IBinder asBinder() {
		return this;
	}

	@SuppressLint("NewApi")
	private boolean connectDevice() {
		UUID suuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		if (curDevice == null) {
			return false;
		}
		if (curSocket != null) {
			try {
				curSocket.close();
				curSocket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		BluetoothSocket tmp = null;
		try {
			if (Build.VERSION.SDK_INT >= 10) {
				tmp = curDevice
						.createInsecureRfcommSocketToServiceRecord(suuid);
			} else {
				// Method m;
				// m = curDevice.getClass()
				// .getMethod("createRfcommSocket",
				// new Class[] { int.class });
				// tmp = (BluetoothSocket) m.invoke(curDevice, 1);
				tmp = curDevice.createRfcommSocketToServiceRecord(suuid);
			}

		} catch (IOException e) {
			return false;
		}
		// catch (NoSuchMethodException e) {
		// e.printStackTrace();
		// return false;
		// } catch (IllegalAccessException e) {
		// e.printStackTrace();
		// return false;
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// return false;
		// } catch (InvocationTargetException e) {
		// e.printStackTrace();
		// return false;
		// }

		curSocket = tmp;

		try {
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			adapter.cancelDiscovery();
			curSocket.connect();
		} catch (IOException e) {
			try {
				curSocket.close();
				curSocket = null;
			} catch (IOException e2) {
				return false;
			}
			e.printStackTrace();
			return false;
		}
		DeviceConnectionManager.getInstance().setmIsConnected(true, curDevice);
		return true;
	}

	@Override
	public boolean connectToy(String name, String macaddr)
			throws RemoteException {

		Timber.d("connectToy:");
		curDevice = null;

		if (!BluetoothUtils.isBluetoothEnabled()) {
			return false;
		}

		Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter()
				.getBondedDevices();

		if (devices.size() <= 0) {
			return false;
		}

		for (Iterator<BluetoothDevice> iterator = devices.iterator(); iterator
				.hasNext();) {
			BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator.next();
			String deviceName = bluetoothDevice.getName();
			Timber.d("device mac address = %s and name = %s",
					bluetoothDevice.getAddress(), deviceName);
			if (deviceName.contains(name)) {
				if (bluetoothDevice.getAddress().equalsIgnoreCase(macaddr)) {
					curDevice = bluetoothDevice;
					return connectDevice();
				}
			}

		}

		return false;
	}

	private int sendCommand(byte[] data) {
		OutputStream tmpOut = null;
		try {
			tmpOut = curSocket.getOutputStream();

		} catch (IOException e) {
			Timber.e(e, "Bluetooth outputstream sockets not created");
			return Toy_Lost;
		} catch (Exception e) {
			Timber.e(e, "Bluetooth outputstream sockets not created");
			return Toy_Lost;
		}

		try {
			tmpOut.write(data);
		} catch (IOException e) {
			Timber.d("device disconnect on sexpostionmode");
			if (DeviceConnectionManager.getInstance().isSexPositionMode) {
				DeviceConnectionManager.getInstance().setSexPositionMode(false);
				DeviceConnectionManager.getInstance().setmIsConnected(false,
						curDevice);
				DeviceConnectionManager.getInstance().stopTimerTask();
			}
			e.printStackTrace();
			curDevice = null;
			curSocket = null;
			return Toy_Lost;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Toy_Success;
	}

	private int setLocalToySpeed(int speed) {
		Timber.d("speed=%d", speed);
		if (speed > 40 || speed < 1)
			return Toy_BadCommand;

		if (curSocket == null) {
			return Toy_Lost;
		}
		curSpeed = speed;
		byte spd = (byte) speed;
		byte crc = (byte) (0x26 + spd);
		engineData[2] = spd;
		engineData[7] = crc;
		return sendCommand(engineData);
	}

	private int setLocalToyMode(int mode) {
		Log.i("LoacalToyMode:", mode + "");
		if (mode > 8 || mode < 0)
			return Toy_BadCommand;

		if (mode == 8) {
			return setLocalToySpeed(1);
		}
		if (curSocket == null) {
			return Toy_Lost;
		}
		if (mode == 0) {
			setLocalToySpeed(1);
		}

		byte newmode = (byte) mode;
		byte crc = (byte) (0x26 + mode);
		modeData[4] = newmode;
		modeData[7] = crc;
		return sendCommand(modeData);
	}

	@Override
	public int cacheToySpeed(int speed, boolean overtime)
			throws RemoteException {
		Timber.i("Cache Local Speed: %d", speed);
		return setLocalToySpeed(speed);
	}

	@Override
	public int cacheSexPositionMode(int mode) throws RemoteException {
		Timber.i("Cache Local Mode:", "" + mode);
		return setLocalToyMode(mode);
	}

	@Override
	public boolean disconnectToy(String name, String macaddr)
			throws RemoteException {
		if (curDevice == null || curSocket == null) {
			return false;
		}
		try {
			curSocket.close();
			curSocket = null;
		} catch (IOException e2) {
			return false;
		}
		DeviceConnectionManager.getInstance().setmIsConnected(false, curDevice);
		return true;
	}

	@Override
	public int cacheShake(long shkspd, boolean overtime) throws RemoteException {
		int spd;
		for (spd = 0; (spd < ShakeThreshHold.length)
				&& (shkspd >= ShakeThreshHold[spd]); spd++) {
		}
		cacheToySpeed(KShakeSpeed[shakeSensi][spd - 1], overtime);
		return 0;
	}

	@Override
	public int setWave(long waveng) throws RemoteException {
		int spd;
		for (spd = 0; (spd < WaveThreshHold.length)
				&& (waveng >= WaveThreshHold[spd]); spd++) {

		}
		cacheToySpeed(KWaveSpeed[spd - 1], false);
		return 0;
	}

	@Override
	public int setMicWave(int sound) throws RemoteException {
		int spd;
		for (spd = 0; (spd < MicThreshHold.length)
				&& (sound >= MicThreshHold[spd]); spd++) {

		}
		cacheToySpeed(MicWaveSpeed[spd - 1], false);
		return 0;
	}

	@Override
	public boolean setWaveMode(int index, int val) throws RemoteException {
		if (index < WaveThreshHold.length) {
			ShakeThreshHold[index] = val;
		}
		return false;
	}

	@Override
	public boolean setShakeMode(int index, int val) throws RemoteException {
		if (index < KWaveToySpeed.length) {
			KShakeToySpeed[index] = val;
		}
		return false;
	}

	@Override
	public void setShakeSensitivity(int v) throws RemoteException {
		shakeSensi = v;
	}

	@Override
	public void setVoiceSensitivity(int v) throws RemoteException {
		voiceSensi = v;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean checkData() {
		if (curDevice == null || curSocket == null) {
			return false;
		}

		OutputStream tmpOut = null;
		try {
			tmpOut = curSocket.getOutputStream();
		} catch (IOException e) {
			Timber.e(e, "sockets not created");
			return false;
		}

		try {
			tmpOut.write(checkData);
		} catch (IOException e) {
			e.printStackTrace();
			Timber.i("Toy is disconnected.");
			return false;
		}

		Timber.i("Toy is connected.");

		return true;
	}

	@Override
	public boolean closeToy() throws RemoteException {
		if (setLocalToySpeed(1) == Toy_Success) {
			return true;
		}
		return false;
	}

}
