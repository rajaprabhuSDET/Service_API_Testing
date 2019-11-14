package com.company.test.listener;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import com.company.test.Configuration.PropertiesHandle;
import com.company.test.apiPackage.API2;
import com.company.test.exception.APIException;

public class FireEventAPI2 implements API2
{
	protected API2 api= null;
	
	
	public FireEventAPI2(API2 api)
	{
		this.api=api;
		
	}
	
	protected LinkedList<Listener> listeners1 = new LinkedList<Listener>();

	public void addListener(Listener listener){
	    listeners1.add((Listener) listener);
	}

	public boolean removeListener(Listener listener){
	    return listeners1.remove(listener);
	}
	
	@Override
	public void LoadSampleRequest(LinkedHashMap<Integer,LinkedHashMap<String, String>> InputData) throws APIException 
	{
		// TODO Auto-generated method stub
		for (Listener listen : listeners1)
			listen.beforeLoadSampleRequest();
		try
		{
			api.LoadSampleRequest(InputData);
		}
		catch(APIException e)
		{
			for (Listener listen : listeners1)
				listen.onError(e);
			throw new APIException(e);
		}
		for (Listener listen : listeners1)
			listen.afterLoadSampleRequest();
	}

	public void PumpDataToRequest(LinkedHashMap<String, String> commonmap,LinkedHashMap<Integer,LinkedHashMap<String, String>> InputData) throws APIException 
	{
		// TODO Auto-generated method stub
		for (Listener listen : listeners1)
			listen.beforePumpDataToRequest();
		try
		{
			api.PumpDataToRequest(commonmap, InputData);
		}
		catch(APIException e)
		{
			e.printStackTrace();
			for (Listener listen : listeners1)
				listen.onError(e);
			throw new APIException(e);
		}
		for (Listener listen : listeners1)
			listen.afterPumpDataToRequest();
	}
	
	@Override
	public String RequestToString(String Token) throws APIException 
	{
		// TODO Auto-generated method stub
		for (Listener listen : listeners1)
			listen.beforeRequestToString();
		String requesttoString="";
		try
		{
			api.RequestToString(Token);
		}
		catch(APIException e)
		{
			for (Listener listen : listeners1)
				listen.onError(e);
			throw new APIException(e);
		}
		for (Listener listen : listeners1)
			listen.afterRequestToString();
		return requesttoString;
	}

	@Override
	public void AddHeaders(String Token) throws APIException 
	{
		// TODO Auto-generated method stub
		for (Listener listen : listeners1)
			listen.beforeAddHeaders(Token);
		try
		{
			api.AddHeaders(Token);
		}
		catch(APIException e)
		{
			for (Listener listen : listeners1)
				listen.onError(e);
			throw new APIException(e);
		}
		for (Listener listen : listeners1)
			listen.afterAddHeaders(Token);
	}

	@Override
	public void SendAndReceiveData() throws APIException 
	{
		// TODO Auto-generated method stub
		for (Listener listen : listeners1)
			listen.beforeSendAndReceiveData();
		try
		{
			api.SendAndReceiveData();
		}
		catch(APIException e)
		{
			for (Listener listen : listeners1)
				listen.onError(e);
			throw new APIException(e);
		}
		for (Listener listen : listeners1)
			listen.afterSendAndReceiveData();
	}

	@Override
	public List<String> SendResponseDataToFile(LinkedHashMap<String, String> output) throws APIException 
	{
		List <String> output1;
		// TODO Auto-generated method stub
		for (Listener listen : listeners1)
			listen.beforeSendResponseDataToFile();
		try
		{
			output1 =api.SendResponseDataToFile(output);
		}
		catch(APIException e)
		{
			for (Listener listen : listeners1)
				listen.onError(e);
			throw new APIException(e);
		}
		for (Listener listen : listeners1)
			listen.afterSendResponseDataToFile();
		return output1;
	}

	@Override
	public LinkedHashMap<Integer,LinkedHashMap<String, String>> CompareFunction(LinkedHashMap<Integer,LinkedHashMap<String, String>> inputrow,
			LinkedHashMap<String, String> output) throws APIException 
	{
		// TODO Auto-generated method stub
		for (Listener listen : listeners1)
			listen.beforeCompareFunction();
		try
		{
			inputrow =api.CompareFunction(inputrow,output);
		}
		catch(APIException e)
		{
			for (Listener listen : listeners1)
				listen.onError(e);
			throw new APIException(e);
		}
		for (Listener listen : listeners1)
			listen.afterCompareFunction();
		return inputrow;
	}

	

	@Override
	public String ResponseToString() throws APIException 
	{
		// TODO Auto-generated method stub
		for (Listener listen : listeners1)
			listen.beforeResponseToString();
		String ResponseToString = "";
		try
		{
			ResponseToString=api.ResponseToString();
		}
		catch(APIException e)
		{
			for (Listener listen : listeners1)
				listen.onError(e);
			throw new APIException(e);
		}
		for (Listener listen : listeners1)
			listen.afterResponseToString();
		return ResponseToString;
	}

	@Override
	public String tokenGenerator(PropertiesHandle config) throws APIException {
		for (Listener listen : listeners1)
			listen.beforeTokenGenerator();
		String token ="";
		try
		{
			token=api.tokenGenerator(config);
		}
		catch(APIException e)
		{
			for (Listener listen : listeners1)
				listen.onError(e);
			throw new APIException(e);
		}
		for (Listener listen : listeners1)
			listen.afterTokenGeneratior();
		return token;
	}

	/*@Override
	public LinkedHashMap<String, String> differrence(LinkedHashMap<Integer,LinkedHashMap<String, String>> inputrow,
			LinkedHashMap<String, String> output) throws APIException {
		// TODO Auto-generated method stub
		for (Listener listen : listeners1)
			listen.beforedifferrence();
		try
		{
			output =api.differrence(inputrow,output);
		}
		catch(APIException e)
		{
			for (Listener listen : listeners1)
				listen.onError(e);
			throw new APIException(e);
		}
		for (Listener listen : listeners1)
			listen.afterCompareFunction();
		return output;
	}	*/
	
}
