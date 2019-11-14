package com.company.test.apiPackage.Product2;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import com.company.test.Configuration.PropertiesHandle;
import com.company.test.apiPackage.API;
import com.company.test.apiPackage.BaseClass;
import com.company.test.exception.APIException;
import com.company.test.exception.DatabaseException;
import com.company.test.exception.RequestFormatException;
import com.company.test.util.api.DBColoumnVerify;
import com.jayway.jsonpath.PathNotFoundException;

public class IsoBopQuote extends BaseClass implements API 
{
	public IsoBopQuote(PropertiesHandle config) throws SQLException
	{
		this.config = config;
		jsonElements = new LinkedHashMap<String, String>();
		
		InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
		OutputColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));	
		StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
	}
	
	@Override
	public LinkedHashMap<String, String> SendResponseDataToFile(LinkedHashMap<String, String> output)   throws APIException
	{
		try
		{
			 if(config.getProperty("Execution_Flag").equals("ActualOnly")||config.getProperty("Execution_Flag").equals("Comparison")||config.getProperty("Execution_Flag").equals("ActualandComparison"))
			 {
			LinkedHashMap<Integer, LinkedHashMap<String, String>> tableOutputColVerify = OutputColVerify.GetDataObjects(config.getProperty("OutputColQuery"));
			
			String ResponseStatus=response.read("..ResponseStatus").replaceAll("\\[\"", "").replaceAll("\"\\]", "").replaceAll("\\\\","");
			if(ResponseStatus.equals("SUCCESS"))
			{
			
			for (Entry<Integer, LinkedHashMap<String, String>> entry : tableOutputColVerify.entrySet())	
			{
				LinkedHashMap<String, String> rowOutputColVerify = entry.getValue();
				  if((rowOutputColVerify.get("Flag").equalsIgnoreCase("Y"))&&conditioncheck.ConditionReading(rowOutputColVerify.get("OutputColumnCondtn"),input))
					{
					try
						{
					
						String actual = (response.read(rowOutputColVerify.get(config.getProperty("OutputJsonPath"))).replaceAll("\\[\"", "")).replaceAll("\"\\]", "").replaceAll("\\\\","");
		
						output.put(rowOutputColVerify.get(config.getProperty("OutputColumn")), actual);
						output.put("Flag_for_execution", ResponseStatus);
						output.put("Time", (end-start) + " Millis");
						}
						catch(PathNotFoundException | RequestFormatException e)
						{
							output.put(rowOutputColVerify.get(config.getProperty("OutputColumn")), "Path not Found");
						}
					}
			}
			}
			else
			{
				output.put("Flag_for_execution", "FailedResponse");
				
				String Message=response.read("..Message").replaceAll("\\[\"", "").replaceAll("\"\\]", "").replaceAll("\\\\","");
				String Message2=response.read("..UserMessage").replaceAll("\\[\"", "").replaceAll("\"\\]", "").replaceAll("\\\\","");
				output.put("User_message",Message);
				output.put("Message",Message2);
				output.put("AnalyserResult","Rule-"+Message2);
				
			}
		 }
			
		}
		catch(DatabaseException | RequestFormatException e)
		{
			 throw new APIException("ERROR SendResponseDataToFile FUNCTION -- DTC-RatingService CLASS", e);
		}
		return output;
	}
}