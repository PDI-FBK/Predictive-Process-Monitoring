package org.processmining.plugins.predictive_monitor.bpm.replayer;

import java.text.DecimalFormat;
import java.util.Map;

import org.jfree.chart.needle.ShipNeedle;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.TraceEvaluationResult;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.LongRange;

import javafx.beans.property.SimpleStringProperty;

public class EvaluationResult extends GenericResult{
	
	String traceId ;
	String evaluationPoint;
	String prediction;
	String expectation;
	String result;
	String confidence;
	String support;
	String time;
	
	public EvaluationResult(TraceEvaluationResult traceResult) {
		super();
		
		/*System.out.println("Trace: "+XConceptExtension.instance().extractName(traceResult.getTraceRun().getTrace())+
				"\n\t\tEvaluationPoint: "+traceResult.getResult().getIndex()+
				"\n\t\tResult: "+traceResult.getResult().getLabel()+
				"\n\t\tConfidence: "+traceResult.getResult().getConfidence()+
				"\n\t\tSupport: "+traceResult.getResult().getSupport()+
				"\n\t\tConfiguration:"+traceResult.getTraceRun().getRunId()+
				"\n\t\tClassification:"+traceResult.getTraceRun().getClassification()+
				"\n\t\tCorrect:"+traceResult.getCorrectness().getLabel());
	*/
		DecimalFormat df = new DecimalFormat("#.##");
		traceId = new String(traceResult.getTraceRun().getName());
		evaluationPoint = new String(df.format(traceResult.getResult().getIndex()));
		prediction = "";
		
		for(String s: traceResult.getResult().getResults().keySet())
		{
			/*if(traceResult.getResult().getResults().get(s) instanceof LongRange && jumpToCurrentEvent)
			{
				//((LongRange)traceResult.getResult().getResults().get(s));
			}*/
			prediction+= s+": "+ traceResult.getResult().getResults().get(s).getLabel()+" ";
		}
		expectation = "";
		
		for(String s: traceResult.getTraceRun().getClassifications().keySet())
		{
			expectation+= s+": "+ prettyPrint(""+traceResult.getTraceRun().getClassifications().get(s))+" ";
		}
		
		result = new String(traceResult.getCorrectness().getLabel());
		confidence = new String(df.format(traceResult.getResult().getConfidence()));
		support = new String(df.format(traceResult.getResult().getSupport()));
		time = new String(df.format(traceResult.getResult().getTime()));
		row.put("Trace Id",traceId);
		row.put("Evaluation Point",evaluationPoint);
		row.put("Prediction",prediction);
		row.put("Expectation",expectation);
		row.put("Result",result);
		row.put("Confidence",confidence);
		row.put("Support",support);
		row.put("Time",time);
	}

	private String prettyPrint(String s) {
		try{
			Long l = Long.parseLong(s);
			return LongRange.longToTime(l);
		}
		catch(Exception e)
		{}
		return s;
	}

	public EvaluationResult(Map<String, String> row) {
		//System.out.println("---------------------------- Received Start: --------------------------------");
		//System.out.println("TraceId: " + row.get("Trace Id"));
		traceId = row.get("Trace Id");
		//System.out.println("Evaluation Point: " + row.get("Evaluation Point"));
		evaluationPoint = row.get("Evaluation Point");
		//System.out.println("Prediction: " + row.get("Prediction"));
		prediction = row.get("Prediction");
		//System.out.println("Expectation: " + row.get("Expectation"));
		expectation = row.get("Expectation");
		//System.out.println("Result: " + row.get("Result"));
		result = row.get("Result");
		//System.out.println("Confidence: " + row.get("Confidence"));
		confidence = row.get("Confidence");
		//System.out.println("Support: " + row.get("Support"));
		support = row.get("Support");
		//System.out.println("Time: " + row.get("Time"));
		time = row.get("Time");
		//System.out.println("---------------------------- :Received End --------------------------------");
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public String getEvaluationPoint() {
		return evaluationPoint;
	}

	public void setEvaluationPoint(String evaluationPoint) {
		this.evaluationPoint = evaluationPoint;
	}

	public String getPrediction() {
		return prediction;
	}

	public void setPrediction(String prediction) {
		this.prediction = prediction;
	}

	public String getExpectation() {
		return expectation;
	}

	public void setExpectation(String expectation) {
		this.expectation = expectation;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getConfidence() {
		return confidence;
	}

	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}

	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	

}
