package org.processmining.plugins.predictive_monitor.bpm.replayer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.operationalsupport.client.InvocationException;
import org.processmining.operationalsupport.client.SessionClosedException;
import org.processmining.operationalsupport.client.SessionHandle;
import org.processmining.operationalsupport.messages.reply.ResponseSet;
import org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification.SatisfactionTimeTraceClassifier;
import org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification.SatisfactionTraceClassifier;
import org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification.TraceClassifier;
import org.processmining.plugins.predictive_monitor.bpm.configuration.ClientConfigurationClass;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.Connection;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.ConfigurationOperation;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.EvaluateTraceOperation;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.SatisfactionClassifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier.PredictionType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.SimpleFormula;

public class ConfigurationSenderThread extends Thread{
	ConcurrentLinkedQueue<SingleRun> confQueue;
	private final Semaphore locked;
	private ReplayerScheduler replayerScheduler;
	private SessionHandle handle;
	private final XLog emptyLog = XFactoryRegistry.instance().currentDefault().createLog();

	
	public ConfigurationSenderThread(SessionHandle handle)
	{
		this.handle=handle;
		confQueue = new ConcurrentLinkedQueue<>();
		locked = new Semaphore(0);
		replayerScheduler= new ReplayerScheduler(handle);
	}
	
	public void add(SingleRun singleRun)
	{
		confQueue.add(singleRun);
		locked.release();
	}
	
	
	@Override
	public void run()
	{
		try{
			while(true)
			{
				locked.acquire();

				while(confQueue.size()!=0)
				{
					SingleRun singleRun = confQueue.poll();
					Map<String,Object> configuration = singleRun.getConfiguration();
					String runId=(String) configuration.remove("runId");
					Long initTime = null;
					ResponseSet<Object> result = Connection.send(handle, new ConfigurationOperation(configuration, runId));
					for (String provider : result) {
						for (Object r : result.getResponses(provider)) {
							initTime = (Long)r;
						}
					}
					singleRun.getResultListener().setInitTime(initTime);
					XLog log=null;
					String inputFile = (String)configuration.get("testingInputLogFile");
					if(inputFile.toLowerCase().contains("xes")){
						XesXmlParser parser = new XesXmlParser();
						if(parser.canParse(new File(inputFile))){
							try {
								log = parser.parse(new File(inputFile)).get(0);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
					Map<String,PredictionType> predictionType = (Map<String,PredictionType>) configuration.get("predictionType");
					String formula = (String) configuration.get("formulas");
					Vector<Formula> formulas = new Vector<>();
					SimpleFormula simpleFormula = new SimpleFormula(formula);
					formulas.add(simpleFormula);				
					boolean evaluationRun = (boolean)configuration.get("evaluationRun");
					Map<String,TraceClassifier> traceClassifiers = new HashMap<String, TraceClassifier>();
					
					if(!evaluationRun)
					{
						for(String s: predictionType.keySet())
						{
							TraceClassifier traceClassifier = null;
							switch(predictionType.get(s))
							{
							case FORMULA_SATISFACTION:
								traceClassifier = new SatisfactionTraceClassifier(log, formulas);
								break;
							case FORMULA_SATISFACTION_TIME:
								traceClassifier = new SatisfactionTimeTraceClassifier(log, formulas);
								break;
							}
							traceClassifiers.put(s, traceClassifier);
						}
					}
					
					singleRun.getResultListener().setTotalTraceNumber(log.size());
					
					for(XTrace trace:log)
					{
						if(evaluationRun){
							replayerScheduler.add(runId, trace, singleRun.getResultListener());
						}
						else
						{
							replayerScheduler.add(runId, trace, traceClassifiers,  singleRun.getResultListener());
						}
					}
					sleep(1000);
				}
		
			}
		}catch (InterruptedException e){}
		catch (IOException | InvocationException
			| SessionClosedException e1) {
			e1.printStackTrace();
		}
	}
}
