package org.processmining.plugins.predictive_monitor.bpm.server.data_controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.processmining.plugins.predictive_monitor.bpm.server.data.ClusteredData;
import org.processmining.plugins.predictive_monitor.bpm.server.data.LabelledData;

public class ClusteredDataController extends GenericDataController{
	List<ClusteredData> clusteredData;
	
	public ClusteredDataController() throws IOException
	{
		clusteredData = new ArrayList<ClusteredData>();
		id=0;
		path+="/clustering";
		//loadConfigurations(path); TODO fix file export
	}
	
	public ClusteredData getClusteredData(Map<String,Object> configuration,LabelledData labelledData) throws InterruptedException, IOException	
	{
		System.out.println("\tChecking for existing ClusteredDataStructures in main memory...");
		for(ClusteredData c:clusteredData)
		{
			if(c.isCompatible(configuration,config,wait)){
				System.out.println("\tStructure already in memory");
				return c;
			}
		}
		System.out.println("\tChecking for existing ClusteredDataStructures on disk...");
		for(Long cId: configurations.keySet())
		{
			if(isCompatible(configuration, cId))
			{
				System.out.println("\tLoading Structure in main memory...");
				ClusteredData c = new ClusteredData(config,wait,cId,configuration,labelledData,path,true);
				System.out.println("\tDone");
				return c;
			}
		}
		System.out.println("\tCreating new ClusteredDataStructure...");
		ClusteredData c = new ClusteredData(config,wait,id,configuration,labelledData,path);
		id++;
		clusteredData.add(c);
		System.out.println("\tClusteredDataStructure created");
		return c;
	}
}
