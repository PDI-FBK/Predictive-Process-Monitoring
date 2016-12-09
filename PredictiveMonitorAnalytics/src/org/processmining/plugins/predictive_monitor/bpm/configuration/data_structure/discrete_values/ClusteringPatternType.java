package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
//none
public class ClusteringPatternType extends Discrete_Values{
	public ClusteringPatternType(){
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
		list.add("clusteringDiscriminativePatternMinumSupport");
		list.add("clusteringDiscriminativePatternCount");
		list.add("clusteringSameLengthDiscriminativePatternCount");
		impliedFields.put("DISCRIMINATIVE",list);
		
//		list = new ArrayList<String>();
//		list.add("clusteringPatternMinimumSupport");
//		list.add("clusteringMinimumPatternLength");
//		list.add("clusteringDiscriminativeMinimumSupport");
//		impliedFields.put("DISCR_SEQUENTIAL_WITHOUT_HOLES",list);

//		list = new ArrayList<String>();
//		list.add("classificationPatternMinimumSupport");
//		list.add("classificationMinimumPatternLength");
//		list.add("classificationMaximumPatternLength");
//		list.add("classificationDiscriminativeMinimumSupport");
//		impliedFields.put("DISCR_SEQUENTIAL_WITH_HOLES",list);
		
		this.setImpliedFields(impliedFields);
		
		super.addDefaultValue("NONE");
		super.setTooltip("It happens that a set of items could be viewed as a set of subsets \n"
				+ "depending on the level of the detail, discriminating the subsets \n"
				+ "can be a hard job due to the grade of heterogeneity, in this way "
				+ "a lot of tecniques could be used to find them effectively");
	}
}
