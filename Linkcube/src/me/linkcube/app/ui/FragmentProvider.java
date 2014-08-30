package me.linkcube.app.ui;

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
	DialogFragment getFragment();

}
