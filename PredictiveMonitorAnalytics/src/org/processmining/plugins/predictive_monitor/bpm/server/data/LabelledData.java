package org.processmining.plugins.predictive_monitor.bpm.server.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.predictive_monitor.bpm.PredictiveMonitor;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.ActivationVerificationGapClassifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.SatisfactionClassifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.TimeClassifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier.PredictionType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.TimeClassifier.PartitionMethod;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.SimpleFormula;
import org.processmining.plugins.predictive_monitor.bpm.utility.TracePrefixGenerator;

import com.thoughtworks.xstream.XStream;

public class LabelledData extends GenericDataStructure{
	
	private Classifier classifier;
	private final TrainingData trainingData;
	
	public LabelledData(Semaphore config, Semaphore wait,long id,Map<String,Object> configuration, TrainingData trainingData, String path) throws InterruptedException, IOException
	{
		super(config,id);
		String formula = (String) getObject(configuration,"formulas");
		PredictionType predictionType=(PredictionType) getObject(configuration,"predictionType");
		PartitionMethod partitionMethod = (PartitionMethod) getObject(configuration,"partitionMethod");
		Integer numberOfIntervals = (Integer) getObject(configuration,"numberOfIntervals");
		path+="/"+id;
		for(String key: trainingData.getConfiguration().keySet())
		{
			this.configuration.put(key, trainingData.getConfiguration().get(key));
		}
		writeConfiguration(path);
		wait.acquire();
		config.release();
		
		w.start();
		classifier=null;
		this.trainingData = trainingData;
		Vector<Formula> formulas = new Vector<>();
		SimpleFormula simpleFormula = new SimpleFormula(formula);
		formulas.add(simpleFormula);				
		
		System.out.println("\t\tLabelling log with "+predictionType+" classifier...");
		switch (predictionType){
		case FORMULA_SATISFACTION:
			classifier = new SatisfactionClassifier(trainingData.getLog(),formulas);
			break;
		case FORMULA_SATISFACTION_TIME:
			classifier = new TimeClassifier(trainingData.getLog(),formulas,partitionMethod,numberOfIntervals);
			break;
		case ACTIVATION_VERIFICATION_FORMULA_TIME:
			classifier=new ActivationVerificationGapClassifier(trainingData.getLog());
			break;
		}
		System.out.println("\t\tDone");
		initTime=w.msecs();
		//writeToFile(path);
		wait.release();
		
	}
	
	public LabelledData(Semaphore config, Semaphore wait,long id , Map<String,Object> configuration, TrainingData trainingData, String path, boolean load) throws InterruptedException, FileNotFoundException
	{
		super(config, id);
		this.configuration = configuration;
		this.trainingData = trainingData;
		config.release();
		wait.acquire();
		File classifierFile = new File(path+"/"+id+"/classifier.dat");
		InputStream is = new FileInputStream(classifierFile);
		classifier = (Classifier) xstream.fromXML(is);
		wait.release();
	}
	
	public Classifier getClassifier()
	{
		return classifier;
	}
	
	public TrainingData getTrainingData()
	{
		return trainingData;
	}
	
	private void writeToFile(String path) throws IOException
	{			
		File classifierFile = new File(path+"/classifier.dat");
		PrintWriter pw = new PrintWriter(classifierFile);

	    pw.print(xstream.toXML(classifier));
	    pw.flush();
		pw.close();
	}
	
}
