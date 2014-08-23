package me.linkcube.client;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.oplibs.services.XMPPUtils;
import com.oplibs.syncore.SyncMgr;
import com.oplibs.syncore.UserRoster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 
 * 
 * 个人信息编辑
 */

public class PersonalInfoEditActivity extends Activity{
	
	private ListView lvPersonalInfo;
	private InfoItemAdapter infoItemAdapter;
	private Button btnChangeInfo;
	
	private ImageView imAvatar;
	
	private UserRoster curRoster;
	
	private byte[] avatarBuf=null;
	private String newAvatarPath="";
	private boolean bUpdateAvatar=false;
	
	protected void RefreshItems()
	{
		infoItemAdapter.ResetData();
    	infoItemAdapter.addNew(new KeyValuePair("昵称：",curRoster.NickName,true,LinkDefines.REQ_NickName));
    	infoItemAdapter.addNew(new KeyValuePair("连酷ID：",XMPPUtils.GetDisplayID(curRoster.JID),false,LinkDefines.REQ_JID));
    	infoItemAdapter.addNew(new KeyValuePair("性别：",curRoster.userGendre,true,LinkDefines.REQ_Gendre));
    	infoItemAdapter.addNew(new KeyValuePair("生日：", curRoster.birthDate,true,LinkDefines.REQ_BirthDate));
    	infoItemAdapter.addNew(new KeyValuePair("签名档：",curRoster.PS,true,LinkDefines.REQ_PS));
    	infoItemAdapter.addNew(new KeyValuePair("连接密码：",curRoster.connectCode,true,LinkDefines.REQ_ConnectCode));
    	infoItemAdapter.notifyDataSetChanged();
	}
	protected void UpdateUIData()
	{
		SyncMgr syncmgr = SyncMgr.GetInstance();
		curRoster = new UserRoster(syncmgr.signedUser);
		RefreshItems();
	}
	
	private OnClickListener avatarClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			 Intent intent = new Intent(); 
             intent.setType("image/*"); 
             intent.setAction(Intent.ACTION_GET_CONTENT);  
             startActivityForResult(intent, LinkDefines.REQ_SelectAvatar); 
		}
	};
	
	private OnClickListener changeInfoListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			SyncMgr syncmgr = SyncMgr.GetInstance();
			LinkcubeApplication.getConfig(PersonalInfoEditActivity.this).updatePrefs();
			if(syncmgr.ChangePersonalCard(curRoster))
            {
				if(bUpdateAvatar)
				{
					if(!syncmgr.UploadAvatar(newAvatarPath))
					{
						Toast.makeText(PersonalInfoEditActivity.this, "个人头像修改失败！,请检查网络", Toast.LENGTH_SHORT).show();
					}
					else
					{
						bUpdateAvatar = false;
					}
				}
				finish();
            }
			else
			{
				Toast.makeText(PersonalInfoEditActivity.this, "个人信息修改失败！,请检查网络", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private OnClickListener editInfoListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			InfoItemAdapter.InfoItemViewHolder holder = (InfoItemAdapter.InfoItemViewHolder)v.getTag();
			KeyValuePair pair = infoItemAdapter.fieldList.get(holder.index);
			switch(pair.itemType)
			{
			case LinkDefines.REQ_NickName:
				{
					Intent intent = new Intent (PersonalInfoEditActivity.this,InfoItemEditActivity.class);	
					Bundle bundle=new Bundle();
					bundle.putInt("type", LinkDefines.REQ_PS);
					bundle.putString(LinkDefines.Data_Title, "请输入昵称");
					bundle.putString(LinkDefines.Data_CurValue,pair.fv);
					intent.putExtras(bundle);
					startActivityForResult(intent, LinkDefines.REQ_NickName);
				}
				break;
			case LinkDefines.REQ_Gendre:
			{
	    		new AlertDialog.Builder(PersonalInfoEditActivity.this).setTitle("性别").setIcon(
	    			     android.R.drawable.ic_dialog_info).setSingleChoiceItems(
	    			     new String[] { "男", "女" }, 0,
	    			     new DialogInterface.OnClickListener() {
	    			      @Override
						public void onClick(DialogInterface dialog, int which) {
	    			    	  curRoster.isWoman = (which==1);
	    			    	  if(which==1)
	    			    	  {
	    			    		  curRoster.userGendre = "女"; 
	    			    	  }
	    			    	  else
	    			    	  {
	    			    		  curRoster.userGendre = "男";
	    			    	  }
	    			    	  RefreshItems();
	    			       dialog.dismiss();
	    			      }
	    			     }).setNegativeButton("取消", null).show();
			}
				break;
			case LinkDefines.REQ_BirthDate:
			{
				DatePickerDialog dialog = new DatePickerDialog(PersonalInfoEditActivity.this, dateListener, 1990,0,01);
				dialog.show();
			}
				break;
			case LinkDefines.REQ_PS:
				{
					Intent intent = new Intent (PersonalInfoEditActivity.this,InfoItemEditMLActivity.class);	
					Bundle bundle=new Bundle();
					bundle.putInt("type", LinkDefines.REQ_PS);
					bundle.putString(LinkDefines.Data_Title, "请输入签名档");
					bundle.putString(LinkDefines.Data_CurValue,pair.fv);
					intent.putExtras(bundle);
					startActivityForResult(intent, LinkDefines.REQ_PS);
				}
				break;
			case LinkDefines.REQ_ConnectCode:
				{
					Intent intent = new Intent (PersonalInfoEditActivity.this,InfoItemEditActivity.class);	
					//intent.putExtra(LinkDefines.Data_Title, "请输入六位数字连接密码");
					Bundle bundle=new Bundle();
					bundle.putInt("type", LinkDefines.REQ_ConnectCode);
					bundle.putString(LinkDefines.Data_Title, "请输入六位数字作为您能的连接密码");
					bundle.putString(LinkDefines.Data_CurValue,pair.fv);
					intent.putExtras(bundle);
					startActivityForResult(intent, LinkDefines.REQ_ConnectCode);
				}
				break;
			}
			return;
		}
	};
	
	DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener()
	{  
		@Override
		public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth)
		{
			//curRoster.birthDate = new Date()
			SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");
			Date newDate = new Date();
			newDate.setYear(year-1900);
			newDate.setMonth(monthOfYear);
			newDate.setDate(dayOfMonth);
			curRoster.birthDate = sdf.format(newDate);
			RefreshItems();
			return;
		}  
	};
	
	protected void BindUIHandle()
	{
		imAvatar = (ImageView)findViewById(R.id.iv_avatar_change);
		imAvatar.setOnClickListener(avatarClickListener);
		
		btnChangeInfo = (Button)findViewById(R.id.btn_change_info);
		btnChangeInfo.setOnClickListener(changeInfoListener);
		
		lvPersonalInfo = (ListView)findViewById(R.id.lv_infoedit);
		infoItemAdapter = new InfoItemAdapter(this,null);
    	lvPersonalInfo.setAdapter(infoItemAdapter);
    	
	   avatarBuf=getIntent().getByteArrayExtra(LinkDefines.Data_AvatarBuf); 
	   if(avatarBuf!=null)
       {
		   ByteArrayInputStream bais = new ByteArrayInputStream(avatarBuf);  
		   Bitmap bitmap = BitmapFactory.decodeStream(bais);
		   imAvatar.setImageBitmap(bitmap);
       }
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(SampleList.THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.text);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_personalinfoedit);
        BindUIHandle();
        UpdateUIData();
    }
    
    @Override 
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
    	switch(requestCode)
    	{
    	case LinkDefines.REQ_SelectAvatar:
	    	{
	    		if (resultCode == LinkDefines.RES_Yes&&data!=null) 
	    		{
	    			SyncMgr syncmgr = SyncMgr.GetInstance();
	                Uri uri = data.getData(); 
	                String[] proj = { MediaColumns.DATA };
	                Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);
	                int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaColumns.DATA);
	                actualimagecursor.moveToFirst();
	                newAvatarPath = actualimagecursor.getString(actual_image_column_index);
	                
	                ContentResolver cr = this.getContentResolver(); 
	                try { 
	                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri)); 
	                    imAvatar.setImageBitmap(bitmap); 
	                    bUpdateAvatar = true;
	                } catch (FileNotFoundException e) { 
	                    Log.e("Select avatar Exception", e.getMessage(),e); 
	                }
	                
	            } 
	    	}
    		break;
    	case LinkDefines.REQ_NickName:
	    	{
	    		if(resultCode==LinkDefines.RES_Yes&&data!=null)
	    		{
	    			Bundle bundle=data.getExtras();
	    			if(bundle!=null)
					{
	    				String value = bundle.getString(LinkDefines.Data_Result);
	    				if(value!=null)
	    				{
	    					curRoster.NickName = value;
	    				}
	    				RefreshItems();
					}
	    		}
	    	}
    		break;
    	case LinkDefines.REQ_PS:
    	{
    		if(resultCode==LinkDefines.RES_Yes&&data!=null)
    		{
    			Bundle bundle=data.getExtras();
				curRoster.PS = bundle.getString(LinkDefines.Data_Result);
				RefreshItems();
    		}
    	}
    		break;
    	case LinkDefines.REQ_ConnectCode:
    	{
    		if(resultCode==LinkDefines.RES_Yes&&data!=null)
    		{
    			Bundle bundle=data.getExtras();
				curRoster.connectCode = bundle.getString(LinkDefines.Data_Result);
				RefreshItems();
    		}
    	}
    		break;
    	case LinkDefines.REQ_Gendre:

    		break;
    		default:
    			break;
    	}
        
        super.onActivityResult(requestCode, resultCode, data); 
    }  

    class KeyValuePair
    {
    	public String fn;
    	public String fv;
    	public boolean vi;
    	public int itemType;
    	
    	public KeyValuePair(String name,String value,boolean isvisible,int type)
    	{
    		fn = name;
    		fv = value;
    		vi = isvisible;
    		itemType = type;
    	}
    }

    class InfoItemAdapter extends BaseAdapter
    {
        private LayoutInflater mInflater;
        public List<KeyValuePair> fieldList = new ArrayList<KeyValuePair>();

        public InfoItemAdapter(Context context, List<BluetoothDevice> devlist)
        {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount()
        {
        	return fieldList.size();
        }

        @Override
        public Object getItem(int position)
        {
        	return fieldList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
        	return position;
        }

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			InfoItemViewHolder holder;
            if (convertView == null)
            {
                convertView = mInflater.inflate(R.layout.listcell_infoitem, null);
                holder = new InfoItemViewHolder();
                holder.tvFieldName = (TextView) convertView.findViewById(R.id.tv_fieldname);
                holder.tvFieldValue = (TextView) convertView.findViewById(R.id.tv_fieldvalue);
                //holder.imEditable = (ImageView)findViewById(R.id.iv_editable);
                holder.btnEdit = (Button)convertView.findViewById(R.id.btn_edit);
                boolean bvisible = fieldList.get(position).vi;
                if(bvisible)
                {
                    holder.btnEdit.setVisibility(View.VISIBLE);
                }
                else
                {
                	holder.btnEdit.setVisibility(View.INVISIBLE);
                }
                holder.index = position;
                
                convertView.setTag(holder);
                convertView.setOnClickListener(editInfoListener);
            }
            else
            {
                holder = (InfoItemViewHolder) convertView.getTag();
            }
            
            holder.tvFieldName.setText(fieldList.get(position).fn);
            holder.tvFieldValue.setText(fieldList.get(position).fv);
            
            convertView = convertView;

            return convertView;
        }

		public boolean ResetData(/*List<KeyValuePair> list*/)
		{
			fieldList.clear();
			return true;
		}
		
		public boolean addNew(KeyValuePair val)
		{
			fieldList.add(val);
			return true;
		}


        /* Define an item object, which is used to process the controls.*/
        public class InfoItemViewHolder {
                TextView tvFieldName;
                TextView tvFieldValue;
                Button btnEdit;
                ImageView imEditable;
                int index;
        }
    }
}
