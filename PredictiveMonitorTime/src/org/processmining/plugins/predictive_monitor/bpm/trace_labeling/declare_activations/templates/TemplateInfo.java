package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import ltl2aut.automaton.Automaton;
import ltl2aut.automaton.Transition;
import ltl2aut.formula.DefaultParser;
import ltl2aut.formula.Formula;
import ltl2aut.formula.conjunction.ConjunctionFactory;
import ltl2aut.formula.conjunction.ConjunctionTreeLeaf;
import ltl2aut.formula.conjunction.ConjunctionTreeNode;
import ltl2aut.formula.conjunction.DefaultTreeFactory;
import ltl2aut.formula.conjunction.GroupedTreeConjunction;
import ltl2aut.formula.conjunction.TreeFactory;
import ltl2aut.ltl.SyntaxParserException;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.plugins.correlation.Correlator;
import org.processmining.plugins.correlation.Disambiguation;
import org.processmining.plugins.correlation.ExtendedEvent;
import org.processmining.plugins.correlation.ExtendedTrace;
import org.processmining.plugins.declareminer.DeclareMinerInput;
import org.processmining.plugins.declareminer.ExecutableAutomaton;
import org.processmining.plugins.declareminer.MetricsValues;
import org.processmining.plugins.declareminer.PossibleNodes;
import org.processmining.plugins.declareminer.Support;
import org.processmining.plugins.declareminer.apriori.FindItemSets;
import org.processmining.plugins.declareminer.enumtypes.AprioriKnowledgeBasedCriteria;
import org.processmining.plugins.declareminer.enumtypes.DeclareTemplate;
import org.processmining.plugins.declareminer.enumtypes.FrequentItemSetType;
import org.processmining.plugins.declareminer.templates.LTLFormula;
import org.processmining.plugins.declareminer.visualizing.ActivityDefinition;
import org.processmining.plugins.declareminer.visualizing.ConstraintDefinition;
import org.processmining.plugins.declareminer.visualizing.DeclareMap;

public abstract class TemplateInfo {

	protected int numberOfDiscoveredConstraints;
	protected HashMap<String, MetricsValues> metricsValues4response;
	protected HashMap<String, MetricsValues> metricsValues4precedence;

//	public abstract MetricsValues getMetrics(DeclareMinerOutput model, ConstraintDefinition cd, String correlation, DeclareMinerInput input, float alpha, float support,
//			XLog log, DeclareTemplate currentTemplate,
//			UIPluginContext context);

	public abstract Vector<MetricsValues> getMetrics(DeclareMinerInput input, Map<FrequentItemSetType, Map<Set<String>, Float>> frequentItemSetTypeFrequentItemSetSupportMap, Map<DeclareTemplate, List<List<String>>> declareTemplateCandidateDispositionsMap, float alpha, float support,
			XLog log, PrintWriter pw, DeclareTemplate currentTemplate,
			UIPluginContext context, boolean verbose, FindItemSets f);
	
	public abstract Vector<Long> getTimeDistances(DeclareMinerInput input, XTrace t, ConstraintDefinition cd, Set<Integer> activations);
	public abstract Vector<Long> getTimeDistances(HashMap<String,ExtendedEvent>  extEvents, DeclareMinerInput input, XTrace t, ConstraintDefinition cd, Set<Integer> activations, String correlation);

		public MetricsValues computeMetrics(DeclareMinerInput input, DeclareTemplate template, List<String> parametersList, XLog log, PrintWriter pw, float alpha, FindItemSets f){
			
				
			String formula = LTLFormula.getFormulaByTemplate(template); 
			String antecedent = "";
			String consequent = "";
			if (template.equals(DeclareTemplate.Precedence) || template.equals(DeclareTemplate.Alternate_Precedence) ||
					template.equals(DeclareTemplate.Chain_Precedence)) {
				antecedent = parametersList.get(1);
				consequent = parametersList.get(0);
			}else{
				antecedent = parametersList.get(0);
				consequent = parametersList.get(1);
			}
			formula = formula.replace( "\""+"A"+"\"" , "\""+parametersList.get(0)+"\"");
			formula = formula.replace( "\""+"B"+"\"" , "\""+parametersList.get(1)+"\"");
			float supportRule;
			float supportConsequentAct;
			float supportAntecedentAct;
			float supportConsequentSat;
			float supportAntecedentSat;
			float numTraces = 0;
			float satTraces = 0;
			float actTraces = 0;
			float sata = 0;
			float satb = 0;
			Vector<XTrace> traces = new Vector<XTrace>();
			for (XTrace trace : log) {
				numTraces++;
				Support suppOneMinusAlpha = null;
				if(alpha != 1.f){
					suppOneMinusAlpha = checkFormula(input, template,formula, antecedent, consequent, trace, true);
					if (suppOneMinusAlpha.isSupportAUB()) {
						actTraces++;
						traces.add(trace);
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
					suppAlpha = checkFormula(input, template,formula, antecedent, consequent, trace, false);
					if (suppAlpha.isSupportAUB()) {
						satTraces++;
					}
					if(alpha == 1.f){
						if (suppAlpha.isSupportA()) {
							sata++;
						}
						if (suppAlpha.isSupportB()) {
							satb++;
						}	
					}
				}
			}
			
			supportRule = (alpha * (satTraces / numTraces)) + ((1-alpha)*(actTraces/numTraces));
			supportAntecedentAct = sata / numTraces;
			supportConsequentAct = satb / numTraces;
			supportAntecedentSat = 1;
			supportConsequentSat = satb / numTraces;
			
	//		System.out.println(parametersList.get(0)+","+parametersList.get(1)+" suppRule: "+supportRule);
			
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
			printMetrics(pw, formula, supportRule, confidence, supportAntecedentAct, supportConsequentAct, supportAntecedentSat, supportConsequentSat, interestFactor, cpir);
			result.setConfidence(confidence);
			result.setCPIR(cpir);
			result.setFormula(formula);
			result.setI(interestFactor);
			result.setParameters(parametersList);
			result.setSuppAntec(supportAntecedentAct);
			result.setSupportConseq(supportConsequentAct);
			result.setSupportRule(supportRule);
			result.setTemplate(template);
			return result;
		}


	public MetricsValues computeMetrics(DeclareMap model, ConstraintDefinition cd, DeclareMinerInput input, DeclareTemplate template, List<String> parametersList, XLog log, float alpha, String correlation){
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
		String formula = LTLFormula.getFormulaByTemplate(template); 
		String antecedent = "";
		String consequent = "";

		Vector<ExtendedTrace> tracesWithCorrespondingEvents; 

		if (template.equals(DeclareTemplate.Precedence) || template.equals(DeclareTemplate.Alternate_Precedence) ||
				template.equals(DeclareTemplate.Chain_Precedence)) {
			antecedent = parametersList.get(1);
			consequent = parametersList.get(0);
		}else{
			antecedent = parametersList.get(0);
			consequent = parametersList.get(1);
		}
		tracesWithCorrespondingEvents = Correlator.getExtendedTracesWithCorrespondingEvents(template, antecedent, consequent, log );
		formula = formula.replace( "\""+"A"+"\"" , "\""+parametersList.get(0)+"\"");
		formula = formula.replace( "\""+"B"+"\"" , "\""+parametersList.get(1)+"\"");
		float supportRule;
		float supportConsequentAct;
		float supportAntecedentAct;
		float supportConsequentSat;
		float supportAntecedentSat;
		float numTraces = 0;
		float satTraces = 0;
		float actTraces = 0;
		float sata = 0;
		float satb = 0;
		Vector<XTrace> traces = new Vector<XTrace>();
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
					suppOneMinusAlpha = checkFormula(input, template,formula, antecedent, consequent, trace, true);
					if (suppOneMinusAlpha.isSupportAUB()) {
						actTraces++;
						traces.add(trace);
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
					suppAlpha = checkFormula(input, template,formula, antecedent, consequent, trace, false);
					if (suppAlpha.isSupportAUB()) {
						satTraces++;
					}
					if(alpha == 1.f){
						if (suppAlpha.isSupportA()) {
							sata++;
						}
						if (suppAlpha.isSupportB()) {
							satb++;
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

		//		System.out.println(parametersList.get(0)+","+parametersList.get(1)+" suppRule: "+supportRule);

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
	//	printMetrics(pw, formula, supportRule, confidence, supportAntecedentAct, supportConsequentAct, supportAntecedentSat, supportConsequentSat, interestFactor, cpir);
		result.setConfidence(confidence);
		result.setCPIR(cpir);
		result.setFormula(formula);
		result.setI(interestFactor);
		result.setParameters(parametersList);
		result.setSuppAntec(supportAntecedentAct);
		result.setSupportConseq(supportConsequentAct);
		result.setSupportRule(supportRule);
		result.setTemplate(template);
		return result;
	}



	public Support checkFormula(DeclareMinerInput input, DeclareTemplate template, String formula, String antecedent, String subsequent, XTrace trace,
			boolean activated) {
		List<Formula> formulaeParsed = new ArrayList<Formula>();
		boolean a = false;
		boolean b = false;
		boolean aUb = false;
		try {
			formulaeParsed.add(new DefaultParser(formula).parse());
			TreeFactory<ConjunctionTreeNode, ConjunctionTreeLeaf> treeFactory = DefaultTreeFactory.getInstance();
			ConjunctionFactory<? extends GroupedTreeConjunction> conjunctionFactory = GroupedTreeConjunction
					.getFactory(treeFactory);
			GroupedTreeConjunction conjunction = conjunctionFactory.instance(formulaeParsed);
			Automaton aut = conjunction.getAutomaton().op.reduce();
			ExecutableAutomaton execAut = new ExecutableAutomaton(aut);
			execAut.ini();

			PossibleNodes current = null;
			boolean viol = false;
			for(XEvent event : trace){
				boolean violated = true;
				String label = (XConceptExtension.instance().extractName(event));
				if(input.getAprioriKnowledgeBasedCriteriaSet()==null || input.getAprioriKnowledgeBasedCriteriaSet().contains(AprioriKnowledgeBasedCriteria.AllActivitiesWithEventTypes)){
					if(event.getAttributes().get(XLifecycleExtension.KEY_TRANSITION)!=null){
						label = (XConceptExtension.instance().extractName(event))+"-"+XLifecycleExtension.instance().extractTransition(event);
					}else{
						label = (XConceptExtension.instance().extractName(event))+"-"+input.getReferenceEventType();
					}
				}
				if (label.equals(antecedent)) {
					a = true;
				}
				if (label.equals(subsequent)) {
					b = true;
				}
				if (!viol) {
					current = execAut.currentState();
					for (Transition out : current.output()) {
						if (out.parses(label)) {
							violated = false;
							break;
						}
					}
					if (!violated) {
						execAut.next(label);
					} else {
						aUb = false;
						viol = true;
					}
				}
			}
			if (!viol) {
				current = execAut.currentState();
				if (current.isAccepting() && !activated) {
					aUb = true;
				}
				if(isPositiveRelation(template)){
					if (current.isAccepting() && activated && a) {
						aUb = true;
					}

				}
				if(isNegativeRelation(template)){
					if (current.isAccepting() && activated && a) {
						aUb = true;
					}

				}
			}
		} catch (SyntaxParserException e) {
			e.printStackTrace();
		}
		Support supp = new Support();
		supp.setSupportA(a);
		supp.setSupportAUB(aUb);
		supp.setSupportB(b);
		return supp;
	}

	protected void printMetrics(PrintWriter pw, String formula, float supportRule){
		if(pw!=null){
			pw.println("  ");
			pw.println("analyzed rule: "+formula);
			pw.println("support(rule) for rule "+formula+":"+supportRule);
			pw.flush();
		}
	}

	protected void printMetrics(PrintWriter pw, String formula, float supportRule, float confidence, float supportAntecedentAct,  float supportConsequentAct,  float supportAntecedentSat, float supportConsequentSat, float interestFactor, float cpir){
		if(pw!=null){
			pw.println("  ");
			pw.println("analyzed rule: "+formula);
			pw.println("support(antecedent) for rule "+formula+":"+supportAntecedentAct+" (for activation)");
			pw.println("support(consequent) for rule "+formula+":"+supportConsequentAct+" (for activation)");
			pw.println("support(antecedent) for rule "+formula+":"+supportAntecedentSat+" (for satisfaction)");
			pw.println("support(consequent) for rule "+formula+":"+supportConsequentSat+" (for satisfaction)");
			pw.println("support(rule) for rule "+formula+":"+supportRule);
			pw.println("confidence(rule) for rule "+formula+":"+confidence);
			pw.println("CPIR(rule) for rule "+formula+":"+cpir);
			pw.println("I(rule) for rule "+formula+":"+interestFactor);
			pw.flush();
		}
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

	protected boolean isPositiveRelation(DeclareTemplate template){
		return template.equals(DeclareTemplate.Alternate_Precedence) || template.equals(DeclareTemplate.Alternate_Response) ||
				template.equals(DeclareTemplate.Alternate_Succession) || template.equals(DeclareTemplate.Chain_Precedence) ||
				template.equals(DeclareTemplate.Chain_Response) || template.equals(DeclareTemplate.Chain_Succession) ||
				template.equals(DeclareTemplate.CoExistence) || template.equals(DeclareTemplate.Precedence) ||
				template.equals(DeclareTemplate.Responded_Existence) || template.equals(DeclareTemplate.Response) ||
				template.equals(DeclareTemplate.Succession);
	}

	protected boolean isNegativeRelation(DeclareTemplate template){
		return template.equals(DeclareTemplate.Not_CoExistence) || template.equals(DeclareTemplate.Not_Succession) || template.equals(DeclareTemplate.Not_Chain_Succession);
	}


	public int getNumberOfDiscoveredConstraints() {
		return numberOfDiscoveredConstraints;
	}

	public void setNumberOfDiscoveredConstraints(int numberOfDiscoveredConstraints) {
		this.numberOfDiscoveredConstraints = numberOfDiscoveredConstraints;
	}

	public HashMap<String, MetricsValues> getMetricsValues4response() {
		return metricsValues4response;
	}

	public void setMetricsValues4response(HashMap<String, MetricsValues> metricsValues4response) {
		this.metricsValues4response = metricsValues4response;
	}

	public HashMap<String, MetricsValues> getMetricsValues4precedence() {
		return metricsValues4precedence;
	}

	public void setMetricsValues4precedence(HashMap<String, MetricsValues> metricsValues4precedence) {
		this.metricsValues4precedence = metricsValues4precedence;
	}

	public Vector<ExtendedTrace> getNonAmbiguousActivations(XLog log, DeclareTemplate template, String activation, String target){ 
		return new Vector<ExtendedTrace>();
	}
}
