package org.processmining.plugins.predictive_monitor.bpm.classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.encoding.DataEncoder;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.enumeration.PatternType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier;
import org.processmining.plugins.predictive_monitor.bpm.utility.Print;

import weka_predictions.classifiers.trees.RandomForest_predictions;
import weka_predictions.classifiers.trees.j48.J48_predictions;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;
import weka_predictions.data_predictions.Result;


public class ClassificationController {
	private Instances instances = null;
	private Print print = new Print();
	public J48_predictions trainDecisionTree(XLog log, Classifier classifier){
		DataEncoder dataEncoder = new DataEncoder();
		instances = dataEncoder.getInstances(log, classifier);
		J48_predictions tree = Predictor.trainDecisionTree(instances, classifier);
		return tree;
	}
	
	
	public J48_predictions trainDecisionTree(XLog log, Classifier classifier, ArrayList<Pattern> classificationPatterns, PatternType classificationPatternType){
		DataEncoder dataEncoder = new DataEncoder();
		Instances inst = null;
		switch (classificationPatternType) {
		case DISCRIMINATIVE:
		case SEQUENTIAL_WITHOUT_HOLES:
		case SEQUENTIAL_WITH_HOLES:
		case DISCR_SEQUENTIAL_WITHOUT_HOLES:
		case DISCR_SEQUENTIAL_WITH_HOLES:
			inst = dataEncoder.getInstances(log, classifier, classificationPatterns);
			break;
		case NONE:
		default:
			inst = dataEncoder.getInstances(log, classifier);
			break;
		}
		J48_predictions tree = Predictor.trainDecisionTree(inst, classifier);
		instances = inst;
		return tree;
	}
			
	
	public RandomForest_predictions trainRandomForest(XLog log, Classifier classifier, int rFMaxDepth, int rFNumFeatures, int rFNumTrees, int rFSeed){
		DataEncoder dataEncoder = new DataEncoder();
		Instances inst = dataEncoder.getInstances(log, classifier);
		instances = inst;
		RandomForest_predictions rF=Predictor.trainRandomForest(inst, classifier, rFMaxDepth, rFNumFeatures, rFNumTrees, rFSeed);

		return rF;
	}
	
	public RandomForest_predictions trainRandomForest(XLog log, Classifier classifier, ArrayList<Pattern> classificationPatterns, PatternType classificationPatternType, int rFMaxDepth, int rFNumFeatures, int rFNumTrees, int rFSeed){
		DataEncoder dataEncoder = new DataEncoder();
		Instances inst = null;
		switch (classificationPatternType) {
		case DISCRIMINATIVE:
		case SEQUENTIAL_WITHOUT_HOLES:
		case SEQUENTIAL_WITH_HOLES:
		case DISCR_SEQUENTIAL_WITHOUT_HOLES:
		case DISCR_SEQUENTIAL_WITH_HOLES:
			inst = dataEncoder.getInstances(log, classifier, classificationPatterns);
			break;
		case NONE:
		default:
			inst = dataEncoder.getInstances(log, classifier);
			break;
		}
		instances = inst;
		RandomForest_predictions rF=Predictor.trainRandomForest(inst, classifier, rFMaxDepth, rFNumFeatures, rFNumTrees, rFSeed);

		return rF;
	}
	
	//public String getArffFilePath(){
	//	return arffFilePath;
	//}
	
	public Instances getInstances(){
		return instances;
	}
	
	public Map<String, Result> classifyTraceDecisionTree(XTrace trace, ArrayList<Pattern> patterns, Map<String, String> variables, Vector<String> currentVariables, J48_predictions tree){
		DataEncoder dataEncoder = new DataEncoder();
		Map<String, String> encodedTrace = dataEncoder.encodeTraceDataAndPatterns(trace, patterns, variables);
		try {
			print.that(tree.graph());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Result> suggestions = Predictor.makePredictionDecisionTree(tree, currentVariables, variables);
		return suggestions;		
	}
	
	public Map<String, Result> classifyTraceDecisionTree(XTrace trace,  Map<String, String> variables, Vector<String> currentVariables, J48_predictions tree){
		DataEncoder dataEncoder = new DataEncoder();
		Map<String, String> encodedTrace = variables;
		try {
		//	System.out.println(tree.graph());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Result> suggestions = null;
		if(encodedTrace!=null && instances!=null)
			suggestions = Predictor.makePredictionDecisionTree(tree, currentVariables, variables);
		return suggestions;		
	}
	
	public Map<String, Result> classifyTraceRandomForest(XTrace trace, ArrayList<Pattern> patterns, HashMap<String, String> variables, HashMap<String, ArrayList<String>> arrayType, RandomForest_predictions randomForest, Instances instances){
		DataEncoder dataEncoder = new DataEncoder();
		Instance encodedTrace = dataEncoder.encodeTraceDataAndPatterns(trace, patterns, arrayType, variables);
		Map<String, Result> suggestions = null;
		/**/
		/**/
		if(encodedTrace!=null && instances!=null)
			suggestions = Predictor.makePredictionRandomForest(randomForest, instances, encodedTrace);
		return suggestions;		
	}
}
