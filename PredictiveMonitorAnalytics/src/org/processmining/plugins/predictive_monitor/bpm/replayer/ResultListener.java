package org.processmining.plugins.predictive_monitor.bpm.replayer;


import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.processmining.plugins.predictive_monitor.bpm.operational_support.TraceEvaluationResult;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.TraceResult;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.NoResult;





public class ResultListener{
	protected ObservableList<GenericResult> results;
	protected ObservableList<PredictionResult> predictionResults;
	protected ObservableList<EvaluationResult> evaluationResults;
	protected int totalTraceCount;
	protected double earlinessSum;
	protected SimpleIntegerProperty evaluatedTraceCount;
	protected SimpleDoubleProperty progress;
	protected SimpleDoubleProperty earliness;
	protected SimpleDoubleProperty failureRate;
	protected SimpleIntegerProperty notPredictedCount;
	protected SimpleLongProperty totalTime;
	protected SimpleDoubleProperty correctCount;
	protected SimpleDoubleProperty accuracy;
	protected SimpleLongProperty initTime;
	protected boolean jumpToCurrentEvent;

	
	public ResultListener(boolean jumpToCurrentEvent){
		this.jumpToCurrentEvent = jumpToCurrentEvent;
		totalTraceCount=1;
		evaluatedTraceCount = new SimpleIntegerProperty(0);
		notPredictedCount = new SimpleIntegerProperty(0);
		failureRate = new SimpleDoubleProperty(0);
		progress = new SimpleDoubleProperty(0);
		earliness = new SimpleDoubleProperty(0);
		earlinessSum = 0;
		totalTime = new SimpleLongProperty(0);
		correctCount = new SimpleDoubleProperty(0);
		accuracy = new SimpleDoubleProperty(0);
		results=FXCollections.observableArrayList();
		predictionResults=FXCollections.observableArrayList();
		evaluationResults=FXCollections.observableArrayList();
		initTime = new SimpleLongProperty(0);
	}
	
	public void setInitTime(long initTime)
	{
		this.initTime.set(initTime);
	}
	
	public synchronized void addResult(TraceResult traceResult)
	{
		if(traceResult instanceof TraceEvaluationResult)
		{
			if(jumpToCurrentEvent)
			{
				((TraceEvaluationResult)traceResult).jumpToCurrentEvent();
			}
			results.add(new EvaluationResult((TraceEvaluationResult)traceResult));
			evaluationResults.add(new EvaluationResult((TraceEvaluationResult)traceResult));
		}
		else
		{
			if(jumpToCurrentEvent)
			{
				traceResult.jumpToCurrentEvent();
			}
			results.add(new PredictionResult(traceResult));
			predictionResults.add(new PredictionResult(traceResult));
		}
		
		evaluatedTraceCount.setValue(evaluatedTraceCount.get()+1);
		
		totalTime.setValue(totalTime.get()+traceResult.getResult().getTime());
		
		if(traceResult.getResult().getResults().size()==0 || !traceResult.getResult().isValid())
		{
			notPredictedCount.setValue(notPredictedCount.get()+1);
			failureRate.setValue(((double)notPredictedCount.get()/evaluatedTraceCount.get()));
		}
		else
		{
			if(traceResult instanceof TraceEvaluationResult)
			{
				correctCount.setValue(correctCount.get()+((TraceEvaluationResult)traceResult).getCorrectness().getValue());
				accuracy.setValue((double)correctCount.get()/(evaluatedTraceCount.get()-notPredictedCount.get()));
			}
			earlinessSum += (double)traceResult.getResult().getIndex()/traceResult.getTraceRun().getTrace().size();
			earliness.setValue(earlinessSum/(evaluatedTraceCount.get()-notPredictedCount.get()));
		}
		
		progress.setValue((double)evaluatedTraceCount.get()/totalTraceCount);
		//System.out.println("Progress:"+getProgress().get()+" Accuracy:"+getAccuracy().get()+" notPredictedCount:"+notPredictedCount.get());
	}
	
	public void setTotalTraceNumber(int totalTraceNumber)
	{
		this.totalTraceCount=totalTraceNumber;
	}
	
	public SimpleDoubleProperty getProgress()
	{
		return progress;
	}
	
	public SimpleDoubleProperty getFailureRate()
	{
		return failureRate;
	}
	
	public SimpleDoubleProperty getEarliness()
	{
		return earliness;
	}
	
	public SimpleIntegerProperty getEvaluatedTraceCount()
	{
		return evaluatedTraceCount;
	}
	
	public int getTotalTraceCount()
	{
		return totalTraceCount;
	}
	
	public SimpleLongProperty getTotalTime()
	{
		return totalTime;
	}
	
	
	public ObservableList<GenericResult> getResults()
	{
		return results;
	}
	
	public ObservableList<EvaluationResult> getEvaluationResults()
	{		
		return evaluationResults;
	}
	
	public ObservableList<PredictionResult> getPredictionResults()
	{		
		return predictionResults;
	}
	
	public SimpleDoubleProperty getAccuracy()
	{
		return accuracy;
	}

	public double getEarlinessSum() {
		return earlinessSum;
	}

	public void setEarlinessSum(double earlinessSum) {
		this.earlinessSum = earlinessSum;
	}

	public SimpleIntegerProperty getNotPredictedCount() {
		return notPredictedCount;
	}

	public void setNotPredictedCount(SimpleIntegerProperty notPredictedCount) {
		this.notPredictedCount = notPredictedCount;
	}

	public SimpleDoubleProperty getCorrectCount() {
		return correctCount;
	}

	public SimpleDoubleProperty getWrongCount() {
		return new SimpleDoubleProperty((double)evaluatedTraceCount.get()-notPredictedCount.doubleValue()-correctCount.doubleValue());
	}

	public void setCorrectCount(SimpleDoubleProperty correctCount) {
		this.correctCount = correctCount;
	}

	public void setResults(ObservableList<GenericResult> results) {
		this.results = results;
	}

	public void setPredictionResults(ObservableList<PredictionResult> predictionResults) {
		this.predictionResults = predictionResults;
	}

	public void setEvaluationResults(ObservableList<EvaluationResult> evaluationResults) {
		this.evaluationResults = evaluationResults;
	}

	public void setTotalTraceCount(int totalTraceCount) {
		this.totalTraceCount = totalTraceCount;
	}

	public void setEvaluatedTraceCount(SimpleIntegerProperty evaluatedTraceCount) {
		this.evaluatedTraceCount = evaluatedTraceCount;
	}

	public void setProgress(SimpleDoubleProperty progress) {
		this.progress = progress;
	}

	public void setEarliness(SimpleDoubleProperty earliness) {
		this.earliness = earliness;
	}

	public void setFailureRate(SimpleDoubleProperty failureRate) {
		this.failureRate = failureRate;
	}

	public void setTotalTime(SimpleLongProperty totalTime) {
		this.totalTime = totalTime;
	}

	public void setAccuracy(SimpleDoubleProperty accuracy) {
		this.accuracy = accuracy;
	}

	public DoublePropertyBase getAverageTime() {
		return new SimpleDoubleProperty((totalTime.get())/((double)evaluatedTraceCount.get()));
	}
	
	public SimpleLongProperty getInitTime()
	{
		return initTime;
	}

	
}
