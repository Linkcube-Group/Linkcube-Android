package me.linkcube.app.ui.user;


import me.linkcube.app.R;
import me.linkcube.app.core.Timber;
import me.linkcube.app.ui.BaseActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UpdateUserInfoActivity extends BaseActivity implements OnClickListener{
	
	private Button actionbarFirstBtn;
	
	private EditText changeUserInfoEv;
	private TextView changeUserInfoShowTv;
	private Button saveUserInfoBtn;
	private String information;
	private int requestCode;
	private int CHANGE_NICKNAME=3;
	private int CHANGE_PERSON_STATE=4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_user_info_activity);
		
		configureActionBar(R.string.update_user_info);
		
		
		initView();
		
		Intent intent=getIntent();
		information=intent.getStringExtra("information");
		requestCode=intent.getIntExtra("requestCode",0);
		changeUserInfoEv.setText(information);
		
		if(requestCode==CHANGE_NICKNAME){
			changeUserInfoShowTv.setText("好名字可以让你的朋友更容易记住你");
		}else if(requestCode==CHANGE_PERSON_STATE){
			changeUserInfoShowTv.setText("换个状态换个心情");
		}
	}
	
	private void initView() {
		changeUserInfoEv=(EditText)findViewById(R.id.change_user_info_ev);
		changeUserInfoShowTv=(TextView)findViewById(R.id.change_user_info_show_tv);
		
		actionbarFirstBtn=(Button)actionbarView.findViewById(R.id.actionbar_first_btn);
		actionbarFirstBtn.setVisibility(View.VISIBLE);
		actionbarFirstBtn.setText(R.string.save_user_info);
		actionbarFirstBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//case R.id.save_user_info_btn:
		case R.id.actionbar_first_btn:
			if(requestCode==CHANGE_NICKNAME){
				Intent data=new Intent();
				data.putExtra("returnInfo", changeUserInfoEv.getText().toString());
				Timber.d(changeUserInfoEv.getText().toString());
				setResult(Activity.RESULT_OK, data);
				this.finish();
			}else if(requestCode==CHANGE_PERSON_STATE){
				Intent data=new Intent();
				data.putExtra("returnInfo", changeUserInfoEv.getText().toString());
				Timber.d(changeUserInfoEv.getText().toString());
				setResult(Activity.RESULT_OK, data);
				this.finish();
			}
			
			break;

		default:
			break;
		}
	}
	
}
