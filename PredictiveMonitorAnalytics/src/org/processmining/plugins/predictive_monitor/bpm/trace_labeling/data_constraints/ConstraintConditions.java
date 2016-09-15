package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_constraints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.processmining.plugins.declareminer.enumtypes.DeclareTemplate;

/**
 * This class provides the conditions that might be required for a constraint.
 * This class distinguishes among three types of conditions:
 * <ol>
 * 	<li>Data conditions on activation;</li>
 * 	<li>Data conditions on the constraint;</li>
 * 	<li>Time conditions.</li>
 * </ol>
 * 
 * @author Andrea Burattin
 */
public class ConstraintConditions {

	/**
	 * This enumerate type describes the possible time granularities supported
	 * by the system.
	 */
	public enum TIME_GRANULARITY {
		DAY, /** Days */
		HOUR,
		MINUTE,
		SECOND, /** One second */
		MILLISECOND;
		
		/**
		 * Static method to create a {@link TIME_GRANULARITY} from starting from
		 * a string representation.
		 * 
		 * @param s a string representation for a time granularity
		 * @return the corresponding time granularity if matched, {@link #SECOND} otherwise
		 */
		public static TIME_GRANULARITY getEnum(String s){
			if (s == null) {
				return SECOND;
			}
			s = s.toLowerCase().trim();
			if (s.equals("d") || s.equals("day") || s.equals("days")) {
				return DAY;
			} else if (s.equals("h") || s.equals("hour") || s.equals("hours")) {
				return HOUR;
			} else if (s.equals("m") || s.equals("minute") || s.equals("minutes")) {
				return MINUTE;
			} else if (s.equals("s") || s.equals("second") || s.equals("seconds")) {
				return SECOND;
			} else if (s.equals("ms") || s.equals("millisecond") || s.equals("milliseconds")) {
				return MILLISECOND;
			}
			return SECOND;
		}
	};
	
	private static final String CONDITION_PATTERN = "\\[(.*)\\]\\[(.*)\\]\\[((.*),(.*),(d|h|m|s|ms))?\\]";
	private static final Pattern pattern = Pattern.compile(CONDITION_PATTERN);
	
	private String activationCondition = null;
	private String constraintCondition = null;
	private Long timeLeft = null;
	private Long timeRight = null;
	
	/**
	 * This method is used to build a new {@link ConstraintConditions}. This
	 * method receives, as input, a string that represents a formula. The
	 * current version, supports three possible string formats:
	 * <ul>
	 * 	<li><tt>[A][B][t1,t2,g]</tt>: in this case, <tt>A</tt> represents a data
	 * 		condition on the activation; <tt>B</tt> represents a data condition
	 * 		on the entire constraint; <tt>t1</tt> and <tt>t2</tt> represents the
	 * 		time interval in which the constraint must be satisfied and
	 * 		<tt>g</tt> represent the time granularity (can be one of:
	 * 		<tt>d</tt>, <tt>h</tt>, <tt>m</tt>, <tt>s</tt>, <tt>ms</tt>);</li>
	 * 	<li><tt>[t1,t2,g]</tt>: this is equivalent to <tt>[][][t1,t2,g]</tt>;
	 * 		</li>
	 * 	<li><tt>A</tt>: this is equivalent to <tt>[A][][]</tt>.</li>
	 * </ul>
	 * 
	 * Examples of valid constraints are:
	 * <ul>
	 * 	<li><tt>[condition][][1,     4,m]</tt>;</li>
	 * 	<li><tt>condition</tt>;</li>
	 * 	<li><tt>[5,6,d]</tt>;</li>
	 * 	<li>"<tt> </tt>" (the empty condition).</li>
	 * </ul>
	 * 
	 * @param formula the string that represent the formula
	 * @return the {@link ConstraintConditions} built
	 */
	public static ConstraintConditions build(String formula) {
		formula = preprocess(formula);
		ConstraintConditions c = new ConstraintConditions();
		Matcher m = pattern.matcher(formula);
		if (!m.matches()) {
			return c;
		}

		c.activationCondition = c.trimIfNotNull(m.group(1));
		c.constraintCondition = c.trimIfNotNull(m.group(2));
		c.timeLeft = c.longIfNotNull(m.group(4));
		c.timeRight = c.longIfNotNull(m.group(5));
		
		// we convert everything to milliseconds
		if (c.timeLeft + c.timeRight > 0) {
			switch (TIME_GRANULARITY.getEnum(m.group(6))) {
				case DAY :
					c.timeLeft = c.timeLeft * 1000 * 60 * 60 * 24;
					c.timeRight = c.timeRight * 1000 * 60 * 60 * 24;
					break;
				case HOUR :
					c.timeLeft = c.timeLeft * 1000 * 60 * 60;
					c.timeRight = c.timeRight * 1000 * 60 * 60;
					break;
				case MINUTE :
					c.timeLeft = c.timeLeft * 1000 * 60;
					c.timeRight = c.timeRight * 1000 * 60;
					break;
				case SECOND :
					c.timeLeft = c.timeLeft * 1000;
					c.timeRight = c.timeRight * 1000;
					break;
				case MILLISECOND :
					break;
			};
		}
		
		return c;
	}
	
	/**
	 * This method performs some common pre-processing to the given formula.
	 * With this method it is possible to parse formula generated with the
	 * current Declare miner (i.e., formula only on activations or formula only
	 * times).
	 * 
	 * @param formula a string with a general formula
	 * @return the new formula, compliant to the rules of this class
	 */
	private static String preprocess(String formula) {
		formula = removeIfTemplateName(formula);
		if (!formula.contains("[") && !formula.contains("]")) {
			return "[" + formula + "][][]";
		}
		if (StringUtils.countMatches(formula, "[") == 1) {
			return "[][]" + formula;
		}
		return formula;
	}
	
	/**
	 * This method checks if the given formula is just a constraint name. If
	 * this is the case, then the formula is replaces with the empty one.
	 * 
	 * @param formula the formula to be parsed
	 * @return if the provided formula is a constraint name, this method
	 * returns the empty formula, otherwise it returns the provided formula
	 */
	private static String removeIfTemplateName(String formula) {
		String formulaToCheck = formula.toLowerCase().trim();
		DeclareTemplate declareTemplate = DeclareTemplate.Absence;
		DeclareTemplate[] declareTemplateNames = declareTemplate.getDeclaringClass().getEnumConstants(); 
		for(DeclareTemplate d : declareTemplateNames){ 
			String templateName = d.toString().replaceAll("_", " ").toLowerCase().trim();
			if (formulaToCheck.equals(templateName)) {
				return "";
			}
		}
		String[] otherNames = {
			// absence
			"0",
			"0..1",
			"0..2",
			// existence
			"1..*",
			"2..*",
			"3..*",
			// exactly
			"1",
			"2"
			};
		for(String templateName : otherNames){ 
			if (formulaToCheck.equals(templateName)) {
				return "";
			}
		}
		return formula;
	}
	
	/**
	 * This method returns the activation condition for JEval. If no activation
	 * condition has been specified, an always-true condition is returned.
	 * 
	 * @return the activation condition string to be analyzed by JEval
	 */
	public String getActivationCondition() {
		if (activationCondition.isEmpty()) {
			return "(1)";
		}
		return "(" + activationCondition + ")";
	}
	
	/**
	 * This method returns the constraint condition for JEval. If no constraint
	 * condition has been specified, an always-true condition is returned.
	 * 
	 * @return the constraint condition string to be analyzed by JEval
	 */
	public String getConstraintCondition() {
		if (constraintCondition.isEmpty()) {
			return "(1)";
		}
		return "(" + constraintCondition + ")";
	}
	
	/**
	 * This method returns the time condition for JEval. If no time condition
	 * has been specified, an always-true condition is returned.
	 * 
	 * @param milliseconds the current time difference between activation and
	 * satisfaction events
	 * @return the time condition string to be analyzed by JEval
	 */
	public String getTimeCondition(Long milliseconds) {
		if (timeLeft + timeRight == 0) {
			return "(1)";
		}
		if (milliseconds >= timeLeft && milliseconds <= timeRight) {
			return "(1)";
		}
		return "(0)";
	}
	
	/**
	 * This method returns the conjunction of the constraint
	 * ({@link #getConstraintCondition()}) and time
	 * ({@link #getTimeCondition(Long)}) condition.
	 * 
	 * @param milliseconds the current time difference between activation and
	 * satisfaction events
	 * @return the conjunction condition string to be analyzed by JEval
	 */
	public String getCompleteConstraintCondition(Long milliseconds) {
		String c = getConstraintCondition();
		String t = getTimeCondition(milliseconds);
		if (t.equals("(0)")) {
			return "(0)";
		}
		return c;
	}
	
	/**
	 * This method returns whether an activation condition is contained, or not
	 * 
	 * @return <tt>true</tt> if an activation condition is provided,
	 * <tt>false</tt> otherwise
	 */
	public boolean containsActivationCondition() {
		return !activationCondition.equals("");
	}
	
	/**
	 * This method returns whether a constraint condition is contained, or not
	 * 
	 * @return <tt>true</tt> if a constraint condition is provided,
	 * <tt>false</tt> otherwise
	 */
	public boolean containsConstraintCondition() {
		return !constraintCondition.equals("");
	}
	
	/**
	 * This method returns whether a time condition is contained, or not
	 * 
	 * @return <tt>true</tt> if a time condition is provided, <tt>false</tt>
	 * otherwise
	 */
	public boolean containsTimeCondition() {
		return timeLeft + timeRight != 0;
	}
	
	/**
	 * Method to trim a string
	 * 
	 * @param input a string or <tt>null</tt>
	 * @return the trimmed string or, if <tt>null</tt> is given, the empty
	 * string
	 */
	private String trimIfNotNull(String input) {
		if (input == null) {
			return "";
		}
		return input.trim();
	}
	
	/**
	 * Method to convert a string into a long
	 * 
	 * @param input a string or <tt>null</tt>
	 * @return the long number or, if <tt>null</tt> is given, 0
	 */
	private Long longIfNotNull(String input) {
		if (input == null) {
			return 0L;
		}
		return Long.parseLong(input.trim());
	}
	
	@Override
	public String toString() {
		return "Activation condition: ``" + activationCondition + "''\n" +
				"Constraint condition: ``" + constraintCondition + "''\n" +
				"Time constraint: [" + timeLeft + " - " + timeRight + "] ms";
	}
	
	public static void main(String[] a) {
		for (String s : new String[]{
				"[aaa][][1,     4,m]",
				"second",
				"[5,6,d]",
				"",
				"[][test][1,3,s]",
				"Succession"}){
			System.out.println("\"" + s +"\"");
			ConstraintConditions c = ConstraintConditions.build(s);
			System.out.println("a = " + c.getActivationCondition());
			System.out.println("c = " + c.getConstraintCondition());
			System.out.println("t = " + c.getTimeCondition(1L));
			System.out.println("complete = " + c.getCompleteConstraintCondition(2000L));
			System.out.println(c.containsActivationCondition());
			System.out.println(c.containsConstraintCondition());
			System.out.println(c.containsTimeCondition());
			System.out.println("");
		}
	}
}
