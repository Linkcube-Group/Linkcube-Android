package me.linkcube.client;
import com.oplibs.services.XMPPUtils;
import com.oplibs.support.Intents;
import com.oplibs.support.StringUtils;
import com.oplibs.syncore.SyncMgr;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.regex.*;


/*
 * 
 * 创建账户页面
 * 
 * 
 */


public class CreateAccountActivity extends Activity implements OnClickListener{
	
	private ImageView ivReturn;

	private EditText etUserID;
	private EditText etPassword;
	private EditText etPasswordConfirm;
	private Button btnCreateAccount;
	
	private String strUserID;
	private String strNickName;
	private String strPassword;
	private String strPasswordConfirm;
	
	private BroadcastReceiver registerFailedReceiver =  new BroadcastReceiver() 
	{
    	@Override
    	public void onReceive(Context context, Intent intent) 
    	{
    		unregisterReceiver(registerReceiver);
    		unregisterReceiver(registerFailedReceiver);
    		Bundle bundle=intent.getExtras();  //data为B中回传的Intent
    		String failstr = bundle.getString(Intents.RegisterReason);
    		Toast.makeText(CreateAccountActivity.this, failstr, Toast.LENGTH_SHORT).show();
			return;
    	}
    };
    
	private BroadcastReceiver registerReceiver =  new BroadcastReceiver() 
	{
    	@Override
    	public void onReceive(Context context, Intent intent) 
    	{
    		unregisterReceiver(registerReceiver);
    		unregisterReceiver(registerFailedReceiver);
    		CreateAccountActivity.this.setResult(LinkDefines.RES_Yes, intent);
			finish();
			return;
    	}
    };
    
	protected void BindUIHandle()
	{
		etUserID = (EditText)findViewById(R.id.et_userid);
		etPassword = (EditText)findViewById(R.id.et_password);
		etPasswordConfirm = (EditText)findViewById(R.id.et_password_confirm);
		
		btnCreateAccount = (Button)findViewById(R.id.btn_createaccount);
		btnCreateAccount.setOnClickListener(this);
	}
	
	private Boolean isLegal()
	{
		if((StringUtils.IsStringNullOrEmpty(strUserID))
				||(StringUtils.IsStringNullOrEmpty(strPassword))
				||(StringUtils.IsStringNullOrEmpty(strPasswordConfirm)))
			{
				Toast.makeText(CreateAccountActivity.this, "填写信息不完整", Toast.LENGTH_SHORT).show();
				return false;
			}
		
		Pattern p = Pattern.compile("\\w+@(\\w+\\.)+[a-z]{2,3}"); 
		Matcher m = p.matcher(strUserID); 
		if(!m.matches())
		{
			Toast.makeText(CreateAccountActivity.this, "请输入正确的有邮箱地址作为id", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(strUserID.contains("-"))
		{
			Toast.makeText(CreateAccountActivity.this, "请输入正确的有邮箱地址作为id", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(!strPasswordConfirm.equals(strPassword))
		{
			Toast.makeText(CreateAccountActivity.this, "两次输入的密码不一致，请修改", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_createaccount);
        BindUIHandle();
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();  
        //请求代码可以自己设置，这里设置成20  
		if(v==ivReturn)
		{
			setResult(LinkDefines.RES_No, intent);
	        finish();
		}
		else if(v==btnCreateAccount)
		{
			
			strUserID = etUserID.getText().toString();
			strPassword = etPassword.getText().toString();
			strPasswordConfirm = etPasswordConfirm.getText().toString();

			if(isLegal())
			{
				SyncMgr syncmgr = SyncMgr.GetInstance();
				
				IntentFilter registerFilter = new IntentFilter();
				registerFilter.addAction(Intents.RegisterSucceed);
				registerReceiver(registerReceiver, registerFilter);
				
				IntentFilter registerFailedFilter = new IntentFilter();
				registerFailedFilter.addAction(Intents.RegisterFailed);
				registerReceiver(registerFailedReceiver, registerFailedFilter);
				
				syncmgr.CreateNewAccount(XMPPUtils.GetJID(strUserID),strPassword,strNickName);
				/*
				if(syncmgr.CreateNewAccount(strUserID, strPassword, strNickName))
				{
					Toast.makeText(CreateAccountActivity.this, "注册成功，请登录！！", Toast.LENGTH_SHORT).show();
					setResult(LinkDefines.RES_Yes, intent);
					finish();
				}
				else
				{
					Toast.makeText(CreateAccountActivity.this, "注册失败，该用户已存在！", Toast.LENGTH_SHORT).show();
				}
				*/
			}
			else
			{
				Toast.makeText(CreateAccountActivity.this, "注册信息有误，请重新填写！", Toast.LENGTH_SHORT).show();
			}
		}
	}

	
}
