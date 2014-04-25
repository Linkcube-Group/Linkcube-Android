package me.linkcube.app.ui.main.multi;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ChatListView extends ListView {

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

	public void setOnChatListViewClickListener(
			OnChatListViewClickListener listener) {
		this.mListener = listener;
	}

	public interface OnChatListViewClickListener extends OnItemClickListener,
			OnItemLongClickListener {

	}
}
