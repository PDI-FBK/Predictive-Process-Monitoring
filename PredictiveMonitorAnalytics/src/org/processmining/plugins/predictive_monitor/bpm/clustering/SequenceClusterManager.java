package org.processmining.plugins.predictive_monitor.bpm.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;


public class SequenceClusterManager {
	
	private List<XLog> logs = null;
	private String clusterMeanFilePath = null;
	private String trainingFilePath = null; 
	
	public SequenceClusterManager(String trainingFilePath, String cluesterMeanFilePath) {
		this.trainingFilePath = trainingFilePath;
		this.clusterMeanFilePath = cluesterMeanFilePath;
	}
	
	
	public void buildClusters(String clusterFilePath, XLog log){
		HashMap<Integer, ArrayList<Integer>> instanceMap = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(clusterFilePath)));
			int currentTrace = 0;
			String currentCluster;
			while ((currentCluster = br.readLine())!=null){
				Integer currClusterI= new Integer(currentCluster);
				ArrayList<Integer> clusterTraces = instanceMap.get(currClusterI);
				if (clusterTraces==null)
					clusterTraces = new ArrayList<Integer>();
				clusterTraces.add(new Integer(currentTrace));
				instanceMap.put(currClusterI, clusterTraces);
				currentTrace++;
			}
			logs = new ArrayList<XLog>();
			for (Integer key : instanceMap.keySet()) {
				XLog newClusterLog= XFactoryRegistry.instance().currentDefault().createLog();
				for (Integer index : instanceMap.get(key)) {
					XTrace trace = log.get(index);
					newClusterLog.add(trace);
				}
				logs.add(newClusterLog);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	public int computeTraceCluster(XTrace trace){
		ModelClusterer2 clusterer = new ModelClusterer2();
		String[] traceString = new String[trace.size()];
		int i = 0;
		for (XEvent event : trace) {
			traceString[i] = XConceptExtension.instance().extractName(event);
			i++;
		}
		return clusterer.cluster(traceString, "euclidean");
	}


	public List<XLog> getLogs() {
		return logs;
	}
	
	
	private ArrayList<String> computeAlphabet(XLog log){
		ArrayList<String> keys = new ArrayList<String>(); 
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				String eventName = XConceptExtension.instance().extractName(event);
				if (!keys.contains(eventName))
					keys.add(eventName);
			}
		}
		return keys;
	}
	
	
	
}
