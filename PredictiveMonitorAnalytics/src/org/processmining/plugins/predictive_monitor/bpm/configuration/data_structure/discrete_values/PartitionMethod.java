package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values;

import java.util.Set;
import java.util.TreeSet;
//fixed sorted division
//dipende da formula satisfaction time
public class PartitionMethod extends Discrete_Values{
	
	public PartitionMethod() {
		super();
		Set <String> availableValues = new TreeSet<String>();
		availableValues.add("MAX_MINUS_MIN_OVER_N");
		availableValues.add("NORMAL_TIME_DISTRIBUTION");
		availableValues.add("SORTED_DIVISION");
		availableValues.add("FIXED_SORTED_DIVISION");
		this.setPossibleValues(availableValues);
		
		super.addDefaultValue("FIXED_SORTED_DIVISION");
		super.setTooltip("This parameter is strictly correlated to the prediction mode \"Formual Satisfaction Time\"\n"
						 + "In formula satisfactin time prediction mode one will receive predictions trying that point out \n"
						 + " in how many time the trace will accomplish the given formula, this amount of time will be \n"
						 + "expressed in time intervals. The number of this time intervals depends on \"Number of Intervals\" \n"
						 + " field. This intervals of time are computed can be computed in many different ways, particularly \n"
						 + "one can try to get the same amount of traces in each time interval, to accomplish this task we \n"
						 + "create a set of different tecniques: \n"
						 + "-Max Minus Over N: Intervals are computed dividing the duration of the longest trace into\n"
						 + " \"Number of Intervals\" parts \n"
						 + "-Normal Time Distibution: Intervals are created according to gaussian distribution, assuming\n"
						 + " that each event has the same probability to accomplish the formula\n"
						 + "-Sorted Division: Intervals are created sorting every possible time distance from the first \n"
						 + "event and then splitting them into \"Number of Intervals\" groups and taking the first of each group\n"
						 + "-Fixed Sorted Division: Similar to sorted division, but the last interval is shorter then the others\n");

		Set <String> dependingFrom = new TreeSet<>();
		dependingFrom.add("FORMULA_SATISFACTION_TIME");
		super.setDependendingFromFields(dependingFrom);
	}
}
