package me.linkcube.app.ui.main.single;

/**
 * StatusBar实现类
 * 
 * @author orange
 * 
 */
public interface OnSingleStatusBarClickListener {

	/**
	 * 展示个人信息
	 */
	void showUserInfoActivity();

	/**
	 * 展示登录界面
	 */
	void showLoginActivity();

	/**
	 * 展示Setting页面
	 */
	void showMoreSettingActivity();

	/**
	 * 连接玩具
	 */
	void showBluetoothSettingActivity();

	/**
	 * 重置玩具档位
	 */
	void resetToy();

}
