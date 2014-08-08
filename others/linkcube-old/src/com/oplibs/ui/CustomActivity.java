package com.oplibs.ui;

import me.linkcube.client.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;


/*
 * 定制界面
 * 
 * 
 */
public class CustomActivity extends Activity{
	
	enum CustomActivityStatus
	{
		Custom_Normal,
		Custom_Refreshing
	};
	
	protected void BindUIHandle()
	{

	}
	
	protected void UpdateUI(CustomActivityStatus status)
	{
		switch(status)
		{
		case Custom_Normal:
			break;
		case Custom_Refreshing:
			break;
		}
	}
	
	protected void GetInitData()
	{
		
	}

	protected void BindServiceListener()
	{
		
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_aboutus);
        
        BindServiceListener();
        BindUIHandle();
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (resultCode)
		{ //resultCode为回传的标记，我在B中回传的是RESULT_OK
		case 0:
			{
				Bundle b=data.getExtras();  //data为B中回传的Intent
			}
			break;
		case 1:
			{
				Bundle b=data.getExtras();  //data为B中回传的Intent
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);  
	}
	
	/* 传递参数
	传递字符串
	Intent intent=new Intent();
	intent.setClass(MainActivity.this, SecondActivity.class);
	intent.putExtra("str", "传递参数—字符串");
	
	传递整数
	Bundle bundle=new Bundle();
	bundle.putInt("key", 1);
	
	传递对象
	Book book=new Book();
　	book.setName(bookName);
　　	book.setAuthor(author);
　　	book.setId(id);
　　	bundle.putSerializable("book", book);

　　	intent.putExtras(bundle);

	startActivity(intent);
	
	获取参数
	Intent intent=getIntent();
	Bundle bundle=intent.getExtras();
	String str=bundle.getString("str");
	Bundle bundle=intent.getExtras();
	int value =bundle.getSerializable("key");
	*/
}
