package org.processmining.plugins.predictive_monitor.bpm.operational_support;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.LongRange;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.PredictionResult;
import org.processmining.plugins.predictive_monitor.bpm.replayer.TraceRun;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class TraceResult {
	protected TraceRun trace;
	protected PredictionResult result;
	protected final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");


	public TraceResult(TraceRun trace,PredictionResult result)
	{
		this.trace=trace;
		this.result=result;
	}
	
	public TraceRun getTraceRun()
	{
		return trace;
	}
	
	public PredictionResult getResult()
	{
		return result;
	}

	public void jumpToCurrentEvent() {
		for(String key:result.getResults().keySet())
		{
			if(result.getResults().get(key) instanceof LongRange)
			{
				int currPrefIndex = result.getIndex();
				if(trace.getTrace().size()>0)
				{
					try {
						long currentTime = ((Date)format.parseObject(trace.getTrace().get(currPrefIndex).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString())).getTime();
						currentTime -= ((Date)format.parseObject(trace.getTrace().get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString())).getTime();
						((LongRange)result.getResults().get(key)).translate(currentTime);
					} catch (ParseException e) {}
				}
			}
		}
	}
}
