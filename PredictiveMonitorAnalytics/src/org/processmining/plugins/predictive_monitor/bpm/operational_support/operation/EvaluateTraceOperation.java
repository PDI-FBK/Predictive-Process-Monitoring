package org.processmining.plugins.predictive_monitor.bpm.operational_support.operation;

import org.processmining.plugins.predictive_monitor.bpm.replayer.TraceRun;

public class EvaluateTraceOperation extends Operation{
	private TraceRun traceRun;
	
	public EvaluateTraceOperation(TraceRun traceRun) {
		super("evaluateTrace");
		this.traceRun=traceRun;
	}
	
	public TraceRun getTraceRun()
	{
		return traceRun;
	}
}
