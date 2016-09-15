package org.processmining.plugins.predictive_monitor.bpm.server;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.XEvent;

public class TraceManager {
	Map<String,XTrace> partialTraces;
	
	public TraceManager()
	{
		partialTraces = new HashMap<String, XTrace>();
	}
	
	public XTrace addEvent(XEvent event,String traceId)
	{
		XTrace trace;
		if(partialTraces.containsKey(traceId))
		{
			trace = partialTraces.get(traceId);
			trace.add(event);
		}
		else
		{
			trace = XFactoryRegistry.instance().currentDefault().createTrace();
			XConceptExtension.instance().assignName(trace,traceId);
			trace.add(event);
			partialTraces.put(traceId,trace);
		}
		return trace;
	}
}
