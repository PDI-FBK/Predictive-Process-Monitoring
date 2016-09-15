
package org.processmining.plugins.predictive_monitor.bpm.client_interface.settingsPage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.processmining.plugins.predictive_monitor.bpm.client_interface.GUI;
import org.processmining.plugins.predictive_monitor.bpm.configuration.ConfigurationSet;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values.Boolean_Values;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.directory_path_values.Directory_Path_Values;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.Discrete_Values;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.TrainingFile;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values.Double_Values;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.file_path_values.File_Path_Values;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.Integer_Values;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.string_values.String_Values;
import org.processmining.plugins.predictive_monitor.bpm.configuration.modules.Classification;
import org.processmining.plugins.predictive_monitor.bpm.configuration.modules.Clustering;
import org.processmining.plugins.predictive_monitor.bpm.configuration.modules.Evaluation;
import org.processmining.plugins.predictive_monitor.bpm.configuration.modules.TrainingTracesModule;
import org.processmining.plugins.predictive_monitor.bpm.configuration.modules.LogOption;
import org.processmining.plugins.predictive_monitor.bpm.configuration.modules.Module;
import org.processmining.plugins.predictive_monitor.bpm.configuration.modules.PredictionTypeModule;
import org.processmining.plugins.predictive_monitor.bpm.replayer.ConfigurationSender;
import org.processmining.plugins.predictive_monitor.bpm.unfoder.Unfolder;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

	/**
	 * FXML Controller class
	 *
	 * @author Willo
	 */
	public class SettingsController implements Initializable, EventHandler<ActionEvent>, ChangeListener<TreeItem<String>>  {
	    @FXML
	    private TreeView<String> treeView;
	    @FXML
	    private Button runButton;
	    @FXML
	    private VBox settingsTable;
	    
	    private Map<String, Module> selections = new HashMap <>();
	    
	    private static int RunNumber = 0;
	    /**
	     * Initializes the controller class.
	     * @param url
	     * @param rb
	     */
	    @Override
	    public void initialize(URL url, ResourceBundle rb) {
	    	
	        TreeItem<String> rootItem = new TreeItem<>();  
	        rootItem.setExpanded(true); 
	        
	        TreeItem<String> training = new TreeItem<>("Training");        
	        
	        final TreeItem<String> clustering = new TreeItem<>("Clustering");
	        clustering.setExpanded(false);
	        training.getChildren().add(clustering);
	        selections.put(clustering.getValue(), new Clustering());

	        final TreeItem<String> classification = new TreeItem<>("Classification");
	        classification.setExpanded(false);
	        training.getChildren().add(classification);
	        selections.put(classification.getValue(), new Classification());
	       	        
	        final TreeItem<String> predictionType = new TreeItem<>("Prediction Type");
	        predictionType.setExpanded(false);
	        training.getChildren().add(predictionType);
	        selections.put(predictionType.getValue(), new PredictionTypeModule());
	        
	        final TreeItem<String> trainingTraces = new TreeItem<>("Training Traces");
	        trainingTraces.setExpanded(false);
	        training.getChildren().add(trainingTraces);	        
	        TrainingTracesModule trainingTracesModule = new TrainingTracesModule();
	        trainingTracesModule.setAvailableTrainingFile(GUI.trainingFilePath);	        
	        selections.put(trainingTraces.getValue(), trainingTracesModule);
	        
	        /*final TreeItem<String>  logOption = new TreeItem<>("Log Option");
	        logOption.setExpanded(false);
	        training.getChildren().add(logOption);
	        selections.put(logOption.getValue(), new LogOption());*/

	        TreeItem<String> run = new TreeItem<>("Execution");
	        
	        TreeItem<String> evaluation = new TreeItem<>("Evaluation");
	        evaluation.setExpanded(false);
	        run.getChildren().add(evaluation);
	        selections.put(evaluation.getValue(), new Evaluation());
	        
	        treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);  
	        treeView.getSelectionModel().selectedItemProperty().addListener(this);  
	        rootItem.getChildren().add(training);
	        rootItem.getChildren().add(run);
	        treeView.setRoot(rootItem);
	        treeView.setShowRoot(false);
	        
	        runButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	            public void handle(MouseEvent e1) {
	            	
	            	ConfigurationSet currentConfiguration = new ConfigurationSet();
	            	currentConfiguration.setClassification(selections.get("Classification"));
	            	currentConfiguration.setClustering(selections.get("Clustering"));
	            	currentConfiguration.setTrainingTraces(selections.get("Training Traces"));
	            	currentConfiguration.setLogOption(selections.get("Log Option"));
	            	currentConfiguration.setPredictionType(selections.get("Prediction Type"));
	            	currentConfiguration.setEvaluation(selections.get("Evaluation"));
	            	
	            	TextInputDialog dialog = new TextInputDialog("newRunID"+(RunNumber == 0 ? "" : RunNumber));
	            	RunNumber++;
	            	dialog.setTitle("Text Input Dialog");
	            	dialog.setHeaderText("Tell me a name for the new run");
	            	dialog.setContentText("Enter here the name of the new run:");

	            	String runId = null; 
	            	Optional<String> result = dialog.showAndWait();
	            	if (result.isPresent() && result != null){
	            	    runId = result.get();
	            	   final String rId = runId;
	            	    new Thread(new Runnable() {
	                        @Override
	                        public void run() {
	                            Platform.runLater(new Runnable() {
	                                @Override
	                                public void run() {
	                                   //UI Stuff
	                                	Unfolder unfolder = new Unfolder(currentConfiguration, rId, GUI.configurationSender);
	                                	
	                                	unfolder.unfoldAndSendConf();
	                                	Stage settingsStage = new Stage();	        		
	                                	
	                                	final FXMLLoader loader = new FXMLLoader();
	                                	loader.setLocation(this.getClass().getResource("../resultPage/Report.fxml"));
	                                	
	                                	loader.setClassLoader(this.getClass().getClassLoader());
	                                	
	                                	Parent parent = null;
	                                	try {
	                                		parent = loader.load();
	                                	} catch (IOException e2) {
	                                		e2.printStackTrace();
	                                	}
	                                	
	                                	Scene scene = new Scene(parent);
	                                	settingsStage.setScene(scene);
	                                	settingsStage.getIcons().add(new Image(getClass().getResourceAsStream("../mockup1.jpg")));
	                                	settingsStage.setTitle("Predictive Monitoring");
	                                	
	                                	settingsStage.show();
	                                	
	                                }
	                            });
	                        }
	                    }).start();
		                //This closes the settings window
		                //((Node)(e1.getSource())).getScene().getWindow().hide();
		                
		                TrainingFile t = null;
		                GUI.configurationSender = new ConfigurationSender();
		                for(Parameter p:selections.get("Training Traces").getParameterList())
		                {
		                	if(p instanceof TrainingFile)
		                	{
		                		t = (TrainingFile)p;
		                	}
		                }
		                //This should reset the settings
		                selections = new HashMap<String, Module>();
		                selections.put("Classification", new Classification());
		            	selections.put("Clustering", new Clustering());
		            	selections.put("Training Traces", new TrainingTracesModule(t));
		            	selections.put("Log Option", new LogOption());
		            	selections.put("Prediction Type", new PredictionTypeModule());
		            	selections.put("Evaluation", new Evaluation());
		            	/*
		            	//this refreshes the view
		            	settingsTable.getChildren().clear();
		            	
		            	settingsTable.setAlignment(Pos.CENTER);
			        	settingsTable.setSpacing(5);
			        	Label l1 = new Label("Welcome to Predictive Monitoring");
			        	l1.setFont(Font.font("Helvetica", FontWeight.NORMAL, 24));
			        	settingsTable.getChildren().add(l1);
			        	
			        	Label l2 = new Label("You can now choose in the left panel witch parameters to set. ");
			        	l2.setFont(Font.font("Helvetica", FontWeight.NORMAL, 14));
			        	settingsTable.getChildren().add(l2);
			        	
			        	Label l3 = new Label("All provided parameters can be customized between a set of possible values. ");
			        	l3.setFont(Font.font("Helvetica", FontWeight.NORMAL, 14));
			        	settingsTable.getChildren().add(l3);
			        	
			        	Label l4 = new Label("To change a value just select it from the provided box and click the  \"Add\" button, if you don't the default value will be used.");
			        	l4.setFont(Font.font("Helvetica", FontWeight.NORMAL, 14));
			        	settingsTable.getChildren().add(l4);
			        	*/
			        	treeView.getSelectionModel().clearAndSelect(0);
	            	}
	            };
	        });
	    }    

	    @Override
	    public void handle(ActionEvent event) {
	        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }
	    
	    
	    @Override
	    public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue, TreeItem<String> newValue) {
	        settingsTable.getChildren().clear();
	        settingsTable.setAlignment(Pos.TOP_LEFT);
	        settingsTable.setSpacing(5);
	        
	        if(!newValue.getValue().equals("Training") && !newValue.getValue().equals("Execution")){
	        	Label title = new Label(newValue.getValue());
	        	title.setFont(new Font(18));
	        	title.setPadding(new Insets(10));
	        	
	        	settingsTable.setAlignment(Pos.TOP_LEFT);
	        	
            	settingsTable.getChildren().add(title);
            	
            	try{
	    	        for(Parameter i : selections.get(oldValue.getValue()).getParameterList())
	    	        	i.onGUI = false;
            	}catch(Exception e){}

            	Module selected = selections.get(newValue.getValue()); 
            	
    	    	List <Parameter> paramMap = selected.getParameterList();

    	    	for(int j = 0; j < paramMap.size(); j++){
    	    		if ((paramMap.get(j)).getDependendingFromFields().isEmpty() && paramMap.get(j).onGUI == false){
	    	    		if(paramMap.get(j) instanceof Discrete_Values && !(paramMap.get(j).getClass().getSimpleName().equals("ClassificationPatternType"))){ // <--- Pay attention this is a spike solution to void drawing a param in GUI
	    	    			paramMap.get(j).onGUI = true;
    	    				settingsTable.getChildren().add(new SubmoduleGUI((Discrete_Values) paramMap.get(j), paramMap, settingsTable, newValue.getValue(), selections));
    	    				for(Object selectedParam : paramMap.get(j).getSelectedValues()){
	    	    				List <String> fields = paramMap.get(j).getImpliedFields().get(selectedParam);
	    	    			    if (fields!=null){
	    	    				    for (String field : fields) {
	    	    					    for (Parameter parameter : paramMap) {
	    	    							if (parameter.getClass().getSimpleName().equalsIgnoreCase(field)){
	    	    								if(parameter.onGUI == false){
	    	    									//module.getChildren().add(new SubmoduleGUI((Discrete_Values) parameter, addedTable, paramMap));
	    	    				    	    		if(parameter instanceof Discrete_Values){
	    	    				    	    			if (((Discrete_Values) parameter).getDependendingFromFields().isEmpty())
	    	    				    	    				parameter.onGUI = true;
	    	    				    	    				settingsTable.getChildren().add(new SubmoduleGUI((Discrete_Values) parameter));
	    	    				    	    		}else if(parameter instanceof Integer_Values){
	    	    				    	    			parameter.onGUI = true;
	    	    				    	    			settingsTable.getChildren().add(new SubmoduleGUI((Integer_Values) parameter));
	    	    				    	    		}else if(parameter instanceof Double_Values){
	    	    				    	    			parameter.onGUI = true;
	    	    				    	    			settingsTable.getChildren().add(new SubmoduleGUI((Double_Values) parameter));
	    	    				    	    		}else if(parameter instanceof Boolean_Values){
	    	    				    	    			parameter.onGUI = true;
	    	    				    	    			settingsTable.getChildren().add(new SubmoduleGUI((Boolean_Values) parameter, paramMap, settingsTable, newValue.getValue(), selections));
	    	    				    	    		}else if(parameter instanceof String_Values){
	    	    				    	    			parameter.onGUI = true;
	    	    				    	    			settingsTable.getChildren().add(new SubmoduleGUI((String_Values) parameter));
	    	    				    	    		}else System.out.println("Yet not implemented parse for: "+ parameter.getClass().getSimpleName());	    		
	    	    								}
	    	    							}
	    	    						}
	    	    				    }
	    	    			    }
    	    				}
	    	    		}else if(paramMap.get(j) instanceof Integer_Values){
	    	    			paramMap.get(j).onGUI = true;
    	    				settingsTable.getChildren().add(new SubmoduleGUI((Integer_Values) paramMap.get(j)));
    	    				paramMap.get(j).onGUI = true;
	    	    		}else if(paramMap.get(j) instanceof Double_Values){
	    	    			paramMap.get(j).onGUI = true;
    	    				settingsTable.getChildren().add(new SubmoduleGUI((Double_Values) paramMap.get(j)));
	    	    		}else if(paramMap.get(j) instanceof Boolean_Values){
	    	    			paramMap.get(j).onGUI = true;
	    	    			settingsTable.getChildren().add(new SubmoduleGUI((Boolean_Values) paramMap.get(j), paramMap, settingsTable, newValue.getValue(), selections));
	    	    			for(Object selectedParam : paramMap.get(j).getSelectedValues()){
	    	    				List <String> fields = paramMap.get(j).getImpliedFields().get(selectedParam);
	    	    			    if (fields!=null){
	    	    				    for (String field : fields) {
	    	    					    for (Parameter parameter : paramMap) {
	    	    							if (parameter.getClass().getSimpleName().equalsIgnoreCase(field)){
	    	    								if(parameter.onGUI == false){
	    	    									//module.getChildren().add(new SubmoduleGUI((Discrete_Values) parameter, addedTable, paramMap));
	    	    				    	    		if(parameter instanceof Discrete_Values){
	    	    				    	    			if (((Discrete_Values) parameter).getDependendingFromFields().isEmpty())
	    	    				    	    				parameter.onGUI = true;
	    	    				    	    				settingsTable.getChildren().add(new SubmoduleGUI((Discrete_Values) parameter));
	    	    				    	    		}else if(parameter instanceof Integer_Values){
	    	    				    	    			parameter.onGUI = true;
	    	    				    	    			settingsTable.getChildren().add(new SubmoduleGUI((Integer_Values) parameter));
	    	    				    	    		}else if(parameter instanceof Double_Values){
	    	    				    	    			parameter.onGUI = true;
	    	    				    	    			settingsTable.getChildren().add(new SubmoduleGUI((Double_Values) parameter));
	    	    				    	    		}else if(parameter instanceof Boolean_Values){
	    	    				    	    			parameter.onGUI = true;
	    	    				    	    			settingsTable.getChildren().add(new SubmoduleGUI((Boolean_Values) parameter, paramMap, settingsTable, newValue.getValue(), selections));
	    	    				    	    		}else if(parameter instanceof String_Values){
	    	    				    	    			parameter.onGUI = true;
	    	    				    	    			settingsTable.getChildren().add(new SubmoduleGUI((String_Values) parameter));
	    	    				    	    		}else System.out.println("Yet not implemented parse for: "+ parameter.getClass().getSimpleName());	    		
	    	    								}
	    	    							}
	    	    						}
	    	    				    }
	    	    			    }
    	    				}
	    	    		}else if(paramMap.get(j) instanceof String_Values){
	    	    			paramMap.get(j).onGUI = true;
	    	    			settingsTable.getChildren().add(new SubmoduleGUI((String_Values) paramMap.get(j)));
	    	    		}else if(paramMap.get(j) instanceof File_Path_Values){
	    	    			paramMap.get(j).onGUI = true;
	    	    			settingsTable.getChildren().add(new SubmoduleGUI((File_Path_Values) paramMap.get(j)));
	    	    		}else if(paramMap.get(j) instanceof Directory_Path_Values){
	    	    			paramMap.get(j).onGUI = true;
			    			settingsTable.getChildren().add(new SubmoduleGUI((Directory_Path_Values) paramMap.get(j)));
	    	    		}else System.out.println("Yet not implemented parse for: "+ paramMap.get(j).getClass().getSimpleName());	
    	    		}
    	    	}
	        }else{
	        	settingsTable.setAlignment(Pos.CENTER);
	        	settingsTable.setSpacing(5);
	        	Label l1 = new Label("Welcome to Predictive Monitoring");
	        	l1.setFont(Font.font("Helvetica", FontWeight.NORMAL, 24));
	        	settingsTable.getChildren().add(l1);
	        	
	        	Label l2 = new Label("You can now choose in the left panel which parameters to set. ");
	        	l2.setFont(Font.font("Helvetica", FontWeight.NORMAL, 14));
	        	settingsTable.getChildren().add(l2);
	        	
	        	Label l3 = new Label("All provided parameters can be customized between a set of possible values. ");
	        	l3.setFont(Font.font("Helvetica", FontWeight.NORMAL, 14));
	        	settingsTable.getChildren().add(l3);
	        	
	        	Label l4 = new Label("To change a value just select it from the provided box and click the  \"Add\" button, if you don't the default value will be used.");
	        	l4.setFont(Font.font("Helvetica", FontWeight.NORMAL, 14));
	        	settingsTable.getChildren().add(l4);
	        	
	        }
	    }
	}
