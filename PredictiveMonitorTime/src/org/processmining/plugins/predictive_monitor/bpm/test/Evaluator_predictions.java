package org.processmining.plugins.predictive_monitor.bpm.test;

import java.util.Map;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

public class Evaluator_predictions {
	
	public static String computeExpression(Map<String, String> variables, String expression) throws EvaluationException{
		String parsedExpression = parseExpression (expression, variables);
		Evaluator eval = new Evaluator();
		eval.setVariables(variables);
		eval.parse(parsedExpression);
		String result = eval.evaluate(parsedExpression);
		return result;
	}
	
	public static boolean evaluateExpression(Map<String, String> variables, String expression) throws EvaluationException{
		String parsedExpression = parseExpression (expression, variables);
		Evaluator eval = new Evaluator();
		eval.setVariables(variables);
		eval.parse(parsedExpression);
		boolean bool = eval.getBooleanResult(parsedExpression); 
		return bool;
	}
	
	
	
	private static String parseExpression(String expression, Map<String, String> map){
		String keywordString = "";
		String stringKeywordString = "";
		for (String keyword : map.keySet()) {
			if (map.get(keyword)!=null){
				if (isNumeric(map.get(keyword))){			
					if (keywordString.isEmpty())
						keywordString = keywordString.concat(keyword.trim());
					else
						keywordString = keywordString.concat("|"+keyword.trim());
				} else {
					if (stringKeywordString.isEmpty())
						stringKeywordString = stringKeywordString.concat(keyword.trim());
					else
						stringKeywordString = stringKeywordString.concat("|"+keyword.trim());
				}
			}
		}
		String parsedExpression = expression;
		if (!keywordString.isEmpty())
			parsedExpression = parsedExpression.replaceAll("("+keywordString+")", "#{$1}"); 
		if (!stringKeywordString.isEmpty())
			parsedExpression = parsedExpression.replaceAll("("+stringKeywordString+")", "'#{$1}'");
		return parsedExpression; 
	}
	
	
	
	public static boolean isNumeric(String str)
	{
		boolean numeric = false;
		if (str!=null)
			numeric =str.matches("-?\\d+(\\.\\d+)?"); 
		return numeric;
	}

}
