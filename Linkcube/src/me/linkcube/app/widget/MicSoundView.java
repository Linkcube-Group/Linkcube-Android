package me.linkcube.app.widget;

import java.util.ArrayList;
import java.util.List;

import me.linkcube.app.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MicSoundView extends LinearLayout {

	private List<ImageView> micSoundViews = new ArrayList<ImageView>();

	private int[] resources = { R.id.mic_sound_iv_1, R.id.mic_sound_iv_2,
			R.id.mic_sound_iv_3, R.id.mic_sound_iv_4, R.id.mic_sound_iv_5,
			R.id.mic_sound_iv_6, R.id.mic_sound_iv_7, R.id.mic_sound_iv_8,
			R.id.mic_sound_iv_9, R.id.mic_sound_iv_10 };

	public MicSoundView(Context context) {
		super(context);
		init(context);
	}

	public MicSoundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.mic_sound_view, this, true);
		for (int i = 0; i < resources.length; i++) {
			micSoundViews.add((ImageView) view.findViewById(resources[i]));
		}
	}

	public void changeSoundIv(int position) {
		for (int i = 0; i < micSoundViews.size(); i++) {
			if(i<position){
				micSoundViews.get(i).setImageResource(R.drawable.btn_pink_normal);
			}else{
				micSoundViews.get(i).setImageResource(R.drawable.btn_grey_normal);
			}
		}
	}

}
