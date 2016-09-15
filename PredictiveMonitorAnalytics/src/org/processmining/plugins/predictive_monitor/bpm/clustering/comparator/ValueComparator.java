package org.processmining.plugins.predictive_monitor.bpm.clustering.comparator;

import java.util.Comparator;
import java.util.HashMap;


public class ValueComparator  implements Comparator<Integer>  {
	
	HashMap<Integer, Double> base;
	
	    public ValueComparator(HashMap<Integer, Double> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(Integer a, Integer b) {
	        if (base.get(a) < base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	
}
