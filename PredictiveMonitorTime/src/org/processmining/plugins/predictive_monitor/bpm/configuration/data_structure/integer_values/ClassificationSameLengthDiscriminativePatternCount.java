package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

import java.util.Set;
import java.util.TreeSet;

//discriminative
//1-inf
//399
public class ClassificationSameLengthDiscriminativePatternCount extends Integer_Values{
	public ClassificationSameLengthDiscriminativePatternCount() {

		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(399));
		super.setTooltip("This parameter let's one decide witch should be the maximum number of occurrences of same length pattern in a classification");
		
		Set<String> dependendingFromFields = new TreeSet<String>();
		dependendingFromFields.add("DISCRIMINATIVE");
		this.setDependendingFromFields(dependendingFromFields);
	}
}
