package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values;

public class TrainingFile extends Discrete_Values{	
	public TrainingFile() {
		super();
		
		super.addDefaultValue("old/BPI2011_20.xes");
		super.setTooltip("Training data consists of a set of traces that will be fully analized \n"
				+ "to discover execution trends that will be then adopted to make prediction on future\n"
				+ " traces");
	}
}
