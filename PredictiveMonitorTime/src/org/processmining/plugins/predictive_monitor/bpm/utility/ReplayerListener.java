package org.processmining.plugins.predictive_monitor.bpm.utility;

import java.util.Set;

import org.deckfour.xes.model.XAttributeMap;

public interface ReplayerListener {
	void openTrace(XAttributeMap attribs, String traceId,Set<String> candidateActivations);
	void closeTrace(XAttributeMap attribs, String traceId);
	void processEvent(XAttributeMap attribs, int index);
}
