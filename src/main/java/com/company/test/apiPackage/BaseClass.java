package com.company.test.apiPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.company.test.Configuration.PropertiesHandle;
import com.company.test.exception.APIException;
import com.company.test.exception.DatabaseException;
import com.company.test.exception.HTTPHandleException;
import com.company.test.exception.POIException;
import com.company.test.exception.RequestFormatException;
import com.company.test.util.api.*;
import com.company.test.util.common.DatabaseOperation;
import com.company.test.util.common.ExcelOperationsPOI;
import com.jayway.jsonpath.PathNotFoundException;
import freemarker.template.TemplateException;

public class BaseClass 
{
	protected RequestHandler sampleInput = null;
	protected RequestResponse request = null;
	protected RequestResponse response = null;
	protected LinkedHashMap<String, String> XmlElements = null;
	protected LinkedHashMap<String, String> jsonElements = null;
	protected PropertiesHandle config = null;
	protected LinkedHashMap<String, String> input = null;
	protected LinkedHashMap<String, String> output = null;
	protected HttpHandle http = null;
	protected DBColoumnVerify InputColVerify = null;
	protected DBColoumnVerify OutputColVerify = null;
	protected DBColoumnVerify StatusColVerify = null;
	protected ArrayList<String> errorParentname = new ArrayList<String>();
	protected ArrayList<String> errorMessage=new ArrayList<String>();
	protected DBColoumnVerify conditioncheck = new DBColoumnVerify();
	protected LinkedHashMap<Integer, LinkedHashMap<String, String>> table1;

	protected long start;
	protected long end;
	public String tokenGenerator(PropertiesHandle config)
	{
		/*String Token="";
		 * try
		{
			System.out.println(config.getProperty("AuthenticationURL"));
			HttpHandle http = new HttpHandle(config.getProperty("AuthenticationURL"),"POST");
			http.AddHeader("Content-Type", config.getProperty("content_type"));
			String input_data = "{  \"ServiceRequestDetail\": { \"OwnerId\": \""+config.getProperty("OwnerID")+"\", \"ResponseType\": \"JSON\", \"BrowserIp\": \"192.168.5.140\", \"ServiceRequestVersion\": \"2.0\" }, \"UserCredential\": { \"UserName\": \""+config.getProperty("Userneme")+"\",    \"Password\": \""+config.getProperty("Password")+"\"  } }";
			http.SendData(input_data);
			String response_string = http.ReceiveData();	
			System.out.println(input_data+"/n/n/n"+response_string);
			JsonHandle response = new JsonHandle();
			//response.StringToFile(response_string);
			//response.FileToString();
			Token = Token+response.readToken("$..Token",response_string).replaceAll("\\[\"", "").replaceAll("\"\\]", "").replaceAll("\\\\","");
			System.out.println(Token);
		}
		catch(Exception e)
		{
			System.out.println("Error in Generating Token");
			e.printStackTrace();
		}*/
		return config.getProperty("AuthenticationToken");
	}
	
//------------------------------------------------------------------------------------------------------------------------------------------------------
	public void urltopdf(String URL,String path) throws IOException
	{
		System.setProperty("jsse.enableSNIExtension", "false");	
		URL website = new URL(URL);
		Path targetPath = new File(path).toPath();
		InputStream in = website.openStream();		
		Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);		
	}
	
//---------------------------------------------------------------LOAD SAMPLE REQUEST--------------------------------------------------------------------	
	public void LoadSampleRequest(LinkedHashMap<String, String> InputData) throws APIException
	{
		this.input = InputData;
		
		try
		{
			sampleInput = new RequestHandler(config);
			sampleInput.openTemplate();
		}
		catch(IOException|ClassNotFoundException| DatabaseException e)
		{
			throw new APIException("Error in load sample request", e);
		}
	}
	
	public void PumpDataToRequest(LinkedHashMap<String, String> commonmap,LinkedHashMap<String, String> InputData) throws APIException 
	{
		try
		{
			LinkedHashMap<Integer, LinkedHashMap<String, String>> tableInputColVerify =  InputColVerify.GetDataObjects(config.getProperty("InputColQuery"));
			sampleInput.LoadData(tableInputColVerify, InputData);
			sampleInput.PumpinDatatoRequest(tableInputColVerify,InputData,commonmap);	
			sampleInput.saveJsontoPath(config.getProperty("request_location")+input.get("Testdata")+".json");
		}
		catch( DatabaseException| TemplateException| IOException e)
		{
			e.printStackTrace();
			throw new APIException("Error in pumpData to request", e);
		}
		
	}
	

//------------------------------------------------------------CONVERTING REQUEST TO STRING--------------------------------------------------------------	
	public String RequestToString(String Token) throws APIException
	{
	  try 
	  {
		  request = new JsonHandle(config.getProperty("request_location")+input.get("Testdata")+".json");
		 // request.write("$..Token", Token);
		  return request.FileToString();
	  } 
	  catch (RequestFormatException e)
	  {
		  throw new APIException("ERROR OCCURS IN REQUEST TO STRING FUNCTION -- BASE CLASS", e);
	   }
	}
	
//-------------------------------------------------------------ADDING HEADER || TOKENS------------------------------------------------------------------	
	public void AddHeaders(String Token) throws APIException
	{
		try
		{
			http = new HttpHandle(config.getProperty("test_url"),"POST");
			http.AddHeader("Content-Type", config.getProperty("content_type"));
			http.AddHeader("Token", Token);
		}
		catch(HTTPHandleException e)
		{
			throw new APIException("ERROR ADD HEADER FUNCTION -- BASE CLASS", e);
		}
	}

//------------------------------------------------------------STORING RESPONSE TO FOLDER----------------------------------------------------------------	
	public void SendAndReceiveData() throws APIException 
	{
		try
		{
			String input_data= null;
			input_data = request.FileToString();
			start = System.currentTimeMillis();
		    http.SendData(input_data);
			String response_string = http.ReceiveData();
		    end = System.currentTimeMillis();
			response = new JsonHandle(config.getProperty("response_location")+input.get("Testdata")+".json");
			response.StringToFile(response_string);
		}
		catch(RequestFormatException | HTTPHandleException e)
		{
			e.printStackTrace();
			throw new APIException("ERROR IN SEND AND RECIEVE DATA FUNCTION -- BASE CLASS", e);
		}
	}
	
//-------------------------------------------------------------CONVERTING RESPONSE TO STRING------------------------------------------------------------
	public String ResponseToString() throws APIException 
	{
		try
		{
			return response.FileToString();
		}
		catch(RequestFormatException e)
		{
			throw new APIException("ERROR IN RESPONSE TO STRING FUNCTION -- BASE CLASS", e);
		}
	}
	
//-----------------------------------------------------------UPDATING RESPONSE DATA TO DATABASE---------------------------------------------------------	
	public LinkedHashMap<String, String> SendResponseDataToFile(LinkedHashMap<String, String> output) throws APIException
	{
		try
		{
			LinkedHashMap<Integer, LinkedHashMap<String, String>> tableOutputColVerify = OutputColVerify.GetDataObjects(config.getProperty("OutputColQuery"));		
			for (Entry<Integer, LinkedHashMap<String, String>> entry : tableOutputColVerify.entrySet())	
			{
				LinkedHashMap<String, String> rowOutputColVerify = entry.getValue();
				String condition = rowOutputColVerify.get("OutputColumnCondtn");
				if(conditioncheck.ConditionReading(condition, input)&& (rowOutputColVerify.get("Flag").equalsIgnoreCase("Y")))
				{
					try
					{
						///System.out.println("Writing Response to Table");
						//System.out.println(rowOutputColVerify.get(config.getProperty("OutputColumn")));
						String actual = (response.read(rowOutputColVerify.get(config.getProperty("OutputJsonPath"))).replaceAll("\\[\"", "")).replaceAll("\"\\]", "").replaceAll("\\\\","");
						output.put(rowOutputColVerify.get(config.getProperty("OutputColumn")), actual);
						//System.out.println(actual);
						output.put("flag_for_execution", "Completed");
						output.put("Time", (end-start) + " Millis");
					}
					catch(PathNotFoundException e)
					{
						output.put(rowOutputColVerify.get(config.getProperty("OutputColumn")), "Path not Found");
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

//---------------------------------------------------------------COMAPRISION FUNCTION-------------------------------------------------------------------	
	public LinkedHashMap<String, String> CompareFunction(LinkedHashMap<String, String> inputrow,LinkedHashMap<String, String> outputrow) throws APIException
	{		 
	 if(outputrow.get("Flag_for_execution").equals("SUCCESS"))
	{		
	    try
	    {
	    	LinkedHashMap<Integer, LinkedHashMap<String, String>> tableStatusColVerify = StatusColVerify.GetDataObjects(config.getProperty("OutputColQuery"));
	    	for (Entry<Integer, LinkedHashMap<String, String>> entry : tableStatusColVerify.entrySet()) 	
			{	
			    LinkedHashMap<String, String> rowStatusColVerify = entry.getValue();
			    String condition = rowStatusColVerify.get("OutputColumnCondtn");
			    if(conditioncheck.ConditionReading(condition, inputrow) && (rowStatusColVerify.get("Comaparision_Flag").equalsIgnoreCase("Y")))
				{
					String ExpectedColumn = rowStatusColVerify.get(config.getProperty("ExpectedColumn"));
					String ActualColumn = rowStatusColVerify.get(config.getProperty("OutputColumn"));
					String StatusColumn = rowStatusColVerify.get(config.getProperty("StatusColumn"));
					if(!(StatusColumn.equals("")) && !(ExpectedColumn.equals("")))
					{
						if(premium_comp(outputrow.get(ExpectedColumn),outputrow.get(ActualColumn)))
						{
							outputrow.put(StatusColumn, "Pass");
						}
						else
						{
							outputrow.put(StatusColumn, "Fail");
							analyse(rowStatusColVerify,outputrow);
						}
					}
				}
			}
			String message = "";
			for(int i=0;i<errorMessage.size();i++)
			{
				message=message+errorMessage.get(i)+",";
			}
			if(message.equals(""))
			{
				outputrow.put("AnalyserResult", "Pass");
			}
			else
			{
				outputrow.put("AnalyserResult", message.substring(0, message.length() - 2)+" Failed");
			}
			errorMessage.clear();
			errorParentname.clear();
			return outputrow;

	    }	
	    catch(DatabaseException e)
	    {
	    	throw new APIException("ERROR IN DB COMPARISON FUNCTION -- BASE CLASS", e);
	    }
	}
	 return outputrow;
 }
	
//-----------------------------------------------------PRIVATE FUNCTION FOR SUPPORTING COMPARISON FUNCTION---------------------------------------------------	
	protected static boolean premium_comp(String expected,String actual)
	{
		
		boolean status = false;
		if(actual == null||actual.equals("")||actual.isEmpty())
		{
			if((expected == null || expected.equals("")||expected.equals("0") || expected.equals("0.0")))
			{
				status = true;
				return status;
			}
		}
		if(expected == null||expected.equals("")||expected.isEmpty())
		{
			if(actual == null|| actual.equals("")||actual.equals("0") || actual.equals("0.0"))
			{
				status = true;
				return status;
			}
		}
		if(actual!=null && expected!=null)
		{
			expected = expected.replaceAll("\\[\"", "");
    		actual = actual.replaceAll("\\[\"", "");	
    		expected = expected.replaceAll("\"\\]", "");
    		actual = actual.replaceAll("\"\\]", "");
    		expected = expected.replaceAll("\\$", "");
    		actual = actual.replaceAll("\\$", "");

    	//System.out.println(expected+"-----------"+actual);
    	
    		if(actual.matches(".*[a-z].*")||expected.matches(".*[a-z].*")||actual.matches(".*[A-Z].*")||expected.matches(".*[A-Z].*")||actual.equals("[]")||expected.matches("[a-zA-Z]")||expected.equals("")||actual.matches("[a-zA-Z]")||actual.matches("[M|D|C|L|X|V|I]*")||expected.matches("[M|D|C|L|X|V|I]*"))
    		{
    			//System.out.println("expected or actual is string");
    			if(expected.equals(actual))
        		{
        			status = true;
        			return status;
        		}
    		}
    		else	
    		{
	    		expected = Double.toString(Math.round(Double.parseDouble(expected)));
	    		actual = Double.toString(Math.round(Double.parseDouble(actual)));
	    		if(expected.equals(actual))
	    		{
	    			status = true;
	    			return status;
	    		}
    		}
		}

		return status;	
		
	}

	protected void analyse(LinkedHashMap<String, String> Conditiontablerow,LinkedHashMap<String, String> outputrow ) throws DatabaseException 
	{		
		boolean flag = false;
		
		if(outputrow.get(Conditiontablerow.get("StatusColumn")).equals("Pass"))
		{		

		}

		else if(outputrow.get(Conditiontablerow.get("StatusColumn")).equals("Fail"))
		{	
			String[] Parentname =Conditiontablerow.get("ParentName").split(";");
			int noOfParentname=Parentname.length;
			for(int i=0;i<noOfParentname;i++)
			{
				errorParentname.add(Parentname[i]);
				if(!this.ifexist(Conditiontablerow.get("NodeName")))
				{
					if(flag == false)
					{
						errorMessage.add(Conditiontablerow.get("Message"));
						flag = true;
					}
				}
			}
						
		}

	}

	protected boolean ifexist (String NodeName)
	{
		boolean exist = false;
		int arraylength =errorParentname.size();
		for(int i = 0; i<arraylength;i++)
		{
			String existParentName =errorParentname.get(i);
			if(existParentName.equals(NodeName))
			{
				exist = true;
				break;
			}
		}
		return exist;
	}
	
	
	protected String excelreportlocation;
	public void generateReport(PropertiesHandle config,String ReportPath) throws DatabaseException, POIException, FileNotFoundException, SQLException, IOException
	{
		try 
		{
			String Samplepath = config.getProperty("report_template_location")+"ResultTemplate.xls";
			ExcelOperationsPOI sample=new ExcelOperationsPOI(Samplepath);
			sample.Copy(Samplepath, ReportPath);
			sample.save();
		    
			this.ExportToExcelTable(config.getProperty("TestcaseQuery"), ReportPath, "Testcases");
			this.ExportToExcelTable(config.getProperty("resultQuery"), ReportPath, "ComparisonResults");
		}
		catch(Exception e) 
		{
			System.out.print("error in copy Sample Report Template");
			e.printStackTrace();
		}
	}
	
	public void comparisonReport(String excelreportlocation1) throws DatabaseException, POIException
	{
		DatabaseOperation db=new DatabaseOperation();
		table1=db.GetDataObjects("SELECT AnalyserResult, COUNT(*) as NoOfCount FROM "+config.getProperty("outputTable")+"  GROUP BY AnalyserResult");
		Iterator<Entry<Integer, LinkedHashMap<String,String>>> inputtableiterator = table1.entrySet().iterator();
		ExcelOperationsPOI ob=new ExcelOperationsPOI(excelreportlocation1);
		ob.getsheets("TestReport");
		ob.write_data(5, 4,config.getProperty("Project")+"-"+config.getProperty("API"));
		Date today=new Date();
		ob.write_data(5, 7,today);
		ob.write_data(5, 14,config.getProperty("ExecutionName"));
		int	row=9;
		int si_no=1;
		while (inputtableiterator.hasNext()) 
		{
			 Entry<Integer, LinkedHashMap<String, String>> inputentry = inputtableiterator.next();
			 LinkedHashMap<String, String> inputrow = inputentry.getValue();
			
			    ob.write_data(row, 2,si_no );
			    ob.write_data(row,3,inputrow.get("AnalyserResult"));
			    ob.write_data(row,4,Integer.parseInt(inputrow.get("NoOfCount")));
				
			 row++;
			 si_no++;
			 
		}
		ob.refresh();
		ob.save();
	}
	
	@SuppressWarnings("resource")
	public void ExportToExcelTable(String Query,String FileToExport,String Sheet) throws DatabaseException, SQLException, FileNotFoundException, IOException
	{
		
		try
		{
			//System.out.println("Exporting Report with Test cases to Excel");
			DatabaseOperation db=new DatabaseOperation();
			ResultSet rs=null;
			HSSFWorkbook workBook=null;
			HSSFSheet sheet =null;
			rs=db.GetQueryResultsSet(Query);
			File file = new File(FileToExport);
			if(!file.exists())                               //Creation of Workbook and Sheet
			{
				workBook =new HSSFWorkbook();
			}
			else
			{
				workBook = new HSSFWorkbook(new FileInputStream(FileToExport));
			}
			sheet = workBook.createSheet(Sheet);
                                                                                         //import columns to Excel
			ResultSetMetaData metaData=rs.getMetaData();
			int columnCount=metaData.getColumnCount();
			ArrayList<String> columns = new ArrayList<String>();
			for (int i = 1; i <= columnCount; i++) 
			{
				String columnName = metaData.getColumnName(i);
				columns.add(columnName);
			}
		    
			HSSFRow row = sheet.createRow(0);
			int  Fieldcol=0; 
			for (String columnName : columns) 
			{
				row.createCell(Fieldcol).setCellValue(columnName);
				Fieldcol++;
			}
                                                            //import column values to Excel	
			int ValueRow=1;
			do
			{
				int Valuecol=0;
				HSSFRow valrow = sheet.createRow(ValueRow);
				for (String columnName : columns)
				{
					String value = rs.getString(columnName);
					valrow.createCell(Valuecol).setCellValue(value);
					Valuecol++;
				}
				ValueRow++;
			} while (rs.next());
		                                                    //Save the Details and close the File
		
	          FileOutputStream out = new FileOutputStream(FileToExport);
	          workBook.write(out);
	          out.close();
	          System.out.println("REPORT GENERATED SUCCESSFULLY ON DISK");
		 }
	     catch (Exception e) 
	     {
	    	 System.out.println("Error in Exporting the Testcase with Results");	 
	       e.printStackTrace();
	     }
	}
	
	//==========================================================================================================================================
	public LinkedHashMap<String, String> differrence(LinkedHashMap<String, String> inputrow,LinkedHashMap<String, String> outputrow) throws APIException
	{		 
	 if(outputrow.get("Flag_for_execution").equals("SUCCESS"))
	{		
	    try
	    {
	    	LinkedHashMap<Integer, LinkedHashMap<String, String>> tableStatusColVerify = StatusColVerify.GetDataObjects(config.getProperty("OutputColQuery"));
	    	for (Entry<Integer, LinkedHashMap<String, String>> entry : tableStatusColVerify.entrySet()) 	
			{	
			    LinkedHashMap<String, String> rowStatusColVerify = entry.getValue();
			    String condition = rowStatusColVerify.get("OutputColumnCondtn");
			    long expected;
			    long actual;
			    long diff;
			    
			    if(conditioncheck.ConditionReading(condition, inputrow) && (rowStatusColVerify.get("Comaparision_Flag").equalsIgnoreCase("Y")))
				{
					String ExpectedColumn = rowStatusColVerify.get(config.getProperty("ExpectedColumn"));
					String ActualColumn = rowStatusColVerify.get(config.getProperty("OutputColumn"));
					String StatusColumn = rowStatusColVerify.get(config.getProperty("StatusColumn"));
					if(!(ActualColumn.equals("")) && !(ExpectedColumn.equals("")))
					{
						expected=(long) Double.parseDouble(outputrow.get(ExpectedColumn));
						actual=(long) Double.parseDouble(outputrow.get(ActualColumn));
						
                       diff=actual-expected;
                       outputrow.put(StatusColumn,Double.toString(diff));
			    		
				}
				}
			}

	    }	
	    catch(DatabaseException e)
	    {
	    	throw new APIException("ERROR IN DB COMPARISON FUNCTION -- BASE CLASS", e);
	    }
	}
	 return outputrow;
 }
	
	
}
