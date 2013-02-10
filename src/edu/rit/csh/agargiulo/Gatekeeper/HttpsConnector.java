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
		usernameNvp = new BasicNameValuePair("username", prefs.getString(
				"username", ""));
		passwordNvp = new BasicNameValuePair("password", prefs.getString(
				"password", ""));

	}

	private URI createUrl (String path)
	{
		URI post_uri;
		try
		{
			post_uri = URIUtils.createURI("https", POST_HOST, 0, path, null,
					null);
		} catch(URISyntaxException e)
		{
			Log.e(this.getClass().toString() + "URISyntaxException",
					e.getMessage(), e);
			post_uri = null;
		}
		return post_uri;

	}

	/**
	 * Gets all of the doors
	 */
	public void getDoors ()
	{
		URI doors_url = createUrl("all_doors");
		BasicNameValuePair urlNvp = new BasicNameValuePair("url",
				doors_url.toString());
		new HttpsPostAsyncTask(new HttpsClient(context), activity).execute(
				urlNvp, usernameNvp, passwordNvp);
	}
}
