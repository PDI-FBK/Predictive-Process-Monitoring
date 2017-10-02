package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values;

import java.util.Set;
import java.util.TreeSet;

//0-1
//discriminative sequential with holes su classification
public class ClassificationPatternMinimumSupport extends Double_Values{
	public ClassificationPatternMinimumSupport() {
		super();

		this.setBounds(new Double(0), new Double(1));
		
		super.addDefaultValue(new Double(0.6));
		//super.setDependendingFromFields("DISCRIMINATIVE_SEQUENTIAL_WITH_HOLES");
		super.setTooltip("Analizing trends in order to discover similarities in control flows\n"
				+ " one could say that saying that something is trending is a\n"
				+ " questionable thing, with this parameters we can define exactly\n"
				+ " what trend means");
		
		Set <String> dependingFrom = new TreeSet<>();
//		dependingFrom.add("DISCR_SEQUENTIAL_WITHOUT_HOLES");
//		dependingFrom.add("DISCR_SEQUENTIAL_WITH_HOLES");
		super.setDependendingFromFields(dependingFrom);
	}

}
