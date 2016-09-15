package org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.FormulaVerificator;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;


public class SatisfactionTimeTraceClassifier extends TraceClassifier{ 	
	
	private final transient DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

	public SatisfactionTimeTraceClassifier(XLog log,Vector<Formula> formulas)
	{	
		super(log,formulas);
	}
	
	public Long classifyTrace(XTrace trace)
	{
		Long time = null;

		Date initialDate;
		try {
			initialDate = (Date) format.parseObject(trace.get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
			Long initialTime = initialDate.getTime();
			Long satisfactionOrViolationTime = FormulaVerificator.eventViolationTime(listener,trace,formulas);
			Long endTime = ((Date)format.parseObject(trace.get(trace.size()-1).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString())).getTime();
			//System.out.println("Trace:"+XConceptExtension.instance().extractName(trace)+"Formula time:"+(satisfactionOrViolationTime-initialTime)+"  Last Event:"+(endTime-initialTime));
			if(satisfactionOrViolationTime!=null)
			{
				time=satisfactionOrViolationTime-initialTime;
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time;
	}
}

