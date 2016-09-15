package org.processmining.plugins.predictive_monitor.bpm.operational_support;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.DiscreteResult;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.LongRange;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.NoResult;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.PredictionResult;
import org.processmining.plugins.predictive_monitor.bpm.replayer.TraceEvaluationRun;

public class TraceEvaluationResult extends TraceResult{
	
	private Correctness correctness;
	private Map<String,Object> classifications;
	
	public TraceEvaluationResult(TraceEvaluationRun trace, PredictionResult result)
	{
		super(trace,result);
		classifications = trace.getClassifications();
		if(result.isValid())
		{
			correctness = new BooleanCorrectness(result,trace.getClassifications());
		}
		else if( result.getResults().size()==0 || !result.isValid())
		{
			correctness = new NotPredicted();
		}
	}
	
	public Correctness getCorrectness()
	{
		return correctness;
	}
	
	@Override
	public TraceEvaluationRun getTraceRun()
	{
		return (TraceEvaluationRun)trace;
	}
	
	@Override
	public void jumpToCurrentEvent()
	{
		super.jumpToCurrentEvent();
		for(String key: classifications.keySet())
		{
			if(classifications.get(key) instanceof Long)
			{
				try {
					Long currentValue = (Long)classifications.get(key);
					classifications.remove(key);
					int currPrefIndex = result.getIndex();
					long currentTime = ((Date)format.parseObject(trace.getTrace().get(currPrefIndex).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString())).getTime();
					long startTime = ((Date)format.parseObject(trace.getTrace().get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString())).getTime();
					classifications.put(key,new Long(currentValue-currentTime+startTime));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
