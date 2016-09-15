package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;

public abstract class Integer_Values extends Parameter {
	private Integer lowerBound;
	private Integer upperBound;

	
	protected Integer_Values(){
		super(new TreeSet<Object>());
	}
	
	public void setBounds(Integer lowerBound, Integer upperBound){
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	public void addValue (Integer selectedValue){
		if(selectedValue >= lowerBound && selectedValue <= upperBound)
			super.addSelectedValue(selectedValue);
		else System.out.println("Error processing number, it's Out Of Bounds!");
	}
	public Set<Object> getValues(){
		return super.getSelectedValues();
	}
	public Integer getLowerBound(){
		return lowerBound;
	}
	public Integer getUpperBound(){
		return upperBound;
	}
}
