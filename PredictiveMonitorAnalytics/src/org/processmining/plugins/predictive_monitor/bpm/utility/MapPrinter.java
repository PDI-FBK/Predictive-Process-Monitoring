package org.processmining.plugins.predictive_monitor.bpm.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class MapPrinter {
	
	public static void printMap(HashMap<Integer, ArrayList<Integer>> clusterMap) {
//		if(ServerConfigurationClass.printDebug){
//			for (Integer clusterNumber : clusterMap.keySet()) {
//				System.out.print(clusterNumber+": ");
//				for (Integer traceNumber : clusterMap.get(clusterNumber)) {
//					System.out.print(traceNumber+" ");
//				}
//				System.out.println();
//			}
//		}
	}
	
	public static void printMap(HashMap<Integer, ArrayList<Integer>> clusterMap, String filePath) {
		
		try {
			FileWriter fW = new FileWriter(new File(filePath));
			
			for (Integer clusterNumber : clusterMap.keySet()) {
				fW.write(clusterNumber+": ");
				for (Integer traceNumber : clusterMap.get(clusterNumber)) {
					fW.write(traceNumber+" ");
				}
				fW.write("\n");
			}
			
			fW.flush();
			fW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printMap(HashMap<Integer, ArrayList<Integer>> clusterMap, int numOfClusters, int numOfNoisy, String filePath) {

		try {
			FileWriter fW = new FileWriter(new File(filePath));
			fW.write("NUMBER OF CLUSTERS "+numOfClusters+"\n");
			fW.write("NUMBER OF NOISY STRINGS "+numOfNoisy);			
			for (Integer clusterNumber : clusterMap.keySet()) {
				fW.write(clusterNumber+": ");
				for (Integer traceNumber : clusterMap.get(clusterNumber)) {
					fW.write(traceNumber+" ");
				}
				fW.write("\n");
			}
			
			fW.flush();
			fW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
