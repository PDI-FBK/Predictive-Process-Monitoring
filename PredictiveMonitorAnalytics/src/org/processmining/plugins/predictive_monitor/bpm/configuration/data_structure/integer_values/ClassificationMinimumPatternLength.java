package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

import java.util.Set;
import java.util.TreeSet;

//discriminative sequential 
//sequential with holes and without holes
//disattiva la possibilità di scegliere questo valore per intervallo
//1-inf
//1
public class ClassificationMinimumPatternLength extends Integer_Values{
	public ClassificationMinimumPatternLength() {
		super();
		
		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(1));
		super.setTooltip("Minimum length of a trend sequence to define it pattern");
		
		Set<String> dependendingFromFields = new TreeSet<String>();
//		dependendingFromFields.add("DISCR_SEQUENTIAL_WITH_HOLES");
//		dependendingFromFields.add("DISCR_SEQUENTIAL_WITHOUT_HOLES");
		this.setDependendingFromFields(dependendingFromFields);
	}
}
