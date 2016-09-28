package org.processmining.plugins.predictive_monitor.bpm.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Tuple;

public class ModelClusterer2 {

	private List<Map<String, Double>> model;
	private String DEFAULT_METRIC = "euclidean";
	
	public ModelClusterer2(){
	}

	public ModelClusterer2(String filepath) {
		model = readModel(filepath);
	}
	
	public List<Integer> cluster(String filepath) {
		return cluster(filepath, DEFAULT_METRIC);
	}

	public List<Integer> cluster(String filepath, String metric) {
		List<String[]> sequences = new ArrayList<String[]>();
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] sequence = line.split(";");
				sequences.add(sequence);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cluster(sequences, metric);
	}
	
	public List<Integer> cluster(List<String[]> sequences) {
		return cluster(sequences,DEFAULT_METRIC);
	}
	
	public List<Integer> cluster(List<String[]> sequences, String metric) {
		List<Integer> clusters = new ArrayList<Integer>();
		for (String[] sequence : sequences) {
			clusters.add(cluster(sequence, metric, calculateFrequencies(sequence)));
		}
		return clusters;
	}

	public int cluster(String[] sequence, String metric) {
		return cluster(sequence, metric, calculateFrequencies(sequence));
	}

	public int cluster(String[] sequence, String metric, Map<String, Double> freqs) {

		Double bestDistance = Double.MAX_VALUE;
		int bestCluster = -1;
		for (int i = 0; i < model.size(); i++) {
			Double distance;
			if (metric.equals("cosine")) {
				distance = calculateCosineDistance(freqs, model.get(i));
			} else {
				distance = calculateEuclideanDistance(freqs,
						model.get(i));
			}
			if (distance < bestDistance) {
				bestDistance = distance;
				bestCluster = i + 1;
			}
		}
		return bestCluster;
	}
	
	public int clusterTrace(String[] sequence) {
		return cluster(sequence, DEFAULT_METRIC, calculateFrequencies(sequence));
	}


	public int clusterTrace(String[] sequence, Map<String, Double> freqs) {
		return cluster(sequence, DEFAULT_METRIC, freqs);
	}

	public List<Integer> getTopClusters(String[] sequence, int nclusters, Map<String, Double> freqs) {
		return getTopClusters(sequence, DEFAULT_METRIC, nclusters, freqs);
	}

	public List<Integer> getTopClusters(String[] sequence, String metric, int nclusters, Map<String, Double> freqs) {
		PriorityQueue<Tuple<Integer, Double>> clusterDistances = new PriorityQueue<Tuple<Integer, Double>>();
		for (int i = 0; i < model.size(); i++) {
			Double distance;
			if (metric.equals("cosine")) {
				distance = calculateCosineDistance(freqs, model.get(i));
			} else {
				distance = calculateEuclideanDistance(freqs,
					model.get(i));
			}
			clusterDistances.add(new Tuple<Integer, Double>(i+1,distance));
		}
		List<Integer> topClusters = new ArrayList<Integer>();
		while (!clusterDistances.isEmpty() && topClusters.size() < nclusters) {
			topClusters.add(clusterDistances.poll().x);
		}

		return topClusters;
 	}


	
	private List<Map<String, Double>> readModel(String filepath) {
		List<Map<String, Double>> clusteringModel = new ArrayList<Map<String, Double>>();
		try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(";");
				String event = parts[0];
				if (clusteringModel.size() == 0) {
					for (int i = 1; i < parts.length; i++) {
						clusteringModel.add(new HashMap<String, Double>());
					}
				}
				for (int i = 1; i < parts.length; i++) {
					Double meanValue = Double.parseDouble(parts[i]);
					clusteringModel.get(i - 1).put(event, meanValue);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return clusteringModel;
	}
	
	private Map<String, Double> calculateFrequencies(String[] sequence) {
		Map<String, Double> freqs = new HashMap<String, Double>();
		for (String event : sequence) {
			if (freqs.get(event) != null) {
				freqs.put(event, freqs.get(event) + 1);
			} else {
				freqs.put(event, 1.0);
			}
		}
		return freqs;
	}
	
	private Double calculateEuclideanDistance(
			Map<String, Double> sequenceFreqs, Map<String, Double> clusterModel) {
		Double distance = 0.0;

		for (Map.Entry<String, Double> entry : clusterModel.entrySet()) {
			String event = entry.getKey();
			Double meanValue = entry.getValue();
			Double seqValue = sequenceFreqs.get(event);
			if (seqValue == null) {
				distance += Math.pow(0.0 - meanValue, 2);
			} else {
				distance += Math.pow(seqValue - meanValue, 2);
			}
		}

		return Math.sqrt(distance);
	}
	
	private Double calculateNorm(Map<String, Double> freqs) {
		Double norm = 0.0;
		for (Double value : freqs.values()) {
			norm = norm + Math.pow(value, 2);
		}
		return Math.sqrt(norm);
	}

	private Double calculateCosineDistance(Map<String, Double> sequenceFreqs,
			Map<String, Double> clusterModel) {
		Double similarity = 0.0;

		for (Map.Entry<String, Double> entry : clusterModel.entrySet()) {
			String event = entry.getKey();
			Double meanValue = entry.getValue();
			Double seqValue = sequenceFreqs.get(event);
			if (seqValue == null) {
				similarity += 1.0 * meanValue;
			} else {
				similarity += seqValue * meanValue;
			}
		}

		similarity /= calculateNorm(sequenceFreqs);
		similarity /= calculateNorm(clusterModel);

		return 1 - similarity;
	}
	
	public HashMap<Integer, ArrayList<Integer>> clusterTraces(String RPath, String RScriptPath, String trainingLogFilePath, String clusterFilePath){
		HashMap<Integer, ArrayList<Integer>> instanceMap = new HashMap<>();
		instantiateRScript(RScriptPath, trainingLogFilePath, clusterFilePath);
		String RCommand = RPath+"Rscript";
		String s = RCommand+" "+RScriptPath;
		try {
			Runtime.getRuntime().exec(RCommand+" "+RScriptPath);
			clusterTraces(clusterFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instanceMap;
	}
		
	public HashMap<Integer, ArrayList<Integer>> clusterTraces(String clusteringOutputFilePath){
		HashMap<Integer, ArrayList<Integer>> instanceMap = new HashMap<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(clusteringOutputFilePath)));
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
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instanceMap;
	}
	
	

	
	private void instantiateRScript(String RScriptPath, String trainingLogFilePath, String clusterFilePath){
		
	}


}
