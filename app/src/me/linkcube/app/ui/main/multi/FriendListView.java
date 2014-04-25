package me.linkcube.app.ui.main.multi;

import me.linkcube.app.R;
import me.linkcube.app.widget.ViewUtils;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class FriendListView extends ListView {

	private LinearLayout addFriendBtn;

	private LinearLayout friendsAddedBtn;

	/**
	 * 新消息提醒指示器
	 */
	private ImageView newFriendTipIv;

	private OnFriendListViewClickListener mListener;

	public FriendListView(Context context) {
		super(context);
		init(context);
	}

	public FriendListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View header = mInflater.inflate(R.layout.friend_listview_header, null);
		addFriendBtn = (LinearLayout) header.findViewById(R.id.add_friend_cell);
		friendsAddedBtn = (LinearLayout) header
				.findViewById(R.id.friends_added_cell);
		newFriendTipIv = (ImageView) header
				.findViewById(R.id.new_friend_tip_iv);
		addHeaderView(header);
		addFriendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.showAddFriendActivity();
			}
		});

		friendsAddedBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.showFriendsAddedActivity();
				setNewFriendTipInVisible(true);

			}
		});
		setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mListener.onItemClick(arg0, arg1, arg2, arg3);
			}
		});
		setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				return mListener.onItemLongClick(arg0, arg1, arg2, arg3);
			}
		});
	}

	public void setNewFriendTipInVisible(boolean invisible) {
		ViewUtils.setInvisible(newFriendTipIv, invisible);
	}

	public void setOnFriendListViewClickListener(
			OnFriendListViewClickListener listener) {
		this.mListener = listener;
	}

	public interface OnFriendListViewClickListener extends OnItemClickListener,
			OnItemLongClickListener {

		void showFriendsAddedActivity();

		void showAddFriendActivity();

	}
}
