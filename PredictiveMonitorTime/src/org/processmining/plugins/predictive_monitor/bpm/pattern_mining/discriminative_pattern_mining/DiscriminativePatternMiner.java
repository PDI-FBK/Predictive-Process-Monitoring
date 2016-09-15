package org.processmining.plugins.predictive_monitor.bpm.pattern_mining.discriminative_pattern_mining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.KMPMatcher;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Tuple;

public class DiscriminativePatternMiner {

	private List<Tuple<List<String>, String>> sdb;
	private int w = 1;

	private Map<List<Integer>, Double> entropyMap = new HashMap<List<Integer>, Double>();
	private Map<List<String>, Double> utilityMap = new HashMap<List<String>, Double>();
	private Map<List<String>, Integer[]> minPrefixMap = new HashMap<List<String>, Integer[]>();
	private Set<String> alphabet = new HashSet<String>();
	private KMPMatcher matcher = new KMPMatcher();
	private Double sdbEntropy;

	public DiscriminativePatternMiner(final XLog log, HashMap<String, String> labels) {
		sdb = readSdbXlog(log, labels);
		List<Integer> sdbIdxs = new ArrayList<Integer>();
		for (int i = 0; i < sdb.size(); i++) {
			sdbIdxs.add(i);
		}
		sdbEntropy = calculateEntropy(sdbIdxs);
	}
	
	private List<Tuple<List<String>, String>> readSdbXlog(XLog log, HashMap<String, String> labels) {
		List<Tuple<List<String>, String>> allTraces = new ArrayList<Tuple<List<String>, String>>();
			for (XTrace trace : log) {
				List<String> sequence = new ArrayList<String>();
				for (XEvent event : trace) {
					String eventName = XConceptExtension.instance()
							.extractName(event);
					alphabet.add(eventName);
					sequence.add(eventName);
				}
				String classLabel =  labels.get(XConceptExtension.instance().extractName(trace));
				allTraces.add(new Tuple<List<String>, String>(sequence,
						classLabel));
			}
		return allTraces;
	}


	public ArrayList<Pattern> selectTopKFeatures(int numberOfPatterns, int maxPatternsSameLength, Double minSup) {
		PriorityQueue<Tuple<List<String>, Double>> seeds = new PriorityQueue<Tuple<List<String>, Double>>(
				100, Collections.reverseOrder());
		PriorityQueue<Tuple<List<String>, Double>> prevSeeds = new PriorityQueue<Tuple<List<String>, Double>>(
				100, Collections.reverseOrder());
		List<String> root = new ArrayList<String>();
		root.add("");
		prevSeeds.add(new Tuple<List<String>, Double>(root, 0.0));
		while (prevSeeds.size() > 0) {
			PriorityQueue<Tuple<List<String>, Double>> candidateSeeds = new PriorityQueue<Tuple<List<String>, Double>>(
					100, Collections.reverseOrder());
			for (Tuple<List<String>, Double> prevSeed : prevSeeds) {
				for (String symbol : alphabet) {
					List<String> newSeed;
					if (prevSeed.x.get(0).equals("")) {
						newSeed = new ArrayList<String>();
					} else {
						newSeed = new ArrayList<String>(prevSeed.x);
					}
					newSeed.add(symbol);
					if (calculateSupport(newSeed) >= minSup) {
						candidateSeeds.add(new Tuple<List<String>, Double>(
								newSeed, calculateUtility(newSeed)));
					}
				}
			}
			prevSeeds = new PriorityQueue<Tuple<List<String>, Double>>(
				100, Collections.reverseOrder());

			int i = 0;
			while (!candidateSeeds.isEmpty() && i < maxPatternsSameLength) {
				Tuple<List<String>, Double> selectedFeature = candidateSeeds
						.poll();
				seeds.add(selectedFeature);
				prevSeeds.add(selectedFeature);
				i++;
			}

		}
		ArrayList<Pattern> finalSeeds = new ArrayList<Pattern>();
		int i = 0;
		while (!seeds.isEmpty() && i < numberOfPatterns) {
			Pattern pattern  = new Pattern();
			pattern.setItems((ArrayList<String>) seeds.poll().x);
			finalSeeds.add(pattern);
			i++;
		}
		return finalSeeds;
	}

	private Double calculateSupport(List<String> feature) {
		Double count = 0.0;
		for (int i = 0; i < sdb.size(); i++) {
			Tuple<List<String>, String> sequence = sdb.get(i);

			if (minPrefixMap.get(feature) == null) {
				minPrefixMap.put(feature, new Integer[sdb.size()]);
			}
			Integer minPrefixIdx = minPrefixMap.get(feature)[i];
			if (minPrefixIdx == null) {
				minPrefixIdx = matcher.match(sequence.x, feature);
				minPrefixMap.get(feature)[i] = minPrefixIdx;
			}
			if (minPrefixIdx >= 0) {
				count += 1;
			}
		}
		return count / sdb.size();
	}

	public Double calculateUtility(List<String> feature) {
		Double utility = utilityMap.get(feature);
		if (utility != null) {
			return utility;
		}
		List<Integer> featureSdbIdxs = getFeatureSdbIdxs(feature);
		//System.out.print(feature + ", ");
		Double featureSdbEntropy = calculateEntropy(featureSdbIdxs);
		Double wsup = calculateWsup(feature, featureSdbIdxs);
		// System.out.println(feature + " " + wsup);
		utility = Math.pow((sdbEntropy - featureSdbEntropy), w) * wsup;
		utilityMap.put(feature, utility);
		return utility;
	}

	private Double calculateWsup(List<String> feature,
			List<Integer> featureSdbIdxs) {
		Double sum = 0.0;
		for (Integer sequenceIdx : featureSdbIdxs) {
			sum += 1.0 / (minPrefixMap.get(feature)[sequenceIdx] + 1);
		}
		return sum / sdb.size();
	}

	private Double calculateEntropy(List<Integer> sdbIdxs) {
		Double entropy = entropyMap.get(sdbIdxs);
		if (entropy != null) {
			return entropy;
		}
		Map<Boolean, Integer> classFrequencies = new HashMap<Boolean, Integer>();
		for (Integer sdbIdx : sdbIdxs) {
			Tuple<List<String>, String> sequence = sdb.get(sdbIdx);

			Boolean classLabel = sequence.y=="yes"; //TODO change to support also time intervals
			if (classFrequencies.get(classLabel) != null) {
				classFrequencies.put(classLabel,
						classFrequencies.get(classLabel) + 1);
			} else {
				classFrequencies.put(classLabel, 1);
			}
		}

		Double sum = 0.0;
		for (Map.Entry<Boolean, Integer> entry : classFrequencies.entrySet()) {
			Double prob = 1.0 * entry.getValue() / sdb.size();
			//System.out.print(entry.getKey() + ": " + prob + ", ");
			if (sdb.size() == 0) {
				sum += 0.0;
			} else {
				sum += prob * Math.log(prob);
			}
		}
		//System.out.println();
		entropyMap.put(sdbIdxs, -sum);
		return -sum;
	}

	private List<Integer> getFeatureSdbIdxs(List<String> feature) {
		List<Integer> featureSdbIdxs = new ArrayList<Integer>();
		for (int i = 0; i < sdb.size(); i++) {
			if (minPrefixMap.get(feature) == null) {
				minPrefixMap.put(feature, new Integer[sdb.size()]);
			}
			Integer minPrefixIdx = minPrefixMap.get(feature)[i];
			if (minPrefixIdx == null) {
				minPrefixIdx = matcher.match(sdb.get(i).x, feature);
				minPrefixMap.get(feature)[i] = minPrefixIdx;
			}
			if (minPrefixIdx >= 0) {
				featureSdbIdxs.add(i);
			}
		}
		return featureSdbIdxs;
	}

}
