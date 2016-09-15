package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

public class PrefixGap extends Integer_Values{
	public PrefixGap() {
		super();

		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(5));
		super.setTooltip("Placeholder");
	}
}
