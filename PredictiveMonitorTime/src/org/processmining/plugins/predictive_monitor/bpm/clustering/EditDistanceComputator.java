package org.processmining.plugins.predictive_monitor.bpm.clustering;

import java.util.List;

import org.processmining.plugins.predictive_monitor.bpm.clustering.data_structures.Entry;
import org.processmining.plugins.predictive_monitor.bpm.clustering.data_structures.Operation;





public class EditDistanceComputator {
    public static int computeEditDistance(List<String> initialString, List<String> finalString) {
        Entry entryTable[][];
    	
        entryTable = new Entry[finalString.size() + 1][initialString.size() + 1];
        entryTable[0][0] = new Entry(0, Operation.NOOP); // initial state
        
        for (int i = 0; i < finalString.size() + 1; i++) {
            for (int j = 0; j < initialString.size() + 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }

                int minCost = Integer.MAX_VALUE;
                Operation minCostOp = null;

                if (i > 0 && j > 0 && initialString.get(j - 1).equals(finalString.get(i - 1))) {
                    int c = entryTable[i - 1][j - 1].getCost() + Operation.OK.cost;
                    if (c < minCost) {
                        minCost = c;
                        minCostOp = Operation.OK;
                    }
                }

                if (i > 0 && j > 0) {
                    int c = entryTable[i - 1][j - 1].getCost() + Operation.REPLACE.cost;
                    if (c < minCost) {
                        minCost = c;
                        minCostOp = Operation.REPLACE;
                    }
                }

                if (i > 0) {
                    int c = entryTable[i - 1][j].getCost() + Operation.INSERT.cost;
                    if (c < minCost) {
                        minCost = c;
                        minCostOp = Operation.INSERT;
                    }
                }

                if (j > 0) {
                    int c = entryTable[i][j - 1].getCost() + Operation.DELETE.cost;
                    if (c < minCost) {
                        minCost = c;
                        minCostOp = Operation.DELETE;
                    }
                }

                entryTable[i][j] = new Entry(minCost, minCostOp);
            }
        }
        
        return entryTable[finalString.size()][initialString.size()].getCost();
    }
    
    public static double computeNormalizedEditDistance(List<String> initialString, List<String> finalString) {
    	int editDistance = computeEditDistance(initialString, finalString);
    	int max = Math.max(initialString.size(),finalString.size());
    	double normalizedEditDistance = 0.0;
    	if (max>0)
    		normalizedEditDistance = ((double) editDistance)/(initialString.size()+finalString.size());
		return normalizedEditDistance;
    }

    public static double computeNormalizedSimilarity(List<String> initialString, List<String> finalString) {
    	double normalizedEditDistance = computeNormalizedEditDistance(initialString, finalString);
		return (1-normalizedEditDistance);
    }
}
