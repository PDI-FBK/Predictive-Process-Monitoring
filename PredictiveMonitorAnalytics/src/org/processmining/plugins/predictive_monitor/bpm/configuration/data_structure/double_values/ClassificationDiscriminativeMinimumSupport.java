package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values;

import java.util.Set;
import java.util.TreeSet;

public class ClassificationDiscriminativeMinimumSupport extends Double_Values{
	public ClassificationDiscriminativeMinimumSupport(){
		super();

		this.setBounds(new Double(0), new Double(Double.MAX_VALUE));
		super.addDefaultValue(new Double(0.8));
		super.setTooltip("In discriminative models we have a conditional probability distribution \n"
						+ "that point out witch is the likelihood that a given trace will evolve in \n"
						+ " some way, the minimum support is the minimum probability allowed to let a \n"
						+ "event be classified as evolving in way instead of another");
		
		Set <String> dependingFrom = new TreeSet<>();
		dependingFrom.add("DISCRIMINATIVE");
//		dependingFrom.add("DISCR_SEQUENTIAL_WITHOUT_HOLES");
//		dependingFrom.add("DISCR_SEQUENTIAL_WITH_HOLES");
		super.setDependendingFromFields(dependingFrom);
	}

}
