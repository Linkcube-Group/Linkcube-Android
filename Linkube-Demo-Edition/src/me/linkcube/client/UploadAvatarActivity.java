package me.linkcube.client;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;


/*
 * 
 * 
 * 
 * 
 */


public class UploadAvatarActivity extends Activity implements OnClickListener{

	//public Intent(android.content.Context packageContext, java.lang.Class cls);
	private Button btn1;
	private ImageView imClose;
	
	protected void BindUIHandle()
	{
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.text);
        getWindow().setBackgroundDrawable(new BitmapDrawable());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_upload_avatar);
        
        BindUIHandle();
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent data=new Intent();  
        data.putExtra("bookname", "haha");  
        //data.putExtra("booksale", str_booksale);  
        //请求代码可以自己设置，这里设置成20  
		if(v==imClose)
		{
			setResult(0, data);
		}
	}
}
