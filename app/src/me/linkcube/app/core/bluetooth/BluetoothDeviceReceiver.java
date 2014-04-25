package me.linkcube.app.core.bluetooth;

import static android.bluetooth.BluetoothAdapter.EXTRA_STATE;
import me.linkcube.app.core.Timber;
import me.linkcube.app.ui.bluetooth.OnBluetoothDeviceListener;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 搜索蓝牙设备广播接收器
 * 
 * @author orange
 * 
 */
public class BluetoothDeviceReceiver extends BroadcastReceiver {

	private OnBluetoothDeviceListener mListener;

	public BluetoothDeviceReceiver(OnBluetoothDeviceListener listener) {
		mListener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Timber.d("action:" + action);
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			Timber.d("onReceive:discover a device");
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (BluetoothUtils.isLinkCubeDevice(device)) {
				Timber.d("onReceive:discover a linkcube device and the bond state is "
						+ device.getBondState());
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					Timber.d("onReceive:discover a linkcube device and the device is not bonded");
					mListener.onBluetoothDeviceDiscoveryOne(device);
				}
			}

		}
		if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			Timber.d("onReceive:finish discovery");
			mListener.onBluetoothDeviceDiscoveryFinished();
		}
		if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
			Timber.d("onReceive:bluetooth bond state changed");
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (BluetoothUtils.isLinkCubeDevice(device)) {
				int connectState = device.getBondState();
				switch (connectState) {
				case BluetoothDevice.BOND_NONE:
					break;
				case BluetoothDevice.BOND_BONDING:
					break;
				case BluetoothDevice.BOND_BONDED:
					break;
				}
			}
		}
		if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
			int state = intent.getIntExtra(EXTRA_STATE, -1);
			Timber.d("Extra_state " + state);
			switch (state) {
			case BluetoothAdapter.STATE_TURNING_ON:
				mListener.onBluetoothStateTuringOn();
				break;
			case BluetoothAdapter.STATE_ON:
				mListener.onBluetoothStateOn();
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				mListener.onBluetoothStateTuringOff();
				break;
			case BluetoothAdapter.STATE_OFF:
				mListener.onBluetoothStateOff();
				break;
			default:
				break;
			}
		}

	}

}
