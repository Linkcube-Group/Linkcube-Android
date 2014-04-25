package me.linkcube.app.ui.main.single;

import java.util.ArrayList;
import java.util.List;

import me.linkcube.app.core.Timber;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ResetViewReceiver extends BroadcastReceiver {

	private ASmackRequestCallBack resetShakeViewCallBack;
	
	private ASmackRequestCallBack resetVoiceViewCallBack;
	
	private ASmackRequestCallBack resetPositionViewCallBack;
	
	private List<ASmackRequestCallBack> viewResetCallBacks=new ArrayList<ASmackRequestCallBack>();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action=intent.getAction();
		if (action.equals("com.linkcube.resetvoicemodeview")) {
			resetVoiceViewCallBack.responseSuccess(0);
		}else if(action.equals("com.linkcube.resetshakemodeview")){
			resetShakeViewCallBack.responseSuccess(0);
		}else if(action.equals("com.linkcube.resetsexpositionmodeview")){
			resetPositionViewCallBack.responseSuccess(0);
		}else if(action.equals("com.linkcube.resetview")){
			Timber.d("viewResetCallBacks:"+viewResetCallBacks.size());
			for (ASmackRequestCallBack viewResetCallBack : viewResetCallBacks) {
				viewResetCallBack.responseSuccess(0);
			}
		}
		
	}

	public void setResetShakeViewCallBack(
			ASmackRequestCallBack resetShakeViewCallBack) {
		this.resetShakeViewCallBack = resetShakeViewCallBack;
		viewResetCallBacks.add(resetShakeViewCallBack);
	}


	public void setResetVoiceViewCallBack(
			ASmackRequestCallBack resetVoiceViewCallBack) {
		this.resetVoiceViewCallBack = resetVoiceViewCallBack;
		viewResetCallBacks.add(resetVoiceViewCallBack);
	}

	public void setResetPositionViewCallBack(
			ASmackRequestCallBack resetPositionViewCallBack) {
		this.resetPositionViewCallBack = resetPositionViewCallBack;
		viewResetCallBacks.add(resetPositionViewCallBack);
	}

}
