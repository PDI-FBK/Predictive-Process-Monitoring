package org.processmining.plugins.predictive_monitor.bpm.clustering.data_structures;

import java.util.ArrayList;

import org.deckfour.xes.model.XTrace;

public class TracePoint implements Point {
	
	private boolean visited;
	private ArrayList<String> currentTrace;
	private Integer cluster;

	
	public TracePoint(boolean visited, ArrayList<String> currentTrace) {
		super();
		this.visited = visited;
		this.currentTrace = currentTrace;
		this.cluster= -2;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public ArrayList<String> getCurrentTrace() {
		return currentTrace;
	}

	public Integer getCluster() {
		return cluster;
	}

	public void setCluster(Integer cluster) {
		this.cluster = cluster;
	}
	
	
	


	


}
