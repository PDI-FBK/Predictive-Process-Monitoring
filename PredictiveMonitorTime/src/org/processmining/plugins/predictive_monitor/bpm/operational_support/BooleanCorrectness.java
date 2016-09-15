package org.processmining.plugins.predictive_monitor.bpm.operational_support;

import java.util.Map;

import org.processmining.plugins.predictive_monitor.bpm.prediction.result.DiscreteResult;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.LongRange;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.LongResult;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.NoResult;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.PredictionResult;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.ResultType;

public class BooleanCorrectness extends Correctness{
	private final String correct = "Correct";
	private final String wrong = "Wrong";
	private int value;

	public BooleanCorrectness(PredictionResult prediction, Map<String,Object> expectation) {
		value = 1;
		for(String s: prediction.getResults().keySet())
		{
			if(prediction.getResults().get(s) instanceof LongRange)
			{
				if(!((LongRange)prediction.getResults().get(s)).contains((Long)expectation.get(s)))
				{
					value = 0;
				}
			}
			else if(prediction.getResults().get(s) instanceof DiscreteResult)
			{
				if(!((DiscreteResult)prediction.getResults().get(s)).getLabel().equals((String)expectation.get(s)))
				{
					value = 0;
				}
			}
		}
	}
	
	@Override 
	public String getLabel()
	{
		return value==1?correct:wrong;
	}
	
	@Override
	public double getValue()
	{
		return value;
	}
}
