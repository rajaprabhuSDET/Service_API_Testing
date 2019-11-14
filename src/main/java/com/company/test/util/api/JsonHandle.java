package com.company.test.util.api;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.company.test.exception.RequestFormatException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class JsonHandle implements RequestResponse
{
	private JsonPath path;
	@SuppressWarnings("unused")
	private JSONObject obj = new JSONObject();
	@SuppressWarnings("unused")
	private JSONParser parser = new JSONParser();
	private String file_location;
	private BufferedReader read_file = null;
	private FileWriter write_file = null;
	
	public JsonHandle(String file_location)
	{
		this.file_location = file_location;
		
	}	
	
	public JsonHandle() 
	{

	}

	public void getFilePath(String filepath) 
	{
		this.file_location = filepath;
	}

	private String enable_read() throws RequestFormatException
	{
		StringBuffer si = new StringBuffer();
		try 
		{
			//read_file = new FileReader(file_location);
			read_file = new BufferedReader(new InputStreamReader(new FileInputStream(file_location), "UTF-8"));
			String s = null;			

			while (( s=read_file.readLine())!=null)
		    {
					si=si.append(s);
		          // System.out.println(s);
		    }
		} 
		catch (IOException e) 
		{
			throw new RequestFormatException("ERROR OCCURS WHILE SPECIFIED JSON FILEPATH = " + file_location +" TO READ", e);
		}
		
		read_file = null;
		//System.out.println(obj.toJSONString());
		//System.out.println(si);
		return si.toString();
		
	}
	
	private void enable_write(String json_string) throws RequestFormatException 
	{
		try 
		{
			write_file = new FileWriter(file_location);
		} 
		catch (IOException e) 
		{
			throw new RequestFormatException("ERROR OCCURS WHILE SPECIFYING JSON FILEPATH = " + file_location +" TO WRITE", e);
		}
		
		try 
		{
			write_file.write(json_string);
		} 
		catch (IOException e) 
		{
			throw new RequestFormatException("ERROR OCCURS WHILE WRITING A JSON FILE", e);
		}
		
		try 
		{
			write_file.flush();
		} 
		catch (IOException e) 
		{
			throw new RequestFormatException("ERROR OCCURS WHILE FLUSH A JSON FILE AFTER WRITING A FILE", e);
		}
		
		try 
		{
			write_file.close();
		}
		catch (IOException e) 
		{
			throw new RequestFormatException("ERROR OCCURS WHILE CLOSING A JSON FILE AFTER WRITING A FILE", e);
		}
	}
	
	
	public void StringToFile(String json_string) throws RequestFormatException
	{
		try 
		{
			enable_write(json_string);
		} 
		catch (RequestFormatException e) 
		{
			throw new RequestFormatException("ERROR OCCURS WHILE CONVERTING STRING TO FILE OPERATION", e);
		}
	}
	
	public String read(String json_path) throws RequestFormatException
	{
		path = JsonPath.compile(json_path);
		String value= "";
		try 
		{
			value=JsonPath.parse(enable_read()).read(path).toString();;
		} 
		catch (RequestFormatException e) 
		{
			throw new RequestFormatException("ERROR OCCURS WHILE READING STRING OPERATION", e);
		}
		
		
		return value;
	}
	
	public String FileToString() throws RequestFormatException
	{		
		return enable_read();
	}
	
	public void write(String json_path,String new_value) throws RequestFormatException
	{
		path = JsonPath.compile(json_path);
		DocumentContext newJson;
		try 
		{
			newJson = JsonPath.parse(enable_read()).set(path, new_value);
		} 
		catch (RequestFormatException e) 
		{
			throw new RequestFormatException("ERROR OCCURS WHILE ENABLE WRITING OPERATION", e);
		}
		
		enable_write(newJson.jsonString());
		
	}

	public String readToken(String json_path, String response) throws RequestFormatException, IOException, ParseException
	{
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		JsonPath path;
		obj = (JSONObject) parser.parse(new StringReader(response));
		path = JsonPath.compile(json_path);
		return JsonPath.parse(obj.toJSONString()).read(path).toString();	
	}	
	
}
