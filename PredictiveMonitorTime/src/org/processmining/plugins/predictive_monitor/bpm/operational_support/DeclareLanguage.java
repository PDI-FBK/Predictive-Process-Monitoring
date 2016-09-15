package org.processmining.plugins.predictive_monitor.bpm.operational_support;


import org.processmining.operationalsupport.client.Language;
import org.processmining.plugins.declareminer.visualizing.AssignmentModel;

public class DeclareLanguage extends Language<AssignmentModel> {

	public static final String MIME_TYPE = "xml/declare";

	public static final DeclareLanguage INSTANCE = new DeclareLanguage();

	public DeclareLanguage() {
		super(MIME_TYPE);
	}

}
