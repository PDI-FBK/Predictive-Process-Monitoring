package org.processmining.plugins.predictive_monitor.bpm.operational_support;

public class NotPredicted extends Correctness{

	private String label;
	public NotPredicted()
	{
		label = "Not predicted";
	}
	
	public NotPredicted(String label)
	{
		this.label = "Not predicted ("+label+")";
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	@Override 
	public double getValue()
	{
		return 0;
	}
	
}
