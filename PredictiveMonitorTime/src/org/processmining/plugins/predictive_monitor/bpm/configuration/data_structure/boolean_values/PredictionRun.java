package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values;

//valutazione precision e ..
public class PredictionRun extends Boolean_Values{
	public PredictionRun() {
		super();
		
		super.addDefaultValue(false);
		super.setTooltip("Choose if the run should be a evaluation or a prediction run");
	}
}
