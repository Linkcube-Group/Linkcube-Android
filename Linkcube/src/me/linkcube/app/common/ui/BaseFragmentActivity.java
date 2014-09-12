package me.linkcube.app.common.ui;

import java.io.InputStream;
import java.io.Serializable;

import javax.crypto.Mac;

import me.linkcube.app.R;
import me.linkcube.app.common.util.PreferenceUtils;
import me.linkcube.app.core.Timber;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author Ervin
 * 
 */
public abstract class BaseFragmentActivity extends SherlockFragmentActivity
		implements FragmentProvider {

	protected Activity mActivity = this;

	protected ProgressDialog progressDialog = null;

	/**
	 * 显示进度框
	 */
	protected void showProgressDialog(String message) {
		if (progressDialog == null)
			progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(true);
		progressDialog.setMessage(message);
		progressDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	protected void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@SuppressWarnings("unchecked")
	protected <V extends Serializable> V getSerializable(final String name) {
		return (V) getIntent().getSerializableExtra(name);
	}

	protected int getIntExtra(final String name) {
		return getIntent().getIntExtra(name, -1);
	}

	protected boolean getBooleanExtra(final String name) {
		return getIntent().getBooleanExtra(name, false);
	}

	protected String getStringExtra(final String name) {
		return getIntent().getStringExtra(name);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Timber.d("onCreate");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Timber.d("onDestroy");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Timber.d("onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Timber.d("onStop");
	}

	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

}