package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
public class ClusteringType extends Discrete_Values {
	public ClusteringType(){
		super();
		Set <String> availableValues = new TreeSet<String>();
		availableValues.add("DBSCAN");
		availableValues.add("KMEANS");
		availableValues.add("AGGLOMERATIVE");
		availableValues.add("NONE");
//		availableValues.add("KMEANSPLUSPLUS");
		//availableValues.add("EM");
		availableValues.add("MODEL-BASED");
		this.setPossibleValues(availableValues);
		
		Map <String, List<String>> impliedFields = new HashMap<String, List<String>>();
		
		List<String> list = new ArrayList<String>();
		list.add("ClusterNumber");
		list.add("HierarchicalDistanceMetrics ");
		list.add("DistanceMetrics");
		list.add("ClusteringPatternType");
		impliedFields.put("AGGLOMERATIVE",list);
		
		list = new ArrayList<String>();
		impliedFields.put("NONE",list);

		list = new ArrayList<String>();
		list.add("ClusterNumber");
		list.add("ClusteringPatternType");
		impliedFields.put("KMEANS",list);
		
//		list = new ArrayList<String>();
//		list.add("ClusterNumber");
//		list.add("ClusteringPatternType");
//		impliedFields.put("KMEANSPLUSPLUS",list);

		list = new ArrayList<String>();
		list.add("ClusterNumber");
		impliedFields.put("MODEL-BASED",list);
		
		list = new ArrayList<String>();
		list.add("DBScanEpsilon");
		list.add("DBScanMinPoints");
		list.add("ClusteringPatternType");
		impliedFields.put("DBSCAN",list);

		this.setImpliedFields(impliedFields);
		
		super.addDefaultValue("DBSCAN");
		/*super.setTooltip("Clustering is the assignment of a set of observations \n"
				+ "into subsets (called clusters) so that observations in \n"
				+ "the same cluster are similar in some sense.");*/
		super.setTooltip("ClusteringType represents the type of clustering technique \n"
		+ "you choose to use for clustering training execution traces.");
		
		
	}
}