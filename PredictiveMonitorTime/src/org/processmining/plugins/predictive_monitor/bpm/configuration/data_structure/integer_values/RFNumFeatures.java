package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

import java.util.Set;
import java.util.TreeSet;
//random forest
//0-inf
//0
public class RFNumFeatures extends Integer_Values {
	public RFNumFeatures(){
		super();
		
		this.setBounds(new Integer(0), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(0));
		super.setTooltip("Placeholder");
		
		Set<String> dependendingFromFields = new TreeSet<String>();
		dependendingFromFields.add("RANDOM_FOREST");
		this.setDependendingFromFields(dependendingFromFields);
	}
}
