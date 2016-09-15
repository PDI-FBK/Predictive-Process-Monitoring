package weka_predictions.data_predictions;

import java.util.Vector;

public class DecisionNode {
	
	private String attributeName;
	private String 	ATTRIBUTE_TYPE;
	private Vector<DecisionEdge> outgoingEdges;
	
	public DecisionNode(String attributeName, String aTTRIBUTE_TYPE) {
		super();
		this.attributeName = attributeName;
		ATTRIBUTE_TYPE = aTTRIBUTE_TYPE;
	}
	
	
	public void addChild(String childName, String childType){
		DecisionNode childNode = new DecisionNode(childName, childType);
		DecisionEdge edge = new DecisionEdge(this, childNode);
	}
	
}
