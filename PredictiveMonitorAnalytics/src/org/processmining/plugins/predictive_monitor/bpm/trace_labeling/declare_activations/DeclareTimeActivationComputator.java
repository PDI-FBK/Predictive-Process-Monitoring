/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2006 TU/e Eindhoven * and is licensed under the * Common
 * Public License, Version 1.0 * by Eindhoven University of Technology *
 * Department of Information Systems * http://is.tm.tue.nl * *
 **********************************************************/

package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.declareanalyzer.executions.ExecutionsTree;
import org.processmining.plugins.declareminer.DeclareMinerInput;
import org.processmining.plugins.declareminer.enumtypes.AprioriKnowledgeBasedCriteria;
import org.processmining.plugins.declareminer.enumtypes.DeclareTemplate;
import org.processmining.plugins.declareminer.visualizing.ConstraintDefinition;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.SimpleDeclareFormula;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates.Absence2Info;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates.AbsenceInfo;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates.ChoiceInfo;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates.CoexistenceInfo;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates.Exactly1Info;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates.ExclusiveChoiceInfo;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates.ExistenceInfo;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates.InitInfo;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates.NegativeRelationInfo;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates.NotCoexistenceInfo;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates.PrecedenceInfo;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates.ResponseInfo;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates.SuccessionInfo;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.templates.TemplateInfo;
import org.processmining.plugins.predictive_monitor.bpm.utility.XLogReader;


public class DeclareTimeActivationComputator {

	public static void main(String[] args) {
		String inputLogFileName = "./input/BPI2011_80.xes";
		try {
			XLog log = XLogReader.openLog(inputLogFileName);
			//SimpleDeclareFormula formula = new SimpleDeclareFormula("CEA - tumor marker using meia", "squamous cell carcinoma using eia", DeclareTemplate.Response);
			SimpleDeclareFormula formula = new SimpleDeclareFormula("squamous cell carcinoma using eia", "histological examination - biopsies nno", DeclareTemplate.Precedence);
			HashMap<String, Vector<Long>> map = getTimeDistances(log, formula);
			printMap(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	public static HashMap<String, Vector<Long>> getTimeDistances(XLog log, SimpleDeclareFormula formula){
		
		String[] parameters = formula.getParams();
		
		ConstraintDefinition constraintDefinition = DeclareTimeUtilManager.getConstraintDefinition(parameters, formula.getTemplate());
	
		HashMap<String, Vector<Long>> logTimeDistances = new HashMap<String, Vector<Long>>();
		for (XTrace trace : log) {
			List<Integer> traceIndexes = new LinkedList<Integer>();
			List<String> traceEvents = new LinkedList<String>();
			int i = 0;
			for (XEvent event : trace) {
				XAttributeMap eventAttributeMap = event.getAttributes();
				traceEvents.add((eventAttributeMap.get(XConceptExtension.KEY_NAME)+"-"+eventAttributeMap.get(XLifecycleExtension.KEY_TRANSITION)).toLowerCase());
				traceIndexes.add(i);
				i++;
			}
			ExecutionsTree executiontree = new ExecutionsTree(traceEvents, traceIndexes, constraintDefinition);	
			Set<Integer> activations =  executiontree.getActivations();
			Set<Integer> violations = executiontree.getViolations();
			Set<Integer> fulfillments = executiontree.getFulfillments();
			Set<Integer> conflicts = executiontree.getConflicts();
	
	
			TemplateInfo templateInfo = null;
	
			DeclareTemplate template = formula.getTemplate(); 
			
				switch(template){
					case Succession:
					case Alternate_Succession:
					case Chain_Succession:
						templateInfo = new SuccessionInfo();
						break;
					case Choice:
						templateInfo = new ChoiceInfo();
						break;
					case Exclusive_Choice:
						templateInfo = new ExclusiveChoiceInfo();
						break;
					case Existence:
					case Existence2:
					case Existence3:
						templateInfo = new ExistenceInfo();
						break;
					case Init:
						templateInfo = new InitInfo();
						break;
					case Absence:
						templateInfo = new AbsenceInfo();
						break;
					case Absence2:
					case Absence3:
						templateInfo = new Absence2Info();
						break;
					case Exactly1:
					case Exactly2:
						templateInfo = new Exactly1Info();
						break;
					case Precedence:
					case Alternate_Precedence:
					case Chain_Precedence:
						templateInfo = new PrecedenceInfo();
						break;
					case Responded_Existence:
					case Response:
					case Alternate_Response:
					case Chain_Response:
						templateInfo = new ResponseInfo();
						break;
					case CoExistence:
						templateInfo = new CoexistenceInfo();
						break;
					case Not_CoExistence:
						templateInfo = new NotCoexistenceInfo();
						break;
					case Not_Succession:
					case Not_Chain_Succession:
						templateInfo = new NegativeRelationInfo();
						break;	
				}
				DeclareMinerInput input = DeclareTimeUtilManager.getDeclareMinerInput();
				Set<AprioriKnowledgeBasedCriteria> criteria = new HashSet<AprioriKnowledgeBasedCriteria>();
				criteria.add(AprioriKnowledgeBasedCriteria.AllActivitiesWithEventTypes);
				input.setAprioriKnowledgeBasedCriteriaSet(criteria);
				input.setReferenceEventType("complete");

				Vector<Long> timeDistances = templateInfo.getTimeDistances(input, trace, constraintDefinition, activations);
				logTimeDistances.put(XConceptExtension.instance().extractName(trace), timeDistances);
			}
		return logTimeDistances;
		
		}
	
	public static void printMap(HashMap<String, Vector<Long>> map) {
//		if(ServerConfigurationClass.printDebug){
//			for (String key : map.keySet()) {
//				System.out.println(key+" "+map.get(key));
//			}
//		}
	}
	

	}

	


