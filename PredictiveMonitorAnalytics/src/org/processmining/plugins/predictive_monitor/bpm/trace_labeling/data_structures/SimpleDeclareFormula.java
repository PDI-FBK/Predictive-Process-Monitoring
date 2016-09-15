package org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures;

import org.processmining.plugins.declareminer.enumtypes.DeclareTemplate;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.string_values.String_Values;

public class SimpleDeclareFormula extends String_Values implements Formula{
	
	private String param1;
	private String param2;
	private DeclareTemplate template;
	
	public SimpleDeclareFormula() {
		super();

		this.param1 = null;
		this.param2 = null;
		this.template = null;
		
		super.addDefaultValue("Not Yet Implmented");
		//super.setDependendingFromFields("ClusteringTypeAgglomerative");
	}
	
	
	public SimpleDeclareFormula(String param1,
			String param2, DeclareTemplate template) {
		super();
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




	public String getParam1() {
		return param1;
	}


	public String getParam2() {
		return param2;
	}


	public DeclareTemplate getTemplate() {
		return template;
	}

	public String[] getParams(){
		String[] parameters;
		if (param2==null){
			parameters = new String[1];
			parameters[0] = param1;
		} else {
			parameters = new String[2];
			parameters[0] = param1;
			parameters[1]= param2;
		}
		return parameters;
			
	}
	
	
	

}
