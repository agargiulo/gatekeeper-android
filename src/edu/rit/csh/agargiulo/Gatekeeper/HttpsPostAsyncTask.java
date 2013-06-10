package edu.rit.csh.agargiulo.Gatekeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

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
	private ProgressDialog progress;
	private int doorID;
	private String contentType = "application/x-www-form-urlencoded";

	/**
	 * 
	 */
	public HttpsPostAsyncTask (HttpsClient client, Activity callingActivity, int doorID)
	{
		gatekeeperClient = client;
		activity = callingActivity;
		this.doorID = doorID;
	}

	private BasicNameValuePair[] copyOfRange (BasicNameValuePair[] src, int start, int end)
	{
		int length = end - start;
		if (length < 0) { throw new IllegalArgumentException("end needs to be greater than start"); }
		BasicNameValuePair[] copy = new BasicNameValuePair[length];
		System.arraycopy(src, start, copy, 0, Math.min(src.length - start, length));
		return copy;
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
		HttpPost request;
		HttpResponse response;
		String line = "", page;

		try
		{
			request = new HttpPost(args[0].getValue());

			request.setEntity(getPostEntity(copyOfRange(args, 1, args.length)));

			try
			{
				response = gatekeeperClient.execute(request);
			}
			catch (Exception ise)
			{
				Log.wtf("CSH-Gatekeeper", "This should not happen...I'm confused.", ise);
				return null;
			}
			bufReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			while ( (line = bufReader.readLine()) != null)
			{
				strBuf.append(line + newLine);
			}

			bufReader.close();
			page = strBuf.toString();
			return page;

		}
		catch (ClientProtocolException cpe)
		{
			Log.e(this.getClass().toString(), cpe.getMessage(), cpe);
			return null;
		}
		catch (UnsupportedEncodingException uee)
		{
			Log.e(this.getClass().toString(), uee.getMessage(), uee);
			return null;
		}
		catch (IOException ioe)
		{
			Log.e(this.getClass().toString(), ioe.getMessage(), ioe);
			return null;
		}

	}

	private HttpEntity getPostEntity (BasicNameValuePair... args)
			throws UnsupportedEncodingException
	{
		StringEntity entity;
		StringBuffer buff = new StringBuffer();
		for (BasicNameValuePair nvp : args)
		{
			if (buff.length() > 0)
			{
				buff.append("&");
			}
			buff.append(nvp.getName()).append("=").append(nvp.getValue());
		}
		entity = new StringEntity(buff.toString());
		entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, contentType));
		return entity;

	}

	@Override
	protected void onPostExecute (String json)
	{
		super.onPostExecute(json);
		if (progress != null && progress.isShowing())
		{
			progress.cancel();
		}
		if (activity != null)
		{
			((GatekeeperActivity) activity).update(json, doorID);
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
