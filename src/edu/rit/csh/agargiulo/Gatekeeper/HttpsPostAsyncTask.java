/**
 * 
 */
package edu.rit.csh.agargiulo.Gatekeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Anthony Gargiulo <anthony@agargiulo.com>
 * 
 */
public class HttpsPostAsyncTask extends AsyncTask<BasicNameValuePair, Integer, String>
{
	private HttpsClient gatekeeperClient;
	private Activity activity;
	ProgressDialog progress;

	/**
	 * 
	 */
	public HttpsPostAsyncTask (HttpsClient client, Activity callingActivity)
	{
		gatekeeperClient = client;
		activity = callingActivity;
	}

	/**
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground (BasicNameValuePair... args)
	{
		BufferedReader bufReader;
		StringBuffer strBuf = new StringBuffer();
		String newLine = System.getProperty("line.separator");
		ArrayList<NameValuePair> argPairs = new ArrayList<NameValuePair>();
		HttpPost request;
		HttpResponse response;
		String line = "", page;

		try
		{
			request = new HttpPost(args[0].getValue());

			for(BasicNameValuePair nvp : args)
			{
				argPairs.add(new BasicNameValuePair(nvp.getName(), nvp.getValue()));
				if(!nvp.getName().equals("password"))
				{
					Log.d("postasync", nvp.toString());
				}
			}

			request.setEntity(new UrlEncodedFormEntity(argPairs));

			response = gatekeeperClient.execute(request);
			bufReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			while( (line = bufReader.readLine()) != null)
			{
				strBuf.append(line + newLine);
			}

			bufReader.close();
			page = strBuf.toString();
			return page;

		} catch(ClientProtocolException cpe)
		{
			Log.e(this.getClass().toString(), cpe.getMessage(), cpe);
			return null;
		} catch(UnsupportedEncodingException uee)
		{
			Log.e(this.getClass().toString(), uee.getMessage(), uee);
			return null;
		} catch(IOException ioe)
		{
			Log.e(this.getClass().toString(), ioe.getMessage(), ioe);
			return null;
		}

	}

	@Override
	protected void onPostExecute (String json)
	{
		super.onPostExecute(json);
		if(progress.isShowing())
		{
			progress.cancel();
		}
		if(activity != null)
		{
			((GatekeeperActivity) activity).update(json);
		}
	}

	@Override
	protected void onPreExecute ()
	{
		super.onPreExecute();
		progress = new ProgressDialog(activity);
		progress.setTitle("Thinking...");
		progress.setMessage("Talking to Gatekeeper...");
		progress.setCancelable(false);
		progress.show();
	}
}
