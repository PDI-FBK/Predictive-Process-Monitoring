package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values;

import java.util.Set;
import java.util.TreeSet;
//1-inf
//6
// dipende da dbscan
public class DBScanMinPoints extends Integer_Values{
	public DBScanMinPoints(){
		super();
		
		this.setBounds(new Integer(1), new Integer(Integer.MAX_VALUE));
		super.addDefaultValue(new Integer(4));
		//super.setTooltip("Placeholder");
		super.setTooltip("DBScanMinPoints is a parameter of the DBSCAN clustering technique. \n"
				+ "It intuitively represets the minimum number of elements a cluster has to have.");
		
		Set<String> dependendingFromFields = new TreeSet<String>();
		dependendingFromFields.add("DBSCAN");
		this.setDependendingFromFields(dependendingFromFields);
	}
}