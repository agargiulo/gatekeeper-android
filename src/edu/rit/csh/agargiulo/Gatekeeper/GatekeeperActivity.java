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

import com.google.analytics.tracking.android.EasyTracker;

/**
 * This is the class for the Main Activity of the Gatekeeper App
 * 
 * @author Anthony Gargiulo <anthony@agargiulo.com>
 */
public class GatekeeperActivity extends Activity
{
	class InvalidCredsOnClickListener implements DialogInterface.OnClickListener
	{

		/* (non-Javadoc)
		 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
		 */
		@Override
		public void onClick (DialogInterface dialog, int whichButton)
		{
			switch (whichButton)
			{
			case DialogInterface.BUTTON_POSITIVE:
				startActivity(new Intent(getApplicationContext(), LoginActivity.class));
				finish();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				logout();
				break;
			case DialogInterface.BUTTON_NEUTRAL:
				break;
			}
		}
	}

	private HttpsConnector connector;
	private SharedPreferences prefs;

	/**
	 * 
	 * @param doorState
	 *            can be one of unknown, unlocked, or locked
	 * @return Gray for unlocked, red for unlocked, or green for locked
	 */
	private int getColorFromState (String doorState)
	{
		int color;
		if (doorState.equals(Gatekeeper.D_STATE_UNKNOWN))
		{
			// #DDDDDD = Gray
			color = Color.parseColor(Gatekeeper.COLOR_GRAY);
		}
		else if (doorState.equals(Gatekeeper.D_STATE_UNLOCKED))
		{
			// #FF8080 = The red used on the web app
			color = Color.parseColor(Gatekeeper.COLOR_RED);
		}
		else if (doorState.equals(Gatekeeper.D_STATE_LOCKED))
		{
			// #80c080 = The green used on the web app
			color = Color.parseColor(Gatekeeper.COLOR_GREEN);
		}
		else
		{
			color = Color.MAGENTA;
		}
		return color;
	}

	private int getId (int doorId)
	{
		switch (doorId)
		{
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
		prefs.edit().remove(Gatekeeper.PREF_USERNAME).remove(Gatekeeper.PREF_PASSWORD);
		prefs.edit().remove(Gatekeeper.PREF_LOGGEDIN).commit();
		connector = null;
		startActivity(new Intent(this, WelcomeActivity.class));
		finish();

	}

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (connector == null)
		{
			connector = new HttpsConnector(this);
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
		switch (item.getItemId())
		{
		case R.id.menu_logout:
			logout();
			return true;
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		case R.id.menu_reload_doors:
			connector.getAllDoors();
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu)
	{
		boolean result = super.onPrepareOptionsMenu(menu);
		if (prefs.getBoolean(Gatekeeper.PREF_LOGGEDIN, false))
		{
			menu.findItem(R.id.menu_logout).setVisible(true);
			menu.findItem(R.id.menu_about).setVisible(true);
		}
		else
		{
			menu.findItem(R.id.menu_logout).setVisible(false);
			menu.findItem(R.id.menu_about).setVisible(false);
		}
		return result;
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

	public void popLock (View view)
	{
		((Button) view).setBackgroundColor(Color.parseColor(Gatekeeper.COLOR_RED));
		((Button) view).setEnabled(false);
		int doorId;
		switch (view.getId())
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

	public void update (String jsonstr, int door)
	{
		// The object generated from jsonstr
		JSONObject obj;
		JSONArray response;

		InvalidCredsOnClickListener alertListener;

		int doorId;
		String doorState, doorName;

		Button tempButton;

		AlertDialog.Builder dialogBuild;
		AlertDialog dialog;

		// These will be the values received from the JSON string
		String responseStr, errorStr, errorTypeStr;
		boolean success;

		if (jsonstr == null)
		{
			// This is a big issue, but please don't crash the app over it.
			dialogBuild = new AlertDialog.Builder(this);
			dialogBuild.setTitle("ERROR ERROR ERROR!!!1!!!One!!");
			dialogBuild.setMessage("Error talking to Gatekeeper");
			dialog = dialogBuild.create();
			dialog.show();
			return;
		}

		try
		{
			obj = new JSONObject(jsonstr);

			/*
			 * initialize these here to make life SO much better later on
			 */
			success = Boolean.valueOf(obj.getString(Gatekeeper.JSON_SUCCESS));
			responseStr = obj.getString(Gatekeeper.JSON_RESPONSE);
			errorStr = obj.getString(Gatekeeper.JSON_ERROR);
			errorTypeStr = obj.getString(Gatekeeper.JSON_ERROR_TYPE);

			if (!success)
			{
				// The command we tried to run failed.
				// Display a dialog to the user stating the error

				alertListener = new InvalidCredsOnClickListener();
				dialogBuild = new AlertDialog.Builder(this);
				dialogBuild.setTitle(errorTypeStr);
				if (errorTypeStr.equals(Gatekeeper.JSON_ERROR_LOGIN))
				{
					dialogBuild.setMessage(errorStr + "\nGo to the log in screen?");
					dialogBuild.setPositiveButton("Yes, please!", alertListener);
					dialogBuild.setNegativeButton("Clear invalid credentials", alertListener);
				}
				else if (errorTypeStr.equals(Gatekeeper.JSON_ERROR_DENIAL))
				{
					dialogBuild.setMessage(errorStr);
					dialogBuild.setNeutralButton("Okay", alertListener);
				}
				else if (errorTypeStr.equals(Gatekeeper.JSON_ERROR_COMMAND))
				{
					dialogBuild.setMessage(errorStr);
					dialogBuild.setNeutralButton("Okay", alertListener);
					Log.wtf("gatekeeper update(String jsonstr)",
							"Invalid command! This should never get run unless Crawford changed the API on me");
				}
				else
				{
					dialogBuild.setMessage(errorStr);
					dialogBuild.setNeutralButton("Okay", alertListener);
					Log.e("gatekeeper update(String jsonstr)",
							"Gatekeeper server returned error message: " + errorStr);
				}
				dialog = dialogBuild.create();
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}
			else if (!responseStr.equals(Gatekeeper.JSON_RESPONSE_NULL))
			{
				if (responseStr.equals(Gatekeeper.D_STATE_UNLOCKED)
						|| responseStr.equals(Gatekeeper.D_STATE_LOCKED))
				{
					tempButton = (Button) findViewById(getId(door));
					tempButton.setBackgroundColor(getColorFromState(responseStr));
					tempButton.setEnabled(true);
				}
				else
				{
					// all_doors or door_state/id was called
					response = new JSONArray(responseStr);
					for (int i = 0; i < response.length(); i++ )
					{
						doorState = response.getJSONObject(i).getString(
								Gatekeeper.JSON_RESPONSE_STATE);
						doorId = response.getJSONObject(i).getInt(Gatekeeper.JSON_RESPONSE_ID);
						doorName = response.getJSONObject(i).getString(
								Gatekeeper.JSON_RESPONSE_NAME);
						tempButton = (Button) findViewById(getId(doorId));
						if (tempButton == null)
						{
							Log.e("gatekeeper update()", "Gatekeeper server returned a door ("
									+ doorName + ") I don't know about");
							return;
						}
						tempButton.setText(doorName + " : " + doorState);
						tempButton.setBackgroundColor(getColorFromState(doorState));
						if (doorState.equals(Gatekeeper.D_STATE_UNKNOWN))
						{
							tempButton.setEnabled(false);
						}
						else
						{
							tempButton.setEnabled(true);
						}
					}
				}
			}

		}
		catch (JSONException je)
		{
			Log.e("JSON Errors " + this.getClass().toString(), je.getMessage(), je);
		}
	}

}
