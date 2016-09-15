package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
//formula_sat_time
//questo va nella nuova scheda
public class PredictionTypeModule extends Discrete_Values{
	public PredictionTypeModule(){
		super();
		Set <String> availableValues = new TreeSet<String>();
		availableValues.add("FORMULA_SATISFACTION");
		availableValues.add("FORMULA_SATISFACTION_TIME");
		availableValues.add("FORMULA_SATISFACTION_AND_TIME");
		this.setPossibleValues(availableValues);
		
		Map <String, List<String>> impliedFields = new HashMap<String, List<String>>();
		
		List<String> list = new ArrayList<String>();
		list.add("PartitionMethod");
		list.add("NumberOfIntervals");
		impliedFields.put("FORMULA_SATISFACTION_TIME",list);
		
		list = new ArrayList<String>();
		list.add("PartitionMethod");
		list.add("NumberOfIntervals");
		impliedFields.put("FORMULA_SATISFACTION_AND_TIME",list);
		
		this.setImpliedFields(impliedFields);
		
		super.addDefaultValue("FORMULA_SATISFACTION_TIME");
		super.setTooltip("The type of prediction to carry out lets one choose weather to predict "
						+ "only the satisfaction of the inserted formula or the time it will take to \n"
						+ "accomplish the give constraint as well as to choose witch will be the \n"
						+ "format of the formula");
	}
}
