package me.linkcube.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * 
 * 确定页面
 * 
 */

public class ConfirmationDlgActivity extends Activity implements OnClickListener{

	//public Intent(android.content.Context packageContext, java.lang.Class cls);
	private Button btnAgree;
	private Button btnReject;
	private TextView tvInfoTitle;
	private TextView tvInfoMsg;
	private ImageView imClose;
	
	protected void BindUIHandle()
	{
		btnAgree = (Button)findViewById(R.id.btn_agree);
		btnAgree.setOnClickListener(this);
		btnReject = (Button)findViewById(R.id.btn_reject);
		btnReject.setOnClickListener(this);


		tvInfoTitle = (TextView)findViewById(R.id.tv_title);
		tvInfoMsg = (TextView)findViewById(R.id.tv_msg);
		
		//imClose = (ImageView)findViewById(R.id.im_close);
		//imClose.setOnClickListener(this);
		
		Bundle extras = getIntent().getExtras();
		String titleText = extras.getString(LinkDefines.Data_Nickname);
		String detailText = extras.getString(LinkDefines.Data_Result);
		tvInfoTitle.setText(titleText);
		tvInfoMsg.setText(detailText);
	}
	
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new BitmapDrawable());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setFinishOnTouchOutside(false);
        setContentView(R.layout.dialog_confirmation);
        
        BindUIHandle();
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();   
        //请求代码可以自己设置，这里设置成20  
		if(v==btnAgree)
		{
			setResult(LinkDefines.RES_Yes, intent);
	        finish();
		}
		else if(v==btnReject)
		{
			setResult(LinkDefines.RES_No, intent);
	        finish();
		}
	}
}
