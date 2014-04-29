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

public class ChatListView extends ListView {

	private LinearLayout addFriendBtn;

	private LinearLayout newMessagesTv;

	private LinearLayout friendsTv;

	/**
	 * 新消息提醒指示器
	 */
	private ImageView newTipsIv;

	private OnChatListViewClickListener mListener;

	public ChatListView(Context context) {
		super(context);
		init(context);
	}

	public ChatListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View header = mInflater.inflate(R.layout.friend_listview_header, null);
		addFriendBtn = (LinearLayout) header.findViewById(R.id.add_friend_cell);
		newMessagesTv = (LinearLayout) header
				.findViewById(R.id.new_messages_cell);
		friendsTv = (LinearLayout) header.findViewById(R.id.friends_cell);
		newTipsIv = (ImageView) header.findViewById(R.id.new_tips_iv);
		addHeaderView(header);
		addFriendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.showAddFriendActivity();
			}
		});

		newMessagesTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.showFriendAddedActivity();
				setNewFriendTipInVisible(true);

			}
		});
		friendsTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.showFriendListActivity();
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
		ViewUtils.setInvisible(newTipsIv, invisible);
	}

	public void setOnChatListViewClickListener(
			OnChatListViewClickListener listener) {
		this.mListener = listener;
	}

	public interface OnChatListViewClickListener extends OnItemClickListener,
			OnItemLongClickListener {

		void showFriendAddedActivity();

		void showAddFriendActivity();

		void showFriendListActivity();

	}
}
