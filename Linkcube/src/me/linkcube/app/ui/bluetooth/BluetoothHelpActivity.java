package me.linkcube.app.ui.bluetooth;

import android.os.Bundle;
import me.linkcube.app.R;
import me.linkcube.app.common.ui.BaseActivity;

public class BluetoothHelpActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_help_activity);
		configureActionBar(R.string.bluetooth_help);
	}

	

}
