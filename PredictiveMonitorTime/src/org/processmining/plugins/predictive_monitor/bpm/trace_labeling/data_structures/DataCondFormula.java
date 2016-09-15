package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures;

import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.enumeration.DeclareTemplate;




public class DataCondFormula implements Formula {
	
	private String dataCondition;
	private String param1;
	private String param2;
	private DeclareTemplate template;
	
	public DataCondFormula() {

		this.dataCondition = null;
		this.param1 = null;
		this.param2 = null;
		this.template = null;
	}
	
	
	public DataCondFormula(String dataCondition, String param1,
			String param2, DeclareTemplate template) {
		super();
		this.dataCondition = dataCondition;
		this.param1 = param1;
		this.param2 = param2;
		this.template = template;
	}

	public String getLTLFormula() {
		String LTLFormula = null;
		switch (template) {
		case Response:
			LTLFormula = "( []( ( \""+param1+"\" -> <>( \""+param2+"\" ) ) ))";;
			break;
		case Precedence:
			LTLFormula = "( ! (\""+param2+"\" ) U \""+param1+"\" ) \\/ ([](!(\""+param2+"\"))) /\\ ! (\""+param2+"\" )";;
			break;
		case Responded_Existence:
			LTLFormula = "(( ( <>( \""+param1+"\" ) -> (<>( \""+param2+"\" ) )) ))";
			break;
		}
		return LTLFormula;
	}


	public String getDataCondition() {
		return dataCondition;
	}


	public String getParam1() {
		return param1;
	}


	public String getParam2() {
		return param2;
	}


	public DeclareTemplate getTemplate() {
		return template;
	}


	
	
	

}
