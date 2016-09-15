package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
//random_forest
public class ClassificationType extends Discrete_Values{
	public ClassificationType(){
		super();
		Set <String> availableValues = new TreeSet<String>();
		availableValues.add("DECISION_TREE");
		availableValues.add("RANDOM_FOREST");
		this.setPossibleValues(availableValues);
		
Map <String, List<String>> impliedFields = new HashMap<String, List<String>>();
		
		List<String> list = new ArrayList<String>();
		list.add("rFMaxDepth");
		list.add("rFNumFeatures");
		list.add("rFNumTrees");
		list.add("rfSeed");
		impliedFields.put("RANDOM_FOREST",list);
		
		this.setImpliedFields(impliedFields);
		
		super.addDefaultValue("RANDOM_FOREST");
		super.setTooltip("Classification methods allows one to choose \n"
				+ "identify to which set of categories\n"
				+ " (sub-populations) a new observation belongs,\n"
				+ " on the basis of a training set of data containing\n"
				+ " observations (or instances) whose category membership is known.");
	}
}
