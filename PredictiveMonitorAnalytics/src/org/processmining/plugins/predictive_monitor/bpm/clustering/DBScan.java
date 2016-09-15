package org.processmining.plugins.predictive_monitor.bpm.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import org.processmining.plugins.predictive_monitor.bpm.clustering.comparator.ValueComparator;
import org.processmining.plugins.predictive_monitor.bpm.clustering.data_structures.Point;
import org.processmining.plugins.predictive_monitor.bpm.clustering.data_structures.TracePoint;
import org.processmining.plugins.predictive_monitor.bpm.clustering.metrics.EditDistanceComputator;
import org.processmining.plugins.predictive_monitor.bpm.utility.Print;



public class DBScan {
	
	private ArrayList<Point> dataset = null;
	private double epsilon=0.9; 
	private int minPoints=2;
	private HashMap<Integer, ArrayList<Integer>> clusterMap = new HashMap();
	private static Print print = new Print();
	
	public DBScan(ArrayList<Point> dataset, double eps, int minPoints) {
		this.dataset = dataset;
		this.epsilon = eps;
		this.minPoints = minPoints;
	}
	
	 public HashMap<Integer, ArrayList<Integer>> computeClusters() {
		Integer cluster = 0;
		for (Point point : dataset) {
			if (!point.isVisited()){
				point.setVisited(true);
				ArrayList<Point> neighbours=regionQuery(point, epsilon);
				if (neighbours.size() < minPoints)
					point.setCluster(-1);
				else {
					cluster++;
					expandCluster(point, neighbours, cluster, epsilon, minPoints);
				}
			}
		}
		return clusterMap;
	}
	
	private void expandCluster(Point point, ArrayList<Point> neighbours, Integer cluster,
		double eps, int minPoints) {
		
		addPointToCluster(point, cluster);

		//ArrayList<Point> newNeighbours = new ArrayList<Point>();
		int index = 0;
		while (index < neighbours.size()) {
			Point neighbourPoint = neighbours.get(index);
		//for (Point neighbourPoint : neighbours) {
			if (!neighbourPoint.isVisited()){
				neighbourPoint.setVisited(true);
				ArrayList<Point> neighboursOfNeighbours = regionQuery(neighbourPoint, eps);
				if (neighboursOfNeighbours.size()>=minPoints){
					//neighbours.addAll(neighboursOfNeighbours);
					neighbours = merge(neighbours, neighboursOfNeighbours);
				}
			}
			if(neighbourPoint.getCluster()==-2){
				addPointToCluster(neighbourPoint, cluster);
			}
			index++;
		}
		//neighbours.addAll(newNeighbours);
	
	}
	
	

	private void addPointToCluster(Point point, Integer cluster) {
		ArrayList<Integer> pointsInCluster = clusterMap.get(cluster);
		int pointN = dataset.indexOf(point);
		if (pointsInCluster==null)
			pointsInCluster = new ArrayList<Integer>();
		pointsInCluster.add(pointN);
		clusterMap.put(cluster, pointsInCluster);
		point.setCluster(cluster);
	}

	private ArrayList<Point> regionQuery(Point point, double eps) {
		ArrayList<Point> regionQuery = new ArrayList<Point>();
		TracePoint tracePoint = (TracePoint) point; 
		for (Point datasetPoint : dataset) {
			TracePoint datasetTracePoint = (TracePoint) datasetPoint;
			if (EditDistanceComputator.computeNormalizedEditDistance(tracePoint.getCurrentTrace(), datasetTracePoint.getCurrentTrace()) < eps)
				regionQuery.add(datasetPoint);
		}
		return regionQuery;
	}
	
	public int countNoisy(){
		int noisyTraces = 0;
		for (Point tracePoint : dataset) {
			if (tracePoint.getCluster()==-1)
				noisyTraces++;
		}
		return noisyTraces;
	}
	
	private ArrayList<Point> merge(ArrayList<Point> pointList1, ArrayList<Point> pointList2) {
		Set<Point> oneSet = new HashSet<>(pointList1);
		for (Point point2 : pointList2) {
			if (!oneSet.contains(point2))
				pointList1.add(point2);
		}
        return pointList1;

    }

	public int getCluster(ArrayList<String> trace){
		double minDistance = 1.0;
		int cluster = -1;
		for (Point point : dataset) {
			TracePoint dSTracePoint = (TracePoint) point; 
			ArrayList<String> dsTrace = dSTracePoint.getCurrentTrace();
			double distance = EditDistanceComputator.computeNormalizedEditDistance(trace, dsTrace);
			 if (distance<minDistance){
				int dsCluster = dSTracePoint.getCluster(); 
				if (distance<epsilon && dsCluster!=-1 ){
					print.thatln(dataset.indexOf(point));
					minDistance = distance;
					cluster = dsCluster;
				}
			 }
		}
		return cluster;
	}

	public ArrayList<Point> getDataset(){
		return dataset;
	}

	
	public ArrayList<Integer> getTopClusters(ArrayList<String> trace, int voters){
		HashMap<Integer, Double> distanceFromClusters = new HashMap<Integer, Double>();  
		ArrayList<Integer> topClusters = new ArrayList<Integer>();
		
		for (Integer clusterNumber : clusterMap.keySet()) {
			if (clusterNumber!=-1){
				ArrayList<Integer> clusterTraces = clusterMap.get(clusterNumber);
				double minDistance = 1.0;			
				for (Integer clusterTrace : clusterTraces) {
					Point dsPoint = dataset.get(clusterTrace);
					TracePoint dSTracePoint = (TracePoint) dsPoint; 
					ArrayList<String> dsTrace = dSTracePoint.getCurrentTrace();
					double distance =EditDistanceComputator.computeNormalizedEditDistance(trace, dsTrace);
					if (distance<minDistance){
						minDistance = distance;
					}
				}
				distanceFromClusters.put(clusterNumber, minDistance);
			}		 
		}
        ValueComparator bvc =  new ValueComparator(distanceFromClusters);
        TreeMap<Integer,Double> sorted_map = new TreeMap<Integer,Double>(bvc);
        sorted_map.putAll(distanceFromClusters);
 		
		for (int i = 0; i < voters; i++) {
			if(trace.get(0).equals("demurrage - all spec.beh.kinderg.-Reval.")){
				print.thatln(sorted_map.keySet().toArray()[i]+" "+sorted_map.get(sorted_map.keySet().toArray()[i]));
			}
			topClusters.add((Integer) sorted_map.keySet().toArray()[i]);
		}		
		
	
		return topClusters;
	}
	         
}
