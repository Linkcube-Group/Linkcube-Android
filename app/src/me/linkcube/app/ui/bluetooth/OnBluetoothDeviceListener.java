package me.linkcube.app.ui.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * 搜索蓝牙设备的回调接口
 * 
 * @author orange
 * 
 */
public interface OnBluetoothDeviceListener {

	/**
	 * 发现一个设备的回调接口
	 */
	void onBluetoothDeviceDiscoveryOne(BluetoothDevice device);

	/**
	 * 查找蓝牙设备完毕的回调接口
	 */
	void onBluetoothDeviceDiscoveryFinished();

	/**
	 * 蓝牙正在开启
	 */
	void onBluetoothStateTuringOn();

	/**
	 * 蓝牙正在关闭
	 */
	void onBluetoothStateTuringOff();

	/**
	 * 蓝牙已经打开
	 */
	void onBluetoothStateOn();

	/**
	 * 蓝牙已经关闭
	 */
	void onBluetoothStateOff();

	/**
	 * 绑定设备成功
	 */
	void onBluetoothStateBonded();

	/**
	 * 没有绑定设备
	 */
	void onBluetoothStateBondNone();

	/**
	 * 正在绑定
	 */
	void onBluetoothStateBonding();

}
