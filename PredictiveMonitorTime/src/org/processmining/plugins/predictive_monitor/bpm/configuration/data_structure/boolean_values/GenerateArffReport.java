package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values;

public class GenerateArffReport extends Boolean_Values{
	public GenerateArffReport() {
		super();
		
		super.addDefaultValue(false);
		super.setTooltip("Choose if generate or not the .arff instances log that can be analized with Weka machine learning suite.");
	}
}
