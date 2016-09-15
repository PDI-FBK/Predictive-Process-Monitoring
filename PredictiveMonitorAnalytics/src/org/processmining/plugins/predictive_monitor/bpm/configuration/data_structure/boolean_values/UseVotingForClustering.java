package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//false
public class UseVotingForClustering extends Boolean_Values{
	public UseVotingForClustering() {
		super();
		
Map <String, List<String>> impliedFields = new HashMap<String, List<String>>();
		
		List<String> list = new ArrayList<String>();
		list.add("ConfidenceAndSupportVotingStrategy");
		list.add("Voters");
		impliedFields.put("true",list);
		
		this.setImpliedFields(impliedFields);
		
		super.addDefaultValue(false);
		super.setTooltip("Voting for clustering is a method that allows every item \n"
				+ "in the set that could pertain to a different clusters to choose \n"
				+ "all the possibilities and then after classification be assigned \n"
				+ "only to nearest one. This method is now implementd only for \n"
				+ "model based clustering method, more implementation will come \n"
				+ "in a while.");
	}
}
