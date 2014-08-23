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
import android.widget.EditText;
import android.widget.ImageView;


/*
 * 连接码界面
 * 
 * 
 */

public class ConnectCodeActivity extends Activity implements OnClickListener{
	//public Intent(android.content.Context packageContext, java.lang.Class cls);
	private Button btnConfirm;
	private ImageView imClose;
	private EditText etConnectCode;
	
	protected void BindUIHandle()
	{
		btnConfirm = (Button)findViewById(R.id.btn_dlg_ok);
		btnConfirm.setOnClickListener(this);

		imClose = (ImageView)findViewById(R.id.im_close);
		imClose.setOnClickListener(this);
		
		etConnectCode = (EditText)findViewById(R.id.et_connectcode);
	}
	
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.text);
        getWindow().setBackgroundDrawable(new BitmapDrawable());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_connectcode);
        
        BindUIHandle();
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();  
        //data.putExtra("bookname", "haha");  
        //data.putExtra("booksale", str_booksale);  
        //请求代码可以自己设置，这里设置成20  
		if(v==imClose)
		{
			setResult(LinkDefines.RES_No, intent);
	        finish();
		}
		else if(v==btnConfirm)
		{
			String connectcode =etConnectCode.getText().toString();
			intent.putExtra(LinkDefines.Data_Connectcode, connectcode);
			setResult(LinkDefines.RES_Yes, intent);
	        finish();
		}
	}
}
