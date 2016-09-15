package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values;

import java.util.Set;
import java.util.TreeSet;
//0-1
//.1
public class DBscanEpsilon extends Double_Values{
	public DBscanEpsilon(){
		super();

		this.setBounds(new Double(0), new Double(1));
		super.addDefaultValue(new Double(.26));
		/*super.setTooltip("Plotting a set of entities could be a good idea to use elements\n"
				+ " like cartesian distance to discover how much an element is\n"
				+ " close - in terms of similarities depending on the way we are\n"
				+ " plotting the entities - to their neighbours. The cartesian maximum distance between two entities to consider them pertaining to the same cluster");*/
		super.setTooltip("DBScan epsilon is a parameter of the DBSCAN clustering technique. \n"
		+ " It intuitively represents the maximum allowed distance between two elements of the same cluster");
		
		Set<String> dependendingFromFields = new TreeSet<String>();
		dependendingFromFields.add("DBSCAN");
		this.setDependendingFromFields(dependendingFromFields);
	}
}