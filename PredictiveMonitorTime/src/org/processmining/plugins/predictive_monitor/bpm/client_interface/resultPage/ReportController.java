package org.processmining.plugins.predictive_monitor.bpm.client_interface.resultPage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.processmining.plugins.predictive_monitor.bpm.replayer.EvaluationResult;
import org.processmining.plugins.predictive_monitor.bpm.replayer.GenericResult;
import org.processmining.plugins.predictive_monitor.bpm.replayer.GlobalResultListener;
import org.processmining.plugins.predictive_monitor.bpm.replayer.PredictionResult;
import org.processmining.plugins.predictive_monitor.bpm.client_interface.GUI;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Dependencies;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ReportController<S> implements Initializable {
	@FXML
	private TabPane resultsTabPane;
	@FXML
	private TabPane configPane;
	@FXML
	private TableView<GlobalStubResult> runConfigs;
	@FXML
	public Tab runsSummary;
	@FXML
	public Button printOnCSV;
	
	List<String> summaryColumns;
	TableView<RunsSummaryValues> summarytableView;
	Map<String,ObservableList<ConfigurationPair>> configurationContent;
	
	Map<String,Map<String,Object>> unfoldedValues;
	GlobalResultListener globalResultListener;
	
	
	
	/**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
    	
    	unfoldedValues = GUI.unfoldedValues;
    	globalResultListener = GUI.globalResultListener;
		//((Node)(e.getSource())).getScene().getWindow().setOnCloseRequest(e1 -> System.exit(0));
    	    	
    	//Tab clustering = new Tab("Clustering", new Text("clusteringType = DBSCAN \nhierarchicalDistanceMetrics = EDIT_DISTANCE	\nagglomerativeFeatureType = WHOLETRACE	\nmodelClusteringFrom = EXTERNAL_INPUT_FILE	\nclusterNumber = 15	\nDBscanEpsilon = 0.1	\nDBScanMinPoints = 8	\nmaxSizeTraceThreshold = 100"));
    	Tab clustering = new Tab("Clustering");
    	Tab classification = new Tab("Classification");
    			//	\nrFMaxDepth = 0 \nrFNumFeatures = 0	\nrFNumTrees = 10	\nrFSeed = 1;"));
    	//Tab discriminativePatternMining = new Tab("Discriminative Pattern Mining");
    	//\nvoters = 	\nconfidenceAndSupportVotingStrategy = MAX"));
    	//Tab predictionType = new Tab("Prediction Type", new Text("predictionType = ACTIVATION_VERIFICATION_FORMULA_TIME"));
    	Tab trainingTraces = new Tab("Training Traces");
    	//Tab logOption = new Tab("Log Option");
    	Tab predictionTypeTab = new Tab("Prediction Type");
    	//Tab patternMining = new Tab("Pattern Mining");
    	Tab evaluation = new Tab("Evaluation");

        configPane.getTabs().add(clustering);
        configPane.getTabs().add(classification);
        //configPane.getTabs().add(discriminativePatternMining);
        //configPane.getTabs().add(predictionType);
        configPane.getTabs().add(trainingTraces);
        //configPane.getTabs().add(logOption);
        configPane.getTabs().add(predictionTypeTab);
        //configPane.getTabs().add(patternMining);
        configPane.getTabs().add (evaluation);
    	
        summarytableView = new TableView<RunsSummaryValues>();
        summarytableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        
		summaryColumns = new ArrayList();
        runsSummary.setContent(summarytableView);
		ObservableList<RunsSummaryValues> summaryResultTable = FXCollections.observableArrayList();
		summarytableView.setItems(summaryResultTable);
		
    	TableColumn<RunsSummaryValues, String> runIdResult = new TableColumn<RunsSummaryValues, String>();
    	runIdResult.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("runId"));  
    	summarytableView.getColumns().add(runIdResult);
    	
    	Label runIdLabel = new Label("runId");  
    	summaryColumns.add("runId");
    	runIdLabel.setTooltip(new Tooltip("Name of the run inserted by user"));    	
    	runIdResult.setGraphic(runIdLabel);
    	
    	
    	TableColumn<RunsSummaryValues, String> notPredicted = new TableColumn<RunsSummaryValues, String>();
    	notPredicted.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("notPredicted"));  
    	summarytableView.getColumns().add(notPredicted);
    	
    	Label notPredictedLabel = new Label("notPredicted");
    	summaryColumns.add("notPredicted");
    	notPredictedLabel.setTooltip(new Tooltip("Number of not predicted traces"));    	
    	notPredicted.setGraphic(notPredictedLabel);
    	
    	/*This boolean lets one choose to show or not true positive false negative and so on*/
    	
    	if(/*(boolean)  unfoldedValues.get(runId).get("evaluationRun") == */false){//not show columns that are not usable in intervalled results
    		
    		/*
    		TableColumn<RunsSummaryValues, String> expectation = new TableColumn<RunsSummaryValues, String>();
    		expectation.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("expectation"));  
	    	summarytableView.getColumns().add(expectation);
	    	
	    	Label expectationLabel = new Label("expectation");    	
	    	expectationLabel.setTooltip(new Tooltip("Number of boh non saprei.."));    	
	    	expectation.setGraphic(expectationLabel);
	    	*/
	    	
	    	/*
	    	TableColumn<RunsSummaryValues, String> correct = new TableColumn<RunsSummaryValues, String>();
	    	correct.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("correct"));  
	    	summarytableView.getColumns().add(correct);
	
	    	Label correctLabel = new Label("correct");    	
	    	correctLabel.setTooltip(new Tooltip("Number of boh non saprei.."));    	
	    	correct.setGraphic(correcteLabel);
	    	*/
	    	
	    	/*
	    	TableColumn<RunsSummaryValues, String> truePositive = new TableColumn<RunsSummaryValues, String>();
	    	truePositive.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("truePositive"));  
	    	summarytableView.getColumns().add(truePositive);
	    	
	    	Label truePositiveLabel = new Label("truePositive");    	
	    	truePositiveLabel.setTooltip(new Tooltip("Number of boh non saprei.."));    	
	    	truePositive.setGraphic(truePositiveLabel);
	    	
	    	TableColumn<RunsSummaryValues, String> trueNegative = new TableColumn<RunsSummaryValues, String>();
	    	trueNegative.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("trueNegative"));  
	    	summarytableView.getColumns().add(trueNegative);
	
	    	Label trueNegativeLabel = new Label("trueNegative");    	
	    	trueNegativeLabel.setTooltip(new Tooltip("Number of boh non saprei.."));    	
	    	trueNegative.setGraphic(trueNegativeLabel);
	    	
	    	TableColumn<RunsSummaryValues, String> falsePositive = new TableColumn<RunsSummaryValues, String>(); 
	    	falsePositive.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("falsePositive"));  
	    	summarytableView.getColumns().add(falsePositive);
	
	    	Label falsePositiveLabel = new Label("falsePositive");    	
	    	falsePositiveLabel.setTooltip(new Tooltip("Number of boh non saprei.."));    	
	    	falsePositive.setGraphic(falsePositiveLabel);
	    	
	    	TableColumn<RunsSummaryValues, String> falseNegative = new TableColumn<RunsSummaryValues, String>();
	    	falseNegative.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("falseNegative"));  
	    	summarytableView.getColumns().add(falseNegative);
	
	    	Label falseNegativeLabel = new Label("falseNegative");    	
	    	falseNegativeLabel.setTooltip(new Tooltip("Number of boh non saprei.."));    	
	    	falseNegative.setGraphic(falseNegativeLabel);
	    	
	    	TableColumn<RunsSummaryValues, String> truePositiveRate = new TableColumn<RunsSummaryValues, String>();
	    	truePositiveRate.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("truePositiveRate"));  
	    	summarytableView.getColumns().add(truePositiveRate);
	
	    	Label truePositiveRateLabel = new Label("truePositiveRate");    	
	    	truePositiveRateLabel.setTooltip(new Tooltip("Number of boh non saprei.."));    	
	    	truePositiveRate.setGraphic(truePositiveRateLabel);
	    	
	    	TableColumn<RunsSummaryValues, String> trueNegativeRate = new TableColumn<RunsSummaryValues, String>();
	    	trueNegativeRate.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("trueNegativeRate"));  
	    	summarytableView.getColumns().add(trueNegativeRate);
	
	    	Label trueNegativeRateLabel = new Label("trueNegativeRate");    	
	    	trueNegativeRateLabel.setTooltip(new Tooltip("Number of boh non saprei.."));    	
	    	trueNegativeRate.setGraphic(trueNegativeRateLabel);
	    	
	    	TableColumn<RunsSummaryValues, String> positivePredictiveValues = new TableColumn<RunsSummaryValues, String>();
	    	positivePredictiveValues.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("positivePredictiveValues"));  
	    	summarytableView.getColumns().add(positivePredictiveValues);
	
	    	Label positivePredictiveValuesLabel = new Label("positivePredictiveValues");    	
	    	positivePredictiveValuesLabel.setTooltip(new Tooltip("Number of boh non saprei.."));    	
	    	positivePredictiveValues.setGraphic(positivePredictiveValuesLabel);
	    	
	    	TableColumn<RunsSummaryValues, String> f1 = new TableColumn<RunsSummaryValues, String>();
	    	f1.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("f1"));  
	    	summarytableView.getColumns().add(f1);
	
	    	Label f1Label = new Label("f1");    	
	    	f1Label.setTooltip(new Tooltip("Number of boh non saprei.."));    	
	    	f1.setGraphic(f1Label);
	    	
	    	TableColumn<RunsSummaryValues, String> earlinessAvg = new TableColumn<RunsSummaryValues, String>();
	    	earlinessAvg.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("earlinessAvg"));  
	    	summarytableView.getColumns().add(earlinessAvg);
	    	
	    	Label earlinessAvgLabel = new Label("earlinessAvg");    	
	    	earlinessAvgLabel.setTooltip(new Tooltip("Number of boh non saprei.."));    	
	    	earlinessAvg.setGraphic(earlinessAvgLabel);
	    	*/
	    }
    	if(!((Boolean) unfoldedValues.get( unfoldedValues.keySet().iterator().next()).get("evaluationRun")))
    	{
    		TableColumn<RunsSummaryValues, String> correct = new TableColumn<RunsSummaryValues, String>();
    		correct.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("correct"));  
    		summarytableView.getColumns().add(correct);
    		
    		Label correctLabel = new Label("correct");
    		summaryColumns.add("correct");
    		correctLabel.setTooltip(new Tooltip("Number of correct predictions"));    	
    		correct.setGraphic(correctLabel);
    		
    		TableColumn<RunsSummaryValues, String> wrong = new TableColumn<RunsSummaryValues, String>();
    		wrong.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("wrong"));  
    		summarytableView.getColumns().add(wrong);
    		
    		Label wrongLabel = new Label("wrong"); 
    		summaryColumns.add("wrong");
    		wrongLabel.setTooltip(new Tooltip("Number of wrong predictions traces"));    	
    		wrong.setGraphic(wrongLabel);
    		
	    	TableColumn<RunsSummaryValues, String> accuracy = new TableColumn<RunsSummaryValues, String>();
	    	accuracy.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("accuracy"));  
	    	summarytableView.getColumns().add(accuracy);
	
	    	Label accuracyLabel = new Label("accuracy");    	
	    	summaryColumns.add("accuracy");
	    	//accuracyLabel.setTooltip(new Tooltip("Number of boh non saprei.."));    	
	    	accuracyLabel.setTooltip(new Tooltip("Accuracy represents the percentage of correct predictions with respect to the total number of predictions."));
	    	accuracy.setGraphic(accuracyLabel);
    	}

    	
    	TableColumn<RunsSummaryValues, String> failureRate = new TableColumn<RunsSummaryValues, String>();
    	failureRate.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("failureRate"));  
    	summarytableView.getColumns().add(failureRate);

    	Label failureRateLabel = new Label("failureRate");
    	summaryColumns.add("failureRate");
    	//failureRateLabel.setTooltip(new Tooltip("Number of boh non saprei.."));    	
    	failureRateLabel.setTooltip(new Tooltip("Failure rate represents the percentage of unpredicted predictions with respect to the total number of predictions. "));
    	failureRate.setGraphic(failureRateLabel);
    	
    	TableColumn<RunsSummaryValues, String> initTime = new TableColumn<RunsSummaryValues, String>();  
    	initTime.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("initTime"));  
    	summarytableView.getColumns().add(initTime);

    	Label initTimeLabel = new Label("initTime");
    	summaryColumns.add("initTime");
    	//initTimeLabel.setTooltip(new Tooltip("Number of boh non saprei.."));    	
    	initTimeLabel.setTooltip(new Tooltip("InitTime represents the time required for initializing the underlying data structures."));    	
    	initTime.setGraphic(initTimeLabel);
    	
    	TableColumn<RunsSummaryValues, String> totalProcessingTime = new TableColumn<RunsSummaryValues, String>();
    	totalProcessingTime.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("totalProcessingTime"));  
    	summarytableView.getColumns().add(totalProcessingTime);

    	Label totalProcessingTimeLabel = new Label("totalProcessingTime");    	
    	summaryColumns.add("totalProcessingTime");
    	//totalProcessingTimeLabel.setTooltip(new Tooltip("Number of boh non saprei.."));
    	totalProcessingTimeLabel.setTooltip(new Tooltip("TotalProcessingTime represents the time required for processing all the traces of the testing log."));
    	totalProcessingTime.setGraphic(totalProcessingTimeLabel);
    	
    	TableColumn<RunsSummaryValues, String> averageProcessingTime = new TableColumn<RunsSummaryValues, String>();
    	averageProcessingTime.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("averageProcessingTime"));  
    	summarytableView.getColumns().add(averageProcessingTime);

    	Label averageProcessingTimeLabel = new Label("averageProcessingTime");
    	summaryColumns.add("averageProcessingTime");
    	//averageProcessingTimeLabel.setTooltip(new Tooltip("Number of boh non saprei.."));    	
    	averageProcessingTimeLabel.setTooltip(new Tooltip("AverageProcessingTime represents the average time required for processing a testing trace."));
    	averageProcessingTime.setGraphic(averageProcessingTimeLabel);
    	
    	TableColumn<RunsSummaryValues, String> earliness = new TableColumn<RunsSummaryValues, String>();
    	earliness.setCellValueFactory(new PropertyValueFactory<RunsSummaryValues, String>("earliness"));  
    	summarytableView.getColumns().add(earliness);

    	Label earlinessLabel = new Label("earliness");    	
    	summaryColumns.add("earliness"); 
    	earlinessLabel.setTooltip(new Tooltip("Earliness intuitively represents how much early, on average, a prediction with  \n"
    			+ "class probability and support higher than the minimum thresholds can be provided for a trace"));    	
    	earliness.setGraphic(earlinessLabel);
    	
    	
    	
       	Map<String,PrintWriter> pw = new HashMap<>();
    	
    	for(String runId :  unfoldedValues.keySet()){
    		/*
    		System.out.println("-- START PARAMETERS --");
    		System.out.println("Currently analizing: " + runId);
    		System.out.println("## CONFIGURATION SENT ##");
    		for(String i :  unfoldedValues.get(runId).keySet()){
    			System.out.println(i+": "+ unfoldedValues.get(runId).get(i));
    		}
    		System.out.println("## END OF SENT CONFIGURATION ##");
    		System.out.println("-- END PARAMETERS --");
    		*/    		
    		Tab tab = new Tab(runId);
    		
            resultsTabPane.getTabs().add(tab);
            
            javafx.event.EventHandler<Event> eventHandler = new javafx.event.EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					Map <String, Object> act =  unfoldedValues.get(tab.getTabPane().getSelectionModel().getSelectedItem().getText());
					List<String> toRemove = new ArrayList<>();
					
					if(tab.getTabPane().getSelectionModel().getSelectedItem().getText().equals("Runs Summary")){
						
				    	Map<String,Object> valuesMap = new HashMap<>();
				    	for(String conf :  unfoldedValues.keySet())
				    	{
				    		
				    		for(String param :  unfoldedValues.get(conf).keySet())
				    		{
				    			Map<String,Object> values = (Map<String,Object>) valuesMap.get(param);
				    			if(values == null)
				    			{
				    				valuesMap.put(param,new HashMap<String,Object>());
				    			}
				    			
				    			((Map<String,Object>)valuesMap.get(param)).put(conf, unfoldedValues.get(conf).get(param));
				    		}
				    	}
				    	
				    	act = valuesMap;
					}
					else
					{
			 			for(String parameter : act.keySet())
			 			{
			 				if(!Dependencies.isDependencySatisfied(parameter, act)){
			 					toRemove.add(parameter);
			 				}
			 			}
					}
			 		
					configurationContent = new HashMap<>();
					
					TableView<ConfigurationPair> clusteringTableView = new TableView<ConfigurationPair>();
					clusteringTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
					
					clustering.setContent(clusteringTableView);
					
			    	TableColumn clusteringType = new TableColumn("Type");
			    	clusteringType.setCellValueFactory(new PropertyValueFactory("Type"));  
			    	clusteringTableView.getColumns().add(clusteringType);
			    	
			    	TableColumn clusteringValue = new TableColumn("Value");
			    	clusteringValue.setCellValueFactory(new PropertyValueFactory("Value"));  
			    	clusteringTableView.getColumns().add(clusteringValue);
			    	
			    	ObservableList<ConfigurationPair> clusteringData = FXCollections.observableArrayList();
			    	
			    	addData("clusteringType", act, toRemove,clusteringData);
			    	addData("clusteringPatternType", act, toRemove,clusteringData);
			    	addData("clusteringDiscriminativePatternCount", act, toRemove,clusteringData);
			    	addData("clusteringDiscriminativeMinimumSupport", act, toRemove,clusteringData);
			    	addData("clusteringMaximumPatternLength", act, toRemove,clusteringData);
			    	addData("clusteringMinimumPatternLength", act, toRemove,clusteringData);
			    	addData("clusteringSameLengthDiscriminativePatternCount", act, toRemove,clusteringData);
			    	//addData("hierarchicalDistanceMetrics", act, toRemove,clusteringData);
			    	//addData("modelClusteringFrom", act, toRemove,clusteringData);
			    	addData("clusterNumber", act, toRemove,clusteringData);
			    	addData("clusteringPatternMinimumSupport", act, toRemove,clusteringData);
			    	addData("clusteringDiscriminativePatternMinimumSupport", act, toRemove,clusteringData);
			    	addData("dbScanEpsilon", act, toRemove,clusteringData);
			    	addData("dbScanMinPoints", act, toRemove,clusteringData);
			    	addData("confidenceAndSupportVotingStrategy", act, toRemove,clusteringData);
			    	addData("useVotingForClustering", act, toRemove,clusteringData);
			    	addData("voters", act, toRemove,clusteringData);

			    	clusteringTableView.setItems(clusteringData);
			    	configurationContent.put("Clustering",clusteringData);
			    	
			    	TableView<ConfigurationPair> classificationTableView = new TableView<ConfigurationPair>();
					classificationTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			    	classification.setContent(classificationTableView);
					
					TableColumn classificationType = new TableColumn("Type");
					classificationType.setCellValueFactory(new PropertyValueFactory("Type"));  
			    	classificationTableView.getColumns().add(classificationType);
			    	
			    	TableColumn classificationValue = new TableColumn("Value");
			    	classificationValue.setCellValueFactory(new PropertyValueFactory("Value"));  
			    	classificationTableView.getColumns().add(classificationValue);
			    	
			    	ObservableList<ConfigurationPair> classificationData = FXCollections.observableArrayList();
			    	
			    	addData("classificationType", act, toRemove,classificationData);
			    	//classificationData.add(new ConfigurationPair("classificationPatternType",act.get("classificationPatternType")));
			    	//classificationData.add(new ConfigurationPair("classificationPatternMinimumSupport",act.get("classificationPatternMinimumSupport")));
			    	//classificationData.add(new ConfigurationPair("classificationDiscriminativePatternCount",act.get("classificationDiscriminativePatternCount")));
			    	//classificationData.add(new ConfigurationPair("classificationDiscriminativeMinimumSupport",act.get("classificationDiscriminativeMinimumSupport")));
			    	//classificationData.add(new ConfigurationPair("classificationMaximumPatternLength",act.get("classificationMaximumPatternLength")));
			    	//classificationData.add(new ConfigurationPair("classificationSameLengthDiscriminativePatternCount",act.get("classificationSameLengthDiscriminativePatternCount")));
			    	addData("rFMaxDepth", act, toRemove,classificationData);
			    	addData("rFNumFeatures", act, toRemove,classificationData);
			    	addData("rFNumTrees", act, toRemove,classificationData);
			    	addData("rFSeed", act, toRemove,classificationData);
			    	
			    	classificationTableView.setItems(classificationData);
			    	configurationContent.put("Classification",classificationData);
			    	
			    	TableView<ConfigurationPair> inputOutpuTableView = new TableView<ConfigurationPair>();
					inputOutpuTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			    	trainingTraces.setContent(inputOutpuTableView);
					
					TableColumn inputOutputType = new TableColumn("Type");
					inputOutputType.setCellValueFactory(new PropertyValueFactory("Type"));  
			    	inputOutpuTableView.getColumns().add(inputOutputType);
			    	
			    	TableColumn inputOutputValue = new TableColumn("Value");
			    	inputOutputValue.setCellValueFactory(new PropertyValueFactory("Value"));  
			    	inputOutpuTableView.getColumns().add(inputOutputValue);
			    	
			    	ObservableList<ConfigurationPair> trainingTracesData = FXCollections.observableArrayList();
			    	
			    	addData("trainingFile", act, toRemove, trainingTracesData);
			    	addData("minPrefixLength", act, toRemove, trainingTracesData);
			    	addData("maxPrefixLength", act, toRemove, trainingTracesData);
			    	addData("prefixGap", act, toRemove, trainingTracesData);
			    	
			    	inputOutpuTableView.setItems(trainingTracesData);
			    	configurationContent.put("Training",trainingTracesData);
			    	
			    	/*TableView<ConfigurationPair> logTableView = new TableView<ConfigurationPair>();
					logOption.setContent(logTableView);
					
					TableColumn logType = new TableColumn("Type");
					logType.setCellValueFactory(new PropertyValueFactory("Type"));  
			    	logTableView.getColumns().add(logType);
			    	
			    	TableColumn logValue = new TableColumn("Value");
			    	logValue.setCellValueFactory(new PropertyValueFactory("Value"));  
			    	logTableView.getColumns().add(logValue);
			    	
			    	ObservableList<ConfigurationPair> logData = FXCollections.observableArrayList();
			    	
			    	logData.add(new ConfigurationPair("logFilePath",act.get("logFilePath")));
			    	logData.add(new ConfigurationPair("generateArffReport",act.get("generateArffReport")));
			    	logData.add(new ConfigurationPair("printDebug",act.get("printDebug")));
			    	logData.add(new ConfigurationPair("generateLog",act.get("generateLog")));
			    	
			    	logTableView.setItems(logData);*/
			    	
			    	TableView<ConfigurationPair> formulaTableView = new TableView<ConfigurationPair>();
					formulaTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			    	predictionTypeTab.setContent(formulaTableView);
					
					TableColumn formulaType = new TableColumn("Type");
					formulaType.setCellValueFactory(new PropertyValueFactory("Type"));  
			    	formulaTableView.getColumns().add(formulaType);
			    	
			    	TableColumn formulaValue = new TableColumn("Value");
			    	formulaValue.setCellValueFactory(new PropertyValueFactory("Value"));  
			    	formulaTableView.getColumns().add(formulaValue);
			    	
			    	ObservableList<ConfigurationPair> predictionTypeData = FXCollections.observableArrayList();
			    	
			    	addData("formulas", act, toRemove, predictionTypeData);
			    	addData("timeFromLastEvent", act, toRemove, predictionTypeData);
			    	addData("predictionType", act, toRemove, predictionTypeData);
			    	addData("partitionMethod", act, toRemove, predictionTypeData);
			    	addData("numberOfIntervals", act, toRemove, predictionTypeData);

			    	formulaTableView.setItems(predictionTypeData);
			    	configurationContent.put("PredictionType",predictionTypeData);
			    	
			    	TableView<ConfigurationPair> evaluationTableView = new TableView<ConfigurationPair>();
					evaluationTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			    	evaluation.setContent(evaluationTableView);
					
					TableColumn evaluationType = new TableColumn("Type");
					evaluationType.setCellValueFactory(new PropertyValueFactory("Type"));  
			    	evaluationTableView.getColumns().add(evaluationType);
			    	
			    	TableColumn evaluationValue = new TableColumn("Value");
			    	evaluationValue.setCellValueFactory(new PropertyValueFactory("Value"));  
			    	evaluationTableView.getColumns().add(evaluationValue);
			    	
			    	ObservableList<ConfigurationPair> evaluationData = FXCollections.observableArrayList();
			    	
			    	addData("testingInputLogFile", act, toRemove, evaluationData);
			    	addData("evaluationRun", act, toRemove, evaluationData);
			    	addData("minConfidence", act, toRemove, evaluationData);
			    	addData("minSupport", act, toRemove, evaluationData);
			    	addData("evaluationGap", act, toRemove, evaluationData);
			    	addData("evaluationStartPoint", act, toRemove, evaluationData);

			    	evaluationTableView.setItems(evaluationData);
			    	configurationContent.put("Evaluation",evaluationData);
				}
				
				void addData(String name, Map<String,Object> data, List<String> ignore,ObservableList<ConfigurationPair> table)
				{
					if(!ignore.contains(name))
			    		table.add(new ConfigurationPair(name,data.get(name)));
				}
			};
            
            tab.getTabPane().addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
            
            eventHandler.handle(null);

			try {
				pw.put(runId, new PrintWriter(new File("output/newTestRuns/"+runId)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
			for(String key :  unfoldedValues.keySet())
			{
				pw.get(runId).println("\""+key+"\",\""+ unfoldedValues.get(key)+"\",");
			}
			pw.get(runId).flush();
			ProgressBar progressBar = new ProgressBar();
			progressBar.setMaxWidth(Double.MAX_VALUE);
			
			
			TableView<GenericResult> tableView = new TableView<GenericResult>();
			tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
				//myTable = tableView;
				
				
				VBox vb = new VBox(tableView, progressBar);
	    		tab.setContent(vb);
					

				ObservableList<GenericResult> resultTable = FXCollections.observableArrayList();
				tableView.setItems(resultTable);
				
				MyBoolean firstTuple = new MyBoolean(true);
	            
		    	globalResultListener.getResultListener(runId).getResults().addListener(new ListChangeListener<GenericResult>() {
		
					@Override
					public void onChanged(javafx.collections.ListChangeListener.Change<? extends GenericResult> arg0) {
						
						if(firstTuple.getValue()){
							System.out.println("This is a PREDICTION run");
							
							Map <String, String> newResult = arg0.getList().get(arg0.getList().size()-1).getRow();
							List<String> headers = new ArrayList<>();
							headers.add("Trace Id");
							headers.add("Confidence");
							headers.add("Support");
							headers.add("Evaluation Point");
							headers.add("Prediction");
							headers.add("Expectation");
							headers.add("Result");
							headers.add("Time");
							

							
							for(String i : headers){
								
								if(!(i.equals("Expectation") || i.equals("Result")) || !((Boolean)unfoldedValues.get(unfoldedValues.keySet().iterator().next()).get("evaluationRun")))
									Platform.runLater(new Runnable() {
										
										@Override
										public void run() {
											TableColumn<GenericResult, ?> col = new TableColumn<GenericResult, String>(i);
								        	col.setCellValueFactory(new PropertyValueFactory(i.replaceAll("\\s","")));  
								        	tableView.getColumns().add(col);
										}
									});								
							}
						}
						firstTuple.setValue(false);
						if(((Boolean)unfoldedValues.get(unfoldedValues.keySet().iterator().next()).get("evaluationRun")))
						{
							resultTable.add(new PredictionResult(arg0.getList().get(arg0.getList().size()-1).getRow()));
						}
						else
						{
							resultTable.add(new EvaluationResult(arg0.getList().get(arg0.getList().size()-1).getRow()));
						}
					}
				});
			            
	    	
	    	globalResultListener.getResultListener(runId).getProgress().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
					Double progress = (Double)(arg0.getValue().doubleValue());
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							progressBar.setProgress(progress);
						}
					});	
					
					{
						
						/**/
						System.out.println("####################################### Results #######################################");
						
						/*RunID*/
						System.out.println("Run ID: " + runId);
						
						/*Correct*/
						System.out.println("Correct: " + globalResultListener.getResultListener(runId).getCorrectCount());
												
						/*Not Predicted*/
						System.out.println("Not Predicted: " + globalResultListener.getResultListener(runId).getNotPredictedCount().get());
						
						/*Wrong*/
						System.out.println("Wrong: " + globalResultListener.getResultListener(runId).getWrongCount().get() );
						
						/*Accuracy*/
						System.out.println("Accuracy: " + globalResultListener.getResultListener(runId).getAccuracy().get());
						
						/*EarlinessAvg*/
						System.out.println("EarlinessAvg: " + globalResultListener.getResultListener(runId).getEarliness().get());
						
						/*FailureRate*/
						System.out.println("FailureRate: " + globalResultListener.getResultListener(runId).getFailureRate().get());
						
						/*Init Time*/
						System.out.println("Init Time: " + "Not yet implemented" );
						
						/*Total Processing Time*/
						System.out.println("Total Processing Time: " + globalResultListener.getResultListener(runId).getTotalTime().get());
						
						/*AveragePredictionTime*/
						System.out.println("AveragePredictionTime: " + "Not yet implemented");
						
						System.out.println("#######################################################################################");
						
						/*TODO add values to summary table*/
						RunsSummaryValues runsSummaryValues = null;
						for(RunsSummaryValues rsv: summaryResultTable){
							if(rsv.getRunId().equals(runId.toString()))
							{
								runsSummaryValues = rsv;
								break;
							}
						}
						
						String accuracy = ((Double)globalResultListener.getResultListener(runId).getAccuracy().get()).toString();
						String correctCount = ((Double)globalResultListener.getResultListener(runId).getCorrectCount().get()).toString();
						String notPredictedCount = ((Integer)globalResultListener.getResultListener(runId).getNotPredictedCount().get()).toString();
						String wrongCount = ((Double)globalResultListener.getResultListener(runId).getWrongCount().get()).toString();
						String earliness = ((Double)globalResultListener.getResultListener(runId).getEarliness().get()).toString();
						String failureRate = ((Double)globalResultListener.getResultListener(runId).getFailureRate().get()).toString();
						String totalTime = ((Long)globalResultListener.getResultListener(runId).getTotalTime().get()).toString();
						String averageTime = ((Double)globalResultListener.getResultListener(runId).getAverageTime().get()).toString();
						String initTime = ((Long)globalResultListener.getResultListener(runId).getInitTime().get()).toString();
						
						if(runsSummaryValues != null)
						{
							summaryResultTable.remove(runsSummaryValues);
						}
						summaryResultTable.add(new RunsSummaryValues(runId.toString(), 
								correctCount,  
								notPredictedCount,
								wrongCount,
								accuracy,
								earliness,
								failureRate,
								initTime,
								totalTime,
								averageTime)
								);

					}
				}
			});
    	}
    	
    	printOnCSV.addEventHandler(MouseEvent.MOUSE_CLICKED, new javafx.event.EventHandler<Event>() {
	
			@Override
			public void handle(Event event) {
				FileChooser fileChooser = new FileChooser();
	              
	              File file = fileChooser.showSaveDialog(new Stage ());
	              
	              file.mkdir();
	              
	              if(file != null){
					  try {
						  FileWriter fileWriter = null;	  	                      
					      
					      
					      MyBoolean addHeader = new MyBoolean(true);
					      
					      for(String runId : globalResultListener.getRunResults().keySet()){
					    	  
				    		  fileWriter = new FileWriter(new File(file + "/" + runId + ".csv"));
				    		  String content = new String();
				    		  
				    		  for(String category : configurationContent.keySet()){
				    			  content += "\""+category+"\"\n";
				    			  for(ConfigurationPair pair: configurationContent.get(category)){
					    			  content += ("\""+ pair.getType() +"\",");
					    			  content += ("\""+ pair.getValue() +"\",");
					    			  content += "\n";
				    			  }
				    			  content += "\n";
				    		  }
				 
				    		  content += "\n\n";
				    		  
				    		  if(globalResultListener.getRunResults().get(runId).getResults().size()>0)
				    		  {
				    			  int i=0;
				    			  Map<Integer,String> order = new HashMap<>();
					    		  for(String header : globalResultListener.getRunResults().get(runId).getResults().get(0).getRow().keySet())
					    		  {
					    			  content += "\""+header+"\",";
					    			  order.put(new Integer(i), header);
					    			  i++;
					    		  }
					    		  content += "\n";
					    		  
					    		  for(GenericResult r : globalResultListener.getRunResults().get(runId).getResults()){
					    			  for(Integer o : order.keySet())
					    			  {
					    				  content += "\""+r.getRow().get(order.get(o))+"\",";
					    			  }
					    			  content+="\n";
					    		  }
				    		  }
				    		  
				    		  content += "\n";
				    		  content+= "\"Accuracy\",\""+globalResultListener.getResultListener(runId).getAccuracy().doubleValue()+"\"\n";
				    		  content+= "\"Earliness\",\""+globalResultListener.getResultListener(runId).getEarliness().doubleValue()+"\"\n";
				    		  content+= "\"Failure Rate\",\""+globalResultListener.getResultListener(runId).getFailureRate().doubleValue()+"\"\n";
				    		  content+= "\"Total Time\",\""+globalResultListener.getResultListener(runId).getTotalTime().doubleValue()+"\"\n";

				    		  
				    		  fileWriter.write(content);
				    		  fileWriter.flush();
				    		  fileWriter.close();
				    	  }
					     fileWriter = new FileWriter(new File(file + "/summary.csv"));
					     String content = "";
					     
					     for(String c : summaryColumns)
					      {
					    	  content+="\""+c+"\",";
					      }
					     content+="\n";
					     for(RunsSummaryValues rsv:summarytableView.getItems())
					      {
					    	 for(String c : summaryColumns)
						      {
					    		 switch(c)
					    		 {
					    		 case "accuracy":
					    			 content+="\""+rsv.getAccuracy()+"\",";
					    			 break;
					    		 case "correct":
					    			 content+="\""+rsv.getCorrect()+"\",";
					    			 break;
					    		 case "wrong":
					    			 content+="\""+rsv.getWrong()+"\",";
					    			 break;
					    		 case "failureRate":
					    			 content+="\""+rsv.getFailureRate()+"\",";
					    			 break;
					    		 case "initTime":
					    			 content+="\""+rsv.getInitTime()+"\",";
					    			 break;
					    		 case "runId":
					    			 content+="\""+rsv.getRunId()+"\",";
					    			 break;
					    		 case "notPredicted":
					    			 content+="\""+rsv.getNotPredicted()+"\",";
					    			 break;
					    		 case "totalProcessingTime":
					    			 content+="\""+rsv.getTotalProcessingTime()+"\",";
					    			 break;
					    		 case "averageProcessingTime":
					    			 content+="\""+rsv.getAverageProcessingTime()+"\",";
					    			 break;
					    		 case "earliness":
					    			 content+="\""+rsv.getEarliness()+"\",";
					    			 break; 
					    		 }
						      }
					    	 content+="\n";
					      }
					      fileWriter.write(content);
					      fileWriter.flush();
					      fileWriter.close();
					      
	                  } catch (Exception ex) {
	                      ex.printStackTrace();
	                  }
	              }
        	    
			
			}
    		
    	});
    }
}
