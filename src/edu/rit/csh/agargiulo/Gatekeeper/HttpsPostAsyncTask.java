package edu.rit.csh.agargiulo.Gatekeeper;

import android.app.Activity;
import android.app.ProgressDialog;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * @author Anthony Gargiulo <anthony@agargiulo.com>
 * 
 */
public class HttpsPostAsyncTask extends AsyncHttpResponseHandler
{
	private Activity activity;
	private ProgressDialog progress;
	private int doorID;

	/**
	 * 
	 */
	public HttpsPostAsyncTask (Activity callingActivity, int doorID)
	{
		activity = callingActivity;
		this.doorID = doorID;
	}

	@Override
	public void onFinish ()
	{
		super.onFinish();
		if (progress != null && progress.isShowing())
		{
			progress.cancel();
		}
	}

	@Override
	public void onStart ()
	{
		super.onStart();
		progress = new ProgressDialog(activity);
		progress.setTitle("\tThinking...");
		progress.setMessage("\tTalking to Gatekeeper...");
		progress.setCancelable(false);
		progress.show();
	}

	@Override
	public void onSuccess (String json)
	{
		super.onSuccess(json);
		if (activity != null)
		{
			((GatekeeperActivity) activity).update(json, doorID);
		}
	}

}
