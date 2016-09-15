package org.processmining.plugins.predictive_monitor.bpm.server.data;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.declareminer.Watch;
import org.processmining.plugins.predictive_monitor.bpm.PredictiveMonitor;
import org.processmining.plugins.predictive_monitor.bpm.utility.TracePrefixGenerator;


public class TrainingData extends GenericDataStructure{
	private XLog log;
	private String hash;
	private XLog prefixLog;
	
	public TrainingData(Semaphore config, Semaphore wait, long id, Map<String,Object> configuration) throws InterruptedException
	{	
		super(config,id);
		String inputFilePath=(String)PredictiveMonitor.rootTrainingFilesFolder+"/"+getObject(configuration,"trainingFile");
		Integer minPrefixLength = (Integer) getObject(configuration,"minPrefixLength");
		Integer maxPrefixLength = (Integer) getObject(configuration,"maxPrefixLength");
		Integer prefixGap = (Integer) getObject(configuration,"prefixGap");
		wait.acquire();
		config.release();
		
		
		w.start();
		System.out.println("\t\tReading file "+ inputFilePath+"...");
		XesXmlParser parser = new XesXmlParser();
		try {
			File f = new File(inputFilePath);
			log = parser.parse(f).get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\t\tDone");
		
		System.out.println("\t\tGenerating prefix Log...");
		hash = log.size()+"";
		prefixLog = TracePrefixGenerator.generatePrefixesFromLog(log,minPrefixLength,maxPrefixLength,prefixGap);
		System.out.println("\t\tDone");
		initTime=w.msecs();
		wait.release();
	}
	
	public XLog getLog()
	{
		return log;
	}
	
	public XLog getPrefixLog()
	{
		return prefixLog;
	}
	
	public String getHash()
	{
		return hash;
	}

}
