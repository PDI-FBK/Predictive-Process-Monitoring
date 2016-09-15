package org.processmining.plugins.predictive_monitor.bpm.operational_support.operation;

public class Operation {
	private String type;
	
	protected Operation(String operation)
	{
		this.type=operation;
	}
	public String getType()
	{
		return type;
	}
}
