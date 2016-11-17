package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
//none
//lo devo mettere in classification nell'interfaccia
public class ClassificationPatternType extends Discrete_Values{
	public ClassificationPatternType(){
		super();
		Set <String> availableValues = new TreeSet<String>();
		availableValues.add("DISCRIMINATIVE");
//		availableValues.add("SEQUENTIAL_WITH_HOLES");
//		availableValues.add("SEQUENTIAL_WITHOUT_HOLES");
//		availableValues.add("DISCR_SEQUENTIAL_WITH_HOLES");
//		availableValues.add("DISCR_SEQUENTIAL_WITHOUT_HOLES");
		availableValues.add("NONE");
		this.setPossibleValues(availableValues);
		
		Map <String, List<String>> impliedFields = new HashMap<String, List<String>>();
		
		List<String> list = new ArrayList<String>();
		list.add("classificationDiscriminativePatternMinimumSupport");
		list.add("classificationDiscriminativePatternCount");
		list.add("classificationSameLengthDiscriminativePatternCount");
		impliedFields.put("DISCRIMINATIVE",list);
		
//		list = new ArrayList<String>();
//		list.add("classificationPatternMinimumSupport");
//		list.add("classificationMinimumPatternLength");
//		list.add("classificationDiscriminativeMinimumSupport");
//		impliedFields.put("DISCR_SEQUENTIAL_WITHOUT_HOLES",list);
		
//		list = new ArrayList<String>();
//		list.add("classificationPatternMinimumSupport");
//		list.add("classificationMinimumPatternLength");
//		list.add("classificationMaximumPatternLength");
//		list.add("classificationDiscriminativeMinimumSupport");
//		impliedFields.put("DISCR_SEQUENTIAL_WITH_HOLES",list);
		
		this.setImpliedFields(impliedFields);
		
		super.addDefaultValue("NONE");
		super.setTooltip("Analizing entities from a set can emerge that some have similar trends, \n"
				+ "in order to Classification Pattern Type lets one decide witch algorithm\n"
				+ " to use in order to discover this similarities");
	}

}
