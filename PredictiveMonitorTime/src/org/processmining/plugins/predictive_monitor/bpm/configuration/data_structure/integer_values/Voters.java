package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

import java.util.Set;
import java.util.TreeSet;

//usevotingforclustering
//1-inf
//3
public class Voters extends Integer_Values{
	public Voters() {
		super();
		
		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(3));
		super.setTooltip("Placeholder");
		
		Set<String> dependendingFromFields = new TreeSet<String>();
		dependendingFromFields.add("useVotingForClustering");
		this.setDependendingFromFields(dependendingFromFields);
	}
}
