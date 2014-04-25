package me.linkcube.app.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import me.linkcube.app.R;
import me.linkcube.app.ui.DialogActivity;
import me.linkcube.app.ui.main.MainActivity;

/**
 * 引导功能页
 * 
 * @author Orange
 * 
 */
public class GuideActivity extends DialogActivity implements
		OnGuideCompleteListener {
	private ViewPager viewPager;

	private GuidePagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_activity);
		setUpViews();
		adapter.setOnCompleteListener(this);
		viewPager.setAdapter(adapter);
	}

	private void setUpViews() {
		viewPager = (ViewPager) findViewById(R.id.viewPagerGuide);
		adapter = new GuidePagerAdapter(this);

	}

	@Override
	public void onComplete() {
		startActivity(new Intent(GuideActivity.this, MainActivity.class));
		finish();
	}
}
