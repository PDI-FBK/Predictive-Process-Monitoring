package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values;
//implementato da Willy
//dipendente dalla simple declare
//va messo dove scelgo il tipo di prediction
public class TimeFromLastEvent extends Boolean_Values {
	public TimeFromLastEvent() {
		super();
		
		super.addDefaultValue(false);
		//super.addDependingFromFields("SimpleDeclare");
		super.setTooltip("Choose whether to start the evaluation from the beginning of the trace or from the current index position");
	}
}
