package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.string_values;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;

public  abstract class String_Values extends Parameter{
	
	public String_Values() {
		super(new TreeSet<Object>());
	}
	
	public void addValue (String selectedValue){
		super.addSelectedValue(selectedValue);
	}
	public Set<Object> getValues(){
		return super.getSelectedValues();
	}
	
}
