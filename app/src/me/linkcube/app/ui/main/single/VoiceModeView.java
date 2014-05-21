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

public class VoiceModeView extends RelativeLayout{

	private Button modeBtn;

	private int level = 0;

	private ModeSelectedListener mListener;
	
	private ResetViewReceiver resetViewReceiver;

	public VoiceModeView(Context context) {
		super(context);
		init(context);
	}

	public VoiceModeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.voice_mode_view, this, true);
		modeBtn = (Button) view.findViewById(R.id.voice_mode_btn);
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
			/*case 0:
				level++;
				modeBtn.setBackgroundResource(R.drawable.voice_mode_1);
				break;
			case 1:
				level++;
				modeBtn.setBackgroundResource(R.drawable.voice_mode_2);
				break;
			case 2:
				level++;
				modeBtn.setBackgroundResource(R.drawable.voice_mode_3);
				break;
			case 3:
				level++;
				modeBtn.setBackgroundResource(R.drawable.voice_mode_4);
				break;
			case 4:
				level = 0;
				modeBtn.setBackgroundResource(R.drawable.voice_mode_0);
				break;*/
			//音乐模式更换成二档位
			case 0:
				level=2;
				modeBtn.setBackgroundResource(R.drawable.voice_mode_4);
				break;
			case 2:
				level = 0;
				modeBtn.setBackgroundResource(R.drawable.voice_mode_0);
				break;
			default:
				break;
			}
			mListener.onVoiceMode(level);
		}
	};
	
	private void onresetViewReceiver(Context context){
		resetViewReceiver=new ResetViewReceiver();
		resetViewReceiver.setResetVoiceViewCallBack(new ASmackRequestCallBack() {
			
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
		filter.addAction("com.linkcube.resetvoicemodeview");
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		context.registerReceiver(resetViewReceiver, filter);
	}
	
	private Handler resetViewHandler=new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			level = 0;
			modeBtn.setBackgroundResource(R.drawable.voice_mode_0);
		}
		
	};
	
	public void unRegisterReceiver(Context context){
		context.unregisterReceiver(resetViewReceiver);
	}
}