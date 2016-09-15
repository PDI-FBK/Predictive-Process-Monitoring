package org.processmining.plugins.predictive_monitor.bpm.prediction.result;

public class RealResult extends ResultType{
	double value;
	
	public RealResult(double value)
	{
		this.value=value;
		label = ""+value;
	}
}
