package me.linkcube.client;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*
 * 确定对话框
 * 
 * 
 */

public class ConfirmationDialog extends Dialog {
    private Context context = null;
    private static ConfirmationDialog customConfirmDialog = null;
    
    private TextView tvMsg;
    private Button btnAction;
    private int typ=0;
    
    private static android.view.View.OnClickListener actionClick = new android.view.View.OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			customConfirmDialog.dismiss();
			return;
		}
	};
    
    public ConfirmationDialog(Context context){
        super(context);
        this.context = context;
    }
    public ConfirmationDialog(Context context, int theme) {
        super(context, theme);
    }
    public static ConfirmationDialog createDialog(Context context){
    	ConfirmationDialog dlg = new ConfirmationDialog(context,R.style.CustomDialog);
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
        if (customConfirmDialog == null){
            return;
        }
    }
    private ConfirmationDialog setBtnTitle(String strTitle){
    	if(strTitle!=null)
    	{
    		customConfirmDialog.btnAction.setVisibility(View.VISIBLE);
    		customConfirmDialog.btnAction.setText(strTitle);
    	}
    	else
    	{
    		customConfirmDialog.btnAction.setVisibility(View.INVISIBLE);
    	}
        return customConfirmDialog;
    }
    private ConfirmationDialog setMessage(String strMessage){
    	customConfirmDialog.tvMsg.setText(strMessage);
        return customConfirmDialog;
    }
    
	public static void startConfirmationDialog(Context ctx, String info,String title)
	{
		if (customConfirmDialog == null){
			customConfirmDialog = ConfirmationDialog.createDialog(ctx);
		}
		customConfirmDialog.setBtnTitle(title);
		customConfirmDialog.setMessage(info);
		customConfirmDialog.show();
	}
	public static void stopConfirmationDialog(){
        if (customConfirmDialog != null){
        	customConfirmDialog.dismiss();
        	customConfirmDialog = null;
        }
    }
}
