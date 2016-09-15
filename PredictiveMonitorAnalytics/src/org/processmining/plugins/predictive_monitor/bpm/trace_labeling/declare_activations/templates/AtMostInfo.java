package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.plugins.declareminer.DeclareMinerInput;
import org.processmining.plugins.declareminer.MetricsValues;
import org.processmining.plugins.declareminer.VectorBasedConstraints;
import org.processmining.plugins.declareminer.apriori.FindItemSets;
import org.processmining.plugins.declareminer.enumtypes.DeclareTemplate;
import org.processmining.plugins.declareminer.enumtypes.FrequentItemSetType;
import org.processmining.plugins.declareminer.templates.LTLFormula;


public abstract class AtMostInfo extends TemplateInfo {

	public Vector<MetricsValues> getMetrics(DeclareMinerInput input, Map<FrequentItemSetType, Map<Set<String>, Float>> frequentItemSetTypeFrequentItemSetSupportMap, Map<DeclareTemplate, List<List<String>>> declareTemplateCandidateDispositionsMap, float alpha, float support,
			XLog log, PrintWriter pw, DeclareTemplate currentTemplate,
			UIPluginContext context, boolean verbose, FindItemSets f){
		numberOfDiscoveredConstraints = 0;
		Vector<MetricsValues> metricsValues = new Vector<MetricsValues>();
		List<List<String>> declareTemplateCandidateDispositionsList;
		declareTemplateCandidateDispositionsList = declareTemplateCandidateDispositionsMap.get(currentTemplate);
		VectorBasedConstraints  v =  new VectorBasedConstraints(log,input);
		for(int k=0; k< declareTemplateCandidateDispositionsList.size(); k++){
			if(context!=null){context.getProgress().inc();}
			MetricsValues values = new MetricsValues();
			float supportRule = -1;
			String formula = LTLFormula.getFormulaByTemplate(currentTemplate);

			formula = formula.replace( "\""+"A"+"\"" , "\""+declareTemplateCandidateDispositionsList.get(k).get(0)+"\"");
			if (currentTemplate.equals(DeclareTemplate.Absence2)) {
				if(v.getAtmostExistenceActivatedAndSatisfiedTraces(declareTemplateCandidateDispositionsList.get(k).get(0),1)==null){
					supportRule = 0;
				}else{
					supportRule = ((float)v.getAtmostExistenceActivatedAndSatisfiedTraces(declareTemplateCandidateDispositionsList.get(k).get(0), 1).size())/log.size();
				}
			}else if (currentTemplate.equals(DeclareTemplate.Absence3)) {
				if(v.getAtmostExistenceActivatedAndSatisfiedTraces(declareTemplateCandidateDispositionsList.get(k).get(0),2)==null){
					supportRule = 0;
				}else{
					supportRule = ((float)v.getAtmostExistenceActivatedAndSatisfiedTraces(declareTemplateCandidateDispositionsList.get(k).get(0), 2).size())/log.size();
				}
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
		VectorBasedConstraints  v =  new VectorBasedConstraints(log, input);
		formula = formula.replace( "\""+"A"+"\"" , "\""+parametersList.get(0)+"\"");
		if (template.equals(DeclareTemplate.Absence2)) {
			if(v.getAtmostExistenceActivatedAndSatisfiedTraces(parametersList.get(0), 1)==null){
				supportRule = 0;
			}else{
				supportRule = ((float)v.getAtmostExistenceActivatedAndSatisfiedTraces(parametersList.get(0), 1).size())/log.size();
			}
		}else if (template.equals(DeclareTemplate.Absence3)) {
			if(v.getAtmostExistenceActivatedAndSatisfiedTraces(parametersList.get(0), 2)==null){
				supportRule = 0;
			}else{
				supportRule = ((float)v.getAtmostExistenceActivatedAndSatisfiedTraces(parametersList.get(0), 2).size())/log.size();
			}
		}
		values.setSupportRule(supportRule);
		values.setFormula(formula);
		values.setParameters(parametersList);
		values.setTemplate(template);
		return values;
	}
	
	

	
	
}

