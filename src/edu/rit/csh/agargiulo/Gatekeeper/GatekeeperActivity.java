package edu.rit.csh.agargiulo.Gatekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author Anthony Gargiulo <anthony@agargiulo.com>
 * 
 */
public class GatekeeperActivity extends FragmentActivity
{

	private HttpsConnector connector;

	/**
	 * Deletes the stored username and password from the preferences effectivly
	 * logging out of the app
	 */
	private void logout ()
	{
		// This is possible because awesomeness
		PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
				.remove("username").remove("password").remove("loggedin").commit();
		invalidateOptionsMenu();
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data)
	{
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String username = prefs.getString("username", "");
		if(username == "")
		{
			Log.e("Gatekeeper", "LoginActivity.onActivityResults: Invalid username");
			PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
					.remove("loggedin").commit();
		} else
		{
			Log.d("gatekeeper", username);
			invalidateOptionsMenu();
			connector.popDoor(4);
		}

	}

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		connector = new HttpsConnector(this);
		if(!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(
				"loggedin", false))
		{
			startActivity(new Intent(this, LoginActivity.class));
		}
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
		if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(
				"loggedin", false))
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

	public void update (String json)
	{
		Log.d("GatekeeperActivity.update(s): ", json);
	}

}
