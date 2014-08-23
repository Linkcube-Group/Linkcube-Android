package me.linkcube.client;

import com.oplibs.services.IOnChatServiceCall;

import me.linkcube.toy.IToyServiceCall;
import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

/*
 * 封装application
 * 
 * 
 */
public class LinkcubeApplication extends Application{
	
	public IToyServiceCall toyServiceCall;
	public IOnChatServiceCall chatServiceCall;
	public LinkcubeConfiguration mConfig;
	public int gHeight=0;
	public int gWidth=0;

	public LinkcubeApplication() {
		super();
	}

	@Override
	public void onCreate() {
		mConfig = new LinkcubeConfiguration(PreferenceManager.getDefaultSharedPreferences(this));
	}

	public static LinkcubeApplication getApp(Context ctx) {
		return (LinkcubeApplication)ctx.getApplicationContext();
	}
	
	public static LinkcubeConfiguration getConfig(Context ctx) {
		return getApp(ctx).mConfig;
	}

}
