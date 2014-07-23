package me.linkcube.app.core.game;

import java.util.Timer;
import java.util.TimerTask;

import android.R.bool;

public class ToyConnectTimeManager {

	private static ToyConnectTimeManager instance = null;

	private Timer statisticsTimer = null;

	public static int duration = 0;

	private boolean flag = true;

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

	public void startTimeStatistics() {
		duration = 0;
		if (flag) {
			statisticsTimer = new Timer();
			statisticsTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					System.out.println("--duration:" + duration);
					duration++;
				}
			}, 0, 1000);
		}
		flag = false;
	}

	public void stopTimeStatistics() {
		if (statisticsTimer != null) {
			System.out.println("--stopTimeStatistics--");
			statisticsTimer.cancel();
			statisticsTimer = null;
		}
	}

	public long getDuration() {
		return duration;
	}

}
