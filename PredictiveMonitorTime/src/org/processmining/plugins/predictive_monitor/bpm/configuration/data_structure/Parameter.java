package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Parameter{

	private Set<Object> selectedValues;
	private Set<Object> defaultValues;
	private String tooltip;
	private Set <String> dependendingFromFields;
	private Map <String, List<String>> impliedFields;
	public boolean onGUI;

	public Parameter(TreeSet<Object> treeSet) {
		selectedValues = treeSet;
		defaultValues = new TreeSet<Object>();
		dependendingFromFields = new TreeSet<>();
		impliedFields = new HashMap<String, List<String>>();
		onGUI = false;
	}

	public Set<Object> getSelectedValues() {
		return ((selectedValues.size() > 0 && selectedValues != null) ? selectedValues : defaultValues);
	}

	public void setSelectedValues(Set<Object> selectedValues) {
		this.selectedValues = selectedValues;
	}
	
	public void addSelectedValue(Object selectedValue) {
		selectedValues.add(selectedValue);
	}
	public void addDefaultValue(Object def) {
		defaultValues.add(def);
	}
	
	//spike solution
	public Object getDefaultValue(){
		return defaultValues.iterator().next();
	}
	
	public boolean removeValue(Object value){
		return selectedValues.remove(value);
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	
	public Map<String, List<String>> getImpliedFields() {
		return impliedFields;
	}

	public void setImpliedFields(Map<String, List<String>> impliedFields) {
		this.impliedFields = impliedFields;
	}

	public Set<String> getDependendingFromFields() {
		return dependendingFromFields;
	}

	public void setDependendingFromFields(Set<String> dependendingFromFields) {
		this.dependendingFromFields = dependendingFromFields;
	}

	public void setSingleValue(Boolean newVal) {
		selectedValues = new TreeSet<>();
		selectedValues.add(newVal);		
	}

	/*@Override
	public boolean hasMoreElements() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E nextElement() {
		// TODO Auto-generated method stub
		return null;
	}*/
}
