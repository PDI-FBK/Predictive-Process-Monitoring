package org.processmining.plugins.predictive_monitor.bpm.replayer;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification.TraceClassifier;

public class TraceRun {
	protected String runId;
	protected XTrace trace;
	
	public TraceRun(String runId, XTrace trace)
	{
		this.runId=runId;
		this.trace=trace;
	}
	
	public XTrace getTrace()
	{
		return trace;
	}
	
	public String getRunId()
	{
		return runId;
	}
	
	public float getWork()
	{
		return trace.size()*0.5f;
	}

	public String getName() {
		return XConceptExtension.instance().extractName(trace);
	}
}
