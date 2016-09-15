package org.processmining.plugins.predictive_monitor.bpm.replayer;

import java.text.DecimalFormat;
import java.util.Map;

import org.processmining.plugins.predictive_monitor.bpm.operational_support.TraceEvaluationResult;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.TraceResult;

public class PredictionResult extends GenericResult{
	
	String traceId;
	String evaluationPoint;
	String prediction;
	String confidence;
	String support;
	String time;
	String expectation;
	String correct;
	
	public PredictionResult(TraceResult traceResult) {
		super();
		/*System.out.println("Trace: "+XConceptExtension.instance().extractName(traceResult.getTraceRun().getTrace())+
				"\n\t\tEvaluationPoint: "+traceResult.getResult().getIndex()+
				"\n\t\tPrediction: "+traceResult.getResult().getLabel()+
				"\n\t\tConfidence: "+traceResult.getResult().getConfidence()+
				"\n\t\tSupport: "+traceResult.getResult().getSupport()+
				"\n\t\tConfiguration:"+traceResult.getTraceRun().getRunId());
		*/
		DecimalFormat df = new DecimalFormat("#.##");
		traceId = new String(traceResult.getTraceRun().getName());
		evaluationPoint = new String(df.format(traceResult.getResult().getIndex()));
		prediction = "";
		for(String s: traceResult.getResult().getResults().keySet())
		{
			prediction+= s+": "+ traceResult.getResult().getResults().get(s).getLabel()+" ";
		}
		confidence = new String(df.format(traceResult.getResult().getConfidence()));
		support = new String(df.format(traceResult.getResult().getSupport()));
		time = new String(df.format(traceResult.getResult().getTime()));
		//if(traceResult instanceof TraceEvaluationResult)
		{
		//	correct = ((TraceEvaluationResult)traceResult).getCorrectness().getLabel();
			//expectation = new String(df.format(traceResult.getResult().get))
		}
		row.put("Trace Id",traceId);
		row.put("Evaluation Point",evaluationPoint);
		row.put("Prediction",prediction);
		row.put("Confidence",confidence);
		row.put("Support",support);
		row.put("Time",time);
	}

	public PredictionResult(Map<String, String> row) {
		//System.out.println("---------------------------- Received Start: --------------------------------");
		//System.out.println("TraceId: " + row.get("Trace Id"));
		traceId = row.get("Trace Id");
		//System.out.println("Evaluation Point: " + row.get("Evaluation Point"));
		evaluationPoint = row.get("Evaluation Point");
		//System.out.println("Prediction: " + row.get("Prediction"));
		prediction = row.get("Prediction");
		//System.out.println("Confidence: " + row.get("Confidence"));
		confidence = row.get("Confidence");
		//System.out.println("Support: " + row.get("Support"));
		support = row.get("Support");
		//System.out.println("Time: " + row.get("Time"));
		time = row.get("Time");
		//System.out.println("---------------------------- :Received End --------------------------------");
		this.row = row;
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
