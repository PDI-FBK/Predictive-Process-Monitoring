package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

import java.util.Set;
import java.util.TreeSet;

//1-inf
//400
//forse è da togliere
public class ClusteringDiscriminativePatternCount extends Integer_Values{
	public ClusteringDiscriminativePatternCount() {
		super();

		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(400));
		super.setTooltip("Maximum number of trend sequences that can be defined as pattern");
		
		Set<String> dependendingFromFields = new TreeSet<String>();
		dependendingFromFields.add("DISCRIMINATIVE");
		this.setDependendingFromFields(dependendingFromFields);
	}
}