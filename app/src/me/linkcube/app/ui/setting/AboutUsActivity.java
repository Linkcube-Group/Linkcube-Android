package me.linkcube.app.ui.setting;

import me.linkcube.app.R;
import me.linkcube.app.ui.BaseActivity;

import android.os.Bundle;

public class AboutUsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutus_activity);
		configureActionBar(R.string.about_us);
	}

}
