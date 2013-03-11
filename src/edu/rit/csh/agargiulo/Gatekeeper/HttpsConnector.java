/**
 * 
 */
package edu.rit.csh.agargiulo.Gatekeeper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.loopj.android.http.RequestParams;

/**
 * @author Anthony Gargiulo <anthony@agargiulo.com>
 * 
 */
public class HttpsConnector
{
	static protected final String POST_HOST = "https://api.gatekeeper.csh.rit.edu/";
	private HttpsClient client;
	private Context context;
	private Activity activity;
	private SharedPreferences prefs;
	private RequestParams params;

	/**
	 * 
	 */
	public HttpsConnector (Activity act)
	{
		this(act.getApplicationContext());
		activity = act;
	}

	/**
	 * 
	 */
	public HttpsConnector (Context context)
	{
		String username, password;
		this.context = context;
		client = new HttpsClient(this.context);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		username = prefs.getString("username", "");
		password = prefs.getString("password", "");
		params = new RequestParams();
		params.put("username", username);
		params.put("password", password);
	}

	private String createUrl (String path)
	{
		String postUrl;
		postUrl = POST_HOST + path;
		return postUrl;
	}

	/**
	 * Gets all of the doors
	 */
	public void getAllDoors ()
	{
		String doorsUrl = createUrl("all_doors");
		client.post(context, doorsUrl, params, new HttpsPostAsyncTask(activity, -1));
	}

	/**
	 * Get the state for a given Door ID
	 */
	public void getDoorState (int doorId)
	{
		String stateUrl = createUrl("door_state/" + doorId);
		client.post(context, stateUrl, params, new HttpsPostAsyncTask(activity, doorId));
	}

	/**
	 * Lock the given door
	 */
	public void lockDoor (int doorId)
	{
		String lockUrl = createUrl("lock/" + doorId);
		client.post(context, lockUrl, params, new HttpsPostAsyncTask(activity, doorId));
	}

	/**
	 * Pop the lock on the given door.
	 */
	public void popDoor (int doorId)
	{
		String popUrl = createUrl("pop/" + doorId);
		client.post(context, popUrl, params, new HttpsPostAsyncTask(activity, doorId));
	}

	/**
	 * Unlock the given door
	 */
	public void unlockDoor (int doorId)
	{
		String unlockUrl = createUrl("unlock/" + doorId);
		client.post(context, unlockUrl, params, new HttpsPostAsyncTask(activity, doorId));
	}
}
