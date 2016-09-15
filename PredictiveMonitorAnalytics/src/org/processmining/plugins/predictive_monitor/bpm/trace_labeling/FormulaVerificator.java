package org.processmining.plugins.predictive_monitor.bpm.trace_labeling;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import ltl2aut.automaton.Automaton;
import ltl2aut.automaton.Transition;
import ltl2aut.formula.DefaultParser;
import ltl2aut.formula.conjunction.ConjunctionFactory;
import ltl2aut.formula.conjunction.ConjunctionTreeLeaf;
import ltl2aut.formula.conjunction.ConjunctionTreeNode;
import ltl2aut.formula.conjunction.DefaultTreeFactory;
import ltl2aut.formula.conjunction.GroupedTreeConjunction;
import ltl2aut.formula.conjunction.TreeFactory;
import ltl2aut.ltl.SyntaxParserException;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.declareminer.ExecutableAutomaton;
import org.processmining.plugins.declareminer.PossibleNodes;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints.AlternatePrecedenceAnalyzer;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints.AlternateResponseAnalyzer;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints.ChainPrecedenceAnalyzer;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints.ChainResponseAnalyzer;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints.NotChainPrecedenceAnalyzer;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints.NotChainResponseAnalyzer;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints.NotPrecedenceAnalyzer;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints.NotRespondedExistenceAnalyzer;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints.NotResponseAnalyzer;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints.PrecedenceAnalyzer;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints.RespondedExistenceAnalyzer;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints.ResponseAnalyzer;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.DataCondFormula;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.SimpleFormula;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.enumeration.DeclareTemplate;
import org.processmining.plugins.predictive_monitor.bpm.utility.DataSnapshotListener;

public class FormulaVerificator {

	public static boolean isTraceViolated(DataSnapshotListener listener, Formula formula, XTrace trace)
	{
		return traceViolatedEvent(listener,formula,trace)!=null;
	}
	
	public static XEvent traceViolatedEvent(DataSnapshotListener listener, Formula formula, XTrace trace){

		String ltlFormula = formula.getLTLFormula();
		List<ltl2aut.formula.Formula> formulaeParsed = new ArrayList<ltl2aut.formula.Formula>();
		boolean violated = true;
		XEvent event = null;
		
		try {
			formulaeParsed.add(new DefaultParser(ltlFormula).parse());
			TreeFactory<ConjunctionTreeNode, ConjunctionTreeLeaf> treeFactory = DefaultTreeFactory.getInstance();
			ConjunctionFactory<? extends GroupedTreeConjunction> conjunctionFactory = GroupedTreeConjunction
					.getFactory(treeFactory);
			GroupedTreeConjunction conjunction = conjunctionFactory.instance(formulaeParsed);
			Automaton aut = conjunction.getAutomaton().op.reduce();
			ExecutableAutomaton execAut = new ExecutableAutomaton(aut);
			execAut.ini();
			PossibleNodes current = null;
	
			if (formula instanceof SimpleFormula)
			{
				XEvent lastEvent=null;
				for(XEvent e : trace)
				{
					lastEvent=e;
					String label = ((XAttributeLiteral) e.getAttributes().get("concept:name")).getValue();
					violated = true;
					current = execAut.currentState();
					if(current!=null && !(current.get(0)==null))
					{
						for (Transition out : current.output()) 
						{
							if (out.parses(label)) 
							{
								violated = false;
								break;
							}
						}
					}
					if(!violated)
					{
						execAut.next(label);
					}
					else
					{
						event=e;
						break;
					}
					current = execAut.currentState();
					lastEvent=e;
				}
				if(!violated)
				{
					if(current.isAccepting())
					{
						violated = false;
					}
					else
					{
						violated = true;
						event=lastEvent;
					}
				}

			} 
			else 
			{
				//if(!violated){
				if(formula instanceof DataCondFormula){ //TODO return correct event
					DataCondFormula dataFormula = (DataCondFormula)formula;
					DeclareTemplate template = dataFormula.getTemplate();
					//String activation = null;
					switch (template) {
						case Response:
							ResponseAnalyzer info = new ResponseAnalyzer();
							violated = info.checkDataConditions(false, listener,((DataCondFormula) formula).getParam1(), ((DataCondFormula) formula).getParam2(), trace, ((DataCondFormula) formula).getDataCondition());
							break;
						case Chain_Response:
							ChainResponseAnalyzer infoch = new ChainResponseAnalyzer();
							violated = infoch.checkDataConditions(false, listener,((DataCondFormula) formula).getParam1(), ((DataCondFormula) formula).getParam2(), trace, ((DataCondFormula) formula).getDataCondition());
							break;
						case Alternate_Response:
							AlternateResponseAnalyzer infoalt = new AlternateResponseAnalyzer();
							violated = infoalt.checkDataConditions(false, listener,((DataCondFormula) formula).getParam1(), ((DataCondFormula) formula).getParam2(), trace, ((DataCondFormula) formula).getDataCondition());
							break;
						case NotChain_Response:
							NotChainResponseAnalyzer infonotch = new NotChainResponseAnalyzer();
							violated = infonotch.checkDataConditions(false, listener,((DataCondFormula) formula).getParam1(), ((DataCondFormula) formula).getParam2(), trace, ((DataCondFormula) formula).getDataCondition());
							break;
						case NotResponse:
							NotResponseAnalyzer infonot = new NotResponseAnalyzer();
							violated = infonot.checkDataConditions(false, listener,((DataCondFormula) formula).getParam1(), ((DataCondFormula) formula).getParam2(), trace, ((DataCondFormula) formula).getDataCondition());
							break;
						case Precedence:
							PrecedenceAnalyzer info2 = new PrecedenceAnalyzer();
							violated = info2.checkDataConditions(false, listener, ((DataCondFormula) formula).getParam2(), ((DataCondFormula) formula).getParam1(), trace, ((DataCondFormula) formula).getDataCondition());
							break;
						case Alternate_Precedence:
							AlternatePrecedenceAnalyzer infoaltpre = new AlternatePrecedenceAnalyzer();
							violated = infoaltpre.checkDataConditions(false, listener, ((DataCondFormula) formula).getParam2(), ((DataCondFormula) formula).getParam1(), trace, ((DataCondFormula) formula).getDataCondition());
							break;
						case Chain_Precedence:
							ChainPrecedenceAnalyzer infochpre = new ChainPrecedenceAnalyzer();
							violated = infochpre.checkDataConditions(false, listener, ((DataCondFormula) formula).getParam2(), ((DataCondFormula) formula).getParam1(), trace, ((DataCondFormula) formula).getDataCondition());
							break;
						case NotChain_Precedence:
							NotChainPrecedenceAnalyzer infonotchpre = new NotChainPrecedenceAnalyzer();
							violated = infonotchpre.checkDataConditions(false, listener, ((DataCondFormula) formula).getParam2(), ((DataCondFormula) formula).getParam1(), trace, ((DataCondFormula) formula).getDataCondition());
							break;
						case NotPrecedence:
							NotPrecedenceAnalyzer infonotpre = new NotPrecedenceAnalyzer();
							violated = infonotpre.checkDataConditions(false, listener, ((DataCondFormula) formula).getParam2(), ((DataCondFormula) formula).getParam1(), trace, ((DataCondFormula) formula).getDataCondition());
							break;
						case Responded_Existence:
							RespondedExistenceAnalyzer info3 = new RespondedExistenceAnalyzer();
							violated = info3.checkDataConditions(false, listener,((DataCondFormula) formula).getParam1(), ((DataCondFormula) formula).getParam2(), trace, ((DataCondFormula) formula).getDataCondition());
							break;
						case NotResponded_Existence:
							NotRespondedExistenceAnalyzer info3not = new NotRespondedExistenceAnalyzer();
							violated = info3not.checkDataConditions(false, listener,((DataCondFormula) formula).getParam1(), ((DataCondFormula) formula).getParam2(), trace, ((DataCondFormula) formula).getDataCondition());
							break;
					}
					
				}	
			}	
		} catch (SyntaxParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(!violated)
		{
			return null;
		}
		return event;
	}
	
	public static Boolean isFormulaVerified( DataSnapshotListener listener, XTrace trace, Vector<Formula> formulas){
		boolean violated = false;
		for (Formula formula : formulas) {	
			violated = violated || isTraceViolated(listener, formula, trace);
		}
		return new Boolean(!violated);
	}
	
	public static Long eventSatisfactionTime(DataSnapshotListener listener, Formula formula,XTrace trace){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		XEvent event;
		Date violation=null;
		event=traceViolatedEvent(listener, formula, trace);
		Formula notFormula=new SimpleFormula("!( "+formula.getLTLFormula()+" )");
		if(event!=null) //if the formula is violated save the timestamp
		{
			try {
					violation= (Date) format.parseObject(event.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return violation.getTime();
		}
		
		else	//else check the violation on (not formula) and save the timestamp
		{
			event=traceViolatedEvent(listener,notFormula, trace);

			if(event!=null) //if the formula is violated save the timestamp
			{
				try {
						violation= (Date) format.parseObject(event.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return violation.getTime();
			}
		}
		//otherwise return the last event
		try {
			violation = (Date) format.parseObject(trace.get(trace.size()-1).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return violation.getTime();
	}
	
	public static Long eventViolationTime(DataSnapshotListener listener, XTrace trace, Vector<Formula> formulas){
		List<Long> formulaTime = new ArrayList<>();

		for (Formula formula : formulas) {
			formulaTime.add(eventSatisfactionTime(listener,formula,trace));
		}
		
		Long min=null;
		for(Long time: formulaTime)
		{
			if(min==null)
			{
				min=time;
			}
			else if(time<min)
			{
				min=time;
			}
		}
		return min;
	}
}
