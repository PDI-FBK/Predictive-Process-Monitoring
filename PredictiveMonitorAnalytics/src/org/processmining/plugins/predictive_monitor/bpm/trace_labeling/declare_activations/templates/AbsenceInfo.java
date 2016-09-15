package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates;

import java.io.PrintWriter;
import java.util.HashMap;
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
import org.processmining.plugins.correlation.ExtendedEvent;
import org.processmining.plugins.declareminer.DeclareMinerInput;
import org.processmining.plugins.declareminer.MetricsValues;
import org.processmining.plugins.declareminer.VectorBasedConstraints;
import org.processmining.plugins.declareminer.apriori.FindItemSets;
import org.processmining.plugins.declareminer.enumtypes.AprioriKnowledgeBasedCriteria;
import org.processmining.plugins.declareminer.enumtypes.DeclareTemplate;
import org.processmining.plugins.declareminer.enumtypes.FrequentItemSetType;
import org.processmining.plugins.declareminer.templates.LTLFormula;
import org.processmining.plugins.declareminer.visualizing.ActivityDefinition;
import org.processmining.plugins.declareminer.visualizing.ConstraintDefinition;

public class AbsenceInfo extends TemplateInfo {

	public Vector<MetricsValues> getMetrics(DeclareMinerInput input, Map<FrequentItemSetType, Map<Set<String>, Float>> frequentItemSetTypeFrequentItemSetSupportMap, Map<DeclareTemplate, List<List<String>>> declareTemplateCandidateDispositionsMap, float alpha, float support,
			XLog log, PrintWriter pw,DeclareTemplate currentTemplate,
			UIPluginContext context, boolean verbose, FindItemSets f){
		Vector<MetricsValues> metricsValues = new Vector<MetricsValues>();
		List<List<String>> declareTemplateCandidateDispositionsList = declareTemplateCandidateDispositionsMap.get(DeclareTemplate.Absence);
		VectorBasedConstraints  v =  new VectorBasedConstraints(log,input);
		for(int k=0; k< declareTemplateCandidateDispositionsList.size(); k++){
			if(context!=null){context.getProgress().inc();}
			MetricsValues values = new MetricsValues();
			float supportRule = -1;
			String formula = LTLFormula.getFormulaByTemplate(currentTemplate);
			formula = formula.replace( "\""+"A"+"\"" , "\""+declareTemplateCandidateDispositionsList.get(k).get(0)+"\"");
			if(v.getAtmostExistenceActivatedAndSatisfiedTraces(declareTemplateCandidateDispositionsList.get(k).get(0), 0)==null){
				supportRule = 0;
			}else{
				supportRule = ((float)v.getAtmostExistenceActivatedAndSatisfiedTraces(declareTemplateCandidateDispositionsList.get(k).get(0), 0).size())/log.size();
			}
			values.setFormula(formula);
			values.setParameters(declareTemplateCandidateDispositionsList.get(k));
			values.setSupportRule(supportRule);
			if(supportRule>=support){
				numberOfDiscoveredConstraints ++;
			}
			values.setTemplate(currentTemplate);
			metricsValues.add(values);
			printMetrics(pw, formula, supportRule);
		}
		return metricsValues;
	}

	public MetricsValues computeMetrics(DeclareMinerInput input, DeclareTemplate template, List<String> parametersList, XLog log, PrintWriter pw, float alpha,FindItemSets f){
		MetricsValues values = new MetricsValues();
		float supportRule = -1;
		String formula = LTLFormula.getFormulaByTemplate(template);
		VectorBasedConstraints  v =  new VectorBasedConstraints(log,input);

		formula = formula.replace( "\""+"A"+"\"" , "\""+parametersList.get(0)+"\"");
		if(v.getAtmostExistenceActivatedAndSatisfiedTraces(parametersList.get(0), 0)==null){
			supportRule = 0;
		}else{
			supportRule = ((float)v.getAtmostExistenceActivatedAndSatisfiedTraces(parametersList.get(0), 0).size())/log.size();
		}
		values.setSupportRule(supportRule);
		values.setFormula(formula);
		values.setParameters(parametersList);
		values.setTemplate(template);
		return values;
	}

	public Vector<Long> getTimeDistances(DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> activations) {
		boolean found = false;
		Vector<Long> timeDists = new Vector<Long>();
		XEvent activation = trace.get(0);
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
				found = true;
				long timeDistance1 = XTimeExtension.instance().extractTimestamp(event).getTime();
				long timeDistance2 = XTimeExtension.instance().extractTimestamp(activation).getTime();
				long timeDiff = timeDistance1 - timeDistance2;
				timeDists.add(timeDiff);

			}
			if(found){
				break;
			}
		}
		return timeDists;
	}

	
	public Vector<Long> getTimeDistances(HashMap<String,ExtendedEvent>  extEvents, DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> activations, String correlation){
		boolean found = false;
		Vector<Long> timeDists = new Vector<Long>();
		XEvent activation = trace.get(0);
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
				found = true;
				long timeDistance1 = XTimeExtension.instance().extractTimestamp(event).getTime();
				long timeDistance2 = XTimeExtension.instance().extractTimestamp(activation).getTime();
				long timeDiff = timeDistance1 - timeDistance2;
				timeDists.add(timeDiff);

			}
			if(found){
				break;
			}
		}
		return timeDists;
	}
}
