package org.processmining.plugins.predictive_monitor.bpm.server.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.clustering.ClusterController;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ClusteringType;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.HierarchicalDistanceMetrics;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ModelClusteringFrom;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.PatternController;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.enumeration.PatternType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier;

public class ClusteredData extends GenericDataStructure {
	private ArrayList<Pattern> clusteringPatterns;
	private ClusterController clusterController = null;
	private List<XLog> clusterLogs;
	private ClusteringType clusteringType;
	private PatternType clusteringPatternType;
	private Integer clusterTotNumber;
	private HierarchicalDistanceMetrics hierarchicalDistanceMetrics;
	private Boolean useVotingForClustering;
	private String clusterMeansFilePath; //TODO IMPLEMENT
	private Integer voters;
	private final LabelledData labelledData;
	private boolean discriminative;

	public ClusteredData (Semaphore config, Semaphore wait,long id, Map<String,Object> configuration, LabelledData labelledData, String path) throws InterruptedException, IOException
	{
		super(config,id);
		clusteringPatternType = (PatternType)getObject(configuration,"clusteringPatternType");
		clusterTotNumber = (Integer)getObject(configuration,"clusterNumber");
		clusteringType = (ClusteringType) getObject(configuration,"clusteringType");		
		hierarchicalDistanceMetrics = (HierarchicalDistanceMetrics) getObject(configuration,"hierarchicalDistanceMetrics");
		clusterController = new ClusterController();
		useVotingForClustering = (Boolean) getObject(configuration, "useVotingForClustering");
		voters = (Integer) getObject(configuration, "voters");
		useVotingForClustering = useVotingForClustering == null ? new Boolean(false) : useVotingForClustering;
		Double discriminativePatternMinimumSupport = (Double) getObject(configuration,"clusteringDiscriminativePatternMinimumSupport");
		Integer discriminativePatternCount = (Integer) getObject(configuration,"clusteringDiscriminativePatternCount");
		Integer sameLengthDiscriminativePatternCount = (Integer) getObject(configuration,"clusteringSameLengthDiscriminativePatternCount");
		Double patternMinimumSupport = (Double) getObject(configuration,"clusteringPatternMinimumSupport");
		Integer minimumPatternLength = (Integer) getObject(configuration,"clusteringMinimumPatternLength");
		Integer maximumPatternLength = (Integer) getObject(configuration,"clusteringMaximumPatternLength");
		Double discriminativeMinimumSupport = (Double) getObject(configuration,"clusteringDiscriminativeMinimumSupport");
		ModelClusteringFrom modelClusteringFrom = (ModelClusteringFrom) getObject(configuration,"modelClusteringFrom");
		String discriminativePatternFilePath = (String) getObject(configuration,"discriminativePatternFilePath");
		Double epsilon = (Double) getObject(configuration,"dbScanEpsilon");
		Integer minPoints  = (Integer)getObject(configuration,"dbScanMinPoints");
		this.labelledData = labelledData;
		for(String key: labelledData.getTrainingData().getConfiguration().keySet())
		{
			this.configuration.put(key, labelledData.getTrainingData().getConfiguration().get(key));
		}
		if (clusteringPatternType==PatternType.DISCRIMINATIVE || 
				clusteringPatternType==PatternType.DISCR_SEQUENTIAL_WITH_HOLES){
			for(String key: labelledData.getConfiguration().keySet())
			{
				this.configuration.put(key, labelledData.getConfiguration().get(key));
			}	
		}
		path+="/"+id;
		//writeConfiguration(path);
		//System.out.println(this.configuration);
		wait.acquire();
		config.release();

		w.start();
		clusteringPatterns = new ArrayList<Pattern>();
		if(clusteringPatternType!=PatternType.NONE)
		{
			System.out.println("\t\tGenerating patterns "+clusteringPatternType+"...");
			switch (clusteringPatternType) {
			case DISCRIMINATIVE:
				clusteringPatterns = PatternController.generateDiscriminativePatterns(labelledData.getTrainingData().getLog(),labelledData.getClassifier().getClassification(), discriminativePatternMinimumSupport, discriminativePatternCount, sameLengthDiscriminativePatternCount);
				break;
			case SEQUENTIAL_WITHOUT_HOLES:
				clusteringPatterns = PatternController.generateSequentialPatternsWithoutHoles(labelledData.getTrainingData().getLog(), clusterTotNumber, clusterTotNumber);
				break;
			case SEQUENTIAL_WITH_HOLES:
				clusteringPatterns = PatternController.generateSequentialPatternsWithHoles(labelledData.getTrainingData().getLog(), clusterTotNumber, minimumPatternLength, maximumPatternLength);
				break;
			case DISCR_SEQUENTIAL_WITHOUT_HOLES:
				clusteringPatterns = PatternController.generateDiscriminativeSequentialPatternsWithoutHoles(labelledData.getTrainingData().getLog(), clusterTotNumber, clusterTotNumber, labelledData.getClassifier().getClassification(), discriminativeMinimumSupport);
				break;
			case DISCR_SEQUENTIAL_WITH_HOLES:
				clusteringPatterns = PatternController.generateDiscriminativeSequentialPatternsWithHoles(labelledData.getTrainingData().getLog(), clusterTotNumber, clusterTotNumber, clusterTotNumber, labelledData.getClassifier().getClassification(), discriminativeMinimumSupport);
				break;
			default:
				break;
			}
			System.out.println("\t\tDone");
		}
		System.out.println("\t\tGenerating clusters "+clusteringType+"...");
		switch (clusteringType) {
		case AGGLOMERATIVE:
			clusterController.computeHierarchicalClustering(labelledData.getTrainingData().getPrefixLog(), clusterTotNumber, clusteringPatterns, hierarchicalDistanceMetrics, clusteringPatternType);
			break;
		case EM:
			clusterController.computeEMClusteringBasedOnFrequencyEncoding(labelledData.getTrainingData().getPrefixLog(), clusterTotNumber, clusteringPatterns, clusteringPatternType);
			break;

		case KMEANS:
			clusterController.computeKMeansClusteringBasedOnFrequencyEncoding(labelledData.getTrainingData().getPrefixLog(), clusterTotNumber, clusteringPatterns, clusteringPatternType);
			break;
		case KMEANSPLUSPLUS:
			clusterController.computeKMeansPlusPlusClusteringBasedOnFrequencyEncoding(labelledData.getTrainingData().getPrefixLog(), clusterTotNumber, clusteringPatterns, clusteringPatternType);
			break;
		case MODEL:
			clusterController.computeModelClusteringBasedOnEventAndPatternFrequencyEncoding(labelledData.getTrainingData().getPrefixLog(), clusterTotNumber, clusteringPatterns, clusteringPatternType);
			break;
		case DBSCAN:
			clusterController.computeDBScanClustering(labelledData.getTrainingData().getPrefixLog(), epsilon, minPoints);
			break;
		case NONE:
			clusterController.computeNoCluster(labelledData.getTrainingData().getPrefixLog()); 
		default:
			break;
		}
		clusterLogs = clusterController.getXLogClusters();
		System.out.println("\t\t"+clusterLogs.size()+" clusters created");
		//writeToFile(path);
		initTime = w.msecs();
		wait.release();
	}
	
	public ClusteredData (Semaphore config, Semaphore wait,long id, Map<String,Object> configuration, LabelledData labelledData, String path, boolean load) throws InterruptedException, IOException
	{
		super(config, id);
		this.configuration = configuration;
		this.labelledData = labelledData;
		config.release();
		wait.acquire();
		File clusterLogsFile = new File(path+"/"+id+"/clusterLogs.dat");
		InputStream is = new FileInputStream(clusterLogsFile);
		clusterLogs =  (List<XLog>) xstream.fromXML(is);
		wait.release();
	}


	public List<XLog> getClusterLogs() 
	{
		return clusterLogs;
	}
	
	public List<Integer> computeTopClusters(XTrace lastTrace) 
	{
		switch (clusteringType) {
		case AGGLOMERATIVE:
			return clusterController.getAgglomerativeClusterNumber(lastTrace, clusteringPatterns, clusteringPatternType, hierarchicalDistanceMetrics);
		case EM:
			return clusterController.getEMClusterNumber(lastTrace, clusteringPatterns, clusteringPatternType);
		case KMEANS:
			return clusterController.getKMeansClusterNumber(lastTrace, clusteringPatterns, clusteringPatternType);
		case KMEANSPLUSPLUS:
			return clusterController.getKMeansPlusPlusClusterNumber(lastTrace, clusteringPatterns, clusteringPatternType);
		case MODEL:
			return clusterController.getModelerClusterNumber(lastTrace, clusteringPatterns, clusteringPatternType, clusterTotNumber, useVotingForClustering, clusterMeansFilePath);
		case DBSCAN:
			return clusterController.getDBScanClusterNumber(lastTrace, useVotingForClustering, voters);
		default:
			List<Integer> topClusters = new ArrayList<Integer>();
			topClusters.add(new Integer(1));
			return topClusters;
		}
	}
	
	public LabelledData getLabelledData()
	{
		return labelledData;
	}
	
	public boolean isDiscriminative()
	{
		return discriminative;
	}
	
	private void writeToFile(String path) throws FileNotFoundException 
	{
		File clusterLogsFile = new File(path+"/clusterLogs.dat");
		PrintWriter pw = new PrintWriter(clusterLogsFile);

	    pw.print(xstream.toXML(clusterLogs));
	    pw.flush();
		pw.close();
	}
}
