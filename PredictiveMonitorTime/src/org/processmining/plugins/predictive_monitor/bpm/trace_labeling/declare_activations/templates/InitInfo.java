package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
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
import org.processmining.plugins.declareminer.visualizing.ConstraintDefinition;

public class InitInfo extends TemplateInfo {

	public Vector<MetricsValues> getMetrics(DeclareMinerInput input, Map<FrequentItemSetType, Map<Set<String>, Float>> frequentItemSetTypeFrequentItemSetSupportMap, Map<DeclareTemplate, List<List<String>>> declareTemplateCandidateDispositionsMap, float alpha, float support,
			XLog log, PrintWriter pw, DeclareTemplate currentTemplate,
			UIPluginContext context, boolean verbose, FindItemSets f){
		Vector<MetricsValues> metricsValues = new Vector<MetricsValues>();
		numberOfDiscoveredConstraints = 0;
		List<List<String>> declareTemplateCandidateDispositionsList = null;
		if(declareTemplateCandidateDispositionsMap.containsKey(DeclareTemplate.Init)){
			declareTemplateCandidateDispositionsList = declareTemplateCandidateDispositionsMap.get(DeclareTemplate.Init);
		}
		for(int k=0; k< declareTemplateCandidateDispositionsList.size(); k++){
			if(context!=null){context.getProgress().inc();}
			MetricsValues values = new MetricsValues();
			String formula = LTLFormula.getFormulaByTemplate(currentTemplate);
			formula = formula.replace( "\""+"A"+"\"" , "\""+declareTemplateCandidateDispositionsList.get(k).get(0)+"\"");
			float supportRule = -1;
			supportRule = computeInitSupport(input, declareTemplateCandidateDispositionsList.get(k), log, pw, alpha);
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




	public Vector<Long> getTimeDistances(DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> activations){
		Vector<Long> timeDists = new Vector<Long>(); 
		timeDists.add((long)0);
		return timeDists;
	}

	
	public Vector<Long> getTimeDistances(HashMap<String,ExtendedEvent>  extEvents, DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> activations, String correlation){
		Vector<Long> timeDists = new Vector<Long>(); 
		timeDists.add((long)0);
		return timeDists;
	}


	static float computeInitSupport(DeclareMinerInput input, List<String> actualParameters, XLog log, PrintWriter pw, float alpha){
		float support;
		int numTraces = 0;
		float satTraces = 0;
		for (XTrace trace : log) {
			numTraces++;
			XEvent event = trace.get(0);
			String label = (XConceptExtension.instance().extractName(event));
			if(input.getAprioriKnowledgeBasedCriteriaSet().contains(AprioriKnowledgeBasedCriteria.AllActivitiesWithEventTypes)){
				if(event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
					label = (XConceptExtension.instance().extractName(event))+"-"+XLifecycleExtension.instance().extractTransition(event);
				}else{
					label = (XConceptExtension.instance().extractName(event))+"-"+input.getReferenceEventType();
				}
			}
			if (label.equals(actualParameters.get(0))) {
				satTraces++;
			}
		}
		support = (satTraces / numTraces);
		return support;
	}

	public MetricsValues computeMetrics(DeclareMinerInput input, DeclareTemplate template, List<String> parametersList, XLog log, PrintWriter pw, float alpha, FindItemSets f){
		MetricsValues values = new MetricsValues();
		String formula = LTLFormula.getFormulaByTemplate(template);
		formula = formula.replace( "\""+"A"+"\"" , "\""+parametersList.get(0)+"\"");
		float supportRule = -1;
		supportRule = computeInitSupport(input, parametersList, log, pw, alpha);
		values.setFormula(formula);
		values.setParameters(parametersList);
		values.setSupportRule(supportRule);
		values.setTemplate(template);
		return values;
	}
}
