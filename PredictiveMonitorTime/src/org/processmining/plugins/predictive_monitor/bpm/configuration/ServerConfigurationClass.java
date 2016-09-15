
package org.processmining.plugins.predictive_monitor.bpm.configuration;


import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.AgglomerativeFeatureType;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ClusteringType;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ConfidenceAndSupportVotingStrategy;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.HierarchicalDistanceMetrics;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ModelClusteringFrom;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.enumeration.PatternType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.TimeClassifier.PartitionMethod;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier.*;
import org.processmining.plugins.predictive_monitor.bpm.classification.enumeration.*;

public class ServerConfigurationClass {
	
	//Predictor Configurations
	//Trace prefix
	public final static int minPrefixLength = 1;
	public final static int maxPrefixLength = 11;
	public final static int prefixGap = 5;
	
	//LogConfiguration
	public final static String logFilePath = "./log/log.txt";

	// Cross-validation
	public final static int crossValidationParts = 5;
	public final static Integer partNumber = 1;
	//public final static String logPart = "_part"+partNumber;
	public final static String logPart = "";
	
	// Pattern Mining
	//Sequence Pattern Mining Configuratinos
	public final static PatternType clusteringPatternType = PatternType.NONE; 
	
	//Absolute percentage of traces in which the pattern occurs
	public final static double patternMinimumSupport=0.8;
	public final static int minimumPatternLength=1;
	public final static int maximumPatternLength=4;
	public final static double discriminativeMinimumSupport=0.8;
	// Discriminative pattern configurations
	public final static double discriminativePatternMinimumSupport = 0.1;
	public final static int discriminativePatternCount = 400;
	public final static int sameLengthDiscriminativePatternCount = 399;
	public final static String discriminativePatternFilePath = "./input/discriminative_patterns_k" + discriminativePatternCount + "_kprim" + sameLengthDiscriminativePatternCount + "_supp" + (int) (1000 * discriminativePatternMinimumSupport) + "_gap" + prefixGap + "_max" + maxPrefixLength + logPart + ".txt";
	
	// Log files
	public final static String trainingInputLogFile = logPart.equals("") ? "./input/BPI2011_80.xes" : "./input/BPI2011_80_train" + logPart + ".xes";
	public final static String prefixInputEventLogFile = "./input/prefix_gap" + prefixGap + "_max" + maxPrefixLength + "_BPI2011_80" + logPart + ".txt";
	
	public final static boolean generateArffReport = true;
	public final static boolean printDebug = false;
	public final static boolean printLog = false;
	
	//Clustering
	public final static HierarchicalDistanceMetrics hierarchicalDistanceMetrics = HierarchicalDistanceMetrics.EDIT_DISTANCE;
	public final static AgglomerativeFeatureType agglomerativeFeatureType = AgglomerativeFeatureType.WHOLETRACE;
	public final static ModelClusteringFrom modelClusteringFrom = ModelClusteringFrom.EXTERNAL_INPUT_FILE; 
	public final static int clusterNumber = 15; 
	//public final static int minClusterNumber = 3; 
//	public final static int maxClusterNumber = 34;

	//DBSCAN clustering
	public final static double DBscanEpsilon = 0.17;
	public final static int DBScanMinPoints = 5;
	public final static int maxSizeTraceThreshold = 100;
		
	//Model Clustering Parameters
	//public final static String toCheckInput ="./input/BPI2011_80_prefixes_gap10.txt";
	public final static String frequencyTracesFilePath = "./input/BPI2011_80_gap" + prefixGap + "_max" + maxPrefixLength + logPart + "_freqs.txt";
	//public final static String frequencyTracesFilePath = "./input/BPI2011_80_gap" + prefixGap + "_max" + maxPrefixLength + "_k" + discriminativePatternCount + "_kprim" + sameLengthDiscriminativePatternCount + "_supp" + (int) (1000 * discriminativePatternMinimumSupport) + "_freqs" + logPart + ".txt";
	public final static String defaultFrequencyTracesFilePath = "./input/BPI2011_80_freqs.txt";
	//public final static String clusteredTracesFilePath = "./input/BPI2011_80_mclust_clustering_k30_kprim29_supp100_gap10_max21_combined.txt";
	//public final static String clusterMeansFilePath = "./input/BPI2011_80_mclust_clusters_mean_k30_kprim29_supp100_gap10_max21_combined.txt";	

	//public final static String clusteredTracesFilePath = "./input/BPI2011_80_mclust_clustering_k" + discriminativePatternCount + "_kprim" + sameLengthDiscriminativePatternCount + "_supp" + (int) (1000 * discriminativePatternMinimumSupport) + "_gap" + prefixGap + "_max" + maxPrefixLength + logPart + ".txt";
	//public final static String clusterMeansFilePath = "./input/BPI2011_80_mclust_clusters_mean_k" + discriminativePatternCount + "_kprim" + sameLengthDiscriminativePatternCount + "_supp" + (int) (1000 * discriminativePatternMinimumSupport) + "_gap" + prefixGap + "_max" + maxPrefixLength + logPart + ".txt";	
	public final static String clusteredTracesFilePath = "./input/BPI2011_80_mclust_gap" + prefixGap + "_max" + maxPrefixLength + logPart + "_clusters_";
	public final static String clusterMeansFilePath = "./input/BPI2011_80_mclust_gap" + prefixGap + "_max" + maxPrefixLength + logPart + "_clusters_mean_";

	
	
	//fixed
	public final static String RPath = "C:\\PROGRA~1\\myPrograms\\R\\R-214~1.1\\bin\\";
	public final static String RScriptPath = "./RScript/clustering.R";
	
		
	//Classification
		public final static ClassificationType classificationType= ClassificationType.RANDOM_FOREST;
		
		public final static int rFMaxDepth = 0;
		public final static int rFNumFeatures = 0;
		public final static int rFNumTrees = 10;
		public final static int rFSeed = 1;

	
	
	//
		//Voting
		 public static boolean useVotingForClustering = false;
		 public static int voters = 3;
		 public static ConfidenceAndSupportVotingStrategy confidenceAndSupportVotingStrategy = ConfidenceAndSupportVotingStrategy.MAX;

		//Prediction Type
		public final static PredictionType predictionType = PredictionType.FORMULA_SATISFACTION;
		
		//TimeClassifier
		public final static PartitionMethod partitionMethod = PartitionMethod.FIXED_SORTED_DIVISION;
		public final static int numberOfIntervals = 3;
		public final static long division[]={86400000l,864000000l,2592000000l,31536000000l,315360000000l}; //1 day, 10 days, 1 month, 1 year, 10 years

}
