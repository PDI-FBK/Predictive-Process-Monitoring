package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

import java.util.Set;
import java.util.TreeSet;

//discriminative
//1-inf
//399
public class ClusteringSameLengthDiscriminativePatternCount extends Integer_Values{
	public ClusteringSameLengthDiscriminativePatternCount() {
		
		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(400));
		
		//super.setDependendingFromFields("DISCTIMINATIVE");
		super.setTooltip("This parameter let's one decide witch should be\n"
				+ " the maximum number of pattern of the same length");
		
		Set<String> dependendingFromFields = new TreeSet<String>();
		dependendingFromFields.add("DISCRIMINATIVE");
		this.setDependendingFromFields(dependendingFromFields);
	}
}
