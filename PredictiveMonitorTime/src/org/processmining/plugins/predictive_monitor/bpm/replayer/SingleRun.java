package org.processmining.plugins.predictive_monitor.bpm.replayer;

import java.util.Map;



public class SingleRun {
	private Map<String,Object> configuration;
	private ResultListener listener;
	
	public SingleRun(Map<String,Object> configuration, ResultListener listener)
	{
		this.configuration = configuration;
		this.listener = listener;
	}
	
	public Map<String,Object> getConfiguration()
	{
		return configuration;
	}
	
	public ResultListener getResultListener()
	{
		return listener;
	}
}
