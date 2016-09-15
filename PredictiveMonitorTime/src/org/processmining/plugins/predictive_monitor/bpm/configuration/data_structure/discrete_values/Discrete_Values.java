package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;


public abstract class Discrete_Values extends Parameter {
	private Set <String> possibleValues;
	
	protected Discrete_Values(){
		super(new TreeSet<Object>());
		possibleValues = new TreeSet<String>();
	}
	
	public void setPossibleValues(Set<String> newValues){
		possibleValues = newValues;
	}
	
	public void addValue (String selectedValue){
		for(String i : possibleValues)
			if(i.equals(selectedValue))
				super.addSelectedValue(selectedValue);
	}
	
	public Set<Object> getValues(){
		return super.getSelectedValues();
	}
	
	public Set<String> getPossibleValues(){
		return possibleValues;
	}

	public void setDefaultValue(Object def){
		super.addDefaultValue(def);
	}
	
}