/**
 * 
 */
package edu.rit.csh.agargiulo.Gatekeeper;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIUtils;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author agargiulo
 * 
 */
public class HttpsConnector
{
	private final String POST_HOST = "api.gatekeeper.csh.rit.edu";
	private HttpsClient client;
	private Context context;
	private Activity activity;
	private SharedPreferences prefs;
	BasicNameValuePair usernameNvp, passwordNvp;

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
		this.context = context;
		client = new HttpsClient(this.context);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		usernameNvp = new BasicNameValuePair("username", prefs.getString("username", ""));
		passwordNvp = new BasicNameValuePair("password", prefs.getString("password", ""));

	}

	private URI createUrl (String path)
	{
		URI postUri;
		try
		{
			postUri = URIUtils.createURI("https", POST_HOST, 0, path, null, null);
		} catch(URISyntaxException e)
		{
			Log.e(this.getClass().toString() + "URISyntaxException", e.getMessage(), e);
			postUri = null;
		}
		return postUri;

	}

	/**
	 * Gets all of the doors
	 */
	public void getAllDoors ()
	{
		URI doorsUrl = createUrl("all_doors");
		BasicNameValuePair urlNvp = new BasicNameValuePair("url", doorsUrl.toString());
		new HttpsPostAsyncTask(client, activity).execute(urlNvp, usernameNvp, passwordNvp);
	}

	/**
	 * Get the state for a given Door ID
	 */
	public void getDoorState (int doorId)
	{
		URI stateUrl = createUrl("door_state/" + doorId);
		BasicNameValuePair urlNvp = new BasicNameValuePair("url", stateUrl.toString());
		new HttpsPostAsyncTask(client, activity).execute(urlNvp, usernameNvp, passwordNvp);
	}

	/**
	 * Lock the given door
	 */
	public void lockDoor (int doorId)
	{
		URI lockUrl = createUrl("lock/" + doorId);
		BasicNameValuePair urlNvp = new BasicNameValuePair("url", lockUrl.toString());
		new HttpsPostAsyncTask(client, activity).execute(urlNvp, usernameNvp, passwordNvp);
	}

	/**
	 * Pop the lock on the given door.
	 */
	public void popDoor (int doorId)
	{
		URI popUrl = createUrl("pop/" + doorId);
		BasicNameValuePair urlNvp = new BasicNameValuePair("url", popUrl.toString());
		new HttpsPostAsyncTask(client, activity).execute(urlNvp, usernameNvp, passwordNvp);
	}

	/**
	 * Unlock the given door
	 */
	public void unlockDoor (int doorId)
	{
		URI unlockUrl = createUrl("unlock/" + doorId);
		BasicNameValuePair urlNvp = new BasicNameValuePair("url", unlockUrl.toString());
		new HttpsPostAsyncTask(client, activity).execute(urlNvp, usernameNvp, passwordNvp);
	}
}
