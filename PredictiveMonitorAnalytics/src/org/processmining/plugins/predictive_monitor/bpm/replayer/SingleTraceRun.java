package org.processmining.plugins.predictive_monitor.bpm.replayer;


public class SingleTraceRun {
	private ResultListener resultListener;
	private TraceRun traceRun;
	
	public SingleTraceRun(TraceRun traceRun, ResultListener resultListener)
	{
		this.traceRun=traceRun;
		this.resultListener=resultListener;
	}
	
	public ResultListener getResultListener()
	{
		return resultListener;
	}
	
	public TraceRun getTraceRun()
	{
		return traceRun;
	}
}
