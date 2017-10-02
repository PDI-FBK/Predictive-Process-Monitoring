package org.processmining.plugins.predictive_monitor.bpm.clustering;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.predictive_monitor.bpm.configuration.ServerConfigurationClass;

import weka_predictions.clusterers.HierarchicalClusterer_predictions;
import weka_predictions.clusterers.SimpleKMeans;
import weka_predictions.core.Attribute;
import weka_predictions.core.DistanceFunction;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;
import weka_predictions.core.SelectedTag;
import weka_predictions.core.converters.CSVLoader;
import weka_predictions.core.neighboursearch.PerformanceStats;

public class KMeansPlusPlusClusterer {
   
	ArrayList<Instance> instanceAL = new ArrayList<Instance>();
	private SimpleKMeans kMeans = null;


	public void cluster(String filePath){

		try {

			 // load CSV
		    CSVLoader loader = new CSVLoader();
			//ArffLoader loader = new ArffLoader();
		    loader.setSource(new File(filePath));
		    Instances data = loader.getDataSet();

			HierarchicalClusterer_predictions h = new HierarchicalClusterer_predictions();
			DistanceFunction d = new DistanceFunction() {

				@Override
				public void setOptions(String[] options) throws Exception {
					// TODO Auto-generated method stub

				}

				@Override
				public Enumeration listOptions() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String[] getOptions() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void update(Instance ins) {
					// TODO Auto-generated method stub

				}

				@Override
				public void setInvertSelection(boolean value) {
					// TODO Auto-generated method stub

				}

				@Override
				public void setInstances(Instances insts) {
					// TODO Auto-generated method stub

				}

				@Override
				public void setAttributeIndices(String value) {
					// TODO Auto-generated method stub

				}

				@Override
				public void postProcessDistances(double[] distances) {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean getInvertSelection() {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public Instances getInstances() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getAttributeIndices() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public double distance(Instance first, Instance second, double cutOffValue, PerformanceStats stats) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public double distance(Instance first, Instance second, double cutOffValue) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public double distance(Instance first, Instance second, PerformanceStats stats) throws Exception {
					// TODO Auto-generated method stub
					return 0;
				}

				private ArrayList<String> getInstanceString (Instance instance){
					ArrayList<String> instanceString = new ArrayList<String>();
					for (int i = 0; i < instance.numAttributes(); i++) {
						Attribute a = instance.attribute(i);
						instanceString.add((new Double(instance.value(i))).toString());
					}
					return instanceString;
				}

				@Override
				public double distance(Instance first, Instance second) {
					ArrayList<String> firstS = getInstanceString(first);
					ArrayList<String> secondS = getInstanceString(second);
					//EditDistance eD = new EditDistance(firstS, secondS);
					//return eD.computeNormalizedEditDistance();
					EditDistance eD = new EditDistance();
					return eD.computeNormalizedEditDistance(firstS, secondS);					
				}
			};

			 h.setDistanceFunction(d);
			 SelectedTag s = new SelectedTag(1, HierarchicalClusterer_predictions.TAGS_LINK_TYPE);
			 h.setLinkType(s);

			 h.buildClusterer(data);


			 /*if(ServerConfigurationClass.printDebug)
				 {
				 System.out.println(h.graph());
				 
				 int[] clusters = h.getClusters();
				 System.out.println(h.getNumClusters());
				 for (int i = 0; i < clusters.length; i++) {
					System.out.print(clusters[i]+" ");
				 }
			 }*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public HashMap<Integer, ArrayList<Integer>> clusterTraces(Instances encodedTraces, int numClusters){
		HashMap<Integer, ArrayList<Integer>> instanceMap = new HashMap<Integer, ArrayList<Integer>>();

		// create the model
		kMeans = new SimpleKMeans();
		try {
			kMeans.setInitializeUsingKMeansPlusPlusMethod(true);
			kMeans.setNumClusters(numClusters);
			kMeans.buildClusterer(encodedTraces);

			//TEMP
//			System.out.println("NUM CLUSTERS "+numClusters+" "+kMeans.getAvgSilCoeff());
//			FileWriter fW = new FileWriter(new File("./output/temp_silhouette.txt"));
//			fW.write("NUM CLUSTERS "+numClusters+" "+kMeans.getAvgSilCoeff()+"\n");
//			fW.close();

			ArrayList<Attribute> attributes = new ArrayList<Attribute>();
			for (int i = 0; i<encodedTraces.numAttributes(); i++) {
				attributes.add(encodedTraces.attribute(i));
			}

			// get cluster membership for each instance
			for (int i = 0; i < encodedTraces.numInstances(); i++) {
				int clusterNumber = kMeans.clusterInstance(encodedTraces.instance(i));
				//= new ArrayList<Integer>();
				ArrayList<Integer> instanceIndexes = instanceMap.get(clusterNumber);
				if (instanceIndexes == null){
					//instanceIndexes = new Instances("I"+i, attributes, originalInstances.numInstances());
					instanceIndexes = new ArrayList<Integer>();
				}
				instanceIndexes.add(i);
				instanceMap.put(new Integer(clusterNumber), instanceIndexes);
			}


/*		// print out the cluster centroids
		Instances centroids = kMeans.getClusterCentroids();
		for (int i = 0; i < centroids.numInstances(); i++) {
		  System.out.print("Centroid ");
		  System.out.print(i + 1);
		  System.out.print (": ");
		  System.out.println (centroids.instance(i));
		}

		// get cluster membership for each instance
		for (int i = 0; i < data.numInstances(); i++) {
			System.out.print(data.instance(i));
			System.out.print(" is in cluster ");
			System.out.println(kMeans.clusterInstance(data.instance(i)) + 1);
		}*/

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instanceMap;

	}




	public int clusterInstance (Instance instance, HashMap<Integer,XLog> clusters){
		int clusterNumber = -1;
		try {
//			Instances centroids = kMeans.getClusterCentroids();
//			DistanceFunction dF = kMeans.getDistanceFunction();
//			int minCluster = clusters.keySet().iterator().next();
//			double min = dF.distance(centroids.get(minCluster), instance);
//			for (Integer clusterIndex: clusters.keySet()) {
//				Instance centroid  = centroids.get(clusterIndex);
//				double dist = dF.distance(centroid,instance);
//				if (dist<min){
//					min = dist;
//					minCluster= clusterIndex;
//				}
//			}
			clusterNumber =  kMeans.clusterInstance(instance);
		//	clusterNumber = minCluster;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// increasing the cluster Number by 1 because our clusters start from 1
		return clusterNumber;
	}

	public int clusterTrace(Instance encodedTrace, HashMap<Integer,XLog> clusterlogMap){
		int clusterNumber = clusterInstance(encodedTrace, clusterlogMap);
		return clusterNumber;
	}




}
