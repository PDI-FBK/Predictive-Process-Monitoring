package org.processmining.plugins.predictive_monitor.bpm.server.data_controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.processmining.plugins.predictive_monitor.bpm.PredictiveMonitor;
import org.processmining.plugins.predictive_monitor.bpm.server.data.LabelledData;
import org.processmining.plugins.predictive_monitor.bpm.server.data.TrainingData;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier.PredictionType;

public class LabelledDataController extends GenericDataController{
	List<LabelledData> labelledData;
	
	public LabelledDataController() throws IOException
	{
		labelledData = new ArrayList<LabelledData>();
		path += "/labelling";
		//loadConfigurations(path);
	}
	
	public LabelledData getLabelledData(Map<String,Object> configuration, TrainingData trainingData) throws InterruptedException, IOException
	{
		System.out.println("\tChecking for existing LabelledDataStructures in main memory...");
		for(LabelledData l:labelledData)
		{
			if(l.isCompatible(configuration, config, wait)){
				System.out.println("\tStructure already in memory");
				return l;
			}
		}
		System.out.println("\tChecking for existing LabelledDataStructures on disk...");
		for(Long cId: configurations.keySet())
		{
			if(isCompatible(configuration, cId))
			{
				System.out.println("\tLoading Structure in main memory...");
				LabelledData l = new LabelledData(config,wait,cId,configuration, trainingData,path, true);
				System.out.println("\tDone");
				return l;
			}
		}
		System.out.println("\tCreating new LabelledDataStructure...");
		LabelledData l = new LabelledData(config,wait,id,configuration, trainingData,path);
		labelledData.add(l);
		id++;
		System.out.println("\tLabelledDataStructure created");
		return l;
	}
}
