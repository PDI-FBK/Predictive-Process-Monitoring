package org.processmining.plugins.predictive_monitor.bpm.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.classification.ArffBuilder;
import org.processmining.plugins.predictive_monitor.bpm.classification.Predictor;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.sequence_mining.SequenceMiner;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier.PredictionType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.ActivationVerificationGapClassifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.SatisfactionClassifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.TimeClassifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.DataCondFormula;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.enumeration.DeclareTemplate;
import org.processmining.plugins.predictive_monitor.bpm.utility.XLogReader;

import weka_predictions.core.Instance;
import weka_predictions.core.Instances;
import weka_predictions.data_predictions.Result;


public class Test {

	/**
	 * @param args
	 */
	/*
	public static void main(String[] args) {
		String inputLogFilePath = "./input/loan_test_wo_age.mxml";

		double minSup = 0.5;
		int minLength = 2;		
		try {
			XLog logOriginal = XLogReader.openLog(inputLogFilePath);
			XLog log = (XLog) logOriginal.clone();
			log.remove(0);
			
			XLog traceLog = (XLog) logOriginal.clone();
			for (int i = 1; i < traceLog.size(); i++) {
				traceLog.remove(i);
			}
			
			XTrace currentTrace = traceLog.get(0);
			for (int i = 4; i > 0; i--) {
				currentTrace.remove(currentTrace.size()-i);
			}
			
			//Formula formula = new SimpleFormula("<>(\"request_complex_accepted\")");
			//Formula formula = new DataCondFormula("[salary>1000][][]","retrieve_u_data","request_denied",DeclareTemplate.Response);
			Formula formula = new DataCondFormula("[salary>1000][T.length>25][0,40,d]","retrieve_u_data","request_rejected",DeclareTemplate.Response);
			
			Vector<Formula> formulas = new Vector<Formula>();
			formulas.add(formula);
				
			
			//XTrace currentTrace = (XTrace) trace.clone();

			ArrayList patterns = (ArrayList<Pattern>) SequenceMiner.mineFrequentPatternsMaxSPWithHoles(log, minSup, minLength);

			Classifier classifier=null;
			if(ServerConfigurationClass.predictionType==PredictionType.FORMULA_SATISFACTION)
			{
				classifier = new SatisfactionClassifier(log,formulas);
			}
			else if(ServerConfigurationClass.predictionType==PredictionType.FORMULA_SATISFACTION_TIME)
			{
				classifier = new TimeClassifier(log,formulas,ServerConfigurationClass.partitionMethod,ServerConfigurationClass.numberOfIntervals);
			}
			else if(ServerConfigurationClass.predictionType==PredictionType.ACTIVATION_VERIFICATION_FORMULA_TIME)
			{
				classifier=new ActivationVerificationGapClassifier(log);
			}
			
			
			//File arff = ArffBuilder.writeArffFile(listener, formulas, currentTrace, log, minSup, minLength, patterns);
			Instances inst = ArffBuilder.instancesGenerator(log, patterns, classifier);
			HashMap<String, ArrayList<String>> attributeTypes = ArffBuilder.getAttributeTypes();
			
			
			HashMap<String, String> variables = new HashMap<String, String>();
			XAttributeMap traceAttr = currentTrace.getAttributes();
			for (String attr : traceAttr.keySet()) {
				if(!attr.equals("Activity code") &&!attr.equals("creator")&&!attr.contains(":")&& !attr.equals("description")){
					variables.put(attr, traceAttr.get(attr).toString());
				}
			}
			//ArrayList<Pattern> patterns = (ArrayList<Pattern>) SequenceMiner.mineFrequentPatternsMaxSPWithHoles(log, minSup, minLength);
			Instance instance = ArffBuilder.getTraceInstance(currentTrace, patterns, attributeTypes, variables);

			
			Map<String, Result> suggestions = Predictor.makePredictionRandomForest(inst, instance);
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}*/


}
