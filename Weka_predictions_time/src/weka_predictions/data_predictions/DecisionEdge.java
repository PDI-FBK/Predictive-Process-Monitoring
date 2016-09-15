package weka_predictions.data_predictions;

public class DecisionEdge {
	
	private Condition condition;
	private DecisionNode source;
	private DecisionNode target;
	
	public DecisionEdge(DecisionNode source, DecisionNode target) {
		super();
		this.source = source;
		this.target = target;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public DecisionNode getSource() {
		return source;
	}

	public DecisionNode getTarget() {
		return target;
	}
	
	

}
