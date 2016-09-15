package org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.string_values;

import java.util.Set;
import java.util.TreeSet;

//introdurre traduzione per il gap classifier ad activationFormula
//"(  <>(\"tumor marker CA-19.9\") ) \\/ ( <> (\"ca-125 using meia\") ) "
public class LTLFormula extends String_Values{
	public LTLFormula(){
		super();
		super.addDefaultValue(new String("( <>(\"tumor marker CA-19.9\") ) \\/ ( <> (\"ca-125 using meia\") ))"));
		super.setTooltip("Placeholder");
		
		Set<String> dependendingFromFields = new TreeSet<String>();
		dependendingFromFields.add("ACTIVATION_VERIFICATION_CLASSIFIER");
		this.setDependendingFromFields(dependendingFromFields);
	}
}
