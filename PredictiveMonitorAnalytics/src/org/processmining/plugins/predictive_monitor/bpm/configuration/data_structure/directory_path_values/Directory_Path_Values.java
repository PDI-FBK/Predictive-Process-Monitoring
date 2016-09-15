package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.directory_path_values;

import java.util.Set;
import java.util.TreeSet;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;

public  abstract class Directory_Path_Values extends Parameter{
	
	protected Directory_Path_Values(){
		super(new TreeSet<Object>());
	}
	
	public void addValue (String selectedValue){
		super.addSelectedValue(selectedValue);
	}
	public Set<Object> getValues(){
		return super.getSelectedValues();
	}
	
}
