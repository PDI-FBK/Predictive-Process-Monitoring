package org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures;

import java.util.ArrayList;
import java.util.Set;

public class Pattern {

	private ArrayList<String> items;
	private double earlinessDegree;
	private Set<Integer> sequencesID;

	public ArrayList<String> getItems() {
		return items;
	}

	public void setItems(ArrayList<String> items) {
		this.items = items;
	}

	public double getEarlinessDegree() {
		return earlinessDegree;
	}

	public void setEarlinessDegree(double earlinessDegree) {
		this.earlinessDegree = earlinessDegree;
	}

	public Set<Integer> getSequencesID() {
		return sequencesID;
	}

	public void setSequencesID(Set<Integer> sequencesID) {
		this.sequencesID = sequencesID;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (String string : items) {
			builder.append(string);
			builder.append("|");
		}
		builder.deleteCharAt(builder.length()-1);
		return builder.toString();
	}

	
}
