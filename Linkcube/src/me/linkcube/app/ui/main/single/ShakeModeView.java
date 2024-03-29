package me.linkcube.app.ui.main.single;

import com.umeng.analytics.MobclickAgent;

import me.linkcube.app.R;
import me.linkcube.app.core.bluetooth.DeviceConnectionManager;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import me.linkcube.app.core.Const.UmengEvent;

public class ShakeModeView extends RelativeLayout{
	
	private Context context;

	private Button modeBtn;

	private int level = 0;

	private ModeSelectedListener mListener;
	
	private  ResetViewReceiver resetViewReceiver;

	public ShakeModeView(Context context) {
		super(context);
		this.context=context;
		init();
	}

	public ShakeModeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		init();
	}

	private void init() {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.shake_mode_view, this, true);
		modeBtn = (Button) view.findViewById(R.id.shake_mode_btn);
		modeBtn.setOnClickListener(onClickListener);
		
		onresetViewReceiver(context);
	}

	public void setOnModeSelectedListener(ModeSelectedListener listener) {
		mListener = listener;
	}

	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			DeviceConnectionManager.getInstance().setSexPositionMode(false);
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
				level=4;
				MobclickAgent.onEvent(context,UmengEvent.SHACK_MODE_EVENT);
				modeBtn.setBackgroundResource(R.drawable.shake_mode_4);
				mListener.onShakeMode(level);
				break;
			case 4:
				level = 0;
				modeBtn.setBackgroundResource(R.drawable.shake_mode_0);
				mListener.offShakeMode(level);
				break;
			default:
				break;
			}
			
		}
	};
	
	private void onresetViewReceiver(Context context){
		resetViewReceiver=new ResetViewReceiver();
		resetViewReceiver.setResetShakeViewCallBack(new ASmackRequestCallBack() {
			
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
		filter.addAction("com.linkcube.resetshakemodeview");
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		context.registerReceiver(resetViewReceiver, filter);
	}
	
	private Handler resetViewHandler=new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			level = 0;
			modeBtn.setBackgroundResource(R.drawable.shake_mode_0);
		}
		
	};
	
	public void unRegisterReceiver(Context context){
		context.unregisterReceiver(resetViewReceiver);
	}

}
