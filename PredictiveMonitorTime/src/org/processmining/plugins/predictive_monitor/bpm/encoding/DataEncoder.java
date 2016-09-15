package org.processmining.plugins.predictive_monitor.bpm.encoding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.classification.ArffBuilder;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier;

import weka_predictions.core.Instance;
import weka_predictions.core.Instances;

public class DataEncoder extends Encoder{

	public Instances getInstances(XLog log, Classifier classifier){
		Instances inst = ArffBuilder.instancesGenerator(log, classifier);
		return inst;
	}
	
	public Instances getInstances(XLog log, Classifier classifier, ArrayList<Pattern> patterns){
		Instances inst = ArffBuilder.instancesGenerator(log, patterns, classifier);
		return inst;
	}
	
	public Map<String, String> encodeTraceDataAndPatterns(XTrace trace,  ArrayList<Pattern> patterns, Map<String, String> variables ){
		ArrayList<Integer> patternVector = ArffBuilder.generatePatternVector(trace, patterns);
		for (int i = 0; i < patternVector.size(); i++) {
			Integer pattern = patternVector.get(i);
			String patternId = "P"+i;
			String value = null;
			if(pattern>0)
				value="true";
			else
				value="false";
			variables.put(patternId, value);
		}
		return variables;
	}
	
	public Instance encodeTraceDataAndPatterns(XTrace trace, ArrayList<Pattern> patterns, HashMap<String, ArrayList<String>> attributeType, HashMap<String, String> variables){
		return ArffBuilder.getTraceInstance(trace, patterns, attributeType, variables);

	}
}