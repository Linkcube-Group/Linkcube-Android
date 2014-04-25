package me.linkcube.app.ui.user;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.os.Bundle;
import android.widget.TextView;
import me.linkcube.app.R;
import me.linkcube.app.ui.BaseActivity;

public class LinkcubeProtocolActivity extends BaseActivity {
	
	private TextView linkcubeProtocolTv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.linkcube_protocol_activity);
		
		configureActionBar(R.string.linkcube_protocol);
		
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
