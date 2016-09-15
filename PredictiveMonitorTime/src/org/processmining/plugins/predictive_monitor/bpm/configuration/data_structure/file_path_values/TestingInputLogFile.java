package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.file_path_values;

public class TestingInputLogFile extends File_Path_Values{
	public TestingInputLogFile() {
		super();
		
		super.addDefaultValue("input/old/BPI2011_20.xes");
		super.setTooltip("When an algorithm of classification is trained enough we have to discover\n"
				+ " how much that train had strengthened its capacity of accomplishing the task \n"
				+ "for what it had been trained in this way it's a good idea to use a different \n"
				+ "set of entities to query the algorithm and discover how much effective \n"
				+ "had been the training ");
	}
}
