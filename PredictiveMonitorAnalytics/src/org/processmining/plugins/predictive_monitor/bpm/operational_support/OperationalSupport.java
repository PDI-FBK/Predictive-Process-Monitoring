package org.processmining.plugins.predictive_monitor.bpm.operational_support;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.operationalsupport.client.SessionHandle;
import org.processmining.operationalsupport.messages.reply.ResponseSet;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;

import weka_predictions.data_predictions.Result;

public class OperationalSupport {
	
	public static Map<String, Result> provideOperationalSupport(Vector<Formula> formulas, List<String> currentEvents, Vector<String> currentVariables, Map<String, String> variables, String traceID, Map<String,Integer>configuration){
//		Classifier classifier = new Classifier();
//		String filePath = classifier.classifyTraces(formula, currentEvents);
//		Predictor predictor = new Predictor();
//		Map<String, Result> suggestions = predictor.suggestInput(filePath, currentVariables, variables);
		
		
		Map<String, Result> analysis = new HashMap<String, Result>();
		HashMap<String, Object> properties = new HashMap<String,Object>();
		Hashtable<String,XTrace> partialTraces = new Hashtable<String,XTrace>();
		Hashtable handles = new Hashtable();
		SessionHandle handle = null;
		properties.put("formulas", formulas);
		properties.put("currentVariables", currentVariables);
		properties.put("variables", variables);
		properties.put("configuration", configuration);
		//ResponseSet result = null;
		try {
			String piID = traceID;			
			if(!handles.containsKey(piID)){
				handle = SessionHandle.create("localhost",1202, DeclareMonitorQuery.INSTANCE,properties);
			//	handle.setModel(DeclareLanguage.INSTANCE, referenceXML);
				handles.put(piID, handle);
			}else{			
				handle = (SessionHandle)handles.get(piID);
			}

			XTrace partialTrace;
		//	String eventName = "nome attivita";
			if (partialTraces.containsKey(piID)) {
				partialTrace = (XTrace) partialTraces.get(piID);
			} else {
				partialTrace = new XTraceImpl(new XAttributeMapImpl());
			}
			boolean done = false;
			if(currentEvents.get(currentEvents.size()-1).equals("complete")){
				done = true;
			}
			for(String eventName : currentEvents){
				XEvent completeEvent = new XEventImpl();
				XConceptExtension.instance().assignName(completeEvent, eventName/*.toLowerCase()*/);
				handle.addEvent(completeEvent);
				partialTrace.add(completeEvent);
			}
			XConceptExtension.instance().assignName(partialTrace,piID);
			partialTraces.put(piID, partialTrace);
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
