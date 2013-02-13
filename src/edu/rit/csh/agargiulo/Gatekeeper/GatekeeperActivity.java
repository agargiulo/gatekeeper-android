package edu.rit.csh.agargiulo.Gatekeeper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * @author Anthony Gargiulo <anthony@agargiulo.com>
 * 
 */
public class GatekeeperActivity extends Activity
{
	class AlertOnClickListener implements DialogInterface.OnClickListener
	{

		@Override
		public void onClick (DialogInterface dialog, int which)
		{
			switch(which)
			{
			case DialogInterface.BUTTON_POSITIVE:
				startActivity(new Intent(getApplicationContext(), LoginActivity.class));
			default:
				break;
			}
		}

	}

	private HttpsConnector connector;

	private SharedPreferences prefs;

	private int getColorFromState (String doorState)
	{
		int color;
		if(doorState.equals("unknown"))
		{
			color = Color.GRAY;
		} else if(doorState.equals("unlocked"))
		{
			color = Color.RED;
		} else if(doorState.equals("locked"))
		{
			color = Color.GREEN;
		} else
		{
			color = Color.MAGENTA;
		}
		return color;
	}

	private int getId (int doorId)
	{
		switch(doorId)
		{
		/*
		case 1:
			return R.id.door_1;
		case 2:
			return R.id.door_2;
		case 3:
			return R.id.door_3;
			*/
		case 4:
			return R.id.door_4;
		case 5:
			return R.id.door_5;
		case 6:
			return R.id.door_6;
		case 7:
			return R.id.door_7;
		case 8:
			return R.id.door_8;
		case 9:
			return R.id.door_9;
		default:
			return -1;
		}
	}

	/**
	 * Deletes the stored username and password from the preferences effectivly
	 * logging out of the app
	 */
	private void logout ()
	{
		// This is possible because awesomeness
		prefs.edit().remove("username").remove("password").remove("loggedin").commit();
		invalidateOptionsMenu();
		Log.d("logout", "logged out user");
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
			prefs.edit().remove("loggedin").commit();
		} else
		{
			Log.d("gatekeeper", username);
			invalidateOptionsMenu();
		}

	}

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		connector = new HttpsConnector(this);
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(!prefs.getBoolean("loggedin", false))
		{
			startActivityForResult(new Intent(this, LoginActivity.class), 0);
		}
		connector.getAllDoors();
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
		if(prefs.getBoolean("loggedin", false))
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

	public void popLock (View view)
	{
		int doorId;
		switch(view.getId())
		{
		case R.id.door_4:
			doorId = 4;
			break;
		case R.id.door_5:
			doorId = 5;
			break;
		case R.id.door_6:
			doorId = 6;
			break;
		case R.id.door_7:
			doorId = 7;
			break;
		case R.id.door_8:
			doorId = 8;
			break;
		case R.id.door_9:
			doorId = 9;
			break;
		default:
			doorId = -1;
		}
		connector.popDoor(doorId);

	}

	public void update (String jsonstr)
	{
		JSONObject obj;
		int doorId;
		String doorState, doorName;
		Button tempButton;
		AlertDialog.Builder dialogBuild;
		AlertDialog dialog;
		// RelativeLayout relLayout = (RelativeLayout)
		// findViewById(R.id.gatekeeper_main_screen);
		try
		{
			obj = new JSONObject(jsonstr);
			Log.d("GatekeeperActivity.update(s): ", obj.toString(3));
			if(obj.has("response") && !obj.getString("response").equals("null"))
			{
				// all_doors or door_state/id was called
				JSONArray response = obj.getJSONArray("response");
				for(int i = 0; i < response.length(); i ++)
				{
					doorState = response.getJSONObject(i).getString("state");
					doorId = response.getJSONObject(i).getInt("id");
					doorName = response.getJSONObject(i).getString("name");
					tempButton = (Button) findViewById(getId(doorId));
					tempButton.setText(doorName + ":" + doorState);
					// tempButton.setBackground
					// tempButton.setBackgroundColor(getColorFromState(doorState));
					tempButton.setTextColor(getColorFromState(doorState));
				}

			} else
			{
				// We did a lock/pop/unlock opperation
				if(obj.getString("success").equals("false"))
				{
					String errorMessage = obj.getString("error")
							+ "\nGo back to the log in screen?";

					dialogBuild = new AlertDialog.Builder(GatekeeperActivity.this).setTitle(
							obj.getString("error_type")).setMessage(errorMessage);
					dialogBuild.setPositiveButton("Yes, please", new AlertOnClickListener());
					dialogBuild.setNegativeButton("No thanks", new AlertOnClickListener());
					dialog = dialogBuild.create();
					dialog.show();
				}

			}
		} catch(JSONException je)
		{
			Log.e(this.getClass().toString(), je.getMessage(), je);
			return;
		}
	}
}
