package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

public class MaxPrefixLength extends Integer_Values{
	public MaxPrefixLength() {
		super();

		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(21));
		super.setTooltip("Placeholder");
	}
}
