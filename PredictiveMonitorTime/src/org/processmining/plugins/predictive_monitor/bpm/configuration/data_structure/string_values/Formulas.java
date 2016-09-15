package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.string_values;

public class Formulas extends String_Values{
	public Formulas() {
		super();
		
		super.addDefaultValue("(  <>(\"tumor marker CA-19.9\") ) \\/ ( <> (\"ca-125 using meia\") )  ");
		//super.setDependendingFromFields("SimpleDeclare");
		super.setTooltip("Placeholder");
	}
}
