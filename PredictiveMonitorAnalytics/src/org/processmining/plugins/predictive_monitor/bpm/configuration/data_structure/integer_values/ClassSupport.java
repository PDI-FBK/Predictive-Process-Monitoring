package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;
//1-inf
//4
public class ClassSupport extends Integer_Values{
	public ClassSupport() {
		super();
		
		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(4));
		super.setTooltip("Minimum number of labelled entities that shares control flow similarities\n"
				+ " with analized entity in order to make or not the prediction based\n"
				+ " on the label of the supporting entities");
	}
}
