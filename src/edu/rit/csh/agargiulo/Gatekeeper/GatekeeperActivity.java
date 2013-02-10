package edu.rit.csh.agargiulo.Gatekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class GatekeeperActivity extends FragmentActivity
{

	public static final String PREFS_NAME = "GatekeeperPrefsFile";

	private boolean loggedin = false;

	private void logout ()
	{
		loggedin = false;
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.remove("username").remove("password").commit();
		invalidateOptionsMenu();
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode,
			Intent data)
	{
		loggedin = true;
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String username = prefs.getString("username", "");
		if(username == "")
		{
			Log.e("Gatekeeper",
					"LoginActivity.onActivityResults: Invalid username");
		}
		Log.d("gatekeeper", username);
		invalidateOptionsMenu();

	}

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.menu_login:
			// login();
			startActivityForResult(new Intent(this, LoginActivity.class), 0);
			return true;
		case R.id.menu_logout:
			logout();
			return true;
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu)
	{
		boolean result = super.onPrepareOptionsMenu(menu);
		if(loggedin)
		{
			menu.findItem(R.id.menu_login).setVisible(false);
			menu.findItem(R.id.menu_logout).setVisible(true);
		} else
		{
			menu.findItem(R.id.menu_logout).setVisible(false);
			menu.findItem(R.id.menu_login).setVisible(true);
		}
		return result;
	}

	public void update (String s)
	{
		Log.d("GatekeeperActivity.update(s): ", s);
	}

}
