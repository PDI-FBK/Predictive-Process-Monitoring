package org.processmining.plugins.predictive_monitor.bpm.encoding;

import java.util.ArrayList;
import java.util.HashMap;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.clustering.data_structures.Point;
import org.processmining.plugins.predictive_monitor.bpm.clustering.data_structures.TracePoint;
import org.processmining.plugins.predictive_monitor.bpm.utility.XLogReader;


public class TraceToPointEncoder {
	HashMap<Integer, XTrace> traceMapping = new HashMap<Integer, XTrace>();
	
	public ArrayList<Point> transformIntoTracePoints(String inputXesFilePath){
		ArrayList<Point> points = new ArrayList<Point>();
		try {
			XLog log = XLogReader.openLog(inputXesFilePath);
			transformIntoTracePoints(log);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return points;
		
	}
	
	public ArrayList<Point> transformIntoTracePoints(XLog log){
		ArrayList<Point> points = new ArrayList<Point>();
		int i=0;
			for (XTrace trace : log) {
				points.add(computePoint(trace));
				traceMapping.put(i, trace);
				i++;
			}
		
		return points;
		
	}
	
	public static Point computePoint(XTrace trace){
		Point point = null;
		ArrayList<String> traceArrayList = new ArrayList<>();
		for (XEvent event : trace) {
			traceArrayList.add(XConceptExtension.instance().extractName(event));
		}
		point = new TracePoint(false, traceArrayList);
		return point;
	}

	public HashMap<Integer, XTrace> getTraceMapping() {
		return traceMapping;
	}
	
	
}
