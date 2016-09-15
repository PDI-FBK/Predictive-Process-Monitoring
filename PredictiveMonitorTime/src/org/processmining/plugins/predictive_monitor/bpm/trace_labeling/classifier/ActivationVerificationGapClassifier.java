package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
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
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.TimeClassifier.PartitionMethod;
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
 */
public class ActivationVerificationGapClassifier implements Classifier {
	private static SimpleDeclareFormula formula;
	private int numberOfIntervals;
	private List<Long> lastValue;
	private List<Integer> count;
	HashMap <String, String> classification;
	
	public ActivationVerificationGapClassifier(XLog log){
//		formula = ClientConfigurationClass.activationFormulas;
//		numberOfIntervals=ServerConfigurationClass.numberOfIntervals;
//		lastValue = new ArrayList<Long>();
//		count = new ArrayList<Integer>();
//		classification = new HashMap<String, String>();
//		
//		List<Long> times=new ArrayList<>();
//		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
//		Date first=null;
//		Date last=null;
//		for(XTrace trace:log)
//		{
//			try {
//				first=(Date)format.parseObject(trace.get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
//				PartitionMethod partitionMethod = ServerConfigurationClass.partitionMethod;
//				if( partitionMethod == PartitionMethod.SORTED_DIVISION || partitionMethod == PartitionMethod.FIXED_SORTED_DIVISION)
//				{
//					for(XEvent event:trace)
//					{
//						last=(Date)format.parseObject(event.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
//						times.add(last.getTime()-first.getTime());
//					}
//				}
//				else
//				{
//					last=(Date)format.parseObject(trace.get(trace.size()-1).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
//					times.add(last.getTime()-first.getTime());
//				}
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		computeIntervals(times);
//		for(Long interval:lastValue)
//		{
//			count.add(new Integer(0));
//		}
//		createMean(log);
	}
	
	private void createMean(XLog log){
//		try {
//			HashMap<String, Vector<Long>> map = getTimeDistances(log);
//			Long mean;
//			for(String traceID : map.keySet()){
//				mean = new Long(0);
//				for(Long tmp : map.get(traceID)){
//					mean+=tmp;
//				}
//				if (map.get(traceID).size()>0){
//					mean/=map.get(traceID).size();
//					String label = classify(mean);
//					classification.put(traceID, label);
//				}
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	public String classify(Long time)
//	{
//		for(int i=0;i<numberOfIntervals;i++)
//		{
//			if(time<=lastValue.get(i))
//			{
//				count.set(i, count.get(i)+1);
//				return indexToLabel(i);
//			}
//		}
//		return "";
//	}
//	
//	@Override
//	public List<String> getLabels()
//	{
//		List<String> labels = new ArrayList<String>();
//		for(int i=0;i<numberOfIntervals;i++)
//		{
//			labels.add(indexToLabel(i));
//		}
//		return labels;
//	}
//
//	
//	private String indexToLabel(int index)
//	{
//		if(index==0)
//		{
//			return "0-"+lastValue.get(0);
//		}
//		else
//		{
//			return lastValue.get(index-1)+"-"+lastValue.get(index);
//		}
	}
	
	public static HashMap<String, Vector<Long>> getTimeDistances(XLog log){
//		String[] parameters = formula.getParams();
//		ConstraintDefinition constraintDefinition = DeclareTimeUtilManager.getConstraintDefinition(parameters, formula.getTemplate());
//		HashMap<String, Vector<Long>> logTimeDistances = new HashMap<String, Vector<Long>>();
//		for (XTrace trace : log) {
//			List<Integer> traceIndexes = new LinkedList<Integer>();
//			List<String> traceEvents = new LinkedList<String>();
//			int i = 0;
//			for (XEvent event : trace) {
//				XAttributeMap eventAttributeMap = event.getAttributes();
//				traceEvents.add((eventAttributeMap.get(XConceptExtension.KEY_NAME)+"-"+eventAttributeMap.get(XLifecycleExtension.KEY_TRANSITION)).toLowerCase());
//				traceIndexes.add(i);
//				i++;
//			}
//			ExecutionsTree executiontree = new ExecutionsTree(traceEvents, traceIndexes, constraintDefinition);	
//			Set<Integer> activations =  executiontree.getActivations();
//			TemplateInfo templateInfo = null;
//			DeclareTemplate template = formula.getTemplate(); 
//			switch(template){
//			case Succession:
//			case Alternate_Succession:
//			case Chain_Succession:
//				templateInfo = new SuccessionInfo();
//				
//				
//				break;
//			case Choice:
//				templateInfo = new ChoiceInfo();
//				break;
//			case Exclusive_Choice:
//				templateInfo = new ExclusiveChoiceInfo();
//				break;
//			case Existence:
//			case Existence2:
//			case Existence3:
//				templateInfo = new ExistenceInfo();
//				break;
//			case Init:
//				templateInfo = new InitInfo();
//				break;
//			case Absence:
//				templateInfo = new AbsenceInfo();
//				break;
//			case Absence2:
//			case Absence3:
//				templateInfo = new Absence2Info();
//				break;
//			case Exactly1:
//			case Exactly2:
//				templateInfo = new Exactly1Info();
//				break;
//			case Precedence:
//			case Alternate_Precedence:
//			case Chain_Precedence:
//				templateInfo = new PrecedenceInfo();
//				break;
//			case Responded_Existence:
//			case Response:
//			case Alternate_Response:				
//			case Chain_Response:
//				templateInfo = new ResponseInfo();
//				break;
//			case CoExistence:
//				templateInfo = new CoexistenceInfo();
//				break;
//			case Not_CoExistence:
//				templateInfo = new NotCoexistenceInfo();
//				break;
//			case Not_Succession:
//			case Not_Chain_Succession:
//				templateInfo = new NegativeRelationInfo();
//				break;	
//			}
//			DeclareMinerInput input = DeclareTimeUtilManager.getDeclareMinerInput();
//			Set<AprioriKnowledgeBasedCriteria> criteria = new HashSet<AprioriKnowledgeBasedCriteria>();
//			criteria.add(AprioriKnowledgeBasedCriteria.AllActivitiesWithEventTypes);
//			input.setAprioriKnowledgeBasedCriteriaSet(criteria);
//			input.setReferenceEventType("complete");
//			Vector<Long> timeDistances = templateInfo.getTimeDistances(input, trace, constraintDefinition, activations);
//			logTimeDistances.put(XConceptExtension.instance().extractName(trace), timeDistances);
//		}
//		return logTimeDistances;
		return null; //REMOVE
	}
	

	private void computeIntervals(List<Long> times)
	{
//		final long precision=1000l; //event that are closer than (precision milliseconds) are considered the same
//		Collections.sort(times);
//		Long value;
//		int index;
//		for(int i=1;i<=numberOfIntervals;i++)
//		{
//			index=(int)((float)(times.size()-1)/numberOfIntervals*i);
//			value=times.get(index);
//			lastValue.add(value);
//			for(int j=index+1;j<times.size();)
//			{
//				if(Math.abs(times.get(j).longValue()-value.longValue())<precision)
//				{
//					times.remove(times.get(j));
//				}
//				else break;
//			}
//		}
	}
	
	@Override
	public HashMap<String,String> getClassification() {
		return classification;
	}
	
	public String getInterval(int interval)
	{
		String s="";
		if(interval==0)
		{
			if(lastValue.get(0)==0)
			{
				s+="now ";
			}
			else
			{
				s+="now - "+longToTime(lastValue.get(0));
			}
		}
		else
		{
			s+=longToTime(lastValue.get(interval-1))+"- "+longToTime(lastValue.get(interval));
		}
		return s;
	}
	
	private String longToTime(Long number)
	{
		String s="";
		boolean written=false;
		int count=0;

		long years=(long)number/((long)60*60*24*12*30*1000);
		if(years>0)
		{
			s+=years+" years ";
			written=true;
		}
		if(written)
		{
			count++;
		}
		number-=years*((long)60*60*24*12*30*1000);
		
		long months=number/((long)60*60*24*30*1000);
		if(months>0)
		{
			s+=months+" months ";
			written=true;
		}	
		if(written)
		{
			count++;
		}
		number-=months*((long)60*60*24*30*1000);
		
		long days=number/((long)60*60*24*1000);
		if(days>0)
		{
			s+=days+" days ";
			written=true;
		}
		if(written)
		{
			count++;
		}
		if(count==2)
		{
			return s;
		}
		number-=days*((long)60*60*24*1000);
		
		long hours=number/((long)60*60*1000);
		if(hours>0)
		{
			s+=hours+" hours ";
			written=true;
		}	
		if(written)
		{
			count++;
		}
		if(count==2)
		{
			return s;
		}
		number-=hours*((long)60*60*1000);
		
		long minutes=number/((long)60*1000);
		if(minutes>0) 
		{
			s+=minutes+" minutes ";
			written=true;
		}
		if(written)
		{
			count++;
		}
		if(count==2)
		{
			return s;
		}
		number-=minutes*((long)60*60*1000);
		
		long seconds=number/((long)60*1000);
		if(seconds>0) // maximum precision allowed
		{
			s+=seconds+" seconds ";
			written=true;
		}
		number-=seconds*((long)60*1000);
		
		if(!written)
		{
			if(number==0)
			{
				s+="now ";
			}
			else
			{
				s+=number+" milliseconds ";
			}
		}
		return s;
	}

	@Override
	public List<String> getLabels() {
		// TODO Auto-generated method stub
		return null;
	}
}
