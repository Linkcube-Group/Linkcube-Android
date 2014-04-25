package me.linkcube.app.ui.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import me.linkcube.app.R;
import me.linkcube.app.ui.BaseListAdapter;

/**
 * 蓝牙搜索列表适配器
 * 
 * @author orange
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
		if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
			cell.setDeviceState(R.string.bonded);
		} else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
			cell.setDeviceState(R.string.unbond);
		} else {
			cell.setDeviceState(R.string.connected);
		}
		return cell;
	}

}
