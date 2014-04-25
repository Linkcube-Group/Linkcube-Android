package me.linkcube.app.ui.user;

import me.linkcube.app.R;
import me.linkcube.app.sync.core.ASmackManager;
import me.linkcube.app.sync.user.FindPassword;
import me.linkcube.app.ui.BaseActivity;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindPasswordActivity extends BaseActivity {

	private EditText findPasswordEt;
	private Button findPasswordBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_password_activity);
		configureActionBar(R.string.foget_password);
		findPasswordEt = (EditText) findViewById(R.id.find_password_et);
		findPasswordBtn = (Button) findViewById(R.id.find_password_btn);

		findPasswordBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String emailString = findPasswordEt.getText().toString();
				//ProviderManager.getInstance().addIQProvider("query", "ss:iq:findPassword", new FindPasswordProvider());
				
				XMPPConnection connection = ASmackManager.getInstance().getXMPPConnection();
				try {
					FindPassword find = new FindPassword();
					find.setUsername(emailString);
					find.setType(IQ.Type.GET);
//					PacketFilter filter = new AndFilter(new PacketIDFilter(find.getPacketID()), new PacketTypeFilter(IQ.class));
					PacketFilter filter = new  PacketIDFilter(find.getPacketID());
					PacketCollector collector = connection.createPacketCollector(filter);
					connection.sendPacket(find);
					
					System.out.println(find.toXML());
					Packet re =  collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
					collector.cancel();// 停止请求results（是否成功的结果）
					
					//对re进行解析
					System.out.println(re.toXML());
					//Timber.i(re.getError().getType().toString());
					//re.get

				} catch (Exception e) {
					e.printStackTrace();
				}
				Toast.makeText(FindPasswordActivity.this, emailString, Toast.LENGTH_SHORT).show();
			}
		});
	}

}
