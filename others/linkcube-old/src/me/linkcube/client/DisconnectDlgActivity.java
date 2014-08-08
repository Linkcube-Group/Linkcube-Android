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
import android.widget.TextView;


/*
 * 
 * 
 * 断开连接界面
 */


public class DisconnectDlgActivity extends Activity implements OnClickListener{

	//public Intent(android.content.Context packageContext, java.lang.Class cls);
	private Button btnAgree;
	private TextView tvInfoTitle;
	
	protected void BindUIHandle()
	{
		btnAgree = (Button)findViewById(R.id.btn_agree);
		btnAgree.setOnClickListener(this);

		tvInfoTitle = (TextView)findViewById(R.id.tv_title);
		
		//imClose = (ImageView)findViewById(R.id.im_close);
		//imClose.setOnClickListener(this);
		
		Bundle extras = getIntent().getExtras();
		String titleText = extras.getString(LinkDefines.Data_Nickname);
		tvInfoTitle.setText(titleText);
	}
	
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new BitmapDrawable());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setFinishOnTouchOutside(false);
        setContentView(R.layout.dialog_disconnect);
        
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
	}
}
