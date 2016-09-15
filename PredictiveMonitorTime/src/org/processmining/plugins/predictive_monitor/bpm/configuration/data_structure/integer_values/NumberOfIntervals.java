package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

import java.util.Set;
import java.util.TreeSet;

//patition method time
//1-inf
//5
public class NumberOfIntervals extends Integer_Values{
	public NumberOfIntervals() {
		super();

		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(5));	
		super.setTooltip("Placeholder");
		
		Set<String> dependendingFromFields = new TreeSet<String>();
		dependendingFromFields.add("PREDICTION_SATISFACTION_TIME");
		this.setDependendingFromFields(dependendingFromFields);

	}
}
