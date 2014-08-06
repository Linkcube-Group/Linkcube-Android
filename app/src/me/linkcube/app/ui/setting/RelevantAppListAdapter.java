package me.linkcube.app.ui.setting;

import me.linkcube.app.R;
import me.linkcube.app.core.Const;
import me.linkcube.app.core.Const.DownloadAppConst;
import me.linkcube.app.core.entity.AppFileEntity;
import me.linkcube.app.core.entity.DownloadFileEntity;
import me.linkcube.app.core.update.DownloadAppManager;
import me.linkcube.app.core.update.DownloadNewApkHttpGet;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RelevantAppListAdapter extends BaseAdapter {
	private SparseArray<AppFileEntity> dataList = null;
	private LayoutInflater inflater = null;
	private Context mContext;
	private DownloadAppManager downloadManager;
	private ListView listView;

	public RelevantAppListAdapter(Context context,
			SparseArray<AppFileEntity> dataList) {
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.dataList = dataList;
		this.mContext = context;
		this.downloadManager = DownloadAppManager.getInstance();
		this.downloadManager.setHandler(mHandler);
	}

	public void setListView(ListView view) {
		this.listView = view;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// 改变下载按钮的样式
	private void changeBtnStyle(Button btn, boolean enable) {
		if (enable) {
			btn.setBackgroundResource(R.drawable.btn_pink_normal);
		} else {
			btn.setBackgroundResource(R.drawable.btn_pink_pressed);
		}
		btn.setEnabled(enable);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.relevant_app_list_item,
					null);
			holder.layout = (LinearLayout) convertView
					.findViewById(R.id.gamelist_item_layout);
			holder.titleLayout=(LinearLayout)convertView.findViewById(R.id.mode_item_layout);
			holder.icon = (ImageView) convertView.findViewById(R.id.app_icon);
			holder.name = (TextView) convertView.findViewById(R.id.app_name);
			holder.mode = (TextView) convertView
					.findViewById(R.id.relevant_app_mode);
			holder.size = (TextView) convertView.findViewById(R.id.app_size);
			holder.btn = (Button) convertView.findViewById(R.id.download_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final AppFileEntity app = dataList.get(position);
		
		if(app.name.equals("1")){
			holder.layout.setVisibility(View.GONE);
			holder.titleLayout.setVisibility(View.VISIBLE);
			holder.mode.setText("音乐模式相关app");
		}else if(app.name.equals("2")){
			holder.layout.setVisibility(View.GONE);
			holder.titleLayout.setVisibility(View.VISIBLE);
			holder.mode.setText("语音模式相关app");
		}else{
			holder.layout.setVisibility(View.VISIBLE);
			holder.titleLayout.setVisibility(View.GONE);
		holder.name.setText(app.name);
		holder.icon.setImageBitmap(dataList.get(position).appIcon);

		switch (app.downloadState) {
		case DownloadAppConst.DOWNLOAD_STATE_NORMAL:
			holder.btn.setText("下载");
			this.changeBtnStyle(holder.btn, true);
			break;
		case DownloadAppConst.DOWNLOAD_STATE_DOWNLOADING:
			holder.btn.setText("下载中");
			this.changeBtnStyle(holder.btn, false);
			break;
		case DownloadAppConst.DOWNLOAD_STATE_FINISH:
			holder.btn.setText("已下载");
			this.changeBtnStyle(holder.btn, false);
			break;
		case DownloadAppConst.DOWNLOAD_STATE_WAITING:
			holder.btn.setText("排队中");
			this.changeBtnStyle(holder.btn, false);
			break;
		}
		holder.btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DownloadFileEntity downloadFile = new DownloadFileEntity();
				downloadFile.downloadID = app.id;
				downloadFile.downloadUrl = app.downloadUrl;
				downloadFile.downloadState = DownloadAppConst.DOWNLOAD_STATE_WAITING;
				app.downloadState = DownloadAppConst.DOWNLOAD_STATE_WAITING;
				downloadFile.downloadSize = app.downloadSize;
				downloadFile.totalSize = app.size;
				holder.btn.setText("排队中");
				changeBtnStyle(holder.btn, false);
				downloadManager.startDownload(downloadFile);
			}
		});
		}
		return convertView;
	}

	static class ViewHolder {
		LinearLayout titleLayout;
		LinearLayout layout;
		ImageView icon;
		TextView name;
		TextView size;
		TextView mode;
		Button btn;
	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			DownloadFileEntity downloadFile = (DownloadFileEntity) msg.obj;
			AppFileEntity appFile = dataList.get(downloadFile.downloadID);
			appFile.downloadSize = downloadFile.downloadSize;
			appFile.downloadState = downloadFile.downloadState;

			// notifyDataSetChanged会执行getView函数，更新所有可视item的数据

			// notifyDataSetChanged();

			// 只更新指定item的数据，提高了性能
			updateView(appFile.id);
		}
	};

	// 更新指定item的数据
	private void updateView(int index) {
		int visiblePos = listView.getFirstVisiblePosition();
		int offset = index - visiblePos;

		// Log.e("", "index="+index+"visiblePos="+visiblePos+"offset="+offset);

		// 只有在可见区域才更新
		if (offset < 0)
			return;

		View view = listView.getChildAt(offset);
		final AppFileEntity app = dataList.get(index);
		ViewHolder holder = (ViewHolder) view.getTag();

		// Log.e("", "id="+app.id+", name="+app.name);

		holder.name.setText(app.name);
		holder.size.setText((app.downloadSize * 100.0f / app.size) + "%");

		switch (app.downloadState) {
		case DownloadAppConst.DOWNLOAD_STATE_DOWNLOADING:
			holder.btn.setText("下载中");
			this.changeBtnStyle(holder.btn, false);
			break;
		case DownloadAppConst.DOWNLOAD_STATE_FINISH:
			holder.btn.setText("已下载");
			this.changeBtnStyle(holder.btn, false);
			String downloadPath = Environment.getExternalStorageDirectory()
					.getPath() + "/linkcube";
			// 安装apk
			DownloadNewApkHttpGet.installApk(mContext, downloadPath + "/"
					+ "momo.apk");
			break;
		}

	}
}
