package org.processmining.plugins.predictive_monitor.bpm.utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Pair;


public class DataSnapshotListener implements ReplayerListener {
	Map<String, Map<String, List<Pair<Integer, Map<String, Object>>>>> instances;
	Map<String, Class<?>> dataTypes;

	//Set<String> activityLabels;
	List<Pair<String,Pair<Integer, Map<String,Object>>>> records;
	Map<String, Set<String>> stringDomains;

	Map<String, Object> assignment = null;
	String traceId = null;
	private Set<String> frequentActivations;

	public DataSnapshotListener(Map<String, Class<?>> dataTypes, Set<String> activityLabels) {
		this.instances = new HashMap<String, Map<String,List<Pair<Integer,Map<String,Object>>>>>();
	//	this.activityLabels = activityLabels;
		this.dataTypes = dataTypes;

		this.stringDomains = new HashMap<String, Set<String>>();
		for (String varname: dataTypes.keySet())
			if (dataTypes.get(varname).equals(String.class) || dataTypes.get(varname).equals(Boolean.class))
				stringDomains.put(varname, new HashSet<String>());
	}

	@Override
	public void openTrace(XAttributeMap attribs, String traceId,Set<String> frequentActivations) {
		this.frequentActivations = frequentActivations;
		this.traceId = traceId;
		this.assignment = new HashMap<String, Object>();
		this.records = new LinkedList<Pair<String, Pair<Integer, Map<String,Object>>>>();

		for (String varname: dataTypes.keySet())
			assignment.put(varname, null);		
		updateSnapshot(attribs, assignment);
	}

	@Override
	public void closeTrace(XAttributeMap attribs, String traceId) {
		copyRecords(instances, traceId, records);
	}

	@Override
	public void processEvent(XAttributeMap attribs, int index) {
		String activityName = attribs.get(XConceptExtension.KEY_NAME).toString();//+"-"+attribs.get(XLifecycleExtension.KEY_TRANSITION);
		updateSnapshot(attribs, assignment);
		records.add(new Pair<String, Pair<Integer,Map<String,Object>>>(activityName, new Pair<Integer, Map<String,Object>>(index++, new HashMap<String, Object>(assignment))));		
	}

	private void updateSnapshot(XAttributeMap attribs,
			Map<String, Object> assignment) {
		for (XAttribute attr: attribs.values()) {
				String varName = attr.getKey();//.replaceAll("\\s", "_");
				String token = attr.toString();
				//Object value = token;
				String value = token;
				
				
				if (!varName.contains(":")){

/*					if (dataTypes.get(varName).equals(Long.class)) 
						value = Long.parseLong(token);
					else if (dataTypes.get(varName).equals(Float.class))
						value = Float.parseFloat(token);
					else if (dataTypes.get(varName).equals(Calendar.class))
						value = DatatypeConverter.parseDateTime(token);
					else {
						value = token;
						stringDomains.get(varName).add((String)value);
					}*/
					assignment.put(varName, value);
				}
		}
	}
//activityName; traceId; index; assignment
	private void copyRecords(Map<String, Map<String, List<Pair<Integer, Map<String, Object>>>>> instances2, String traceId, List<Pair<String, Pair<Integer, Map<String, Object>>>> records) {
		for (Pair<String, Pair<Integer, Map<String, Object>>> record: records) {
			Map<String, List<Pair<Integer, Map<String, Object>>>> map = instances2.get(record.getFirst());
			if (map == null)
				instances2.put(record.getFirst(), map = new HashMap<String,List<Pair<Integer,Map<String,Object>>>>());
			if (!record.getSecond().getSecond().isEmpty()) {
				List<Pair<Integer,Map<String, Object>>> list = map.get(traceId);
				if (list == null)
					map.put(traceId, list = new LinkedList<Pair<Integer, Map<String,Object>>>());
				list.add(record.getSecond());
			}
		}
	}

	public Map<String, Map<String, List<Pair<Integer, Map<String, Object>>>>> getInstances() {
		return instances;
	}
	
	public Map<String, Set<String>> getDomains() {
		return stringDomains;
	}
	
	public Map<String, Class<?>> getDataTypes() {
		return dataTypes;
	}

	public Map<String, Object> getAssignment() {
		return assignment;
	}

	public void setAssignment(Map<String, Object> assignment) {
		this.assignment = assignment;
	}
	
	
}
