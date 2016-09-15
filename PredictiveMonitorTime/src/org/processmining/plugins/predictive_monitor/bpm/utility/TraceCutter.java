package org.processmining.plugins.predictive_monitor.bpm.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;

public class TraceCutter {
	
	public static void main(String[] args) {
		String outputFilePath = "./input/BPI2011_80_913.xes";
		String inputFilePath = "./input/BPI2011_80.xes";
		
		XLog log;
		try {
			log = XLogReader.openLog(inputFilePath);
			cutTrace(913, 126, log, outputFilePath);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void cutTrace(int maxTraceNumber, int maxEventNumber, XLog log, String outputLogPath){
		
		
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XLog newLog = factory.createLog();
		newLog.setAttributes(log.getAttributes());
		for (int i = 0; i < maxTraceNumber; i++) {
			XTrace trace = log.get(i);
			if (i==912){
				XTrace newTrace = factory.createTrace();
				newTrace.setAttributes(trace.getAttributes());
				for (int j = 0; j < maxEventNumber; j++) {
					newTrace.add(trace.get(j));
				}
				newLog.add(newTrace);
			} else
				newLog.add(trace);
		}
		saveLog(newLog, outputLogPath);
		
	}
	
	private static void saveLog(XLog log, String outputLogPath){
		File output = new File(outputLogPath);

		try {
			FileOutputStream fBOS = new FileOutputStream(output);
			XesXmlSerializer serializer = new XesXmlSerializer();
			serializer.serialize(log, fBOS);
			fBOS.flush();
			fBOS.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
