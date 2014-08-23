package me.linkcube.app.ui.friend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class FriendListView extends ListView {

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

	public void setOnFriendListViewClickListener(
			OnFriendListViewClickListener listener) {
		this.mListener = listener;
	}

	public interface OnFriendListViewClickListener extends OnItemClickListener,
			OnItemLongClickListener {

	}
}
