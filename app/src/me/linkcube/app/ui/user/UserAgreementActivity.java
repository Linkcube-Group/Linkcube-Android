package me.linkcube.app.ui.user;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.os.Bundle;
import android.widget.TextView;
import me.linkcube.app.R;
import me.linkcube.app.ui.BaseActivity;

public class UserAgreementActivity extends BaseActivity {
	
	private TextView linkcubeProtocolTv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_agreement_activity);
		
		configureActionBar(R.string.user_agreement_activity);
		
		initView();
	}

	private void initView() {
		linkcubeProtocolTv=(TextView)findViewById(R.id.linkcube_protocol_tv);
		String result=null;
		InputStream iStream=getResources().openRawResource(R.raw.linkcube_protocol);
		int length;
		try {
			length = iStream.available();
			byte[] buffer=new byte[length];
			iStream.read(buffer);
			result=EncodingUtils.getString(buffer, "GBK");
		} catch (IOException e) {
			e.printStackTrace();
		}
		linkcubeProtocolTv.setText(result);
		
	}

	
}
