package org.processmining.plugins.predictive_monitor.bpm.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.naming.InitialContext;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.declareminer.Watch;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ConfidenceAndSupportVotingStrategy;
import org.processmining.plugins.predictive_monitor.bpm.configuration.TraceEvaluation;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.PredictionResult;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.PredictionResultConverter;
import org.processmining.plugins.predictive_monitor.bpm.server.data.ClassifiedData;
import org.processmining.plugins.predictive_monitor.bpm.server.data.ClusteredData;
import org.processmining.plugins.predictive_monitor.bpm.server.data.LabelledData;
import org.processmining.plugins.predictive_monitor.bpm.server.data.TrainingData;
import org.processmining.plugins.predictive_monitor.bpm.server.data_controller.DataControllerFactory;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier.PredictionType;

import weka_predictions.data_predictions.Result;

public class ServerConfiguration {
	private double minConfidence;
	private int minSupport;
	private int evaluationGap;
	private int evaluationStartPoint;
	private Map<String,SuggestionGenerator> suggestionGenerators;
	private ConfidenceAndSupportVotingStrategy confidenceAndSupportVotingStrategy;
	private long inizializationTime;
	private boolean jumpToCurrentEvent;
	//private boolean generateLog;
	
	public ServerConfiguration(Map<String,Object> configuration, String runId)
	{	
	
		suggestionGenerators = new HashMap<>();
		
		minConfidence=(double)configuration.get("minConfidence");
		minSupport=(int)configuration.get("minSupport");
		evaluationGap=(int)configuration.get("evaluationGap");
		evaluationStartPoint=(int)configuration.get("evaluationStartPoint");
		confidenceAndSupportVotingStrategy = (ConfidenceAndSupportVotingStrategy) configuration.get("confidenceAndSupportVotingStrategy");
		//jumpToCurrentEvent = (boolean) configuration.get("jumpToCurrentIndex");
		//generateLog = (boolean) configuration.get("generateLog");
		inizializationTime = 0;
		
		
		Map<String,PredictionType> predictionTypes = (Map<String,PredictionType>) configuration.get("predictionType");
		configuration.remove("predictionType");
		
		try{
			for(String predictionType : predictionTypes.keySet())
			{
				Map<String,Object> currentConfiguration = new HashMap<>();
				currentConfiguration.putAll(configuration);
				currentConfiguration.put("predictionType",predictionTypes.get(predictionType));
				
				System.out.println("########## RUN:"+runId+" PREDICTION TYPE:"+predictionType+" ##########\n");
				System.out.println("--LOADING TRAINING FILES");
				TrainingData trainingData = DataControllerFactory.getTrainingDataController().getTrainingData(currentConfiguration);
				System.out.println("--LOADED\n");
				inizializationTime+=trainingData.getInitTime();
				System.out.println("--LABELLING TRAINING LOG");
				LabelledData labelledData = DataControllerFactory.getLabelledDataController().getLabelledData(currentConfiguration,trainingData);
				System.out.println("--LABELLED\n");
				inizializationTime+=labelledData.getInitTime();
				System.out.println("--CLUSTERING TRAINING LOG");
				ClusteredData clusteredData = DataControllerFactory.getClusteredDataController().getClusteredData(currentConfiguration, labelledData);
				System.out.println("--CLUSTERED\n");
				inizializationTime+=clusteredData.getInitTime();
				System.out.println("--CREATING CLASSIFICATION STRUCTURE");
				ClassifiedData classifiedData = DataControllerFactory.getClassifiedDataController().getClassifiedData(currentConfiguration, clusteredData, labelledData);
				System.out.println("--CLASSIFIED\n");
				inizializationTime+=classifiedData.getInitTime();
				System.out.println("########## END ##########\n\n");
				
				
				suggestionGenerators.put(predictionType,new SuggestionGenerator(clusteredData, classifiedData, confidenceAndSupportVotingStrategy));
			}
			
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	public PredictionResult evaluateTrace(XTrace trace, boolean partial)
	{
		TraceEvaluation traceEvaluation;
		if(partial){
			traceEvaluation = new TraceEvaluation(trace, evaluationStartPoint, evaluationGap);
		}
		else {
			traceEvaluation = new TraceEvaluation(trace, trace.size()+1, evaluationGap);
		}
		System.out.println("Trace "+traceEvaluation.getName());
		Map<String,Result> lastResult = null;
		Map<String,Result> lastValidResult = new HashMap<>();
		Double lastConfidence = new Double(0);
		Double lastSupport = new Double(0);
		int lastEvaluationPoint = 0;
		Watch w = new Watch();
		w.start();
		
		do{
			lastResult = new HashMap<>();
			System.out.println("\t"+traceEvaluation.getCurrentIndex());
			Double confidence = null;
			Double support = null;
			for(String s: suggestionGenerators.keySet())
			{
				Result result = suggestionGenerators.get(s).evaluateTrace(traceEvaluation);
				if(result != null)
				{
					System.out.println("\t\t"+result.getLabel()+ " "+result.getConfidence()+" "+result.getSupport());
					lastResult.put(s,result);
					
					if(confidence == null) {
						confidence = new Double(result.getConfidence());
					} else {
						confidence = new Double(confidence*result.getConfidence());
					}
					
					
					if(support == null){
						support = new Double(result.getSupport());
					} else {
						support = new Double(Math.min(support, result.getSupport()));
					}
				}
				else
				{
					confidence = new Double(0);
					support = new Double(0);
					break;
				}
			}
			if(lastResult.size()!=0)
			{
				lastValidResult = lastResult;
			}
			if(confidence!=null && support!=null)
			{
				lastConfidence = confidence;
				lastSupport = support;
				lastEvaluationPoint = traceEvaluation.getCurrentIndex();
			}
			//Result result = lastSuggestion.get(lastSuggestion.keySet().toArray()[0]);
			
			if(lastResult.size()!=0)
			{
				if(confidence.doubleValue()>=minConfidence && support.doubleValue()>=minSupport)
				{
					System.out.println("\tCONFIDENCE AND SUPPORT OK");
					long lastEvaluatedEventTime = 0;
					return PredictionResultConverter.convertResult(lastResult,confidence,support,traceEvaluation.getCurrentIndex(), true, w.msecs());
				}
			}
		}while(traceEvaluation.nextEvaluationPoint());
		System.out.println("\tTRACE END");
		return PredictionResultConverter.convertResult(lastValidResult,lastConfidence,lastSupport, lastEvaluationPoint, false, w.msecs());
	}
	
	public long getInitTime()
	{
		return inizializationTime;
	}
	
}

