package org.processmining.plugins.predictive_monitor.bpm.configuration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Date;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

public class TraceEvaluation {
	private int currPrefIndex;
	private int evaluationGap;
	private XTrace completeTrace;
	private XTrace partialTrace;
	private boolean finished;
	private long startTime;
	private final static DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	
	public TraceEvaluation(XTrace trace,int currPrefStart,int evaluationGap)
	{
		currPrefIndex=currPrefStart;
		finished=false;
		this.evaluationGap=evaluationGap;
		this.completeTrace=trace;
		this.partialTrace=XFactoryRegistry.instance().currentDefault().createTrace();
		if(currPrefStart>trace.size()-1)
		{
			currPrefStart=trace.size();
			currPrefIndex=trace.size()-1;
		}
		XConceptExtension.instance().assignName(partialTrace,XConceptExtension.instance().extractName(trace));
		
		if(trace.size()>0)
		{
			try {
				startTime = ((Date)format.parseObject(completeTrace.get(currPrefIndex).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString())).getTime();
			} catch (ParseException e) {
				startTime = 0;
			}
		}
		
		for(int i=0;i<=currPrefStart;i++)
		{
			if(i<trace.size())
			{
				partialTrace.add(trace.get(i));
			}
			else
			{
				currPrefIndex = i-1;
				break;
			}
		}
	}
	
	public boolean nextEvaluationPoint()
	{
		if(finished || currPrefIndex==completeTrace.size()-1)
		{
			return false;
		}
		for(int i=0;i<evaluationGap;i++)
		{
			if(i+currPrefIndex>completeTrace.size()-1)
			{
				currPrefIndex=completeTrace.size()-1;
				finished=true;
				return true;
			}
			partialTrace.add(completeTrace.get(i+currPrefIndex));
		}
		currPrefIndex+=evaluationGap;
		if(currPrefIndex>=completeTrace.size()-1)
		{
			currPrefIndex=completeTrace.size()-1;
			finished=true;
		}
		return true;
	}

	public XTrace getPartialTrace()
	{
		return partialTrace;
	}
	
	public Vector<String> getCurrentVariables()
	{
		List<String> currentEvents = new Vector<String>();
		int i = 0;
		for(XEvent event : completeTrace){
			currentEvents.add(event.getAttributes().get(XConceptExtension.KEY_NAME).toString());
			i++;
			if(i>currPrefIndex){
				break;
			}
		}
		Vector<String> currentVariables = new Vector<String>();
		for(String attribute : completeTrace.get(currPrefIndex).getAttributes().keySet()){
			currentVariables.add(attribute);
		}		
		return currentVariables;
	}
	
	public HashMap<String,String> getVariables()
	{
		
		HashMap<String, String> variables = new HashMap<String, String>();
		XAttributeMap traceAttr = completeTrace.getAttributes();
		for(String attribute : traceAttr.keySet()){
			variables.put(attribute, traceAttr.get(attribute).toString());
		}
		int i= 0;
		for(XEvent e : completeTrace){
			XAttributeMap eventAttr = e.getAttributes();
			for(String attribute : eventAttr.keySet()){
				variables.put(attribute, eventAttr.get(attribute).toString());
			}
			i++;
			if(i>currPrefIndex){
				break;
			}
		}
		return variables;
	}
	public String getName()
	{
		return XConceptExtension.instance().extractName(completeTrace);
	}
	public int getCurrentIndex()
	{
		return currPrefIndex;
	}

	public long getCurrentTime() {
		try {
			long currentTime = ((Date)format.parseObject(completeTrace.get(currPrefIndex).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString())).getTime();
			return currentTime-startTime;
		} catch (ParseException e) {
			return 0;
		}
		
	}
}
