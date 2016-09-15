package org.processmining.plugins.predictive_monitor.bpm.server.data_controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.processmining.plugins.predictive_monitor.bpm.server.data.ClassifiedData;
import org.processmining.plugins.predictive_monitor.bpm.server.data.ClusteredData;
import org.processmining.plugins.predictive_monitor.bpm.server.data.LabelledData;

public class ClassifiedDataController extends GenericDataController{
	List<ClassifiedData> classifiedData;
	
	public ClassifiedDataController() throws IOException
	{
		classifiedData = new ArrayList<ClassifiedData>();
		path += "/classification";
		//loadConfigurations(path);
	}
	
	public ClassifiedData getClassifiedData(Map<String,Object> configuration,ClusteredData clusteredData, LabelledData labelledData) throws InterruptedException, IOException	
	{
		System.out.println("\tChecking for existing ClassifiedDataStructures...");
		for(ClassifiedData c:classifiedData)
		{
			if(c.isCompatible(configuration,config,wait)){
				System.out.println("\tStructure already in memory");
				return c;
			}
		}
		System.out.println("\tCreating new ClassifiedDataStructure...");
		ClassifiedData c = new ClassifiedData(config,wait,id,configuration,clusteredData, labelledData,path);
		classifiedData.add(c);
		id++;
		System.out.println("\tClassifiedDataStructure created");
		return c;
	}
}
