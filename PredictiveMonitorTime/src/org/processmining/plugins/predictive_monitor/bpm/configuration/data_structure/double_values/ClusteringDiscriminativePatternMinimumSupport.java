package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values;

import java.util.Set;
import java.util.TreeSet;

public class ClusteringDiscriminativePatternMinimumSupport extends Double_Values {
	//TODO chiedere a Chiara
		public ClusteringDiscriminativePatternMinimumSupport() {
			super();

			this.setBounds(new Double(0), new Double(1));
			super.addDefaultValue(new Double(0.1));

			//this.setBounds(upperBound, lowerBound);
			super.setTooltip("Searching for similar items into a set to create a subset of it\n"
					+ " can lead to searching for entities that share some parts of the\n"
					+ " control flow coposition, this parameter let's one discriminate\n"
					+ " what trend means in order of time of recurrences to be classified \n"
					+ "as pattern");
			
			Set <String> dependingFrom = new TreeSet<>();
			dependingFrom.add("ClusteringPatternType");
			super.setDependendingFromFields(dependingFrom);
		}

}
