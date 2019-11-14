package com.company.test.apiPackage;

import java.util.LinkedHashMap;
import com.company.test.Configuration.PropertiesHandle;
import com.company.test.exception.APIException;

public interface API 
{
	public String tokenGenerator(PropertiesHandle config) throws APIException;
	public void LoadSampleRequest(LinkedHashMap<String, String> InputData) throws APIException;
	public void PumpDataToRequest(LinkedHashMap<String, String> commonmap,LinkedHashMap<String, String> InputData) throws APIException;
	public void AddHeaders(String Token) throws APIException;
	public void SendAndReceiveData() throws APIException;
	public LinkedHashMap<String, String> SendResponseDataToFile(LinkedHashMap<String, String> output) throws APIException;
	public String RequestToString(String Token) throws APIException;
	public String ResponseToString() throws APIException;
	public LinkedHashMap<String, String> CompareFunction(LinkedHashMap<String, String> inputrow, LinkedHashMap<String, String> output) throws APIException;
	public LinkedHashMap<String, String> differrence(LinkedHashMap<String, String> inputrow, LinkedHashMap<String, String> output) throws APIException;
}
