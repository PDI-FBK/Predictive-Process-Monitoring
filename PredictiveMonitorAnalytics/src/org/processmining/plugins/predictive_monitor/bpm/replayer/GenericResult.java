package org.processmining.plugins.predictive_monitor.bpm.replayer;

import java.util.HashMap;
import java.util.Map;

public class GenericResult {
	protected Map<String,String> row;
	public GenericResult(){
		row = new HashMap<String, String>();
	}
	public Map<String,String> getRow()
	{
		return row;
	}
	
	public void removeRow()
	{
		row = null;
	}
	
}
