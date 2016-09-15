package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
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
import org.processmining.plugins.declareminer.Support;
import org.processmining.plugins.declareminer.apriori.FindItemSets;
import org.processmining.plugins.declareminer.enumtypes.AprioriKnowledgeBasedCriteria;
import org.processmining.plugins.declareminer.enumtypes.DeclareTemplate;
import org.processmining.plugins.declareminer.enumtypes.FrequentItemSetType;
import org.processmining.plugins.declareminer.templates.LTLFormula;
import org.processmining.plugins.declareminer.visualizing.ActivityDefinition;
import org.processmining.plugins.declareminer.visualizing.ConstraintDefinition;
import org.processmining.plugins.declareminer.visualizing.Parameter;

public class NegativeRelationInfo extends TemplateInfo {

	
	public Vector<MetricsValues> getMetrics(DeclareMinerInput input, Map<FrequentItemSetType, Map<Set<String>, Float>> frequentItemSetTypeFrequentItemSetSupportMap, Map<DeclareTemplate, List<List<String>>> declareTemplateCandidateDispositionsMap, float alpha, float support,
			XLog log, PrintWriter pw, DeclareTemplate currentTemplate,
			UIPluginContext context, boolean verbose, FindItemSets f){
		numberOfDiscoveredConstraints = 0;
		Vector<MetricsValues> metricsValues = new Vector<MetricsValues>();
		List<List<String>> declareTemplateCandidateDispositionsList = declareTemplateCandidateDispositionsMap.get(currentTemplate);
		for(int k=0; k< declareTemplateCandidateDispositionsList.size(); k ++){	
			if(context!=null){context.getProgress().inc();}
			MetricsValues values = new MetricsValues();
			float supportRule= 0.f;
			Set<Integer> supportingTraceIndicesPositive = f.getSupportingTraceIndices(declareTemplateCandidateDispositionsList.get(k).get(0),declareTemplateCandidateDispositionsList.get(k).get(1));
			String formula = LTLFormula.getFormulaByTemplate(currentTemplate);
			formula = formula.replace( "\""+"A"+"\"" , "\""+declareTemplateCandidateDispositionsList.get(k).get(0)+"\"");
			formula = formula.replace( "\""+"B"+"\"" , "\""+declareTemplateCandidateDispositionsList.get(k).get(1)+"\"");
			String antecedent = declareTemplateCandidateDispositionsList.get(k).get(0);
			String consequent = declareTemplateCandidateDispositionsList.get(k).get(1);
			float supportAntecedent;
			float actTraces = 0.f;
			for (Integer id : supportingTraceIndicesPositive) {
				XTrace trace = log.get(id);
				Support suppOneMinusAlpha = null;
			//	if(alpha != 1.f){
					suppOneMinusAlpha = checkFormula(input, currentTemplate, formula, antecedent, consequent, trace, true);
					if (suppOneMinusAlpha.isSupportAUB()) {
						actTraces++;
					}
			//	}
			}
			float percaUbAct = actTraces/log.size();
			float cActSupp = f.getSupport("NOT-"+declareTemplateCandidateDispositionsList.get(k).get(0),declareTemplateCandidateDispositionsList.get(k).get(1))/100.f+(f.getSupport(declareTemplateCandidateDispositionsList.get(k).get(0),"NOT-"+declareTemplateCandidateDispositionsList.get(k).get(1))/100.f)+percaUbAct;
			float cSatSupp = f.getSupport("NOT-"+declareTemplateCandidateDispositionsList.get(k).get(0),declareTemplateCandidateDispositionsList.get(k).get(1))/100.f+f.getSupport(declareTemplateCandidateDispositionsList.get(k).get(0),"NOT-"+declareTemplateCandidateDispositionsList.get(k).get(1))/100.f+(f.getSupport("NOT-"+declareTemplateCandidateDispositionsList.get(k).get(0),"NOT-"+declareTemplateCandidateDispositionsList.get(k).get(1))/100.f)+percaUbAct; 
			supportRule = (1.f-alpha)*cActSupp + alpha*cSatSupp;
			Set<Integer> supportingTraceIndicesSet = new HashSet<Integer>();
			if(f.getSupportingTraceIndices(declareTemplateCandidateDispositionsList.get(k).get(0)) == null){
				supportingTraceIndicesSet.addAll(new HashSet<Integer>());
			}else{
				supportingTraceIndicesSet.addAll(f.getSupportingTraceIndices(declareTemplateCandidateDispositionsList.get(k).get(0)));
			}
			if(f.getSupportingTraceIndices(declareTemplateCandidateDispositionsList.get(k).get(1)) == null){
				supportingTraceIndicesSet.addAll(new HashSet<Integer>());
			}else{
				supportingTraceIndicesSet.addAll(f.getSupportingTraceIndices(declareTemplateCandidateDispositionsList.get(k).get(1)));	
			}
			supportAntecedent = supportingTraceIndicesSet.size()/(log.size()*1.0f);
			values.setSuppAntec(supportAntecedent);
			values.setSupportConseq(supportAntecedent);
			float CPIRsat;
			float CPIRact;
			if ((supportAntecedent * (1 - supportAntecedent)) == 0) {
				CPIRact = Float.MAX_VALUE;
			} else {
				CPIRact = (cActSupp - (supportAntecedent * supportAntecedent)) / (supportAntecedent * (1 - supportAntecedent));
			}
			CPIRsat = Float.MAX_VALUE;
			float cpir = (alpha * CPIRsat) + ((1-alpha)* CPIRact);
			float confidence;
			float interestFactor;
			if(supportAntecedent==0){
				confidence = Float.MAX_VALUE;
			}else{
				confidence = (alpha * (cSatSupp / 1.f)) + ((1.f-alpha)*(cActSupp/supportAntecedent));
			}
			if(supportAntecedent==0){
				interestFactor = Float.MAX_VALUE;
			}else{
				interestFactor = (alpha * (cSatSupp / 1.f)) + ((1-alpha)*(cActSupp/(supportAntecedent*supportAntecedent)));
			}
			values.setConfidence(confidence);
			values.setCPIR(cpir);
			values.setI(interestFactor);
			values.setSupportRule(supportRule);
			values.setFormula(formula);
			values.setParameters(declareTemplateCandidateDispositionsList.get(k));
			values.setSupportRule(supportRule);
			if(supportRule>=support){
				numberOfDiscoveredConstraints ++;
			}
			values.setTemplate(currentTemplate);
			printMetrics(pw, formula, supportRule, confidence, supportAntecedent, supportAntecedent, 1, 1, interestFactor, cpir);
			metricsValues.add(values);
		}
		return metricsValues;
	}
	
	
	public MetricsValues computeMetrics(DeclareMinerInput input, DeclareTemplate template, List<String> parametersList, XLog log, PrintWriter pw, float alpha, FindItemSets f){
		MetricsValues values = new MetricsValues();
		float supportRule= 0.f;
		Set<Integer> supportingTraceIndicesPositive = f.getSupportingTraceIndices(parametersList.get(0),parametersList.get(1));
		String formula = LTLFormula.getFormulaByTemplate(template);
		formula = formula.replace( "\""+"A"+"\"" , "\""+parametersList.get(0)+"\"");
		formula = formula.replace( "\""+"B"+"\"" , "\""+parametersList.get(1)+"\"");
		String antecedent = parametersList.get(0);
		String consequent = parametersList.get(1);
		float supportAntecedent;
		float actTraces = 0.f;
		for (Integer id : supportingTraceIndicesPositive) {
			XTrace trace = log.get(id);
			Support suppOneMinusAlpha = null;
		//	if(alpha != 1.f){
				suppOneMinusAlpha = checkFormula(input ,template, formula, antecedent, consequent, trace, true);
				if (suppOneMinusAlpha.isSupportAUB()) {
					actTraces++;
				}
		//	}
		}
		//float percaUbAct = actTraces/log.size();
		float percaUbAct = f.getSupport(parametersList.get(0),parametersList.get(1))/100.f;
		float cActSupp = f.getSupport("NOT-"+parametersList.get(0),parametersList.get(1))/100.f+(f.getSupport(parametersList.get(0),"NOT-"+parametersList.get(1))/100.f)+percaUbAct;
		float cSatSupp = f.getSupport("NOT-"+parametersList.get(0),parametersList.get(1))/100.f+f.getSupport(parametersList.get(0),"NOT-"+parametersList.get(1))/100.f+(f.getSupport("NOT-"+parametersList.get(0),"NOT-"+parametersList.get(1))/100.f)+percaUbAct; 
		supportRule = (1.f-alpha)*cActSupp + alpha*cSatSupp;
		Set<Integer> supportingTraceIndicesSet = new HashSet<Integer>();
		if(f.getSupportingTraceIndices(parametersList.get(0)) == null){
			supportingTraceIndicesSet.addAll(new HashSet<Integer>());
		}else{
			supportingTraceIndicesSet.addAll(f.getSupportingTraceIndices(parametersList.get(0)));
		}
		if(f.getSupportingTraceIndices(parametersList.get(1)) == null){
			supportingTraceIndicesSet.addAll(new HashSet<Integer>());
		}else{
			supportingTraceIndicesSet.addAll(f.getSupportingTraceIndices(parametersList.get(1)));	
		}
		supportAntecedent = supportingTraceIndicesSet.size()/(log.size()*1.0f);
		values.setSuppAntec(supportAntecedent);
		values.setSupportConseq(supportAntecedent);
		float CPIRsat;
		float CPIRact;
		if ((supportAntecedent * (1 - supportAntecedent)) == 0) {
			CPIRact = Float.MAX_VALUE;
		} else {
			CPIRact = (cActSupp - (supportAntecedent * supportAntecedent)) / (supportAntecedent * (1 - supportAntecedent));
		}
		CPIRsat = Float.MAX_VALUE;
		float cpir = (alpha * CPIRsat) + ((1-alpha)* CPIRact);
		float confidence;
		float interestFactor;
		if(supportAntecedent==0){
			confidence = Float.MAX_VALUE;
		}else{
			confidence = (alpha * (cSatSupp / 1.f)) + ((1.f-alpha)*(cActSupp/supportAntecedent));
		}
		if(supportAntecedent==0){
			interestFactor = Float.MAX_VALUE;
		}else{
			interestFactor = (alpha * (cSatSupp / 1.f)) + ((1-alpha)*(cActSupp/(supportAntecedent*supportAntecedent)));
		}
		values.setConfidence(confidence);
		values.setCPIR(cpir);
		values.setI(interestFactor);
		values.setSupportRule(supportRule);
		values.setFormula(formula);
		values.setParameters(parametersList);
		values.setSupportRule(supportRule);
		values.setTemplate(template);
		return values;
	}
	
	public Vector<Long>  getTimeDistances(DeclareMinerInput input,  XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> conflicts){	
		Vector<Long> timeDists = new Vector<Long>(); for(Integer pos : conflicts){
			boolean found = false;
			XEvent act = trace.get(pos);
			ActivityDefinition source = constraintDefinition.getBranches(constraintDefinition.getParameters().iterator().next()).iterator().next();
			String activityName = source.getName();
			String eventName = (XConceptExtension.instance().extractName(act));
			//}
			if(input.getAprioriKnowledgeBasedCriteriaSet().contains(AprioriKnowledgeBasedCriteria.AllActivitiesWithEventTypes)){
				if(!containsEventType(source.getName())){
					if(act.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
						activityName = source.getName()+"-"+input.getReferenceEventType();
						eventName = (XConceptExtension.instance().extractName(act))+"-"+XLifecycleExtension.instance().extractTransition(act);
					}
				}else{
					if(act.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
						eventName = (XConceptExtension.instance().extractName(act))+"-"+XLifecycleExtension.instance().extractTransition(act);
					}else{
						eventName = (XConceptExtension.instance().extractName(act))+"-"+input.getReferenceEventType();
					}
				}
			}
			if(eventName.equals(activityName)){
				for(int c=pos; c<trace.size(); c++){
					XEvent event = trace.get(c);
					Iterator<Parameter> iter = constraintDefinition.getParameters().iterator();
					iter.next();
					Parameter p2 = iter.next();
					ActivityDefinition target = constraintDefinition.getBranches(p2).iterator().next();
					
					activityName = target.getName();
					eventName = (XConceptExtension.instance().extractName(event));
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
						long timeDiff = timeDistance1 - timeDistance2;
						timeDists.add(timeDiff);

					}
					if(found){
						break;
					}
				}
			}
		}
		return timeDists;
	}
	
	
	
	
	
	
	
	public Vector<Long> getTimeDistances(HashMap<String,ExtendedEvent>  extEvents, DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> conflicts, String correlation) {	
		Vector<Long> timeDists = new Vector<Long>(); 
		CorrelationMiner miner = new CorrelationMiner();
		for(Integer pos : conflicts){
			boolean found = false;
			XEvent act = trace.get(pos);
			ActivityDefinition source = constraintDefinition.getBranches(constraintDefinition.getParameters().iterator().next()).iterator().next();
			String activityName = source.getName();
			String eventName = (XConceptExtension.instance().extractName(act));
			//}
			if(input.getAprioriKnowledgeBasedCriteriaSet().contains(AprioriKnowledgeBasedCriteria.AllActivitiesWithEventTypes)){
				if(!containsEventType(source.getName())){
					if(act.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
						activityName = source.getName()+"-"+input.getReferenceEventType();
						eventName = (XConceptExtension.instance().extractName(act))+"-"+XLifecycleExtension.instance().extractTransition(act);
					}
				}else{
					if(act.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
						eventName = (XConceptExtension.instance().extractName(act))+"-"+XLifecycleExtension.instance().extractTransition(act);
					}else{
						eventName = (XConceptExtension.instance().extractName(act))+"-"+input.getReferenceEventType();
					}
				}
			}
			boolean evType = false;
			DeclareModelGenerator gen = new DeclareModelGenerator();
			
			if(gen.hasEventTypeInName(activityName)){
				evType= true;
			}
			if(eventName.equals(activityName)){
				for(int c=pos; c<trace.size(); c++){
					XEvent event = trace.get(c);
					Iterator<Parameter> iter = constraintDefinition.getParameters().iterator();
					iter.next();
					Parameter p2 = iter.next();
					ActivityDefinition target = constraintDefinition.getBranches(p2).iterator().next();
					
					activityName = target.getName();
					eventName = (XConceptExtension.instance().extractName(event));
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
						if(miner.isValid(evType, correlation, act, event, extEvents)){
						found = true;
						long timeDistance1 = XTimeExtension.instance().extractTimestamp(event).getTime();
						long timeDistance2 = XTimeExtension.instance().extractTimestamp(act).getTime();
						long timeDiff = timeDistance1 - timeDistance2;
						timeDists.add(timeDiff);
						}
					}
					if(found){
						break;
					}
				}
			}
		}
		return timeDists;
	}
	
	
	
	
	
	
	public Vector<ExtendedTrace> getNonAmbiguousActivations(XLog log, DeclareTemplate template, String activation, String target){
		// for response 
		Vector<ExtendedTrace> output = new Vector<ExtendedTrace>();




		if(template.equals(DeclareTemplate.Not_Succession)){

			for(XTrace trace: log){
				int pos = 0;
				HashMap<Integer,Vector<Integer>> numbAmb = new HashMap<Integer, Vector<Integer>>();

				ExtendedTrace extr = new ExtendedTrace();
				extr.setTrace(trace);
				extr.setNonambi(new Vector<Integer>());
				extr.setCorrespcorrel(new HashMap<Integer,Vector<Integer>>());
				for(XEvent event : trace){
					String eventName = XConceptExtension.instance().extractName(event);
					if(eventName.equals(activation)){
						numbAmb.put(pos, new Vector<Integer>());
					}
					if(eventName.equals(target)){
						for(int i:numbAmb.keySet()){
							Vector<Integer> trgts = numbAmb.get(i);
							trgts.add(pos);
							numbAmb.put(i, trgts);
						}
					}
					pos++;
				}

				for(int i:numbAmb.keySet()){
					if(numbAmb.get(i).size()>=1){
						Vector<Integer> nonambi = extr.getNonambi();
						nonambi.add(i);
						extr.setNonambi(nonambi);
						HashMap<Integer,Vector<Integer>> corresp = extr.getCorrespcorrel();
						Vector<Integer> c = numbAmb.get(i);
						corresp.put(i,c);
						extr.setCorrespcorrel(corresp);
						
					}
				}
				output.add(extr);
		}


		}
		return output;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
}
