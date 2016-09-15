package org.processmining.plugins.predictive_monitor.bpm.replayer;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.processmining.operationalsupport.client.InvocationException;
import org.processmining.operationalsupport.client.SessionClosedException;
import org.processmining.operationalsupport.client.SessionHandle;
import org.processmining.operationalsupport.xml.OSXMLConverter;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.Connection;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.DeclareMonitorQuery;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.ConfigurationOperation;

public class ConfigurationSender {
	private boolean running=false;
	ConfigurationSenderThread configurationSenderThread;
	private SessionHandle handle;
	
	public ConfigurationSender()
	{
		handle = Connection.createSession();
		configurationSenderThread = new ConfigurationSenderThread(handle);
		configurationSenderThread.start();
	}
	
	public void addRun(Map<String,Map<String,Object>> configurations, GlobalResultListener listener)
	{
		for(String configurationId :configurations.keySet())
		{
			Map<String,Object> configuration = configurations.get(configurationId);
			ResultListener resultListener = listener.getResultListener(configurationId);
			configurationSenderThread.add(new SingleRun(configuration, resultListener));
		}
	}

}
