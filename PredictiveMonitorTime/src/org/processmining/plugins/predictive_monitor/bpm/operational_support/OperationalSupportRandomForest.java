package org.processmining.plugins.predictive_monitor.bpm.operational_support;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.operationalsupport.client.SessionHandle;
import org.processmining.operationalsupport.messages.reply.ResponseSet;
import org.processmining.plugins.predictive_monitor.bpm.configuration.ClientConfigurationClass;
import org.processmining.plugins.predictive_monitor.bpm.configuration.TraceEvaluation;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;

import weka_predictions.data_predictions.Result;

public class OperationalSupportRandomForest {
	
	public static SessionHandle handle=null;
	
	public static void createHandle(){

		//	handle.setModel(DeclareLanguage.INSTANCE, referenceXML);
		}
	
	public static Map<String, Result> provideOperationalSupport(XTrace trace, Map<String,Object>configuration){
	
//		Classifier classifier = new Classifier();
//		String filePath = classifier.classifyTraces(formula, currentEvents);
//		Predictor predictor = new Predictor();
//		Map<String, Result> suggestions = predictor.suggestInput(filePath, currentVariables, variables);
		
		
		Map<String, Result> analysis = new HashMap<String, Result>();

		Hashtable<String,XTrace> partialTraces = new Hashtable<String,XTrace>();

		try {
			//String piID = traceEvaluation.getName();
			String piID = XConceptExtension.instance().extractName(trace);
			Hashtable handles = new Hashtable();
			HashMap<String, Object> properties = new HashMap<String,Object>();
			properties.put("runId",configuration.get("runId"));
			properties.put("configuration",configuration);
			properties.put("trace",trace);
			//properties.put("trace",traceEvaluation);

			//ResponseSet result = null;
			try {
				handle = SessionHandle.create("localhost",1202, DeclareMonitorQuery.INSTANCE,properties);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			boolean done = false;
			/*XTrace partialTrace;
		//	String eventName = "nome attivita";
			if (partialTraces.containsKey(piID)) {
				partialTrace = (XTrace) partialTraces.get(piID);
			} else {
				//partialTrace = new XTraceImpl(new XAttributeMapImpl());
				partialTrace = currentTrace;
			}
			
			if(partialTrace.size()>0)
			{
				if(partialTrace.get(partialTrace.size()-1).equals("complete")){
					done = true;
				}
			}

			*/

			//for(XEvent event : trace){
			//	handle.addEvent(event);
			//}
			
			/*
			XConceptExtension.instance().assignName(partialTrace,piID);
			partialTraces.put(piID, partialTrace);*/
			XLog emptyLog = XFactoryRegistry.instance().currentDefault().createLog();
			ResponseSet<Map<String, Object>> result = handle.simple(piID, emptyLog ,done);
			
			for (String provider : result) {
				for (Map<String, Object> r : result.getResponses(provider)) {
					for(String key : r.keySet()){
						analysis.put(key, (Result)r.get(key));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return analysis;
	}

}
