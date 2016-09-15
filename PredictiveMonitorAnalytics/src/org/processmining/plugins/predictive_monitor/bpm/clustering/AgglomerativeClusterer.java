package org.processmining.plugins.predictive_monitor.bpm.clustering;

import java.util.ArrayList;
import java.util.HashMap;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.guidetreeminer_predictions.GuideTreeMinerInput;
import org.processmining.plugins.guidetreeminer_predictions.MineGuideTree;
import org.processmining.plugins.guidetreeminer_predictions.types.AHCJoinType;
import org.processmining.plugins.guidetreeminer_predictions.types.DistanceMetricType;
import org.processmining.plugins.guidetreeminer_predictions.types.GTMFeature;
import org.processmining.plugins.guidetreeminer_predictions.types.GTMFeatureType;
import org.processmining.plugins.guidetreeminer_predictions.types.LearningAlgorithmType;
import org.processmining.plugins.guidetreeminer_predictions.types.SimilarityDistanceMetricType;
import org.processmining.plugins.guidetreeminer_predictions.types.SimilarityMetricType;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.AgglomerativeFeatureType;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.HierarchicalDistanceMetrics;
import org.processmining.plugins.predictive_monitor.bpm.configuration.ServerConfigurationClass;

import weka_predictions.clusterers.HierarchicalClusterer;
import weka_predictions.core.Attribute;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;

public class AgglomerativeClusterer {
	HierarchicalClusterer hclusterer = null;

	public HashMap<Integer, ArrayList<Integer>> clusterTraces(Instances encodedTraces, int numClusters, HierarchicalDistanceMetrics hierarchicalDistanceMetrics){

		HashMap<Integer, ArrayList<Integer>> instanceMap = new HashMap<Integer, ArrayList<Integer>>();
		hclusterer = new HierarchicalClusterer();
		if (hierarchicalDistanceMetrics.equals(HierarchicalDistanceMetrics.EDIT_DISTANCE))
			hclusterer.setDistanceFunction(new EditDistance());		
	//	FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
		hclusterer.setNumClusters(numClusters);
		try {
			hclusterer.buildClusterer(encodedTraces);

			ArrayList<Attribute> attributes = new ArrayList<Attribute>();
			for (int i = 0; i<encodedTraces.numAttributes(); i++) {
				attributes.add(encodedTraces.attribute(i));
			}

			// get cluster membership for each instance
			for (int i = 0; i < encodedTraces.numInstances(); i++) {
				int clusterNumber = hclusterer.clusterInstance(encodedTraces.instance(i));
				//= new ArrayList<Integer>();
				ArrayList<Integer> instanceIndexes = instanceMap.get(clusterNumber);
				if (instanceIndexes == null){
					//instanceIndexes = new Instances("I"+i, attributes, originalInstances.numInstances());
					instanceIndexes = new ArrayList<Integer>();
				}
				instanceIndexes.add(i);
				instanceMap.put(new Integer(clusterNumber), instanceIndexes);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instanceMap;
	}


	public Object[] clusterTraces(XLog log, int clusterNumber, AgglomerativeFeatureType type){
		GuideTreeMinerInput treeMinerInput = new GuideTreeMinerInput();
		if (type.equals(AgglomerativeFeatureType.ALPHABET))
			initializeFeatureTypeAlphabet(treeMinerInput, clusterNumber);
		else 
			initializeFeatureTypeWholeTrace(treeMinerInput, clusterNumber);
		MineGuideTree guide = new MineGuideTree();
		Object[] patt = guide.mine(treeMinerInput, log);
		return patt;
	}

	public void initializeFeatureTypeAlphabet(GuideTreeMinerInput treeMinerInput, int clusterNumber){
		treeMinerInput.setFeatureType(GTMFeatureType.Alphabet);
		treeMinerInput.setFeature(GTMFeature.IE);
		//input.setFeature(GTMFeature.TR);
		treeMinerInput.addFeature(GTMFeature.IE);
		treeMinerInput.setSimilarityMetricType(SimilarityMetricType.FScore);
		treeMinerInput.setSimilarityDistanceMetricType(SimilarityDistanceMetricType.Similarity);
		//input.addFeature(GTMFeature.TR);
		treeMinerInput.setBaseFeatures(true);
		treeMinerInput.setNumberOfClusters(clusterNumber);
		treeMinerInput.setLearningAlgorithmType(LearningAlgorithmType.AHC);
		treeMinerInput.setAhcJoinType(AHCJoinType.MinVariance);
		treeMinerInput.setIsOutputClusterLogs(true);	
	}

	public void initializeFeatureTypeWholeTrace (GuideTreeMinerInput treeMinerInput, int clusterNumber){
		treeMinerInput.setFeatureType(GTMFeatureType.WholeTrace);
		treeMinerInput.setNumberOfClusters(clusterNumber);
		treeMinerInput.setDistanceMetricType(DistanceMetricType.GenericEditDistance);
		treeMinerInput.setLearningAlgorithmType(LearningAlgorithmType.AHC);
		treeMinerInput.setAhcJoinType(AHCJoinType.MinVariance);
		treeMinerInput.setIsOutputClusterLogs(true);
	}

//	public int clusterTrace(ArrayList<String> encodedTrace, HashMap<Integer, XLog> xLogClusterMap)
//	{
//		HashMap<Integer, XTrace> centroids = new HashMap<Integer, XTrace>();
//
//		int j=1;
//		double mindist = 0;
//		int cl = 0;
//		for(Integer clusterNumber: xLogClusterMap.keySet()){
//			XLog clusterLog = xLogClusterMap.get(clusterNumber);
//			centroids.put(clusterNumber, getCentroid(clusterLog));
//
//			ArrayList<String> paragone = new ArrayList<String>();
//			for (XEvent e : centroids.get(clusterNumber)) {
//				paragone.add(XConceptExtension.instance().extractName(e));
//			}
//			EditDistance edit = new EditDistance(encodedTrace, paragone);
//			double currdist = edit.computeNormalizedSimilarity();
//			if(currdist>mindist){
//				mindist = currdist;
//				cl = j;
//			}
//			j++;
//		}
//		return cl;
//	}

//	private XTrace getCentroid(XLog cluster){
//		//XTrace output = null;
//		double minDist = Double.MAX_VALUE;
//		XTrace minP = null;
//		int position1 = 0;
//		for(XTrace p1 : cluster){
//			double totalDist = 0d;
//			int position2 = 0;
//			for (final XTrace p2 : cluster) {
//				// sum up the distance to all points p2 | p2!=p1
//				if (position1 != position2) {
//					totalDist += this.getDistance(p1, p2);
//				}
//				position2 ++;
//			}
//			// if the current distance is lower that the min, take it as new min
//			if (totalDist < minDist) {
//				minDist = totalDist;
//				minP = p1;
//			}
//			position1 ++;
//		}
//		return minP;
//	}

//	private double getDistance(XTrace t1, XTrace t2){
//		double out = 0;
//		ArrayList<String> encode1 = new ArrayList<String>();
//		for(XEvent e1 : t1){
//			encode1.add(XConceptExtension.instance().extractName(e1));
//		}
//		ArrayList<String> encode2 = new ArrayList<String>();
//		for(XEvent e2 : t2){
//			encode1.add(XConceptExtension.instance().extractName(e2));
//		}
//		EditDistance edit = new EditDistance(encode1, encode2);
//
//		out = edit.computeNormalizedEditDistance();
//		return out;
//	}
	


	public int clusterTrace(Instance encodedTrace){
		int clusterNumber = -1;
		try {
			clusterNumber = hclusterer.clusterInstance(encodedTrace);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clusterNumber;
	}



}
