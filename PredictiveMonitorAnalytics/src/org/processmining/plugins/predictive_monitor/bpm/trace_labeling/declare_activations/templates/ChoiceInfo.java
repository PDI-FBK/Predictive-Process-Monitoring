package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
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

public class ChoiceInfo extends TemplateInfo{

	public Vector<MetricsValues> getMetrics(DeclareMinerInput input, Map<FrequentItemSetType, Map<Set<String>, Float>> frequentItemSetTypeFrequentItemSetSupportMap, Map<DeclareTemplate, List<List<String>>> declareTemplateCandidateDispositionsMap, float alpha, float support,
			XLog log, PrintWriter pw, DeclareTemplate currentTemplate,
			UIPluginContext context, boolean verbose, FindItemSets f){
		List<List<String>> declareTemplateCandidateDispositionsList = declareTemplateCandidateDispositionsMap.get(DeclareTemplate.Choice);
		VectorBasedConstraints  v =  new VectorBasedConstraints(log,input);
		Vector<MetricsValues> metricsValues = new Vector<MetricsValues>();
		numberOfDiscoveredConstraints = 0;
		Vector<String> dispositionsAlreadyConsidered4Coexistence = new Vector<String>();
		for(int k=0; k< declareTemplateCandidateDispositionsList.size(); k++){
			if(context!=null){context.getProgress().inc();}
			dispositionsAlreadyConsidered4Coexistence.add(declareTemplateCandidateDispositionsList.get(k).get(0)+declareTemplateCandidateDispositionsList.get(k).get(1));
			if(!dispositionsAlreadyConsidered4Coexistence.contains(declareTemplateCandidateDispositionsList.get(k).get(1)+declareTemplateCandidateDispositionsList.get(k).get(0))){
				MetricsValues values = new MetricsValues();	
				float supportRule = -1;
				Set<Integer> tracea = null;
				Set<Integer> traceb = null;
				if(v.getAtleastExistenceActivatedAndSatisfiedTraces(declareTemplateCandidateDispositionsList.get(k).get(0), 1)==null){
					tracea = new HashSet<Integer>();
				}else{
				  tracea = v.getAtleastExistenceActivatedAndSatisfiedTraces(declareTemplateCandidateDispositionsList.get(k).get(0), 1);
				}
				if(v.getAtleastExistenceActivatedAndSatisfiedTraces(declareTemplateCandidateDispositionsList.get(k).get(1), 1)==null){
					traceb = new HashSet<Integer>();
				}else{
					traceb =v.getAtleastExistenceActivatedAndSatisfiedTraces(declareTemplateCandidateDispositionsList.get(k).get(1), 1);
				}
				int sizea = tracea.size(); 
				int sizeb = traceb.size();

				tracea.retainAll(traceb);
				String formula = LTLFormula.getFormulaByTemplate(currentTemplate);
				formula = formula.replace( "\""+"A"+"\"" , "\""+declareTemplateCandidateDispositionsList.get(k).get(0)+"\"");
				formula = formula.replace( "\""+"B"+"\"" , "\""+declareTemplateCandidateDispositionsList.get(k).get(1)+"\"");
				int sizeintersect = tracea.size();
				supportRule = (sizea+sizeb-sizeintersect)/(1.0f*log.size());


				if(supportRule>=support){
					numberOfDiscoveredConstraints++;
				}
				values.setFormula(formula);
				values.setParameters(declareTemplateCandidateDispositionsList.get(k));
				values.setSupportRule(supportRule);
				values.setTemplate(currentTemplate);
				metricsValues.add(values);
				printMetrics(pw, formula, supportRule);
			}
		}
		return metricsValues;
	}


	public MetricsValues computeMetrics(DeclareMinerInput input, DeclareTemplate template, List<String> parametersList, XLog log, PrintWriter pw, float alpha, FindItemSets f){
		MetricsValues values = new MetricsValues();
		Set<Integer> tracea = null;
		Set<Integer> traceb = null;
		float supportRule = -1;
		VectorBasedConstraints  v =  new VectorBasedConstraints(log,input);
		if(v.getAtleastExistenceActivatedAndSatisfiedTraces(parametersList.get(0), 1)==null){
			tracea = new HashSet<Integer>();
		}else{
		  tracea = v.getAtleastExistenceActivatedAndSatisfiedTraces(parametersList.get(0), 1);
		}
		if(v.getAtleastExistenceActivatedAndSatisfiedTraces(parametersList.get(1), 1)==null){
			traceb = new HashSet<Integer>();
		}else{
			traceb = v.getAtleastExistenceActivatedAndSatisfiedTraces(parametersList.get(1), 1);
		}
		int sizea = tracea.size(); 
		int sizeb = traceb.size();
		tracea.retainAll(traceb);
		String formula = LTLFormula.getFormulaByTemplate(template);
		formula = formula.replace( "\""+"A"+"\"" , "\""+parametersList.get(0)+"\"");
		formula = formula.replace( "\""+"B"+"\"" , "\""+parametersList.get(1)+"\"");
		int sizeintersect = tracea.size();
		supportRule = (sizea+sizeb-sizeintersect)/(1.0f*log.size());
		values.setFormula(formula);
		values.setSupportRule(supportRule);
		values.setParameters(parametersList);
		values.setTemplate(template);
		return values;
	}


	public Vector<Long>  getTimeDistances(DeclareMinerInput input,  XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> activations){
		boolean found = false;
		Vector<Long> timeDists = new Vector<Long>(); 
		XEvent activation = trace.get(0);
		for(XEvent event : trace){
			ActivityDefinition target1 = constraintDefinition.getBranches(constraintDefinition.getParameters().iterator().next()).iterator().next();
			ActivityDefinition target2 = constraintDefinition.getBranches(constraintDefinition.getParameters().iterator().next()).iterator().next();
			String activityName1 = target1.getName();
			String eventName = (XConceptExtension.instance().extractName(event));
			//}
			if(input.getAprioriKnowledgeBasedCriteriaSet().contains(AprioriKnowledgeBasedCriteria.AllActivitiesWithEventTypes)){
				if(!containsEventType(target1.getName())){
					if(event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
						activityName1 = target1.getName()+"-"+input.getReferenceEventType();
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
			Object activityName2 = target2.getName();
			//}
			if(input.getAprioriKnowledgeBasedCriteriaSet().contains(AprioriKnowledgeBasedCriteria.AllActivitiesWithEventTypes)){
				if(!containsEventType(target2.getName())){
					if(event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
						activityName2 = target2.getName()+"-"+input.getReferenceEventType();
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

			if(eventName.equals(activityName1)||eventName.equals(activityName2)){
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
			ActivityDefinition target1 = constraintDefinition.getBranches(constraintDefinition.getParameters().iterator().next()).iterator().next();
			ActivityDefinition target2 = constraintDefinition.getBranches(constraintDefinition.getParameters().iterator().next()).iterator().next();
			String activityName1 = target1.getName();
			String eventName = (XConceptExtension.instance().extractName(event));
			//}
			if(input.getAprioriKnowledgeBasedCriteriaSet().contains(AprioriKnowledgeBasedCriteria.AllActivitiesWithEventTypes)){
				if(!containsEventType(target1.getName())){
					if(event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
						activityName1 = target1.getName()+"-"+input.getReferenceEventType();
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
			Object activityName2 = target2.getName();
			//}
			if(input.getAprioriKnowledgeBasedCriteriaSet().contains(AprioriKnowledgeBasedCriteria.AllActivitiesWithEventTypes)){
				if(!containsEventType(target2.getName())){
					if(event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
						activityName2 = target2.getName()+"-"+input.getReferenceEventType();
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

			if(eventName.equals(activityName1)||eventName.equals(activityName2)){
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
