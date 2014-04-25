package me.linkcube.app.ui.main;

import me.linkcube.app.R;
import me.linkcube.app.core.user.UserManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * 底部导航栏
 * 
 * @author orange
 * 
 */
public class TabIndicatorView extends LinearLayout {

	/**
	 * 底部导航单人模式TAB的Index
	 */
	public static final int SINGLE_TAB = 0;

	/**
	 * 底部导航多人模式TAB的Index
	 */
	public static final int MULTI_TAB = 1;

	private int index;

	private ImageButton singleBtn;

	private ImageButton multiBtn;

	private OnTabIndicatorClickListener mListener;

	public TabIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TabIndicatorView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.tab_indicator, this, true);
		singleBtn = (ImageButton) view.findViewById(R.id.single_tab);
		multiBtn = (ImageButton) view.findViewById(R.id.multi_tab);
		singleBtn.setOnClickListener(clickListener);
		multiBtn.setOnClickListener(clickListener);
		index = SINGLE_TAB;
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.single_tab:
				selectSingleTab();
				break;
			case R.id.multi_tab:
				selectMultiTab();
				break;
			default:
				break;
			}

		}
	};

	/**
	 * 选中底部导航页面的Tab
	 * 
	 * @param index
	 */
	public void setCurrentTab(int index) {
		if (index == SINGLE_TAB) {
			selectSingleTab();
			return;
		}
		if (index == MULTI_TAB) {
			selectMultiTab();
			return;
		}
	}

	/**
	 * 返回当前导航页的索引值
	 * 
	 * @return index
	 */
	public int getCurrentTabIndex() {
		return index;
	}

	private void selectSingleTab() {
		index = SINGLE_TAB;
		singleBtn.setBackgroundResource(R.drawable.tab_indicator_selected);
		multiBtn.setBackgroundResource(R.drawable.tab_indicator_normal);
		singleBtn.setImageResource(R.drawable.tab_indicator_single_selected);
		multiBtn.setImageResource(R.drawable.tab_indicator_multi_normal);
		mListener.onSelectSingleTab();
	}

	private void selectMultiTab() {
		if (!UserManager.getInstance().isAuthenticated()) {
			mListener.showLoginActivity();
			return;
		}
		index = MULTI_TAB;
		singleBtn.setBackgroundResource(R.drawable.tab_indicator_normal);
		multiBtn.setBackgroundResource(R.drawable.tab_indicator_selected);
		singleBtn.setImageResource(R.drawable.tab_indicator_single_normal);
		multiBtn.setImageResource(R.drawable.tab_indicator_multi_selected);
		mListener.onSelectMultiTab();
	}

	public void setOnTabIndictorClickListener(
			OnTabIndicatorClickListener listener) {
		this.mListener = listener;
	}

	/**
	 * 底部导航栏实现回调接口
	 * 
	 * @author orange
	 * 
	 */

	public interface OnTabIndicatorClickListener {

		/**
		 * 选中单人模式Tab
		 */
		void onSelectSingleTab();

		/**
		 * 选中多人模式Tab
		 */
		void onSelectMultiTab();

		void showLoginActivity();

	}

}
