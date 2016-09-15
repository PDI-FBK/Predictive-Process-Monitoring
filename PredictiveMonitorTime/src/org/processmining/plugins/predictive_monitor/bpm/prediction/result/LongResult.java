package org.processmining.plugins.predictive_monitor.bpm.prediction.result;

public class LongResult extends ResultType{
	long value;
	
	public LongResult(long value)
	{
		this.value=value;
		label = ""+value;
	}
}
