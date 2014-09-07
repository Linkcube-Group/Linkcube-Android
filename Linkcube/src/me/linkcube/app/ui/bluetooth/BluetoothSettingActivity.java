package me.linkcube.app.ui.bluetooth;

import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import me.linkcube.app.LinkcubeApplication;
import me.linkcube.app.R;
import me.linkcube.app.common.ui.DialogActivity;
import me.linkcube.app.common.util.AlertUtils;
import me.linkcube.app.common.util.PreferenceUtils;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.bluetooth.BluetoothDeviceReceiver;
import me.linkcube.app.core.bluetooth.BluetoothUtils;
import me.linkcube.app.core.bluetooth.DeviceConnectionManager;
import static me.linkcube.app.core.Const.Device.*;

public class BluetoothSettingActivity extends DialogActivity implements
		OnClickListener, OnDeviceItemClickListener, OnBluetoothDeviceListener {

	private ToggleButton bluetoothTb;

	private Button discoverDevicesBtn, enterMallBtn,bluetoothHelpBtn;

	private BluetoothDeviceListView deviceLv;

	private List<BluetoothDevice> deviceList;

	private BluetoothDeviceAdapter deviceAdapter;

	private BluetoothDeviceReceiver deviceDiscoveryReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_setting_activity);
		configureActionBar(R.string.bluetooth);
		deviceDiscoveryReceiver = new BluetoothDeviceReceiver(this);
		deviceList = BluetoothUtils.getBondedDevices();
		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		bluetoothTb.setChecked(BluetoothUtils.isBluetoothEnabled());
		bluetoothTb.setOnCheckedChangeListener(switchListener);
		BluetoothUtils
				.regiserDeviceReceiver(mActivity, deviceDiscoveryReceiver);
		deviceAdapter = new BluetoothDeviceAdapter(mActivity);
		deviceAdapter.setList(deviceList);
		deviceLv.setAdapter(deviceAdapter);

	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BluetoothUtils.unregisterDeviceReceiver(mActivity,
				deviceDiscoveryReceiver);
		deviceList.clear();
	}

	private void initViews() {
		bluetoothTb = (ToggleButton) findViewById(R.id.bluetooth_tb);
		deviceLv = (BluetoothDeviceListView) findViewById(R.id.device_lv);
		deviceLv.setOnDeviceItemClickListener(this);
		discoverDevicesBtn = (Button) findViewById(R.id.discover_devices_btn);
		discoverDevicesBtn.setOnClickListener(this);
		if (BluetoothUtils.isBluetoothEnabled()) {
			showBondedDevices();
		}
		bluetoothHelpBtn = (Button) findViewById(R.id.bluetooth_help_btn);
		bluetoothHelpBtn.setOnClickListener(this);
	}

	private void showBondedDevices() {
		if (BluetoothUtils.getBondedDevices() != null) {
			deviceLv.showDeviceListView();
		}
	}

	private void clearDeviceList() {
		deviceLv.showTipTextView();
		deviceLv.setTip(R.string.switch_on_bluetooth_to_search_toy);
		deviceList.clear();
		deviceAdapter.notifyDataSetChanged();
	}

	private void startDiscoverBluetoothDevices() {
		BluetoothAdapter madapter = BluetoothAdapter.getDefaultAdapter();
		madapter.startDiscovery();
		discoverDevicesBtn.setText(R.string.searching);
	}

	private void finishDiscoverBluetoothDevices() {
		discoverDevicesBtn.setText(R.string.search_toy);
	}

	private void bondDevice(BluetoothDevice device, int position) {
		if (BluetoothUtils.bondDevice(device)) {
			showProgressDialog(getResources().getString(R.string.dialog_bonding_bluetooth));
		} else {
			Timber.d("绑定拉玩具失败");
			AlertUtils.showToast(mActivity, getResources().getString(R.string.toast_toy_unbonded));
		}
	}

	private OnCheckedChangeListener switchListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			BluetoothUtils.setBluetoothEnabled(isChecked);
			if (!isChecked) {
				discoverDevicesBtn.setText(R.string.search_toy);
			} else {
				showBondedDevices();
			}
		}
	};

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.discover_devices_btn:
			if (BluetoothUtils.isBluetoothEnabled()) {
				startDiscoverBluetoothDevices();
			} else {
				AlertUtils.showToast(mActivity, getResources().getString(R.string.toast_pls_open_bluetooth));
			}
			break;
		case R.id.bluetooth_help_btn:
			startActivity(new Intent(BluetoothSettingActivity.this,BluetoothHelpActivity.class));
		
		default:
			break;
		}

	}

	@Override
	public void onBluetoothStateTuringOn() {
		Timber.i("正在打开蓝牙");
		bluetoothTb.setClickable(false);
		deviceLv.setTip(R.string.switching_on_bluetooth);
		AlertUtils.showToast(this, getResources().getString(R.string.toast_open_bluetooth_wait));

	}

	@Override
	public void onBluetoothStateTuringOff() {
		Timber.i("正在关闭蓝牙");
		bluetoothTb.setClickable(false);
		deviceLv.setTip(R.string.switching_off_bluetooth);
		AlertUtils.showToast(this, getResources().getString(R.string.toast_close_bluetooth_wait));
	}

	@Override
	public void onBluetoothStateOn() {
		Timber.i("蓝牙已打开");
		bluetoothTb.setClickable(true);
		AlertUtils.showToast(this, getResources().getString(R.string.toast_bluetooth_open));
		showBondedDevices();
		startDiscoverBluetoothDevices();
	}

	@Override
	public void onBluetoothStateOff() {
		Timber.i("蓝牙已关闭");
		bluetoothTb.setClickable(true);
		AlertUtils.showToast(this, getResources().getString(R.string.toast_bluetooth_closed));
		clearDeviceList();
	}

	@Override
	public void onBluetoothDeviceDiscoveryOne(BluetoothDevice device) {
		deviceLv.showDeviceListView();
		Timber.d("发现一个设备:" + device.getName());
		filterDevices(device);
		if (deviceList.size() > 0) {
			deviceAdapter.notifyDataSetChanged();
			deviceLv.showDeviceListView();
		} else {
			deviceLv.showTipTextView();
		}

	}

	@Override
	public void onBluetoothDeviceDiscoveryFinished() {
		Timber.d("搜索蓝牙设备完毕！");
		finishDiscoverBluetoothDevices();
		AlertUtils.showToast(this, getResources().getString(R.string.toast_searching_toy_over));
		if (deviceList.size() == 0) {
			deviceLv.setTip(getResources().getString(R.string.toast_no_device_nearby));
			deviceLv.showTipTextView();
		}
	}

	@Override
	public void onBondDeviceClick(BluetoothDevice device, int position) {
		bondDevice(device, position);
	}

	@Override
	public void onConnectDeviceClick(BluetoothDevice device, int position) {
		new ConnectDeviceAsyncTask(device, position).execute();
	}

	private class ConnectDeviceAsyncTask extends
			AsyncTask<BluetoothDevice, Void, Boolean> {

		private BluetoothDevice mDevice;

		public ConnectDeviceAsyncTask(BluetoothDevice device, int position) {
			this.mDevice = device;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Timber.d("准备连接设备");
			showProgressDialog(getResources().getString(R.string.toast_connecting_toy));
		}

		@Override
		protected Boolean doInBackground(BluetoothDevice... params) {
			Timber.d("正在连接设备");
			boolean success = false;
			try {
				success = LinkcubeApplication.toyServiceCall.connectToy(
						mDevice.getName(), mDevice.getAddress());
			} catch (RemoteException e) {
				success = false;
				e.printStackTrace();
			}
			return success;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			Timber.d("连接设备完毕");
			dismissProgressDialog();
			if (success) {
				DeviceConnectionManager.getInstance().startCheckConnetionTask();
				AlertUtils.showToast(mActivity, getResources().getString(R.string.toast_connect_toy_success));
				// TODO 保存连接上的设备名和状态
				PreferenceUtils.setString(DEVICE_NAME, mDevice.getName());
				PreferenceUtils.setString(DEVICE_ADDRESS, mDevice.getAddress());
				deviceAdapter.notifyDataSetChanged();
			} else {
				AlertUtils.showToast(mActivity, getResources().getString(R.string.toast_connect_toy_failure));
			}

		}

	}

	private List<BluetoothDevice> filterDevices(BluetoothDevice device) {
		for (int i = 0; i < deviceList.size(); i++) {
			if (deviceList.get(i).getAddress().equals(device.getAddress())) {
				return deviceList;
			}
		}
		deviceList.add(device);
		return deviceList;
	}

	@Override
	public void onBluetoothStateBonded() {
		Timber.d("onReceive:bluetooth bond state changed -> " + "BONDED");
		Timber.d("玩具配对成功");
		dismissProgressDialog();
		AlertUtils.showToast(mActivity, getResources().getString(R.string.toast_bond_toy_success));
		deviceAdapter.notifyDataSetChanged();
	}

	@Override
	public void onBluetoothStateBondNone() {
		Timber.d("onReceive:bluetooth bond state changed -> " + "BOND_NONE");
		deviceAdapter.notifyDataSetChanged();
	}

	@Override
	public void onBluetoothStateBonding() {
		Timber.d("onReceive:bluetooth bond state changed -> " + "BONDING");
		Timber.d("正在绑定玩具");
	}

}
