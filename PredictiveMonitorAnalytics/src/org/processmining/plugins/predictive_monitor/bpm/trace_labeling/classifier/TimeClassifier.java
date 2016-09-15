package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification.SatisfactionTimeTraceClassifier;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.PredictionResultConverter;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.FormulaVerificator;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;
import org.processmining.plugins.predictive_monitor.bpm.utility.Conversion;
import org.processmining.plugins.predictive_monitor.bpm.utility.DataSnapshotListener;
import org.processmining.plugins.predictive_monitor.bpm.utility.LogReaderAndReplayer;
import org.processmining.plugins.predictive_monitor.bpm.utility.Print;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

/**
 * Class used to create a classifier based on time
 * @author Marco
 *
 */
public class TimeClassifier implements Classifier{ //TODO rename with TimeIntervalClassifier
	private static Print print = new Print();
	private int numberOfIntervals;
	private List<Long> lastValue;
	private HashMap<String,String> classification;
	/**
	 * the partition method could be:
	 * MAX_MINUS_MIN_OVER_N: computed approximating data with a line
	 * NORMAL_TIME_DISTRIBUTION: trace total duration is approximate with a normal distribution and average satisfaction time is approximate to half of the total time of each trace
	 * SORTED_DIVISION: each possible event time is inserted into an array list and sorted, then the array is divided into parts with the same amount of elements
	 * @author marco
	 *
	 */
	
	public enum PartitionMethod {MAX_MINUS_MIN_OVER_N,NORMAL_TIME_DISTRIBUTION,SORTED_DIVISION,FIXED_SORTED_DIVISION};
	
	private PartitionMethod partitionMethod;
	private final double notClassifiedRate = 0.3;
	//private  DateFormat format;

	public TimeClassifier(XLog log,Vector<Formula> formulas, PartitionMethod partitionMethod, int numberOfIntervals)
	{	
		this.partitionMethod=partitionMethod;
		this.numberOfIntervals=numberOfIntervals;
		SatisfactionTimeTraceClassifier traceClassifier = new SatisfactionTimeTraceClassifier(log, formulas);
		lastValue=new ArrayList<>();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		classification=new HashMap<String, String>();
		
		computeIntervals(log, format);
		
		classifyTraces(log,traceClassifier);
	}
	
	private void computeIntervals(XLog log, DateFormat format)
	{
		Date last=null;
		Date first=null;
		List<Long> times=new ArrayList<>();
		
		for(XTrace trace:log)
		{
			try {
				first=(Date)format.parseObject(trace.get(0).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
				
				if(partitionMethod==PartitionMethod.SORTED_DIVISION || partitionMethod==PartitionMethod.FIXED_SORTED_DIVISION)
				{
					for(XEvent event:trace)
					{
						last=(Date)format.parseObject(event.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
						times.add(last.getTime()-first.getTime());
					}
				}
				else
				{
					last=(Date)format.parseObject(trace.get(trace.size()-1).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
					times.add(last.getTime()-first.getTime());
				}
			
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(partitionMethod==PartitionMethod.MAX_MINUS_MIN_OVER_N)
		{
			computeIntervals0(times);
		}
		else if(partitionMethod==PartitionMethod.NORMAL_TIME_DISTRIBUTION)
		{
			computeIntervals1(times);
		}
		else if(partitionMethod==PartitionMethod.SORTED_DIVISION)
		{
			computeIntervals2(times);
		}
		else if(partitionMethod==PartitionMethod.FIXED_SORTED_DIVISION)
		{
			computeIntervals3(times);
		}
	}

	private void classifyTraces(XLog log,SatisfactionTimeTraceClassifier traceClassifier)
	{
		for(XTrace trace:log)
		{
			String label = classify(traceClassifier.classifyTrace(trace));
			classification.put(XConceptExtension.instance().extractName(trace), label);
		}
	}
	
	private void computeIntervals0(List<Long> times)
	{
		Long min=null;
		Long max=null;
		if(times.size()!=0)
		{
			min=times.get(0);
			max=times.get(0);
		}
		for(Long time:times)
		{
			if(min>time)
			{
				min=time;
			}
			if(max<time)
			{
				max=time;
			}
		}
		
		for(int i=1;i<=numberOfIntervals;i++)
		{
			lastValue.add((max-min)/numberOfIntervals*i);
		}
	}
	
	private void computeIntervals1(List<Long> times)
	{
		double array[]=new double[times.size()];
		Mean mean=new Mean();
		Variance variance=new Variance();
		int i=0;
		Long max=new Long(times.get(0));
		for(Long time:times)
		{
			array[i]=time;
			i++;
			if(time>max)max=new Long(time);
		}
		
		double varianceValue=variance.evaluate(array)/4.0;
		double meanValue=mean.evaluate(array)/2.0;
		NormalDistribution normalDistribution = new NormalDistribution(0,1);
		long value=0;
		long oldValue=-1;
		long offset=0;
		for(i=1;i<numberOfIntervals;i++)
		{
			value=(long)((normalDistribution.inverseCumulativeProbability(1.00/numberOfIntervals*i)*Math.sqrt(varianceValue)+meanValue));
			if(value<oldValue)
			{
				offset=-value+oldValue+2;
			}
			lastValue.add(new Long(value+offset));
			oldValue=value+offset;
		}
		lastValue.add(max);
	}
	
	private void computeIntervals2(List<Long> times)
	{
		final long precision=1000l; //event that are closer than (precision milliseconds) are considered the same
		Collections.sort(times);
		Long value;
		int index;
		for(int i=1;i<=numberOfIntervals;i++)
		{
			index=(int)((float)(times.size()-1)/numberOfIntervals*i);
			value=times.get(index);
			lastValue.add(value);
			for(int j=index+1;j<times.size();)
			{
				if(Math.abs(times.get(j).longValue()-value.longValue())<precision)
				{
					times.remove(times.get(j));
				}
				else break;
			}
		}
	}
	
	private void computeIntervals3(List<Long> times)
	{
		final long precision=1000l; //event that are closer than (precision milliseconds) are considered the same
		Collections.sort(times);
		Long value;
		int index;
		double part=(times.size()/(((numberOfIntervals-1)/(1-notClassifiedRate)+1))/(1-notClassifiedRate));
		for(int i=1;i<numberOfIntervals;i++)
		{
			index=(int)(part*i);
			value=times.get(index);
			lastValue.add(value);
			for(int j=index+1;j<times.size();)
			{
				if(Math.abs(times.get(j).longValue()-value.longValue())<precision)
				{
					times.remove(times.get(j));
				}
				else break;
			}
		}
		lastValue.add(times.get(times.size()-1));
	}
	
	/**
	 * method that, once the classifier is initialized, given a time, returns the relative interval
	 * @param time time related to formula satisfaction
	 * @return index of interval which matches with given time
	 */

	public String classify(Long time)
	{
		for(int i=0;i<numberOfIntervals;i++)
		{
			if(time<=lastValue.get(i))
			{
				return PredictionResultConverter.toLongInterval((indexToLabel(i)));
			}
		}
		return null;
	}
	
	@Override
	public List<String> getLabels()
	{
		List<String> labels = new ArrayList<String>();
		for(int i=0;i<numberOfIntervals;i++)
		{
			labels.add(PredictionResultConverter.toLongInterval(indexToLabel(i)));
		}
		return labels;
	}
	
	@Override
	public HashMap<String, String> getClassification() {
		return classification;
	}
	
	private String indexToLabel(int index)
	{
		if(index==0)
		{
			return "0-"+lastValue.get(0);
		}
		else
		{
			return lastValue.get(index-1)+"-"+lastValue.get(index);
		}
	}
}
