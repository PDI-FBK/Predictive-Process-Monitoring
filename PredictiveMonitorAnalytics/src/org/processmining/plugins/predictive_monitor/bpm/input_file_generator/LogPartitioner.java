package org.processmining.plugins.predictive_monitor.bpm.input_file_generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.predictive_monitor.bpm.utility.XLogReader;
import org.processmining.plugins.predictive_monitor.bpm.utility.XLogWriter;

public class LogPartitioner {

	/*public static void main(String[] args) {
		String inputLogFilePath = ServerConfigurationClass.trainingInputLogFile;
		int k = ServerConfigurationClass.crossValidationParts;
		generateLogParts(inputLogFilePath, k);
	}*/

	public static void generateLogParts(String inputLogFilePath, int k) {
		try {
			XLog log = XLogReader.openLog(inputLogFilePath);
			inputLogFilePath = inputLogFilePath.split("\\.xes")[0];
			
			int logSize = log.size();
			int logPartSize = (int) Math.ceil(1.0 * logSize / k);

			List<Integer> idxs = new ArrayList<Integer>();
			for (int i = 0; i < logSize; i++) {
				idxs.add(i);
			}
			Collections.shuffle(idxs);

			List<XLog> logParts = new ArrayList<XLog>();
			for (int i = 0; i < k; i++) {
				XLog logPart = XFactoryRegistry.instance().currentDefault()
						.createLog();
				logPart.setAttributes(log.getAttributes());
				for (int j = i * logPartSize; j < (i + 1) * logPartSize
						& j < logSize; j++) {
					logPart.add(log.get(j));
				}
				logParts.add(logPart);
			}
			for (int i = 0; i < k; i++) {
				XLog trainingLog = XFactoryRegistry.instance().currentDefault()
						.createLog();
				for (int j = 0; j < k; j++) {
					if (j != i) {
						trainingLog.addAll(logParts.get(j));
					}
				}
				XLog testingLog = logParts.get(i);
				XLogWriter.saveLog(trainingLog, inputLogFilePath + "_train_part"
						+ (i+1) + ".xes");
				XLogWriter.saveLog(testingLog, inputLogFilePath + "_test_part"
						+ (i+1) + ".xes");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
