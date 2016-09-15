package org.processmining.plugins.predictive_monitor.bpm.pattern_mining.sequence_mining;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ca.pfv.spmf_predictions.algorithms.sequentialpatterns.fournier2008_seqdim.SequenceDatabase;

public class StringSequenceDatabaseFV08 {

	private Map<String, Integer> alphabetMap;
	private SequenceDatabase sequenceDB;
	
	
	public StringSequenceDatabaseFV08() {
		alphabetMap = new HashMap<String, Integer>();
		sequenceDB = null;
	}


	public StringSequenceDatabaseFV08(Map<String, Integer> alphabetMap, SequenceDatabase sequenceDB) {
		this.alphabetMap = alphabetMap;
		this.sequenceDB = sequenceDB;
	}


	public Map<String, Integer> getAlphabetMap() {
		return alphabetMap;
	}


	public SequenceDatabase getSequenceDB() {
		return sequenceDB;
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
