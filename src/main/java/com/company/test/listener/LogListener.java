package com.company.test.listener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.company.test.servicetesting.ServiceTestingProgram.MainClass2;


public class LogListener implements Listener
{
	private static Logger Log;
	static
	{
		String log4jConfigFile = "src/main/java/log4j.properties";
		PropertyConfigurator.configure(log4jConfigFile);
		Log = Logger.getLogger(MainClass2.class);
	}
	
	@Override
	public void beforeLoadSampleRequest()
	{
		//System.out.println("beforeLoadSampleRequest");
		Log.info("beforeLoadSampleRequest");
	}

	@Override
	public void afterLoadSampleRequest() 
	{
		//System.out.println("afterLoadSampleRequest");
		Log.info("afterLoadSampleRequest");
	}

	@Override
	public void beforePumpDataToRequest()
	{
		//System.out.println("beforePumpDataToRequest");
		Log.info("beforePumpDataToRequest");
	}

	@Override
	public void afterPumpDataToRequest() 
	{
		//System.out.println("afterPumpDataToRequest");
		Log.info("afterPumpDataToRequest");
	}
	@Override
	public void beforeRequestToString()
	{
		//System.out.println("beforeRequestToString");
		Log.info("beforeRequestToString");
	}

	@Override
	public void afterRequestToString() 
	{
		//System.out.println("afterRequestToString");
		Log.info("afterRequestToString");
	}

	@Override
	public void beforeAddHeaders(String Token)
	{
		//System.out.println("beforeAddHeaders");
		Log.info("beforeAddHeaders");
	}

	@Override
	public void afterAddHeaders(String Token) 
	{
		//System.out.println("afterAddHeaders");
		Log.info("afterAddHeaders");
	}

	@Override
	public void beforeSendAndReceiveData()
	{
		//System.out.println("beforeSendAndReceiveData");
		Log.info("beforeSendAndReceiveData");
	}

	@Override
	public void afterSendAndReceiveData() 
	{
		//System.out.println("afterSendAndReceiveData");
		Log.info("afterSendAndReceiveData");
	}
	
	@Override
	public void beforeResponseToString()
	{
		//System.out.println("beforeResponseToString");
		Log.info("beforeResponseToString");
	}

	@Override
	public void afterResponseToString() 
	{
		//System.out.println("afterResponseToString");
		Log.info("afterResponseToString");
	}

	@Override
	public void beforeSendResponseDataToFile()
	{
		//Log.info("beforeSendResponseDataToFile");
	}

	@Override
	public void afterSendResponseDataToFile() 
	{
		//System.out.println("afterSendResponseDataToFile");
		Log.info("afterSendResponseDataToFile");
	}

	@Override
	public void beforeCompareFunction() 
	{
		//System.out.println("beforeCompareFunction");
		Log.info("beforeCompareFunction");
	}

	@Override
	public void afterCompareFunction() 
	{
		//System.out.println("afterCompareFunction");
		Log.info("afterCompareFunction");
	}

	@Override
	public void onError(Throwable e)
	{
		//System.out.println("Printed by Listener");
		System.out.println(e.getMessage()+"--"+e.getCause().getMessage()+"--"+e.getCause().getCause().getMessage());
		Log.error(e.getMessage()+"--"+e.getCause().getMessage()+"--"+e.getCause().getCause().getMessage());
		Log.error(e);
		e.printStackTrace();
	}

	@Override
	public void beforeTokenGenerator() {
		// TODO Auto-generated method stub
		Log.info("beforeTokenGenerator");
	}

	@Override
	public void afterTokenGeneratior() {
		// TODO Auto-generated method stub
		Log.info("afterTokenGeneratior");
	}

	@Override
	public void beforedifferrence() {
		// TODO Auto-generated method stub
		Log.info("beforedifference");
	}

	@Override
	public void afterdifferrence() {
		// TODO Auto-generated method stub
		Log.info("afterdifference");
	}
}
