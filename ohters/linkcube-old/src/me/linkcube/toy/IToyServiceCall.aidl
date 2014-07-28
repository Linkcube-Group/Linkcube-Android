package me.linkcube.toy;

import java.util.List;
import java.util.Map;

//玩具相关：连接、关闭、设置震动幅度、设置声音幅度、设置震动模式、设置七种预定模式

interface IToyServiceCall {
	boolean IsConnected(String name, String macaddr);
	boolean IsToyConnected();
	boolean ConnectToy(String name, String macaddr);
	boolean CloseToy(String name, String macaddr);

	int CacheToySpeed(int speed,boolean overtime);
	int CacheToyMode(int mode);
	
	void SetShakenSensi(int v);
	void SetVoiceSensi(int v);

	int CacheShake(long shkspd,boolean overtime);
	int SetWave(long waveng);
	
	//远程模式
	boolean EnableToyRemoteMode(boolean enable,String jid);
	//声音模式
	boolean SetWaveMode(int index, int val);
	//震动模式
	boolean SetShakeMode(int index, int val);
}

