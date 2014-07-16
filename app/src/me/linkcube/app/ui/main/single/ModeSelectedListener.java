package me.linkcube.app.ui.main.single;

/**
 * 模式选中回调接口
 * 
 * @author orange
 * 
 */
public interface ModeSelectedListener {

	/**
	 * 摇一摇模式的回调
	 * 
	 * @param level
	 */
	void onShakeMode(int level);
	
	/**
	 * 关闭摇一摇模式的回调
	 * 
	 * @param level
	 */
	void offShakeMode(int level);

	/**
	 * 声控模式的回调
	 * 
	 * @param level
	 */
	void onVoiceMode(int level);
	
	/**
	 * 关闭声控模式的回调
	 * 
	 * @param level
	 */
	void offVoiceMode(int level);

	/**
	 * 姿势选择的回调
	 */
	void onSexPositionMode(int level);

	/**
	 * 提示蓝牙没有连接
	 */
	void showConnectBluetoothTip();
	/**
	 * 提示用户打开外部声音
	 */
	void showOpenMusicPlayerDialog();
}
