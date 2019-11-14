package com.company.test.apiPackage.Product1;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import com.company.test.Configuration.PropertiesHandle;
import com.company.test.apiPackage.API;
import com.company.test.apiPackage.BaseClass;
import com.company.test.exception.APIException;
import com.company.test.exception.HTTPHandleException;
import com.company.test.util.api.DBColoumnVerify;
import com.company.test.util.api.HttpHandle;

public class RenewalPolicy extends BaseClass implements API 
{
	public RenewalPolicy(PropertiesHandle config) throws SQLException
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
}