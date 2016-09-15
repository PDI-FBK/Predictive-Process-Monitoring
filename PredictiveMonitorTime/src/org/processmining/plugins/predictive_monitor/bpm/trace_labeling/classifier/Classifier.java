package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier;

import java.util.HashMap;
import java.util.List;

public interface Classifier {
	public static enum PredictionType {FORMULA_SATISFACTION,FORMULA_SATISFACTION_TIME,ACTIVATION_VERIFICATION_FORMULA_TIME};
	public HashMap<String,String> getClassification();
	public List<String> getLabels();
}
