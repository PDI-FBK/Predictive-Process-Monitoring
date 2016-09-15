package org.processmining.plugins.predictive_monitor.bpm.configuration.modules;

import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values.GenerateArffReport;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values.PrintDebug;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values.PrintLog;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.directory_path_values.LogFilePath;

public class LogOption implements Module{
	
	private LogFilePath logFilePath;
	private GenerateArffReport generateArffReport;
	private PrintDebug printDebug;
	private PrintLog printlog;
	
	public LogOption() {
		logFilePath = new LogFilePath();
		generateArffReport = new GenerateArffReport();
		printDebug = new PrintDebug();
		printlog = new PrintLog();
	}

	@Override
	public String getModuleName() {
		return "Log Option";
	}

	@Override
	public List<Parameter> getParameterList() {
		List <Parameter> retval = new ArrayList<Parameter>();
		retval.add(logFilePath);
		retval.add(generateArffReport);
		retval.add(printDebug);
		retval.add(printlog);
		return retval;
	}

	public final LogFilePath getLogFilePath() {
		return logFilePath;
	}

	public final void setLogFilePath(LogFilePath logFilePath) {
		this.logFilePath = logFilePath;
	}

	public final GenerateArffReport getGenerateArffReport() {
		return generateArffReport;
	}

	public final void setGenerateArffReport(GenerateArffReport generateArffReport) {
		this.generateArffReport = generateArffReport;
	}

	public final PrintDebug getPrintDebug() {
		return printDebug;
	}

	public final void setPrintDebug(PrintDebug printDebug) {
		this.printDebug = printDebug;
	}

	public final PrintLog getPrintlog() {
		return printlog;
	}

	public final void setPrintlog(PrintLog printlog) {
		this.printlog = printlog;
	}
	
}

	/*

	//LogConfiguration
		public final static String logFilePath = "./log/log.txt";
		public final static String logPart = "";
/*
		// Log files
		public final static String trainingInputLogFile = logPart.equals("") ? "./input/BPI2011_80.xes" : "./input/BPI2011_80_train" + logPart + ".xes";
		public final static String prefixInputEventLogFile = "./input/prefix_gap" + prefixLength+ "_max" + maxPrefixLength + "_BPI2011_80" + logPart + ".txt";
*/
/*
		public final static boolean generateArffReport = false;
		public final static boolean printDebug = false;
		public final static boolean printLog = false;
		
		// Log files
		public final static String testingInputLogFile = (logPart.equals("")) ? "./input/BPI2011_20.xes" : "./input/BPI2011_80_test" + logPart + ".xes";

*/
