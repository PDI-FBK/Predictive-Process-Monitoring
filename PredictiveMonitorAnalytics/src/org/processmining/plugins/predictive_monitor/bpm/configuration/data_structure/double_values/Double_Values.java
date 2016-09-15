package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;

public abstract class Double_Values extends Parameter {
	private Double lowerBound;
	private Double upperBound;
	
	protected Double_Values(){
		super(new TreeSet<Object>());
	}
	
	public void setBounds(Double lowerBound, Double upperBound){
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	public void addValue (Double selectedValue){
		if(selectedValue >= lowerBound && selectedValue <= upperBound)
			super.addSelectedValue(selectedValue);
	}
	public Set<Object> getValues(){
		return super.getSelectedValues();
	}
	public Double getLowerBound(){
		return lowerBound;
	}
	public Double getUpperBound(){
		return upperBound;
	}
	
}
