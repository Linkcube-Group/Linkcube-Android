package me.linkcube.app.ui.bluetooth;

import com.umeng.analytics.MobclickAgent;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import me.linkcube.app.LinkcubeApplication;
import me.linkcube.app.R;
import me.linkcube.app.common.ui.BaseListAdapter;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.bluetooth.DeviceConnectionManager;
import me.linkcube.app.core.game.ToyConnectTimeManager;

/**
 * 蓝牙搜索列表适配器
 * 
 * @author Ervin
 * 
 */
public class BluetoothDeviceAdapter extends BaseListAdapter<BluetoothDevice> {

	private Context mContext;

	public BluetoothDeviceAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BluetoothDeviceListCell cell;
		if (convertView == null) {
			cell = new BluetoothDeviceListCell(mContext);
		} else {
			cell = (BluetoothDeviceListCell) convertView;
		}
		BluetoothDevice device = getItem(position);
		String name = device.getName();
		cell.setDeviceName(name);

		if (device.equals(DeviceConnectionManager.getInstance()
				.getDeviceConnected())) {
			if (DeviceConnectionManager.getInstance().isConnected()) {
				ToyConnectTimeManager.getInstance().startTimeStatistics();
				TelephonyManager telephonyManager= (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
				String imei=telephonyManager.getDeviceId();
				MobclickAgent.onEventBegin(mContext, Const.UmengEvent.CONNECT_TOY_DURATION,imei);
				cell.setDeviceState(R.string.connected);
			} else {
				if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
					cell.setDeviceState(R.string.bonded);
				} else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
					cell.setDeviceState(R.string.unbond);
				} else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
					cell.setDeviceState(R.string.bonding);
				}
			}

		} else {
			if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
				cell.setDeviceState(R.string.bonded);
			} else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
				cell.setDeviceState(R.string.unbond);
			} else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
				cell.setDeviceState(R.string.bonding);
			}
		}

		return cell;
	}

}
