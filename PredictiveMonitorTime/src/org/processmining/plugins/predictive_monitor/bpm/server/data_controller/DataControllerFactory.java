package org.processmining.plugins.predictive_monitor.bpm.server.data_controller;

import java.io.IOException;

public class DataControllerFactory {
	private static TrainingDataController trainingDataController;
	private static LabelledDataController labelledDataController;
	private static ClusteredDataController clusteredDataController;
	private static ClassifiedDataController classifiedDataController;
	
	
	public static TrainingDataController getTrainingDataController()
	{
		if(trainingDataController==null)
		{
			trainingDataController = new TrainingDataController();
		}
		return trainingDataController;
	}
	
	public static LabelledDataController getLabelledDataController() throws IOException
	{
		if(labelledDataController==null)
		{
			labelledDataController = new LabelledDataController();
		}
		return labelledDataController;
	}
	
	public static ClusteredDataController getClusteredDataController() throws IOException
	{
		if(clusteredDataController==null)
		{
			clusteredDataController = new ClusteredDataController();
		}
		return clusteredDataController;
	}
	
	public static ClassifiedDataController getClassifiedDataController() throws IOException
	{
		if(classifiedDataController==null)
		{
			classifiedDataController = new ClassifiedDataController();
		}
		return classifiedDataController;
	}
}
