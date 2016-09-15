package org.processmining.plugins.predictive_monitor.bpm.client_interface.resultPage;

import javafx.beans.property.SimpleStringProperty;

public class GlobalStubResult extends StubResult{
    private final SimpleStringProperty runId;
	private final SimpleStringProperty correct;
    private final SimpleStringProperty notPredicted;
    private final SimpleStringProperty wrong;
    private final SimpleStringProperty truePositive;
    private final SimpleStringProperty trueNegative;
    private final SimpleStringProperty falsePositive;
    private final SimpleStringProperty falseNegative;
    private final SimpleStringProperty accuracy;
    private final SimpleStringProperty truePositiveRate;
    private final SimpleStringProperty trueNegativeRate;
    private final SimpleStringProperty positivePredictiveValues;
    private final SimpleStringProperty f1;
    private final SimpleStringProperty earlinessAvg;
    private final SimpleStringProperty failureRate;
    private final SimpleStringProperty initTime;
    private final SimpleStringProperty totalProcessingTime;
    private final SimpleStringProperty averageProcessingTime;
    
    public GlobalStubResult(String runId, String correct, String notPredicted, String wrong,
    		String truePositive, String trueNegative, String falsePositive, String falseNegative, 
    		String accuracy, String truePositiveRate, String trueNegativeRate, String positivePredictiveValues, 
    		String f1, String earlinessAvg,  String failureRate, 
    		String initTime, String totalProcessingTime, String averageProcessingTime) {
    	this.runId = new SimpleStringProperty(runId);
        this.correct = new SimpleStringProperty(correct);
        this.notPredicted = new SimpleStringProperty(notPredicted);
        this.wrong = new SimpleStringProperty(wrong);
        this.truePositive = new SimpleStringProperty(truePositive);
        this.trueNegative = new SimpleStringProperty(trueNegative);
        this.falsePositive = new SimpleStringProperty(falsePositive);
        this.falseNegative = new SimpleStringProperty(falseNegative);
        this.accuracy = new SimpleStringProperty(accuracy);
        this.truePositiveRate = new SimpleStringProperty(truePositiveRate);
        this.trueNegativeRate = new SimpleStringProperty(trueNegativeRate);
        this.positivePredictiveValues = new SimpleStringProperty(positivePredictiveValues);     
        this.f1 = new SimpleStringProperty(f1);
        this.earlinessAvg = new SimpleStringProperty(earlinessAvg);
        this.failureRate = new SimpleStringProperty(failureRate);          
        this.initTime = new SimpleStringProperty(initTime);
        this.totalProcessingTime = new SimpleStringProperty(totalProcessingTime);
        this.averageProcessingTime = new SimpleStringProperty(averageProcessingTime);
    }
 
    public SimpleStringProperty runIdProperty() {
		return runId;
	}

	public SimpleStringProperty correctProperty() {
		return correct;
	}

	public SimpleStringProperty notPredictedProperty() {
		return notPredicted;
	}

	public SimpleStringProperty wrongProperty() {
		return wrong;
	}

	public SimpleStringProperty truePositiveProperty() {
		return truePositive;
	}

	public SimpleStringProperty trueNegativeProperty() {
		return trueNegative;
	}

	public SimpleStringProperty falsePositiveProperty() {
		return falsePositive;
	}

	public SimpleStringProperty falseNegativeProperty() {
		return falseNegative;
	}
	
    public SimpleStringProperty accuracyProperty() {
		return accuracy;
	}

	public SimpleStringProperty truePositiveRateProperty() {
		return truePositiveRate;
	}

	public SimpleStringProperty trueNegativeRateProperty() {
		return trueNegativeRate;
	}

	public SimpleStringProperty positivePredictiveValuesProperty() {
		return positivePredictiveValues;
	}

	public SimpleStringProperty f1Property() {
		return f1;
	}

	public SimpleStringProperty earlinessAvgProperty() {
		return earlinessAvg;
	}

	public SimpleStringProperty failureRateProperty() {
		return failureRate;
	}

	public SimpleStringProperty initTimeProperty() {
		return initTime;
	}
	
	public SimpleStringProperty totalProcessingTimeProperty() {
		return totalProcessingTime;
	}	
	
	public SimpleStringProperty averageProcessingTimeProperty() {
		return averageProcessingTime;
	}	
}
