/**
 * 
 */
package edu.rit.csh.agargiulo.Gatekeeper;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import android.content.Context;
import android.util.Log;

/**
 * @author Anthony Gargiulo <anthony@agargiulo.com>
 * 
 *         This is based on WebnewsHttpClient.java written by JD Batchik
 *         https://github.com/JDrit/AndroidWebnews/blob/master/src/edu/rit/csh/
 *         androidwebnews/WebnewsHttpClient.java
 * 
 *         Using this instead of the default Http client because CSH certs are
 *         weird.
 * 
 */
public class HttpsClient extends DefaultHttpClient
{

	final Context context;

	/**
	 * HttpsClient takes in the current context from the activity
	 */
	public HttpsClient (Context context)
	{
		this.context = context;
	}

	/**
	 * @return {@link ClientConnectionManager}
	 */
	@Override
	protected ClientConnectionManager createClientConnectionManager ()
	{
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("https", newSSLSocketFactory(), 443));
		return new SingleClientConnManager(getParams(), registry);
	}

	/**
	 * 
	 * @return {@link SocketFactory}
	 */
	private SSLSocketFactory newSSLSocketFactory ()
	{
		KeyStore trustedKeys;
		SSLSocketFactory sslSf;
		try
		{
			trustedKeys = KeyStore.getInstance("BKS");
			InputStream input = context.getResources().openRawResource(R.raw.keystore);
			trustedKeys.load(input, "mysecret".toCharArray());
			input.close();
			sslSf = new SSLSocketFactory(trustedKeys);
			sslSf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return sslSf;
		}
		catch (KeyStoreException kse)
		{
			Log.e(this.getClass().toString() + " KeyStoreException", kse.getMessage(), kse);
		}
		catch (NoSuchAlgorithmException nsae)
		{
			Log.e(this.getClass().toString() + " NoSuchAlgorithmException", nsae.getMessage(), nsae);
		}
		catch (CertificateException ce)
		{
			Log.e(this.getClass().toString() + " CertificateException", ce.getMessage(), ce);
		}
		catch (IOException ioe)
		{
			Log.e(this.getClass().toString() + " IOException", ioe.getMessage(), ioe);
		}
		catch (KeyManagementException kme)
		{
			Log.e(this.getClass().toString() + " KeyManagementException", kme.getMessage(), kme);
		}
		catch (UnrecoverableKeyException uke)
		{
			Log.e(this.getClass().toString() + " UnrecoverableKeyException", uke.getMessage(), uke);
		}
		return null;
	}
}
