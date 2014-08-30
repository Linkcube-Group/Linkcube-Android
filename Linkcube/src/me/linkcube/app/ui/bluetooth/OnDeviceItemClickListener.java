package me.linkcube.app.ui.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * 列表中设备被点击后的回调函数
 * 
 * @author Ervin
 * 
 */
public interface OnDeviceItemClickListener {

	void onBondDeviceClick(BluetoothDevice device, int position);

	void onConnectDeviceClick(BluetoothDevice device, int position);

}
