package org.processmining.plugins.predictive_monitor.bpm.replayer;

import java.util.concurrent.Semaphore;

public class ClassificationThread extends Thread {
	private TraceEvaluationRun traceRun;
	private Semaphore classification;
	public ClassificationThread(TraceEvaluationRun traceRun,Semaphore classification) {
		this.classification = classification;
		this.traceRun = traceRun;
	}
	
	@Override
	public void run()
	{
		traceRun.classify();
		classification.release();
	}
}
