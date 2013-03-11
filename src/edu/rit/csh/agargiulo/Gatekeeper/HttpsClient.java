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

import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

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
public class HttpsClient extends AsyncHttpClient
{

	final Context context;

	/**
	 * HttpsClient takes in the current context from the activity
	 */
	public HttpsClient (Context context)
	{
		this.context = context;
		setSSLSocketFactory(newSSLSocketFactory());
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
