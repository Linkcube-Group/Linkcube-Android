package me.linkcube.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * 信息界面
 * 
 * 
 */

public class InfoDlgActivity extends Activity implements OnClickListener{

	/*
	class InfoDlgExc
	{
		@SuppressWarnings("rawtypes")
		Class Parent;
		//Activity Parent;
		Context Child;
		public InfoDlgExc(Class parent,Context child)
		//public InfoDlgExc(Activity parent,Context child)
		{
			Parent = parent;
			Child = child;
		}
	}
	
	final InfoDlgExc dlgExc[]={{MainTabActivity.Class,InfoDlgActivity.this}};
	*/
	//public Intent(android.content.Context packageContext, java.lang.Class cls);
	private Button btnConfirm;
	private TextView tvInfoTitle;
	private TextView tvInfoDetail;
	private ImageView imClose;
	
	protected void BindUIHandle()
	{
		btnConfirm = (Button)findViewById(R.id.btn_dlg_ok);
		btnConfirm.setOnClickListener(this);

		tvInfoTitle = (TextView)findViewById(R.id.tv_dlginfotitle);
		tvInfoDetail = (TextView)findViewById(R.id.tv_dlginfodetail);
		
		imClose = (ImageView)findViewById(R.id.im_close);
		imClose.setOnClickListener(this);
		
		Bundle extras = getIntent().getExtras();
		String titleText = extras.getString(LinkDefines.Data_Nickname);
		String detailText = extras.getString("infodetail");
		tvInfoTitle.setText(titleText);
		tvInfoDetail.setText(detailText);
	}
	
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.text);
        getWindow().setBackgroundDrawable(new BitmapDrawable());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_infodlg);
        
        BindUIHandle();
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();  
        //data.putExtra("bookname", "haha");  
        //data.putExtra("booksale", str_booksale);  
        //请求代码可以自己设置，这里设置成20  
		if(v==imClose)
		{
			setResult(LinkDefines.RES_No, intent);
	        finish();
		}
		else if(v==btnConfirm)
		{
			setResult(LinkDefines.RES_Yes, intent);
	        finish();
		}
	}
}
