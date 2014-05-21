package me.linkcube.app.core.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import me.linkcube.app.core.Const;

public class DownloadNewApkHttpGet {

	private AppUpdateCallback appUpdateCallback;

	public DownloadNewApkHttpGet() {

	}

	public void downloadNewApkFile(final Context context,
			final String downloadURL) {
		Thread thread = new Thread() {

			@Override
			public void run() {
				String downloadPath = Environment.getExternalStorageDirectory()
						.getPath() + "/linkcube";
				File file = new File(downloadPath);
				if (!file.exists()) {
					file.mkdir();
				}
				HttpGet httpGet = new HttpGet(downloadURL);

				try {
					HttpResponse httpResponse = new DefaultHttpClient()
							.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						InputStream iStream = httpResponse.getEntity()
								.getContent();
						int fileLength = (int) httpResponse.getEntity()
								.getContentLength();
						appUpdateCallback.beforeApkDOwnload(fileLength);
						// 开始下载apk
						FileOutputStream fos = new FileOutputStream(
								downloadPath + "/" + Const.AppUpdate.APK_NAME);
						byte[] buffer = new byte[20480];
						int count = 0, downLoadFileSize = 0;
						while ((count = iStream.read(buffer)) != -1) {
							fos.write(buffer, 0, count);
							downLoadFileSize += count;
							appUpdateCallback.inApkDownload(downLoadFileSize);
						}
						fos.close();
						iStream.close();
						appUpdateCallback.afterApkDownload(0);
						// 安装apk
						installApk(context, downloadPath + "/"
								+ Const.AppUpdate.APK_NAME);
					} else {
						appUpdateCallback.FailureApkDownload(-1);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					appUpdateCallback.FailureApkDownload(-1);
				} catch (IOException e) {
					e.printStackTrace();
					appUpdateCallback.FailureApkDownload(-1);
				}
			}

		};
		thread.start();

	}

	/**
	 * 安装apk
	 * 
	 * @param filename
	 */
	private void installApk(Context context, String filename) {
		File file = new File(filename);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW); // 浏览网页的Action(动作)
		String type = "application/vnd.android.package-archive";
		intent.setDataAndType(Uri.fromFile(file), type); // 设置数据类型
		context.startActivity(intent);
	}

	public void setAppUpdateCallback(AppUpdateCallback appUpdateCallback) {
		this.appUpdateCallback = appUpdateCallback;
	}

	public interface AppUpdateCallback {
		public void beforeApkDOwnload(int fileLength);

		public void inApkDownload(int downLoadFileSize);

		public void afterApkDownload(int reFlag);

		public void FailureApkDownload(int reFlag);
	}

}
