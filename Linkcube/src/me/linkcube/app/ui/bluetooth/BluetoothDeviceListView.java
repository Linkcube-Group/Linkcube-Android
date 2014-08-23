package me.linkcube.app.ui.bluetooth;

import me.linkcube.app.R;
import me.linkcube.app.widget.ViewUtils;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 蓝牙搜索列表，并带有辅助提示功能
 * 
 * @author orange
 * 
 */

public class BluetoothDeviceListView extends RelativeLayout {

	private TextView mTipTv;

	private ListView mDeviceLv;

	private OnDeviceItemClickListener onItemClickListener;

	public BluetoothDeviceListView(Context context) {
		super(context);
		init(context);
	}

	public BluetoothDeviceListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.bluetooth_device_listview, this,
				true);
		mTipTv = (TextView) view.findViewById(R.id.tip_tv);
		mDeviceLv = (ListView) view.findViewById(android.R.id.list);
		//View header = mInflater.inflate(R.layout.bluetooth_device_listview_header, null);
		//mDeviceLv.addHeaderView(header);
		mDeviceLv.setOnItemClickListener(new OnItemClickListener());
	}

	private class OnItemClickListener implements
			AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Object object = parent.getAdapter().getItem(position);
			if (object instanceof BluetoothDevice) {
				if (onItemClickListener == null) {
					return;
				}
				BluetoothDevice device = (BluetoothDevice) object;
				if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
					onItemClickListener.onConnectDeviceClick(device,
							position);
				} else {
					onItemClickListener.onBondDeviceClick(device,
							position);
				}
			}
		}

	}

	public void setAdapter(ListAdapter adapter) {
		mDeviceLv.setAdapter(adapter);
	}

	public void showTipTextView() {
		ViewUtils.setGone(mTipTv, false);
		ViewUtils.setGone(mDeviceLv, true);
	}

	public void showDeviceListView() {
		ViewUtils.setGone(mDeviceLv, false);
		ViewUtils.setGone(mTipTv, true);
	}

	public void setTip(int resId) {
		mTipTv.setText(resId);
	}

	public void setTip(String tip) {
		mTipTv.setText(tip);
	}

	public void setOnDeviceItemClickListener(OnDeviceItemClickListener listener) {
		this.onItemClickListener = listener;
	}

}
