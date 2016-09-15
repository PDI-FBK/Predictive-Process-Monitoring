package org.processmining.plugins.predictive_monitor.bpm.pattern_mining.sequence_mining;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class StringSequenceDatabaseClasP {
	
	private Map<String, Integer> alphabetMap;
	private ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase sequenceDB;
	private double support;
	
	
	public StringSequenceDatabaseClasP() {
		alphabetMap = new HashMap<String, Integer>();
		sequenceDB = null;
	}


	public StringSequenceDatabaseClasP(Map<String, Integer> alphabetMap, ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase sequenceDB, double support) {
		this.alphabetMap = alphabetMap;
		this.sequenceDB = sequenceDB;
		this.support = support;
	}


	public Map<String, Integer> getAlphabetMap() {
		return alphabetMap;
	}


	public ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase getSequenceDB() {
		return sequenceDB;
	}
	
	
	public double getSupport() {
		return support;
	}


	public String getMappedString(int index){
		String searchedString = null;
		for (Iterator iterator = alphabetMap.keySet().iterator(); iterator.hasNext() && searchedString==null;) {
			String mappedString = (String) iterator.next();
			Integer mappedIndex = alphabetMap.get(mappedString);
			if (mappedIndex.intValue()==index)
				searchedString = mappedString;
			
		}
		return searchedString;
	}


}
