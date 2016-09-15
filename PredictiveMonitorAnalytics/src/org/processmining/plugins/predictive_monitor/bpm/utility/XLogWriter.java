package org.processmining.plugins.predictive_monitor.bpm.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.FormulaVerificator;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.Formula;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.SimpleFormula;

import weka_predictions.core.Attribute;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;

public class XLogWriter {

	public static void writeEventLog(XLog log, String outputLogFilePath){
		try {

			FileWriter fW = new FileWriter(outputLogFilePath);
			for (XTrace trace : log) {
				boolean start = true;
				for (XEvent event : trace) {
					String eventName = XConceptExtension.instance().extractName(event);
					if (!start)
						fW.write(";");
					fW.write(eventName);
					start = false;
				}
				fW.write("\n");
			}
			fW.flush();
			fW.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeEventLogAndLabel(XLog log, String formulaLTL, String outputLogFilePath){
		try {

			FileWriter fW = new FileWriter(outputLogFilePath);
			
			Formula formula = new SimpleFormula(formulaLTL);

			LogReaderAndReplayer replayer = null;
			try {
				replayer = new LogReaderAndReplayer(log);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DataSnapshotListener listener = new DataSnapshotListener(replayer.getDataTypes(), replayer.getActivityLabels());

			
			for (XTrace trace : log) {
				boolean startEv = true;
				boolean startAttr = false;
				for (XEvent event : trace) {
					String eventName = XConceptExtension.instance().extractName(event);
					if (!startEv){
						fW.write(";");
					}
					fW.write(eventName);
					startEv = false;
				}
				boolean verified = !FormulaVerificator.isTraceViolated(listener, formula, trace);
				fW.write(";"+verified);
				
				
				fW.write("\n");
			}
			fW.flush();
			fW.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	public static void writeLogFrequencies(Instances encodedTraces, String trainingFilePath) {
		try {
			FileWriter fW = new FileWriter(new File(trainingFilePath));
			
			
			Enumeration<Attribute> alphabetNames = encodedTraces.enumerateAttributes();
			boolean first = true;

			
			while (alphabetNames.hasMoreElements()){
				if (!first)
					fW.write(";");
				Attribute alphabetElement = alphabetNames.nextElement();
				fW.write(alphabetElement.name());
				first = false;
			}
			fW.write("\n");
			
			int max = encodedTraces.numAttributes();
			
			for (Instance encodedTrace : encodedTraces) {
				first = true;
				for (int i = 0; i < max; i++) {
					if (!first)
						fW.write(";");
					fW.write(encodedTrace.toString(i).replace('?', '0'));
					first = false;

				}
				fW.write("\n");
			}
		
			fW.flush();
			fW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void saveLog(XLog log, String outputLogPath){
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
