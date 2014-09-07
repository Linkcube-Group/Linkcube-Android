package me.linkcube.app.common.ui;

import java.io.Serializable;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * 
 * @author Ervin
 * 
 */
public class DialogFragment extends BaseFragment {

	private ProgressDialog progDialog = null;

	protected boolean isUsable() {
		return getActivity() != null;
	}

	@SuppressWarnings("unchecked")
	protected <V extends Serializable> V getSerializableExtra(final String name) {
		Activity activity = getActivity();
		if (activity != null)
			return (V) activity.getIntent().getSerializableExtra(name);
		else
			return null;
	}

	protected void showProgressDialog(String message) {
		if (progDialog == null)
			progDialog = new ProgressDialog(getActivity());
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage(message);
		progDialog.show();
	}

	protected void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

}
