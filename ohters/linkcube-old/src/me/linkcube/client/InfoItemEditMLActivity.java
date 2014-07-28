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


public class InfoItemEditMLActivity extends Activity implements OnClickListener{
	private Button btnConfirm;
	private TextView tvTitle;
	private EditText etInput;
	private ImageView imClose;
	
	protected void BindUIHandle()
	{
		btnConfirm = (Button)findViewById(R.id.btn_dlg_ok);
		btnConfirm.setOnClickListener(this);
		
		tvTitle=(TextView)findViewById(R.id.tv_title);
		etInput = (EditText)findViewById(R.id.et_input);

        Intent intent = getIntent();
		Bundle bundle=intent.getExtras();  //data为B中回传的Intent
		String title = bundle.getString(LinkDefines.Data_Title);
		tvTitle.setText(title);
		String curvalue = bundle.getString(LinkDefines.Data_CurValue);
		etInput.setText(curvalue);
		
		imClose = (ImageView)findViewById(R.id.im_close);
		imClose.setOnClickListener(this);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.text);
        getWindow().setBackgroundDrawable(new BitmapDrawable());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_infoitemmledit);
        
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
			intent.putExtra(LinkDefines.Data_Result, result);
			setResult(LinkDefines.RES_Yes, intent);
	        finish();
		}
	}
}
