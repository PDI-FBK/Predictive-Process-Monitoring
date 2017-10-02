package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

import java.util.Set;
import java.util.TreeSet;
//1-inf
//20
public class ClusterNumber extends Integer_Values{
	public ClusterNumber (){
		super();

		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(20));
		super.setTooltip("Every set of entities usually hold in subsets made by common similarities,\n"
					+ " the number of subsets may vary on the level of detail we analize them, this\n"
					+ " level of detail could be expressed also by the number of subsets we find. \n"
					+ "Cluster number is exactly the number of clusters we want to find in our set");
		
		Set dependendingFromFields = new TreeSet<String>();
		dependendingFromFields.add("KMEANS");
		dependendingFromFields.add("AGGLOMERATIVE");
		dependendingFromFields.add("EM");
		dependendingFromFields.add("MODEL");
//		dependendingFromFields.add("KMEANSPLUSPLUS");
		this.setDependendingFromFields(dependendingFromFields);
	}
}