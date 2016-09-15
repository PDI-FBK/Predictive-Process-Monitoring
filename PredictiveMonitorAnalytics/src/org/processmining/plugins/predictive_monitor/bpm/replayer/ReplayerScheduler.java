package org.processmining.plugins.predictive_monitor.bpm.replayer;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XTrace;
import org.processmining.operationalsupport.client.SessionHandle;
import org.processmining.operationalsupport.messages.query.CreateSession;
import org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification.TraceClassifier;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.Connection;

import weka_predictions.data_predictions.Result;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReplayerScheduler {
	
	private final int threadNumber = 1; //Runtime.getRuntime().availableProcessors();
	private List<ReplayerThread> replayerThreads;
	private ConcurrentLinkedQueue<SingleTraceRun> traceRuns;

	public ReplayerScheduler(SessionHandle handle)
	{
		replayerThreads = new ArrayList<>();
		for(int i=0;i<threadNumber;i++)
		{
			ReplayerThread replayerThread=null;
			
			replayerThread = new ReplayerThread(Connection.createSession(), this);
			
			replayerThread.start();
			replayerThreads.add(replayerThread);
			traceRuns = new ConcurrentLinkedQueue<>();
			
		}
	}
	
	public void add(String runId,XTrace trace, Map<String,TraceClassifier> traceClassifiers, ResultListener resultListener)
	{
		TraceRun traceRun = new TraceEvaluationRun(runId, trace, traceClassifiers);
		addTraceRun(new SingleTraceRun(traceRun, resultListener));
	}
	
	public void add(String runId, XTrace trace, ResultListener resultListener)
	{
		TraceRun traceRun = new TraceRun(runId, trace);
		addTraceRun(new SingleTraceRun(traceRun, resultListener));
	}
	
	private void addTraceRun(SingleTraceRun singleTraceRun)
	{
		for(int i=0;i<replayerThreads.size();i++)
		{
			if(replayerThreads.get(i).getWork()==0)
			{
				replayerThreads.get(i).add(singleTraceRun);
				System.out.println("Aggiungo una traccia al replayer "+i);
				return;
			}
		}
		traceRuns.add(singleTraceRun);
	}
	
	public synchronized SingleTraceRun getNewTrace()
	{
		return traceRuns.poll();
	}

}
