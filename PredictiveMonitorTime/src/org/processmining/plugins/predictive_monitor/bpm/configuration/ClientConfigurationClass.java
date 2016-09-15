package org.processmining.plugins.predictive_monitor.bpm.configuration;

import org.processmining.plugins.declareminer.enumtypes.DeclareTemplate;
import org.processmining.plugins.predictive_monitor.bpm.classification.enumeration.ClassificationType;
import org.processmining.plugins.predictive_monitor.bpm.clustering.enumeration.ClusteringType;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.enumeration.PatternType;
import org.processmining.plugins.predictive_monitor.bpm.prediction.PredictionGenerator.CheckType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier.PredictionType;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.SimpleDeclareFormula;
import org.processmining.plugins.predictive_monitor.bpm.utility.OutputFilePrinter.OutputFileType;


public class ClientConfigurationClass {

	// Log files
	public final static String dataSet="old/";
	public final static String testingInputLogFile =  "./input/"+dataSet+"BPI2011_20.xes";
	
	//Replayer Configurations
	public final static int evaluationPointGap = 5;
	
	public final static boolean previsionForTesting = true; //set to false to remove expectations in output
	public final static boolean generateRealTimeReport = true; //set to true to create real time report
	public final static boolean generateFinalReport = true;

	public final static OutputFileType outputFileType = OutputFileType.CSV_FILE;
	public final static boolean printErrorMatrix = false;

	//Formula Satisfaction and Formula Satisfaction Time LTL formulas
	public final static String[] phi = new String[]{
	"(  <>(\"tumor marker CA-19.9\") ) \\/ ( <> (\"ca-125 using meia\") )  ",
	"([](    ((\"CEA - tumor marker using meia\") -> ( <>(\"squamous cell carcinoma using eia\")))))",
	"(  (! (\"histological examination - biopsies nno\")) U (\"squamous cell carcinoma using eia\"))",
	"   ( <> (\"histological examination - big resectiep\") )   ",
	"(<> (\"01_HOOFD_010\") ) /\\ ( <> (\"01_HOOFD_193\") )",
	"( <>(\"08_AWB45_030\") ) \\/ ( <> (\"01_HOOFD_493\") )",
	"[]( ( (\"01_HOOFD_020\") -> ( <>(\"08_AWB45_020_1\")) ) )"};
	
	//ActivationVerificationGap Traces Declare formulas

	//public final static SimpleDeclareFormula activationFormulas = new SimpleDeclareFormula ("squamous cell carcinoma using eia", "histological examination - biopsies nno", DeclareTemplate.Precedence);
	public final static SimpleDeclareFormula activationFormulas = new SimpleDeclareFormula ("administratief tarief - eerste pol", "aanname laboratoriumonderzoek", DeclareTemplate.Responded_Existence);
	//public final static SimpleDeclareFormula activationFormulas = new SimpleDeclareFormula ("aanname laboratoriumonderzoek", "ordertarief", DeclareTemplate.Response);
	//public final static SimpleDeclareFormula activationFormulas = new SimpleDeclareFormula ("vervolgconsult poliklinisch", "administratief tarief - eerste pol", DeclareTemplate.Precedence);
	
	public final static int[] activeFormulas = new int[]{0};

	public final static PatternType[] clusteringPatternType = new PatternType[]{PatternType.NONE};
	public final static double[] dbScanEpsilon = new double[]{0.17}; //DBScan required
	public final static int[] dbScanMinPoints = new int[]{5}; //DBScan required
	public final static int[] numberOfCl(){ //not DBScan required
		int[] numberOfCl = new int[31];
		for (int i = 0; i < 5; i++) {
			numberOfCl[i] = 10+5*i;
		}
		return numberOfCl;
	}
	public final static ClassificationType[] classificationType = new ClassificationType[]{ClassificationType.DECISION_TREE};
	public final static int numberOfRuns = 1;
	
	//Confidence and support thresholds
	public final static double[] minConfidences = new double[]{0.6};
	public final static int[] minSupports = new int[]{6};

	public final static boolean shiftToCurrentEvent = true;
	public final static CheckType  checkType =  CheckType.EQUAL;
	
	public final static boolean jumpTofirstNewDate = false;
	public final static PredictionType predictionType = PredictionType.FORMULA_SATISFACTION;
	
	public final static ClusteringType clusteringType = ClusteringType.DBSCAN; //DBSCAN - KMEANS -> (EM)
	public final static PatternType classificationPatternType = PatternType.NONE; 

}
