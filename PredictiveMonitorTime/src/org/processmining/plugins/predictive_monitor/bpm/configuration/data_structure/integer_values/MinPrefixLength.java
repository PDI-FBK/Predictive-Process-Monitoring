package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;
//1-inf
//default 1-21 gap 5
public class MinPrefixLength extends Integer_Values{
	public MinPrefixLength() {
		super();

		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(1));
		super.setTooltip("Placeholder");
	}
}
