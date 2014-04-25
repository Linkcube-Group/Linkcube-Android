package me.linkcube.app.ui.main.multi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MultiPlayerPagerAdapter extends FragmentPagerAdapter {

	private static int NUM_ITEMS = 2;

	private ChatListFragment chatListFragment;

	private FriendListFragment friendListFragment;

	public MultiPlayerPagerAdapter(FragmentManager fm) {
		super(fm);
		chatListFragment = new ChatListFragment();
		friendListFragment = new FriendListFragment();
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return chatListFragment;
		case 1:
			return friendListFragment;
		default:
			break;
		}
		return null;
	}

	@Override
	public int getCount() {
		return NUM_ITEMS;
	}

}
