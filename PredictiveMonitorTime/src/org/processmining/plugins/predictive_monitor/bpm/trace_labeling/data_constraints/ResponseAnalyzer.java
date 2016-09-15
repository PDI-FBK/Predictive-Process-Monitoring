package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.test.Evaluator_predictions;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Pair;
import org.processmining.plugins.predictive_monitor.bpm.utility.DataSnapshotListener;


public class ResponseAnalyzer implements DeclareReplayer{

	HashMap<String, String> conditionsAct = new HashMap<String, String>();
	HashMap<String, String> conditionsTarg = new HashMap<String, String>();
	HashMap<String, HashMap<String, HashMap<String, Set<Integer>>>> fulfillments = new HashMap<String, HashMap<String, HashMap<String, Set<Integer>>>>();
	HashMap<String, HashMap<String, HashMap<String, Set<Integer>>>> pendingActivations = new HashMap<String, HashMap<String, HashMap<String, Set<Integer>>>>();
	List<List<String>> dispositions;
	HashMap<String, ConstraintConditions> cMap = new HashMap<String, ConstraintConditions>();


	public void process(int eventIndex, String event, XTrace trace, String traceId, DataSnapshotListener listener) {
		HashMap<String, HashMap<String, Set<Integer>>> pendingPerTrace;
		if(pendingActivations.containsKey(traceId)){
			pendingPerTrace = pendingActivations.get(traceId);
		}else{
			pendingPerTrace = new HashMap<String, HashMap<String,Set<Integer>>>();
			for(List<String> pair : dispositions){
				HashMap<String, Set<Integer>> pend;
				if(pendingPerTrace.containsKey(pair.get(0))){
					pend = pendingPerTrace.get(pair.get(0));
				}else{
					pend = new HashMap<String, Set<Integer>>();
				}
				pend.put(pair.get(1), new HashSet<Integer>());
				pendingPerTrace.put(pair.get(0),pend);
			}	
		}

		if(pendingPerTrace.containsKey(event)){
			HashMap<String, Set<Integer>> pend = pendingPerTrace.get(event);
			for(String param2 : pend.keySet()){
				boolean isActivation = true;
				Pair<String,String> params = new Pair<String, String>(event, param2);
				//	String time2 = trace.get(index)
				String conditionActivation = conditionsAct.get(params.getFirst()+params.getSecond());		
				if(!conditionActivation.equals("1")){
					isActivation = false;
					List<Pair<Integer, Map<String, Object>>> snapshotsActivation = listener.getInstances().get(event).get(traceId);
					Map<String, String> snapshotActivation = null;
					Map<String,String> datavalues = new HashMap<String, String>();
					for(Pair pair : snapshotsActivation){
						if((Integer)pair.getFirst()==eventIndex){
							snapshotActivation = (Map<String, String>)pair.getSecond();
							for(String attribute : snapshotActivation.keySet()){
								datavalues.put("A."+attribute, snapshotActivation.get(attribute));
								datavalues.put(attribute, snapshotActivation.get(attribute));
							}
							break;
						}
					}
					try{
						isActivation = Evaluator_predictions.evaluateExpression(datavalues, conditionActivation);
					}catch(Exception e){
						e.printStackTrace();
					}

				}
				if(isActivation){
					Set<Integer> indexesForPending = pend.get(param2);
					indexesForPending.add(eventIndex);
					pend.put(param2, indexesForPending);
				}
			}
		}
		HashMap<String, HashMap<String, Set<Integer>>> fulfillmentPerTrace;
		if(fulfillments.containsKey(traceId)){
			fulfillmentPerTrace = fulfillments.get(traceId);
		}else{
			fulfillmentPerTrace = new HashMap<String, HashMap<String,Set<Integer>>>();
			for(List<String> pair : dispositions){
				HashMap<String, Set<Integer>> fulfill;
				if(fulfillmentPerTrace.containsKey(pair.get(0))){
					fulfill = fulfillmentPerTrace.get(pair.get(0));
				}else{
					fulfill = new HashMap<String, Set<Integer>>();
				}
				fulfill.put(pair.get(1), new HashSet<Integer>());
				fulfillmentPerTrace.put(pair.get(0),fulfill);
			}	
		}
		for(String param1 : pendingPerTrace.keySet()){
			HashMap<String, Set<Integer>> pending = pendingPerTrace.get(param1);
			HashMap<String, Set<Integer>> fulfill = fulfillmentPerTrace.get(param1);
			Pair<String,String> params = new Pair<String, String>(param1, event);
			String conditionTarget = conditionsTarg.get(params.getFirst()+params.getSecond());
			if(pending.containsKey(event)){
				if(conditionTarget.equals("1")){
					Set<Integer> indexesPend = pending.get(event);
					Set<Integer> indexesFulfill = fulfill.get(event);
					indexesFulfill.addAll(indexesPend);
					indexesPend.clear();
					pending.put(event, indexesPend);
					fulfill.put(event, indexesFulfill);
					fulfillmentPerTrace.put(param1, fulfill);
					pendingPerTrace.put(param1, pending);
				}else{
					Set<Integer> indexesPend = pending.get(event);
					Set<Integer> indexesToremove = new HashSet<Integer>();
					for(Integer p : indexesPend){
						boolean	targetFound = false;
						String activation = (trace.get(p).getAttributes().get(XConceptExtension.KEY_NAME).toString());//+"-"+trace.get(p).getAttributes().get(XLifecycleExtension.KEY_TRANSITION));
						//Format timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
						Format timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
						Date date1 = null;
						Date date2 = null;
						try {
							date1 = (Date) timestampFormat.parseObject(trace.get(p).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
							date2 = (Date) timestampFormat.parseObject(trace.get(eventIndex).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							//System.out.println("RICORDA!!!!!!!!!!");
						}
						//Date date1 = format1.p
						//Date date1 = DateFormat.ptrace.get(p).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
						List<Pair<Integer, Map<String, Object>>> snapshotsActivation = listener.getInstances().get(activation).get(traceId);
						Map<String, String> snapshotActivation = null;
						Map<String,String> datavalues = new HashMap<String, String>();
						for(Pair pair : snapshotsActivation){
							if((Integer)pair.getFirst()==p){
								snapshotActivation = (Map<String, String>)pair.getSecond();
								for(String attribute : snapshotActivation.keySet()){
									datavalues.put("A."+attribute, snapshotActivation.get(attribute));
									datavalues.put(attribute, snapshotActivation.get(attribute));
								}
								break;
							}
						}
						//String activation = (trace.get(p).getAttributes().get(XConceptExtension.KEY_NAME)+"-"+trace.get(p).getAttributes().get(XLifecycleExtension.KEY_TRANSITION));
						List<Pair<Integer, Map<String, Object>>> snapshotsTarget = listener.getInstances().get(event).get(traceId);
						Map<String, String> snapshotTarget = null;
						//Map<String,String> datavalues = new HashMap<String, String>();
						for(Pair pair : snapshotsTarget){
							if((Integer)pair.getFirst()==eventIndex){
								snapshotTarget = (Map<String, String>)pair.getSecond();
								for(String attribute : snapshotTarget.keySet()){
									datavalues.put("T."+attribute, snapshotTarget.get(attribute));
									//	datavalues.put(attribute, snapshotTarget.get(attribute));
								}
								break;
							}
						}
						try{
							long milliseconds = 0;
							long m1 = date1.getTime();
							long m2 = date2.getTime();
							if(m1>m2){
								milliseconds = m1-m2;
							}else{
								milliseconds = m2-m1;
							}
							targetFound = Evaluator_predictions.evaluateExpression(datavalues, conditionTarget) && Evaluator_predictions.evaluateExpression(datavalues,cMap.get(params.getFirst()+params.getSecond()).getTimeCondition(milliseconds));
						}catch(Exception e){
							e.printStackTrace();
						}
						if(targetFound){
							Set<Integer> indexesFulfill = fulfill.get(event);
							indexesFulfill.add(p);
							//indexesPend.clear();
							indexesToremove.add(p);
							
							pending.put(event, indexesPend);
							fulfill.put(event, indexesFulfill);
							fulfillmentPerTrace.put(param1, fulfill);
							pendingPerTrace.put(param1, pending);
						}
					}
					indexesPend.removeAll(indexesToremove);
				}
			}
		}
		fulfillments.put(traceId, fulfillmentPerTrace);
		pendingActivations.put(traceId, pendingPerTrace);
	}


	public Set<Integer> getFulfillments(String traceId, String param1, String param2) {
		return fulfillments.get(traceId).get(param1).get(param2);
	}


	public Set<Integer> getPendingActivations(String traceId, String param1, String param2) {
		return pendingActivations.get(traceId).get(param1).get(param2);
	}
	
	public boolean checkDataConditions(boolean eventTypes, DataSnapshotListener listener, String activation, String targetName, XTrace trace, String condition){
		dispositions = new ArrayList<List<String>>();
		ArrayList<String> element = new ArrayList<String>();
		element.add(activation);
		element.add(targetName);
		element.add(condition);
		dispositions.add(element);
		for(List<String> item : dispositions){
			Pair<String,String> pair = new Pair<String, String>(item.get(0), item.get(1)); 
			ConstraintConditions c = ConstraintConditions.build(item.get(2));
			cMap.put(pair.getFirst()+pair.getSecond(), c);
			if(!c.containsActivationCondition()){
				conditionsAct.put(pair.getFirst()+pair.getSecond(), "1");
			}else{
				conditionsAct.put(pair.getFirst()+pair.getSecond(), c.getActivationCondition());
			}
			if(!c.containsConstraintCondition() && ! c.containsTimeCondition()){
				conditionsTarg.put(pair.getFirst()+pair.getSecond(), "1");
			}else{
				conditionsTarg.put(pair.getFirst()+pair.getSecond(), c.getConstraintCondition());
			}
		}
		boolean violated = false;
		String traceId = trace.getAttributes().get(XConceptExtension.KEY_NAME).toString();	
		listener.openTrace(trace.getAttributes(), traceId,new HashSet<String>());

		int index = 0;
		for (XEvent event : trace) {
			listener.processEvent(event.getAttributes(), index);
			index++;
		}
		listener.closeTrace(trace.getAttributes(), traceId);

		index = 0;
		for (XEvent event : trace) {
			process(index, XConceptExtension.instance().extractName(event), trace, traceId, listener);
			index++;
		}
		if(pendingActivations.get(traceId).get(activation).get(targetName).size()>0){
			violated = true;
		}
		//listener.closeTrace(trace.getAttributes(), traceId);
		return violated;
	}

	protected boolean containsEventType(String activityName){
		return (!(!activityName.contains("-assign")&&!activityName.contains("-ate_abort")&&
				!activityName.contains("-suspend")&&!activityName.contains("-complete")&&
				!activityName.contains("-autoskip")&&!activityName.contains("-manualskip")&&
				!activityName.contains("pi_abort")&&!activityName.contains("-reassign")&&!
				activityName.contains("-resume")&&!activityName.contains("-schedule")&&
				!activityName.contains("-start")&&!activityName.contains("-unknown")&&
				!activityName.contains("-withdraw")));
	}

}
