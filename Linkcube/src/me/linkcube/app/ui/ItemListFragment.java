package me.linkcube.app.ui;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 
 * @author Ervin
 * 
 * @param <E>
 */
public abstract class ItemListFragment<E> extends DialogFragment {

	private ListView listView;

	protected boolean isScrolling;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listView = (ListView) view.findViewById(android.R.id.list);
		configureList(getActivity(), getListView());
	}

	protected void configureList(FragmentActivity activity, ListView listView) {
		listView.setAdapter(createAdapter());
		listView.setOnScrollListener(onScrollListener);
	}

	public void onLoadedFinished(List<E> items) {
		getListAdapter().setList(items);
		showList();
	}

	public void setOnScrollListener(OnScrollListener listener) {
		listView.setOnScrollListener(listener);
	}

	private OnScrollListener onScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			if (scrollState == SCROLL_STATE_IDLE) {
				isScrolling = false;
			} else {
				isScrolling = true;
			}

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub

		}
	};

	public ItemListFragment<E> setListAdapter(ListAdapter adapter) {
		if (listView != null)
			listView.setAdapter(adapter);
		return this;
	}

	protected abstract BaseListAdapter<E> createAdapter();

	@SuppressWarnings("unchecked")
	protected BaseListAdapter<E> getListAdapter() {
		if (listView != null)
			return (BaseListAdapter<E>) listView.getAdapter();
		else
			return null;
	}

	protected ItemListFragment<E> notifyDataSetChanged() {
		BaseListAdapter<E> adapter = getListAdapter();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		return this;
	}

	public ListView getListView() {
		return listView;
	}

	protected void showList() {
		notifyDataSetChanged();
	}

}
