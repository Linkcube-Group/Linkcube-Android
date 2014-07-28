package me.linkcube.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

/*
 * 
 * Splash页面
 * 
 */
public class SplashActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		
		new Handler().postDelayed(new Runnable()
		{
            @Override
			public void run()
            {
                /* Create an Intent that will start the Main WordPress Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, MainTabActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, 900);
	}

}
