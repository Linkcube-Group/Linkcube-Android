package me.linkcube.client;

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
import android.widget.TextView;
import android.widget.Toast;

/*
 * 信息列表
 * 
 * 
 */

public class InfoItemEditActivity extends Activity implements OnClickListener{
	private Button btnConfirm;
	private Button btnReturn;
	private EditText etInput;
	private TextView tvTitle;
	private ImageView imClose;
	private int itemType;
	
	protected void BindUIHandle()
	{
		btnConfirm = (Button)findViewById(R.id.btn_dlg_ok);
		btnConfirm.setOnClickListener(this);
		
		etInput = (EditText)findViewById(R.id.et_input);
		tvTitle = (TextView)findViewById(R.id.tv_title);

        Intent intent = getIntent();
		Bundle bundle=intent.getExtras();  //data为B中回传的Intent
		String strtitle = bundle.getString(LinkDefines.Data_Title);
		tvTitle.setText(strtitle);
		String strcurvalue = bundle.getString(LinkDefines.Data_CurValue);
		etInput.setText(strcurvalue);
		
		imClose = (ImageView)findViewById(R.id.im_close);
		imClose.setOnClickListener(this);
		
		itemType =bundle.getInt("type");
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.text);
        getWindow().setBackgroundDrawable(new BitmapDrawable());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_infoitemedit);
        
        BindUIHandle();
    }
    
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();  
		if(v==imClose)
		{
			setResult(LinkDefines.RES_No, intent);
	        finish();
		}
		else if(v==btnConfirm)
		{
			String result =etInput.getText().toString();
			if(result == null)
			{
				result = "";
				Toast.makeText(InfoItemEditActivity.this, "连接密码不能为空！", Toast.LENGTH_SHORT).show();
				return;
			}
			if(itemType==LinkDefines.REQ_ConnectCode)
			{
				if(result.length()!=6)
				{
					Toast.makeText(InfoItemEditActivity.this, " 请输入六位数字！", Toast.LENGTH_SHORT).show();
					return;
				}
				for(int nc=0;nc<6;nc++)
				{
					if(result.charAt(nc)<'0'||result.charAt(nc)>'9')
					{
						Toast.makeText(InfoItemEditActivity.this, " 请输入六位数字！", Toast.LENGTH_SHORT).show();
						return;
					}
				}
			}
			intent.putExtra(LinkDefines.Data_Result, result);
			setResult(LinkDefines.RES_Yes, intent);
	        finish();
		}
	}
}
