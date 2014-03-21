package edu.rit.csh.agargiulo.Gatekeeper;

import android.app.Activity;
import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * @author Anthony Gargiulo <anthony@agargiulo.com>
 * 
 */
public class AboutActivity extends Activity
{
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}

	@Override
	public void onStart ()
	{
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop ()
	{
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}
}
