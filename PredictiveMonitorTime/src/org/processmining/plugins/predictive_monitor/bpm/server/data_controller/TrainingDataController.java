package org.processmining.plugins.predictive_monitor.bpm.server.data_controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.processmining.plugins.predictive_monitor.bpm.server.data.TrainingData;

public class TrainingDataController extends GenericDataController{
	List<TrainingData> trainingData;
	
	public TrainingDataController()
	{
		trainingData = new ArrayList<TrainingData>();
		id=0;
	}
	
	public TrainingData getTrainingData(Map<String,Object> configuration) throws InterruptedException	
	{
		System.out.println("\tChecking for existing TrainingDataStructures...");
		for(TrainingData t:trainingData)
		{
			if(t.isCompatible(configuration,config,wait)){
				System.out.println("\tLoading temporary TrainingDataStructure...");
				TrainingData t1 = new TrainingData(config,wait,id,configuration);
				//System.out.println("Existing hash:"+t.getHash()+" new hash:"+t1.getHash());
				if(t.getHash().equals(t1.getHash()))
				{
					System.out.println("\tStructure already in memory");
					t1=null;
					System.gc();
					return t;
				}
				else
				{
					trainingData.add(t1);
					id++;
					System.out.println("\tNew TrainingDataStructure created from the temporary one");
					return t1;
				}
			}
		}
		System.out.println("\tCreating new TrainingDataStructure...");
		TrainingData t = new TrainingData(config,wait,id,configuration);
		trainingData.add(t);
		id++;
		System.out.println("\tTrainingDataStructure created");
		return t;
	}
}