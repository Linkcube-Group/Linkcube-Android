package me.linkcube.app.ui;

import me.linkcube.app.widget.AlertUtils;

import android.app.ProgressDialog;

public class DialogFragmentActivity extends BaseFragmentActivity {

	protected ProgressDialog progressDialog = null;

	/**
	 * 显示进度框
	 */
	protected void showProgressDialog(String message) {
		if (progressDialog == null)
			progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(true);
		progressDialog.setMessage(message);
		progressDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	protected void dissmissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * 主UI上的Toast展示
	 * 
	 * @param msg
	 */
	protected void showToast(final String msg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertUtils.showToast(mActivity, msg);
			}
		});
	}

	/**
	 * 主UI上的AlertDialog展示，带有一个确定按钮，并对词按钮无监听
	 * 
	 * @param msg
	 * @param title
	 */
	protected void showAlert(final String msg, final String title) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertUtils.showAlert(mActivity, msg, title);
			}
		});
	}

}
