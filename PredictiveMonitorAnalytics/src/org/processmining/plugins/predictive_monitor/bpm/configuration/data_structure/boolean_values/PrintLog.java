package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values;
//false
//
public class PrintLog extends Boolean_Values{
	public PrintLog() {
		super();
		
		super.addDefaultValue(false);
		super.setTooltip("Choose eather to export a log file containing information about execution or not");
	}
}
