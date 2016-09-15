package org.processmining.plugins.predictive_monitor.bpm.pattern_mining.sequence_mining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.PatternManager;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.FormulaVerificator;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.SimpleFormula;
import org.processmining.plugins.predictive_monitor.bpm.utility.DataSnapshotListener;
import org.processmining.plugins.predictive_monitor.bpm.utility.LogReaderAndReplayer;
import org.processmining.plugins.predictive_monitor.bpm.utility.XLogReader;

public class DiscriminativeSequenceMiner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String inputLogFilePath = "./input/sellingProcess.mxml";
		String outputLogFilePath = "./output/output.txt";
		
		try {
			XLog log = XLogReader.openLog(inputLogFilePath);
			Vector<Formula> formulas = new Vector<Formula>();
			Formula formula = new SimpleFormula();
			formulas.add(formula);
			List<Pattern> minedPatterns = mineDiscriminativeFrequentPatternsWithHoles(log, 2, 2, formulas, 2,2);
			//List<Pattern> minedPatterns = mineDiscriminativeFrequentPatternsWithoutHoles(log, 0.8, 2);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	public static List<Pattern> mineDiscriminativeFrequentPatternsWithHoles(XLog log, int minSup, int minLength, Vector<Formula> formulas, int minPositiveSupp, int minNegativeSupp){
		List<Pattern> frequentPatterns = SequenceMiner.mineFrequentPatternsMaxSPWithHoles(log, minSup, minLength);
		List<Pattern> discriminativePatterns = mineDiscriminativePatterns(log, frequentPatterns, formulas, minPositiveSupp, minNegativeSupp);
		return discriminativePatterns;
	}
	
	
	public static List<Pattern> mineDiscriminativePatterns(XLog log, List<Pattern> frequentPatterns, Vector<Formula> formulas, int minPositiveSupp, int minNegativeSupp){
		List<Pattern> discriminativePatterns = new ArrayList<Pattern>();
		try {
			LogReaderAndReplayer replayer = new LogReaderAndReplayer(log);
			DataSnapshotListener listener = new DataSnapshotListener(replayer.getDataTypes(), replayer.getActivityLabels());	
			for (Pattern pattern : frequentPatterns) {
				ArrayList asboluteSupportValues = getAbsoluteSupportValues(log, listener, pattern.getSequencesID(), formulas);
				Integer absolutePositiveSupportValue =(Integer) asboluteSupportValues.get(0);
				Integer absoluteNegativeSupportValue =(Integer) asboluteSupportValues.get(1);
				if ((absolutePositiveSupportValue>=minPositiveSupp) && !(absoluteNegativeSupportValue>=minNegativeSupp))
					discriminativePatterns.add(pattern);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return discriminativePatterns;
	}
	
	public static ArrayList <Pattern> mineDiscriminativePatterns(XLog log, List<Pattern> frequentPatterns, HashMap<String, String> histTraceFormulaSatisfaction, double minPositiveSupp, double minNegativeSupp){
		ArrayList<Pattern> discriminativePatterns = new ArrayList<Pattern>();
		try {
			int satisfiedTraceNumber = 0;

			for (String traceId : histTraceFormulaSatisfaction.keySet()) {
				if (histTraceFormulaSatisfaction.get(traceId)=="yes")
					satisfiedTraceNumber++;
			}
			int minAbsPositiveSupport = (int) (minPositiveSupp*satisfiedTraceNumber);
			int minAbsNegativeSupport = (int) (minNegativeSupp*(log.size()-satisfiedTraceNumber));
			
			for (Pattern pattern : frequentPatterns) {
				ArrayList<XTrace> traces = PatternManager.getTracesContainingPattern(pattern, log);
				ArrayList asboluteSupportValues = getAbsoluteSupportValues(log, histTraceFormulaSatisfaction, traces);
				Integer absolutePositiveSupportValue =(Integer) asboluteSupportValues.get(0);
				Integer absoluteNegativeSupportValue =(Integer) asboluteSupportValues.get(1);
				if ((absolutePositiveSupportValue>=minAbsPositiveSupport) && !(absoluteNegativeSupportValue>=minAbsNegativeSupport))
					discriminativePatterns.add(pattern);
				if ((absoluteNegativeSupportValue>=minAbsNegativeSupport) && !(absolutePositiveSupportValue>=minAbsPositiveSupport))
					discriminativePatterns.add(pattern);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return discriminativePatterns;
	}
	
	/***************
	 * ArrayList[0] -> ABSOLUTE POSITIVE SUPPORT VALUE
	 * ArrayList[1] -> ABSOLUTE NEGATIVE SUPPORT VALUE
	 ****************
	 *	 */
	public static ArrayList<Integer> getAbsoluteSupportValues(XLog log, DataSnapshotListener listener, Set<Integer> traceIndexes, Vector<Formula> formulas){
		ArrayList<Integer> absoluteSupportValues = new ArrayList<Integer>();
		absoluteSupportValues.add(1);
		absoluteSupportValues.add(1);
		for (Integer traceIndex : traceIndexes) {
			if (FormulaVerificator.isFormulaVerified(listener, log.get(traceIndex), formulas).equals("no")) //TODO change to support also time intervals
				absoluteSupportValues.set(0, absoluteSupportValues.get(0)+1);
			
		}
		return absoluteSupportValues;
	}
	
	
	public static ArrayList<Integer> getAbsoluteSupportValues(XLog log, HashMap<String, String> histTraceFormulaSatisfaction, ArrayList<XTrace> traces){
		ArrayList<Integer> absoluteSupportValues = new ArrayList<Integer>();
		absoluteSupportValues.add(0);
		absoluteSupportValues.add(0);
		for (XTrace trace : traces) {
			if (histTraceFormulaSatisfaction.get(XConceptExtension.instance().extractName(trace)).equals("yes")) //TODO change to support also time intervals
				absoluteSupportValues.set(0, absoluteSupportValues.get(0)+1);
			else 
				absoluteSupportValues.set(1, absoluteSupportValues.get(1)+1);
		}
		return absoluteSupportValues;
	}
	

	

	
/*	private static boolean isVerifiedFormula(XLog log, XTrace trace, Vector<Formula> formulas){
		boolean verified = false;

		 
		try {	
			LogReaderAndReplayer replayer = new LogReaderAndReplayer(log);
			DataSnapshotListener listener = new DataSnapshotListener(replayer.getDataTypes(), replayer.getActivityLabels());

			for (Formula formula : formulas) {
				boolean violated = true;
				
				String ltlFormula = formula.getLTLFormula();
				List<ltl2aut.formula.Formula> formulaeParsed = new ArrayList<ltl2aut.formula.Formula>();
				formulaeParsed.add(new DefaultParser(ltlFormula).parse());
				TreeFactory<ConjunctionTreeNode, ConjunctionTreeLeaf> treeFactory = DefaultTreeFactory.getInstance();
				ConjunctionFactory<? extends GroupedTreeConjunction> conjunctionFactory = GroupedTreeConjunction
						.getFactory(treeFactory);
				GroupedTreeConjunction conjunction = conjunctionFactory.instance(formulaeParsed);
				Automaton aut = conjunction.getAutomaton().op.reduce();
				ExecutableAutomaton execAut = new ExecutableAutomaton(aut);
				execAut.ini();
				//	PossibleNodes current = execAut.currentState();


				ArrayList<String> datalines = new ArrayList<String>();

				//	HashMap<String, String> dataValues = new HashMap<String, String>();
				String currentPrefix = "";
				HashMap<String, ArrayList<String>> attributeTypes = new HashMap<String, ArrayList<String>>();
				HashMap<String, ArrayList<String>> tempAttributeTypes = new HashMap<String, ArrayList<String>>();

				tempAttributeTypes = new HashMap<String, ArrayList<String>>();
				currentPrefix = "";
				//String dataValues = "";
				XAttributeMap traceAttr = trace.getAttributes();
				for(String attribute : traceAttr.keySet()){
					
					if(!attribute.equals("Activity code") &&!attribute.equals("creator")&&!attribute.contains(":")&& !attribute.equals("description")){
						ArrayList<String> value = null;
						if(attributeTypes.get(attribute)==null){
							try{
								if(!attribute.equals("Activity code")){
									new Integer(traceAttr.get(attribute).toString());
									value = new ArrayList<String>();
									value.add("numeric");
								}
							}catch(NumberFormatException ex){
								try{
									if(!attribute.equals("Activity code")){
										new Double(traceAttr.get(attribute).toString());
										value = new ArrayList<String>();
										value.add("numeric");	
									}
								}catch(NumberFormatException exc){
									try {
										DatatypeConverter.parseDateTime(traceAttr.get(attribute).toString());
										value = new ArrayList<String>();
										value.add("date");
									} catch (IllegalArgumentException e) {
										value = new ArrayList<String>();
										value.add(traceAttr.get(attribute).toString());
									}
								}
							}
						}else{
							value = attributeTypes.get(attribute);
							if(!value.contains(traceAttr.get(attribute).toString())){
								value.add(traceAttr.get(attribute).toString());
							}
						}
						attributeTypes.put(attribute, value);
						//	dataValues.put(attribute, traceAttr.get(attribute).toString());
					}
				}

				for(XEvent e : trace){

					String label = ((XAttributeLiteral) e.getAttributes().get("concept:name")).getValue();
					currentPrefix = currentPrefix+label+";";
					XAttributeMap eventAttr = e.getAttributes();


					for(String attribute : eventAttr.keySet()){
						if(!attribute.equals("Activity code")&&!attribute.contains(":")){
							ArrayList<String> value = null;
							if(attributeTypes.get(attribute)==null){
								try{
									if(!attribute.equals("Activity code")){
										new Integer(eventAttr.get(attribute).toString());
										value = new ArrayList<String>();
										value.add("numeric");
									}
								}catch(NumberFormatException ex){
									try{
										if(!attribute.equals("Activity code")){
											new Double(eventAttr.get(attribute).toString());
											value = new ArrayList<String>();
											value.add("numeric");
										}
									}catch(NumberFormatException exc){
										try {
											DatatypeConverter.parseDateTime(eventAttr.get(attribute).toString());
											value = new ArrayList<String>();
											value.add("date");
										} catch (IllegalArgumentException exce) {
											value = new ArrayList<String>();
											value.add(eventAttr.get(attribute).toString());
										}
									}
								}
							}else{
								value = attributeTypes.get(attribute);
								if(!value.contains(eventAttr.get(attribute).toString())){
									value.add(eventAttr.get(attribute).toString());
								}
							}
							tempAttributeTypes.put(attribute, value);
							//if(all || (label.equals(inputActivity) && (currentPrefix.equals(requiredPrefix)))){
							for(String attrib : tempAttributeTypes.keySet()){
								attributeTypes.put(attrib, tempAttributeTypes.get(attrib));
							}

						}


					}
				}


				HashMap<String, String> dataValues = new HashMap<String, String>();
				ArrayList<HashMap<String, String>> dataPerTrace = new ArrayList<HashMap<String, String>>();
				boolean found = false;
				Set<Integer> fulfillments = new HashSet<Integer>();

				//	firstOccurrenceofInputAct = true;
				dataValues = new HashMap<String, String>();
				execAut.ini();
				PossibleNodes current = null;
				XAttributeMap traceAttr = trace.getAttributes();
				for(String attribute : traceAttr.keySet()){
					if(!attribute.equals("Activity code") &&!attribute.equals("creator")&&!attribute.contains(":")&& !attribute.equals("description")){
							dataValues.put(attribute, traceAttr.get(attribute).toString());
					}
				}
				String line = "";
				int index = 0;
				for(XEvent e : trace){

					String label = ((XAttributeLiteral) e.getAttributes().get("concept:name")).getValue();
					if(formula instanceof DataCondFormula){
						DataCondFormula dataFormula = (DataCondFormula)formula;
						DeclareTemplate template = dataFormula.getTemplate();
						String activation = null;
						switch (template) {
							case Response:
								activation = dataFormula.getParam1();
								break;
							case Precedence:
								activation = dataFormula.getParam2();
								break;
							case Responded_Existence:
								activation = dataFormula.getParam1();
								break;
						}
						fulfillments.add(index);
					}
					violated = true;
					current = execAut.currentState();
					XAttributeMap eventAttr = e.getAttributes();
					//	String label = ((XAttributeLiteral) e.getAttributes().get("concept:name")).getValue();
					if(current!=null && !(current.get(0)==null)){
						for (Transition out : current.output()) {
							if (out.parses(label)) {
								violated = false;
								break;
							}
						}
					}
					if(!violated){
						execAut.next(label);
					}
					for(String attribute : eventAttr.keySet()){
						if(!attribute.equals("Activity code")&&!attribute.contains(":")){
							dataValues.put(attribute, eventAttr.get(attribute).toString());
						}
					}

					found = true;
				}

				current = execAut.currentState();
				if(!violated){
					if(formula instanceof DataCondFormula){
						DataCondFormula dataFormula = (DataCondFormula)formula;
						DeclareTemplate template = dataFormula.getTemplate();
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

					if(found && !violated && !line.equals("")){
						if(current.isAccepting()){
							verified = verified && true;
						}else{
							verified = verified && false;
						}
					}
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		return verified;
	}
	*/
	

	
}
