package me.linkcube.app.core.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import me.linkcube.app.core.Const;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.DownloadFileEntity;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import me.linkcube.app.core.Const.DownloadAppConst;

public class DownloadAppManager {
	// SparseArray是android中替代Hashmap的类,可以提高效率
	private SparseArray<DownloadFileEntity> downloadFiles = new SparseArray<DownloadFileEntity>();
	// 用来管理所有下载任务
	private ArrayList<DownloadTask> taskList = new ArrayList<DownloadTask>();
	private Handler mHandler;
	private final static Object syncObj = new Object();
	private static DownloadAppManager instance;
	private ExecutorService executorService;

	private DownloadAppManager() {
		// 最多只能同时下载3个任务，其余的任务排队等待
		executorService = Executors.newFixedThreadPool(1);
	}

	public static DownloadAppManager getInstance() {
		if (null == instance) {
			synchronized (syncObj) {
				instance = new DownloadAppManager();
			}
			return instance;
		}
		return instance;
	}

	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}

	// 开始下载，创建一个下载线程
	public void startDownload(DownloadFileEntity file) {
		downloadFiles.put(file.downloadID, file);
		DownloadTask task = new DownloadTask(file.downloadID);
		taskList.add(task);
		executorService.submit(task);
	}

	public void stopAllDownloadTask() {
		while (taskList.size() != 0) {
			DownloadTask task = taskList.remove(0);
			// 可以在这里做其他的处理
			task.stopTask();
		}
		// 会停止正在进行的任务和拒绝接受新的任务
		executorService.shutdownNow();

	}

	// 下载任务
	class DownloadTask implements Runnable {

		private boolean isWorking = false;
		private int downloadId;

		public DownloadTask(int id) {
			this.isWorking = true;
			this.downloadId = id;
		}

		public void stopTask() {
			this.isWorking = false;
		}

		// 更新listview中对应的item
		public void update(DownloadFileEntity downloadFile) {
			Message msg = mHandler.obtainMessage();
			if (downloadFile.totalSize == downloadFile.downloadSize)
				downloadFile.downloadState = DownloadAppConst.DOWNLOAD_STATE_FINISH;
			msg.obj = downloadFile;
			msg.sendToTarget();

		}

		public void run() {
			DownloadFileEntity downloadFile = downloadFiles.get(downloadId);
			downloadFile.downloadState = DownloadAppConst.DOWNLOAD_STATE_DOWNLOADING;

			String downloadPath = Environment.getExternalStorageDirectory()
					.getPath() + "/linkcube";
			File file = new File(downloadPath);
			if (!file.exists()) {
				file.mkdir();
			}
			HttpGet httpGet = new HttpGet(downloadFile.downloadUrl);

			try {
				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(httpGet);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					InputStream iStream = httpResponse.getEntity().getContent();
					int fileLength = (int) httpResponse.getEntity()
							.getContentLength();
					// 开始下载apk
					FileOutputStream fos = new FileOutputStream(downloadPath
							+ "/" + downloadFile.downloadApkName);
					byte[] buffer = new byte[20480];
					int count = 0, downLoadFileSize = 0;
					while ((count = iStream.read(buffer)) != -1) {
						fos.write(buffer, 0, count);
						downLoadFileSize += count;
						downloadFile.downloadSize = (downLoadFileSize * 100)
								/ fileLength;
						Timber.d("downloadFile.downloadSize:"
								+ downloadFile.downloadSize);
						this.update(downloadFile);
					}
					fos.close();
					iStream.close();
					downloadFiles.remove(downloadFile.downloadID);
					taskList.remove(this);
					isWorking = false;

				} else {
					downloadError(downloadFile);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				downloadError(downloadFile);
			} catch (IOException e) {
				e.printStackTrace();
				downloadError(downloadFile);
			}

		}

		private void downloadError(DownloadFileEntity downloadFile) {
			downloadFile.downloadState = DownloadAppConst.DOWNLOAD_STATE_PAUSE;
			this.update(downloadFile);
			downloadFiles.remove(downloadId);
			isWorking = false;
		}
	}
}
