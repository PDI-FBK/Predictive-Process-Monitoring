package org.processmining.plugins.predictive_monitor.bpm.utility;

import java.util.Iterator;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class TracePrefixGenerator {
	
	public static XLog generatePrefixesFromLog(XLog log, int minPrefixLength, int maxPrefixLength, int gap){
		XLog prefixTraceLog  = XFactoryRegistry.instance().currentDefault().createLog();
		prefixTraceLog.setAttributes(log.getAttributes());
		for (XTrace trace : log) {
			for (int pL = minPrefixLength; pL <= maxPrefixLength && pL <trace.size(); pL = pL+gap) {
				prefixTraceLog.add(getPrefixTrace(trace, pL));
			}
			if (trace.size()<=maxPrefixLength)
			prefixTraceLog.add(trace);

		}
		return prefixTraceLog;
	}
	
	public static XLog computePrefixTraceLog(XLog log, int prefixLength){
		XLog prefixTraceLog  = XFactoryRegistry.instance().currentDefault().createLog();
		prefixTraceLog.setAttributes(log.getAttributes());
		for (XTrace trace : log) {
			prefixTraceLog.add(getPrefixTrace(trace, prefixLength));
		}
		return prefixTraceLog;
	}

	private static XTrace getPrefixTrace(XTrace trace, int prefixLength ){
		XTrace prefixTrace = XFactoryRegistry.instance().currentDefault().createTrace(trace.getAttributes());
		int i=0;
		for (Iterator iterator = trace.iterator(); iterator.hasNext() && i<prefixLength;) {
			XEvent event = (XEvent) iterator.next();
			prefixTrace.add(event);
			i++;
		}

		return prefixTrace;
	}

}
