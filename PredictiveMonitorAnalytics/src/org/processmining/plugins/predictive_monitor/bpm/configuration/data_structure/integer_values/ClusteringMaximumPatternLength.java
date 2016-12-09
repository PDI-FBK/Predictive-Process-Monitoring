package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

import java.util.Set;
import java.util.TreeSet;

//sequential with holes
//disattiva la possibilità di scegliere questo valore per intervallo
//1-inf
//4
public class ClusteringMaximumPatternLength extends Integer_Values {
	public ClusteringMaximumPatternLength() {
		super();

		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(4));
		
		super.setTooltip("This parameter let's one choose the maximum dimension of a single pattern");
		
		Set <String> dependingFrom = new TreeSet<>();
//		dependingFrom.add("SEQUENTIAL_WITH_HOLES");
		super.setDependendingFromFields(dependingFrom);
	}
}
