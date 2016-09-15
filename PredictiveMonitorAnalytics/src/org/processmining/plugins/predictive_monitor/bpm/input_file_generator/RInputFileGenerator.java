package org.processmining.plugins.predictive_monitor.bpm.input_file_generator;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.predictive_monitor.bpm.encoding.FrequencyBasedEncoder;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.PatternController;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier.PredictionType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.ActivationVerificationGapClassifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.SatisfactionClassifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.TimeClassifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.SimpleFormula;
import org.processmining.plugins.predictive_monitor.bpm.utility.TracePrefixGenerator;
import org.processmining.plugins.predictive_monitor.bpm.utility.XLogReader;
import org.processmining.plugins.predictive_monitor.bpm.utility.XLogWriter;

import weka_predictions.core.Instances;

public class RInputFileGenerator {
/*
	public static void main(String[] args) {
		String inputLogFilePath = ServerConfigurationClass.trainingInputLogFile;
		String outputPrefixInputEventLogFilePath = ServerConfigurationClass.prefixInputEventLogFile;
		String outputFrequencyLogFilePath = ServerConfigurationClass.frequencyTracesFilePath;
		// generateTracePrefixFile(inputLogFilePath,
		// outputPrefixInputEventLogFilePath);
		generateEventAndPatternFrequencybasedEncodingFile(inputLogFilePath,
				outputFrequencyLogFilePath);
	}*/

	public static void generateTracePrefixFile(String inputLogFilePath,	String outputEventLogFilePath) {
//		try {
//			XLog log = XLogReader.openLog(inputLogFilePath);
//			XLog prefixLog = TracePrefixGenerator.generatePrefixesFromLog(log,
//					ServerConfigurationClass.minPrefixLength,
//					ServerConfigurationClass.maxPrefixLength,
//					ServerConfigurationClass.prefixGap);
//			XLogWriter.writeEventLog(prefixLog, outputEventLogFilePath);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	public static void generateFrequencybasedEncodingFile( String inputLogFilePath, String frequencyFilePath) {
//		try {
//			XLog log = XLogReader.openLog(inputLogFilePath);
//			XLog prefixLog = TracePrefixGenerator.generatePrefixesFromLog(log,
//					ServerConfigurationClass.minPrefixLength,
//					ServerConfigurationClass.maxPrefixLength,
//					ServerConfigurationClass.prefixGap);
//			FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
//			encoder.encodeTraces(prefixLog);
//			Instances encodedTraces = encoder.getEncodedTraces();
//			XLogWriter.writeLogFrequencies(encodedTraces, frequencyFilePath);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public static void generatePatternOccurrencebasedEncodingFile( String inputLogFilePath, String frequencyFilePath) {
//		try {
//			XLog log = XLogReader.openLog(inputLogFilePath);
//			XLog prefixLog = TracePrefixGenerator.generatePrefixesFromLog(log,
//					ServerConfigurationClass.minPrefixLength,
//					ServerConfigurationClass.maxPrefixLength,
//					ServerConfigurationClass.prefixGap);
//			List<Pattern> patterns = PatternController
//					.generateDiscriminativePatterns(
//							prefixLog,
//							getLabels(log).getClassification(),
//							ServerConfigurationClass.discriminativePatternMinimumSupport,
//							ServerConfigurationClass.discriminativePatternCount,
//							ServerConfigurationClass.sameLengthDiscriminativePatternCount,
//							ServerConfigurationClass.discriminativePatternFilePath);
//			// List<Pattern> patterns =
//			// readPatternsFromFile(ServerConfigurationClass.discriminativePatternFilePath);
//			FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
//			encoder.encodeTracesBasedOnPatternOccurrence(prefixLog, patterns);
//			Instances encodedTraces = encoder.getEncodedTraces();
//			XLogWriter.writeLogFrequencies(encodedTraces, frequencyFilePath);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private static Classifier getLabels(XLog log) {
//		String[] phi = new String[4];
//		phi[0] = "(  <>(\"tumor marker CA-19.9\") ) \\/ ( <> (\"ca-125 using meia\") )  ";
//		phi[1] = "([](    ((\"CEA - tumor marker using meia\") -> ( <>(\"squamous cell carcinoma using eia\")))))";
//		phi[2] = "(  (! (\"histological examination - biopsies nno\")) U (\"squamous cell carcinoma using eia\"))";
//		phi[3] = "   ( <> (\"histological examination - big resectiep\") )   ";
//		Vector<Formula> formulaVector = new Vector<Formula>();
//		String[] formulas = new String[]{phi[0]};
//		for (String lTLFormula : formulas) {
//			SimpleFormula formula = new SimpleFormula(lTLFormula);
//			formulaVector.add(formula);
//		}
//		
//		Classifier classifier=null;
//		if(ServerConfigurationClass.predictionType==PredictionType.FORMULA_SATISFACTION)
//		{
//			classifier = new SatisfactionClassifier(log,formulaVector);
//		}
//		else if(ServerConfigurationClass.predictionType==PredictionType.FORMULA_SATISFACTION_TIME)
//		{
//			classifier = new TimeClassifier(log,formulaVector,ServerConfigurationClass.partitionMethod,ServerConfigurationClass.numberOfIntervals);
//		}
//		else if(ServerConfigurationClass.predictionType==PredictionType.ACTIVATION_VERIFICATION_FORMULA_TIME)
//		{
//			classifier=new ActivationVerificationGapClassifier(log);
//		}
//			
//		return classifier;
//		
		return null; //REMOVE
	}

	public static void generatePatternFrequencybasedEncodingFile(String inputLogFilePath, String frequencyFilePath) {
//		try {
//			XLog log = XLogReader.openLog(inputLogFilePath);
//			XLog prefixLog = TracePrefixGenerator.generatePrefixesFromLog(log,
//					ServerConfigurationClass.minPrefixLength,
//					ServerConfigurationClass.maxPrefixLength,
//					ServerConfigurationClass.prefixGap);
//			List<Pattern> patterns =
//			PatternController.generateDiscriminativePatterns(prefixLog, getLabels(log).getClassification(),
//			ServerConfigurationClass.discriminativePatternMinimumSupport,
//			ServerConfigurationClass.discriminativePatternCount,
//			ServerConfigurationClass.sameLengthDiscriminativePatternCount,
//			ServerConfigurationClass.discriminativePatternFilePath);
////			List<Pattern> patterns = PatternController
////					.readPatternsFromFile(ServerConfigurationClass.discriminativePatternFilePath);
//			FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
//			encoder.encodeTracesBasedOnPatternFrequency(prefixLog, patterns);
//			Instances encodedTraces = encoder.getEncodedTraces();
//			XLogWriter.writeLogFrequencies(encodedTraces, frequencyFilePath);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public static void generateEventAndPatternFrequencybasedEncodingFile(String inputLogFilePath, String frequencyFilePath) {
//		try {
//			XLog log = XLogReader.openLog(inputLogFilePath);
//			XLog prefixLog = TracePrefixGenerator.generatePrefixesFromLog(log,
//					ServerConfigurationClass.minPrefixLength,
//					ServerConfigurationClass.maxPrefixLength,
//					ServerConfigurationClass.prefixGap);
//			List<Pattern> patterns = PatternController
//					.generateDiscriminativePatterns(
//							prefixLog, getLabels(log).getClassification(),
//							ServerConfigurationClass.discriminativePatternMinimumSupport,
//							ServerConfigurationClass.discriminativePatternCount,
//							ServerConfigurationClass.sameLengthDiscriminativePatternCount,
//							ServerConfigurationClass.discriminativePatternFilePath);
//			// List<Pattern> patterns =
//			// PatternController.readPatternsFromFile(ServerConfigurationClass.discriminativePatternFilePath);
//			FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
//			encoder.encodeTracesBasedOnEventAndPatternFrequency(prefixLog,
//					patterns);
//			Instances encodedTraces = encoder.getEncodedTraces();
//			XLogWriter.writeLogFrequencies(encodedTraces, frequencyFilePath);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
