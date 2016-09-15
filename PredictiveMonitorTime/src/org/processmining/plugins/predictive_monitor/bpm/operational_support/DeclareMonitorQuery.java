package org.processmining.plugins.predictive_monitor.bpm.operational_support;

import java.util.Map;

import org.processmining.operationalsupport.client.QueryLanguage;

public class DeclareMonitorQuery extends QueryLanguage<Map<String, Object>, String, Object, Object> {

	public static final String MIME_TYPE = "text/declaremonitor";

	public static final DeclareMonitorQuery INSTANCE = new DeclareMonitorQuery();

	public DeclareMonitorQuery() {
		super(MIME_TYPE);
	}

}
