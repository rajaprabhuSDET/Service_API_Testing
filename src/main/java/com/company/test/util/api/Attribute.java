package com.company.test.util.api;

public class Attribute 
{
	private String Atrib;
	private Object value;
	public Attribute(String Attrib, Object value) 
	{
		this.Atrib = Attrib;
		this.value = value;
	}
	public String getAtrib() 
	{
		return Atrib;
	}
	public void setAtrib(String atrib) 
	{
		Atrib = atrib;
	}
	public Object getValue() 
	{
		return value;
	}
	public void setValue(String value) 
	{
		this.value = value;
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
}