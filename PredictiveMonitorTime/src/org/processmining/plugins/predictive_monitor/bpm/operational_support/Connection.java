package org.processmining.plugins.predictive_monitor.bpm.operational_support;

import java.io.IOException;
import java.util.List;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.processmining.operationalsupport.client.InvocationException;
import org.processmining.operationalsupport.client.SessionClosedException;
import org.processmining.operationalsupport.client.SessionHandle;
import org.processmining.operationalsupport.messages.reply.ResponseSet;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.EvaluateTraceOperation;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.GetLocalTrainingFiles;



public class Connection {
	private static final XLog emptyLog = XFactoryRegistry.instance().currentDefault().createLog();

	public static String serverIp="localhost";
	public static int port = 1202;
	
	public static void setPort(int port)
	{
		Connection.port = port;
	}
	
	public static void setServerIp(String serverIp)
	{
		Connection.serverIp = serverIp;
	}
	
	public static List<String> connect() throws IOException, InvocationException, SessionClosedException
	{
		ResponseSet<Object> result = send(createSession(),new GetLocalTrainingFiles());
		for (String provider : result) {
			for (Object r : result.getResponses(provider)) {
				return (List<String>)r;
			}
		}
		return null;
	}
	
	public static synchronized SessionHandle createSession()
	{
		try {
			SessionHandle handle =  SessionHandle.create(serverIp,port);
			return handle;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static synchronized ResponseSet<Object> send(SessionHandle handle,Object o) throws IOException, InvocationException, SessionClosedException
	{
		return handle.simple(o,emptyLog);
	}
}
