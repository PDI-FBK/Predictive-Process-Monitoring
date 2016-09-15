package org.processmining.plugins.predictive_monitor.bpm.operational_support.operation;

import java.util.Map;

public class ConfigurationOperation extends Operation{
	Map<String,Object> configuration;
	String runId;
	
	public ConfigurationOperation(Map<String,Object> configuration, String runId) {
		super("newConfiguration");
		this.configuration=configuration;
		this.runId=runId;
	}

	public Map<String,Object> getConfiguration()
	{
		return configuration;
	}
	
	public String getRunId()
	{
		return runId;
	}
}
