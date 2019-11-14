package com.company.test.macroPackage;

import java.util.LinkedHashMap;
import com.company.test.Configuration.PropertiesHandle;
import com.company.test.exception.DatabaseException;
import com.company.test.exception.MacroException;
import com.company.test.exception.POIException;

public interface MacroInterface 
{
	public void LoadSampleRatingmodel(PropertiesHandle configFile,LinkedHashMap<String, String> inputData) throws MacroException;
	public void GenerateExpected(LinkedHashMap<String, String> inputData,PropertiesHandle configFile) throws MacroException;
	public void PumpinData(LinkedHashMap<String, String> inputData,PropertiesHandle configFile) throws DatabaseException, POIException, MacroException;
	public void PumpoutData(LinkedHashMap<String, String> outputData,LinkedHashMap<String, String> inputData,PropertiesHandle configFile) throws POIException, MacroException, DatabaseException;
	
}
