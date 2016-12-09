package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

import java.util.Set;
import java.util.TreeSet;

//discriminative sequential 
//sequential with holes and without holes
//disattiva la possibilità di scegliere questo valore per intervallo
//1-inf
//1
public class ClusteringMinimumPatternLength extends Integer_Values{
	public ClusteringMinimumPatternLength() {
		super();
		
		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(1));
		
		//super.setDependendingFromFields("SEQUENTIAL_WITH_HOLES");
		//super.setDependendingFromFields("SEQUENTIAL_WITHOUT_HOLES");
		super.setTooltip("When one is analizing a trace searching for patterns it happens\n"
				+ " that he can find variuos dimension trends this parameter tell \n"
				+ "the algorithm a trend has to be to result acceptable");
		
		Set <String> dependingFrom = new TreeSet<>();
//		dependingFrom.add("DISCR_SEQUENTIAL_WITHOUT_HOLES");
//		dependingFrom.add("DISCR_SEQUENTIAL_WITH_HOLES");
		super.setDependendingFromFields(dependingFrom);
	}
}
