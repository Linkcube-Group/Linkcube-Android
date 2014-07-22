package me.linkcube.app.core.game;

import java.util.Timer;
import java.util.TimerTask;

public class ToyConnectTimeManager {

	public static ToyConnectTimeManager instance = null;
	
	public static Timer statisticsTimer=null;
	
	public static int duration=0;
	
	public static ToyConnectTimeManager getInstance() {
		if (instance == null) {
			synchronized (ToyConnectTimeManager.class) {
				if (instance == null) {
					instance = new ToyConnectTimeManager();
					return instance;
				}
			}
		}
		return instance;
	}
	
	public void startTimeStatistics(){
		statisticsTimer=new Timer();
		statisticsTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				duration++;
			}
		}, 0, 1000);
		
	}
	
	public void stopTimeStatistics(){
		if (statisticsTimer != null) {
			statisticsTimer.cancel();
			statisticsTimer = null;
		}
	}

	public long getDuration() {
		return duration;
	}
	
}
