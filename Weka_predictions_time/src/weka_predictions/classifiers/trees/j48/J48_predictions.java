package weka_predictions.classifiers.trees.j48;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import weka_predictions.classifiers.trees.J48;
import weka_predictions.core.Attribute;
import weka_predictions.core.Instances;
import weka_predictions.data_predictions.Result;
import weka_predictions.data_predictions.ResultDecisionTree;
import context.arch.intelligibility.expression.Comparison;
import context.arch.intelligibility.expression.Expression;
import context.arch.intelligibility.expression.Parameter;
import context.arch.storage.AttributeNameValue;
import context.arch.widget.ClassifierWidget;

public class J48_predictions extends J48{
	
	private ArrayList<String> classLabels = new ArrayList<String>();


	private void visitTree(ClassifierTree root){
		ClassifierTree[] children = root.m_sons;
		for (int i = 0; i < children.length; i++) {
			try {
				ClassifierTree child = children[i];

				System.out.println(child.m_localModel);
				C45Split lM = (C45Split) child.m_localModel;
				System.out.println(child.m_train.attribute(lM.attIndex()));
				System.out.println(lM.splitPoint());
				if (child.m_isLeaf){

					System.out.println(child.m_localModel.dumpLabel(0, child.m_train));
				}
				else {
					System.out.println(child.m_localModel.leftSide(child.m_train) + " "+ child.m_localModel.rightSide(i,child.m_train));

					visitTree(child);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}

	public void visitTree(){
		visitTree(m_root);
	}

	public Map<String, Result> computeConditionInfo(Vector<String> currentVariables, Map<String, String> variables){
		Map<String, Object> knownVariables = new HashMap<String, Object>();
		for (String variable : variables.keySet()) {
			String value = variables.get(variable);
			Object objectValue = null;
			if (isNumeric(value))
				objectValue = new Double(value);
			else
				objectValue = value;
			knownVariables.put(variable, objectValue);

		}

		return computeConditionInfo(m_root, knownVariables, currentVariables, null, null);
	}


	public Map<String, Result> computeConditionInfo(
			ClassifierTree root,
			Map<String, Object> knownVariables,
			Vector<String> currentVariables,
			String tempCondition,
			Vector<Expression> ANDExpression){

		ClassifierSplitModel cSM = root.m_localModel;
		Map<String, Result> resultMap = new HashMap<String, Result>();
//		System.out.println(root.toString());
		Distribution d = cSM.distribution();
		Distribution rd = m_root.m_localModel.distribution();


		Instances data = root.m_train;

		try {
			if (root.m_isLeaf){
					if (d.numCorrect(0)!=0.0){
					Vector<Expression> newANDExpression = new Vector<Expression>();
					String condition = "EMPTY CONDITION";
					Double confidence = d.numCorrect(0)/(d.numIncorrect(0)+d.numCorrect(0));
					Double support = d.numCorrect(0);// /rd.total();
/*					boolean satisfied = false;
					if (cSM.dumpLabel(0, data).startsWith("yes"))
						satisfied = true;*/
					String wholeLabel = cSM.dumpLabel(0, data);

					//String label = wholeLabel.substring(0, wholeLabel.indexOf(" ("));
					String label = "";
					for (String classLabel : classLabels) {
						if (wholeLabel.startsWith(classLabel) && (classLabel.length()>label.length()))
							label = classLabel;
					}
					Result result = new ResultDecisionTree(newANDExpression, support, confidence, label);
					resultMap.put(condition, result);
					}
			}
			else {

				C45Split lM = (C45Split) root.m_localModel;
				Attribute attribute = data.attribute(lM.attIndex());

	//			System.out.println("ATTRIBUTE = "+attribute);
		//		System.out.println("ATTRIBUTENAME = "+attribute.name());

				double splitPoint = lM.splitPoint();
		//		System.out.println("SPLIT POINT "+splitPoint);

				ClassifierTree[] children = root.m_sons;
				for (int i = 0; i < children.length; i++) {

					boolean toBePruned = false;
					String condition = lM.leftSide(data)+" "+lM.rightSide(i, data);
//					System.out.println(condition);
					Parameter<?> currPar = createParameter(lM.rightSide(i, data).substring(1), attribute);
					String attributeName = attribute.name();
					Vector<Expression> newANDExpression = new Vector<Expression>();
					if (ANDExpression!=null)
						newANDExpression.addAll(ANDExpression);

					if (currentVariables.contains(attributeName)){
						if (tempCondition!=null)
							condition = tempCondition+" && "+ condition;
						newANDExpression.add(currPar);
					}
					if (knownVariables.containsKey(attributeName)){
						Class typeClass = currPar.getType();
						Comparable parValue = (Comparable) typeClass.cast(knownVariables.get(attributeName));
						Parameter par = Parameter.instance(attributeName, parValue);
						toBePruned =!(currPar.isSatisfiedBy(par));
	//					System.out.println(currPar + " "+ parValue +" " +toBePruned);
						condition = tempCondition;
					}
					if (children[i].m_isLeaf  && !toBePruned){
						if (d.numCorrect(i)!=0.0){
						Double confidence = d.numCorrect(i)/(d.numIncorrect(i)+d.numCorrect(i));
						Double support = d.numCorrect(i);// /rd.total();
/*						boolean satisfied = false;
						if (lM.dumpLabel(i, data).startsWith("yes"))
							satisfied = true;*/
						String wholeLabel = lM.dumpLabel(0, data);
						//String label = wholeLabel.substring(0, wholeLabel.indexOf(" ("));
						String label = "";
						for (String classLabel : classLabels) {
							if (wholeLabel.startsWith(classLabel) && (classLabel.length()>label.length()))
								label = classLabel;
						}
						Result result = new ResultDecisionTree(newANDExpression, support, confidence, label);
						if (condition == null)
							condition = "EMPTY CONDITION";
						resultMap.put(condition, result);
						}
						condition = null;
						newANDExpression = null;

					}

					if (!toBePruned){
						resultMap.putAll(computeConditionInfo(children[i], knownVariables, currentVariables, condition, newANDExpression));
					}
/*					else
						resultMap = new HashMap<String, Result>(); */
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultMap;
	}



	private static <T extends Comparable<? super T>> Parameter<?> createParameter(String cond, Attribute attr) {
		String attrName = attr.name();
		// extract relation and value
		String[] condParts = cond.split(" ", 2); // split at first ' '
		Comparison.Relation relation = Comparison.Relation.toRelation(condParts[0]);
		String strValue = condParts[1];

		T value  = null;
		if (attr.type()==3){
			// cast value class type
			value = (T) AttributeNameValue.valueOf(
				ClassifierWidget.wekaTypeToClass(1),
				strValue);

		}
		else {
		// cast value class type
			value = (T) AttributeNameValue.valueOf(
				ClassifierWidget.wekaTypeToClass(attr.type()),
				strValue);
		}
		
		// create Expression for name and condition
		Parameter<?> expression;
		if (attr.isNumeric()) {
			expression = Comparison.instance(attrName, value, relation);
		}
		else { // assume nominal, string or date
			expression = Parameter.instance(attrName, value);
		}


		return expression;
	}

	private static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}



	public ArrayList<String> getClassLabels() {
		return classLabels;
	}

	public void setClassLabels(ArrayList<String> classLabels) {
		this.classLabels = classLabels;
	}
}
