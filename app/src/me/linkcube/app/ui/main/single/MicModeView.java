package me.linkcube.app.ui.main.single;

import com.umeng.analytics.MobclickAgent;

import me.linkcube.app.R;
import me.linkcube.app.core.Const.UmengEvent;
import me.linkcube.app.core.bluetooth.DeviceConnectionManager;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.widget.MicSoundView;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MicModeView extends RelativeLayout {
	
	private Context context;
	
	private Button modeBtn;
	
	private LinearLayout micSoundLlayout;
	
	private MicSoundView micSoundView;

	private int level = 0;

	private ModeSelectedListener mListener;

	private ResetViewReceiver resetViewReceiver;

	public MicModeView(Context context) {
		super(context);
		this.context=context;
		init();
	}

	public MicModeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		init();
	}

	private void init() {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.mic_mode_view, this, true);
		micSoundLlayout=(LinearLayout)view.findViewById(R.id.mic_sound_llayout);
		micSoundView=new MicSoundView(context);
		micSoundLlayout.addView(micSoundView);
		modeBtn = (Button) view.findViewById(R.id.mic_mode_btn);
		modeBtn.setOnClickListener(onClickListener);
		onresetViewReceiver(context);
	}
	
	public void setOnModeSelectedListener(ModeSelectedListener listener) {
		mListener = listener;
	}
	
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			MobclickAgent.onEvent(context,UmengEvent.MIC_MODE_EVENT);
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
			// 音乐模式更换成二档位
			case 0:
				level = 2;
				modeBtn.setBackgroundResource(R.drawable.mic_mode_1);
				mListener.onMicMode(level);
				break;
			case 2:
				level = 0;
				modeBtn.setBackgroundResource(R.drawable.mic_mode_0);
				mListener.offMicMode(level);
				break;
			default:
				break;
			}

		}
	};

	private void onresetViewReceiver(Context context) {
		resetViewReceiver = new ResetViewReceiver();
		resetViewReceiver
				.setResetMicViewCallBack(new ASmackRequestCallBack() {

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
		filter.addAction("com.linkcube.resetmicmodeview");
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		context.registerReceiver(resetViewReceiver, filter);
	}

	private Handler resetViewHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			level = 0;
			modeBtn.setBackgroundResource(R.drawable.mic_mode_0);
		}

	};

	public void unRegisterReceiver(Context context) {
		context.unregisterReceiver(resetViewReceiver);
	}

	public MicSoundView getMicSoundView() {
		return micSoundView;
	}
	
}
