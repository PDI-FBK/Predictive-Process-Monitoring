package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.plugins.correlation.CorrelationMiner;
import org.processmining.plugins.correlation.ExtendedEvent;
import org.processmining.plugins.correlation.ExtendedTrace;
import org.processmining.plugins.declareminer.DeclareMinerInput;
import org.processmining.plugins.declareminer.DeclareModelGenerator;
import org.processmining.plugins.declareminer.MetricsValues;
import org.processmining.plugins.declareminer.apriori.FindItemSets;
import org.processmining.plugins.declareminer.enumtypes.AprioriKnowledgeBasedCriteria;
import org.processmining.plugins.declareminer.enumtypes.DeclareTemplate;
import org.processmining.plugins.declareminer.enumtypes.FrequentItemSetType;
import org.processmining.plugins.declareminer.visualizing.ActivityDefinition;
import org.processmining.plugins.declareminer.visualizing.ConstraintDefinition;
import org.processmining.plugins.declareminer.visualizing.DeclareMinerOutput;
import org.processmining.plugins.declareminer.visualizing.Parameter;

public class RespondedExistenceInfo extends TemplateInfo{

	public MetricsValues getMetrics(DeclareMinerOutput model, ConstraintDefinition cd, String correlation, DeclareMinerInput input, float alpha, float support,
			XLog log, DeclareTemplate currentTemplate, UIPluginContext context){
		numberOfDiscoveredConstraints = 0;
		//Vector<MetricsValues> metricsValues = new Vector<MetricsValues>();
		//List<List<String>> declareTemplateCandidateDispositionsList = declareTemplateCandidateDispositionsMap.get(currentTemplate);
		//for(int k=0; k< declareTemplateCandidateDispositionsList.size(); k++){

		ArrayList<String> paramList = new ArrayList<String>();
		paramList.add(cd.getBranches(cd.getParameterWithId(1)).iterator().next().toString());
		paramList.add(cd.getBranches(cd.getParameterWithId(2)).iterator().next().toString());
		if(context!=null){context.getProgress().inc();}
		MetricsValues values = null;
		//	if(verbose){
		//		values = computeMetrics(model, cd,input,currentTemplate, declareTemplateCandidateDispositionsList.get(k), log, pw, alpha,f, correlation);
		//	}else{
		values = computeMetrics(model.getModel(), cd, input,currentTemplate,paramList, log, alpha, correlation);
		//	}
		if(values.getSupportRule()>=support){
			numberOfDiscoveredConstraints ++;
		}
		//	metricsValues.add(values);
		//}
		return values;
	}


	public Vector<MetricsValues> getMetrics(DeclareMinerInput input, Map<FrequentItemSetType, Map<Set<String>, Float>> frequentItemSetTypeFrequentItemSetSupportMap, Map<DeclareTemplate, List<List<String>>> declareTemplateCandidateDispositionsMap, float alpha, float support,
			XLog log, PrintWriter pw, DeclareTemplate currentTemplate,
			UIPluginContext context, boolean verbose, FindItemSets f){
		numberOfDiscoveredConstraints = 0;
		Vector<MetricsValues> metricsValues = new Vector<MetricsValues>();
		List<List<String>> declareTemplateCandidateDispositionsList = declareTemplateCandidateDispositionsMap.get(currentTemplate);
		for(int k=0; k< declareTemplateCandidateDispositionsList.size(); k++){
			if(context!=null){context.getProgress().inc();}
			MetricsValues values = null;
			if(verbose){
				values = computeMetrics(input,currentTemplate, declareTemplateCandidateDispositionsList.get(k), log, pw, alpha,f);
			}else{
				values = computeMetrics(input,currentTemplate, declareTemplateCandidateDispositionsList.get(k), log, null, alpha,f);
			}
			if(values.getSupportRule()>=support){
				numberOfDiscoveredConstraints ++;
			}
			metricsValues.add(values);
		}
		return metricsValues;
	}

	public Vector<Long> getTimeDistances(DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> fulfillments) {
		long timeDiff1 = -1;
		long timeDiff2 = -1;
		Vector<Long> timeDists = new Vector<Long>(); 
		for(Integer pos : fulfillments){
			boolean found = false;
			XEvent act = trace.get(pos);
			for(int c=pos; c<trace.size(); c++){
				XEvent event = trace.get(c);
				Iterator<Parameter> iter = constraintDefinition.getParameters().iterator();
				iter.next();
				Parameter p2 = iter.next();
				ActivityDefinition target = constraintDefinition.getBranches(p2).iterator().next();

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
					found = true;
					long timeDistance1 = XTimeExtension.instance().extractTimestamp(event).getTime();
					long timeDistance2 = XTimeExtension.instance().extractTimestamp(act).getTime();
					timeDiff1 = timeDistance1 - timeDistance2;
				}
				if(found){
					break;
				}
			}

			found = false;

			for(int c=pos; c>=0; c--){
				XEvent event = trace.get(c);
				Iterator<Parameter> iter = constraintDefinition.getParameters().iterator();
				iter.next();
				Parameter p2 = iter.next();
				ActivityDefinition target = constraintDefinition.getBranches(p2).iterator().next();

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
					found = true;
					long timeDistance1 = XTimeExtension.instance().extractTimestamp(act).getTime();
					long timeDistance2 = XTimeExtension.instance().extractTimestamp(event).getTime();
					timeDiff2 = timeDistance1 - timeDistance2;
				}
				if(found){
					break;
				}
			}

			long timeDiff =0;
			if((timeDiff1!=-1)&&(timeDiff2!=-1)&&(timeDiff2<=timeDiff1)){
				timeDiff = timeDiff2;
			}
			if((timeDiff2!=-1)&&(timeDiff1!=-1)&&(timeDiff1<=timeDiff2)){
				timeDiff = timeDiff1;
			}
			if((timeDiff1!=-1)&&(timeDiff2==-1)){
				timeDiff = timeDiff1;
			}
			if((timeDiff2!=-1)&&(timeDiff1==-1)){
				timeDiff = timeDiff2;
			}
		//	if(timeDiff!=0){
				timeDists.add(timeDiff);
		//	}
		}
		return timeDists;
	}


	public Vector<Long> getTimeDistances(HashMap<String,ExtendedEvent>  extEvents, DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> fulfillments, String correlation) {
		long timeDiff1 = -1;
		long timeDiff2 = -1;
		Vector<Long> timeDists = new Vector<Long>(); 
		CorrelationMiner miner = new CorrelationMiner();
		for(Integer pos : fulfillments){
			boolean found = false;
			XEvent act = trace.get(pos);
			for(int c=pos; c<trace.size(); c++){
				XEvent event = trace.get(c);
				Iterator<Parameter> iter = constraintDefinition.getParameters().iterator();
				iter.next();
				Parameter p2 = iter.next();
				ActivityDefinition target = constraintDefinition.getBranches(p2).iterator().next();

				String activityName = target.getName();
				String eventName = (XConceptExtension.instance().extractName(event));
				//}
				boolean evType = false;
				DeclareModelGenerator gen = new DeclareModelGenerator();
				
				if(gen.hasEventTypeInName(activityName)){
					evType= true;
				}
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
					if(miner.isValid(evType, correlation, act, event, extEvents)){
						found = true;
						long timeDistance1 = XTimeExtension.instance().extractTimestamp(event).getTime();
						long timeDistance2 = XTimeExtension.instance().extractTimestamp(act).getTime();
						timeDiff1 = timeDistance1 - timeDistance2;
					}
				}
				if(found){
					break;
				}
			}

			found = false;

			for(int c=pos; c>=0; c--){
				XEvent event = trace.get(c);
				Iterator<Parameter> iter = constraintDefinition.getParameters().iterator();
				iter.next();
				Parameter p2 = iter.next();
				ActivityDefinition target = constraintDefinition.getBranches(p2).iterator().next();

				String activityName = target.getName();
				String eventName = (XConceptExtension.instance().extractName(event));
				//}
				boolean evType = false;
				DeclareModelGenerator gen = new DeclareModelGenerator();
				
				if(gen.hasEventTypeInName(activityName)){
					evType= true;
				}
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
					if(miner.isValid(evType, correlation, act, event, extEvents)){
						found = true;
						long timeDistance1 = XTimeExtension.instance().extractTimestamp(act).getTime();
						long timeDistance2 = XTimeExtension.instance().extractTimestamp(event).getTime();
						timeDiff2 = timeDistance1 - timeDistance2;
					}
				}
				if(found){
					break;
				}
			}

			long timeDiff =0;
			if((timeDiff1!=-1)&&(timeDiff2!=-1)&&(timeDiff2<=timeDiff1)){
				timeDiff = timeDiff2;
			}
			if((timeDiff2!=-1)&&(timeDiff1!=-1)&&(timeDiff1<=timeDiff2)){
				timeDiff = timeDiff1;
			}
			if((timeDiff1!=-1)&&(timeDiff2==-1)){
				timeDiff = timeDiff1;
			}
			if((timeDiff2!=-1)&&(timeDiff1==-1)){
				timeDiff = timeDiff2;
			}
		//	if(timeDiff!=0){
				timeDists.add(timeDiff);
		//	}
		}
		return timeDists;
	}


	public Vector<ExtendedTrace> getNonAmbiguousActivations(XLog log, DeclareTemplate template, String activation, String target){
		// for response 
		Vector<ExtendedTrace> output = new Vector<ExtendedTrace>();
		activation = activation.replaceAll("-assign","").replaceAll("-ate_abort","").replaceAll("-suspend","").replaceAll("-complete","").replaceAll("-autoskip","").replaceAll("-manualskip","").replaceAll("pi_abort","").replaceAll("-reassign","").replaceAll("-resume","").replaceAll("-schedule","").replaceAll("-start","").replaceAll("-unknown","").replaceAll("-withdraw","");
		target = target.replaceAll("-assign","").replaceAll("-ate_abort","").replaceAll("-suspend","").replaceAll("-complete","").replaceAll("-autoskip","").replaceAll("-manualskip","").replaceAll("pi_abort","").replaceAll("-reassign","").replaceAll("-resume","").replaceAll("-schedule","").replaceAll("-start","").replaceAll("-unknown","").replaceAll("-withdraw","");
		for(XTrace trace: log){
			int targetNum = 0;
			Vector<Integer> targetPos= new Vector<Integer>();
			ExtendedTrace extr = new ExtendedTrace();
			extr.setTrace(trace);
			extr.setNonambi(new Vector<Integer>());
			extr.setCorrespcorrel(new HashMap<Integer,Vector<Integer>>());
			Vector<Integer> nonambi = new Vector<Integer>();
			int pos = 0;
			for(XEvent event : trace){
				String eventName = XConceptExtension.instance().extractName(event);
				if(eventName.equals(activation)){
					nonambi.add(pos);
				}
				if(eventName.equals(target)){
					targetNum ++;
					targetPos.add(pos);
				}
				pos++;
			}
			if(nonambi.size() > 0  && targetNum >= 1){
				HashMap<Integer,Vector<Integer>> corresp = extr.getCorrespcorrel();
				Vector<Integer> c = (Vector<Integer>) targetPos.clone();
				for(int i = 0; i<nonambi.size(); i++){
					corresp.put(nonambi.get(i),c);
				}
				extr.setNonambi(nonambi);
				extr.setCorrespcorrel(corresp);
			}
			output.add(extr);
		}
		return output;
	}

}
