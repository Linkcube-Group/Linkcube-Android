package me.linkcube.app.widget;

import android.view.View;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.View.INVISIBLE;

/**
 * 显示和隐藏View的帮助类
 * 
 * @author Ervin
 * 
 */
public class ViewUtils {

	private ViewUtils() {

	}

	public static <V extends View> V setGone(final V view, final boolean gone) {
		if (view != null) {
			if (gone) {
				if (GONE != view.getVisibility()) {
					view.setVisibility(GONE);
				}
			} else {
				if (VISIBLE != view.getVisibility()) {
					view.setVisibility(VISIBLE);
				}
			}
		}
		return view;
	}

	public static <V extends View> V setInvisible(final V view,
			final boolean invisible) {
		if (view != null) {
			if (invisible) {
				if (INVISIBLE != view.getVisibility()) {
					view.setVisibility(INVISIBLE);
				}
			} else {
				if (VISIBLE != view.getVisibility()) {
					view.setVisibility(VISIBLE);
				}
			}
		}
		return view;
	}

}
