package com.oplibs.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;


/*
 * 等待处理事件
 * 
 * 
 */

public class WaitingHandler extends Handler{
	 private static final int MESSAGETYPE_FINISH = 0x0001;
     private ProgressDialog progressDialog = null;
     
     public void StartProgressDialog(Activity activity,String title, String detail)
     {
    	 progressDialog = ProgressDialog.show(activity, title, detail);  
    	 progressDialog.setCanceledOnTouchOutside(false);
     }
     
     public void StopProgressDialog()
     {
    	 Message msg_listData = new Message();
         msg_listData.what = MESSAGETYPE_FINISH;
         sendMessage(msg_listData);
     }
     
	  @Override
	public void handleMessage(Message message)
	  {
		  switch (message.what)
		  {
		  case MESSAGETYPE_FINISH:                                        
        	  //刷新UI，显示数据，并关闭进度条                        
        	  progressDialog.dismiss(); //关闭进度条
              break;
          }
	  }
	  
	  
}
