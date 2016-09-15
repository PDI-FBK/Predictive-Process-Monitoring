package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints;

import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.utility.DataSnapshotListener;

public interface DeclareReplayer {
	
	public void process(int eventIndex, String event, XTrace trace, String traceId,DataSnapshotListener listener);

}
