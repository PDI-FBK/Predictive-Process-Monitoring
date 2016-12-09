package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values;

import java.util.Set;
import java.util.TreeSet;
//max
//dipende da voting
public class ConfidenceAndSupportVotingStrategy extends Discrete_Values{
	public ConfidenceAndSupportVotingStrategy() {
		super();
		Set <String> availableValues = new TreeSet<String>();
		availableValues.add("MAX");
//		availableValues.add("MIN");
		this.setPossibleValues(availableValues);
		
		super.addDefaultValue("MAX");
		super.setTooltip("This parameter defines the way we choose the parameters to apply to an entity after it has been voted");
		
		Set <String> dependingFrom = new TreeSet<>();
		dependingFrom.add("UseVotingForClustering");
		super.setDependendingFromFields(dependingFrom);
	}
}
