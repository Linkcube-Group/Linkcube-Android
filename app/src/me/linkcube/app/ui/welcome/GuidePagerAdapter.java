package me.linkcube.app.ui.welcome;

import java.util.ArrayList;

import me.linkcube.app.R;
import me.linkcube.app.ui.BasePagerAdapter;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 引导页Adapter
 * 
 * @author Orange
 * 
 */
public class GuidePagerAdapter extends BasePagerAdapter {

	private int[] imageIds = new int[] { R.drawable.guide_0,
			R.drawable.guide_1, R.drawable.guide_2 };

	private OnGuideCompleteListener listener;

	public GuidePagerAdapter(Context context) {
		super(context);
		initPager();
	}

	private void initPager() {
		mViewList = new ArrayList<View>();
		for (int i = 0; i < imageIds.length; i++) {
			mViewList.add(i, getImageView(imageIds[i]));
		}
		mViewList.get(2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onComplete();
				}
			}
		});
	}

	public void setOnCompleteListener(OnGuideCompleteListener listener) {
		this.listener = listener;
	}

	private ImageView getImageView(int id) {
		ImageView imageView = new ImageView(mContext);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setImageResource(id);
		return imageView;
	}

}
