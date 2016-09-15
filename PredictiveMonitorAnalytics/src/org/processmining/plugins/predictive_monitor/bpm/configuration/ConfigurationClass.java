package org.processmining.plugins.predictive_monitor.bpm.configuration;

import org.processmining.plugins.predictive_monitor.bpm.classification.enumeration.ClassificationType;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.AgglomerativeFeatureType;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ClusteringType;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ModelClusteringFrom;

public class ConfigurationClass {
	
	// Log files
	public final static String trainingInputLogFile = "./input/BPI2011_80.xes";
	public final static String testingInputLogFile =  "./input/BPI2011_20.xes";
	public final static String prefixInputEventLogFile = "./input/prefix_BPI2011_80.txt";
	
	//Predictor Configurations
	//Trace prefix
	public final static int minPrefixLength = 1;
	public final static int maxPrefixLength = 21;
	public final static int prefixGap = 10;

	//Confidence and support thresholds
	public final static double minConfidenceThreshold = 0.7;
	public final static double minSupportThreshold = 1;
	
	
	//Clustering
	public final static ClusteringType clusteringType = ClusteringType.MODEL;
	public final static AgglomerativeFeatureType agglomerativeFeatureType = AgglomerativeFeatureType.WHOLETRACE;
	public final static ModelClusteringFrom modelClusteringFrom = ModelClusteringFrom.EXTERNAL_INPUT_FILE;
	public final static int clusterNumber = 18;
	public final static int minClusterNumber = 3; 
	public final static int maxClusterNumber = 34;
	
		
	//Model Clustering Parameters
	public final static String toCheckInput ="./input/BPI2011_80_prefixes_gap10.txt";
	//public final static String frequencyTracesFilePath = "./input/BPI2011_80_freqs.txt";
	public final static String frequencyTracesFilePath = "./input/BPI2011_80_trace_freqs.txt";
	public final static String defaultFrequencyTracesFilePath = "./input/BPI2011_80_freqs.txt";
	public final static String clusteredTracesFilePath = "./input/BPI2011_80_mclust_clustering.txt";
	public final static String clusterMeansFilePath = "./input/BPI2011_80_mclust_clusters_mean.txt";	
	public final static String RPath = "C:\\PROGRA~1\\myPrograms\\R\\R-214~1.1\\bin\\";
	public final static String RScriptPath = "./RScript/clustering.R";
	
	
	// Pattern Mining
	//Sequence Pattern Mining
	public final static boolean usePatternFeaturesInClustering = false;
	public final static boolean usePatternFeaturesInClassification = false;
	//Absolute percentage of traces in which the pattern occurs
	public final static double patternMinimumSupport=0.3;
	public final static int minimumPatternLength=1;
	public final static int maximumPatternLength=4;
	public final static double discriminativeMinimumSupport=0.3;
	
	//Classification
	public final static ClassificationType classificationType= ClassificationType.RANDOM_FOREST;

}
