package me.linkcube.app.common.ui;

import java.io.Serializable;

import com.actionbarsherlock.app.SherlockFragment;
import com.umeng.analytics.MobclickAgent;

import me.linkcube.app.core.Timber;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Fragment基类,提供了便捷的Activity向Fragment传递的Serializable和简单类型数据的方法，
 * 并在重要的生命周期中打印Log，方便调试
 * 
 * @author Ervin
 */
public class BaseFragment extends SherlockFragment {

	protected Activity mActivity;

	@SuppressWarnings("unchecked")
	protected <V extends Serializable> V getSerializable(final String name) {
		return (V) getArguments().getSerializable(name);
	}

	protected int getIntExtra(final String name) {
		return getArguments().getInt(name, -1);
	}

	protected boolean getBooleanExtra(final String name) {
		return getArguments().getBoolean(name, false);
	}

	protected String getStringExtra(final String name) {
		return getArguments().getString(name);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		Timber.d("onAttach");
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Timber.d("onViewCreated");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Timber.d("onActivityCreated");
	}

	@Override
	public void onStart() {
		super.onStart();
		Timber.d("onStart");
	}

	@Override
	public void onStop() {
		super.onStop();
		Timber.d("onStop");
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getName());
		Timber.d(this.getClass().getName());
		Timber.d("onResume");
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getName());
		Timber.d(this.getClass().getName());
		Timber.d("onResume");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Timber.d("onDestroyView");
	}

}