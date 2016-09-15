package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification.SatisfactionTraceClassifier;
import org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification.TraceClassifier;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.PredictionResultConverter;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.FormulaVerificator;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;
import org.processmining.plugins.predictive_monitor.bpm.utility.DataSnapshotListener;
import org.processmining.plugins.predictive_monitor.bpm.utility.LogReaderAndReplayer;

public class SatisfactionClassifier implements Classifier{

	private HashMap<String,String> classification;
	
	public SatisfactionClassifier(XLog log,Vector<Formula> formulas) {
		SatisfactionTraceClassifier traceClassifier = new SatisfactionTraceClassifier(log, formulas);
		classification = new HashMap<String, String>();
		classifyTraces(log,traceClassifier);
	}

	private void classifyTraces(XLog log, SatisfactionTraceClassifier traceClassifier) {
		for (XTrace trace : log) 
		{
			String label=PredictionResultConverter.toLabel(traceClassifier.classifyTrace(trace));
			classification.put(XConceptExtension.instance().extractName(trace), label);
		}
	}
	
	@Override
	public List<String> getLabels() {
		List<String> possibileResults=new ArrayList<String>();
		possibileResults.add(PredictionResultConverter.toLabel("yes"));
		possibileResults.add(PredictionResultConverter.toLabel("no"));
		return possibileResults;
	}

	@Override
	public HashMap<String, String> getClassification() {
		return classification;
	}

}
