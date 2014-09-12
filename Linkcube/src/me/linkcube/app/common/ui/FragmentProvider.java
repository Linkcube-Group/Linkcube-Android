package me.linkcube.app.common.ui;

import android.support.v4.app.Fragment;

/**
 * 获取当前Activity持有的Fragment
 * 
 * @author Ervin
 * 
 */
public interface FragmentProvider {

	/**
	 * 获取当前Activity所持有的最上层Fragment
	 * 
	 * @return fragment
	 */
	Fragment getFragment();

}
