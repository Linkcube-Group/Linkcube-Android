package me.linkcube.app.ui;

import java.io.InputStream;
import java.io.Serializable;

import javax.crypto.Mac;

import me.linkcube.app.R;
import me.linkcube.app.core.Timber;
import me.linkcube.app.util.PreferenceUtils;
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
public class BaseFragmentActivity extends SherlockFragmentActivity implements
		FragmentProvider {

	protected Activity mActivity = this;

	protected ProgressDialog progressDialog = null;

	private ImageView guideImage;

	private int guideResourceId[] = { R.drawable.help_guide_bg1,
			R.drawable.help_guide_bg2, R.drawable.help_guide_bg3, 0 };

	private int guidePosition = 0;

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
		addHelpGuideImage();// 添加引导页
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

	@Override
	public DialogFragment getFragment() {
		// TODO 返回当前最上层的Fragment
		return null;
	}

	/**
	 * 添加引导图片
	 */
	public void addHelpGuideImage() {
		try {

			View view = getWindow().getDecorView().findViewById(
					R.id.content_view);// 查找通过setContentView上的根布局
			if (view == null)
				return;
			if (PreferenceUtils.getBoolean("isHelpGuide", false)) {
				// 引导过了
				return;
			}
			ViewParent viewParent = view.getParent();
			if (viewParent instanceof FrameLayout) {
				final FrameLayout frameLayout = (FrameLayout) viewParent;
				guideImage = new ImageView(this);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				guideImage.setLayoutParams(params);
				guideImage.setScaleType(ScaleType.FIT_XY);
				guideImage.setImageResource(guideResourceId[guidePosition]);
				guideImage.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						guidePosition++;
						if (guidePosition >= guideResourceId.length - 1) {
							frameLayout.removeView(guideImage);
							PreferenceUtils.setBoolean("isHelpGuide", true);// 设为已引导
						} else {
							guideImage
									.setImageBitmap(readBitMap(mActivity,guideResourceId[guidePosition]));
						}
					}
				});
				frameLayout.addView(guideImage);// 添加引导图片
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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