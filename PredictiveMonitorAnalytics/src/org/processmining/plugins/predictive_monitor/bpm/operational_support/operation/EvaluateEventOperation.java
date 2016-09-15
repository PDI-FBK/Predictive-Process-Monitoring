package org.processmining.plugins.predictive_monitor.bpm.operational_support.operation;

import org.deckfour.xes.model.XEvent;


public class EvaluateEventOperation extends Operation{
	private XEvent event;
	private String runId;
	private String traceId;
	
	public EvaluateEventOperation(XEvent event, String traceId, String runId) {
		super("evaluateEvent");
		this.event = event;
		this.traceId = traceId;
		this.runId = runId;
	}
	
	public XEvent getEvent()
	{
		return event;
	}
	
	public String getRunId()
	{
		return runId;
	}
	
	public String getTraceId()
	{
		return traceId;
	}

}
