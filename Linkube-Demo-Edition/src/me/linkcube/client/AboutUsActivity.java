package me.linkcube.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

/*
 * 
 * 关于界面
 * 
 */

public class AboutUsActivity extends Activity{
	
	private ImageView btnReturn;
	protected void BindUIHandle()
	{
        btnReturn = (ImageView)findViewById(R.id.ibtn_return);
    	btnReturn.setOnClickListener(returnLastUIListener);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_aboutus);
        BindUIHandle();
    }
    
	private Button.OnClickListener returnLastUIListener = new Button.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			finish();
		}
	};

}
