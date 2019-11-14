package com.company.test.macroPackage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.company.test.Configuration.PropertiesHandle;
import com.company.test.exception.DatabaseException;
import com.company.test.exception.MacroException;
import com.company.test.exception.POIException;
import com.company.test.exception.PropertiesHandleException;
import com.company.test.util.api.DBColoumnVerify;
import com.company.test.util.common.DatabaseOperation;
import com.company.test.util.common.ExcelOperationsPOI;

public class Product2Macro extends DBColoumnVerify implements MacroInterface
{
	protected ExcelOperationsPOI sampleexcel=null;
	protected String Targetpath;
	protected Product2Macro trans;
	protected String Samplepath;
	protected DatabaseOperation configTable = null;
	protected PropertiesHandle configFile;
	
	
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public enum Alphabet 
	{
	    A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,AA,AB,AC,AD,AE,AF,AG,AH,AI,AJ,AK,AL,AM,AN,AO,AP,AQ,AR,AS,AT,AU,AV,AW,AX,AY,AZ;

	    public static int getNum(String targ) 
	    {
	        return valueOf(targ).ordinal();
	    }

	    public static int getNum(char targ)
	    {
	        return valueOf(String.valueOf(targ)).ordinal();
	    }
	}
//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	public Product2Macro()
	{
		
	}
	public Product2Macro(PropertiesHandle configFile) throws MacroException
	{
		configTable = new DatabaseOperation();
		//configFile = new PropertiesHandle("A:/1 Projects/09 StarrGL/Release_24_UAT/RatingTrial/configuration_file/config_json.properties");
		try 
		{
			configTable.GetDataObjects(configFile.getProperty("config_query"));
		}
		catch (DatabaseException e) 
		{
			throw new MacroException("ERROR OCCURS INITILIZE THE OBJECT OF StarrGLMACRO", e);
		}
		
	}
	
	public void LoadSampleRatingmodel(PropertiesHandle configFile,LinkedHashMap<String, String> inputData) throws MacroException
	{
		try
		{
			String RatingModelVersion=inputData.get("RatingModelVersion");
			String RateingModelName = Lookup(RatingModelVersion,configFile);		
			Samplepath= configFile.getProperty("Samplepath")+RateingModelName+".xls";
			sampleexcel= new ExcelOperationsPOI(Samplepath);

		}
		catch (POIException e)
		{
			throw new MacroException("ERROR OCCURS WHILE LOADING SAMPLE RATING MODEL", e);
		}
	}
	
	public void GenerateExpected(LinkedHashMap<String, String> inputData,PropertiesHandle configFile) throws MacroException
	{
		try
		{
			Targetpath =  configFile.getProperty("TargetPath")+inputData.get("Testdata")+".xls";
			sampleexcel.Copy(Samplepath, Targetpath);
			sampleexcel.save();
			System.out.println("generate expected rating over");
		}
		catch( POIException e)
		{
			throw new MacroException("ERROR OCCURS WHILE GENERATING THE EXPECTED RATING MODEL", e);
		}
	}
	
	public void PumpinData(LinkedHashMap<String, String> inputData,PropertiesHandle configFile) throws MacroException
	{
		try
		{
			//DatabaseOperation configTable = new DatabaseOperation();
			LinkedHashMap<Integer, LinkedHashMap<String, String>> tablePumpinData = configTable.GetDataObjects(configFile.getProperty("config_query"));
			ExcelOperationsPOI excel=new ExcelOperationsPOI(Targetpath);
			trans= new Product2Macro(configFile);
			for (Entry<Integer, LinkedHashMap<String, String>> entry : tablePumpinData.entrySet())	
			{								
				LinkedHashMap<String, String> rowPumpinData = entry.getValue();
				String condition = rowPumpinData.get("Condition");
				if (rowPumpinData.get("flag_for_execution").equalsIgnoreCase("Y") &&ConditionReading(condition,inputData))
				{
					if (rowPumpinData.get("Type").equals("input"))
					{
						String Datacolumntowrite = rowPumpinData.get("Input_DB_column");
						String CellAddress = rowPumpinData.get("Cell_Address");
						
						String  Datatowrite = inputData.get(Datacolumntowrite);
						String[] part = CellAddress.split("(?<=\\D)(?=\\d)");
						int columnNum=Alphabet.getNum(part[0].toUpperCase());
						int rowNum = Integer.parseInt(part[1]);
						System.out.println(columnNum+"----"+rowNum+"-----"+rowPumpinData.get("Sheet_Name")+"-----"+Datatowrite);
						excel.getsheets(rowPumpinData.get("Sheet_Name"));
						excel.getcell(rowNum, columnNum);
						
						if(rowPumpinData.get("Translation_Flag").equals("Y"))
						{
							excel.write_data(rowNum-1, columnNum, trans.Translation1(Datatowrite, rowPumpinData, configFile));
						}
						else
						{
							if(trans.isInteger(Datatowrite))
							{
								int datadata =Integer.parseInt(Datatowrite);
								excel.write_data(rowNum-1, columnNum, datadata);	
							}
							else if(trans.isFloat(Datatowrite))
							{
								float floatdata = Float.parseFloat(Datatowrite);
								excel.write_data(rowNum-1, columnNum, floatdata);
							}
							else
							{
								System.out.println(Datacolumntowrite+"----------"+(rowNum-1)+"--------"+columnNum+"----"+Datatowrite);
								excel.write_data(rowNum-1, columnNum, Datatowrite);
							}
						}
					}
				}
			}
			excel.refresh();
			excel.save();
		}
		catch(DatabaseException e)
		{
			throw new MacroException("ERROR OCCURS WHILE PUMP-IN THE DATA TO RATING MODEL OF StarrGL MACRO", e);
			//e.printStackTrace();
		}
		catch(POIException e)
		{
			throw new MacroException("ERROR OCCURS WHILE OPENING AND CLOSING THE RATING MODEL OF StarrGL MACRO", e);
			//e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void PumpoutData(LinkedHashMap<String, String> outputData,LinkedHashMap<String, String> inputData,PropertiesHandle configFile) throws MacroException    
	{
		try
		{
		ExcelOperationsPOI excel=new ExcelOperationsPOI(Targetpath);
		LinkedHashMap<Integer, LinkedHashMap<String, String>> tablePumpoutData = configTable.GetDataObjects(configFile.getProperty("config_query"));
		//excel.refresh();
		for (Entry<Integer, LinkedHashMap<String, String>> entry : tablePumpoutData.entrySet())	
		{
			LinkedHashMap<String, String> rowPumpoutData = entry.getValue();
			String condition = rowPumpoutData.get("Condition");
			if (rowPumpoutData.get("Type").equals("output"))
			{
				if (rowPumpoutData.get("flag_for_execution").equalsIgnoreCase("Y") &&ConditionReading(condition,inputData))
				{				
					String Datacolumntowrite = rowPumpoutData.get("Input_DB_column");
					String CellAddress = rowPumpoutData.get("Cell_Address");
					String[] part = CellAddress.split("(?<=\\D)(?=\\d)");
					int columnNum=Alphabet.getNum(part[0].toUpperCase());
					int rowNum = Integer.parseInt(part[1]);
					excel.getsheets(rowPumpoutData.get("Sheet_Name"));
					excel.getcell(rowNum-1, columnNum);
					String Datatowrite = excel.read_data(rowNum-1, columnNum);
					System.out.println(Datacolumntowrite+"----------" +Datatowrite+"--------"+rowNum+"-------"+columnNum);
					outputData.put(Datacolumntowrite, Datatowrite);
					//outputData.WriteData(Datacolumntowrite, "poda");
				}
			}
			//outputData.UpdateRow();
		}
		excel.save();
		}
		catch(DatabaseException e)
		{
			throw new MacroException("ERROR OCCURS WHILE PUMPOUT THE OUTPUT FROM RATING MODEL OF StarrGL MACRO", e);
		}
		catch (POIException e)
		{
			throw new MacroException("ERROR OCCURS 	WHILE OPENING/CLOSING THE RATING MODEL OF StarrGL MACRO", e);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("output over");
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T Translation1(String Datatowrite, LinkedHashMap<String, String> configTable,  PropertiesHandle configFile) throws   MacroException
	{
		T outputdata = null;
			switch(configTable.get("Translation_Function"))
			{
			case "Date": 
				Date DateData = Date(Datatowrite,"yyyy-mm-dd",configTable.get("Translation_Format"));
				outputdata = (T) DateData;
				break;
			case "Lookup":
				String LookupData = Lookup(Datatowrite, configFile);
				outputdata = (T) LookupData;
				break;
			case "String":
				String Stringdata = IntegertoString(Datatowrite);
				outputdata = (T) Stringdata;
				break;
			case "ReplaceComma":
				int replaceddata= ReplaceComma(Datatowrite);
				Integer replacdata = new Integer (replaceddata);
				outputdata = (T) replacdata;
				break;
			case "Version":
				String versiondata=Version(Datatowrite);
				outputdata = (T) versiondata;
				break;
			}		
		return outputdata;
		
	}
	
	
	protected  Date Date(String Date,String InputFormat,String ExpectedFormat) throws MacroException
	{
		String value ="";
		Date Date1=null;
		
		
			Pattern p = Pattern.compile("[^A-Za-z0-9 ]", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(InputFormat);
			String InputDelimiter="";
			if(m.find())
			{
				InputDelimiter=String.valueOf(InputFormat.charAt(m.start()));
			}
			
			Matcher m1 = p.matcher(ExpectedFormat);
			String ExpectedDelimiter="";
			if(m1.find())
			{
				ExpectedDelimiter=String.valueOf(ExpectedFormat.charAt(m1.start()));
			}
			
			String[] DateInputFormat = InputFormat.split(InputDelimiter);
			String[] DateOutputFormat = ExpectedFormat.split(ExpectedDelimiter);
			String[] date = Date.split(InputDelimiter); //yyyy-mm-dd
			
			HashMap<String,String> DateMaping = new HashMap<String,String>();
			DateMaping.put(DateInputFormat[0].toLowerCase(), date[0]);
			DateMaping.put(DateInputFormat[1].toLowerCase(), date[1]);
			DateMaping.put(DateInputFormat[2].toLowerCase(), date[2]);
			value =  DateMaping.get(DateOutputFormat[0].toLowerCase())+ExpectedDelimiter+DateMaping.get(DateOutputFormat[1].toLowerCase())+ExpectedDelimiter+DateMaping.get(DateOutputFormat[2].toLowerCase());     
			DateFormat format = new SimpleDateFormat(ExpectedFormat, Locale.ENGLISH);
			try 
			{
				Date1=format.parse(value);
			} 
			catch (NumberFormatException | ParseException e) 
			{
				// TODO Auto-generated catch block
				throw new MacroException("ERROR OCCURS 	IN DATE FORMAT OF StarrGL MACRO", e);
			}  			
		   // System.out.println(value+"\t"+Date1);  						
		
		
		return Date1;
		
	}
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	protected String  Lookup(String Lookup1, PropertiesHandle configFile) throws  MacroException
	{
		
		DatabaseOperation Lookup = new DatabaseOperation();		
		HashMap<String,String> LookupMap = new HashMap<String,String>();
		try 
		{
		LinkedHashMap<Integer, LinkedHashMap<String, String>> tableLookup = Lookup.GetDataObjects(configFile.getProperty("lookup_query"));
			for (Entry<Integer, LinkedHashMap<String, String>> entry : tableLookup.entrySet())	
			{
				LinkedHashMap<String, String> rowLookup = entry.getValue();
				LookupMap.put(rowLookup.get("LookupData"), rowLookup.get("LookupValue"));				
				
			}
		} 
		catch (DatabaseException e) 
		{
			// TODO Auto-generated catch block
			throw new MacroException("ERROR OCCURS 	IN LOOKUP TABLE OF StarrGL MACRO", e);
		}
		
		if (LookupMap.get(Lookup1)==null)
		{
			return "Other";
		}
		else
		{
			return LookupMap.get(Lookup1);
		}
	}
	
	protected boolean isInteger(String s) 
	{
	    try 
	    { 
	    	
	        Integer.parseInt(s); 
	    } 
	    catch(NumberFormatException e) 
	    { 
	        return false; 
	    } 
	    catch(NullPointerException e) 
	    {
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}
	protected boolean isFloat(String s)
	{
		try
		{
			Float.parseFloat(s);
		}
		catch(NumberFormatException e) 
	    { 
	        return false; 
	    } 
	    catch(NullPointerException e) 
	    {
	        return false;
	    }
		 return true;
	}
	
	protected String IntegertoString (String s)
	{
		return s;
		
	}
	
	protected int ReplaceComma(String s)
	{
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);
		boolean b = m.find();
		int num=0;
		if (b)
		{
			 s = s.replaceAll("(?<=\\d),(?=\\d)", "");
			 num= Integer.parseInt(s);
		}
		else
		{
			num=Integer.parseInt(s);
		}

		return num;
		
	}
	
	protected String Version(String s)
	{
		
		s=s.substring(1);
		s=s.replace("-", "_");
		return s;
		
	}
	
	public static void main(String args[]) throws PropertiesHandleException, DatabaseException, MacroException
	{
		DatabaseOperation objectInput = new DatabaseOperation();
		DatabaseOperation objectOutput = new DatabaseOperation();
		Product2Macro MG;
		PropertiesHandle configFile=null;
		
		configFile = new PropertiesHandle("E:\\RestFullAPIDeliverable\\Devolpement\\admin\\STARR-GL\\Rating\\config\\config.properties");
		objectInput.ConnectionSetup(configFile);
		 
		 LinkedHashMap<Integer, LinkedHashMap<String, String>> inputtable = objectInput.GetDataObjects(configFile.getProperty("input_query"));
		 Iterator<Entry<Integer, LinkedHashMap<String, String>>> inputtableiterator = inputtable.entrySet().iterator();
		 LinkedHashMap<Integer, LinkedHashMap<String, String>>  outputtable = objectOutput.GetDataObjects(configFile.getProperty("output_query"));
		 Iterator<Entry<Integer, LinkedHashMap<String, String>>> outputtableiterator = outputtable.entrySet().iterator();
		 int rowIterator = 1;
		 while (inputtableiterator.hasNext() && outputtableiterator.hasNext()) 
			{
				Entry<Integer, LinkedHashMap<String, String>> inputentry = inputtableiterator.next();
				Entry<Integer, LinkedHashMap<String, String>> outputentry = outputtableiterator.next();
		        LinkedHashMap<String, String> inputrow = inputentry.getValue();
		        LinkedHashMap<String, String> outputrow = outputentry.getValue();
		        System.out.println("TestData "+inputrow.get("Testdata")+"-----Flag_for_execution "+inputrow.get("Flag_for_execution"));
		        if(inputrow.get("Flag_for_execution").equals("Y"))
				{
					MG=new Product2Macro(configFile);
					MG.LoadSampleRatingmodel(configFile, inputrow);
					MG.GenerateExpected(inputrow, configFile);
					MG.PumpinData(inputrow, configFile);
					MG.PumpoutData(outputrow,inputrow, configFile);
				}
		        inputrow.put("Flag_for_execution", "Completed");	
		        objectInput.UpdateRow(rowIterator, inputrow);
		        objectOutput.UpdateRow(rowIterator, outputrow);
		        rowIterator++;
		        
			}
	}

}