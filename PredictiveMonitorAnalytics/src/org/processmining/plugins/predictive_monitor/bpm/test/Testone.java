package org.processmining.plugins.predictive_monitor.bpm.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;

import org.processmining.plugins.predictive_monitor.bpm.clustering.EditDistance;

import weka_predictions.clusterers.HierarchicalClusterer_predictions;
import weka_predictions.core.Attribute;
import weka_predictions.core.DistanceFunction;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;
import weka_predictions.core.SelectedTag;
import weka_predictions.core.converters.CSVLoader;
import weka_predictions.core.neighboursearch.PerformanceStats;

public class Testone {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

	}

	
	public static void cluster(String filePath){

		try {

			 // load CSV
		    CSVLoader loader = new CSVLoader();
			//ArffLoader loader = new ArffLoader();
		    loader.setSource(new File(filePath));
		    Instances data = loader.getDataSet();
		    
/*		    for (int i = 0; i < data.size(); i++) {
				Instance instance = data.get(i);
				map.put(instance, i);
			}*/
		 
		    // save ARFF
/*		    ArffSaver saver = new ArffSaver();
		    saver.setInstances(data);
		    saver.setFile(new File("./output/boh.arff"));
		    //saver.setDestination(new File("./output/boh.arff"));
		    saver.writeBatch();
*/
			//Instances data = DataSource.read();
			
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
/*					EditDistance eD = new EditDistance(firstS, secondS);
					return eD.computeNormalizedEditDistance();*/
					EditDistance eD = new EditDistance();
					return eD.computeNormalizedEditDistance(firstS, secondS);					
				}
			};
	
			 h.setDistanceFunction(d);
			 SelectedTag s = new SelectedTag(1, HierarchicalClusterer_predictions.TAGS_LINK_TYPE);
			 h.setLinkType(s);

			 h.buildClusterer(data);
			 
			 
//			 if(ServerConfigurationClass.printDebug)
//			 {
//				 System.out.println(h.graph());
//				 
//				 int[] clusters = h.getClusters();
//				 System.out.println(h.getNumClusters());
//				 for (int i = 0; i < clusters.length; i++) {
//					System.out.print(clusters[i]+" ");
//				}
//			 }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
