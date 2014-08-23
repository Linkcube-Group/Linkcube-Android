package me.linkcube.app.ui;

/**
 * 获取当前Activity持有的Fragment
 * 
 * @author Orange
 * 
 */
public interface FragmentProvider {

	/**
	 * 获取当前Activity所持有的最上层Fragment
	 * 
	 * @return fragment
	 */
	DialogFragment getFragment();

}
