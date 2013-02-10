/**
 * 
 */
package edu.rit.csh.agargiulo.Gatekeeper;

import android.app.Activity;
import android.content.Context;

/**
 * @author agargiulo
 * 
 */
public class HttpsConnector
{
	private final String URL = "https://api.gatekeeper.csh.rit.edu";
	private HttpsClient client;
	private Context context;
	private Activity activity;

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
	}
}
