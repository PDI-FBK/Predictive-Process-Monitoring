package org.processmining.plugins.predictive_monitor.bpm.encoding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.guidetreeminer_predictions.ClusterLogOutput;

public class Decoder {
	
	public static XLog computeXLogCluster(ArrayList<Integer> instanceIndexes, Map<Integer, XTrace> logAndEncodedTracesMapping){
		XLog clusterLog = XFactoryRegistry.instance().currentDefault().createLog();
		for (Integer index : instanceIndexes) {
			XTrace trace = logAndEncodedTracesMapping.get(index);
			clusterLog.add(trace);
		}
		return clusterLog;
	}
	
	public static HashMap<Integer,XLog> computeXLogClusterMap(HashMap<Integer, ArrayList<Integer>> clusterMap, Map<Integer,XTrace> logAndEncodedTracesMapping){
		HashMap<Integer, XLog> xLogClusterMap = new HashMap<Integer,XLog>();
		for (Integer key : clusterMap.keySet()) {
			xLogClusterMap.put(key, computeXLogCluster(clusterMap.get(key), logAndEncodedTracesMapping));
		}
		return xLogClusterMap;
	}

	public static HashMap<Integer,XLog> computeXLogClusterMap(Object[] patt){
		HashMap<Integer,XLog> xLogClusterMap = new HashMap<Integer, XLog>();
		ArrayList<XLog> xLogClusterArrayList = (ArrayList<XLog>) ((ClusterLogOutput) patt[1]).clusterLogList();
		int i = 1;
		for (XLog xLog : xLogClusterArrayList) {
			xLogClusterMap.put(new Integer(i), xLog);
			i++;
		}
		return xLogClusterMap;
	}

	
}
