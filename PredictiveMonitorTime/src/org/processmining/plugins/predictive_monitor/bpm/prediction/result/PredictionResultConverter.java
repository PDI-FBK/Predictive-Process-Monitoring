package org.processmining.plugins.predictive_monitor.bpm.prediction.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka_predictions.data_predictions.Result;

public class PredictionResultConverter {
	public static PredictionResult convertResult(Map<String,Result> result,Double confidence, Double support, int evaluationPoint, boolean valid, long time)
	{
		PredictionResult predictionResult = new PredictionResult(confidence,support,evaluationPoint,valid,time);
		for(String key: result.keySet())
		{
			String resultType="";
			String value="";
			
				resultType = result.get(key).getLabel().split(":")[0];
				value = result.get(key).getLabel().split(":")[1];
			
		

			switch(resultType)
			{
				case "label":
					predictionResult.addResult(key,new DiscreteResult(value));
					break;
				case "longInterval":
					long start=Long.parseLong(value.split("-")[0]);
					long end=Long.parseLong(value.split("-")[1]);
					LongRange interval = new LongRange(start, end);
					predictionResult.addResult(key,interval);
					break;
				case "real":
					double realNumber = Double.parseDouble(value);
					predictionResult.addResult(key, new RealResult(realNumber));
				case "long":
					long longNumber = Long.parseLong(value);
					predictionResult.addResult(key, new LongResult(longNumber));
			}
			
		}
		
		return predictionResult;
	}
	
	public static String toLabel(String value)
	{
		return "label:"+value;
	}

	public static String toLongInterval(String value) {
		return "longInterval:"+value;
	}

	public static String toLong(String value) {
		return "long:"+value;
	}
}
