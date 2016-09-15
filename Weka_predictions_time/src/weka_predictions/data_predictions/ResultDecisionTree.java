package weka_predictions.data_predictions;

import java.util.Vector;

import context.arch.intelligibility.expression.Comparison;
import context.arch.intelligibility.expression.Expression;
import context.arch.intelligibility.expression.Parameter;

public class ResultDecisionTree  extends Result{

	private Vector<Expression> expressions;
	private Double support;
	private Double confidence;
	private String label="";

	public ResultDecisionTree(Vector<Expression> expressions, Double probability, Double confidence, String label) {
		super();
		this.expressions = expressions;
		this.support = probability;
		this.confidence = confidence;
		this.label = label;
	}

	public String prettyString(){
			String prettyString = null;
			for (Expression expression : expressions) {
				if (prettyString==null)
					prettyString = "IF (";
				else
					prettyString = prettyString.concat(" AND ");
				prettyString = prettyString.concat(expression.toString());
			}
			prettyString = prettyString.concat(") => ");
/*			if (label)
				prettyString = prettyString.concat("SATISFIED ");
			else
				prettyString = prettyString.concat("NOT SATISFIED ");*/
			prettyString = prettyString.concat(label+" ");
			prettyString = prettyString.concat(" SUPPORT = "+support+" CONF = "+confidence);
			return prettyString;
	}

	public String simplifiedPrettyString(){
		String prettyString = null;
		if (expressions!=null && !expressions.isEmpty()){
			Vector<Expression> simplifiedExpressions = simplifyExpressions();
			for (Expression expression : simplifiedExpressions) {
				if (prettyString==null)
					prettyString = "IF (";
				else
					prettyString = prettyString.concat(" AND ");
				if (expression!=null)
					prettyString = prettyString.concat(expression.toString());

			}
			if (prettyString!=null)
				prettyString = prettyString.concat(") => ");
		} else
			prettyString = "";
		if (prettyString!=null){
/*			if (label)
				prettyString = prettyString.concat("SATISFIED ");
			else
				prettyString = prettyString.concat("NOT SATISFIED ");*/
			prettyString = prettyString.concat(label+" ");			
			prettyString = prettyString.concat(" PROB = "+support+" CONF = "+confidence);
		}
		return prettyString;
}


	public Vector<Expression> simplifyExpressions(){
		Vector<Expression> clonedExpression = cloneExpressions();
		Vector<Expression> simplifiedExpressions = new Vector<Expression>();
		simplifiedExpressions = (Vector<Expression>) clonedExpression.clone();
		for (Expression expression1 : clonedExpression) {
			int i = clonedExpression.indexOf(expression1);
			if (expression1 instanceof Comparison){
				Comparison par1 = (Comparison) expression1;
				while (i+1<clonedExpression.size()){
					Expression expression2 = clonedExpression.get(i+1);
					if (expression2 instanceof Comparison){
						Comparison par2 = (Comparison) expression2;
					if (par1.setRange(par2))
						simplifiedExpressions.remove(par2);
					}
				i++;
				}
			}
		}
		return simplifiedExpressions;
	}

	private Vector<Expression> cloneExpressions(){
		Vector<Expression> clonedExpressions = new Vector<Expression>();
		for (Expression expression : expressions) {
			Parameter parameter = (Parameter) expression;
			Expression simplifiedExpression = parameter.clone();
			clonedExpressions.add(simplifiedExpression);
		}
		return clonedExpressions;
	}

	

	public String getLabel() {
		return label;
	}

	public Vector<Expression> getExpressions() {
		return expressions;
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
