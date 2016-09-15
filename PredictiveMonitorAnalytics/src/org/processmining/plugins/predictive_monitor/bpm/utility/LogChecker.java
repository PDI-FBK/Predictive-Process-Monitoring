package org.processmining.plugins.predictive_monitor.bpm.utility;

import java.io.BufferedReader;
import java.io.FileReader;

import org.deckfour.xes.model.XLog;

public class LogChecker {
	
	public static boolean checkLogEquivalenceWithLogFile(XLog log, String inputLogFilePath){
		boolean correct = false; 
		try {
			XLog inputLog = XLogReader.openLog(inputLogFilePath);
			if (inputLog.size()!=log.size())
				return correct;
			for (int i = 0; i < inputLog.size(); i++) {
				String stringExistingTrace = TraceConverter.getTraceAsString(log.get(i));
				String stringInputTrace = TraceConverter.getTraceAsString(inputLog.get(i));
				if (!stringExistingTrace.equals(stringInputTrace))
					return correct;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public static boolean checkLogEquivalenceWithTxtLog(XLog log, String inputLogFilePath){
		boolean correct = false; 
		try {
			BufferedReader bR = new BufferedReader(new FileReader(inputLogFilePath));
			String line;
			int i=0;
			while ((line=bR.readLine())!=null){
				if (i>log.size())
					return correct;
				String stringExistingTrace = TraceConverter.getTraceAsString(log.get(i));
				if (!stringExistingTrace.equals(line))
					return correct;
				i++;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	

}
