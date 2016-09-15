package org.processmining.plugins.predictive_monitor.bpm.clustering.data_structures;

public interface Point {

	public boolean isVisited();
	
	public void setVisited(boolean visited);
	
	public Integer getCluster();
	
	public void setCluster(Integer cluster);
}
