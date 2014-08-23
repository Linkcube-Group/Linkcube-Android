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

/*
 * 
 * 登陆或创建界面
 * 
 */

public class LoginOrCreateActivity extends Activity implements OnClickListener{
	
	//private ImageView ivReturn;
	private Button btnLogin;
	private Button btnCreateAccount;
	private Button btnCancel;
	protected void BindUIHandle()
	{
		//ivReturn = (ImageView)findViewById(R.id.im_close);
		//ivReturn.setOnClickListener(this);
		btnLogin = (Button)findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(this);
		btnCreateAccount = (Button)findViewById(R.id.btn_createaccount);
		btnCreateAccount.setOnClickListener(this);
		btnCancel = (Button)findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);
	}
	
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new BitmapDrawable());
//        setFinishOnTouchOutside(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loginorcreate);
        BindUIHandle();
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();  
		if(v==btnCancel)
		{
			setResult(LinkDefines.RES_No, intent);
		}
		else if(v==btnLogin)
		{
			setResult(LinkDefines.RES_Login, intent);
		}
		else if(v==btnCreateAccount)
		{
			setResult(LinkDefines.RES_CreateAccount, intent);
		}
        finish();
	}

	
}
