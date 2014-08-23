package me.linkcube.app.core.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import me.linkcube.app.core.Timber;
import me.linkcube.app.sync.core.ASmackRequestCallBack;


import android.os.Handler;
import android.os.Message;

import me.linkcube.app.core.Const.AppUpdate;

public class UpdateHttpGet {
	
	private ASmackRequestCallBack aSmackRequestCallBack;
	
	public UpdateHttpGet(ASmackRequestCallBack aSmackRequestCallBack){
		this.aSmackRequestCallBack=aSmackRequestCallBack;
		getUpdateInfo();
	}
	
	public void getUpdateInfo(){
		final Handler handler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				String result=(String)msg.obj;
				Timber.d("json:"+result);
				if(result==null){
					aSmackRequestCallBack.responseFailure(-1);
				}else{
					aSmackRequestCallBack.responseSuccess(result);
				}
				
			}
			
		};
		
		Thread thread=new Thread(){

			@Override
			public void run() {
				String result=executeHttpGet();
				Message msg=new Message();
				msg.obj=result;
				handler.sendMessage(msg);
			}
			
		};
		thread.start();
		
	}

	private String executeHttpGet() {
		String result = null;
		URL url = null;
		HttpURLConnection connection = null;
		InputStreamReader in = null;
		try {
			url = new URL(AppUpdate.CHECK_VERSION_URL);
			connection = (HttpURLConnection) url.openConnection();
			in = new InputStreamReader(connection.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(in);
			StringBuffer strBuffer = new StringBuffer();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				strBuffer.append(line);
			}
			result = strBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		
		return result;
	}
}
