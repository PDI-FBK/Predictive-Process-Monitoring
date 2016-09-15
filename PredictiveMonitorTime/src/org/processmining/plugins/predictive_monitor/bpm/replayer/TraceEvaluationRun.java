package org.processmining.plugins.predictive_monitor.bpm.replayer;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification.TraceClassifier;

public class TraceEvaluationRun extends TraceRun{
	protected Map<String,Object> classifications;
	protected Map<String,TraceClassifier> traceClassifiers;

	public TraceEvaluationRun(String runId, XTrace trace,Map<String,TraceClassifier> traceClassifiers) {
		super(runId, trace);
		this.traceClassifiers=traceClassifiers;
		classifications=new HashMap<String, Object>();
	}

	public void classify()
	{
		for(String s: traceClassifiers.keySet())
		{
			classifications.put(s,traceClassifiers.get(s).classifyTrace(trace));
		}
	}
	
	public Map<String,Object> getClassifications()
	{
		return classifications;
	}
}
