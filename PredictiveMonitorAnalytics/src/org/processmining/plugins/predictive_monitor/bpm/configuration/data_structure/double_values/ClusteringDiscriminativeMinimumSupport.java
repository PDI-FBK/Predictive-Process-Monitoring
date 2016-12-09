package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values;

import java.util.Set;
import java.util.TreeSet;

public class ClusteringDiscriminativeMinimumSupport extends Double_Values{
	public ClusteringDiscriminativeMinimumSupport(){
		super();

		this.setBounds(new Double(0), new Double(Double.MAX_VALUE));
		super.addDefaultValue(new Double(0.8));
		super.setTooltip("In discriminative models we have a conditional probability distribution \n"
				+ "that point out witch is the likelihood that a given trace will pertain to a set, \n"
				+ " the minimum support is the minimum probability allowed to let a event be\n"
				+ " classified as pertaining to a set instead of another");
		
		Set <String> dependingFrom = new TreeSet<>();
//		dependingFrom.add("DISCR_SEQUENTIAL_WITHOUT_HOLES");
		super.setDependendingFromFields(dependingFrom);
	}

}
