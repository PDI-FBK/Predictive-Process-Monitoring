package org.processmining.plugins.predictive_monitor.bpm.clustering;

import java.util.ArrayList;
import java.util.HashMap;

import weka_predictions.clusterers.EM;
import weka_predictions.core.Attribute;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;

public class EModelClusterer {
	private EM emclusterer = null;

	public HashMap<Integer, ArrayList<Integer>> clusterTraces(Instances encodedTraces, int numClusters){
		emclusterer = new EM();
		HashMap<Integer, ArrayList<Integer>> instanceMap = new HashMap<Integer, ArrayList<Integer>>();
		//hclusterer = new HierarchicalClusterer();
		//FrequencyBasedEncoder encoder = new FrequencyBasedEncoder();
		//emclusterer.setNumClusters(numClusters);
		//emclusterer.setMaximumNumberOfClusters(100);
		try {
			emclusterer.setNumClusters(numClusters);
			emclusterer.buildClusterer(encodedTraces);

			//hclusterer.buildClusterer(encoder.getEncodedTraces());

			ArrayList<Attribute> attributes = new ArrayList<Attribute>();
			for (int i = 0; i<encodedTraces.numAttributes(); i++) {
				attributes.add(encodedTraces.attribute(i));
			}

			// get cluster membership for each instance
			for (int i = 0; i < encodedTraces.numInstances(); i++) {
				int clusterNumber = emclusterer.clusterInstance(encodedTraces.instance(i));
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



	public int clusterTrace(Instance encodedTrace){
		int clusterNumber = -1;
		try {
			clusterNumber = emclusterer.clusterInstance(encodedTrace);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// increasing the cluster Number by 1 because our clusters start from 1
		return clusterNumber;
	}



}
