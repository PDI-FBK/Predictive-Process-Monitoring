package org.processmining.plugins.predictive_monitor.bpm.prediction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.declareminer.Watch;
import org.processmining.plugins.predictive_monitor.bpm.configuration.ClientConfigurationClass;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.Connection;
import org.processmining.plugins.predictive_monitor.bpm.prediction.PredictionGenerator.CheckType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier.PredictionType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;
import org.processmining.plugins.predictive_monitor.bpm.utility.OutputFilePrinter;
import org.processmining.plugins.predictive_monitor.bpm.utility.Print;

import weka_predictions.data_predictions.Result;

public class PredictionGeneratorNew {
	protected HashMap<String,String> predictions;
	protected int totalLogEventNumber;
	protected int numberEv;
	protected double totalLogEventTime;
	protected double secs;
	protected long initTime;
	protected long predictionTime;
	protected double earliness;
	protected double numberOfTraces;
	protected String traceName;
	protected Result prediction;
	protected int index;
	protected OutputFilePrinter ofp;
	protected Map<String,String> actualValue;
	protected int numberOfCorrect;
	protected int predictedNumber;
	protected int totalTraceNumber;
	
	private Print print = new Print();

	public void generatePredictions(XLog log,Map<String, Object> configuration, OutputFilePrinter ofp)
	{
		numberOfCorrect=0;
		predictedNumber=0;
		totalLogEventNumber=0;
		numberEv=0;
		totalLogEventTime=0;
		secs=0;
		initTime=0;
		earliness=0;
		numberOfTraces=0;
		predictedNumber=0;
		totalTraceNumber=0;
		Map<String,Object> clientConfiguration = (Map<String,Object>)configuration.get("clientConfiguration");
		Map<String,Object> serverConfiguration = (Map<String,Object>)configuration.get("serverConfiguration");
		Vector<Formula> formulasVect = (Vector<Formula>)serverConfiguration.get("formulas");
		double minConfidence=(double)clientConfiguration.get("minConfidence");
		int minSupport=(int)clientConfiguration.get("minSupport");
		this.ofp=ofp;
		printFormulas(ofp,formulasVect);
		List<String> labels=new ArrayList<String>();
		labels.add("TraceId");
		labels.add("currPrefIndex");
		labels.add("predictedValue");
		labels.add("Confidence");
		labels.add("Support");
		labels.add("PredictionTime");
		labels.add("InitTime");
		ofp.printTableLine(labels);
		
		actualValue=new HashMap<String,String>();
		
		predictions=new HashMap<String, String>();
		predictTraces(log,(String)configuration.get("runId"),minConfidence,minSupport);
		earliness/=predictions.size();
	}
	
	public void generatePredicionsTest(XLog log,String runId, Map<String,String> actualValue,OutputFilePrinter ofp)
	{
		numberOfCorrect=0;
		predictedNumber=0;
		totalLogEventNumber=0;
		numberEv=0;
		totalLogEventTime=0;
		secs=0;
		initTime=0;
		earliness=0;
		numberOfTraces=0;
		predictedNumber=0;
		totalTraceNumber=0;
		this.ofp=ofp;
		List<String> labels=new ArrayList<String>();
		labels.add("TraceId");
		labels.add("currPrefIndex");
		labels.add("predictedValue");
		labels.add("actualValue");
		labels.add("Prediction");
		labels.add("Confidence");
		labels.add("Support");
		labels.add("PredictionTime");
		labels.add("InitTime");
		ofp.printTableLine(labels);
		
		this.actualValue=actualValue;
		
		predictions=new HashMap<String, String>();
		predictTraces(log,runId,0,0); //TODO SOLVE PROBLEM
		earliness/=predictions.size();
	}
	
	protected void predictTraces(XLog log, String runId ,double minConfidence, int minSupport)
	{
		long totalPredictionTime=0;

		for(XTrace trace : log)
		{
			String traceName=XConceptExtension.instance().extractName(trace);
			if (actualValue.get(traceName)!= null){
				numberOfTraces++;
				int currPrefIndex;
				boolean found=false;
				double maxConfidence=0;
				double maxSupport=0;
				totalPredictionTime=0;
				long initTime=0;
				String predictionLabel="";
				String actualLabel="";
				int currPrefStart=0;
				
				for (currPrefIndex = currPrefStart; currPrefIndex < trace.size() && !found; currPrefIndex+=ClientConfigurationClass.evaluationPointGap) {
					Watch watch_ev = new Watch();
					watch_ev.start();
					long time_ev = 0;
					totalLogEventNumber ++;
					
					
					Map<String, Result> result = null;// Connection.predictTrace(trace, runId);//OperationalSupportRandomForest.provideOperationalSupport(trace, currPrefIndex, traceName, formulasVect, currentVariables, variables, configuration);
					
					totalLogEventTime = totalLogEventTime+time_ev;
					Result prediction = null;
					boolean key = result.keySet().iterator().hasNext();
					if(key ==true){
						prediction = result.get(result.keySet().iterator().next());
						secs = secs+ prediction.getPredictionTime();
						totalPredictionTime+=prediction.getPredictionTime();
						initTime=prediction.getInitializationTime();
						
						if(maxConfidence<prediction.getConfidence())
						{
							maxConfidence=prediction.getConfidence();
						}
						if(maxSupport<prediction.getSupport())
						{
							maxSupport=prediction.getSupport();
						}
						
						if (prediction.getConfidence()>minConfidence && prediction.getSupport()>minSupport) {
							earliness = earliness + (((double)currPrefIndex)/((double)trace.size()));
							found = true;
							
							predictionLabel=computeLabel(prediction.getLabel(),trace.get(currPrefIndex),trace.get(0));
	
							predictions.put(traceName, predictionLabel);
								
							if(actualValue==null)
							{
								print(ofp, traceName, currPrefIndex, predictionLabel, prediction.getConfidence(),prediction.getSupport(), totalPredictionTime, initTime);
							}
							else
							{
								actualLabel=computeLabel(actualValue.get(traceName),trace.get(currPrefIndex),trace.get(0));
								String correct="Wrong";
								predictedNumber++;
								if(check(predictionLabel,actualLabel))
								{
									correct="Correct";
									numberOfCorrect++;
								}
								print(ofp, traceName, currPrefIndex, predictionLabel, prediction.getConfidence(),prediction.getSupport(), totalPredictionTime, initTime,actualLabel,correct);
							}
						}
					}
				}
				if(!found)
				{
					if(actualValue==null)
					{
						print(ofp, traceName, currPrefIndex, "Not Predicted", maxConfidence, maxSupport, totalPredictionTime, initTime);
					}
					else
					{
						actualLabel=actualValue.get(traceName);
						print(ofp, traceName, currPrefIndex, "Not Predicted", maxConfidence, maxSupport, totalPredictionTime, initTime,actualLabel,"-");
					}
				}
			}
		}
	}
	
	public HashMap<String,String> getPredictions()
	{
		return predictions;
	}
	
	public double getEarliness()
	{
		return earliness;
	}
	
	public double getTotalTime()
	{
		return secs;
	}
	
	public double getFailureRate()
	{
		return 1.0-(double)predictions.size()/numberOfTraces;
	}
	
	public long getInitTime()
	{
		return initTime;
	}
	
	public double getAveragePredictionTime()
	{
		return secs/numberEv;
	}
	
	protected void printFormulas(OutputFilePrinter ofp,Vector<Formula> formulasVect) {
		int i=0;
		for(Formula formula:formulasVect)
		{
			ofp.printParameterValue("Formula","\'"+formula.getLTLFormula()+"\' ");
			i++;
		}
		ofp.flush();
	}
	
	private String computeLabel(String label,XEvent current,XEvent start)
	{
//		if(ServerConfigurationClass.predictionType==PredictionType.FORMULA_SATISFACTION_TIME)
//		{
//			if(ClientConfigurationClass.shiftToCurrentEvent)
//			{
//				return LabelUpdate.computeShiftedLabel(label, current.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString(),start.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString()); 
//			}
//		}
		return label;
	}
	
	private void print(OutputFilePrinter ofp, String trace, int currPrefIndex,String prediction, double confidence, double support,long predictionTime, long initTime)
	{
		DecimalFormat numberFormat = new DecimalFormat("0.00");
		List<String> row = new ArrayList<String>();
		row.add(trace);
		row.add(""+currPrefIndex);
		row.add(prediction);
		row.add(""+numberFormat.format(confidence));
		row.add(""+support);
		row.add(""+predictionTime);
		row.add(""+initTime);
		ofp.printTableLine(row);
		ofp.flush();
	}
	
	private void print(OutputFilePrinter ofp, String trace, int currPrefIndex,String prediction, double confidence, double support,long predictionTime, long initTime, String actualValue, String correct)
	{
		DecimalFormat numberFormat = new DecimalFormat("0.00");
		List<String> row = new ArrayList<String>();
		row.add(trace);
		row.add(""+currPrefIndex);
		row.add(prediction);
		row.add(actualValue);
		row.add(correct);
		row.add(""+numberFormat.format(confidence));
		row.add(""+support);
		row.add(""+predictionTime);
		row.add(""+initTime);
		ofp.printTableLine(row);
		ofp.flush();
	}	
	
	private boolean check(String prediction,String result)
	{
		if(ClientConfigurationClass.checkType==CheckType.EQUAL)
		{
			return prediction.equals(result);
		}
		else if(ClientConfigurationClass.checkType==CheckType.BELONG)
		{
			long start=Long.parseLong(prediction.split("-")[0]);
			long end=Long.parseLong(prediction.split("-")[1]);
			print.thatln(result);
			long time;
			time=Long.parseLong(result);
			return time>=start && time<=end;
		}
		return false;
	}
	
	public int getCorrect()
	{
		return numberOfCorrect;
	}
	public double getAccuracy()
	{
		return (double)numberOfCorrect/predictedNumber;
	}
	public int getWrong()
	{
		return predictedNumber-numberOfCorrect;
	}
	public int getNotPredicted()
	{
		return (int)numberOfTraces-predictedNumber;
	}
}
