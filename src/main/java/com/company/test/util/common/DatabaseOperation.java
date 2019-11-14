package com.company.test.util.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.company.test.Configuration.PropertiesHandle;
import com.company.test.exception.DatabaseException;
import com.company.test.exception.POIException;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbJoin;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.mysql.jdbc.PreparedStatement;

public class DatabaseOperation
{
	private static Connection conn = null;
	private static String JDBC_DRIVER = null;
	private static String DB_URL =null;
	private static String USER=null;
	private static String PASS =null;
	protected  String query = null;
	protected  Statement stmt = null;
	protected  ResultSet rs = null;
	protected  int rs_row = 1;
	protected   LinkedHashMap<Integer, LinkedHashMap<String, String>> table = null;
	protected ResultSetMetaData meta = null;
	
	public Connection ConnectionSetup(PropertiesHandle config) throws DatabaseException 
	{
		JDBC_DRIVER =config.getProperty("jdbc_driver");
		DB_URL = config.getProperty("db_url");
		USER=config.getProperty("db_username");
		PASS =config.getProperty("db_password");
		if(conn == null)
		{
			try 
			{
				Class.forName(JDBC_DRIVER);
			} 
			catch (ClassNotFoundException e) 
			{
				throw new DatabaseException("ERROR IN JDBC_DRIVER : " + JDBC_DRIVER, e);
			}
			try 
			{
				conn = DriverManager.getConnection(DB_URL,USER,PASS);
			} 
			catch (SQLException e) 
			{
				throw new DatabaseException("ERROR IN DB - URL / USERNAME / PASSWORD", e);	
			}	
		}	
		return conn;
	}
	
	public void switchDB(String db) throws DatabaseException
	{
		try
		{
			conn.setCatalog(db);
		}
		catch (SQLException e)
		{
			throw new DatabaseException("Error while switch DataBase", e);
		}
	}
	
	public static void ConnectionSetup(String JDBC_DRIVER, String DB_URL, String USER, String password) throws DatabaseException 
	{
		if(conn == null)
		{
			
			try 
			{
				Class.forName(JDBC_DRIVER);
			} 
			catch (ClassNotFoundException e) 
			{
				throw new DatabaseException("ERROR IN JDBC_DRIVER : " + JDBC_DRIVER, e);
			}
			try 
			{
				conn = DriverManager.getConnection(DB_URL,USER,password);
			} 
			catch (SQLException e) 
			{
				throw new DatabaseException("ERROR IN DB - URL / USERNAME / PASSWORD", e);	
			}	
		}		
	}
	
	public static void CloseConn() throws DatabaseException
	{
		try 
		{
			conn.close();
		} 
		catch (SQLException e) 
		{
			throw new DatabaseException("PROBLEM WITH CLOSING DB-CONNECTION", e);
		}
		conn = null;
	}
	
	public LinkedHashMap<Integer, LinkedHashMap<String, String>> GetDataObjects(String query) throws DatabaseException
	{
		this.query = query;
		LinkedHashMap<String, String> row = null;
		//System.out.println("Query"+this.query);
		try 
		{
			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
		    rs =    stmt.executeQuery(this.query);
		    table = new LinkedHashMap<Integer, LinkedHashMap<String, String>>();
	        meta = rs.getMetaData();        
	        while (rs.next())
	        {
	        	row = new LinkedHashMap<String, String>();
	            for (int columnIterator = 1; columnIterator <= meta.getColumnCount(); columnIterator++) 
	            {
	                String key = meta.getColumnName(columnIterator);
	                String value = rs.getString(key);
	                row.put(key, value);
	            }
	            table.put(rs_row, row);
	            rs_row = rs_row + 1;   
	        } 
	        return table;  
		} 
		catch (SQLException e) 
		{
			throw new DatabaseException("PROBLEM WITH RESULT-SET OBTAINED FROM DB",e);
		}
		
	}
	
	/*public void ExecuteQuery(String query) throws SQLException
	{
		stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
	    rs =    stmt.executeQuery(query);
	}*/
	
	public void UpdateRow(Integer rowNumber, LinkedHashMap<String, String> row)
	{		
		try
		{
			java.sql.PreparedStatement  stmt = null;
		ResultSet rs = null;
		stmt = conn.prepareStatement (this.query,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
	    rs =    stmt.executeQuery(this.query);
		rs.first();
		ResultSetMetaData meta = rs.getMetaData();
	    int rowIterator = 1;
		do
		{
			if(rowNumber == rowIterator)
		    {
				for (int i = 1; i <= meta.getColumnCount(); i++)
				{
					
			       rs.updateString(meta.getColumnName(i), row.get(meta.getColumnName(i)));
			    }
				rs.updateRow();
		    }
		 
		    rowIterator++;
		 }while (rs.next());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void insertRow(String Query) throws SQLException
	{	
		java.sql.PreparedStatement ps = conn.prepareStatement(Query);
		ps.executeUpdate();
	}
	
	@SuppressWarnings("unused")
	public void insertRow(LinkedHashMap<String, String> row) throws SQLException
	{
		for (Entry<String, String> entry : row.entrySet())	
		{
			//System.out.println(entry.getValue());			
		}
		String insterQuery = "INSERT INTO MelActual(temp1) VALUES(temp2)";	
		
		StringBuffer temp1 = new StringBuffer();
		StringBuffer temp2 = new StringBuffer();
		for (Entry<String, String> entry : row.entrySet())	
		{
			temp2=temp2.append("'").append(entry.getValue()).append("'").append(",");
			temp1=temp1.append(entry.getKey()).append(",");
		}
		insterQuery=insterQuery.replace("temp1", temp1.substring(0, temp1.length() - 1)).replace("temp2", temp2.substring(0, temp2.length() - 1));
		//System.out.println(insterQuery);
		java.sql.PreparedStatement ps = conn.prepareStatement(insterQuery);
		ps.executeUpdate();
	}
	
	
	public void UpdateTable(LinkedHashMap<Integer, LinkedHashMap<String, String>> table) throws DatabaseException, SQLException
	{
		this.table = table;
		LinkedHashMap<String, String> row = null;
		ResultSetMetaData meta = rs.getMetaData();
		try 
		{
			rs.first();
		    int rowIterator = 1;
			do
			{
				for (int columnIterator = 1; columnIterator <= meta.getColumnCount(); columnIterator++) 
				{  
			       row = table.get(rowIterator);
			       rs.updateString(meta.getColumnName(columnIterator), row.get(meta.getColumnName(columnIterator)));
			    }
			 
			    rs.updateRow();
			    rowIterator++;
			 }while (rs.next());
		}	
		
		catch (SQLException e) 
		{
			throw new DatabaseException("PROBLEM WITH UPDATE ROW IN DB", e);
		}
	}
	

	public void createTable(String query) throws SQLException
	{
		this.query=query;
		 stmt = conn.createStatement();
		 //System.out.println(this.query);
		 stmt.execute(this.query);
	}
	
	public ResultSet GetQueryResultsSet(String query) throws DatabaseException
	{
		this.query = query;
		try 
		{
			stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE);
		    rs =    stmt.executeQuery(this.query);
		    rs.first();
		} 
		catch (SQLException e) 
		{
			throw new DatabaseException("PROBLEM WITH RESULT-SET OBTAINED FROM DB",e);
		}
		return rs;
	}
	
	@SuppressWarnings("resource")
	public static void ExportToExcelTable(String Query,String FileToExport,String Sheet) throws DatabaseException, SQLException, FileNotFoundException, IOException
	{
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
		      //System.out.println(columnName);
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
		try
	     {
	          FileOutputStream out = new FileOutputStream(FileToExport);
	          workBook.write(out);
	          out.close();
	          System.out.println("Results and Data Exported successfully on disk.");
	      } 
	      catch (Exception e) 
	      {
	          e.printStackTrace();
	      }
		
	}
	
	
	
	public void ImportDatatoDB(String filepath,Connection conn,String tableName,String SheetName,String Operation) throws IOException, SQLException, ClassNotFoundException, POIException
	{
		ExcelOperationsPOI xl=new ExcelOperationsPOI(filepath);
		String sql=null;
		DatabaseOperation db=new DatabaseOperation();
		
		xl.getsheets(SheetName);
		int n=xl.getTotColumns();
		int noOfRows=xl.getTotRows();
		int s=xl.getfirstRowNo();
		
		String[] Columns=new String[n];
		String insertString="";
		String values="";
		
		for(int i=s;i<n;i++)
		{
			if(Operation.equalsIgnoreCase("CREATE"))
			{
			String str1=xl.readData(1,i).toString();
			String str2=xl.readData(0,i).toString();
			Columns[i]=str1+" "+str2;
			
			insertString=insertString+xl.read_data(1,i)+",";
			}
			else
			{
				insertString=insertString+xl.read_data(0,i)+",";
			}
			values=values+"?,";
		}
		
		String ColumnString=String.join(",", Columns);
		String insertStrings=insertString.substring(0,(insertString.length()-1));
		String ValueStrings=null;
		int dataRow;
		if(Operation.equalsIgnoreCase("CREATE"))
		{
			dataRow=2;
			sql = "CREATE TABLE "+ tableName +"("+ColumnString+")";
			db.createTable(sql);
		}
		else if(Operation.equalsIgnoreCase("ALTER"))
		{
			dataRow=2;
			sql= "ALTER TABLE "+ tableName +" ADD ("+ColumnString+")";
			db.createTable(sql);
		}
		else
		{
			dataRow=1;
		}
		ValueStrings=values.substring(0,(values.length()-1));

		for(int row=dataRow;row<=noOfRows;row++)
		{
			String sql1 = "INSERT INTO "+ tableName+"("+insertStrings+")"+" VALUES("+ValueStrings+")";
			
			PreparedStatement insertStatement =(PreparedStatement) conn.prepareStatement(sql1);
			for(int col=0;col<n;col++)
			{
				System.out.println(xl.read_data(row, col).trim());
				insertStatement.setString(col+1,xl.read_data(row, col).trim()); 
				
			}
			insertStatement.executeUpdate();
		}
		
	}
	
	public  void truncateTable(String tablename) throws SQLException
	{
		stmt = conn.createStatement();
		String query="TRUNCATE "+tablename;
		stmt.executeUpdate(query);
	}
	
	public void insetRowWithSNO(String OutputTableName,String inputTableName) throws SQLException
	{
		stmt = conn.createStatement();
		String query1 ="INSERT INTO "+OutputTableName+" (`S_No`,`Testdata`,`Flag_for_execution`) SELECT `S_No`,`Testdata`,`Flag_for_execution` FROM "+inputTableName;
		stmt.executeUpdate(query1);
	}
	
	public String buildJoinQuery(List<String> TableNames)
	{
		SelectQuery query=null;
		DbSpec spec = new DbSpec();
	    DbSchema schema = spec.addDefaultSchema();

	    query = new SelectQuery().addAllColumns();
	    DbJoin[] joins =new DbJoin[TableNames.size()-1];
	    if(TableNames.size()==1)
	    {
	    	query.addFromTable(schema.addTable(TableNames.get(0)));
	    }
	    else
	    {
		    for (int i = 0; i < TableNames.size(); i++) 
			{
		    	schema.addTable(TableNames.get(i)).addColumn("S_No", "number", null);
		    	if(!(i==0))
		    	{
		    		joins[i-1] = spec.addJoin(null, TableNames.get(0), null, TableNames.get(i), "S_No");
		    		query.addJoins(SelectQuery.JoinType.LEFT_OUTER, joins[i-1]);
		    	}
			}
	    }
	    String query1= query.validate().toString();
	    return query1;
	}	
}
