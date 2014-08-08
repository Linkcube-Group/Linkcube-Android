package me.linkcube.client;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/*
 * 
 * 登陆界面
 * 
 */
public class LoginActivity extends Activity implements OnClickListener{
	
	private ImageView ivReturn;
	private Button btnLogin;
	private EditText etUserName;
	private EditText etPassword;
	private CheckBox cbAutoSave;
	protected void BindUIHandle()
	{
		ivReturn = (ImageView)findViewById(R.id.ibtn_return);
		ivReturn.setOnClickListener(this);
		btnLogin = (Button)findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(this);
		
		etUserName = (EditText)findViewById(R.id.et_username);
		etPassword = (EditText)findViewById(R.id.et_password);
		if(LinkcubeApplication.getConfig(LoginActivity.this).isSave)
		{
			etUserName.setText(LinkcubeApplication.getConfig(LoginActivity.this).userID);
			etPassword.setText(LinkcubeApplication.getConfig(LoginActivity.this).passWord);
		}
		
		cbAutoSave = (CheckBox)findViewById(R.id.cb_autosave);
		cbAutoSave.setChecked(LinkcubeApplication.getConfig(LoginActivity.this).isSave);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new BitmapDrawable());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        BindUIHandle();
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();  
        //请求代码可以自己设置，这里设置成20  
		if(v==btnLogin)
		{
			//cb_autosave
			if(checkUserInput())
			{
				if(cbAutoSave.isChecked())
				{
					LinkcubeApplication.getConfig(LoginActivity.this).userID = etUserName.getText().toString();
					LinkcubeApplication.getConfig(LoginActivity.this).passWord = etPassword.getText().toString();
					LinkcubeApplication.getConfig(LoginActivity.this).isSave = true;
					LinkcubeApplication.getConfig(LoginActivity.this).updatePrefs();
				}
				else
				{
					LinkcubeApplication.getConfig(LoginActivity.this).userID = "";
					LinkcubeApplication.getConfig(LoginActivity.this).passWord = "";
					LinkcubeApplication.getConfig(LoginActivity.this).isSave = false;
					LinkcubeApplication.getConfig(LoginActivity.this).updatePrefs();
				}
				intent.putExtra(LinkDefines.Data_JID, etUserName.getText().toString());
				intent.putExtra(LinkDefines.Data_Password, etPassword.getText().toString());
				setResult(LinkDefines.RES_Yes, intent);
		        finish();
			}
			else
			{
				Toast.makeText(LoginActivity.this, "用户名或者密码错误，请修改！", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			setResult(LinkDefines.RES_No, intent);
			finish();
		}
	}

	private boolean checkUserInput()
	{
		if((etUserName.getText().toString()!=null)||etPassword.getText().toString()!=null)
		{
			String jid =etUserName.getText().toString();
			String pwd =etPassword.getText().toString();
			if((jid.length()>0)&&(pwd.length()>0))
			{
				return true;
			}
			else 
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
}
