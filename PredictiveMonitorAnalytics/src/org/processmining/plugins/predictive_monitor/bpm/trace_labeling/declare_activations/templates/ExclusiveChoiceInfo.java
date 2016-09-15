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
import org.processmining.plugins.declareminer.apriori.FindItemSets;
import org.processmining.plugins.declareminer.enumtypes.AprioriKnowledgeBasedCriteria;
import org.processmining.plugins.declareminer.enumtypes.DeclareTemplate;
import org.processmining.plugins.declareminer.enumtypes.FrequentItemSetType;
import org.processmining.plugins.declareminer.templates.LTLFormula;
import org.processmining.plugins.declareminer.visualizing.ActivityDefinition;
import org.processmining.plugins.declareminer.visualizing.ConstraintDefinition;

public class ExclusiveChoiceInfo extends TemplateInfo {

	public Vector<MetricsValues> getMetrics(DeclareMinerInput input, Map<FrequentItemSetType, Map<Set<String>, Float>> frequentItemSetTypeFrequentItemSetSupportMap, Map<DeclareTemplate, List<List<String>>> declareTemplateCandidateDispositionsMap, float alpha, float support,
			XLog log, PrintWriter pw, DeclareTemplate currentTemplate,
			UIPluginContext context, boolean verbose, FindItemSets f){
		numberOfDiscoveredConstraints = 0;
		List<List<String>> declareTemplateCandidateDispositionsList = declareTemplateCandidateDispositionsMap.get(DeclareTemplate.Exclusive_Choice);
		Vector<MetricsValues> metricsValues = new Vector<MetricsValues>();
		numberOfDiscoveredConstraints = 0;
		Vector<String> dispositionsAlreadyConsidered4Coexistence = new Vector<String>();
		for(int k=0; k< declareTemplateCandidateDispositionsList.size(); k++){
			if(context!=null){context.getProgress().inc();}
			String formula = LTLFormula.getFormulaByTemplate(currentTemplate);
			formula = formula.replace( "\""+"A"+"\"" , "\""+declareTemplateCandidateDispositionsList.get(k).get(0)+"\"");
			formula = formula.replace( "\""+"B"+"\"" , "\""+declareTemplateCandidateDispositionsList.get(k).get(1)+"\"");
			dispositionsAlreadyConsidered4Coexistence.add(declareTemplateCandidateDispositionsList.get(k).get(0)+declareTemplateCandidateDispositionsList.get(k).get(1));
			if(!dispositionsAlreadyConsidered4Coexistence.contains(declareTemplateCandidateDispositionsList.get(k).get(1)+declareTemplateCandidateDispositionsList.get(k).get(0))){
				MetricsValues values = new MetricsValues();
				float supportRule= f.getSupport(declareTemplateCandidateDispositionsList.get(k).get(0),"NOT-"+declareTemplateCandidateDispositionsList.get(k).get(1))/100.f+f.getSupport("NOT-"+declareTemplateCandidateDispositionsList.get(k).get(0),declareTemplateCandidateDispositionsList.get(k).get(1))/100.f;
				values.setSupportRule(supportRule);
				values.setFormula(formula);
				values.setParameters(declareTemplateCandidateDispositionsList.get(k));
				values.setSupportRule(supportRule);
				if(supportRule>=support){
					numberOfDiscoveredConstraints ++;
				}
				values.setTemplate(currentTemplate);
				printMetrics(pw, formula, supportRule);
				metricsValues.add(values);

			}
		}
		return metricsValues;
	}
	
	public MetricsValues computeMetrics(DeclareMinerInput input, DeclareTemplate template, List<String> parametersList, XLog log, PrintWriter pw, float alpha, FindItemSets f){
		MetricsValues values = new MetricsValues();
		String formula = LTLFormula.getFormulaByTemplate(template);
		formula = formula.replace( "\""+"A"+"\"" , "\""+parametersList.get(0)+"\"");
		formula = formula.replace( "\""+"B"+"\"" , "\""+parametersList.get(1)+"\"");
		float supportRule= f.getSupport(parametersList.get(0),"NOT-"+parametersList.get(1))/100.f+f.getSupport("NOT-"+parametersList.get(0),parametersList.get(1))/100.f;
		values.setSupportRule(supportRule);
		values.setFormula(formula);
		values.setParameters(parametersList);
		values.setSupportRule(supportRule);
		values.setTemplate(template);
		return values;
	}

	public Vector<Long>  getTimeDistances(DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintdefinition, Set<Integer> activations){
		boolean found = false;
		boolean exceed = false;
		long timeDiff = 0;
		Vector<Long> timeDists = new Vector<Long>(); 
		XEvent act = trace.get(0);
		for(XEvent event : trace){
			ActivityDefinition target1 = constraintdefinition.getBranches(constraintdefinition.getParameters().iterator().next()).iterator().next();
			ActivityDefinition target2 = constraintdefinition.getBranches(constraintdefinition.getParameters().iterator().next()).iterator().next();
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
			String activityName2 = target2.getName();;
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
			boolean fp1 = false;
			boolean fp2 = false;
			if(eventName.equals(activityName1)){
				if(!fp2 && !found){
					found = true;
					fp1 = true;
					long timeDistance1 = XTimeExtension.instance().extractTimestamp(event).getTime();
					long timeDistance2 = XTimeExtension.instance().extractTimestamp(act).getTime();
					timeDiff = timeDistance1 - timeDistance2;
					timeDists.add(timeDiff);
				}else if(fp2){
					exceed = true;
				}
			}

			
			if(eventName.equals(activityName2)){
				if(!fp1 && !found){
					found = true;
					fp2 = true;
					long timeDistance1 = XTimeExtension.instance().extractTimestamp(event).getTime();
					long timeDistance2 = XTimeExtension.instance().extractTimestamp(act).getTime();
					timeDiff = timeDistance1 - timeDistance2;
					timeDists.add(timeDiff);
				}else if(fp1){
					exceed = true;
				}
			}
			if(exceed){
				break;
			}
		}
		return timeDists;
	}
	
	
	public Vector<Long> getTimeDistances(HashMap<String,ExtendedEvent>  extEvents, DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintdefinition, Set<Integer> activations, String correlation){
		boolean found = false;
		boolean exceed = false;
		long timeDiff = 0;
		Vector<Long> timeDists = new Vector<Long>(); 
		XEvent act = trace.get(0);
		for(XEvent event : trace){
			ActivityDefinition target1 = constraintdefinition.getBranches(constraintdefinition.getParameters().iterator().next()).iterator().next();
			ActivityDefinition target2 = constraintdefinition.getBranches(constraintdefinition.getParameters().iterator().next()).iterator().next();
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
			String activityName2 = target2.getName();;
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
			boolean fp1 = false;
			boolean fp2 = false;
			if(eventName.equals(activityName1)){
				if(!fp2 && !found){
					found = true;
					fp1 = true;
					long timeDistance1 = XTimeExtension.instance().extractTimestamp(event).getTime();
					long timeDistance2 = XTimeExtension.instance().extractTimestamp(act).getTime();
					timeDiff = timeDistance1 - timeDistance2;
					timeDists.add(timeDiff);
				}else if(fp2){
					exceed = true;
				}
			}

			
			if(eventName.equals(activityName2)){
				if(!fp1 && !found){
					found = true;
					fp2 = true;
					long timeDistance1 = XTimeExtension.instance().extractTimestamp(event).getTime();
					long timeDistance2 = XTimeExtension.instance().extractTimestamp(act).getTime();
					timeDiff = timeDistance1 - timeDistance2;
					timeDists.add(timeDiff);
				}else if(fp1){
					exceed = true;
				}
			}
			if(exceed){
				break;
			}
		}
		return timeDists;
	}
	
	}
