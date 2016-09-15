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
import org.processmining.plugins.correlation.Correlator;
import org.processmining.plugins.correlation.Disambiguation;
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
import org.processmining.plugins.declareminer.visualizing.DeclareMap;
import org.processmining.plugins.declareminer.visualizing.DeclareMinerOutput;
import org.processmining.plugins.declareminer.visualizing.Parameter;


public class SuccessionInfo extends TemplateInfo{



	public MetricsValues getMetrics(DeclareMinerOutput model, ConstraintDefinition cd, String correlation, DeclareMinerInput input, float alpha, float support,
			XLog log, DeclareTemplate currentTemplate, UIPluginContext context){
		numberOfDiscoveredConstraints = 0;
		//Vector<MetricsValues> metricsValues = new Vector<MetricsValues>();

		//List<List<String>> declareTemplateCandidateDispositionsList = declareTemplateCandidateDispositionsMap.get(currentTemplate);

		ArrayList<String> paramList = new ArrayList<String>();
		paramList.add(cd.getBranches(cd.getParameterWithId(1)).iterator().next().toString());
		paramList.add(cd.getBranches(cd.getParameterWithId(2)).iterator().next().toString());

		//	for(int k=0; k< declareTemplateCandidateDispositionsList.size(); k ++){	
		if(context!=null){context.getProgress().inc();}
		MetricsValues values = null;
		//if(verbose){
		//	values = evaluateSuccession(input, currentTemplate, declareTemplateCandidateDispositionsList.get(k), log, pw,alpha);
		//}else{
		values = evaluateSuccession(model.getModel(), cd, input,  currentTemplate, paramList, log, null,alpha, correlation);
		//}
		values.setTemplate(currentTemplate);
		//	metricsValues.add(values);
		if(values.getSupportRule()>=support){
			numberOfDiscoveredConstraints ++;
		}
		//}
		return values;
	}


	public Vector<MetricsValues> getMetrics(DeclareMinerInput input, Map<FrequentItemSetType, Map<Set<String>, Float>> frequentItemSetTypeFrequentItemSetSupportMap, Map<DeclareTemplate, List<List<String>>> declareTemplateCandidateDispositionsMap, float alpha, float support,
			XLog log, PrintWriter pw, DeclareTemplate currentTemplate,
			UIPluginContext context, boolean verbose, FindItemSets f){
		numberOfDiscoveredConstraints = 0;
		Vector<MetricsValues> metricsValues = new Vector<MetricsValues>();

		List<List<String>> declareTemplateCandidateDispositionsList = declareTemplateCandidateDispositionsMap.get(currentTemplate);



		for(int k=0; k< declareTemplateCandidateDispositionsList.size(); k ++){	
			if(context!=null){context.getProgress().inc();}
			MetricsValues values = null;
			if(verbose){
				values = evaluateSuccession(input, currentTemplate, declareTemplateCandidateDispositionsList.get(k), log, pw,alpha);
			}else{
				values = evaluateSuccession(input,  currentTemplate, declareTemplateCandidateDispositionsList.get(k), log, null,alpha);
			}
			values.setTemplate(currentTemplate);
			metricsValues.add(values);
			if(values.getSupportRule()>=support){
				numberOfDiscoveredConstraints ++;
			}
		}
		return metricsValues;
	}

	public MetricsValues computeMetrics(DeclareMinerInput input, DeclareTemplate template, List<String> parametersList, XLog log, PrintWriter pw, float alpha, FindItemSets f){
		MetricsValues values = null;
		values = evaluateSuccession(input, template, parametersList, log, null,alpha);
		values.setTemplate(template);
		return values;
	}

	public Vector<Long> getTimeDistances(DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> fulfillments) {
		Vector<Long> timeDists = new Vector<Long>();
		for(Integer pos : fulfillments){
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

	
	
	public Vector<Long> getTimeDistances(HashMap<String,ExtendedEvent>  extEvents, DeclareMinerInput input, XTrace trace, ConstraintDefinition constraintDefinition, Set<Integer> fulfillments, String correlation) {
		Vector<Long> timeDists = new Vector<Long>();
		CorrelationMiner miner = new CorrelationMiner();
		for(Integer pos : fulfillments){
			boolean found = false;
			XEvent act = trace.get(pos);
			ActivityDefinition source = constraintDefinition.getBranches(constraintDefinition.getParameters().iterator().next()).iterator().next();
			String activityName = source.getName();
			String eventName = (XConceptExtension.instance().extractName(act));
			//}
			boolean evType = false;
			DeclareModelGenerator gen = new DeclareModelGenerator();
			
			if(gen.hasEventTypeInName(activityName)){
				evType= true;
			}
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

	
	
	public MetricsValues evaluateSuccession(DeclareMap model, ConstraintDefinition cd, DeclareMinerInput input, DeclareTemplate successionTemplate, List<String> actualParameters, XLog log,PrintWriter pw, float alpha, String correlation){

		Vector<String> ads = new Vector<String>();
		for(ActivityDefinition ad : model.getModel().getActivityDefinitions()){
			String activityName = ad.getName();
			if((activityName.contains("-assign")||activityName.contains("-ate_abort")||activityName.contains("-suspend")||activityName.contains("-complete")||activityName.contains("-autoskip")||activityName.contains("-manualskip")||activityName.contains("pi_abort")||activityName.contains("-reassign")||activityName.contains("-resume")||activityName.contains("-schedule")||activityName.contains("-start")||activityName.contains("-unknown")||activityName.contains("-withdraw"))){
				String[] splittedName = ad.getName().split("-");
				activityName = splittedName[0];
				for(int i = 1; i<splittedName.length-1; i++){
					activityName = activityName + "-" + splittedName[i];
				}
			}
			ads.add(activityName);
		}

		HashMap<String, ExtendedEvent> extEvents = ExtendedEvent.getEventsWithAttributeTypes(ads,log);


		String formula = null;
		DeclareTemplate response = null;
		DeclareTemplate precedence = null;
		if(successionTemplate.equals(DeclareTemplate.Succession)){
			response = DeclareTemplate.Response;
			precedence = DeclareTemplate.Precedence; 
		}else if(successionTemplate.equals(DeclareTemplate.Alternate_Succession)){
			response = DeclareTemplate.Alternate_Response;
			precedence = DeclareTemplate.Alternate_Precedence; 
		}else{
			response = DeclareTemplate.Chain_Response;
			precedence = DeclareTemplate.Chain_Precedence; 
		}
		String antecedent = actualParameters.get(0);
		String consequent = actualParameters.get(1);
		formula = LTLFormula.getFormulaByTemplate(response);
		formula = formula.replace( "\""+"A"+"\"" , "\""+actualParameters.get(0)+"\"");
		formula = formula.replace( "\""+"B"+"\"" , "\""+actualParameters.get(1)+"\"");
		Vector<ExtendedTrace> tracesWithCorrespondingEvents = Correlator.getExtendedTracesWithCorrespondingEvents(response, antecedent, consequent, log );
		float supportRule;
		float numTraces = 0;
		float satTraces = 0;
		float actTraces = 0;
		float sata = 0;
		float satb = 0;
		Vector<XTrace> sattraces = new Vector<XTrace>();
		Vector<XTrace> acttraces = new Vector<XTrace>();
		Vector<XTrace> aub = new Vector<XTrace>();

		for (XTrace trace : log) {
			ExtendedTrace ext = tracesWithCorrespondingEvents.get((int)numTraces);
			numTraces++;
			int pos = 0;
			boolean found = false;
			for(XEvent ev: trace){
				if((XConceptExtension.instance().extractName(ev)+"-"+XLifecycleExtension.instance().extractTransition(ev)).equals(antecedent)){
					Disambiguation disambiguator = new Disambiguation();
					Vector<Integer> targets = disambiguator.getTargetsCorrespondingToActivationsAfterDisambiguation(ext, cd, pos, correlation, extEvents);
					if(targets.size()==0){
						found = true;
					}
				}
				pos++;
			}
			if(!found){
				Support suppOneMinusAlpha = null;
				if(alpha != 1.f){
					suppOneMinusAlpha = checkFormula(input,response, formula, antecedent, consequent, trace, true);
					if (suppOneMinusAlpha.isSupportAUB()) {
						actTraces++;
						acttraces.add(trace);
					}
					if (suppOneMinusAlpha.isSupportA()) {
						sata++;
						aub.add(trace);
					}
					if (suppOneMinusAlpha.isSupportB()) {
						if(!aub.contains(trace)){
							aub.add(trace);
						}
					}
				}
				Support suppAlpha = null;
				if(alpha != 0.f){
					suppAlpha = checkFormula(input,response, formula, antecedent, consequent, trace, false);
					if (suppAlpha.isSupportAUB()) {
						satTraces++;
						sattraces.add(trace);
					}
					if(alpha == 1.f){
						if (suppAlpha.isSupportA()) {
							sata++;
							aub.add(trace);
						}
						if (suppAlpha.isSupportB()) {
							if(!aub.contains(trace)){
								aub.add(trace);
							}
						}
					}
				}
			}
		}
		float supportConsequentAct;
		float supportAntecedentAct;
		float supportConsequentSat;
		float supportAntecedentSat;

		supportRule = (alpha * (satTraces / numTraces)) + ((1-alpha)*(actTraces/numTraces));
		supportAntecedentAct = sata / numTraces;
		supportConsequentAct = satb / numTraces;
		supportAntecedentSat = 1;
		supportConsequentSat = satb / numTraces;
		float CPIRsat;
		float CPIRact;
		if ((supportAntecedentAct * (1 - supportConsequentAct)) == 0) {
			CPIRact =Float.MAX_VALUE;
		} else {
			CPIRact = ((actTraces/numTraces) - (supportAntecedentAct * supportConsequentAct)) / (supportAntecedentAct * (1 - supportConsequentAct));
		}
		if ((supportAntecedentSat * (1 - supportConsequentSat)) == 0) {
			CPIRsat = Float.MAX_VALUE;
		} else {
			CPIRsat = ((satTraces / numTraces) - (supportAntecedentSat * supportConsequentSat)) / (supportAntecedentSat * (1 - supportConsequentSat));
		}
		float cpir = (alpha * CPIRsat) + ((1-alpha)* CPIRact);
		MetricsValues result = new MetricsValues();
		float confidence = 0.f;
		float interestFactor = 0.f;
		if(supportAntecedentAct==0 || supportAntecedentSat==0){
			confidence = Float.MAX_VALUE;
		}else{
			confidence=	(alpha * (satTraces / (numTraces*supportAntecedentSat))) + ((1-alpha)*(actTraces/(numTraces*supportAntecedentAct)));
		}
		if(supportAntecedentAct==0 || supportAntecedentSat==0 || supportConsequentAct==0 || supportConsequentSat==0){
			interestFactor = Float.MAX_VALUE;
		}else{
			interestFactor = (alpha * (satTraces / (numTraces*supportAntecedentSat*supportConsequentSat))) + ((1-alpha)*(actTraces/(numTraces*supportAntecedentAct*supportConsequentAct)));
		}

		result.setConfidence(confidence);
		result.setCPIR(cpir);
		result.setFormula(formula);
		result.setI(interestFactor);
		result.setParameters(actualParameters);
		result.setSuppAntec(supportAntecedentAct);
		result.setSupportConseq(supportConsequentAct);
		result.setSupportRule(supportRule);
		result.setTemplate(response);
		result.setSuppAntecSat(supportAntecedentSat);
		result.setSupportConseqSat(supportConsequentSat);
		if(metricsValues4response==null){
			metricsValues4response = new HashMap<String, MetricsValues>();
		}else{
			metricsValues4response.put(response+";"+actualParameters.get(0)+";"+actualParameters.get(1),result);
		}

		formula = LTLFormula.getFormulaByTemplate(precedence);
		antecedent = actualParameters.get(1);
		consequent = actualParameters.get(0);

		tracesWithCorrespondingEvents = Correlator.getExtendedTracesWithCorrespondingEvents(precedence, antecedent, consequent, log );
		formula = formula.replace( "\""+"A"+"\"" , "\""+actualParameters.get(0)+"\"");
		formula = formula.replace( "\""+"B"+"\"" , "\""+actualParameters.get(1)+"\"");
		numTraces = 0;
		satTraces = 0;
		actTraces = 0;
		sata = 0;
		satb = 0;
		float actNum = 0.f;
		float satNum = 0.f;
		for (XTrace trace : log) {
			ExtendedTrace ext = tracesWithCorrespondingEvents.get((int)numTraces);
			numTraces++;
			int pos = 0;
			boolean found = false;
			for(XEvent ev: trace){
				if(XConceptExtension.instance().extractName(ev).equals(antecedent)){
					Disambiguation disambiguator = new Disambiguation();
					Vector<Integer> targets = disambiguator.getTargetsCorrespondingToActivationsAfterDisambiguation(ext, cd, pos, correlation, extEvents);
					if(targets.size()==0){
						found = true;
					}
				}
				pos++;
			}
			if(!found){

				//	numTraces++;
				Support suppOneMinusAlpha = null;
				if(alpha != 1.f){
					suppOneMinusAlpha = checkFormula(input,precedence, formula, antecedent, consequent, trace, true);
					if (suppOneMinusAlpha.isSupportAUB()) {
						actTraces++;
						if(acttraces.contains(trace)){
							actNum ++;
						}
					}
					if (suppOneMinusAlpha.isSupportA()) {
						sata++;
					}
					if (suppOneMinusAlpha.isSupportB()) {
						satb++;
					}
				}
				Support suppAlpha = null;
				if(alpha != 0.f){
					suppAlpha = checkFormula(input,precedence, formula, antecedent, consequent, trace, false);
					if (suppAlpha.isSupportAUB()) {
						satTraces++;
						if(sattraces.contains(trace)){
							satNum++;
						}
					}
				}
			}
		}


		supportRule = (alpha * (satTraces / numTraces)) + ((1-alpha)*(actTraces/numTraces));
		supportAntecedentAct = sata / numTraces;
		supportConsequentAct = satb / numTraces;
		supportAntecedentSat = 1;
		supportConsequentSat = satb / numTraces;
		if ((supportAntecedentAct * (1 - supportConsequentAct)) == 0) {
			CPIRact =Float.MAX_VALUE;
		} else {
			CPIRact = ((actTraces/numTraces) - (supportAntecedentAct * supportConsequentAct)) / (supportAntecedentAct * (1 - supportConsequentAct));
		}
		if ((supportAntecedentSat * (1 - supportConsequentSat)) == 0) {
			CPIRsat = Float.MAX_VALUE;
		} else {
			CPIRsat = ((satTraces / numTraces) - (supportAntecedentSat * supportConsequentSat)) / (supportAntecedentSat * (1 - supportConsequentSat));
		}
		cpir = (alpha * CPIRsat) + ((1-alpha)* CPIRact);
		result = new MetricsValues();
		confidence = 0.f;
		interestFactor = 0.f;
		if(supportAntecedentAct==0 || supportAntecedentSat==0){
			confidence = Float.MAX_VALUE;
		}else{
			confidence=	(alpha * (satTraces / (numTraces*supportAntecedentSat))) + ((1-alpha)*(actTraces/(numTraces*supportAntecedentAct)));
		}
		if(supportAntecedentAct==0 || supportAntecedentSat==0 || supportConsequentAct==0 || supportConsequentSat==0){
			interestFactor = Float.MAX_VALUE;
		}else{
			interestFactor = (alpha * (satTraces / (numTraces*supportAntecedentSat*supportConsequentSat))) + ((1-alpha)*(actTraces/(numTraces*supportAntecedentAct*supportConsequentAct)));
		}

		result.setConfidence(confidence);
		result.setCPIR(cpir);
		result.setFormula(formula);
		result.setI(interestFactor);
		result.setParameters(actualParameters);
		result.setSuppAntec(supportAntecedentAct);
		result.setSupportConseq(supportConsequentAct);
		result.setSuppAntecSat(supportAntecedentSat);
		result.setSupportConseqSat(supportConsequentSat);
		result.setSupportRule(supportRule);
		result.setTemplate(precedence);
		result.setSuppAntecSat(supportAntecedentSat);
		result.setSupportConseqSat(supportConsequentSat);
		if(metricsValues4precedence==null){
			metricsValues4precedence = new HashMap<String, MetricsValues>();
		}else{
			metricsValues4precedence.put(precedence+";"+actualParameters.get(0)+";"+actualParameters.get(1),result);
		}

		supportRule = (alpha * (satNum / numTraces)) + ((1-alpha)*(actNum/numTraces));
		supportAntecedentAct = aub.size() / numTraces;
		supportConsequentAct = aub.size() / numTraces;
		supportConsequentSat = aub.size() / numTraces;
		supportAntecedentSat = 1;
		if ((supportAntecedentAct * (1 - supportConsequentAct)) == 0) {
			CPIRact =Float.MAX_VALUE;
		} else {
			CPIRact = ((actTraces/numTraces) - (supportAntecedentAct * supportConsequentAct)) / (supportAntecedentAct * (1 - supportConsequentAct));
		}
		if ((supportAntecedentSat * (1 - supportConsequentSat)) == 0) {
			CPIRsat = Float.MAX_VALUE;
		} else {
			CPIRsat = ((satTraces / numTraces) - (supportAntecedentSat * supportConsequentSat)) / (supportAntecedentSat * (1 - supportConsequentSat));
		}
		cpir = (alpha * CPIRsat) + ((1-alpha)* CPIRact);
		result = new MetricsValues();
		confidence = 0.f;
		if(supportAntecedentAct==0 || supportAntecedentSat==0){
			confidence = Float.MAX_VALUE;
		}else{
			confidence=	(alpha * (satTraces / (numTraces*supportAntecedentSat))) + ((1-alpha)*(actTraces/(numTraces*supportAntecedentAct)));
		}
		interestFactor = 0.f;
		if(supportAntecedentAct==0 || supportAntecedentSat==0 || supportConsequentAct==0 || supportConsequentSat==0){
			interestFactor = Float.MAX_VALUE;
		}else{
			interestFactor = (alpha * (satTraces / (numTraces*supportAntecedentSat*supportConsequentSat))) + ((1-alpha)*(actTraces/(numTraces*supportAntecedentAct*supportConsequentAct)));
		}
		formula = LTLFormula.getFormulaByTemplate(successionTemplate);
		formula = formula.replace( "\""+"A"+"\"" , "\""+actualParameters.get(0)+"\"");
		formula = formula.replace( "\""+"B"+"\"" , "\""+actualParameters.get(1)+"\"");
		printMetrics(pw, formula, supportRule, confidence, supportAntecedentAct, supportConsequentAct, supportAntecedentSat, supportConsequentSat, interestFactor, cpir);
		result.setConfidence(confidence);
		result.setCPIR(cpir);
		result.setFormula(formula);
		result.setI(interestFactor);
		result.setParameters(actualParameters);
		result.setSuppAntec(supportAntecedentAct);
		result.setSupportConseq(supportConsequentAct);
		result.setSupportRule(supportRule);
		return result;
	}

	public MetricsValues evaluateSuccession(DeclareMinerInput input, DeclareTemplate successionTemplate, List<String> actualParameters, XLog log,PrintWriter pw, float alpha){
		String formula = null;
		DeclareTemplate response = null;
		DeclareTemplate precedence = null;
		if(successionTemplate.equals(DeclareTemplate.Succession)){
			response = DeclareTemplate.Response;
			precedence = DeclareTemplate.Precedence; 
		}else if(successionTemplate.equals(DeclareTemplate.Alternate_Succession)){
			response = DeclareTemplate.Alternate_Response;
			precedence = DeclareTemplate.Alternate_Precedence; 
		}else{
			response = DeclareTemplate.Chain_Response;
			precedence = DeclareTemplate.Chain_Precedence; 
		}
		String antecedent = actualParameters.get(0);
		String consequent = actualParameters.get(1);
		formula = LTLFormula.getFormulaByTemplate(response);
		formula = formula.replace( "\""+"A"+"\"" , "\""+actualParameters.get(0)+"\"");
		formula = formula.replace( "\""+"B"+"\"" , "\""+actualParameters.get(1)+"\"");

		float supportRule;
		float numTraces = 0;
		float satTraces = 0;
		float actTraces = 0;
		float sata = 0;
		float satb = 0;
		Vector<XTrace> sattraces = new Vector<XTrace>();
		Vector<XTrace> acttraces = new Vector<XTrace>();
		Vector<XTrace> aub = new Vector<XTrace>();

		for (XTrace trace : log) {
			numTraces++;
			Support suppOneMinusAlpha = null;
			if(alpha != 1.f){
				suppOneMinusAlpha = checkFormula(input,response, formula, antecedent, consequent, trace, true);
				if (suppOneMinusAlpha.isSupportAUB()) {
					actTraces++;
					acttraces.add(trace);
				}
				if (suppOneMinusAlpha.isSupportA()) {
					sata++;
					aub.add(trace);
				}
				if (suppOneMinusAlpha.isSupportB()) {
					if(!aub.contains(trace)){
						aub.add(trace);
					}
				}
			}
			Support suppAlpha = null;
			if(alpha != 0.f){
				suppAlpha = checkFormula(input,response, formula, antecedent, consequent, trace, false);
				if (suppAlpha.isSupportAUB()) {
					satTraces++;
					sattraces.add(trace);
				}
				if(alpha == 1.f){
					if (suppAlpha.isSupportA()) {
						sata++;
						aub.add(trace);
					}
					if (suppAlpha.isSupportB()) {
						if(!aub.contains(trace)){
							aub.add(trace);
						}
					}
				}
			}
		}

		float supportConsequentAct;
		float supportAntecedentAct;
		float supportConsequentSat;
		float supportAntecedentSat;

		supportRule = (alpha * (satTraces / numTraces)) + ((1-alpha)*(actTraces/numTraces));
		supportAntecedentAct = sata / numTraces;
		supportConsequentAct = satb / numTraces;
		supportAntecedentSat = 1;
		supportConsequentSat = satb / numTraces;
		float CPIRsat;
		float CPIRact;
		if ((supportAntecedentAct * (1 - supportConsequentAct)) == 0) {
			CPIRact =Float.MAX_VALUE;
		} else {
			CPIRact = ((actTraces/numTraces) - (supportAntecedentAct * supportConsequentAct)) / (supportAntecedentAct * (1 - supportConsequentAct));
		}
		if ((supportAntecedentSat * (1 - supportConsequentSat)) == 0) {
			CPIRsat = Float.MAX_VALUE;
		} else {
			CPIRsat = ((satTraces / numTraces) - (supportAntecedentSat * supportConsequentSat)) / (supportAntecedentSat * (1 - supportConsequentSat));
		}
		float cpir = (alpha * CPIRsat) + ((1-alpha)* CPIRact);
		MetricsValues result = new MetricsValues();
		float confidence = 0.f;
		float interestFactor = 0.f;
		if(supportAntecedentAct==0 || supportAntecedentSat==0){
			confidence = Float.MAX_VALUE;
		}else{
			confidence=	(alpha * (satTraces / (numTraces*supportAntecedentSat))) + ((1-alpha)*(actTraces/(numTraces*supportAntecedentAct)));
		}
		if(supportAntecedentAct==0 || supportAntecedentSat==0 || supportConsequentAct==0 || supportConsequentSat==0){
			interestFactor = Float.MAX_VALUE;
		}else{
			interestFactor = (alpha * (satTraces / (numTraces*supportAntecedentSat*supportConsequentSat))) + ((1-alpha)*(actTraces/(numTraces*supportAntecedentAct*supportConsequentAct)));
		}

		result.setConfidence(confidence);
		result.setCPIR(cpir);
		result.setFormula(formula);
		result.setI(interestFactor);
		result.setParameters(actualParameters);
		result.setSuppAntec(supportAntecedentAct);
		result.setSupportConseq(supportConsequentAct);
		result.setSupportRule(supportRule);
		result.setTemplate(response);
		result.setSuppAntecSat(supportAntecedentSat);
		result.setSupportConseqSat(supportConsequentSat);
		if(metricsValues4response==null){
			metricsValues4response = new HashMap<String, MetricsValues>();
		}else{
			metricsValues4response.put(response+";"+actualParameters.get(0)+";"+actualParameters.get(1),result);
		}

		formula = LTLFormula.getFormulaByTemplate(precedence);
		antecedent = actualParameters.get(1);
		consequent = actualParameters.get(0);
		formula = formula.replace( "\""+"A"+"\"" , "\""+actualParameters.get(0)+"\"");
		formula = formula.replace( "\""+"B"+"\"" , "\""+actualParameters.get(1)+"\"");
		numTraces = 0;
		satTraces = 0;
		actTraces = 0;
		sata = 0;
		satb = 0;
		float actNum = 0.f;
		float satNum = 0.f;
		for (XTrace trace : log) {
			numTraces++;
			Support suppOneMinusAlpha = null;
			if(alpha != 1.f){
				suppOneMinusAlpha = checkFormula(input,precedence, formula, antecedent, consequent, trace, true);
				if (suppOneMinusAlpha.isSupportAUB()) {
					actTraces++;
					if(acttraces.contains(trace)){
						actNum ++;
					}
				}
				if (suppOneMinusAlpha.isSupportA()) {
					sata++;
				}
				if (suppOneMinusAlpha.isSupportB()) {
					satb++;
				}
			}
			Support suppAlpha = null;
			if(alpha != 0.f){
				suppAlpha = checkFormula(input,precedence, formula, antecedent, consequent, trace, false);
				if (suppAlpha.isSupportAUB()) {
					satTraces++;
					if(sattraces.contains(trace)){
						satNum++;
					}
				}
			}
		}


		supportRule = (alpha * (satTraces / numTraces)) + ((1-alpha)*(actTraces/numTraces));
		supportAntecedentAct = sata / numTraces;
		supportConsequentAct = satb / numTraces;
		supportAntecedentSat = 1;
		supportConsequentSat = satb / numTraces;
		if ((supportAntecedentAct * (1 - supportConsequentAct)) == 0) {
			CPIRact =Float.MAX_VALUE;
		} else {
			CPIRact = ((actTraces/numTraces) - (supportAntecedentAct * supportConsequentAct)) / (supportAntecedentAct * (1 - supportConsequentAct));
		}
		if ((supportAntecedentSat * (1 - supportConsequentSat)) == 0) {
			CPIRsat = Float.MAX_VALUE;
		} else {
			CPIRsat = ((satTraces / numTraces) - (supportAntecedentSat * supportConsequentSat)) / (supportAntecedentSat * (1 - supportConsequentSat));
		}
		cpir = (alpha * CPIRsat) + ((1-alpha)* CPIRact);
		result = new MetricsValues();
		confidence = 0.f;
		interestFactor = 0.f;
		if(supportAntecedentAct==0 || supportAntecedentSat==0){
			confidence = Float.MAX_VALUE;
		}else{
			confidence=	(alpha * (satTraces / (numTraces*supportAntecedentSat))) + ((1-alpha)*(actTraces/(numTraces*supportAntecedentAct)));
		}
		if(supportAntecedentAct==0 || supportAntecedentSat==0 || supportConsequentAct==0 || supportConsequentSat==0){
			interestFactor = Float.MAX_VALUE;
		}else{
			interestFactor = (alpha * (satTraces / (numTraces*supportAntecedentSat*supportConsequentSat))) + ((1-alpha)*(actTraces/(numTraces*supportAntecedentAct*supportConsequentAct)));
		}

		result.setConfidence(confidence);
		result.setCPIR(cpir);
		result.setFormula(formula);
		result.setI(interestFactor);
		result.setParameters(actualParameters);
		result.setSuppAntec(supportAntecedentAct);
		result.setSupportConseq(supportConsequentAct);
		result.setSuppAntecSat(supportAntecedentSat);
		result.setSupportConseqSat(supportConsequentSat);
		result.setSupportRule(supportRule);
		result.setTemplate(precedence);
		result.setSuppAntecSat(supportAntecedentSat);
		result.setSupportConseqSat(supportConsequentSat);
		if(metricsValues4precedence==null){
			metricsValues4precedence = new HashMap<String, MetricsValues>();
		}else{
			metricsValues4precedence.put(precedence+";"+actualParameters.get(0)+";"+actualParameters.get(1),result);
		}

		supportRule = (alpha * (satNum / numTraces)) + ((1-alpha)*(actNum/numTraces));
		supportAntecedentAct = aub.size() / numTraces;
		supportConsequentAct = aub.size() / numTraces;
		supportConsequentSat = aub.size() / numTraces;
		supportAntecedentSat = 1;
		if ((supportAntecedentAct * (1 - supportConsequentAct)) == 0) {
			CPIRact =Float.MAX_VALUE;
		} else {
			CPIRact = ((actTraces/numTraces) - (supportAntecedentAct * supportConsequentAct)) / (supportAntecedentAct * (1 - supportConsequentAct));
		}
		if ((supportAntecedentSat * (1 - supportConsequentSat)) == 0) {
			CPIRsat = Float.MAX_VALUE;
		} else {
			CPIRsat = ((satTraces / numTraces) - (supportAntecedentSat * supportConsequentSat)) / (supportAntecedentSat * (1 - supportConsequentSat));
		}
		cpir = (alpha * CPIRsat) + ((1-alpha)* CPIRact);
		result = new MetricsValues();
		confidence = 0.f;
		if(supportAntecedentAct==0 || supportAntecedentSat==0){
			confidence = Float.MAX_VALUE;
		}else{
			confidence=	(alpha * (satTraces / (numTraces*supportAntecedentSat))) + ((1-alpha)*(actTraces/(numTraces*supportAntecedentAct)));
		}
		interestFactor = 0.f;
		if(supportAntecedentAct==0 || supportAntecedentSat==0 || supportConsequentAct==0 || supportConsequentSat==0){
			interestFactor = Float.MAX_VALUE;
		}else{
			interestFactor = (alpha * (satTraces / (numTraces*supportAntecedentSat*supportConsequentSat))) + ((1-alpha)*(actTraces/(numTraces*supportAntecedentAct*supportConsequentAct)));
		}
		formula = LTLFormula.getFormulaByTemplate(successionTemplate);
		formula = formula.replace( "\""+"A"+"\"" , "\""+actualParameters.get(0)+"\"");
		formula = formula.replace( "\""+"B"+"\"" , "\""+actualParameters.get(1)+"\"");
		printMetrics(pw, formula, supportRule, confidence, supportAntecedentAct, supportConsequentAct, supportAntecedentSat, supportConsequentSat, interestFactor, cpir);
		result.setConfidence(confidence);
		result.setCPIR(cpir);
		result.setFormula(formula);
		result.setI(interestFactor);
		result.setParameters(actualParameters);
		result.setSuppAntec(supportAntecedentAct);
		result.setSupportConseq(supportConsequentAct);
		result.setSupportRule(supportRule);
		return result;
	}


}
