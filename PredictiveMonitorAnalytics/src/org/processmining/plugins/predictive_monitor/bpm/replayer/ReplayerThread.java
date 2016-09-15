package org.processmining.plugins.predictive_monitor.bpm.replayer;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.operationalsupport.client.SessionHandle;
import org.processmining.operationalsupport.messages.query.CreateSession;
import org.processmining.operationalsupport.messages.reply.ResponseSet;
import org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification.TraceClassifier;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.Connection;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.DeclareMonitorQuery;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.TraceEvaluationResult;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.TraceResult;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.EvaluateTraceOperation;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.PredictionResult;

import weka_predictions.data_predictions.Result;
import weka_predictions.data_predictions.ResultDecisionTree;

public class ReplayerThread extends Thread {
	private ConcurrentLinkedQueue<SingleTraceRun> traceRuns;
	private final Semaphore locked;
	float work;
	private SessionHandle<?, Object, EvaluateTraceOperation, ?, ?> handle;
	private ReplayerScheduler replayerScheduler;
	private Semaphore classification;

	
	
	public ReplayerThread(SessionHandle<?, Object, EvaluateTraceOperation, ?, ?> handle, ReplayerScheduler replayerScheduler)
	{
		this.handle=handle;
		this.replayerScheduler=replayerScheduler;
		locked = new Semaphore(0);
		classification = new Semaphore(0);
		traceRuns = new ConcurrentLinkedQueue<SingleTraceRun>();
		work=0;
		
	}
	
	public void add(SingleTraceRun singleTraceRun)
	{
		traceRuns.add(singleTraceRun);
		updateWork(singleTraceRun.getTraceRun().getWork());
		locked.release();
	}
	
	@Override
	public void run()
	{
		ClassificationThread classificationThread;
		try {
			while(true)
			{				
				locked.acquire();
				
				while(traceRuns.size()!=0)
				{
					SingleTraceRun singleTraceRun = traceRuns.poll();
					TraceRun traceRun = singleTraceRun.getTraceRun();
					ResultListener resultListener = singleTraceRun.getResultListener();
					TraceResult traceResult;
					if(traceRun instanceof TraceEvaluationRun)
					{
						classificationThread = new ClassificationThread((TraceEvaluationRun)traceRun,classification);
						classificationThread.start();
						traceResult = new TraceEvaluationResult((TraceEvaluationRun)traceRun,sendTrace(traceRun));
					}
					else
					{
						traceResult = new TraceResult(traceRun,sendTrace(traceRun));
					}
					
					if(traceRun instanceof TraceEvaluationRun)
					{
						classification.acquire();
					}
					
					resultListener.addResult(traceResult);
					updateWork(-traceRun.getWork());
				}
				
				SingleTraceRun newTrace=replayerScheduler.getNewTrace();
				if(newTrace!=null)
				{
					traceRuns.add(newTrace);
					locked.release();
				}
			}
		} catch (InterruptedException e) {}
	}
	
	private synchronized void updateWork(float work)
	{
		this.work+=work;
	}
	
	public synchronized float getWork()
	{
		return work;
	}
	
	private PredictionResult sendTrace(TraceRun traceRun)
	{
		PredictionResult predictionResult = null;
		try {
			
			ResponseSet<Object> result = Connection.send(handle, new EvaluateTraceOperation(traceRun));
			
			for (String provider : result) {
				for (Object r : result.getResponses(provider)) {
					predictionResult = (PredictionResult)r;
				}
			}
		} catch (Exception e) {
			//handle=Connection.createSession();
			System.err.println(e.getMessage());
			/*try {
				Thread.sleep((long)(100*Math.random()));
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			return sendTrace(traceRun);*/
		}
		
		return predictionResult;
	}
}
