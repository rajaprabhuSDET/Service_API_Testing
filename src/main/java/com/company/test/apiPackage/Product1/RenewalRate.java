package com.company.test.apiPackage.Product1;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import com.company.test.Configuration.PropertiesHandle;
import com.company.test.apiPackage.API;
import com.company.test.apiPackage.BaseClass;
import com.company.test.exception.APIException;
import com.company.test.exception.DatabaseException;
import com.company.test.exception.HTTPHandleException;
import com.company.test.exception.MacroException;
import com.company.test.exception.POIException;
import com.company.test.exception.RequestFormatException;
import com.company.test.macroPackage.MacroInterface;
import com.company.test.macroPackage.Product2Macro;
import com.company.test.util.api.DBColoumnVerify;
import com.company.test.util.api.HttpHandle;
import com.jayway.jsonpath.PathNotFoundException;


public class RenewalRate extends BaseClass implements API
{
	MacroInterface macro = null;
	public RenewalRate(PropertiesHandle config) throws APIException
	{
	    try
	    {
			this.config = config;
			jsonElements = new LinkedHashMap<String, String>();
			InputColVerify = new DBColoumnVerify(config.getProperty("InputCondColumn"));
			OutputColVerify = new DBColoumnVerify("OutputColumnCondtn");	
			StatusColVerify = new DBColoumnVerify(config.getProperty("OutputCondColumn"));
			
			if(config.getProperty("Execution_Flag").equals("ExpectedOnly")||config.getProperty("Execution_Flag").equals("Comparison"))
			{
				macro=new Product2Macro(config);	
			}
	    }
	    catch(MacroException e)
	    {
	    	throw new APIException("ERROR INITATING MACRO- GL CLASS", e);
	    }
		
	}
	
	public void LoadSampleRequest(LinkedHashMap<String, String> InputData) throws APIException
	{
		if(config.getProperty("Execution_Flag").equals("ExpectedOnly")||config.getProperty("Execution_Flag").equals("Comparison"))
		{
			try 
			{
				macro.LoadSampleRatingmodel(config, InputData);		
				macro.GenerateExpected(InputData, config);
			} catch (MacroException e) 
			{
				throw new APIException("ERROR LoadSampleRequest FUNCTION -- GL-RATING CLASS", e);
			}
		}
		if(config.getProperty("Execution_Flag").equals("ActualOnly")||config.getProperty("Execution_Flag").equals("ActualandComparison")||config.getProperty("Execution_Flag").equals("Comparison")||config.getProperty("Execution_Flag").equals("ResponseOnly"))
		 {
			super.LoadSampleRequest(InputData);
		 }
	}
	
	public void PumpDataToRequest(LinkedHashMap<String, String> Commanmap,LinkedHashMap<String, String> InputData) throws  APIException
	{			
		if(config.getProperty("Execution_Flag").equals("ExpectedOnly")||config.getProperty("Execution_Flag").equals("Comparison"))
		{
			try 
			{
				macro.PumpinData(input, config);
			} 
			catch (DatabaseException | POIException | MacroException e) 
			{
				throw new APIException("ERROR PumpDataToRequest FUNCTION -- GL-RATING CLASS");
			}
		}
		if(config.getProperty("Execution_Flag").equals("ActualOnly")||config.getProperty("Execution_Flag").equals("ActualandComparison")||config.getProperty("Execution_Flag").equals("Comparison")||config.getProperty("Execution_Flag").equals("ResponseOnly"))
		 {
		super.PumpDataToRequest(Commanmap,InputData);
		 }
	}
	
	@Override
	public void AddHeaders(String Token) throws APIException
	{
		try
		{
			http = new HttpHandle(config.getProperty("test_url"),"POST");
			http.AddHeader("Content-Type", config.getProperty("content_type"));
			http.AddHeader("Token", Token);
			http.AddHeader("EventName", config.getProperty("EventName"));
			http.AddHeader("EventVersion", config.getProperty("EventVersion"));
			System.out.println(config.getProperty("test_url")+config.getProperty("content_type")+config.getProperty("EventName")+config.getProperty("EventVersion"));
		}
    	catch (HTTPHandleException e) 
		{
			throw new APIException("ERROR ADD HEADER FUNCTION -- GL-RATING CLASS", e);
		}
	}
	
	@Override
	 public LinkedHashMap<String, String> SendResponseDataToFile(LinkedHashMap<String, String> output)   throws APIException
	 {
		try
		{
			DBColoumnVerify conditioncheck = new DBColoumnVerify();
			if(config.getProperty("Execution_Flag").equals("ActualOnly")||config.getProperty("Execution_Flag").equals("ActualandComparison")||config.getProperty("Execution_Flag").equals("Comparison")||config.getProperty("Execution_Flag").equals("ResponseOnly"))
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
								output.put("AnalyserResult", "");
								output.put("User_message","");
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
					
					String RuleName=response.read("..RuleName").replaceAll("\\[\"", "").replaceAll("\"\\]", "").replaceAll("\\\\","");
					String Message=response.read("..Message").replaceAll("\\[\"", "").replaceAll("\"\\]", "").replaceAll("\\\\","");
					output.put("AnalyserResult","Rule-"+RuleName);
					output.put("User_message",Message);
				}
			}
			if(config.getProperty("Execution_Flag").equals("ExpectedOnly")||config.getProperty("Execution_Flag").equals("Comparison"))
			{
				macro.PumpoutData(output, input, config);   //	data pumped out from expected rating model to db table
			}
		}
		catch(DatabaseException | POIException | MacroException | RequestFormatException e)
		{
			 throw new APIException("ERROR SendResponseDataToFile FUNCTION -- DTC-RatingService CLASS", e);
		}
		return output;
	}
}
