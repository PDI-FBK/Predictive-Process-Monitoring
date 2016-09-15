package org.processmining.plugins.predictive_monitor.bpm.client.experimentation;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.util.XRegistry;
import org.processmining.operationalsupport.client.InvocationException;
import org.processmining.operationalsupport.client.SessionClosedException;
import org.processmining.operationalsupport.client.SessionHandle;
import org.processmining.operationalsupport.messages.reply.ResponseSet;
import org.processmining.plugins.predictive_monitor.bpm.classification.enumeration.ClassificationType;
import org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification.SatisfactionTimeTraceClassifier;
import org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification.SatisfactionTraceClassifier;
import org.processmining.plugins.predictive_monitor.bpm.classification.trace_classification.TraceClassifier;
import org.processmining.plugins.predictive_monitor.bpm.configuration.ClientConfigurationClass;
import org.processmining.plugins.predictive_monitor.bpm.configuration.ServerConfigurationClass;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.Connection;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.ConfigurationOperation;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.EvaluateEventOperation;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.operation.GetLocalTrainingFiles;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.enumeration.PatternType;
import org.processmining.plugins.predictive_monitor.bpm.prediction.result.PredictionResult;
import org.processmining.plugins.predictive_monitor.bpm.replayer.ConfigurationSender;
import org.processmining.plugins.predictive_monitor.bpm.replayer.GenericResult;
import org.processmining.plugins.predictive_monitor.bpm.replayer.GlobalResultListener;
import org.processmining.plugins.predictive_monitor.bpm.replayer.SingleRun;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier.PredictionType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.SimpleFormula;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class EventFlow {
	public static void main(String[] args) throws IOException, InvocationException, SessionClosedException, NumberFormatException, InterruptedException {

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
		//predictionTypes.put("Time",PredictionType.FORMULA_SATISFACTION_TIME);
		
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
		
		String currentRunId = configurations.keySet().iterator().next();
		Map<String,Object> configuration = configurations.get(currentRunId);
		SessionHandle handle = Connection.createSession();	
		handle.simple(new ConfigurationOperation(configuration, currentRunId),XFactoryRegistry.instance().currentDefault().createLog());
		
		XLog log=null;
		String inputFile = "input/random/BPI2011_20.xes";
		if(inputFile.toLowerCase().contains("xes")){
			XesXmlParser parser = new XesXmlParser();
			if(parser.canParse(new File(inputFile))){
				try {
					log = parser.parse(new File(inputFile)).get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
		Map<String,Queue<XEvent>> eventPool = new HashMap<>();
		
		for(XTrace trace:log)
		{
			Queue<XEvent> eventList = new ConcurrentLinkedQueue<XEvent>();
			for(XEvent event:trace)
			{
				eventList.add(event);
			}
			eventPool.put(XConceptExtension.instance().extractName(trace),eventList);
		}
		
		while(eventPool.size()>0)
		{
			int nEvent= (int) Math.floor(Math.random()*eventPool.size());
			Iterator<String> iterator = eventPool.keySet().iterator();
			for(int v = 0 ;v<nEvent;v++) iterator.next();
			String e = iterator.next();

			
			XEvent event = eventPool.get(e).poll();
			if(event!=null)
			{
				System.out.println("\nSending event \""+XConceptExtension.instance().extractName(event)+"\" of trace "+e);
				ResponseSet<Object> result = handle.simple(new EvaluateEventOperation(event,e,currentRunId),XFactoryRegistry.instance().currentDefault().createLog());
				PredictionResult predictionResult = null;
				for (String provider : result) {
					for (Object r : result.getResponses(provider)) {
						predictionResult = (PredictionResult)r;
					}
				}
				
				System.out.print("\t\tresult: ");
				for(String s: predictionResult.getResults().keySet())
				{
					System.out.print(s+": "+predictionResult.getResults().get(s).getLabel());
				}
				System.out.print("\n\t\tconfidence: "+predictionResult.getConfidence());
				System.out.print("\n\t\tsupport: "+predictionResult.getSupport());
				System.out.print("\n\t\ttrace length: "+predictionResult.getIndex());
				System.out.println("\n\t\ttime: "+predictionResult.getTime());
				
				//Thread.sleep((long) (1000*Math.random())+500);
			}
			else
			{
				eventPool.remove(e);
			}
		}
		
		
		
		
		
	}
}
