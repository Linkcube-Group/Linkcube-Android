package me.linkcube.app.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

/**
 * AlertDialog的创建类
 * 
 * @author Ervin
 * 
 */
public final class AlertUtils {

	private AlertUtils() {

	}

	/**
	 * 弹出Toast提示，默认提示时间为Toast.LENGTH_SHORT
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showToast(final Context context, String msg) {
		showToast(context, msg, Toast.LENGTH_SHORT);
	}

	/**
	 * 弹出Toast提示
	 * 
	 * @param context
	 * @param msg
	 * @param duration
	 */
	public static void showToast(final Context context, String msg, int duration) {
		Toast.makeText(context, msg, duration).show();
	}

	/**
	 * 创建一个AlertDialog，有“确定”按钮，对此按钮按钮事件不监听。
	 * 
	 * @param context
	 * @param msg
	 * @param title
	 * @return AlertDialog
	 */
	public static AlertDialog showAlert(final Context context,
			final String msg, final String title) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	/**
	 * 创建一个AlertDialog，有“确定”按钮，对此按钮点击事件不监听。
	 * 
	 * @param context
	 * @param msgId
	 * @param titleId
	 * @return AlertDialog
	 */
	public static AlertDialog showAlert(final Context context, final int msgId,
			final int titleId) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(titleId);
		builder.setMessage(msgId);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	/**
	 * 创建一个AlertDialog，有“确定”按钮，对此按钮事件监听。
	 * 
	 * @param context
	 * @param msgId
	 * @param titleId
	 * @param okListener
	 * @return AlertDialog
	 */
	public static AlertDialog showAlert(final Context context, final int msgId,
			final int titleId, final DialogInterface.OnClickListener okListener) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(titleId);
		builder.setMessage(msgId);
		builder.setPositiveButton(android.R.string.ok, okListener);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	/**
	 * 创建一个AlertDialog，有“确定”按钮，对此按钮事件监听。
	 * 
	 * @param context
	 * @param msg
	 * @param title
	 * @param okListener
	 * @return AlertDialog
	 */
	public static AlertDialog showAlert(final Context context,
			final String msg, final String title,
			final DialogInterface.OnClickListener okListener) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(android.R.string.ok, okListener);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	/**
	 * 创建一个AlertDialog，有“确定”和“取消”按钮，对此两个按钮按钮事件监听。
	 * 
	 * @param context
	 * @param msgId
	 * @param titleId
	 * @param okListener
	 * @param cancleListener
	 * @return AlertDialog
	 */
	public static AlertDialog showAlert(final Context context, final int msgId,
			final int titleId,
			final DialogInterface.OnClickListener okListener,
			final DialogInterface.OnClickListener cancleListener) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(titleId);
		builder.setMessage(msgId);
		builder.setPositiveButton(android.R.string.ok, okListener);
		builder.setNegativeButton(android.R.string.cancel, cancleListener);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	/**
	 * 创建一个AlertDialog，有“确定”和“取消”按钮，对此两个按钮按钮事件监听。
	 * 
	 * @param context
	 * @param msg
	 * @param title
	 * @param okListener
	 * @param cancleListener
	 * @return AlertDialog
	 */
	public static AlertDialog showAlert(final Context context,
			final String msg, final String title,
			final DialogInterface.OnClickListener okListener,
			final DialogInterface.OnClickListener cancleListener) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(android.R.string.ok, okListener);
		builder.setNegativeButton(android.R.string.cancel, cancleListener);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	/**
	 * 创建一个AlertDialog，有“按钮左”和“按钮右”按钮，并可对这两个按钮的文字进行配置，同时两个按钮按钮事件监听。
	 * 
	 * @param context
	 * @param msg
	 * @param title
	 * @param leftId
	 * @param rightId
	 * @param leftListener
	 * @param rightListener
	 * @return AlertDialog
	 */
	public static AlertDialog showAlert(final Context context, final int msg,
			final int title, final int leftId, final int rightId,
			final DialogInterface.OnClickListener leftListener,
			final DialogInterface.OnClickListener rightListener) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(leftId, leftListener);
		builder.setNegativeButton(rightId, rightListener);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	/**
	 * 创建一个AlertDialog，有“按钮左”和“按钮右”按钮，并可对这两个按钮的文字进行配置，同时两个按钮按钮事件监听。
	 * 
	 * @param context
	 * @param msg
	 * @param title
	 * @param left
	 * @param right
	 * @param leftListener
	 * @param rightListener
	 * @return AlertDialog
	 */
	public static AlertDialog showAlert(final Context context,
			final String msg, final String title, final String left,
			final String right,
			final DialogInterface.OnClickListener leftListener,
			final DialogInterface.OnClickListener rightListener) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setPositiveButton(left, leftListener);
		builder.setNegativeButton(right, rightListener);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	/**
	 * 创建一个AlertDialog，有用户自定义视图和一个“确定”按钮，并对此按钮进行监听。
	 * 
	 * @param context
	 * @param title
	 * @param view
	 * @param okListener
	 * @return AlertDialog
	 */
	public static AlertDialog showAlert(final Context context,
			final String title, final View view,
			final DialogInterface.OnClickListener okListener) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setView(view);
		builder.setPositiveButton(android.R.string.ok, okListener);
		// builder.setCancelable(true);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	/**
	 * 创建一个AlertDialog，有用户自定义视图和一个“取消”按钮，并此按钮进行监听。
	 * 
	 * @param context
	 * @param title
	 * @param view
	 * @param cancleListener
	 * @return AlertDialog
	 */
	public static AlertDialog showAlert(final Context context,
			final String title, final View view,
			final DialogInterface.OnCancelListener cancleListener) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setView(view);
		// builder.setCancelable(true);
		builder.setOnCancelListener(cancleListener);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	/**
	 * 新建一个AlertDialog，有用户自定义视图、一个“按钮左”和一个“按钮右”，并可对这两个按钮的文字进行配置，并对这两个按钮进行监听。
	 * 
	 * @param context
	 * @param title
	 * @param view
	 * @param left
	 * @param right
	 * @param leftListener
	 * @param rightListener
	 * @return AlertDialog
	 */
	public static AlertDialog showAlert(final Context context,
			final String title, final View view, final String left,
			final String right,
			final DialogInterface.OnClickListener leftListener,
			final DialogInterface.OnClickListener rightListener) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setView(view);
		builder.setPositiveButton(left, leftListener);
		builder.setNegativeButton(right, rightListener);
		// builder.setCancelable(false);
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	/**
	 * 新建一个AlertDialog，有用户自定义视图、一个“确定”按钮和一个“取消”按钮，并对这两个按钮进行监听。
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param view
	 * @param okListener
	 * @param cancleListener
	 * @return AlertDialog
	 */
	public static AlertDialog showAlert(final Context context,
			final String title, final String msg, final View view,
			final DialogInterface.OnClickListener okListener,
			final DialogInterface.OnClickListener cancleListener) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setView(view);
		builder.setPositiveButton(android.R.string.ok, okListener);
		builder.setNegativeButton(android.R.string.cancel, cancleListener);
		// builder.setCancelable(true);
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (cancleListener != null) {
					cancleListener.onClick(dialog, 0);
				}
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	/**
	 * 创建带有单选的AlertDialog
	 * 
	 * @param context
	 * @param title
	 * @param itemsId
	 * @param checkedId
	 * @param listener
	 * @param okListener
	 * @return
	 */
	public static AlertDialog showSingleChoiceAlert(final Context context,
			final String title, final int itemsId, final int checkedId,
			final DialogInterface.OnClickListener listener) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setSingleChoiceItems(itemsId, checkedId, listener);
		// builder.setPositiveButton(R.string.ok, okListener);
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}

	/**
	 * 创建带有单选的AlertDialog
	 * 
	 * @param context
	 * @param title
	 * @param itemsId
	 * @param checkedId
	 * @param listener
	 * @param okListener
	 * @return
	 */
	public static AlertDialog showSingleChoiceAlert(final Context context,
			final String title, final CharSequence[] itemsId,
			final int checkedId, final DialogInterface.OnClickListener listener) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return null;
		}

		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setSingleChoiceItems(itemsId, checkedId, listener);
		// builder.setPositiveButton(R.string.ok, okListener);
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
		return alert;
	}
}
