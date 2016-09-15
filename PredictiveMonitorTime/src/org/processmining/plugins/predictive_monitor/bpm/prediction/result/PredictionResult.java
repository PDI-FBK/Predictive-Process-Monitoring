package org.processmining.plugins.predictive_monitor.bpm.prediction.result;

import java.util.HashMap;
import java.util.Map;

public class PredictionResult {
	protected double support;
	protected double confidence;
	protected int index;
	boolean valid;
	long time;
	Map<String,ResultType> results;
	
	
	public PredictionResult(double confidence, double support, int index, boolean valid, long time)
	{
		this.confidence=confidence;
		this.support=support;
		this.index=index;
		this.valid=valid;
		this.time=time;
		results = new HashMap<String, ResultType>();
	}
	
	public double getSupport()
	{
		return support;
	}
	
	public double getConfidence()
	{
		return confidence;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public boolean isValid()
	{
		return valid;
	}
	
	public Map<String,ResultType> getResults()
	{
		return results;
	}
	
	public long getTime()
	{
		return time;
	}
	
	public void addResult(String name,ResultType resultType)
	{
		results.put(name,resultType);
	}

}
