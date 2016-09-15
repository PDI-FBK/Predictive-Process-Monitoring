package org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification;

import java.util.Vector;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;
import org.processmining.plugins.predictive_monitor.bpm.utility.DataSnapshotListener;
import org.processmining.plugins.predictive_monitor.bpm.utility.LogReaderAndReplayer;

public abstract class TraceClassifier {
	protected Vector<Formula> formulas;
	protected DataSnapshotListener listener;
	
	public TraceClassifier(XLog log, Vector<Formula> formulas) {
		this.formulas=formulas;
		LogReaderAndReplayer replayer = null;
		try {
			replayer = new LogReaderAndReplayer(log);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listener = new DataSnapshotListener(replayer.getDataTypes(), replayer.getActivityLabels());
	}
	
	public abstract Object classifyTrace(XTrace trace);

}
