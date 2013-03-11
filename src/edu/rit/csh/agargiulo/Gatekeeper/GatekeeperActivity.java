package edu.rit.csh.agargiulo.Gatekeeper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * This is the class for the Main Activity of the Gatekeeper App
 * 
 * @author Anthony Gargiulo <anthony@agargiulo.com>
 */
@SuppressLint("NewApi")
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
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				logout();
				break;
			case DialogInterface.BUTTON_NEUTRAL:
				break;
			}
		}
	}

	// This is here because Crawford doesn't
	// number the doors in a sane manner
	private static final int FIRST_DOOR = 4;
	private static final int LAST_DOOR = 9;

	private HttpsConnector connector;
	private SharedPreferences prefs;

	/**
	 * Start the about view
	 */
	public void about (View view)
	{
		startActivity(new Intent(this, AboutActivity.class));
	}

	/**
	 * 
	 * @param doorState
	 *            can be one of unknown, unlocked, or locked
	 * @return Gray for unlocked, red for unlocked, or green for locked
	 */
	private int getColorFromState (String doorState)
	{
		int color;
		if (doorState.equals("unknown"))
		{
			// #DDDDDD = Gray
			color = Color.parseColor("#DDDDDD");
		}
		else if (doorState.equals("unlocked"))
		{
			// #FF8080 = The red used on the web app
			color = Color.parseColor("#FF8080");
		}
		else if (doorState.equals("locked"))
		{
			// #80c080 = The green used on the web app
			color = Color.parseColor("#80c080");
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
	 * 
	 * 
	 */
	public void login (View view)
	{
		startActivityForResult(new Intent(this, LoginActivity.class), 0);
	}

	/**
	 * Deletes the stored username and password from the preferences effectivly
	 * logging out of the app
	 */
	private void logout ()
	{
		// This is possible because awesomeness
		prefs.edit().remove("username").remove("password").remove("loggedin").commit();
		connector = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			invalidateOptionsMenu();
		}
		// Log.d("logout", "logged out user");
		resetView();

	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data)
	{
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String username = prefs.getString("username", "");
		if (username == "")
		{
			Log.e("Gatekeeper", "LoginActivity.onActivityResults: Invalid username");
			prefs.edit().remove("loggedin").commit();
		}
		else
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			{
				invalidateOptionsMenu();
			}
			// Log.d("gatekeeper", username);
			if (connector == null)
			{
				connector = new HttpsConnector(this);
			}
			connector.getAllDoors();
		}

	}

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (!prefs.getBoolean("loggedin", false))
		{
			// User is not logged in
			// show Welcome message and the login/about buttons
			resetView();
		}
		else
		{
			// User was already logged in, get ALL of the doors!
			if (connector == null)
			{
				connector = new HttpsConnector(this);
			}
			connector.getAllDoors();
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
		switch (item.getItemId())
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
		if (prefs.getBoolean("loggedin", false))
		{
			menu.findItem(R.id.menu_login).setVisible(false);
			menu.findItem(R.id.menu_logout).setVisible(true);
			menu.findItem(R.id.menu_about).setVisible(true);
		}
		else
		{
			menu.findItem(R.id.menu_logout).setVisible(false);
			menu.findItem(R.id.menu_login).setVisible(false);
			menu.findItem(R.id.menu_about).setVisible(false);
		}
		return result;
	}

	public void popLock (View view)
	{
		((Button) view).setBackgroundColor(Color.parseColor("#FFD280"));
		((Button) view).setEnabled(false);
		int doorId;
		switch (view.getId())
		{
		/*
		case R.id.door_1:
			doorId = 1;
			break;
		case R.id.door_2:
			doorId = 2;
			break;
		case R.id.door_3:
			doorId = 3;
			break;
		*/
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

	private void resetView ()
	{
		Button tempButton;
		for (int i = FIRST_DOOR; i <= LAST_DOOR; i++ )
		{
			tempButton = (Button) findViewById(getId(i));
			tempButton.setVisibility(View.GONE);
		}
		findViewById(R.id.about_button).setVisibility(View.VISIBLE);
		findViewById(R.id.welcome_message).setVisibility(View.VISIBLE);
		findViewById(R.id.login_button).setVisibility(View.VISIBLE);
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
		TextView wel_mesg;

		// These will be the values received from the JSON string
		String responseStr, errorStr, errorTypeStr;
		boolean success;

		try
		{
			obj = new JSONObject(jsonstr);

			/*
			 * initialize these here to make life SO much better later on
			 */
			success = Boolean.valueOf(obj.getString("success"));
			responseStr = obj.getString("response");
			errorStr = obj.getString("error");
			errorTypeStr = obj.getString("error_type");

			if (!success)
			{
				// The command we tried to run failed.
				// Display a dialog to the user stating the error

				alertListener = new InvalidCredsOnClickListener();
				dialogBuild = new AlertDialog.Builder(this);
				dialogBuild.setTitle(errorTypeStr);
				if (errorTypeStr.equals("login"))
				{
					dialogBuild.setMessage(errorStr + "\nGo to the log in screen?");
					dialogBuild.setPositiveButton("Yes, please!", alertListener);
					dialogBuild.setNegativeButton("Clear invalid credentials", alertListener);
				}
				else if (errorTypeStr.equals("denial"))
				{
					dialogBuild.setMessage(errorStr);
					dialogBuild.setNeutralButton("Okay", alertListener);
				}
				else if (errorTypeStr.equals("command"))
				{
					dialogBuild.setMessage(errorStr);
					dialogBuild.setNeutralButton("Okay", alertListener);
					Log.wtf("gatekeeper update(String jsonstr)",
							"Invalid command! This should never get run unless Crawford changed the API on me");
				}
				dialog = dialogBuild.create();
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}
			else if (!responseStr.equals("null"))
			{
				if (responseStr.equals("unlocked") || responseStr.equals("locked"))
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
						doorState = response.getJSONObject(i).getString("state");
						doorId = response.getJSONObject(i).getInt("id");
						doorName = response.getJSONObject(i).getString("name");
						tempButton = (Button) findViewById(getId(doorId));
						tempButton.setText(doorName + ":" + doorState);
						tempButton.setBackgroundColor(getColorFromState(doorState));
						tempButton.setVisibility(View.VISIBLE);
						if (doorState.equals("unknown"))
						{
							tempButton.setEnabled(false);
						}
						else
						{
							tempButton.setEnabled(true);
						}
					}
					wel_mesg = (TextView) findViewById(R.id.welcome_message);
					wel_mesg.setVisibility(View.GONE);
					tempButton = (Button) findViewById(R.id.about_button);
					tempButton.setVisibility(View.GONE);
					tempButton = (Button) findViewById(R.id.login_button);
					tempButton.setVisibility(View.GONE);
				}
			}

		}
		catch (JSONException je)
		{
			Log.e("JSON Errors " + this.getClass().toString(), je.getMessage(), je);
		}
	}
}
