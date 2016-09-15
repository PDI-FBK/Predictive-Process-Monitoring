package org.processmining.plugins.predictive_monitor.bpm.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.declareminer.Watch;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ConfidenceAndSupportVotingStrategy;
import org.processmining.plugins.predictive_monitor.bpm.configuration.TraceEvaluation;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.PredictionResult;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.PredictionResultConverter;
import org.processmining.plugins.predictive_monitor.bpm.server.data.ClassifiedData;
import org.processmining.plugins.predictive_monitor.bpm.server.data.ClusteredData;

import weka_predictions.data_predictions.Result;

public class SuggestionGenerator {
	private ClassifiedData classifiedData;
	private ClusteredData clusteredData;
	private ConfidenceAndSupportVotingStrategy confidenceAndSupportVotingStrategy;
	
	public SuggestionGenerator (ClusteredData clusteredData, ClassifiedData classifiedData, ConfidenceAndSupportVotingStrategy confidenceAndSupportVotingStrategy)
	{
		this.clusteredData = clusteredData;
		this.classifiedData = classifiedData;
		this.confidenceAndSupportVotingStrategy = confidenceAndSupportVotingStrategy;
	}
	
	public Result evaluateTrace(TraceEvaluation traceEvaluation)
	{
		try{
			Map<String, Result> bestSuggestions;
			Map<String, Integer> classificationMap;
			List<Map<String, Result>> allSuggestions;
			Result result=null;
			XTrace lastTrace = traceEvaluation.getPartialTrace();
			HashMap <String, String> variables=traceEvaluation.getVariables();
			Vector<String> currentVariables=traceEvaluation.getCurrentVariables();
			List<Integer> topClusters = clusteredData.computeTopClusters(lastTrace);
			bestSuggestions = new HashMap<String, Result>();
			classificationMap = new HashMap<String, Integer>();
			allSuggestions = new ArrayList<Map<String, Result>>();
		
		
			for (Integer clusterNumber : topClusters) {
				int cl = clusterNumber.intValue();
				Map<String, Result> suggestions = classifiedData.computeSuggestions(cl, lastTrace, variables, currentVariables);
				
				for (String key : suggestions.keySet()) {
					result = suggestions.get(key);
					String label = result.getLabel();
					if(label!="")
					{
						if(classificationMap.containsKey(label))					
						{
							classificationMap.put(label, classificationMap.get(label)+1);
						}
						else
						{
							classificationMap.put(label, 1);
						}
					}
				}
				allSuggestions.add(suggestions);
			}
			if (!topClusters.isEmpty()){
				String dominantLabel = "";
				int count=0;
				
				for(String label:classificationMap.keySet())
				{
					if(classificationMap.get(label) > count)
					{
						dominantLabel=label;
					}
				}
				
				double maxConf = 0.0;
				double maxSupp = 0;
				double minConf = 0.0;
				double minSupp = 0;
				
				for(Map<String, Result> suggestion : allSuggestions) {
					result = suggestion.get(suggestion.keySet().toArray()[0]);
					String label = result.getLabel();
					if (label.equals(dominantLabel)) {
						switch (confidenceAndSupportVotingStrategy) {
						case MAX:					
							if ((result.getConfidence()>maxConf) || (result.getConfidence()==maxConf && result.getSupport()>maxSupp)){
								bestSuggestions = suggestion;
								maxConf = result.getConfidence();
								maxSupp = result.getSupport();
							}
							break;
						case MIN:
							if ((result.getConfidence()<minConf) || (result.getConfidence()==minConf && result.getSupport()<minSupp)){
								bestSuggestions = suggestion;
								minConf = result.getConfidence();
								minSupp = result.getSupport();							
							}
							break;						
						default:
							break;
						}
	
					}
				}
				
				Result finalResult = null;
				if(bestSuggestions.size()>0)
				{
				 finalResult = bestSuggestions.get(bestSuggestions.keySet().toArray()[0]);
				}
				
				return finalResult;
			}
		}
		catch (Exception e){
			//Do nothing
		}
		return null;
	}
}
