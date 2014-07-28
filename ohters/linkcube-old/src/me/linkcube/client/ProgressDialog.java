package me.linkcube.client;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*
 * 
 * 
 * 进度对话框
 */

public class ProgressDialog extends Dialog {
    private Context context = null;
    private static ProgressDialog customProgressDialog = null;
    
    private TextView tvMsg;
    private Button btnAction;
    private int typ=0;
    
    private static android.view.View.OnClickListener actionClick = new android.view.View.OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			customProgressDialog.dismiss();
			return;
		}
	};
    
    public ProgressDialog(Context context){
        super(context);
        this.context = context;
    }
    public ProgressDialog(Context context, int theme) {
        super(context, theme);
    }
    public static ProgressDialog createDialog(Context context){
    	ProgressDialog dlg = new ProgressDialog(context,R.style.CustomDialog);
    	dlg.setContentView(R.layout.dialog_progress);
    	dlg.getWindow().getAttributes().gravity = Gravity.CENTER;
    	dlg.getWindow().setBackgroundDrawable(new BitmapDrawable());
    	dlg.setCanceledOnTouchOutside(false);
        
    	dlg.tvMsg = (TextView)dlg.findViewById(R.id.tv_msg);
    	dlg.btnAction = (Button)dlg.findViewById(R.id.btn_action);
    	dlg.btnAction.setOnClickListener(actionClick);
        
        return dlg;
    }
    @Override
	public void onWindowFocusChanged(boolean hasFocus){
        if (customProgressDialog == null){
            return;
        }
    }
    private ProgressDialog setBtnTitle(String strTitle){
    	if(strTitle!=null)
    	{
    		customProgressDialog.btnAction.setVisibility(View.VISIBLE);
    		customProgressDialog.btnAction.setText(strTitle);
    	}
    	else
    	{
    		customProgressDialog.btnAction.setVisibility(View.INVISIBLE);
    	}
        return customProgressDialog;
    }
    private ProgressDialog setMessage(String strMessage){
    	customProgressDialog.tvMsg.setText(strMessage);
        return customProgressDialog;
    }
    
	public static void startProgressDialog(Context ctx, String info,String title)
	{
		if (customProgressDialog == null){
			customProgressDialog = ProgressDialog.createDialog(ctx);
		}
		customProgressDialog.setBtnTitle(title);
		customProgressDialog.setMessage(info);
		customProgressDialog.show();
	}
	public static void stopProgressDialog(){
        if (customProgressDialog != null){
        	customProgressDialog.dismiss();
        	customProgressDialog = null;
        }
    }
}
