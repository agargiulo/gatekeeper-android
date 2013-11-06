package edu.rit.csh.agargiulo.Gatekeeper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.analytics.tracking.android.EasyTracker;

public class WelcomeActivity extends Activity
{
	private SharedPreferences prefs;

	/**
	 * Start the about view. This is needed by the about button
	 */
	public void about (View view)
	{
		startActivity(new Intent(this, AboutActivity.class));
	}

	/**
	 * Start the login activity. This is needed by the login button
	 */
	public void login (View view)
	{
		startActivityForResult(new Intent(this, LoginActivity.class), 0);
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data)
	{
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String username = prefs.getString(Gatekeeper.PREF_USERNAME, null);
		if (username == null)
		{
			Log.e("Gatekeeper", "LoginActivity.onActivityResults: Invalid username");
			prefs.edit().remove(Gatekeeper.PREF_LOGGEDIN).commit();
		}
		else
		{
			startActivity(new Intent(getApplicationContext(), GatekeeperActivity.class));
			finish();
		}
	}

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (prefs.getBoolean(Gatekeeper.PREF_LOGGEDIN, false))
		{
			// User is already logged in, start the next activity
			startActivity(new Intent(getApplicationContext(), GatekeeperActivity.class));
			finish();
		}
	}

	@Override
	protected void onResume ()
	{
		super.onResume();
		setContentView(R.layout.activity_welcome);
	}

	@Override
	public void onStart ()
	{
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop ()
	{
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

}
