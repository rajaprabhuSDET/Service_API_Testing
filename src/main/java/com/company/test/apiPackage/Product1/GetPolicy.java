package com.company.test.apiPackage.Product1;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import com.company.test.Configuration.PropertiesHandle;
import com.company.test.apiPackage.API;
import com.company.test.apiPackage.BaseClass;
import com.company.test.exception.APIException;
import com.company.test.exception.DatabaseException;
import com.company.test.exception.HTTPHandleException;
import com.company.test.exception.RequestFormatException;
import com.company.test.util.api.DBColoumnVerify;
import com.company.test.util.api.HttpHandle;
import com.jayway.jsonpath.PathNotFoundException;

public class GetPolicy extends BaseClass implements API 
{
	public GetPolicy(PropertiesHandle config) throws SQLException
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
								output.put("RuleName","");
								output.put("User_message","");
								output.put("Time", (end-start) + " Millis");
							}
							else
							{
								output.put("Flag_for_execution",responseStatus);
								output.put("RuleName",response.read("..RuleName"));
								output.put("Message",response.read("..Message"));
								output.put("Status",response.read("..Status"));
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
}