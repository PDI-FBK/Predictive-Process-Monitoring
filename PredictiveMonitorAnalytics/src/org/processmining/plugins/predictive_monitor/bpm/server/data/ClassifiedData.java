package org.processmining.plugins.predictive_monitor.bpm.server.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.classification.ArffBuilder;
import org.processmining.plugins.predictive_monitor.bpm.classification.ClassificationController;
import org.processmining.plugins.predictive_monitor.bpm.classification.enumeration.ClassificationType;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.PatternController;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.enumeration.PatternType;

import weka_predictions.classifiers.trees.RandomForest_predictions;
import weka_predictions.classifiers.trees.j48.J48_predictions;
import weka_predictions.core.Instances;
import weka_predictions.data_predictions.Result;

public class ClassifiedData extends GenericDataStructure {
	
	private HashMap<Integer, ClassificationController> classificationControllers = null;
	private HashMap<Integer,J48_predictions> trees = null;
	private HashMap<Integer,RandomForest_predictions> rForests = null;
	private HashMap<Integer, Instances> instancesPathMap = null;
	private HashMap<Integer, HashMap<String, ArrayList<String>> > attributeTypeMap = null;
	private Map<Integer, ArrayList<Pattern>> patternsMap;
	private ClassificationType classificationType;
	private PatternType classificationPatternType;
	private ClusteredData clusteredData;
	private LabelledData labelledData;
	
	public ClassifiedData(Semaphore config, Semaphore wait,long id, Map<String,Object> configuration, ClusteredData clusteredData, LabelledData labelledData, String path) throws InterruptedException, FileNotFoundException
	{
		super(config,id);
		classificationType = (ClassificationType)getObject(configuration,"classificationType");
		classificationPatternType = (PatternType) getObject(configuration,"classificationPatternType");
		Integer rFMaxDepth = (Integer) getObject(configuration,"rFMaxDepth");
		Integer rFNumTrees = (Integer) getObject(configuration,"rFNumTrees");
		Integer rFSeed = (Integer) getObject(configuration,"rFSeed");
		Integer rFNumFeatures = (Integer) getObject(configuration,"rFNumFeatures");
		Double patternMinimumSupport = (Double) getObject(configuration,"classificationPatternMinimumSupport");
		Integer minimumPatternLength = (Integer) getObject(configuration,"classificationMinimumPatternLength");
		Integer maximumPatternLength = (Integer) getObject(configuration,"classificationMaximumPatternLength");
		Double discriminativeMinimumSupport = (Double) getObject(configuration,"classificationDiscriminativeMinimumSupport");
		Integer discriminativePatternCount = (Integer) getObject(configuration,"classificationDiscriminativePatternCount");
		Integer sameLengthDiscriminativePatternCount = (Integer) getObject(configuration,"classificationSameLengthDiscriminativePatternCount");
		this.clusteredData = clusteredData;
		this.labelledData = labelledData;
		path+="/"+id;
		for(String key: labelledData.getConfiguration().keySet())
		{
			this.configuration.put(key, labelledData.getConfiguration().get(key));
		}
		for(String key: clusteredData.getConfiguration().keySet())
		{
			this.configuration.put(key, clusteredData.getConfiguration().get(key));
		}
		//writeConfiguration(path);
		wait.acquire();
		config.release();
		
		w.start();
	
		classificationControllers = new HashMap<Integer, ClassificationController>();
		int clusterNumber = 1;
		instancesPathMap = new HashMap<Integer, Instances>();
		attributeTypeMap = new HashMap<Integer, HashMap<String, ArrayList<String>>>();
		patternsMap = new HashMap<Integer, ArrayList<Pattern>>();
		trees = new HashMap<Integer, J48_predictions>();
		rForests = new HashMap<Integer, RandomForest_predictions>();
		System.out.println("\t\tClassifying clusters...");
		for(XLog l : clusteredData.getClusterLogs()){
			System.out.println("\t\t\tCluster"+clusterNumber);
			//System.out.println(xstream.toXML(l));
			ClassificationController classificationController = new ClassificationController();
			ArrayList<Pattern> patterns = new ArrayList<Pattern>();
			if(classificationPatternType!=PatternType.NONE)
			{
				System.out.println("\t\t\t\tCreating patterns...");
				switch (classificationPatternType) {
				case DISCRIMINATIVE:
					patterns = PatternController.generateDiscriminativePatterns(l, labelledData.getClassifier().getClassification(), clusterNumber, discriminativePatternCount, sameLengthDiscriminativePatternCount);
					break;
				case SEQUENTIAL_WITHOUT_HOLES:
					patterns = PatternController.generateSequentialPatternsWithoutHoles(l, clusterNumber, clusterNumber);
					break;
				case SEQUENTIAL_WITH_HOLES:
					patterns = PatternController.generateSequentialPatternsWithHoles(l, clusterNumber, clusterNumber, clusterNumber);
					break;
				case DISCR_SEQUENTIAL_WITHOUT_HOLES:
					patterns = PatternController.generateDiscriminativeSequentialPatternsWithoutHoles(l, patternMinimumSupport, minimumPatternLength, labelledData.getClassifier().getClassification(), discriminativeMinimumSupport);
					break;
				case DISCR_SEQUENTIAL_WITH_HOLES:
					patterns = PatternController.generateDiscriminativeSequentialPatternsWithHoles(l, patternMinimumSupport, minimumPatternLength, maximumPatternLength, labelledData.getClassifier().getClassification(), discriminativeMinimumSupport);
					break;
				default:
					break;
				}
				System.out.println("\t\t\t\tDone");
			}
			
			patternsMap.put(clusterNumber, patterns);

			System.out.println("\t\t\t\tGenerating classificationStructure...");
			switch (classificationType) {
			case DECISION_TREE:
				J48_predictions tree = classificationController.trainDecisionTree(l, labelledData.getClassifier(), patterns, classificationPatternType);
				trees.put(clusterNumber, tree);
				break;
			case RANDOM_FOREST:
				RandomForest_predictions rf = classificationController.trainRandomForest(l, labelledData.getClassifier(), patterns, classificationPatternType, rFMaxDepth, rFNumFeatures, rFNumTrees, rFSeed);
				rForests.put(clusterNumber, rf);
				break;
			default:
				break;
			}
			System.out.println("\t\t\t\tDone");
			
			HashMap<String, ArrayList<String>> attributeTypes = ArffBuilder.getAttributeTypes();
			instancesPathMap.put(clusterNumber, classificationController.getInstances());
			attributeTypeMap.put(clusterNumber, attributeTypes);
			classificationControllers.put(clusterNumber, classificationController);
			System.out.println("\t\t\tDone");
			/*if(generateLog)
			{
				File arff;
				arff = new File(outDir+"/cluster_"+clusterNumber+".arff");
	
				ArffSaver arffPrinter = new ArffSaver();
				arffPrinter.setInstances(classificationController.getInstances());
				try {
					arffPrinter.setFile(arff);
					arffPrinter.writeBatch();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}*/
			
			clusterNumber++;
		}
		System.out.println("\t\tDone");
		//writeToFile(path);
		initTime = w.msecs();
		wait.release();
	}

	public Map<String, Result> computeSuggestions(int cl,XTrace lastTrace,HashMap <String, String> variables, Vector<String> currentVariables)
	{
		ArrayList<Pattern> patterns = patternsMap.get(cl);
		switch(classificationType) {
		case DECISION_TREE:
			J48_predictions tree = trees.get(new Integer(cl));
			switch (classificationPatternType) {
			case DISCRIMINATIVE:
			case SEQUENTIAL_WITHOUT_HOLES:
			case SEQUENTIAL_WITH_HOLES:
			case DISCR_SEQUENTIAL_WITHOUT_HOLES:
			case DISCR_SEQUENTIAL_WITH_HOLES:
				return classificationControllers.get(cl).classifyTraceDecisionTree(lastTrace, patterns, variables, currentVariables, tree);
			case NONE:
			default:
				return classificationControllers.get(cl).classifyTraceDecisionTree(lastTrace, variables, currentVariables, tree);
			}
		case RANDOM_FOREST:
			RandomForest_predictions randomForest = rForests.get(new Integer(cl));
			Instances instances = instancesPathMap.get(cl);
			switch (classificationPatternType) {
			case DISCRIMINATIVE:
			case SEQUENTIAL_WITHOUT_HOLES:
			case SEQUENTIAL_WITH_HOLES:
			case DISCR_SEQUENTIAL_WITHOUT_HOLES:
			case DISCR_SEQUENTIAL_WITH_HOLES:
				return classificationControllers.get(cl).classifyTraceRandomForest(lastTrace, patterns, variables, attributeTypeMap.get(cl), randomForest, instances);
			case NONE:
			default:
				return classificationControllers.get(cl).classifyTraceRandomForest(lastTrace, patterns, variables, attributeTypeMap.get(cl), randomForest, instances);
			}
		}
		return null;
	}
	
	public HashMap<Integer, ClassificationController> getClassificationControllers()
	{
		return classificationControllers;
	}
	
	public LabelledData getLabelledData()
	{
		return labelledData;
	}
	
	public ClusteredData getClusteredData()
	{
		return clusteredData;
	}
	
	private void writeToFile(String path) throws FileNotFoundException {
		path+="/classificationControllers";
		
		File classificationControllersFile = new File(path+".dat");
		PrintWriter pw = new PrintWriter(classificationControllersFile);

	    pw.print(xstream.toXML(classificationControllers));
	    pw.flush();
		pw.close();
	
		
	}
}
