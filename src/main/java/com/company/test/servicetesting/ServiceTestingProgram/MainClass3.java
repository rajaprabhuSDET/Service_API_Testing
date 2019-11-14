package com.company.test.servicetesting.ServiceTestingProgram;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import com.company.test.Configuration.PropertiesHandle;
import com.company.test.apiPackage.API2;
import com.company.test.apiPackage.BaseClass2;
import com.company.test.exception.APIException;
import com.company.test.exception.DatabaseException;
import com.company.test.exception.POIException;
import com.company.test.exception.PropertiesHandleException;
import com.company.test.listener.FireEventAPI2;
import com.company.test.listener.Listener;
import com.company.test.listener.LogListener;
import com.company.test.util.common.DatabaseOperation;
import com.company.test.util.common.DirectoryManipulation;
import com.fasterxml.jackson.databind.ObjectMapper;


public class MainClass3
{	
	public static boolean quote = false;	
	public static LinkedHashMap<String, String> commonMap = new LinkedHashMap<String, String>();
	public static String[] apii;
	public static PropertiesHandle[] ConfigObjectRepository;
	public static DatabaseOperation[] OutputDBObjectRepository;
	public static DatabaseOperation[] inputDBObjectRepository;
	public static DatabaseOperation inputTable;
	public static Object[] OutputTableRepository;
	public static Object[] inputIndividualTableRepository;
	public static String InputtableQuery;
	public static String Token;
	public static DatabaseOperation db=null;
	public static Connection Conn=null;
	public static String ExecutionFlag;
	public static List<String> InputtableList;
	public static boolean TokenFlag;
	public static String[] inputTableNames;
	public static String[] outputTableNames;
	public static List<String> OutputtableList;
	
	public static LinkedHashMap<String,List<String>> testCaseToSNOMaping = new LinkedHashMap<String,List<String>>();
	
	@BeforeTest
	public void beforeTest() 
	{
		try
		{
			System.setProperty("jsse.enableSNIExtension", "false");
			System.setProperty("file.encoding","UTF-8");
			disableSslVerification();
			//System.setProperty("string.encode(\"ascii\", \"ignore\")");
			// Charset utff = StandardCharsets.US_ASCII;
			Field charset = Charset.class.getDeclaredField("defaultCharset");
			charset.setAccessible(true);
			charset.set(null,null);
			String apis = System.getProperty("Api");
			apii = apis.split("-");			
			//InputtableQuery="SELECT * FROM INPUT_Quote_GL_V6 a INNER JOIN INPUT_GL_PolicyIssuance_V3 b on a.`S.No` = b.`S.No` INNER JOIN INPUT_GL_Cancel_V2 c on b.`S.No` = c.`S.No`";
			ConfigObjectRepository=new PropertiesHandle[apii.length];
			OutputDBObjectRepository= new DatabaseOperation[apii.length];
			inputDBObjectRepository= new DatabaseOperation[apii.length];
			OutputTableRepository = new Object[apii.length];
			inputIndividualTableRepository = new Object[apii.length];
			
			InputtableList = new ArrayList<String> ();
			OutputtableList = new ArrayList<String> ();
			for(int i=0;i<apii.length;i++)
			{
				
				System.out.println(apii[i]);
				if(ConfigObjectRepository==null)
				{
					System.out.println("config is null"+i);
				}
				ConfigObjectRepository[i]=new PropertiesHandle(System.getProperty("Project"), apii[i], System.getProperty("Env"), System.getProperty("OutputChioce"), System.getProperty("UserName"), System.getProperty("JDBC_DRIVER"), System.getProperty("DB_URL"), System.getProperty("USER"), System.getProperty("password"), System.getProperty("Priority"),System.getProperty("ExecutionName"),System.getProperty("ModeofExecution"));
				inputTableNames = ConfigObjectRepository[i].getProperty("inputTable").split(",");
				
				for(String tbl : inputTableNames)
				{
					InputtableList.add(tbl);
				}
				
				outputTableNames = ConfigObjectRepository[i].getProperty("outputTable").split(",");
				
				for(String tbl1 : outputTableNames)
				{
					OutputtableList.add(tbl1);
				}
				
				OutputDBObjectRepository[i]= new DatabaseOperation();
				inputDBObjectRepository[i]= new DatabaseOperation();
				OutputDBObjectRepository[0].ConnectionSetup(ConfigObjectRepository[i]);
				inputDBObjectRepository[0].ConnectionSetup(ConfigObjectRepository[i]);
				System.out.println(ConfigObjectRepository[i].getProperty("output_query"));
				
				OutputDBObjectRepository[i].GetDataObjects("Select * From "+outputTableNames[0]);		
				inputDBObjectRepository[i].GetDataObjects("Select * From "+inputTableNames[0]);
				//System.out.println(ConfigObjectRepository[i].getProperty("output_query"));
				OutputTableRepository[i]=this.outputtable(ConfigObjectRepository[i]);
				inputIndividualTableRepository[i]=this.inputTables(ConfigObjectRepository[i]);
				
			}
			
			Conn=OutputDBObjectRepository[0].ConnectionSetup(ConfigObjectRepository[0]);
			if(ConfigObjectRepository[0].getProperty("ModeofExecution").equalsIgnoreCase("New"))
			{
				this.beforeTesting(ConfigObjectRepository[0]);
			}
			DatabaseOperation inputquery = new DatabaseOperation();
			InputtableQuery = "Select * From "+inputTableNames[0]+" t0" /*inputquery.buildJoinQuery(InputtableList)*/;
			String ProjectDBName = ConfigObjectRepository[0].getProperty("ProjectDBName");
				
			inputTable = new DatabaseOperation();
			inputTable.switchDB(ProjectDBName+"_"+ConfigObjectRepository[0].getProperty("UserDBName"));
			inputTable.GetDataObjects(InputtableQuery);
			
			ExecutionFlag=ConfigObjectRepository[0].getProperty("Execution_Flag");
						
			TokenFlag = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings({ "unchecked", "unlikely-arg-type" })
	@Test(dataProvider="PaaSTest")
	public void Api1(Integer RowIterator, Object inputtablerowobj) throws InterruptedException, DatabaseException, PropertiesHandleException, APIException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		try {
			LinkedHashMap<Integer, LinkedHashMap<String, String>> inputrow = new LinkedHashMap<Integer, LinkedHashMap<String, String>> ();
			ObjectMapper inputtableobjectMapper = new ObjectMapper();
			inputrow = inputtableobjectMapper.convertValue(inputtablerowobj, LinkedHashMap.class);
			
			String testcasenumber=testCaseToSNOMaping.get(String.valueOf(RowIterator)).get(0);
			//System.out.println(testcasenumber);
			//System.out.println(inputrow.get(testcasenumber));
			if(inputrow.get(testcasenumber).get("Flag_for_execution").equalsIgnoreCase("Y"))
			{
				//System.out.println("S.No--->"+inputrow.get(testcasenumber).get("S_No")+"--->TestDataName--->"+inputrow.get(testcasenumber).get("Testdata")+"--->Flag--->"+inputrow.get(testcasenumber).get("Flag_for_execution"));
				for(int i=0;i<apii.length;i++)
				{
					disableSslVerification();
					GenericMethod(RowIterator-1, inputtablerowobj, (Object[]) OutputTableRepository[i], apii[i], ConfigObjectRepository[i],inputTable,OutputDBObjectRepository[i],inputDBObjectRepository[i],(Object[]) inputIndividualTableRepository[i]);
				}
			}
			else
			{
				System.out.println("TestData " + inputrow.get(testcasenumber).get("S_No") + "---flag_for_execution N");
			}
			commonMap.clear();
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public void GenericMethod(Integer RowIterator, Object inputtablerowobj, Object[] outputtablerowobject, String apis, PropertiesHandle configuration,DatabaseOperation inputTable, DatabaseOperation OutputTable, DatabaseOperation inputIndividualTable,Object[] individualInputTablerowobject)throws InterruptedException, DatabaseException, InterruptedException , DatabaseException, PropertiesHandleException, APIException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		try
		{
			//PropertiesHandle config; 
				
			LinkedHashMap<Integer,LinkedHashMap<String, String>> inputrow = new LinkedHashMap<Integer,LinkedHashMap<String, String>> ();
			LinkedHashMap<String, String> outputrow = new LinkedHashMap<String, String> ();
			LinkedHashMap<String, String> individualinputrow = new LinkedHashMap<String, String> ();
			ObjectMapper inputtableobjectMapper = new ObjectMapper();
			ObjectMapper outputtableobjectMapper = new ObjectMapper();
			ObjectMapper individualInputtableobjectMapper = new ObjectMapper();
			Object outputtablerowobj=new Object(); 
			Object individualinputtablerowobj=new Object();
			//System.out.println(RowIterator);
			outputtablerowobj = outputtablerowobject[RowIterator];
			individualinputtablerowobj = individualInputTablerowobject[RowIterator];
			String outputtablechoice = configuration.getProperty("output_in_same_table");
			String classname = configuration.getProperty("ClassName");		
			
			Class<?> cl = Class.forName("com.solartis.test.apiPackage."+classname);
			Constructor<?> cons = cl.getConstructor(com.company.test.Configuration.PropertiesHandle.class);
			API2 api = (API2) cons.newInstance(configuration);
			FireEventAPI2 fireEventAPI = new FireEventAPI2(api);
			Listener listener = new LogListener();
			fireEventAPI.addListener(listener);
			
			if(inputtablerowobj==null||outputtablerowobj==null)
			{
				System.out.println("objects is null.............");
			}
		
			inputrow = inputtableobjectMapper.convertValue(inputtablerowobj, LinkedHashMap.class);
			outputrow = outputtableobjectMapper.convertValue(outputtablerowobj, LinkedHashMap.class);
			individualinputrow = individualInputtableobjectMapper.convertValue(individualinputtablerowobj, LinkedHashMap.class);
			if (inputrow==null)
			{
				System.out.println("inputrow is null");
			}
			//System.out.println("TestData : " + inputrow.get("S.No"));  	
			//if(inputrow.get("1").get("Flag_for_execution").equalsIgnoreCase("Y"))
			//{
				//System.out.println("TestData " + inputrow.get("1").get("S_No") + "  API--"+apis );		
				
				/*if (TokenFlag)
				{
					if(ExecutionFlag.equalsIgnoreCase("ActualOnly")||ExecutionFlag.equalsIgnoreCase("ActualandComparison")||ExecutionFlag.equalsIgnoreCase("Comparison")||ExecutionFlag.equalsIgnoreCase("ResponseOnly"))
				    {
					    Token=fireEventAPI.tokenGenerator(ConfigObjectRepository[0]);
				    }
					TokenFlag=false;
				}*/
				//inputrow.get("1").put("Token", Token);
								
				fireEventAPI.LoadSampleRequest(inputrow);//LOADING SAMPLE REQUEST
	                            
				fireEventAPI.PumpDataToRequest(commonMap,inputrow);//PUMPING TESTDATA TO SAMPLEREQUEST s
				
				if(ExecutionFlag.equalsIgnoreCase("ActualOnly")||ExecutionFlag.equalsIgnoreCase("ActualandComparison")||ExecutionFlag.equalsIgnoreCase("Comparison")||ExecutionFlag.equalsIgnoreCase("ResponseOnly"))
			    {
					fireEventAPI.RequestToString(Token);//SHOWING REQUEST IN LOG 
								
					fireEventAPI.AddHeaders(Token);//ADDING HEADER || TOKENS || EVENTS FOR HITTING REQUEST
									
					fireEventAPI.SendAndReceiveData();//RECIEVING AND STORING RESPONSE TO THE FILE
					
					//fireEventAPI.ReceiveData();
									
					fireEventAPI.ResponseToString();//SHOWING RESPONSE IN LOG 
			    }	
				if(ExecutionFlag.equalsIgnoreCase("ActualOnly")||ExecutionFlag.equalsIgnoreCase("ActualandComparison")||ExecutionFlag.equalsIgnoreCase("ExpectedOnly")||ExecutionFlag.equalsIgnoreCase("Comparison"))
				{												  
					if(outputtablechoice.equalsIgnoreCase("Y"))//INPUT AND OUT DB TABLE ARE SAME
					{
						/*List<String> inputroww = fireEventAPI.SendResponseDataToFile(inputrow);//FETCHING DATA FROM RESPONSE AND STORE THEM INTO THE DATABASE TABLE
						//commonMap.putAll(inputrow);
						//inputTable.UpdateRow(RowIterator, inputrow);//UPDATE DB TABLE ROWS AFTER INSERTING RESPONSE DATA
						for (int i = 0; i < inputroww.size(); i++) {
							OutputTable.insertRow(inputroww.get(i));
						}*/
					}
					else//INPUT AND OUT DB TABLE ARE DIFFERENT
					{
						List<String> outputroww = fireEventAPI.SendResponseDataToFile(outputrow);//FETCHING DATA FROM RESPONSE AND STORE THEM INTO THE DATABASE TABLE
						//commonMap.putAll(outputrow);
						//System.out.println(RowIterator+"-----------"+outputrow);
						//OutputTable.UpdateRow(RowIterator+1, outputrow);//UPDATE DB TABLE ROWS AFTER INSERTING RESPONSE DATA		
						//System.out.println("Update completed"); 
						for (int i = 0; i < outputroww.size(); i++) {
							OutputTable.insertRow(outputroww.get(i));
						}
						
					}					
				}	
				if(ExecutionFlag.equalsIgnoreCase("ActualOnly")||ExecutionFlag.equalsIgnoreCase("Comparison")||ExecutionFlag.equalsIgnoreCase("ActualandComparison")||ExecutionFlag.equalsIgnoreCase("Difference"))
				{
					if(outputtablechoice.equalsIgnoreCase("Y"))
					{									
						inputrow = fireEventAPI.CompareFunction(inputrow,outputrow);//CALLING COMPARING FUNCTION									     
						
						for(int i=0; i<inputrow.size();i++)
						{
						inputTable.UpdateRow(RowIterator, inputrow.get(i));
						}
					
					}
					else
					{
						System.out.println("Coming to Comparison");
						LinkedHashMap<Integer,LinkedHashMap<String, String>> individualrow = fireEventAPI.CompareFunction(inputrow,outputrow);//CALLING COMPARING FUNCTION
						//OutputTable.UpdateRow(RowIterator+1, outputrow);
						for (Map.Entry<Integer,LinkedHashMap<String, String>> entry : individualrow.entrySet())  
						{
							//System.out.println(individualrow.get(String.valueOf(i)));
							entry.getValue().put("Flag_for_execution", "Completed");
							inputIndividualTable.UpdateRow(Integer.parseInt(entry.getValue().get("S_No")), entry.getValue());
						}
						//commonMap.putAll(outputrow);									
					}
				}
				
				
				if(ExecutionFlag.equalsIgnoreCase("Difference"))
				{
					//inputrow = fireEventAPI.differrence(inputrow,outputrow);
					//inputTable.UpdateRow(RowIterator+1, inputrow);
				}
				//System.out.println(individualinputrow.get("Flag_for_execution"));				
				individualinputrow.put("Flag_for_execution", "Completed");
				//System.out.println(individualinputrow.get("Flag_for_execution"));
				//inputIndividualTable.UpdateRow(RowIterator+1, individualinputrow);//UPDATE DB TABLE ROWS AFTER COMPARSION
				//System.out.println(individualinputrow);
		//	}
		//else
		//	{
		//		System.out.println("TestData " +"---flag_for_execution N");
			//}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
		/*if(actualchoice.equals("Y") || statuschoice.equals("Y"))
		{
			output.MoveForward();
		}*/		
    }
	
	@DataProvider(name="PaaSTest")
	public Object[][] dpapi1() throws DatabaseException, NumberFormatException, SQLException
	{
		
		LinkedHashMap<Integer, LinkedHashMap<String, String>> inputtable;
		LinkedHashMap<Integer, LinkedHashMap<String, String>> testCaseIDMapping;
		ObjectMapper inputtableobjectMapper;		
		DatabaseOperation input = new DatabaseOperation();
		DatabaseOperation input11 = new DatabaseOperation();
		int noOfTestCase =Integer.parseInt(input.GetQueryResultsSet("SELECT count( DISTINCT(TestCaseID) ) as NoOfTestCases FROM "+inputTableNames[0]).getString("NoOfTestCases"));
		Object[][] combined = new Object[noOfTestCase][2];
		try {
			testCaseIDMapping = input11.GetDataObjects(InputtableQuery);
			testCaseToSNOMaping=testCaseID(testCaseIDMapping);
			Iterator<Entry<String, List<String>>> itee= testCaseToSNOMaping.entrySet().iterator();
			while(itee.hasNext())
			{
				Entry<String, List<String>> entry = itee.next();
				System.out.println(entry.getKey()+"----"+entry.getValue());
				
				
			}
			int rowIterator = 0;
			for(int i=1;i<=noOfTestCase;i++) 
			{
				inputtable = input.GetDataObjects(InputtableQuery+" Where t0.TestCaseID="+i);
				//Iterator<Entry<Integer, LinkedHashMap<String, String>>> inputtableiterator = inputtable.entrySet().iterator();			
				//while (inputtableiterator.hasNext() ) 
				//{
				//System.out.println(inputtable);
					//Entry<Integer, LinkedHashMap<String, String>> inputentry = inputtableiterator.next();				
					//LinkedHashMap<String, String> inputrow = inputentry.getValue();	
				inputtableobjectMapper = new ObjectMapper();
				Object inputtablerowobject = inputtableobjectMapper.convertValue(inputtable, Object.class);				 
				combined[rowIterator][0] = rowIterator+1;
				combined[rowIterator][1] = inputtablerowobject;				 
				rowIterator++;
				//}  
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return combined;
	}
	
	private LinkedHashMap<String,List<String>> testCaseID(LinkedHashMap<Integer, LinkedHashMap<String, String>> testCaseIDMapping)
	{
		LinkedHashMap<String,List<String>> local = new LinkedHashMap<String,List<String>>();
		Iterator<Entry<Integer, LinkedHashMap<String, String>>> inputtableiterator = testCaseIDMapping.entrySet().iterator();			
		while (inputtableiterator.hasNext()) 
		{
			Entry<Integer, LinkedHashMap<String, String>> inputentry = inputtableiterator.next();
			LinkedHashMap<String, String> inputrow = inputentry.getValue();
			if(local.containsKey(inputrow.get("TestCaseID"))){
				local.get(inputrow.get("TestCaseID")).add(inputrow.get("S_No"));
			}
			else{
				 ArrayList<String> arrr=new ArrayList<String>();
				 arrr.add(inputrow.get("S_No"));
				local.put(inputrow.get("TestCaseID"), arrr);
			}
		}
		return local;		
	}
	
	public Object[] outputtable(PropertiesHandle config) throws DatabaseException
	{
		
		ObjectMapper outputtableobjectMapper;
		LinkedHashMap<Integer, LinkedHashMap<String, String>> outputtable;
		DatabaseOperation output = new DatabaseOperation();
		outputtable = output.GetDataObjects("Select * From "+outputTableNames[0]);
		Object[] outputableobject = new Object[outputtable.size()];
		Iterator<Entry<Integer, LinkedHashMap<String, String>>> outputtableiterator = outputtable.entrySet().iterator();
		int rowIterator = 0;
		while (outputtableiterator.hasNext()) 
		{
			Entry<Integer, LinkedHashMap<String, String>> outputentry = outputtableiterator.next();
			LinkedHashMap<String, String> outputrow = outputentry.getValue();
			outputtableobjectMapper = new ObjectMapper();
			Object outputtablerowobject = outputtableobjectMapper.convertValue(outputrow, Object.class);
			outputableobject[rowIterator]=outputtablerowobject;
			rowIterator++;
		}
		return outputableobject;		
	}
	
	public Object[] inputTables(PropertiesHandle config) throws DatabaseException
	{
		
		ObjectMapper outputtableobjectMapper;
		LinkedHashMap<Integer, LinkedHashMap<String, String>> outputtable;
		DatabaseOperation output = new DatabaseOperation();
		outputtable = output.GetDataObjects("Select * From "+inputTableNames[0]);
		Object[] outputableobject = new Object[outputtable.size()];
		Iterator<Entry<Integer, LinkedHashMap<String, String>>> outputtableiterator = outputtable.entrySet().iterator();
		int rowIterator = 0;
		while (outputtableiterator.hasNext()) 
		{
			Entry<Integer, LinkedHashMap<String, String>> outputentry = outputtableiterator.next();
			LinkedHashMap<String, String> outputrow = outputentry.getValue();
			outputtableobjectMapper = new ObjectMapper();
			Object outputtablerowobject = outputtableobjectMapper.convertValue(outputrow, Object.class);
			outputableobject[rowIterator]=outputtablerowobject;
			rowIterator++;
		}
		return outputableobject;		
	}
	
	public void beforeTesting(PropertiesHandle config) throws POIException, SQLException, ClassNotFoundException, IOException
	{
		db=new DatabaseOperation();
		db.truncateTable(config.getProperty("inputTable"));
		db.truncateTable(config.getProperty("outputTable"));
		db.ImportDatatoDB(config.getProperty("TestdataPath"),Conn, config.getProperty("inputTable"), "Sheet1", "Import");
		db.insetRowWithSNO(config.getProperty("outputTable"), config.getProperty("inputTable"));
		DirectoryManipulation.deleteFileFromDirectory(config.getProperty("request_location"));
		DirectoryManipulation.deleteFileFromDirectory(config.getProperty("response_location"));
		DirectoryManipulation.deleteFileFromDirectory(config.getProperty("TargetPath"));
		DirectoryManipulation.deleteFileFromDirectory(config.getProperty("report_location"));			
	}
	
	@AfterTest
	public void connectionclose() throws DatabaseException, POIException, APIException
	{
		BaseClass2 base = new BaseClass2();
		try
		{
			String DateandTime = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
			String ReportPath = ConfigObjectRepository[0].getProperty("report_location")+ConfigObjectRepository[0].getProperty("ExecutionName")+"_AnalysisReport_"+DateandTime+".xls";
			base.generateReport(ConfigObjectRepository[0], ReportPath);
			
			if(ConfigObjectRepository[0].getProperty("Execution_Flag").equals("ActualandComparison")||ConfigObjectRepository[0].getProperty("Execution_Flag").equals("Comparison"))
		    {
				base.comparisonReport(ReportPath);
		    }

			DirectoryManipulation.zipFolder(ConfigObjectRepository[0].getProperty("ZipFolderPath"), ConfigObjectRepository[0].getProperty("OverallResults"));		
			DatabaseOperation.CloseConn();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
