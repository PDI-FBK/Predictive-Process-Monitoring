package org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.processmining.plugins.predictive_monitor.bpm.configuration.ClientConfigurationClass;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.SimpleDeclareFormula;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.declare_activations.DeclareTimeUtilManager;
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

/**
 * Class used to create a classifier based on gap between activation and satisfaction of a given SimpleDeclareFormula formula
 * @author Williams
 * 
 * TODO UPDATE STRUCTURE
 */
public class ActivationVerificationGapExpectation {
	private static SimpleDeclareFormula formula;
	HashMap <String, String> classification;
	
	XLog log = null;
	
	public ActivationVerificationGapExpectation(XLog log){
		formula = ClientConfigurationClass.activationFormulas;
		this.log = log;
		classification = new HashMap<String, String>();
		createMean(log);
	}
	
	private void createMean(XLog log){
		try {
			HashMap<String, Vector<Long>> map = getTimeDistances(log);
			Long mean;
			for(String traceID : map.keySet()){
				mean = new Long(0);
				for(Long tmp : map.get(traceID)){
					mean+=tmp;
				}
				if (map.get(traceID).size()>0){
					mean/=map.get(traceID).size();
					String label = mean.toString();
					classification.put(traceID, label);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, Vector<Long>> getTimeDistances(XLog log){
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
				

	public Map<String, String> getExpectations() {
		return classification;
	}
}
