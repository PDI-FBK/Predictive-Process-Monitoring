package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values;

import java.util.Set;
import java.util.TreeSet;
//euclidean
// dipende da clustering type agglomerative
public class HierarchicalDistanceMetrics extends Discrete_Values{
	public HierarchicalDistanceMetrics(){
		super();
		Set <String> availableValues = new TreeSet<String>();
		availableValues.add("EDIT_DISTANCE");
		availableValues.add("EUCLIDEAN");
		this.setPossibleValues(availableValues);
		
		super.addDefaultValue("EUCLIDEAN");
		super.setTooltip("This parameter let's one decide witch strategy of measuring the distance between two or more entities is used");
		
		Set <String> dependingFrom = new TreeSet<>();
		dependingFrom.add("AGGLOMERATIVE");
		super.setDependendingFromFields(dependingFrom);
	}
}