package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values;
//false
//da togliere
public class PrintDebug extends Boolean_Values{
	public PrintDebug() {
		super();
		
		super.addDefaultValue(false);
		super.setTooltip("Choose weather to print to an external file debug information or not");
	}
}
