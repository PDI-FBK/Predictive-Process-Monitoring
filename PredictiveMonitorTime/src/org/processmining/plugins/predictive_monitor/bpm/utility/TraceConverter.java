package org.processmining.plugins.predictive_monitor.bpm.utility;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

public class TraceConverter {
	
	public static String getTraceAsString(XTrace trace){
		String stringTraceDescription = "";
		boolean start = true;
		for (XEvent event : trace) {
			String eventName = XConceptExtension.instance().extractName(event);
			if (!start)
				stringTraceDescription+=";";
			stringTraceDescription+=eventName;
			start = false;
		}
		return stringTraceDescription; 
	}
	
	public static String[] getTraceAsStringArray(XTrace trace){
		String[] stringArrayTraceDescription = new String[trace.size()];
		int i = 0;
		for (XEvent event : trace) {
			String eventName = XConceptExtension.instance().extractName(event);
			stringArrayTraceDescription[i]=eventName;
			i++;
		}
		return stringArrayTraceDescription; 
	}

}
