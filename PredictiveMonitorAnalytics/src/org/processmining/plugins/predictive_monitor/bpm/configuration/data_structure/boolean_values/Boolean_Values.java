package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values;

import java.util.Set;
import java.util.TreeSet;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;

public abstract class Boolean_Values extends Parameter {
	
	protected Boolean_Values(){
		super(new TreeSet<Object>());
	}
	
	public void addValue(Boolean newVal){
		super.addSelectedValue(newVal);
	}
	
	public Set<Object> getValues(){
		return super.getSelectedValues();
	}
	
	public void setSingleValue(Boolean newVal){
		super.setSingleValue(newVal);
	}

}
