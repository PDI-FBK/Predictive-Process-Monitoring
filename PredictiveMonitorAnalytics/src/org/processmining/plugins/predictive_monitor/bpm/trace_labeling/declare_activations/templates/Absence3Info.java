package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.correlation.ExtendedEvent;
import org.processmining.plugins.declareminer.DeclareMinerInput;
import org.processmining.plugins.declareminer.enumtypes.AprioriKnowledgeBasedCriteria;
import org.processmining.plugins.declareminer.templates.AtMostInfo;
import org.processmining.plugins.declareminer.visualizing.ActivityDefinition;
import org.processmining.plugins.declareminer.visualizing.ConstraintDefinition;

public class Absence3Info extends AtMostInfo {

	public Vector<Long>  getTimeDistances(DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> activations){
		boolean found = false;
		boolean first = false;
		boolean second = false;
		Vector<Long> timeDists = new Vector<Long>(); 
		XEvent act = trace.get(0);
		for(XEvent event : trace){
			ActivityDefinition target = constraintDefinition.getBranches(constraintDefinition.getParameters().iterator().next()).iterator().next();
			String activityName = target.getName();
			String eventName = (XConceptExtension.instance().extractName(event));
			//}
			if(input.getAprioriKnowledgeBasedCriteriaSet().contains(AprioriKnowledgeBasedCriteria.AllActivitiesWithEventTypes)){
				if(!containsEventType(target.getName())){
					if(event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
						activityName = target.getName()+"-"+input.getReferenceEventType();
						eventName = (XConceptExtension.instance().extractName(event))+"-"+XLifecycleExtension.instance().extractTransition(event);
					}
				}else{
					if(event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
						eventName = (XConceptExtension.instance().extractName(event))+"-"+XLifecycleExtension.instance().extractTransition(event);
					}else{
						eventName = (XConceptExtension.instance().extractName(event))+"-"+input.getReferenceEventType();
					}
				}
			}
			if(eventName.equals(activityName)){
				if(first && second){
					long timeDistance1 = XTimeExtension.instance().extractTimestamp(event).getTime();
					long timeDistance2 = XTimeExtension.instance().extractTimestamp(act).getTime();
					long timeDiff = timeDistance1 - timeDistance2;
					timeDists.add(timeDiff);
					found = true;
				}else if(first && !second){
					second = true;
				}else{
					first = true;
				}

			}
			if(found){
				break;
			}
		}
		return timeDists;
	}
	
	public Vector<Long> getTimeDistances(HashMap<String,ExtendedEvent>  extEvents, DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> activations, String correlation){
		boolean found = false;
		boolean first = false;
		boolean second = false;
		Vector<Long> timeDists = new Vector<Long>(); 
		XEvent act = trace.get(0);
		for(XEvent event : trace){
			ActivityDefinition target = constraintDefinition.getBranches(constraintDefinition.getParameters().iterator().next()).iterator().next();
			String activityName = target.getName();
			String eventName = (XConceptExtension.instance().extractName(event));
			//}
			if(input.getAprioriKnowledgeBasedCriteriaSet().contains(AprioriKnowledgeBasedCriteria.AllActivitiesWithEventTypes)){
				if(!containsEventType(target.getName())){
					if(event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
						activityName = target.getName()+"-"+input.getReferenceEventType();
						eventName = (XConceptExtension.instance().extractName(event))+"-"+XLifecycleExtension.instance().extractTransition(event);
					}
				}else{
					if(event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
						eventName = (XConceptExtension.instance().extractName(event))+"-"+XLifecycleExtension.instance().extractTransition(event);
					}else{
						eventName = (XConceptExtension.instance().extractName(event))+"-"+input.getReferenceEventType();
					}
				}
			}
			if(eventName.equals(activityName)){
				if(first && second){
					long timeDistance1 = XTimeExtension.instance().extractTimestamp(event).getTime();
					long timeDistance2 = XTimeExtension.instance().extractTimestamp(act).getTime();
					long timeDiff = timeDistance1 - timeDistance2;
					timeDists.add(timeDiff);
					found = true;
				}else if(first && !second){
					second = true;
				}else{
					first = true;
				}

			}
			if(found){
				break;
			}
		}
		return timeDists;
	}

}
