package org.processmining.plugins.predictive_monitor.bpm.pattern_mining;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Matching pattern and sequence based on the Knuth Morris Pratt algorithm
public class KMPMatcher {
	Map<List<String>, int[]> kmpTables = new HashMap<List<String>, int[]>();

	public void preprocess(List<String> feature) {
		int[] kmpTable;
		if (kmpTables.get(feature) != null) {
			return;
		} else {
			kmpTable = new int[feature.size() + 1];
		}

		int i = 0, j = -1;
		kmpTable[i] = j;
		while (i < feature.size()) {
			while (j >= 0 && feature.get(i) != feature.get(j)) {
				j = kmpTable[j];
			}
			i++;
			j++;
			kmpTable[i] = j;
		}
		kmpTables.put(feature, kmpTable);
		return;
	}

	public int match(List<String> sequence, List<String> feature) {
		int i = 0, m = 0;
		int featureLength = feature.size();
		int sequenceLength = sequence.size();

		preprocess(feature);

		while (m + i < sequenceLength) {
			if (sequence.get(i + m).equals(feature.get(i))) {
				if (i == featureLength - 1) {
					// a match is found
					return (m);
				}
				i++;
			} else {
				int kmpTableValue = kmpTables.get(feature)[i];
				if (kmpTableValue > -1) {
					m = m + i + kmpTableValue;
					i = kmpTableValue;
				} else {
					i = 0;
					m++;
				}
			}
		}
		return -1;
	}
	
	public int countOccurrencies(List<String> sequence, List<String> feature) {
		int count = 0;
		int i = 0, m = 0;
		int featureLength = feature.size();
		int sequenceLength = sequence.size();

		preprocess(feature);

		while (m + i < sequenceLength) {
			if (sequence.get(i + m).equals(feature.get(i))) {
				if (i == featureLength - 1) {
					// a match is found
					count++;
					int kmpTableValue = kmpTables.get(feature)[i];
					if (kmpTableValue > -1) {
						m = m + i + kmpTableValue;
						i = kmpTableValue;
					} else {
						i = 0;
						m++;
					}
					i--;
				}
				i++;
			} else {
				int kmpTableValue = kmpTables.get(feature)[i];
				if (kmpTableValue > -1) {
					m = m + i + kmpTableValue;
					i = kmpTableValue;
				} else {
					i = 0;
					m++;
				}
			}
		}
		return count;
	}

}
