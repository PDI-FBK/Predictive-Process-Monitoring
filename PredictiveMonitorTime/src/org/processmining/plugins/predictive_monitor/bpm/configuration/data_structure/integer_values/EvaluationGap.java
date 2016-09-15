package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;
//1-inf
//5
public class EvaluationGap extends Integer_Values{
	 public EvaluationGap() {
		super();
		
		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(5));
		//super.setTooltip("Placeholder");
		super.setTooltip("EvaluationGap represents the gap (in terms of number of events) \n"
				+ "to use when evaluating a trace up to the achievement of the "
				+ "minimum support and confidence values");
	}
}
