package org.processmining.plugins.predictive_monitor.bpm.unfoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.processmining.plugins.predictive_monitor.bpm.classification.enumeration.ClassificationType;
import org.processmining.plugins.predictive_monitor.bpm.replayer.GlobalResultListener;
import org.processmining.plugins.predictive_monitor.bpm.client_interface.GUI;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ClusteringType;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ConfidenceAndSupportVotingStrategy;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.HierarchicalDistanceMetrics;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ModelClusteringFrom;
import org.processmining.plugins.predictive_monitor.bpm.configuration.ConfigurationSet;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Dependencies;
import org.processmining.plugins.predictive_monitor.bpm.configuration.modules.*;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.enumeration.PatternType;
import org.processmining.plugins.predictive_monitor.bpm.replayer.ConfigurationSender;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier.PredictionType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.TimeClassifier.PartitionMethod;

public class Unfolder {
	
	ConfigurationSet confSet;
	String runId;
	ConfigurationSender configurationSender;
	
	public Unfolder (ConfigurationSet conf, String runId, ConfigurationSender configurationSender){
		this.confSet = conf;
		this.runId = runId;
		this.configurationSender = configurationSender;
	}
	
	public void unfoldAndSendConf(){
		GUI.unfoldedValues = new HashMap <String, Map<String, Object>>();
		Map <String, Object> configuration = new HashMap<String, Object>();
		
		Classification classification = confSet.getClassification();
		Clustering clustering = confSet.getClustering();
		Evaluation evaluation = confSet.getEvaluation();
		TrainingTracesModule trainingTraces = confSet.getTrainingTraces();
		PredictionTypeModule predictionTypeModule = confSet.getPredictionType();
		//LogOption logOption = confSet.getLogOption();
		
		Integer count = 0;
		
		boolean jumpToCurrentIndex = (boolean)predictionTypeModule.getJumpToCurrentIndex().getSelectedValues().iterator().next();
		
		/*Chiedo scusa a coloro che leggeranno questo codice*/
		/*NB: Pay attention to the names of the values inserted as keyset in configuration map!*/
		
 		for (Object classificationType : classification.getClassificationType().getSelectedValues()){
 			for(Object classificationPatternType : classification.getClassificationPatternType().getSelectedValues()){
 				for(Object classificationPatternMinimumSupport : classification.getClassificationPatternMinimumSupport().getSelectedValues()){
 					for(Object classificationDiscriminativeMinimumSupport : classification.getClassificationDiscriminativeMinimumSupport().getSelectedValues()){
			 			for(Object classificationDiscriminativePatternCount : classification.getClassificationDiscriminativePatternCount().getSelectedValues()){
			 				for(Object classificationMaximumPatternLength : classification.getClassificationMaximumPatternLength().getSelectedValues()){
			 					for(Object classificationMinimumPatternLength : classification.getClassificationMinimumPatternLength().getSelectedValues()){
			for(Object classificationSameLengthDiscriminativePatternCount : classification.getClassificationSameLengthDiscriminativePatternCount().getSelectedValues()){
				for(Object rFMaxDepth : classification.getrFMaxDepth().getSelectedValues()){
					for(Object rFNumFeatures : classification.getrFNumFeatures().getSelectedValues()){
						for(Object rFNumTrees : classification.getrFNumTrees().getSelectedValues()){
			for(Object rfSeed : classification.getRfSeed().getSelectedValues()){
				for(Object clusteringType: clustering.getClusteringType().getSelectedValues()){
					for(Object clusteringPatternType: clustering.getClusteringPatternType().getSelectedValues()){
						for(Object clusteringDiscriminativePatternCount : clustering.getClusteringDiscriminativePatternCount().getSelectedValues()){
							for(Object clusteringDiscriminativeMinimumSupport : clustering.getClusteringDiscriminativeMinimumSupport().getSelectedValues()){							
			for(Object clusteringMaximumPatternLength : clustering.getClusteringMaximumPatternLength().getSelectedValues()){
				for(Object clusteringMinimumPatternLength : clustering.getClusteringMinimumPatternLength().getSelectedValues()){
					for(Object clusteringSameLengthDiscriminativePatternCount : clustering.getClusteringSameLengthDiscriminativePatternCount().getSelectedValues()){
						for(Object hierarchicalDistanceMetrics : clustering.getHierarchicalDistanceMetrics().getSelectedValues()){
			for(Object modelClusteringFrom : clustering.getModelClusteringFrom().getSelectedValues()){
				for(Object clusterNumber : clustering.getClusterNumber().getSelectedValues()){
					for(Object clusteringPatternMinimumSupport : clustering.getClusteringPatternMinimumSupport().getSelectedValues()){
						for(Object clusteringDiscriminativePatternMinimumSupport : clustering.getClusteringDiscriminativePatternMinimumSupport().getSelectedValues()){
			for(Object dBScanEpsilon : clustering.getdBscanEpsilon().getSelectedValues()){
				for(Object dBScanMinPoints : clustering.getdBScanMinPoints().getSelectedValues()){
					for(Object testingInputLogFile: evaluation.getTestingInputLogFile().getSelectedValues()){
						for(Object predictionForEvaluation : evaluation.getPredictionForEvaluation().getSelectedValues()){
			for(Object classProbability : evaluation.getClassProbability().getSelectedValues()){
				for(Object classSupport : evaluation.getClassSupport().getSelectedValues()){
					for(Object evaluationGap : evaluation.getEvaluationGap().getSelectedValues()){
						for(Object evaluationStartPoint : evaluation.getEvaluationStartPoint().getSelectedValues()){
			for(Object formulas : predictionTypeModule.getFormulas().getSelectedValues()){
					//for(Object jumpToCurrentIndex : predictionTypeModule.getJumpToCurrentIndex().getSelectedValues()){
			for(Object trainingFile : trainingTraces.getTrainingFile().getSelectedValues()){
				for(Object predictionType : predictionTypeModule.getPredictionType().getSelectedValues()){
					for(Object partitionMethod : predictionTypeModule.getPartitionMethod().getSelectedValues()){
						for(Object numberOfintervals : predictionTypeModule.getNumberOfIntervals().getSelectedValues()){
			//for(Object logFilePath : logOption.getLogFilePath().getSelectedValues()){
				//for(Object generateArffReport : logOption.getGenerateArffReport().getSelectedValues()){
					//for(Object printDebug : logOption.getPrintDebug().getSelectedValues()){
						//for(Object printLog : logOption.getPrintlog().getSelectedValues()){
			for(Object minPrefixLength : trainingTraces.getMinPrefixLength().getSelectedValues()){
				for(Object maxPrefixLength : trainingTraces.getMaxPrefixLength().getSelectedValues()){
					for(Object prefixGap: trainingTraces.getPrefixGap().getSelectedValues()){
				for(Object confidenceAndSupportVotingStrategy : clustering.getConfidenceAndSupportVotingStrategy().getSelectedValues()){
					for(Object useVotingForClustering : clustering.getUseVotingForClustering().getSelectedValues()){
						for(Object voters : clustering.getVoters().getSelectedValues()){
							
							ClassificationType classificationTypeEnum = null;
							switch(classificationType.toString()){
								case "DECISION_TREE" : classificationTypeEnum = ClassificationType.DECISION_TREE;
									break;
								case "RANDOM_FOREST" : classificationTypeEnum = ClassificationType.RANDOM_FOREST;
									break;
							}
							configuration.put("classificationType", classificationTypeEnum);
							
							PatternType classificationPatternTypeEnum = null;
							switch(classificationPatternType.toString()) {
								case "DISCRIMINATIVE" : classificationPatternTypeEnum = PatternType.DISCRIMINATIVE;
									break;
								case "SEQUENTIAL_WITH_HOLES" : classificationPatternTypeEnum = PatternType.SEQUENTIAL_WITH_HOLES;
								break;
								case "SEQUENTIAL_WITHOUT_HOLES" : classificationPatternTypeEnum = PatternType.SEQUENTIAL_WITHOUT_HOLES;
								break;
								case "DISCR_SEQUENTIAL_WITH_HOLES" : classificationPatternTypeEnum = PatternType.DISCR_SEQUENTIAL_WITH_HOLES;
								break;
								case "DISCR_SEQUENTIAL_WITHOUT_HOLES": classificationPatternTypeEnum = PatternType.DISCR_SEQUENTIAL_WITHOUT_HOLES;
								break;
								case "NONE" : classificationPatternTypeEnum = PatternType.NONE;
								break;
							}
							configuration.put("classificationPatternType", classificationPatternTypeEnum);
							
							configuration.put("classificationPatternMinimumSupport", classificationPatternMinimumSupport);
							configuration.put("classificationDiscriminativePatternCount", classificationDiscriminativePatternCount);
							configuration.put("classificationDiscriminativeMinimumSupport", classificationDiscriminativeMinimumSupport);
							configuration.put("classificationMaximumPatternLength", classificationMaximumPatternLength);
							configuration.put("classificationMinimumPatternLength", classificationMinimumPatternLength);
							configuration.put("classificationSameLengthDiscriminativePatternCount", classificationSameLengthDiscriminativePatternCount);
							configuration.put("rFMaxDepth", rFMaxDepth);
							configuration.put("rFNumFeatures", rFNumFeatures);
							configuration.put("rFNumTrees", rFNumTrees);
							configuration.put("rFSeed", rfSeed);
							
							ClusteringType clusteringTypeEnum = null;
							switch (clusteringType.toString()){
								case "DBSCAN": clusteringTypeEnum = ClusteringType.DBSCAN;
									break;
								case "KMEANS": clusteringTypeEnum = ClusteringType.KMEANS;
									break;
								case "KMEANSPLUSPLUS": clusteringTypeEnum = ClusteringType.KMEANSPLUSPLUS;
									break;
								case "MODEL-BASED": clusteringTypeEnum = ClusteringType.MODEL;
									break;
								case "AGGLOMERATIVE": clusteringTypeEnum = ClusteringType.AGGLOMERATIVE;
									break;
								case "NONE": clusteringTypeEnum = ClusteringType.NONE;
									break;
							}
							configuration.put("clusteringType", clusteringTypeEnum);
							
							PatternType clusteringPatternTypeEnum = null;
							switch(clusteringPatternType.toString()){
								case "DISCRIMINATIVE" : clusteringPatternTypeEnum = PatternType.DISCRIMINATIVE;
									break;
								case "SEQUENTIAL_WITH_HOLES" : clusteringPatternTypeEnum = PatternType.SEQUENTIAL_WITH_HOLES;
									break;
								case "SEQUENTIAL_WITHOUT_HOLES" : clusteringPatternTypeEnum = PatternType.SEQUENTIAL_WITHOUT_HOLES;
									break;
								case "DISCR_SEQUENTIAL_WITH_HOLES" : clusteringPatternTypeEnum = PatternType.DISCR_SEQUENTIAL_WITH_HOLES;
									break;
								case "DISCR_SEQUENTIAL_WITHOUT_HOLES" : clusteringPatternTypeEnum = PatternType.DISCR_SEQUENTIAL_WITHOUT_HOLES;
									break;
								case "NONE" : clusteringPatternTypeEnum = PatternType.NONE;
									break;
							}
							configuration.put("clusteringPatternType", clusteringPatternTypeEnum);
							
							configuration.put("clusteringDiscriminativePatternCount", clusteringDiscriminativePatternCount);
							configuration.put("clusteringDiscriminativeMinimumSupport", clusteringDiscriminativeMinimumSupport);
							configuration.put("clusteringMaximumPatternLength", clusteringMaximumPatternLength);
							configuration.put("clusteringMinimumPatternLength", clusteringMinimumPatternLength);
							configuration.put("clusteringSameLengthDiscriminativePatternCount", clusteringSameLengthDiscriminativePatternCount);
							
							HierarchicalDistanceMetrics hierarchicalDistanceMetricsEnum = null;
							switch(hierarchicalDistanceMetrics.toString()){
								case "EDIT_DISTANCE" : hierarchicalDistanceMetricsEnum = HierarchicalDistanceMetrics.EDIT_DISTANCE;
									break;
								case "EUCLIDEAN" : hierarchicalDistanceMetricsEnum = HierarchicalDistanceMetrics.EUCLIDEAN;
									break;
							}
							configuration.put("hierarchicalDistanceMetrics", hierarchicalDistanceMetricsEnum);
							
							ModelClusteringFrom modelClusteringFromEnum = null;
							switch(modelClusteringFrom.toString()){
								case "R" : modelClusteringFromEnum = ModelClusteringFrom.R;
									break;
								case "EXTERNAL_INPUT_FILE" : modelClusteringFromEnum = ModelClusteringFrom.EXTERNAL_INPUT_FILE;
									break;
							}
							configuration.put("modelClusteringFrom", modelClusteringFromEnum);
							
							configuration.put("clusterNumber", clusterNumber);
							configuration.put("clusteringPatternMinimumSupport", clusteringPatternMinimumSupport);
							configuration.put("clusteringDiscriminativePatternMinimumSupport", clusteringDiscriminativePatternMinimumSupport);
							configuration.put("dbScanEpsilon", dBScanEpsilon);
							configuration.put("dbScanMinPoints", dBScanMinPoints);
							
							Map<String,PredictionType> predictionTypeEnum = new HashMap<>();
							System.out.println("Prediction Type= " + predictionType.toString());
							switch(predictionType.toString()){
								case "FORMULA_SATISFACTION" : 
									predictionTypeEnum.put("satisfied",PredictionType.FORMULA_SATISFACTION);
									break;
								case "FORMULA_SATISFACTION_TIME" : 
									predictionTypeEnum.put("time",PredictionType.FORMULA_SATISFACTION_TIME);
									break;
								case "FORMULA_SATISFACTION_AND_TIME" : 
									predictionTypeEnum.put("satisfied",PredictionType.FORMULA_SATISFACTION);
									predictionTypeEnum.put("time",PredictionType.FORMULA_SATISFACTION_TIME);
									break;
							}
							configuration.put("predictionType", predictionTypeEnum);
							
							configuration.put("testingInputLogFile", testingInputLogFile);
							configuration.put("evaluationRun", predictionForEvaluation);
							configuration.put("minConfidence", classProbability);
							configuration.put("minSupport", classSupport);
							configuration.put("evaluationGap", evaluationGap);
							configuration.put("evaluationStartPoint", evaluationStartPoint);
							configuration.put("formulas", formulas);
							configuration.put("timeFromLastEvent", jumpToCurrentIndex);
							configuration.put("trainingFile", trainingFile);
							
							PartitionMethod partitionMethodEnum = null;
							System.out.println("Partition method= " + partitionMethod.toString());
							switch(partitionMethod.toString()){
								case "MAX_MINUS_MIN_OVER_N" : partitionMethodEnum = PartitionMethod.MAX_MINUS_MIN_OVER_N;
									break;
								case "NORMAL_TIME_DISTRIBUTION" : partitionMethodEnum = PartitionMethod.NORMAL_TIME_DISTRIBUTION;
									break;
								case "SORTED_DIVISION" : partitionMethodEnum = PartitionMethod.SORTED_DIVISION;
									break;
								case "FIXED_SORTED_DIVISION" : partitionMethodEnum = PartitionMethod.FIXED_SORTED_DIVISION;
									break;
							}
							configuration.put("partitionMethod", partitionMethodEnum);
							
							configuration.put("numberOfIntervals", numberOfintervals);
							
							/*Questi sono farlocchi*/
							configuration.put("discriminativePatternFilePath", "input/discriminative_patterns_k400_kprim399_supp100_gap5_max20.txt");
							/**/
							
							configuration.put("logFilePath", "DummyInput"/*logFilePath*/);
							configuration.put("generateArffReport", false/*generateArffReport*/);
							configuration.put("printDebug", false/*printDebug*/);
							configuration.put("generateLog", false /*printLog*/);
							configuration.put("minPrefixLength", minPrefixLength);
							configuration.put("maxPrefixLength", maxPrefixLength);
							configuration.put("prefixGap", prefixGap);
							
							ConfidenceAndSupportVotingStrategy confidenceAndSupportVotingStrategyEnum = null;
							switch(confidenceAndSupportVotingStrategy.toString()){
								case "MAX" : confidenceAndSupportVotingStrategyEnum = ConfidenceAndSupportVotingStrategy.MAX;
									break;
								case "MIN" : confidenceAndSupportVotingStrategyEnum = ConfidenceAndSupportVotingStrategy.MIN;
									break;
							}
							configuration.put("confidenceAndSupportVotingStrategy", confidenceAndSupportVotingStrategyEnum);
							
							configuration.put("useVotingForClustering", useVotingForClustering);
							configuration.put("voters", voters);
							
							count++;
							configuration.put("runSetId", runId);
							//configuration.put("runId", runId+"_"+count);
							
							GUI.unfoldedValues.put(runId + count, configuration);
							configuration = new HashMap<String, Object>();
							
						}
					}
				}
			}
						}
					}
				}
			}
						}
					}
				}
		//	}
						}
					}
				}
			}
						}
					}
				}
			}
						}
					}
				}
			}
						}
					}
				}
			}
						}
					}
				}									 					
			}
						}
					}
				}
			}
			//			}
			//		}
			//	}
			//}
						}}}}
					}
				}
			}
 		}
 		
 		Map<String,List<String>> toRemove = new HashMap<>();
 		
 		for(String key: GUI.unfoldedValues.keySet())
 		{
 			
 			toRemove.put(key, new ArrayList<>());
 			for(String parameter : GUI.unfoldedValues.get(key).keySet())
 			{
 				if(!Dependencies.isDependencySatisfied(parameter, GUI.unfoldedValues.get(key))){
 					toRemove.get(key).add(parameter);
 					System.out.println("REMOVED:"+parameter);
 				}
 			}
 		}
 		
 		List<String> configurationToRemove = new ArrayList();
 		int n = 0;
 		Iterator<String> iterator = GUI.unfoldedValues.keySet().iterator();
 		while(iterator.hasNext())
 		{
 			String i = iterator.next();
 			n++;
 			Iterator<String> innerIterator = GUI.unfoldedValues.keySet().iterator();
 			for(int k=0;k<n;k++)
 			{
 				innerIterator.next();
 			}
 			while(innerIterator.hasNext())
 			{
 				String j = innerIterator.next();
 				if(!i.equals(j))
 				{
 					List<String> differences = new ArrayList();
 					for(String param: GUI.unfoldedValues.get(i).keySet())
 					{
 						if(!GUI.unfoldedValues.get(i).get(param).toString().equals(GUI.unfoldedValues.get(j).get(param).toString()))
 						{
 							differences.add(param);
 						}
 					}
 					System.out.println("#######differences:"+differences);
 					System.out.println("#######toRemove:"+toRemove);
 					List<String> differencesToRemove = new ArrayList();
 					for(String difference : differences)
 					{
 						if(toRemove.get(i).contains(difference))
 						{
 							differencesToRemove.add(difference);
 						}
 					}
 					for(String difference : differencesToRemove)
 					{
 						differences.remove(difference);
 					}
 					
 					if(differences.size() == 0)
 					{
 						configurationToRemove.add(i);
 					}
 				}
 			}
 		}
 		
 		for(String key : configurationToRemove)
 		{
 			GUI.unfoldedValues.remove(key);
 		}
 		
 		Map<String,Map<String,Object>> newConfiguration = new HashMap();
 		int c=1;
 		for(String key : GUI.unfoldedValues.keySet())
 		{
 			Map<String,Object> config = GUI.unfoldedValues.get(key);
 			config.put("runId", runId+c);
 			newConfiguration.put(runId+c, config);
 			c++;
 		}
 		
 		GUI.unfoldedValues = newConfiguration;
 		
 		GUI.globalResultListener = new GlobalResultListener(GUI.unfoldedValues.keySet(),jumpToCurrentIndex); //jumpTocurrentEvent
 		GUI.runIDKeySet = GUI.unfoldedValues.keySet();
 		
 		/*Debug Print*/ 		
 		System.out.println();
 		System.out.println("############################################ SENT CONFIGURATION ############################################");
 		
 		for(String i : GUI.unfoldedValues.keySet()){
 			System.out.println("************************ Configuration: " + i + " ************************");
 			for(String j : GUI.unfoldedValues.get(i).keySet()){
 				System.out.println(j + ": " + GUI.unfoldedValues.get(i).get(j));
 			}
 		}
 		
 		System.out.println("##############################################################################################################");
 		System.out.println();
 		
 		configurationSender.addRun(GUI.unfoldedValues,GUI.globalResultListener);
	}	
}