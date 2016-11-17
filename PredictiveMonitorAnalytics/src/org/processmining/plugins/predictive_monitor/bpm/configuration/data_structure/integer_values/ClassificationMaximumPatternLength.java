 package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

import java.util.Set;
import java.util.TreeSet;

//sequential with holes
//disattiva la possibilità di scegliere questo valore per intervallo
//1-inf
//4
public class ClassificationMaximumPatternLength extends Integer_Values {
	public ClassificationMaximumPatternLength() {
		super();

		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(4));
		super.setTooltip("When a trace is analized searching for patterns it happens that one can find\n"
				+ " occurrences of trends of different length, this parameter tell the algorithm \n"
				+ "how long at most a trend can be");
		
		Set<String> dependendingFromFields = new TreeSet<String>();
//		dependendingFromFields.add("DISCR_SEQUENTIAL_WITH_HOLES");
//		dependendingFromFields.add("DISCR_SEQUENTIAL_WITHOUT_HOLES");
		this.setDependendingFromFields(dependendingFromFields);
	}
}

