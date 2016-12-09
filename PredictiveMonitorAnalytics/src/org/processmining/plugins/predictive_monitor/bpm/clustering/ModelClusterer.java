package org.processmining.plugins.predictive_monitor.bpm.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Tuple;

import weka_predictions.clusterers.EM;
import weka_predictions.clusterers.SimpleKMeans;
import weka_predictions.core.Attribute;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;

public class ModelClusterer {
	private ModelBased mbclusterer = null;

	public HashMap<Integer, ArrayList<Integer>> clusterTraces(Instances encodedTraces, int numClusters) {
		
		HashMap<Integer, ArrayList<Integer>> instanceMap = new HashMap<Integer, ArrayList<Integer>>();

		// create the model
		mbclusterer = new ModelBased();
		try {
			mbclusterer.setNumClusters(numClusters);
			mbclusterer.buildClusterer(encodedTraces);

			for (int i = 0; i < numClusters; i++) {
				ArrayList<Integer> instanceIndexes = instanceMap.get(i);
				if (instanceIndexes == null){
					instanceIndexes = new ArrayList<Integer>();
				}
				for (int j = 0; j < mbclusterer.dataCluster.length; j++) {
					int clusterNumber = mbclusterer.dataCluster[j];
					if(clusterNumber == i){
						instanceIndexes.add(j);
					}
				}
				//remove empty clusters from choices for classifiers
				if(instanceIndexes.size() > 0){
					instanceMap.put(new Integer(i), instanceIndexes);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instanceMap;
	}

	public int clusterTrace(Instance encodedTrace){
		int clusterNumber = -1;
		try {
			clusterNumber = mbclusterer.clusterInstance(encodedTrace);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clusterNumber;
	}

}
