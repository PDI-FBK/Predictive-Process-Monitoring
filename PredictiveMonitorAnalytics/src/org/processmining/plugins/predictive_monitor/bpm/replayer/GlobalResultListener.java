package org.processmining.plugins.predictive_monitor.bpm.replayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class GlobalResultListener {
	Map<String,ResultListener> runResults;	
	
	public GlobalResultListener(Set<String> runIds, boolean jumpToCurrentEvent)
	{
		runResults = new HashMap<String, ResultListener>();
		for(String runId: runIds)
		{
			runResults.put(runId, new ResultListener(jumpToCurrentEvent));
		}
	}
		
	public Map<String,ResultListener> getRunResults()
	{
		return runResults;
	}
	
	public ResultListener getResultListener(String runId)
	{
		return runResults.get(runId);
	}
}
