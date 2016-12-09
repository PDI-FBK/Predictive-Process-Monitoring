package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.processmining.plugins.predictive_monitor.bpm.classification.enumeration.ClassificationType;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ClusteringType;
import org.processmining.plugins.predictive_monitor.bpm.configuration.ClientConfigurationClass;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.enumeration.PatternType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier.PredictionType;

public class Dependencies {
	private static Map<String,Map<String,List<Object>>> dependencies;
	private static void init()
	{
		dependencies = new HashMap<>();
		
		add("rFMaxDepth","classificationType", ClassificationType.RANDOM_FOREST);
		add("rFNumFeatures","classificationType", ClassificationType.RANDOM_FOREST);
		add("rFNumTrees","classificationType", ClassificationType.RANDOM_FOREST);
		add("rFSeed","classificationType", ClassificationType.RANDOM_FOREST);
		
		
		add("clusteringDiscriminativePatternMinumSupport","clusteringPatternType", PatternType.DISCRIMINATIVE);
		add("clusteringDiscriminativePatternCount","clusteringPatternType", PatternType.DISCRIMINATIVE);
		add("clusteringSameLengthDiscriminativePatternCount","clusteringPatternType", PatternType.DISCRIMINATIVE);
		
//		add("clusteringPatternMinimumSupport","clusteringPatternType", PatternType.SEQUENTIAL_WITH_HOLES);
//		add("clusteringMinimumPatternLength","clusteringPatternType", PatternType.SEQUENTIAL_WITH_HOLES);
//		add("clusteringDiscriminativeMinimumSupport","clusteringPatternType", PatternType.SEQUENTIAL_WITH_HOLES);
		
//		add("clusteringPatternMinimumSupport","clusteringPatternType", PatternType.SEQUENTIAL_WITHOUT_HOLES);
//		add("clusteringMinimumPatternLength","clusteringPatternType", PatternType.SEQUENTIAL_WITHOUT_HOLES);
//		add("clusteringDiscriminativeMinimumSupport","clusteringPatternType", PatternType.SEQUENTIAL_WITHOUT_HOLES);
		
//		add("clusteringPatternMinimumSupport","clusteringPatternType", PatternType.DISCR_SEQUENTIAL_WITHOUT_HOLES);
//		add("clusteringMinimumPatternLength","clusteringPatternType", PatternType.DISCR_SEQUENTIAL_WITHOUT_HOLES);
//		add("clusteringDiscriminativePatternMinimumSupport","clusteringPatternType", PatternType.DISCR_SEQUENTIAL_WITHOUT_HOLES);
		
//		add("clusteringPatternMinimumSupport","clusteringPatternType", PatternType.DISCR_SEQUENTIAL_WITH_HOLES);
//		add("clusteringMinimumPatternLength","clusteringPatternType", PatternType.DISCR_SEQUENTIAL_WITH_HOLES);
//		add("clusteringMaximumPatternLength","clusteringPatternType", PatternType.DISCR_SEQUENTIAL_WITH_HOLES);
//		add("clusteringDiscriminativePatternMinimumSupport","clusteringPatternType", PatternType.DISCR_SEQUENTIAL_WITH_HOLES);
		
		
		add("confidenceAndSupportVotingStrategy","useVotingForClustering", new Boolean(true));
		add("voters","useVotingForClustering", new Boolean(true));
		
		Map<String,PredictionType> values = new HashMap<>();
		values.put("satisfied",PredictionType.FORMULA_SATISFACTION);
		values.put("time",PredictionType.FORMULA_SATISFACTION_TIME);
		add("partitionMethod","predictionType",values);
		add("numberOfIntervals","predictionType",values);
		add("timeFromLastEvent","predictionType",values);
		
		values = new HashMap<>();
		values.put("time",PredictionType.FORMULA_SATISFACTION_TIME);
		add("partitionMethod","predictionType",values);
		add("numberOfIntervals","predictionType",values);
		add("timeFromLastEvent","predictionType",values);
	
		
		add("dbScanEpsilon", "clusteringType",ClusteringType.DBSCAN);
		add("dbScanMinPoints", "clusteringType",ClusteringType.DBSCAN);
		add("useVotingForClustering","clusteringType",ClusteringType.DBSCAN);
		
		add("clusterNumber","clusteringType",ClusteringType.KMEANS);
		add("useVotingForClustering","clusteringType",ClusteringType.KMEANS);
		add("clusterNumber","clusteringType",ClusteringType.AGGLOMERATIVE);
		add("useVotingForClustering","clusteringType",ClusteringType.AGGLOMERATIVE);
		
//		add("clusterNumber","clusteringType",ClusteringType.KMEANSPLUSPLUS);
//		add("useVotingForClustering","clusteringType",ClusteringType.KMEANSPLUSPLUS);
		
		add("clusterNumber","clusteringType",ClusteringType.MODEL);
		
		//TODO add dbscan e kmeans dependencies...
	}
	
	private static void add(String parameter, String father, Object value)
	{
		if(dependencies.get(parameter)==null)
		{
			Map<String,List<Object>> map;
			map = new HashMap<>();
			List<Object> list = new ArrayList();
			list.add(value);
			map.put(father, list);
			dependencies.put(parameter,map);
		}
		else
		{
			if(dependencies.get(parameter).get(father) == null)
			{
				List<Object> list = new ArrayList();
				list.add(value);
				dependencies.get(parameter).put(father, list);
			}
			else
			{
				dependencies.get(parameter).get(father).add(value);
			}
		}
	}
	
	public static boolean isDependencySatisfied(String parameter, Map<String,Object> configuration)
	{
		if(dependencies == null) init();
		if(dependencies.containsKey(parameter))
		{
			for(String depedency : dependencies.get(parameter).keySet())
			{
				System.out.print(parameter+": ");
				for(Object value: dependencies.get(parameter).get(depedency))
				{
					if(isCompatible(value,configuration.get(depedency))){
						return true;
					}
				}
			}
			return false;
		}
		return true;
	}
	
	private static boolean isCompatible(Object a, Object b)
	{
		if(a.toString().equals(b.toString()))
			System.out.println(a.toString()+" = "+b.toString());
		else
			System.out.println(a.toString()+" != "+b.toString());
		return a.toString().equals(b.toString());
	}
}
