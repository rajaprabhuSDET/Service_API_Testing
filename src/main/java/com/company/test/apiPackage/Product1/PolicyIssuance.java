package com.company.test.apiPackage.Product1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.company.test.Configuration.PropertiesHandle;
import com.company.test.apiPackage.API;
import com.company.test.apiPackage.BaseClass;
import com.company.test.exception.APIException;
import com.company.test.exception.DatabaseException;
import com.company.test.exception.HTTPHandleException;
import com.company.test.exception.RequestFormatException;
import com.company.test.util.api.DBColoumnVerify;
import com.company.test.util.api.HttpHandle;
import com.company.test.util.common.DatabaseOperation;
import com.jayway.jsonpath.PathNotFoundException;



public class PolicyIssuance extends BaseClass implements API 
{
	public PolicyIssuance ()
	{
		
	}
	public PolicyIssuance(PropertiesHandle config) throws SQLException
	{
		this.config = config;
		jsonElements = new LinkedHashMap<String, String>();
		
		InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
		OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
		StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
	}

	public void AddHeaders(String Token) throws APIException
	{
		try
		{
			http = new HttpHandle(config.getProperty("test_url"),"POST");
			http.AddHeader("Content-Type", config.getProperty("content_type"));
			http.AddHeader("Token", Token);
			http.AddHeader("EventName", config.getProperty("EventName"));
			http.AddHeader("EventVersion", config.getProperty("EventVersion"));
		}
		catch(HTTPHandleException e)
		{
			throw new APIException("ERROR ADD HEADER FUNCTION -- BASE CLASS", e);
		}
	}
	
	public LinkedHashMap<String, String> SendResponseDataToFile(LinkedHashMap<String, String> output) throws APIException
	{
		try
		{
			if(config.getProperty("Execution_Flag").equals("ActualOnly")||config.getProperty("Execution_Flag").equals("ActualandComparison")||config.getProperty("Execution_Flag").equals("Comparison")||config.getProperty("Execution_Flag").equals("ResponseOnly"))
			{
				LinkedHashMap<Integer, LinkedHashMap<String, String>> tableOutputColVerify = OutputColVerify.GetDataObjects(config.getProperty("OutputColQuery"));		
				for (Entry<Integer, LinkedHashMap<String, String>> entry : tableOutputColVerify.entrySet())	
				{
					LinkedHashMap<String, String> rowOutputColVerify = entry.getValue();
					if((rowOutputColVerify.get("Flag").equalsIgnoreCase("Y"))&&OutputColVerify.ConditionReading(rowOutputColVerify.get("OutputColumnCondtn"),input))
					{
						try
						{
							//System.out.println("Writing Response to Table");
							String responseStatus=response.read("..ResponseStatus").replaceAll("\\[\"", "").replaceAll("\"\\]", "").replaceAll("\\\\","");
							//System.out.println(responseStatus);
							if(responseStatus.equals("SUCCESS"))
							{
								//System.out.println(rowOutputColVerify.get(config.getProperty("OutputColumn")));
								String actual = (response.read(rowOutputColVerify.get(config.getProperty("OutputJsonPath"))).replaceAll("\\[\"", "")).replaceAll("\"\\]", "").replaceAll("\\\\","");
								output.put(rowOutputColVerify.get(config.getProperty("OutputColumn")), actual);
								//System.out.println(actual);
								output.put("Flag_for_execution", "Completed");
								output.put("Message_code","");
								output.put("User_message","");
								output.put("Time", (end-start) + " Millis");
							}
							else
							{
								output.put("Flag_for_execution",responseStatus);
								output.put("Message_code",response.read("..RuleName"));
								output.put("User_message",response.read("..Message"));
							}
						}
						catch(PathNotFoundException e)
						{
								output.put(rowOutputColVerify.get(config.getProperty("OutputColumn")), "Path not Found");
						}
					}
				}
			}
			return output;
		
		}
		catch(DatabaseException | RequestFormatException e)
		{
			throw new APIException("ERROR IN SEND RESPONSE TO FILE FUNCTION -- BASE CLASS", e);
		}
	}
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws DatabaseException 
	{        
        System.out.println("coming to flow1");
        DatabaseOperation objectInput = new DatabaseOperation();
		//PropertiesHandle configFile=null;		
		//configFile = new propertiesHandle("E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\CancelPreview\\Config\\configURLtoVideo.properties");
		PolicyIssuance pdfobj = new PolicyIssuance();
		objectInput.ConnectionSetup("com.mysql.jdbc.Driver","jdbc:mysql://192.168.84.225:3700/Starr_GL_Development_ADMIN","root","redhat");
		LinkedHashMap<Integer, LinkedHashMap<String, String>> inputtable = objectInput.GetDataObjects("Select * From OUTPUT_GL_NonRenewal");
		Iterator<Entry<Integer, LinkedHashMap<String, String>>> inputtableiterator = inputtable.entrySet().iterator();
		int rowIterator = 1;
		System.out.println("coming to flow3");
		while (inputtableiterator.hasNext() ) 
		{
		 //System.out.println("coming to flow4");
			Entry<Integer, LinkedHashMap<String, String>> inputentry = inputtableiterator.next();
	        LinkedHashMap<String, String> inputrow = inputentry.getValue();
	        
	        if(inputrow.get("Flag_for_execution").equals("Completed"))
			{
	        	System.out.println(inputrow.get("Testdata"));
	            String saveDir = "Q:\\Manual Testing\\Starr\\Starr-GL\\Renewal and Endorsement\\NonRenewal\\RC\\quote policy renewalQuotePolicy NonRenewal\\pdfs\\";
	            try {
	            	System.out.println(inputrow.get("Non_DocumentURL"));
	            	pdfobj.urltopdf(inputrow.get("Non_DocumentURL"),saveDir+inputrow.get("Testdata")+".pdf");
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        	
			}
	        //inputrow.put("Flag_for_execution", "Completed");	
	        objectInput.UpdateRow(rowIterator, inputrow);
	        rowIterator++;
	        
		}
    }

	public void urltopdf(String URL,String path) throws IOException
	{
		disableSslVerification();
		System.setProperty("jsse.enableSNIExtension", "false");	
		URL website = new URL(URL);
		Path targetPath = new File(path).toPath();
		InputStream in = website.openStream();		
		Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);		
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