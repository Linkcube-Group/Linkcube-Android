package me.linkcube.app.ui.main.single;

import me.linkcube.app.LinkcubeApplication;
import me.linkcube.app.R;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.bluetooth.DeviceConnectionManager;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * 七星图模式自定义控件视图
 * 
 * @author orange
 * 
 */
public class SexPositionModeView extends RelativeLayout {

	private Button modeBtn;

	private int level = 0;

	private ModeSelectedListener mListener;
	
	private ResetViewReceiver resetViewReceiver;

	public SexPositionModeView(Context context) {
		super(context);
		init(context);
	}

	public SexPositionModeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.sex_position_mode_view, this,
				true);
		modeBtn = (Button) view.findViewById(R.id.sex_position_mode_btn);
		modeBtn.setOnClickListener(onClickListener);
		onresetViewReceiver(context);
	}

	public void setOnModeSelectedListener(ModeSelectedListener listener) {
		mListener = listener;
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			DeviceConnectionManager.getInstance().setSexPositionMode(true);

			try {
				if (!DeviceConnectionManager.getInstance().isConnected()) {
					mListener.showConnectBluetoothTip();
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			switch (level) {
			case 0:
				level++;
				modeBtn.setBackgroundResource(R.drawable.sex_position_mode_1);
				break;
			case 1:
				level++;
				modeBtn.setBackgroundResource(R.drawable.sex_position_mode_2);
				break;
			case 2:
				level++;
				modeBtn.setBackgroundResource(R.drawable.sex_position_mode_3);
				break;
			case 3:
				level++;
				modeBtn.setBackgroundResource(R.drawable.sex_position_mode_4);
				break;
			case 4:
				level++;
				modeBtn.setBackgroundResource(R.drawable.sex_position_mode_5);
				break;
			case 5:
				level++;
				modeBtn.setBackgroundResource(R.drawable.sex_position_mode_6);
				break;
			case 6:
				level++;
				modeBtn.setBackgroundResource(R.drawable.sex_position_mode_7);
				break;
			case 7:
				level = 0;
				modeBtn.setBackgroundResource(R.drawable.sex_position_mode_0);
				break;

			default:
				break;
			}
			mListener.onSexPositionMode(level);
		}
	};
	
	private void onresetViewReceiver(Context context){
		resetViewReceiver=new ResetViewReceiver();
		resetViewReceiver.setResetPositionViewCallBack(new ASmackRequestCallBack() {
			
			@Override
			public void responseSuccess(Object object) {
				resetViewHandler.sendEmptyMessage(0);
			}
			
			@Override
			public void responseFailure(int reflag) {
				
			}
		});
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.linkcube.resetview");
		filter.addAction("com.linkcube.resetsexpositionmodeview");
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		context.registerReceiver(resetViewReceiver, filter);
	}
	
	private Handler resetViewHandler=new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			level = 0;
			modeBtn.setBackgroundResource(R.drawable.sex_position_mode_0);
		}
		
	};
	
	public void unRegisterReceiver(Context context){
		context.unregisterReceiver(resetViewReceiver);
	}

}
