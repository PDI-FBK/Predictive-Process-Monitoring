package org.processmining.plugins.predictive_monitor.bpm.client.experimentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.processmining.operationalsupport.client.InvocationException;
import org.processmining.operationalsupport.client.SessionClosedException;
import org.processmining.operationalsupport.messages.reply.ResponseSet;
import org.processmining.plugins.predictive_monitor.bpm.classification.enumeration.ClassificationType;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ClusteringType;
import org.processmining.plugins.predictive_monitor.bpm.configuration.ClientConfigurationClass;
import org.processmining.plugins.predictive_monitor.bpm.configuration.ServerConfigurationClass;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.Connection;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.GetLocalTrainingFiles;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.enumeration.PatternType;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.PredictionResult;
import org.processmining.plugins.predictive_monitor.bpm.replayer.ConfigurationSender;
import org.processmining.plugins.predictive_monitor.bpm.replayer.GenericResult;
import org.processmining.plugins.predictive_monitor.bpm.replayer.GlobalResultListener;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier.PredictionType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.SimpleFormula;

public class TestingMain {
	public static void main(String[] args) throws FileNotFoundException {
		
		String formulas[]=new String[ClientConfigurationClass.activeFormulas.length];
		PatternType[] clusteringPatternType = ClientConfigurationClass.clusteringPatternType;
		ClassificationType[] classificationType = ClientConfigurationClass.classificationType;
		double[] minConfidences = ClientConfigurationClass.minConfidences;
		int[] minSupports = ClientConfigurationClass.minSupports;
		double[] dbScanEpsilon;
		int[] dbScanMinPoints;
		int[] numberOfCl;
		Vector<Formula> formulaVector = new Vector<Formula>();
		
		String testingInputLogFile = "Datasets/BPIC15_4/BPIC15_4_20.xes";//ClientConfigurationClass.testingInputLogFile;
		int run=-1;
		try (BufferedReader br = new BufferedReader(new FileReader("./output/config/runNumber.txt")))
		{
			run=Integer.parseInt(br.readLine());
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("./output/config/runNumber.txt")))
		{
			bw.write(""+(run+1));
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		String runId = "Run"+run;
		
		int i=0;
		for(int activeFormula:ClientConfigurationClass.activeFormulas)
		{
			System.out.println(ClientConfigurationClass.phi[ClientConfigurationClass.activeFormulas[i]]);
			formulas[i]=ClientConfigurationClass.phi[ClientConfigurationClass.activeFormulas[i]];
			i++;
		}

		
	
		dbScanEpsilon = ClientConfigurationClass.dbScanEpsilon;
		dbScanMinPoints = ClientConfigurationClass.dbScanMinPoints;
		numberOfCl=new int[]{0};
		
		
		dbScanEpsilon=new double[]{0};
		dbScanMinPoints = new int[]{0};
		numberOfCl = ClientConfigurationClass.numberOfCl();
		int k=0;
		Set<String> runIds = new HashSet<>();
		Map<String,Map<String,Object>> configurations = new HashMap<>();
		Map<String,PrintWriter> pw = new HashMap<>();
		
		
		Map<String,PredictionType> predictionTypes = new HashMap<>();
		predictionTypes.put("Satisfaction",PredictionType.FORMULA_SATISFACTION);
		predictionTypes.put("Time",PredictionType.FORMULA_SATISFACTION_TIME);
		
		for(int activeFormula : ClientConfigurationClass.activeFormulas)
		{
			for(double confidence : ClientConfigurationClass.minConfidences)
			{
				for(ClassificationType type : ClientConfigurationClass.classificationType)
				{
					String currentRunId = runId+"_conf"+confidence+"_formula"+activeFormula+"_type"+(type==ClassificationType.DECISION_TREE ? "DT" : "RF");
					runIds.add(currentRunId);
					k++;
					Map<String,Object> configuration = new HashMap<String, Object>();
					
					configuration.put("formulas", ClientConfigurationClass.phi[activeFormula]);
					configuration.put("classificationType", type);
					configuration.put("clusteringType", ClientConfigurationClass.clusteringType);
					
					configuration.put("dbScanEpsilon", ClientConfigurationClass.dbScanEpsilon[0]+0.12);
					configuration.put("dbScanMinPoints", ClientConfigurationClass.dbScanMinPoints[0]);
					
					configuration.put("clusterNumber", new Integer(numberOfCl[0]));
					
					configuration.put("clusteringPatternType", clusteringPatternType[0]); 
					configuration.put("minConfidence", confidence);
					configuration.put("minSupport", minSupports[0]);
					configuration.put("testingInputLogFile","input/random/BPI2011_20.xes");
					configuration.put("evaluationGap", ClientConfigurationClass.evaluationPointGap);
					configuration.put("evaluationStartPoint", 0);
					configuration.put("predictionType",predictionTypes);
					configuration.put("classificationPatternType",ClientConfigurationClass.classificationPatternType);
					configuration.put("runSetId",runId);
					configuration.put("runId", currentRunId);
					configuration.put("trainingFile", "random/BPI2011_20.xes");
					configuration.put("partionMethod", ServerConfigurationClass.partitionMethod);
					configuration.put("numberOfIntervals",ServerConfigurationClass.numberOfIntervals);
					configuration.put("minPrefixLength", ServerConfigurationClass.minPrefixLength);
					configuration.put("maxPrefixLength", ServerConfigurationClass.maxPrefixLength);
					configuration.put("prefixGap", ServerConfigurationClass.prefixGap);
					configuration.put("clusteringDiscriminativePatternMinimumSupport", ServerConfigurationClass.discriminativePatternMinimumSupport);
					configuration.put("clusteringDiscriminativePatternCount", ServerConfigurationClass.discriminativePatternCount);
					configuration.put("clusteringSameLengthDiscriminativePatternCount", ServerConfigurationClass.sameLengthDiscriminativePatternCount);
					configuration.put("clusteringPatternMinimumSupport", ServerConfigurationClass.patternMinimumSupport);
					configuration.put("clusteringMinimumPatternLength", ServerConfigurationClass.minimumPatternLength);
					configuration.put("clusteringMaximumPatternLength", ServerConfigurationClass.maximumPatternLength);
					configuration.put("clusteringDiscriminativeMinimumSupport", ServerConfigurationClass.discriminativeMinimumSupport);
					configuration.put("modelClusteringFrom", ServerConfigurationClass.modelClusteringFrom);
					configuration.put("discriminativePatternFilePath", ServerConfigurationClass.discriminativePatternFilePath);
					configuration.put("classificationDiscriminativePatternCount", ServerConfigurationClass.discriminativePatternCount);
					configuration.put("classificationSameLengthDiscriminativePatternCount", ServerConfigurationClass.sameLengthDiscriminativePatternCount);
					configuration.put("classificationPatternMinimumSupport", ServerConfigurationClass.patternMinimumSupport);
					configuration.put("classificationMinimumPatternLength", ServerConfigurationClass.minimumPatternLength);
					configuration.put("classificationMaximumPatternLength", ServerConfigurationClass.maximumPatternLength);
					configuration.put("classificationDiscriminativeMinimumSupport", ServerConfigurationClass.discriminativeMinimumSupport);
					configuration.put("confidenceAndSupportVotingStrategy", ServerConfigurationClass.confidenceAndSupportVotingStrategy);	
					configuration.put("generateLog",true);
					configuration.put("rFMaxDepth", ServerConfigurationClass.rFMaxDepth);
					configuration.put("rFNumTrees", ServerConfigurationClass.rFNumTrees);
					configuration.put("rFSeed", ServerConfigurationClass.rFSeed);
					configuration.put("rFNumFeatures",ServerConfigurationClass.rFNumFeatures);
					configuration.put("partitionMethod", ServerConfigurationClass.partitionMethod);
					configuration.put("numberOfIntervals",ServerConfigurationClass.numberOfIntervals);
					configuration.put("evaluationRun", false);
					configuration.put("hierarchicalDistanceMetrics", ServerConfigurationClass.hierarchicalDistanceMetrics);
					configuration.put("useVotingForClustering", ServerConfigurationClass.useVotingForClustering);
					configuration.put("voters", ServerConfigurationClass.voters);
					
					configurations.put(currentRunId,configuration);
					pw.put(currentRunId, new PrintWriter(new File("output/testRuns/"+currentRunId)));
					for(String key: configuration.keySet())
					{
						pw.get(currentRunId).println("\""+key+"\",\""+configuration.get(key)+"\",");
					}
					pw.get(currentRunId).flush();
				}
			}
		}
		
		try {
			ResponseSet<Object> result = Connection.createSession().simple(new GetLocalTrainingFiles(), XFactoryRegistry.instance().currentDefault().createLog());
			for (String provider : result) {
				for (Object r : result.getResponses(provider)) {
					System.out.println((List<String>)r);
				}
			}
		} catch (IOException | InvocationException | SessionClosedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		GlobalResultListener globalResultListener = new GlobalResultListener(runIds,true);
		ConfigurationSender configurationSender = new ConfigurationSender();
		
		for(String id : runIds)
		{
			globalResultListener.getResultListener(id).getResults().addListener(new ListChangeListener<GenericResult>() {
			
				@Override
				public void onChanged(javafx.collections.ListChangeListener.Change<? extends GenericResult> arg0) {
					if(arg0.getList().size()==1)
					{
						for(String s :  arg0.getList().get(0).getRow().keySet()){
							pw.get(id).print("\""+s+"\",");
						}
						pw.get(id).println();
						pw.get(id).flush();
					}
					for(String s : arg0.getList().get(arg0.getList().size()-1).getRow().values())				
					{
						pw.get(id).print("\""+s+"\",");
					}
					pw.get(id).println();
					pw.get(id).flush();
					//arg0.getList().get(arg0.getList().size()-1).removeRow();
				}
			});
			
			globalResultListener.getResultListener(id).getProgress().addListener(new ChangeListener<Number>() {
				
				@Override
				public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
					System.out.println("Progress:"+(int)(arg0.getValue().doubleValue()*100)+"%");
					if(1.0-arg0.getValue().doubleValue()<=0.000001)
					{
						pw.get(id).println("\"Accuracy:\",\""+globalResultListener.getResultListener(id).getAccuracy().get()+"\"");
						pw.get(id).println("\"FailureRate:\",\""+globalResultListener.getResultListener(id).getFailureRate().get()+"\"");
						pw.get(id).println("\"Earliness:\",\""+globalResultListener.getResultListener(id).getEarliness().get()+"\"");
						pw.get(id).println("\"EvaluatedTraceCount:\",\""+globalResultListener.getResultListener(id).getEvaluatedTraceCount().get()+"\"");
						pw.get(id).flush();
						pw.get(id).close();
					}
				}
			});
		}
		
		/*globalResultListener.getResultListener(runId).getProgress().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {
				System.out.println("Progress:"+(int)(arg0.getValue().doubleValue()*100)+"%");
				
			}
		});*/
		
		
		
		
		
		/*
		for(String k: configurations.keySet())
		{
			System.out.println();
			System.out.println(k);
			for(String j: configurations.get(k).keySet())
			{
				System.out.println(j+": "+configurations.get(k).get(j));
			}
		}*/
		
		
		configurationSender.addRun(configurations,globalResultListener);
	}
}
