package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

public class EvaluationStartPoint extends Integer_Values{
	
	public EvaluationStartPoint() {
		super();

		this.setBounds(new Integer(0), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(0));
		//super.setTooltip("Placeholder");
		super.setTooltip("EvaluationStartPoint represents the first event from which to start \n"
				+ "with the evaluation of a trace in order to reach minimum class probability and support.");
	}

}
