package org.processmining.plugins.predictive_monitor.bpm.utility;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class LogReaderAndReplayer {
	XLog log;
	Map<String, Class<?>> dataTypes;
	Set<String> activityLabels;
	List<ReplayerListener> listeners;
	private static Print print = new Print();

	public LogReaderAndReplayer(String inputLogFileName) throws Exception {
		this.log = XLogReader.openLog(inputLogFileName);
		init();
	}

	public LogReaderAndReplayer(XLog log) throws Exception {
		this.log = log;
		init();
	}
	
	private void init() throws Exception {
		dataTypes = new HashMap<String, Class<?>>();
		activityLabels = new HashSet<String>();
		listeners = new LinkedList<ReplayerListener>();
		analyzeLog();
	}
	
	public void addReplayerListener(ReplayerListener listener) {
		listeners.add(listener);
	}
	
	public void removeAllReplayerListeners() {
		listeners.clear();
	}
	
	public Map<String, Class<?>> getDataTypes() {
		return dataTypes;
	}

	public Set<String> getActivityLabels() {
		return activityLabels;
	}



	private void analyzeLog() {
		for(XTrace trace: log) {
			analyzeDataAttributes(trace.getAttributes());
			for (XEvent event : trace) {
				String activityName = event.getAttributes().get(XConceptExtension.KEY_NAME).toString();
				String eventType = event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION).toString();
			//	if (eventType.equals("complete")) {
					analyzeDataAttributes(event.getAttributes());
					activityLabels.add(activityName+"_"+eventType);
			//	}
			}
		}
	}

	protected void analyzeDataAttributes(XAttributeMap xAttributeMap) {
		for (XAttribute attr: xAttributeMap.values()) {
			if (!attr.getKey().contains(":")) {
				String varName = attr.getKey().replaceAll("\\s", "_");
				String value = attr.toString();
				if(value.contains("-")){
					value.replaceAll("-", "_");
				}

				// Let's assume the attribute is a string
				Class<?> clazz = String.class;
				boolean done = true;

				// Is it a (long) integer ?
				try {
					Long.parseLong(value);
					clazz = Long.class;
				} catch (NumberFormatException e) {
					done = false;
				}

				// Is it a floating point ?
				try {
					if (!done) {
						Float.parseFloat(value);
						clazz = Float.class;
					}
				} catch (NumberFormatException e) {
					done = false;
				}

				// Is it a data/time attribute ?
				try {
					if (!done) {
						DatatypeConverter.parseDateTime(value);
						clazz = Calendar.class;
					}
				} catch (IllegalArgumentException e) {
					done = false;
				}

				if (dataTypes.containsKey(varName)) {
					Class<?> oldClass = dataTypes.get(varName);
					if (!oldClass.equals(clazz)) {
						if (oldClass.equals(String.class) || clazz.equals(String.class))
							dataTypes.put(varName, String.class);
						else if (oldClass.equals(Float.class) || clazz.equals(Float.class))
							dataTypes.put(varName, Float.class);
					}
				} else
					dataTypes.put(varName, clazz);
			}
		}
	}
	
	public void replayLog(Set<String> candidateActivations) {
		for(XTrace trace: log){
			String traceId = trace.getAttributes().get("concept:name").toString();		
			print.thatln("TRACE ID: "+traceId);
			print.thatln(candidateActivations);
			for (ReplayerListener listener: listeners)
				listener.openTrace(trace.getAttributes(), traceId,candidateActivations);
			
			int index = 0;
			for (XEvent event : trace) {
			//	String eventType = event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION).toString();
			//	if (eventType.equals("complete")) {
					for (ReplayerListener listener: listeners)
						listener.processEvent(event.getAttributes(), index);
					index++;
			//	}
			}
			for (ReplayerListener listener: listeners)
			listener.closeTrace(trace.getAttributes(), traceId);
		//	System.gc();
		}		
	}
}
