package weka_predictions.data_predictions;


public class ResultRandomForest  extends Result{

	private Double support;
	private Double confidence;
	private String label="";

	public ResultRandomForest(Double probability, Double confidence, String label) {
		super();
		this.support = probability;
		this.confidence = confidence;
		this.label = label;
	}

	public String prettyString(){
			String prettyString = null;
/*			if (satisfied)
				prettyString = prettyString.concat("SATISFIED ");
			else
				prettyString = prettyString.concat("NOT SATISFIED ");*/
			prettyString = prettyString.concat(label+" ");
			prettyString = prettyString.concat(" PROB = "+support+" CONF = "+confidence);
			return prettyString;
	}


	

	public String getLabel() {
		return label;
	}

	public String simplifiedPrettyString() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getConfidence() {
		// TODO Auto-generated method stub
		return confidence;
	}

	public double getSupport() {
		// TODO Auto-generated method stub
		return support;
	}





}
