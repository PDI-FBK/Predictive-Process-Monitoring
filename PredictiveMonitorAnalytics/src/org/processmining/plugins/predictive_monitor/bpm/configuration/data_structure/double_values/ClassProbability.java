package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values;

public class ClassProbability extends Double_Values{
	//0-1
	//.6
	public ClassProbability() {
		super();
		
		super.setBounds(new Double(0), new Double(1));
		super.addDefaultValue(new Double(.6));
		super.setTooltip("Some events have more chances than others to happen, \n"
						+ "the probability is the minimum estimated chance needed \n"
						+ " by an event to occuor to be classified as fulfilling \n"
						+ "a given constraint");
	}
}
