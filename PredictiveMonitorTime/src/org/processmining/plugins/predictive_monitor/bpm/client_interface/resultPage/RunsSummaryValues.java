package org.processmining.plugins.predictive_monitor.bpm.client_interface.resultPage;

public class RunsSummaryValues {
/*
	runId	correct  notPredicted  wrong  prediction truePositive trueNegative falsePositive falseNegative accuracy truePositiveRate trueNegativeRate positivePredictiveValues f1 earlinessAvg failureRate initTime totalProcessingTime averageProcessingTime
*/
	
	String runId;
	String correct;
	String notPredicted;
	String wrong;
	String accuracy;
	String earliness;
	String failureRate;
	String initTime;
	String totalProcessingTime;
	String averageProcessingTime;
	
	public RunsSummaryValues(String runId, String correct,	String notPredicted, String wrong, String accuracy,	String earlinessAvg, String failureRate, String initTime,String totalProcessingTime, String averageProcessingTime){
		this.runId = runId;
		this.correct = correct;
		this.notPredicted = notPredicted;
		this.wrong = wrong;
		this.accuracy = accuracy;
		this.earliness = earlinessAvg;
		this.failureRate = failureRate;
		this.initTime = initTime;
		this.totalProcessingTime = totalProcessingTime;
		this.averageProcessingTime = averageProcessingTime;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public String getCorrect() {
		return correct;
	}

	public void setCorrect(String correct) {
		this.correct = correct;
	}

	public String getNotPredicted() {
		return notPredicted;
	}

	public void setNotPredicted(String notPredicted) {
		this.notPredicted = notPredicted;
	}

	public String getWrong() {
		return wrong;
	}

	public void setWrong(String wrong) {
		this.wrong = wrong;
	}

	public String getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}

	public String getEarliness() {
		return earliness;
	}

	public void setEarliness(String earliness) {
		this.earliness = earliness;
	}

	public String getFailureRate() {
		return failureRate;
	}

	public void setFailureRate(String failureRate) {
		this.failureRate = failureRate;
	}

	public String getInitTime() {
		return initTime;
	}

	public void setInitTime(String initTime) {
		this.initTime = initTime;
	}

	public String getTotalProcessingTime() {
		return totalProcessingTime;
	}

	public void setTotalProcessingTime(String totalProcessingTime) {
		this.totalProcessingTime = totalProcessingTime;
	}

	public String getAverageProcessingTime() {
		return averageProcessingTime;
	}

	public void setAverageProcessingTime(String averageProcessingTime) {
		this.averageProcessingTime = averageProcessingTime;
	}
}
