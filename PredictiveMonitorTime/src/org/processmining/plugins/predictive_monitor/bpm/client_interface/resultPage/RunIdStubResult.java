package org.processmining.plugins.predictive_monitor.bpm.client_interface.resultPage;

import javafx.beans.property.SimpleStringProperty;

public class RunIdStubResult extends StubResult {
    private final SimpleStringProperty traceId;
    private final SimpleStringProperty prediction;
    private final SimpleStringProperty evaluationPoint;
    private final SimpleStringProperty expectation;
    private final SimpleStringProperty confidence;
    private final SimpleStringProperty support;
    private final SimpleStringProperty time;    
    private final SimpleStringProperty result;
    
    public RunIdStubResult(SimpleStringProperty traceId, SimpleStringProperty predictedValue, SimpleStringProperty evaluationPoint, SimpleStringProperty expectation, SimpleStringProperty confidence, SimpleStringProperty support, SimpleStringProperty time, SimpleStringProperty result) {
    	this.traceId = traceId; 
    	this.prediction = predictedValue;
    	this.evaluationPoint = evaluationPoint;
    	this.expectation = expectation;
    	this.confidence = confidence;
    	this.support = support;
    	this.time = time;    
    	this.result = result;
    }

	public SimpleStringProperty getTraceId() {
		return traceId;
	}

	public SimpleStringProperty getPrediction() {
		return prediction;
	}

	public SimpleStringProperty getEvaluationPoint() {
		return evaluationPoint;
	}

	public SimpleStringProperty getExpectation() {
		return expectation;
	}

	public SimpleStringProperty getConfidence() {
		return confidence;
	}

	public SimpleStringProperty getSupport() {
		return support;
	}

	public SimpleStringProperty getTime() {
		return time;
	}

	public SimpleStringProperty getResult() {
		return result;
	}
 
    
}
