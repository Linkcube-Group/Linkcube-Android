package me.linkcube.app.ui.welcome;

import static me.linkcube.app.AppConfig.GUIDE_IMAGES_RES;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import me.linkcube.app.R;
import me.linkcube.app.ui.BasePagerAdapter;
import me.linkcube.app.ui.DialogActivity;
import me.linkcube.app.ui.main.MainActivity;

/**
 * 引导功能页
 * 
 * @author Ervin
 * 
 */
public class GuideActivity extends DialogActivity  {
	private ViewPager viewPager;

	private BasePagerAdapter adapter;

	private List<View> viewList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_activity);
		setUpViews();
		viewPager.setAdapter(adapter);
	}

	private void setUpViews() {
		getGuideViewList();
		viewPager = (ViewPager) findViewById(R.id.viewPagerGuide);
		adapter = new BasePagerAdapter(this, viewList);

	}

	private void getGuideViewList() {
		viewList = new ArrayList<View>();
		for (int i = 0; i < GUIDE_IMAGES_RES.length; i++) {
			View view = getImageView(GUIDE_IMAGES_RES[i]);
			view.setOnClickListener(new OnGuideViewClick(i));
			viewList.add(i, view);
		}
	}

	private ImageView getImageView(int resId) {
		ImageView imageView = new ImageView(this);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setImageResource(resId);
		return imageView;
	}

	private class OnGuideViewClick implements OnClickListener {

		private int mPosition;

		public OnGuideViewClick(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View v) {
			if (mPosition == GUIDE_IMAGES_RES.length - 1) {
				startActivity(new Intent(GuideActivity.this, MainActivity.class));
				finish();
			}
		}

	}
}
