package com.company.test.util.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.company.test.exception.HTTPHandleException;

public class HttpHandle 
{
	private URL url;
	private HttpURLConnection conn;
	
	public HttpHandle(String url,String type) throws HTTPHandleException
	{
		
			try 
			{
				disableSslVerification();

				this.url = new URL(url);
			} 
			catch (MalformedURLException e) 
			{
				throw new HTTPHandleException("ERROR IN URL TO HIT HTTP -- REQUESTOR", e);
			}
			
			try 
			{
				conn = (HttpURLConnection) this.url.openConnection();
			} 
			catch (IOException e) 
			{
				throw new HTTPHandleException("ERROR IN URL TO OPEN CONNECTION IN HTTP REQUESTOR", e);
			}
			
			conn.setDoOutput(true);
			
			try 
			{
				conn.setRequestMethod(type);
			} 
			catch (ProtocolException e) 
			{
				throw new HTTPHandleException("ERROR WHILE SENDING REQUEST TO HIT -- HTTP REQUESTOR", e);
			}
		
	}
	
	public void AddHeader(String attr,String value)
	{
		conn.setRequestProperty(attr, value);
	}
	
	public void SendData(String data) throws HTTPHandleException
	{
		OutputStream os;
		
			try 
			{
				
				os = conn.getOutputStream();
				os.write(data.getBytes());
				os.flush();
			} 
			catch (IOException e) 
			{
				throw new HTTPHandleException("ERROR WHILE SENDING JSON REQUEST", e);
			}
		
		
	}
	
	public String ReceiveData() throws HTTPHandleException 
	{
		String output,output2 = null;
		BufferedReader br;
		
			try 
			{
				br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			} 
			catch (IOException e)
			{
				throw new HTTPHandleException("ERROR WHILE RECEIVING JSON RESPONSE", e);
			}
					
			try 
			{
				while ((output = br.readLine()) != null) 
				{
					if(output2 == null)
					{
						output2 = output;
					}
					else
					{
						output2 += output;
					}
				}
			} 
			catch (IOException e) 
			{
				throw new HTTPHandleException("ERROR WHILE RECEIVING JSON RESPONSE", e);
			}
		
		
		if (output2 == null)
		{
			throw new HTTPHandleException("RETURNS EMPTY RESPONSE");
		}
		
		return output2;
	}
	public static void disableSslVerification() {
        try
        {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
				
                
            }
            };
            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }

			
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
}
}
