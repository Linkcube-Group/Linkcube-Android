package me.linkcube.app.ui.setting;

import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.widget.ListView;
import me.linkcube.app.R;
import me.linkcube.app.core.Const.DownloadAppConst;
import me.linkcube.app.core.entity.AppFileEntity;
import me.linkcube.app.ui.BaseActivity;

public class RelevantAppActivity extends BaseActivity {

	private SparseArray<AppFileEntity> appList = new SparseArray<AppFileEntity>();

	private ListView listView;

	private RelevantAppListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.relevant_app_activity);
		configureActionBar(R.string.relevant_app);
		initData();
		initView();
	}

	private void initData() {
		for (int i = 0; i < DownloadAppConst.RELEVANT_APP_NAME.length; i++) {
			AppFileEntity app = new AppFileEntity();
			app.name = DownloadAppConst.RELEVANT_APP_NAME[i];
			app.downloadUrl = DownloadAppConst.RELEVANT_APP_URL[i];
			app.size = 100;
			app.id = i;
			app.appIcon = null;
			app.downloadApkName=DownloadAppConst.RELEVANT_SAVE_APK_NAME[i];
			getBitmap(DownloadAppConst.RELEVANT_APP_ICON[i], i);
			app.downloadState = DownloadAppConst.DOWNLOAD_STATE_NORMAL;
			app.downloadSize = 0;
			appList.put(app.id, app);
		}

	}

	private void initView() {
		listView = (ListView) this.findViewById(R.id.listview);
		adapter = new RelevantAppListAdapter(this, appList);
		adapter.setListView(listView);
		listView.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// DownloadAppManager.getInstance().stopAllDownloadTask();
	}

	public void getBitmap(final String s, final int position) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				Bitmap bitmap = null;
				try {
					URL url = new URL(s);
					bitmap = BitmapFactory.decodeStream(url.openStream());
					Message msg = new Message();
					msg.arg1 = position;
					msg.obj = bitmap;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Bitmap iconBitmap = (Bitmap) msg.obj;
			AppFileEntity app = appList.get(msg.arg1);
			app.appIcon = iconBitmap;
			appList.setValueAt(msg.arg1, app);
			adapter.notifyDataSetChanged();
		}

	};

}
