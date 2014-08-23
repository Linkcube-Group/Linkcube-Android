package me.linkcube.app.ui;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;

public abstract class BaseListAdapter<E> extends android.widget.BaseAdapter {

	protected List<E> items = new ArrayList<E>();

	public void setList(List<E> items) {
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public E getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public abstract View getView(int position, View convertView,
			ViewGroup parent);

}
