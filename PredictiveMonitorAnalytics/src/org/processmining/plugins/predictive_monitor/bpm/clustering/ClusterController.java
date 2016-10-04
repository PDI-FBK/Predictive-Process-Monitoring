package org.processmining.plugins.predictive_monitor.bpm.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.clustering.data_structures.Point;
import org.processmining.plugins.predictive_monitor.bpm.clustering.data_structures.TracePoint;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.AgglomerativeFeatureType;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.HierarchicalDistanceMetrics;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ModelClusteringFrom;
import org.processmining.plugins.predictive_monitor.bpm.configuration.ServerConfigurationClass;
import org.processmining.plugins.predictive_monitor.bpm.encoding.Decoder;
import org.processmining.plugins.predictive_monitor.bpm.encoding.EditDistanceBasedEncoder;
import org.processmining.plugins.predictive_monitor.bpm.encoding.FrequencyBasedEncoder;
import org.processmining.plugins.predictive_monitor.bpm.encoding.SequenceBasedEncoder;
import org.processmining.plugins.predictive_monitor.bpm.encoding.TraceToPointEncoder;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.PatternController;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.enumeration.PatternType;
import org.processmining.plugins.predictive_monitor.bpm.utility.Print;

import weka_predictions.core.Attribute;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;

public class ClusterController {
	private HashMap<Integer, XTrace> traceMapping = null;
	private HashMap<Integer,XLog> xLogClusterMap = null;
	private Map<String, Integer> alphabetMap = new HashMap<String, Integer>();
	private int maxSize = 0;
	private ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	private Instances traceInstances = null;
	
	private KMeansClusterer kMeans = null; 
	private DBScan dbscan = null; 
	private AgglomerativeClusterer hclusterer = null;
	private EModelClusterer emclusterer = null;
	private KMeansPlusPlusClusterer kMeansPlusPlus = null; 
	private ModelClusterer modelClusterer = null; 
	
	private Print print = new Print();
	
	public void computeKMeansClusteringBasedOnFrequencyEncoding (XLog trainingLog, int numClusters, ArrayList<Pattern> clusteringPatterns,PatternType clusteringPatternType){
		FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
		kMeans = new KMeansClusterer();
		
		switch (clusteringPatternType) {
		case DISCRIMINATIVE:
		case SEQUENTIAL_WITHOUT_HOLES:
		case SEQUENTIAL_WITH_HOLES:
		case DISCR_SEQUENTIAL_WITHOUT_HOLES:
		case DISCR_SEQUENTIAL_WITH_HOLES:
			encoder.encodeTracesBasedOnEventAndPatternFrequency(trainingLog, clusteringPatterns);
			break;
		case NONE:
		default:
			encoder.encodeTraces(trainingLog);
			break;
		}
		Instances encodedTraces = encoder.getEncodedTraces();
		//System.out.println(encodedTraces.toString());
		traceMapping = encoder.getTraceMapping();
		alphabetMap  = encoder.getAlphabetMap();
		HashMap<Integer, ArrayList<Integer>> clusterMap = kMeans.clusterTraces(encodedTraces, numClusters);
		xLogClusterMap = Decoder.computeXLogClusterMap(clusterMap, traceMapping);
		
	}
	
	public void computeHierarchicalClustering (XLog trainingLog, int numClusters, ArrayList<Pattern> clusteringPatterns, HierarchicalDistanceMetrics hierarchicalDistanceMetrics, PatternType clusteringPatternType){
		hclusterer = new AgglomerativeClusterer();
		
		switch (hierarchicalDistanceMetrics) {
		case EUCLIDEAN:
			FrequencyBasedEncoder fbEncoder = new FrequencyBasedEncoder();
			switch (clusteringPatternType) {
			case DISCRIMINATIVE:
			case SEQUENTIAL_WITHOUT_HOLES:
			case SEQUENTIAL_WITH_HOLES:
			case DISCR_SEQUENTIAL_WITHOUT_HOLES:
			case DISCR_SEQUENTIAL_WITH_HOLES:
				fbEncoder.encodeTracesBasedOnEventAndPatternFrequency(trainingLog, clusteringPatterns);
				break;
			case NONE:
			default:
				fbEncoder.encodeTraces(trainingLog);
				break;
			}
			traceInstances = fbEncoder.getEncodedTraces();
			alphabetMap   = fbEncoder.getAlphabetMap();
			traceMapping = fbEncoder.getTraceMapping();
			break;
		case EDIT_DISTANCE:
			SequenceBasedEncoder sEncoder = new SequenceBasedEncoder();
			sEncoder.encodeTraces(trainingLog);
			traceInstances = sEncoder.getEncodedTraces();
			maxSize = sEncoder.getMaxSize();
			traceMapping = sEncoder.getTraceMapping();
			attributes = sEncoder.getAttributes();
			break;
		}
		HashMap<Integer, ArrayList<Integer>> clusterMap = hclusterer.clusterTraces(traceInstances, numClusters, hierarchicalDistanceMetrics);
		xLogClusterMap = Decoder.computeXLogClusterMap(clusterMap, traceMapping);
		
	}
	
/*	public void computeHierarchicalClusteringBasedOnEventAndPatternFrequencyEncoding (XLog trainingLog, int numClusters, ArrayList<Pattern> patterns){
		hclusterer = new AgglomerativeClusterer();
		FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
		encoder.encodeTraces(trainingLog);
		Instances encodedTraces = encoder.getEncodedTraces();
		//System.out.println(encodedTraces.toString());
		traceMapping = encoder.getTraceMapping();
		alphabetMap   = encoder.getAlphabetMap();
		HashMap<Integer, ArrayList<Integer>> clusterMap = hclusterer.clusterTraces(encodedTraces, numClusters);
		xLogClusterMap = Decoder.computeXLogClusterMap(clusterMap, traceMapping);
		
	}*/

	
	public void computeEMClusteringBasedOnFrequencyEncoding (XLog trainingLog, int numClusters, ArrayList<Pattern> clusteringPatterns, PatternType clusteringPatternType){
		emclusterer = new EModelClusterer();
		FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
		switch (clusteringPatternType) {
		case DISCRIMINATIVE:
		case SEQUENTIAL_WITHOUT_HOLES:
		case SEQUENTIAL_WITH_HOLES:
		case DISCR_SEQUENTIAL_WITHOUT_HOLES:
		case DISCR_SEQUENTIAL_WITH_HOLES:
			encoder.encodeTracesBasedOnEventAndPatternFrequency(trainingLog, clusteringPatterns);
			break;
		case NONE:
		default:
			encoder.encodeTraces(trainingLog);
			break;
		}
		Instances encodedTraces = encoder.getEncodedTraces();
		//System.out.println(encodedTraces.toString());
		traceMapping = encoder.getTraceMapping();
		alphabetMap   = encoder.getAlphabetMap();
		HashMap<Integer, ArrayList<Integer>> clusterMap = emclusterer.clusterTraces(encodedTraces, numClusters);
		xLogClusterMap = Decoder.computeXLogClusterMap(clusterMap, traceMapping);
		
	}
	
	public void computeModelClusteringBasedOnFrequencyEncoding (XLog trainingLog, ModelClusteringFrom inputFrom, int clusterNumber, String frequencyTracesFilePath, String RPath, String RScriptPath, String clusteredTracesFilePath){
		FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
		modelClusterer = new ModelClusterer();
		
		encoder.encodeTraces(trainingLog);
		
		Instances encodedTraces = encoder.getEncodedTraces();
		//System.out.println(encodedTraces.toString());
		traceMapping = encoder.getTraceMapping();
		alphabetMap  = encoder.getAlphabetMap();
		HashMap<Integer, ArrayList<Integer>> clusterMap = modelClusterer.clusterTraces(encodedTraces, clusterNumber);
		xLogClusterMap = Decoder.computeXLogClusterMap(clusterMap, traceMapping);
		
	}
	
	public void computeModelClusteringBasedOnEventAndPatternFrequencyEncoding (XLog trainingLog, int numClusters, ArrayList<Pattern> clusteringPatterns,PatternType clusteringPatternType){
		FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
		modelClusterer = new ModelClusterer();
		
		switch (clusteringPatternType) {
		case DISCRIMINATIVE:
			encoder.encodeTracesBasedOnEventAndPatternFrequency(trainingLog, clusteringPatterns);
			break;
		case NONE:
		default:
			encoder.encodeTraces(trainingLog);
			break;
		}
		Instances encodedTraces = encoder.getEncodedTraces();
		//System.out.println(encodedTraces.toString());
		traceMapping = encoder.getTraceMapping();
		alphabetMap  = encoder.getAlphabetMap();
		HashMap<Integer, ArrayList<Integer>> clusterMap = modelClusterer.clusterTraces(encodedTraces, numClusters);
		xLogClusterMap = Decoder.computeXLogClusterMap(clusterMap, traceMapping);
		
	}
	

	public void computeAgglomerativeClusteringBasedOnEditDistanceEncoding (XLog trainingLog, int numClusters, AgglomerativeFeatureType type){
		EditDistanceBasedEncoder encoder = new EditDistanceBasedEncoder();
		AgglomerativeClusterer agglomerativeClusterer= new AgglomerativeClusterer();
		
		encoder.encodeTraces(trainingLog);
		XLog log = encoder.getLog();

		Object[] patt = agglomerativeClusterer.clusterTraces(trainingLog, numClusters, type);
		xLogClusterMap = Decoder.computeXLogClusterMap(patt);
		
	}
	
	public void computeDBScanClustering(XLog logTracesToEncode, double epsilon, int minPoints){
		TraceToPointEncoder ttpe = new TraceToPointEncoder();
		ArrayList<Point> dataset = ttpe.transformIntoTracePoints(logTracesToEncode);
		traceMapping = ttpe.getTraceMapping();
		dbscan = new DBScan(dataset, epsilon, minPoints);
		HashMap<Integer, ArrayList<Integer>> clusterMap = dbscan.computeClusters();
		//MapPrinter.printMap(clusterMap, clusterMap.size(), dbscan.countNoisy(), "./log/clusterStructureLog.txt");
		for (Point point : dbscan.getDataset()) {
			TracePoint tracePoint = (TracePoint) point;
			ArrayList<String> trace = tracePoint.getCurrentTrace();
			int cluster = dbscan.getCluster(trace);	
			int ind = dataset.indexOf(point);
			int cluster2 = -1;
			for (Integer key : clusterMap.keySet()) {
				if(clusterMap.get(key).contains(ind))
					cluster2 = key;
			}
			//System.out.println(ind+": ORIGINAL "+cluster2+" "+point.getCluster()+" PREDICTED "+cluster);
			if (point.getCluster()!=cluster){
				print.thatln("***************ERROR****************************************************");
				print.thatln(ind+": ORIGINAL "+cluster2+" "+point.getCluster()+" PREDICTED "+cluster);
			}
		}	
		print.thatln("NUMBER OF CLUSTERS "+clusterMap.size());
		print.thatln("NUMBER OF NOISY STRINGS "+dbscan.countNoisy());
		xLogClusterMap = Decoder.computeXLogClusterMap(clusterMap, traceMapping);

	}
	
	public void computeKMeansPlusPlusClusteringBasedOnFrequencyEncoding (XLog trainingLog, int numClusters, ArrayList<Pattern> clusteringPatterns,PatternType clusteringPatternType){
		FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
		kMeansPlusPlus = new KMeansPlusPlusClusterer();
		
		switch (clusteringPatternType) {
		case DISCRIMINATIVE:
		case SEQUENTIAL_WITHOUT_HOLES:
		case SEQUENTIAL_WITH_HOLES:
		case DISCR_SEQUENTIAL_WITHOUT_HOLES:
		case DISCR_SEQUENTIAL_WITH_HOLES:
			encoder.encodeTracesBasedOnEventAndPatternFrequency(trainingLog, clusteringPatterns);
			break;
		case NONE:
		default:
			encoder.encodeTraces(trainingLog);
			break;
		}
		Instances encodedTraces = encoder.getEncodedTraces();
		//System.out.println(encodedTraces.toString());
		traceMapping = encoder.getTraceMapping();
		alphabetMap  = encoder.getAlphabetMap();
		HashMap<Integer, ArrayList<Integer>> clusterMap = kMeansPlusPlus.clusterTraces(encodedTraces, numClusters);
		xLogClusterMap = Decoder.computeXLogClusterMap(clusterMap, traceMapping);
		
	}
	
	public void computeNoCluster(XLog trainingLog){
		xLogClusterMap = new HashMap<Integer, XLog>();
		xLogClusterMap.put(1, trainingLog);
	}
	
	public ArrayList<Integer> getKMeansClusterNumber(XTrace trace, ArrayList<Pattern> patterns, PatternType clusteringPatternType){
		ArrayList<Integer> topClusters = new ArrayList<Integer>();
		FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
		Instance encodedTrace = null;
		switch (clusteringPatternType) {
		case DISCRIMINATIVE:
		case SEQUENTIAL_WITHOUT_HOLES:
		case SEQUENTIAL_WITH_HOLES:
		case DISCR_SEQUENTIAL_WITHOUT_HOLES:
		case DISCR_SEQUENTIAL_WITH_HOLES:
			encodedTrace = encoder.encodeTraceBasedOnEventAndPatternFrequency(trace, patterns, alphabetMap);
		break; 
		case NONE:
		default:
			encodedTrace = encoder.encodeTrace(trace, alphabetMap);
		break;
		}
		//KMeansClusterer kMeans = new KMeansClusterer();
		// increasing the cluster Number by 1 because our clusters start from 1
		int clusterNumber = kMeans.clusterTrace(encodedTrace, xLogClusterMap)+1;
		topClusters.add(clusterNumber);
		return topClusters;
	}
	
	public ArrayList<Integer> getKMeansPlusPlusClusterNumber(XTrace trace, ArrayList<Pattern> patterns, PatternType clusteringPatternType){
		ArrayList<Integer> topClusters = new ArrayList<Integer>();
		FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
		Instance encodedTrace = null;
		switch (clusteringPatternType) {
		case DISCRIMINATIVE:
		case SEQUENTIAL_WITHOUT_HOLES:
		case SEQUENTIAL_WITH_HOLES:
		case DISCR_SEQUENTIAL_WITHOUT_HOLES:
		case DISCR_SEQUENTIAL_WITH_HOLES:
			encodedTrace = encoder.encodeTraceBasedOnEventAndPatternFrequency(trace, patterns, alphabetMap);
		break; 
		case NONE:
		default:
			encodedTrace = encoder.encodeTrace(trace, alphabetMap);
		break;
		}
		//KMeansClusterer kMeans = new KMeansClusterer();
		// increasing the cluster Number by 1 because our clusters start from 1
		int clusterNumber = kMeansPlusPlus.clusterTrace(encodedTrace, xLogClusterMap)+1;
		topClusters.add(clusterNumber);
		return topClusters;
	}
	
	public ArrayList<Integer> getModelerClusterNumber(XTrace trace, List<Pattern> patterns, PatternType clusteringPatternType, int numberOfClusters, boolean useVotingForClustering, String clusterMeansFilePath ){
		ArrayList<Integer> topClusters = new ArrayList<Integer>();
		FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
		Instance encodedTrace = encoder.encodeTrace(trace, alphabetMap);

		int clusterNumber = modelClusterer.clusterTrace(encodedTrace);
		topClusters.add(clusterNumber);
		return topClusters;
	}
	
	public ArrayList<Integer> getAgglomerativeClusterNumber(XTrace trace, ArrayList<Pattern> patterns, PatternType clusteringPatternType, HierarchicalDistanceMetrics hierarchicalDistanceMetrics){
		ArrayList<Integer> topClusters = new ArrayList<Integer>();
		Instance encodedTrace = null;
		switch (hierarchicalDistanceMetrics) {
		case EUCLIDEAN:
			FrequencyBasedEncoder fbEncoder = new FrequencyBasedEncoder();
			switch (clusteringPatternType) {
			case DISCRIMINATIVE:
			case SEQUENTIAL_WITHOUT_HOLES:
			case SEQUENTIAL_WITH_HOLES:
			case DISCR_SEQUENTIAL_WITHOUT_HOLES:
			case DISCR_SEQUENTIAL_WITH_HOLES:
				encodedTrace = fbEncoder.encodeTraceBasedOnEventAndPatternFrequency(trace, patterns, alphabetMap);
			break; 
			case NONE:
			default:
				encodedTrace = fbEncoder.encodeTrace(trace, alphabetMap);
			break;
			}
			break;
		case EDIT_DISTANCE:
			SequenceBasedEncoder sEncoder = new SequenceBasedEncoder();
			encodedTrace = sEncoder.encodeTrace(trace, maxSize, attributes, traceInstances);
			break;
		}
		//AgglomerativeClusterer agglomerativeClusterer = new AgglomerativeClusterer();
		// increasing the cluster Number by 1 because our clusters start from 1
		int clusterNumber = hclusterer.clusterTrace(encodedTrace)+1;
		topClusters.add(clusterNumber);
		return topClusters;
	}
	
	
	public ArrayList<Integer> getEMClusterNumber(XTrace trace, ArrayList<Pattern> patterns, PatternType clusteringPatternType){
		ArrayList<Integer> topClusters = new ArrayList<Integer>();
		FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
		Instance encodedTrace = encoder.encodeTrace(trace, alphabetMap);
		switch (clusteringPatternType) {
		case DISCRIMINATIVE:
		case SEQUENTIAL_WITHOUT_HOLES:
		case SEQUENTIAL_WITH_HOLES:
		case DISCR_SEQUENTIAL_WITHOUT_HOLES:
		case DISCR_SEQUENTIAL_WITH_HOLES:
			encodedTrace = encoder.encodeTraceBasedOnEventAndPatternFrequency(trace, patterns, alphabetMap);
		break; 
		case NONE:
		default:
			encodedTrace = encoder.encodeTrace(trace, alphabetMap);
		break;
		}		
		//EModelClusterer emClusterer = new EModelClusterer();
		// increasing the cluster Number by 1 because our clusters start from 1
		int clusterNumber = emclusterer.clusterTrace(encodedTrace)+1;
		topClusters.add(clusterNumber);
		return topClusters;
	}
	
	public ArrayList<XLog> getXLogClusters(){
		ArrayList<XLog> xLogClusters = new ArrayList<XLog>();
		for (Integer key : xLogClusterMap.keySet()) {
			xLogClusters.add(xLogClusterMap.get(key));
		}
		return xLogClusters;
	}
	
	public ArrayList<Integer> getDBScanClusterNumber(XTrace trace, boolean useVotingForClustering, int voters){
		ArrayList<Integer> topClusters = new ArrayList<Integer>();
		TracePoint tracePoint = (TracePoint) TraceToPointEncoder.computePoint(trace);

		if (useVotingForClustering) {
			topClusters = dbscan.getTopClusters(tracePoint.getCurrentTrace(),
					voters);
		} else {
			int clusterNumber = -1;
			clusterNumber = dbscan.getCluster(tracePoint.getCurrentTrace());
			// System.out.println("INSTANCE************************: "+encodedTrace.toString()+" "+clusterNumber);
			topClusters.add(clusterNumber);
		}
		
		for (int i = 0; i < topClusters.size(); i++) {
			print.thatln("INSTANCE " + tracePoint.getCurrentTrace().toString() + " CLUSTER "+ topClusters.get(i));
		}
			
		return topClusters;
	}

}
