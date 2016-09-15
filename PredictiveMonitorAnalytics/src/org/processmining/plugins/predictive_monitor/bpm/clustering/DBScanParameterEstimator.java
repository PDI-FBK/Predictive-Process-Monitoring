package org.processmining.plugins.predictive_monitor.bpm.clustering;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.predictive_monitor.bpm.clustering.comparator.ValueComparator;
import org.processmining.plugins.predictive_monitor.bpm.clustering.data_structures.Point;
import org.processmining.plugins.predictive_monitor.bpm.clustering.data_structures.TracePoint;
import org.processmining.plugins.predictive_monitor.bpm.clustering.metrics.EditDistanceComputator;
import org.processmining.plugins.predictive_monitor.bpm.encoding.TraceToPointEncoder;
import org.processmining.plugins.predictive_monitor.bpm.utility.TracePrefixGenerator;

public class DBScanParameterEstimator {
	private static String histogramMapFile = "./output/histogramMap.txt"; 
	private static String differenceDistanceMapFile = "./output/differenceDistanceMap.txt";
	private static String orderedDifferenceDistanceMapFile = "./output/orderedDifferenceDistanceMap.txt";
	
	private static Map<Integer, ArrayList<Double>>  computeHistogramData (ArrayList<Point> dataset, int minK, int maxK){
		Map<Integer, ArrayList<Double>> histogramMap = new HashMap<Integer, ArrayList<Double>>();
		for (int k = minK; k < maxK; k++) {
			ArrayList<Double> kDistances = new ArrayList<Double>(); 
			for (Point pointX : dataset) {
				HashMap<Integer, Double> neighbourDistance = new HashMap<Integer, Double>(); 
				TracePoint tPointX = (TracePoint) pointX;
				ArrayList<String> traceX = tPointX.getCurrentTrace();
				int j=0;
				for (Point pointY : dataset) {
					TracePoint tPointY = (TracePoint) pointY;
					ArrayList<String> traceY = tPointY.getCurrentTrace();
					double distance =EditDistanceComputator.computeNormalizedEditDistance(traceX, traceY);
					neighbourDistance.put(j, distance);
					j++;
				}
				
		        ValueComparator bvc =  new ValueComparator(neighbourDistance);
		        TreeMap<Integer,Double> sorted_map = new TreeMap<Integer,Double>(bvc);
		        sorted_map.putAll(neighbourDistance);
/*		        int d=0;
		        double prev = -1;
		        Double current = null;
		        for (Integer key : sorted_map.keySet()) {
		        	current = neighbourDistance.get(key);
		        	if (current.doubleValue()!=prev){
		        		d++;
		        		prev = current.doubleValue();
		        	}
		        	if (d==k)
		        		break;
				}*/
		        Double current = null;
		        int d= 0;
		        for (Integer key : sorted_map.keySet()) {
		        	current = neighbourDistance.get(key);
		        	d++;
		        	if (d==k)
		        		break;
				}
		        //Double kDistance = sorted_map.get(k);
		        kDistances.add(current.doubleValue());
			}
			Collections.sort(kDistances);
			histogramMap.put(k, kDistances);
			
		}
		return histogramMap;
				
	}
	
	public static void main(String[] argv)
	{
		XLog log=null;
		String inputFile = "input/random/BPI2011_80.xes";
		if(inputFile.toLowerCase().contains("xes")){
			XesXmlParser parser = new XesXmlParser();
			if(parser.canParse(new File(inputFile))){
				try {
					log = parser.parse(new File(inputFile)).get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		XLog prefixLog = TracePrefixGenerator.generatePrefixesFromLog(log,1,51,5);
		TraceToPointEncoder ttpe = new TraceToPointEncoder();
		ArrayList<Point> dataset = ttpe.transformIntoTracePoints(prefixLog);
		try {
			computeHistogramData2(dataset,5);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Map<Integer, ArrayList<Double>>  computeHistogramData2 (ArrayList<Point> dataset, int kMax) throws FileNotFoundException{
		Map<Integer, ArrayList<Double>> histogramMap = new HashMap<Integer, ArrayList<Double>>();
		List<PriorityQueue<Double>> allDistances = new ArrayList<>();
			for (Point pointX : dataset) {
				PriorityQueue<Double> distances = new PriorityQueue<>(); 
				for (Point pointY : dataset) {
					if(pointX!=pointY)
					distances.add(new Double(EditDistanceComputator.computeNormalizedEditDistance(((TracePoint)pointX).getCurrentTrace(),((TracePoint)pointY).getCurrentTrace())));
				}
				allDistances.add(distances);
			}
			
			Map<Integer,List<Double>> distancesMap = new HashMap<>();
			
			for(int i=1;i<=kMax;i++){
				List<Double> kDist = new ArrayList<>();
				for(PriorityQueue<Double> distances : allDistances)
				{
					kDist.add(distances.poll());
				}
				Collections.sort(kDist);
				distancesMap.put(new Integer(i), kDist);
			}
			
			PrintWriter pw = new PrintWriter(new File("output/dbScanEvaluation2.csv"));
			for(int i=1;i<kMax;i++)
			{
				pw.println("######################### K="+i+" ###############################");
				int j=0;
				for(Double distance: distancesMap.get(i)){
					pw.println("\""+j+"\",\""+distance+"\"");
					j++;
				}
				pw.println();
				pw.flush();
			}
			pw.close();
			return null;
	}
	
	private static void writeHistogramMap(FileWriter fW, Map<Integer, ArrayList<Double>> histogramMap){
		try {
			for (Integer kNumber : histogramMap.keySet()) {
				fW.write("*******************  K = "+kNumber+"************************************\n");
				for (Double distance : histogramMap.get(kNumber)) {
					fW.write(distance+"\n");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static Map<Double, Double> computeDifferenceMap(FileWriter fW, FileWriter oFW, ArrayList<Double> kDistances){
		/*HashMap<Double, Double> differenceMap = new HashMap<Double, Double>();
		for (int i = 0; i < kDistances.size()-1; i++) {
			double difference = kDistances.get(i+1) - kDistances.get(i);
			if (difference>0)
				differenceMap.put(kDistances.get(i), difference);
		}
		Set<Double> keySet = differenceMap.keySet();
		ArrayList<Double> keys = new ArrayList<Double>();
		keys.addAll(keySet) ;
		Collections.sort(keys);
		for (Double distance : keys) {
			try {
				fW.write(distance+" "+differenceMap.get(distance)+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        DifferenceDistanceComparator ddc =  new DifferenceDistanceComparator(differenceMap);
        TreeMap<Double,Double> sorted_map = new TreeMap<Double,Double>(ddc);
        sorted_map.putAll(differenceMap);
		for (Double distance : sorted_map.keySet()) {
			try {
				oFW.write(distance+" "+differenceMap.get(distance)+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sorted_map;*/ return null;
	}
	
	private static Map<Integer, Map<Double, Double>> computeDifferenceMaps(FileWriter fW, FileWriter oFW, Map<Integer, ArrayList<Double>> histogramMap){
		Map<Integer, Map<Double, Double>> differenceMaps = new HashMap<Integer, Map<Double, Double>>();
		for (Integer k : histogramMap.keySet()) {
			try {
				fW.write("*******************  K = "+k+"************************************\n");
				oFW.write("*******************  K = "+k+"************************************\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			differenceMaps.put(k, computeDifferenceMap(fW, oFW, histogramMap.get(k)));
		}
		return differenceMaps;
	}
	
	private static void writeDifferenceMap(FileWriter fW,  Map<Integer, Map<Double, Double>> differenceMaps)
	{
		try {
			for (Integer kNumber : differenceMaps.keySet()) {
				fW.write("*******************  K = "+kNumber+"************************************\n");
				Map<Double, Double> differenceMap = differenceMaps.get(kNumber); 
				for (Double distance : differenceMap.keySet()) {
					fW.write(distance+" "+differenceMap.get(distance)+"\n");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void estimateParameterSupport(ArrayList<Point> dataset, int minK, int maxK){
		try {
			Map<Integer, ArrayList<Double>> histogramMap = computeHistogramData(dataset,minK,maxK);
			FileWriter hMFW = new FileWriter(new File(histogramMapFile));
			writeHistogramMap(hMFW, histogramMap);
			hMFW.flush();
			hMFW.close();
			FileWriter dMFW = new FileWriter(new File(differenceDistanceMapFile));
			FileWriter oDMFW = new FileWriter(new File(orderedDifferenceDistanceMapFile));
			Map<Integer, Map<Double, Double>> differenceMaps = computeDifferenceMaps(dMFW, oDMFW, histogramMap);
			dMFW.flush();
			dMFW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}