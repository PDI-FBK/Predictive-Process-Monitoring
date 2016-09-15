package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values;

import java.util.Set;
import java.util.TreeSet;

//0-1
//discrminative sequential with holes su clustering
public class ClusteringPatternMinimumSupport extends Double_Values{
	public ClusteringPatternMinimumSupport() {
		super();
		
		this.setBounds(new Double(0), new Double(1));
		super.addDefaultValue(new Double(0.6));
		super.setTooltip("To say that a pattern is accepted it has to be supported, in the sense of present in at least the value of this parameter");
		
		Set <String> dependingFrom = new TreeSet<>();
		dependingFrom.add("DISCRIMINATIVE_SEQUENTIAL_WITH_HOLES");
		super.setDependendingFromFields(dependingFrom);
	}

}
