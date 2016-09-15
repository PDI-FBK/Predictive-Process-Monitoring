package org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.PredictionResultConverter;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.FormulaVerificator;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;
import org.processmining.plugins.predictive_monitor.bpm.utility.DataSnapshotListener;
import org.processmining.plugins.predictive_monitor.bpm.utility.LogReaderAndReplayer;

public class SatisfactionTraceClassifier extends TraceClassifier {
	
	public SatisfactionTraceClassifier(XLog log, Vector<Formula> formulas) {
		super(log,formulas);
	}

	public String classifyTrace(XTrace trace) {
		return classify(FormulaVerificator.isFormulaVerified(listener, trace, formulas));
	}
	
	private String classify(boolean satisfied)
	{
		return satisfied ? "yes" : "no" ;
	}
}
