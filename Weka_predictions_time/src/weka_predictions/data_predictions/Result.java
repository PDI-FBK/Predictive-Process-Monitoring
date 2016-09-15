package weka_predictions.data_predictions;


public abstract class Result {
	
	private long initializationTime; 
	private long predictionTime;
	private int numberOfClusters;
	private int evaluationPoint;
	
	public abstract String simplifiedPrettyString();

	public abstract String getLabel();

	public abstract double getConfidence();

	public abstract double getSupport();

	public long getInitializationTime() {
		return initializationTime;
	}

	public void setInitializationTime(long initializationTime) {
		this.initializationTime = initializationTime;
	}

	public long getPredictionTime() {
		return predictionTime;
	}

	public void setPredictionTime(long predictionTime) {
		this.predictionTime = predictionTime;
	}

	public int getNumberOfClusters() {
		return numberOfClusters;
	}

	public void setNumberOfClusters(int numberOfClusters) {
		this.numberOfClusters = numberOfClusters;
	}
	
	public void setEvaluationPoint(int evaluationPoint)
	{
		this.evaluationPoint=evaluationPoint;
	}
	
	public int  getEvaluationPoint()
	{
		return evaluationPoint;
	}
	

}
