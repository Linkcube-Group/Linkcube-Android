package me.linkcube.app.sync.core;

import me.linkcube.app.core.Timber;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

public class GetMessageReceiver extends BroadcastReceiver {

	public ASmackRequestCallBack getMsgCallBack;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Message msg = new Message();
		msg.what = 0;
		msg.setData(bundle);
		String from = msg.getData().getString("from");
		String body = msg.getData().getString("body");
		//Timber.d("GetMessageReceiver"+from + "---" + body);
		if (body != null) {
			getMsgCallBack.responseSuccess(msg);
		} else {
			getMsgCallBack.responseFailure(-1);
		}

	}

	public void setiCallBack(ASmackRequestCallBack iCallBack) {
		this.getMsgCallBack = iCallBack;
	}

}
