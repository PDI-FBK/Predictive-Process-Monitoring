package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values;

import java.util.Set;
import java.util.TreeSet;
//TODO : to be fixed
public class ModelClusteringFrom extends Discrete_Values{
	public ModelClusteringFrom(){
		super();
		Set <String> availableValues = new TreeSet<String>();
		availableValues.add("R");
		availableValues.add("EXTERNAL_INPUT_FILE");
		this.setPossibleValues(availableValues);
		
		super.addDefaultValue("EXTERNAL_INPUT_FILE");
		super.setTooltip("This functionality isn't yet implemented");

		Set <String> dependingFrom = new TreeSet<>();
		dependingFrom.add("MODEL");
		super.setDependendingFromFields(dependingFrom);
	}
}