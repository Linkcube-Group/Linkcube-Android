package me.linkcube.app.ui.main;

import me.linkcube.app.core.toy.ShakeSensor;
import me.linkcube.app.core.toy.VoiceSensor;

public interface SensorProvider {

	public ShakeSensor getShakeSensor();

	public VoiceSensor getVoiceSensor();

}
