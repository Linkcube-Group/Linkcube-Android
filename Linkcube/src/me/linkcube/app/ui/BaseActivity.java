package me.linkcube.app.ui;

import java.io.Serializable;

import me.linkcube.app.R;
import me.linkcube.app.core.Timber;
import me.linkcube.app.util.PreferenceUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

/**
 * Activity基类，提供了生命周期的日志输出，定义了一些变量
 * 
 * @author Orange
 * 
 */
public abstract class BaseActivity extends SherlockActivity {

	protected Activity mActivity = this;
	protected View actionbarView = null;

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Timber.d("onCreate");
	}

	/**
	 * 配置ActionBar
	 */
	/*
	 * protected void configureActionBar(String title) { ActionBar actionBar =
	 * getSupportActionBar(); actionBar.setTitle(title);
	 * actionBar.setDisplayHomeAsUpEnabled(true);
	 * actionBar.setDisplayShowHomeEnabled(false); }
	 */

	protected void configureActionBar(String title) {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(title);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		actionbarView = inflater.inflate(R.layout.action_layout, null);
		TextView actionbarTv = (TextView) actionbarView
				.findViewById(R.id.actionbar_title);
		actionbarTv.setText(title);
		final ImageView actionbarBackIv = (ImageView) actionbarView
				.findViewById(R.id.actionbar_back_arrow_iv);
		RelativeLayout actionbarBackRl = (RelativeLayout) actionbarView
				.findViewById(R.id.actionbar_back_rl);
		actionbarBackRl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				actionbarBackIv.setImageResource(R.drawable.back_arrow_pressed);
				finish();
			}
		});
		ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		actionBar.setCustomView(actionbarView, layoutParams);
	}

	/*
	 * @Override public void onAttachedToWindow() { super.onAttachedToWindow();
	 * this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD); }
	 * 
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { switch
	 * (keyCode) { case KeyEvent.KEYCODE_HOME:
	 * NotificationUtils.initNotification(mActivity, 1100,"Linkcube"); } return
	 * super.onKeyDown(keyCode, event); }
	 */

	protected void configureActionBar(int resId) {
		String title = getResources().getString(resId);
		configureActionBar(title);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/*
		 * case android.R.id.home: this.finish(); break;
		 */
		default:
			break;
		}

		return true;
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Timber.d("onDestroy");
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getName());
		MobclickAgent.onPause(this);
		Timber.d(this.getClass().getName());
		Timber.d("onPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getName());
		MobclickAgent.onResume(this);
		Timber.d(this.getClass().getName());
		Timber.d("onResume");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Timber.d("onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Timber.d("onStop");
	}
	
}
