package org.processmining.plugins.predictive_monitor.bpm.client_interface.settingsPage;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values.Boolean_Values;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.directory_path_values.Directory_Path_Values;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.Discrete_Values;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values.Double_Values;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.file_path_values.File_Path_Values;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.Integer_Values;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.string_values.String_Values;
import org.processmining.plugins.predictive_monitor.bpm.configuration.modules.Module;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.SimpleFormula;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ltl2aut.formula.DefaultParser;

public class SubmoduleGUI extends GridPane {
	final private GridPane module;
	public enum inputType {SINGLE_VALUE, INTERVALLED_VALUES}
	TextField value, from, to, gap; 

	private void setStyle()
	{
		module.setPadding(new Insets(8));
		module.setVgap(5);
		module.setHgap(3);
		//module.setAlignment(Pos.CENTER);		
		ColumnConstraints col1Constraints = new ColumnConstraints();
		col1Constraints.setPercentWidth(100);
		/*
		ColumnConstraints col2Constraints = new ColumnConstraints();
		col2Constraints.setPercentWidth(50);*/
		module.getColumnConstraints().addAll(col1Constraints);
		
		module.setStyle("-fx-border-style: solid; -fx-border-color: grey; -fx-border-radius: 5;");

	}
	
	
	private Node topBar(Node label,Node type, Node value, Node addButton)
	{
		GridPane topBar = new GridPane();
		GridPane addBar = new GridPane();
		
		topBar.setAlignment(Pos.CENTER);
		topBar.setVgap(4);
		
		addBar.setAlignment(Pos.CENTER);
		addBar.setHgap(4);
		
		ColumnConstraints col1Constraints = new ColumnConstraints();
		ColumnConstraints col2Constraints = new ColumnConstraints();
		ColumnConstraints col3Constraints = new ColumnConstraints();


		col1Constraints.setPercentWidth(17);
		col2Constraints.setPercentWidth(76);
		col3Constraints.setPercentWidth(7);
	

		
		topBar.add(label,0,0);
		if(type!=null)addBar.add(type,0,0);
		if(value!=null){
			if(type != null){
				addBar.add(value,1,0);
			}else{
				addBar.add(value,0,0,2,1);
			}
		}
		if(addButton!=null){
			((Button)addButton).setMaxWidth(Double.MAX_VALUE);
			addBar.add(addButton,2,0);
		}
		topBar.add(addBar, 0, 1);
		
		addBar.getColumnConstraints().addAll(col1Constraints,col2Constraints,col3Constraints);

		return topBar;
	}
	
	private TableView<ReportTableTuple> createTable()
	{
		TableView<ReportTableTuple> tableView = new TableView<ReportTableTuple>();
		tableView.setFixedCellSize(25);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		return tableView;
	}
	
	private void refreshView(VBox settingsTable, String selectedSection, Map<String, Module> selections){


        settingsTable.getChildren().clear();
        settingsTable.setAlignment(Pos.TOP_LEFT);
        settingsTable.setSpacing(5);
        
        if(!selectedSection.equals("Training") && !selectedSection.equals("Execution")){
        	Label title = new Label(selectedSection);
        	title.setFont(new Font(18));
        	title.setPadding(new Insets(10));
        	
        	settingsTable.setAlignment(Pos.TOP_LEFT);
        	
        	settingsTable.getChildren().add(title);
        	
        	try{
    	        for(Parameter i : selections.get(selectedSection).getParameterList())
    	        	i.onGUI = false;
        	}catch(Exception exc){}

        	Module selected = selections.get(selectedSection); 
        	
	    	List <Parameter> paramMap = selected.getParameterList();

	    	for(int j = 0; j < paramMap.size(); j++){
	    		if ((paramMap.get(j)).getDependendingFromFields().isEmpty() && paramMap.get(j).onGUI == false){
    	    		if(paramMap.get(j) instanceof Discrete_Values && !(paramMap.get(j).getClass().getSimpleName().equals("ClassificationPatternType"))){ // <--- Pay attention this is a spike solution to void drawing a param in GUI
    	    			paramMap.get(j).onGUI = true;
	    				settingsTable.getChildren().add(new SubmoduleGUI((Discrete_Values) paramMap.get(j), paramMap, settingsTable, selectedSection, selections));
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
    	    				    	    			settingsTable.getChildren().add(new SubmoduleGUI((Boolean_Values) parameter, paramMap, settingsTable, selectedSection, selections));
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
    	    			settingsTable.getChildren().add(new SubmoduleGUI((Boolean_Values) paramMap.get(j), paramMap, settingsTable, selectedSection, selections));
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
    	    				    	    			settingsTable.getChildren().add(new SubmoduleGUI((Boolean_Values) parameter, paramMap, settingsTable, selectedSection, selections));
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

	public SubmoduleGUI(final Discrete_Values i) {
		module = this;
		
		setStyle();
		
		Label iLabel = new Label(i.getClass().getSimpleName());
		iLabel.setTooltip(new Tooltip(i.getTooltip()));
		
		
		final ComboBox<String> selectionType = new ComboBox<String>();
		selectionType.getItems().addAll(i.getPossibleValues());
		selectionType.setValue((String) i.getDefaultValue());
		
		selectionType.setPrefWidth(REMAINING);
		
		// Handle ComboBox event.
		selectionType.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
			    String selectedType = selectionType.getSelectionModel().getSelectedItem();
			    i.getImpliedFields().get(selectedType);
			    
            }
		});
		
		
		
		
		Button add = new Button("Add");
		Button remove = new Button("Remove");
		remove.setAlignment(Pos.CENTER);
		
		module.add(topBar(iLabel,null,selectionType,add), 0, 0);
		
		TableView<ReportTableTuple> tableView = createTable();
		ObservableList<ReportTableTuple> resultTable = FXCollections.observableArrayList();
		tableView.setItems(resultTable);
		
		
		TableColumn<ReportTableTuple, ?> valueCol = new TableColumn<ReportTableTuple, String>("Value");
    	valueCol.setCellValueFactory(new PropertyValueFactory("Value"));  
    	tableView.getColumns().add(valueCol);
    	
		add.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
            	//addedTable.getChildren().add(new Label(selectionType.getValue()));
            	i.addValue(selectionType.getValue());
            	
        		resultTable.clear();
        		
        		for(Object j : i.getSelectedValues()){
        			resultTable.add(new ReportTableTuple(j, i));
        		}
            	System.out.println(i.getValues());
            };
        });
		
		remove.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
            	try {
            		i.removeValue(tableView.getSelectionModel().getSelectedItem().getValue());
            		
            		resultTable.clear();
            		
            		for(Object j : i.getSelectedValues()){
            			resultTable.add(new ReportTableTuple(j, i));
            		}
            		
            	}catch(Exception ex){
            		System.out.println("No values in table!");
            	}
            };
        });
		
		tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));
		tableView.minHeightProperty().bind(tableView.prefHeightProperty());
		tableView.maxHeightProperty().bind(tableView.prefHeightProperty());
		
		module.add(tableView, 0, 1);
		module.add(new HBox(remove), 0, 2);
		
		for(Object j : i.getSelectedValues()){
			resultTable.add(new ReportTableTuple(j, i));
		}
		
		
		//module.setMinHeight(module.getHeight());
		
	}
	
	public SubmoduleGUI(final Discrete_Values i, final List <Parameter> paramMap, final VBox settingsTable, String selectedSection, Map<String, Module> selections) {
		module = this;

		setStyle();
		
		Label iLabel = new Label(i.getClass().getSimpleName());
		iLabel.setTooltip(new Tooltip(i.getTooltip()));
		
		
		final ComboBox<String> selectionType = new ComboBox<String>();
		selectionType.getItems().addAll(i.getPossibleValues());
		//adding default to interface
		selectionType.setValue((String) i.getDefaultValue());
		selectionType.setPrefWidth(REMAINING);
		
		
		Button add = new Button("Add");
		module.add(topBar(iLabel, null, selectionType, add),0,0);
		Button remove = new Button("Remove");
		
		TableView<ReportTableTuple> tableView = createTable();
		ObservableList<ReportTableTuple> resultTable = FXCollections.observableArrayList();
		tableView.setItems(resultTable);
		
		TableColumn<ReportTableTuple, ?> valueCol = new TableColumn<ReportTableTuple, String>("Value");
    	valueCol.setCellValueFactory(new PropertyValueFactory("Value"));  
    	tableView.getColumns().add(valueCol);
    	
		add.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
            	i.addValue(selectionType.getValue());
            	
        		resultTable.clear();
        		
        		for(Object j : i.getSelectedValues()){
        			resultTable.add(new ReportTableTuple(j, i));
        		}
            	System.out.println(i.getValues());
            	
            	refreshView(settingsTable, selectedSection, selections);
			    
            };
        });
		
		remove.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
            	
            	try {
            		i.removeValue(tableView.getSelectionModel().getSelectedItem().getValue());

            		resultTable.clear();
            		
            		for(Object j : i.getSelectedValues()){
            			resultTable.add(new ReportTableTuple(j, i));
            		}
            	}catch(Exception ex){
            		System.out.println("No values in table!");
            	}
            	refreshView(settingsTable, selectedSection, selections);
            };
        });
       
		tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));
		tableView.minHeightProperty().bind(tableView.prefHeightProperty());
		tableView.maxHeightProperty().bind(tableView.prefHeightProperty());
		
		module.add(tableView, 0, 1);
		module.add(new HBox(remove), 0, 2);
		
		for ( Object j : i.getSelectedValues()){
			resultTable.add(new ReportTableTuple(j, i));
		}
		
		//module.setMinHeight(module.getHeight());
	}	
	/*
	public void upgradePositions(TableView<ReportTableTuple> tableView, Button add, int tableHeight){
		module.getChildren().remove(tableView);
		module.add(tableView, 0, 1, tableHeight, 6);
		module.getChildren().remove(add);
		module.add(add, 1, tableHeight + 2, 1, 1);
		tableHeight++;
	}
	*/
	
	public SubmoduleGUI(final Integer_Values i) {
		module = this;

		setStyle();
		
		Label iLabel = new Label(i.getClass().getSimpleName());
		iLabel.setTooltip(new Tooltip(i.getTooltip()));
		
		final ComboBox<inputType> selectionType = new ComboBox<inputType>();
		selectionType.getItems().setAll(inputType.values());
		
		selectionType.setPadding(new Insets(5, 5, 5, 5));
		selectionType.setPrefWidth(REMAINING);
		
		Button add = new Button("Add");
		Button remove = new Button("Remove");
		
		TableView<ReportTableTuple> tableView = createTable();
		ObservableList<ReportTableTuple> resultTable = FXCollections.observableArrayList();
		tableView.setItems(resultTable);
		
		TableColumn<ReportTableTuple, ?> valueCol = new TableColumn<ReportTableTuple, String>("Value");
    	valueCol.setCellValueFactory(new PropertyValueFactory("Value"));  
    	tableView.getColumns().add(valueCol);
    	
		add.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
            	
            	if(selectionType.getValue().toString().equals("INTERVALLED_VALUES")){
            		
            		Integer fromInt = null;
        			Integer toInt = null;
        			Integer gapInt = null;
        			
            		try{
            			fromInt = Integer.parseInt(from.getText());
            			toInt = Integer.parseInt(to.getText());
            			gapInt = Integer.parseInt(gap.getText());
            		}catch(Exception ex){
            			System.out.println("Please enter a valid integer!");
            		}
            		
            		if(fromInt != null && toInt != null && gapInt != null){
            			for(int j = fromInt; j <= toInt; j += gapInt){
            				//addedTable.getChildren().add(new Label(""+i));
            				i.addValue(j);
            			}

                		resultTable.clear();
                		
                		for(Object j : i.getSelectedValues()){
                			resultTable.add(new ReportTableTuple(j, i));
                		}
            		}
            		
            	}else if(selectionType.getValue().toString().equals("SINGLE_VALUE")){
            		
            		Integer valueInt = null;
            		try{
            			valueInt = Integer.parseInt(value.getText());
            		}catch(Exception ex){
            			System.out.println("Please enter a valid integer!");
            		}
            		if(valueInt != null){
        				i.addValue(valueInt);
                		resultTable.clear();
                		
                		for(Object j : i.getSelectedValues()){
                			resultTable.add(new ReportTableTuple(j, i));
                		}
            			//addedTable.getChildren().add(new Label(valueInt.toString()));
            			
            		}
            		
            	}
            	
            	
            	System.out.println(i.getValues());
            };
        });
		remove.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
            	
            	try {
            		i.removeValue(Integer.parseInt(tableView.getSelectionModel().getSelectedItem().getValue()));

            		resultTable.clear();
            		
            		for(Object j : i.getSelectedValues()){
            			resultTable.add(new ReportTableTuple(j, i));
            		}
            	}catch(Exception ex){
            		System.out.println("No values in table!");
            	}
            };
        });
				
		final HBox input = new HBox();
		module.add(input, 0, 1, 2, 1);
		GridPane.setHalignment(input, HPos.CENTER);
		
		module.add(topBar(iLabel,selectionType, input, add), 0, 0);
		
		tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));
		tableView.minHeightProperty().bind(tableView.prefHeightProperty());
		tableView.maxHeightProperty().bind(tableView.prefHeightProperty());
		
		module.add(tableView, 0, 1);
		module.add(remove, 0, 2);
		
		for( Object j : i.getSelectedValues()){
			resultTable.add(new ReportTableTuple(j, i));
		}
		
		//module.setMinHeight(module.getHeight());
		
		selectionType.valueProperty().addListener(
				new ChangeListener<inputType>() {

					@Override
					public void changed(ObservableValue<? extends inputType> observable, inputType oldValue, inputType newValue) {						
						switch(newValue.toString()){
							case "INTERVALLED_VALUES":
								input.getChildren().clear();
								input.setPadding(new Insets(5, 5, 5, 5));
								
								Label fromLabel = new Label("From: ");
								fromLabel.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(fromLabel);
								from = new TextField();
								from.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(from);
	
								Label toLabel = new Label("To:");
								toLabel.setMinWidth(USE_PREF_SIZE);
								toLabel.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(toLabel);
								to = new TextField();
								to.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(to);
	
								Label gapLabel = new Label("Gap:");
								gapLabel.setMinWidth(USE_PREF_SIZE);
								gapLabel.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(gapLabel);
								gap = new TextField();
								gap.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(gap);
								break;
							case"SINGLE_VALUE":
								input.getChildren().clear();
								input.setPadding(new Insets(5, 5, 5, 5));
								
								Label valueLabel = new Label("Value: ");
								valueLabel.setMinWidth(USE_PREF_SIZE);
								valueLabel.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(valueLabel);
								value = new TextField();
								value.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(value);
								break;
							default:
									System.out.println(newValue);
									input.getChildren().add(new Label("Sorry there was a problem with the switch"));
									break;
						}
						
					}
				});
		selectionType.setValue(inputType.SINGLE_VALUE);
		
	}
	
	public SubmoduleGUI(final Double_Values i) {
		module = this;

		setStyle();
		
		Label iLabel = new Label(i.getClass().getSimpleName());
		iLabel.setTooltip(new Tooltip(i.getTooltip()));
		
		final ComboBox<inputType> selectionType = new ComboBox<inputType>();
		selectionType.getItems().setAll(inputType.values());
		selectionType.setPadding(new Insets(5, 5, 5, 5));
		selectionType.setPrefWidth(REMAINING);
		
		Button add = new Button("Add");
		Button remove = new Button("Remove");
		
		TableView<ReportTableTuple> tableView = createTable();
		ObservableList<ReportTableTuple> resultTable = FXCollections.observableArrayList();
		tableView.setItems(resultTable);
		
		TableColumn<ReportTableTuple, ?> valueCol = new TableColumn<ReportTableTuple, String>("Value");
    	valueCol.setCellValueFactory(new PropertyValueFactory("Value"));  
    	tableView.getColumns().add(valueCol);
    	
		add.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
            	
            	if(selectionType.getValue().toString().equals("INTERVALLED_VALUES")){
            		Double fromDouble = null;
            		Double toDouble = null;
            		Double gapDouble = null;
        			
            		try{
            			fromDouble = Double.parseDouble(from.getText());
            			toDouble = Double.parseDouble(to.getText());
            			gapDouble = Double.parseDouble(gap.getText());
            		}catch(Exception ex){
            			System.out.println("Please enter a valid Double!");
            		}
            		
            		if(fromDouble != null && toDouble != null && gapDouble != null){
            			for(Double j = fromDouble; j <= toDouble;j += gapDouble){
            				//addedTable.getChildren().add(new Label(""+j));
            				i.addValue(j);
            			}

                		resultTable.clear();
                		
                		for(Object j : i.getSelectedValues()){
                			resultTable.add(new ReportTableTuple(j, i));
                		}
            		}
            		
            	}else if(selectionType.getValue().toString().equals("SINGLE_VALUE")){
            		
            		Double valueDouble = null;
            		try{
            			valueDouble = Double.parseDouble(value.getText());
            		}catch(Exception ex){
            			System.out.println("Please enter a valid Double!");
            		}
            		if(valueDouble != null){
            			i.addValue(valueDouble);

                		resultTable.clear();
                		
                		for(Object j : i.getSelectedValues()){
                			resultTable.add(new ReportTableTuple(j, i));
                		}
            		}
            	}
            	
            	System.out.println(i.getValues());
            };
        });
		
		remove.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
            	
            	try {
            		i.removeValue(Double.parseDouble(tableView.getSelectionModel().getSelectedItem().getValue()));

            		resultTable.clear();
            		
            		for(Object j : i.getSelectedValues()){
            			resultTable.add(new ReportTableTuple(j, i));
            		}
            	}catch(Exception ex){
            		System.out.println("No values in table!");
            	}
            };
        });
				
		final HBox input = new HBox();
		module.add(topBar(iLabel, selectionType, input, add), 0, 0);
		
		
		tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));
		tableView.minHeightProperty().bind(tableView.prefHeightProperty());
		tableView.maxHeightProperty().bind(tableView.prefHeightProperty());
		
		module.add(tableView, 0, 1);
		module.add(remove, 0, 2);
		
		//module.setMinHeight(module.getHeight());
		
		//works only for single values
		for( Object j : i.getSelectedValues()){
			resultTable.add(new ReportTableTuple(j, i));
		}
		
		selectionType.valueProperty().addListener(
				new ChangeListener<inputType>() {

					@Override
					public void changed(ObservableValue<? extends inputType> observable, inputType oldValue, inputType newValue) {						
						switch(newValue.toString()){
							case "INTERVALLED_VALUES":
								input.getChildren().clear();
								input.setPadding(new Insets(5, 5, 5, 5));
								
								Label fromLabel = new Label("From: ");
								fromLabel.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(fromLabel);
								from = new TextField();
								from.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(from);
	
								Label toLabel = new Label("To:");
								toLabel.setMinWidth(USE_PREF_SIZE);
								toLabel.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(toLabel);
								to = new TextField();
								to.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(to);
	
								Label gapLabel = new Label("Gap:");
								gapLabel.setMinWidth(USE_PREF_SIZE);
								gapLabel.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(gapLabel);
								gap = new TextField();
								gap.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(gap);
								break;
							case"SINGLE_VALUE":
								input.getChildren().clear();
								input.setPadding(new Insets(5, 5, 5, 5));
								
								Label valueLabel = new Label("Value: ");
								valueLabel.setMinWidth(USE_PREF_SIZE);
								valueLabel.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(valueLabel);
								value = new TextField();
								value.setPadding(new Insets(5, 5, 5, 5));
								input.getChildren().add(value);
								break;
							default:
									System.out.println(newValue);
									input.getChildren().add(new Label("Sorry there was a problem with the switch"));
									break;
						}
						
					}
				});
			selectionType.setValue(inputType.SINGLE_VALUE);
	}
	
	public SubmoduleGUI(final Boolean_Values i, final List <Parameter> paramMap, final VBox settingsTable, String selectedSection, Map<String, Module> selections) {
		module = this;

		setStyle();
		
		final CheckBox targetField = new CheckBox(i.getClass().getSimpleName());
		targetField.setSelected((Boolean)i.getDefaultValue());
		targetField.setTooltip(new Tooltip(i.getTooltip()));
		targetField.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
			    String selectedType = targetField.isSelected() + "";
			    List <String> fields = i.getImpliedFields().get(selectedType);
			    if (fields!=null){
				    for (String field : fields) {
					    for (Parameter parameter : paramMap) {
							if (parameter.getClass().getSimpleName().equalsIgnoreCase(field)){
								if(parameter.onGUI == false){
									//module.getChildren().add(new SubmoduleGUI((Discrete_Values) parameter, addedTable, paramMap));
				    	    		if(parameter instanceof Discrete_Values){
				    	    			if (((Discrete_Values) parameter).getDependendingFromFields().isEmpty())
				    	    				parameter.onGUI = true;
				    	    				settingsTable.getChildren().add(settingsTable.getChildren().indexOf(module) + 1, new SubmoduleGUI((Discrete_Values) parameter));
				    	    		}else if(parameter instanceof Integer_Values){
				    	    			parameter.onGUI = true;
				    	    			settingsTable.getChildren().add(settingsTable.getChildren().indexOf(module) + 1, new SubmoduleGUI((Integer_Values) parameter));
				    	    		}else if(parameter instanceof Double_Values){
				    	    			parameter.onGUI = true;
				    	    			settingsTable.getChildren().add(settingsTable.getChildren().indexOf(module) + 1, new SubmoduleGUI((Double_Values) parameter));
				    	    		}else if(parameter instanceof Boolean_Values){
				    	    			parameter.onGUI = true;
				    	    			settingsTable.getChildren().add(settingsTable.getChildren().indexOf(module) + 1, new SubmoduleGUI((Boolean_Values) parameter, paramMap, settingsTable, selectedSection, selections));
				    	    		}else if(parameter instanceof String_Values){
				    	    			parameter.onGUI = true;
				    	    			settingsTable.getChildren().add(settingsTable.getChildren().indexOf(module) + 1, new SubmoduleGUI((String_Values) parameter));
				    	    		}else System.out.println("Yet not implemented parse for: "+ i.getClass().getSimpleName());	    		
								}
							}
						}
				    }
			    }
			    
			    if (!(i.getClass().getSimpleName().equals("UseVotingForClustering"))){
			    	i.setSingleValue(targetField.isSelected());
			    }
            }
		});
		GridPane box = new GridPane();
		box.add(targetField, 0, 0);
		box.setHgap(40);
		module.add(box, 0, 0);
		
		if (i.getClass().getSimpleName().equals("UseVotingForClustering")){
			Button add = new Button("Add");
			box.add(add,1,0);
			Button remove = new Button("Remove");
			
			TableView<ReportTableTuple> tableView = createTable();
			ObservableList<ReportTableTuple> resultTable = FXCollections.observableArrayList();
			tableView.setItems(resultTable);
			
			TableColumn<ReportTableTuple, ?> valueCol = new TableColumn<ReportTableTuple, String>("Value");
	    	valueCol.setCellValueFactory(new PropertyValueFactory("Value"));  
	    	tableView.getColumns().add(valueCol);
	
	    	add.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	            public void handle(MouseEvent e) {
	            	
	            	//addedTable.getChildren().add(new Label(targetField.getText()));
	            	i.addValue(targetField.isSelected());
	
	        		resultTable.clear();
	        		
	        		for(Object j : i.getSelectedValues()){
	        			resultTable.add(new ReportTableTuple(j, i));
	        		}
	    			
	            	System.out.println(i.getValues());
	            };
	        });
			
			remove.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
	            	
	            	try {
	            		i.removeValue(Boolean.parseBoolean(tableView.getSelectionModel().getSelectedItem().getValue()));
	
	            		resultTable.clear();
	            		
	            		for(Object j : i.getSelectedValues()){
	            			resultTable.add(new ReportTableTuple(j, i));
	            		}
	            	}catch(Exception ex){
	            		System.out.println("No values in table!");
	            	}
	            	


	    	        settingsTable.getChildren().clear();
	    	        settingsTable.setAlignment(Pos.TOP_LEFT);
	    	        settingsTable.setSpacing(5);
	    	        
	    	        if(!selectedSection.equals("Training") && !selectedSection.equals("Execution")){
	    	        	Label title = new Label(selectedSection);
	    	        	title.setFont(new Font(18));
	    	        	title.setPadding(new Insets(10));
	    	        	
	    	        	settingsTable.setAlignment(Pos.TOP_LEFT);
	    	        	
	                	settingsTable.getChildren().add(title);
	                	
	                	try{
	    	    	        for(Parameter i : selections.get(selectedSection).getParameterList())
	    	    	        	i.onGUI = false;
	                	}catch(Exception exc){}

	                	Module selected = selections.get(selectedSection); 
	                	
	        	    	List <Parameter> paramMap = selected.getParameterList();

	        	    	for(int j = 0; j < paramMap.size(); j++){
	        	    		if ((paramMap.get(j)).getDependendingFromFields().isEmpty() && paramMap.get(j).onGUI == false){
	    	    	    		if(paramMap.get(j) instanceof Discrete_Values && !(paramMap.get(j).getClass().getSimpleName().equals("ClassificationPatternType"))){ // <--- Pay attention this is a spike solution to void drawing a param in GUI
	    	    	    			paramMap.get(j).onGUI = true;
	        	    				settingsTable.getChildren().add(new SubmoduleGUI((Discrete_Values) paramMap.get(j), paramMap, settingsTable, selectedSection, selections));
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
	    	    	    				    	    			settingsTable.getChildren().add(new SubmoduleGUI((Boolean_Values) parameter, paramMap, settingsTable, selectedSection, selections));
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
	    	    	    			settingsTable.getChildren().add(new SubmoduleGUI((Boolean_Values) paramMap.get(j), paramMap, settingsTable, selectedSection, selections));
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
	    	    	    				    	    			settingsTable.getChildren().add(new SubmoduleGUI((Boolean_Values) parameter, paramMap, settingsTable, selectedSection, selections));
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
	            };
	        });
			
			tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));
			tableView.minHeightProperty().bind(tableView.prefHeightProperty());
			tableView.maxHeightProperty().bind(tableView.prefHeightProperty());
			
			module.add(tableView, 0, 1);
			module.add(remove,0,2);
			
			
			for( Object j : i.getSelectedValues()){
				resultTable.add(new ReportTableTuple(j, i));
			}
		}
		//module.setMinHeight(module.getHeight());
	}

	public SubmoduleGUI(final String_Values i) {
		module = this;
		
		setStyle();
		
		Label iLabel = new Label(i.getClass().getSimpleName());
		iLabel.setTooltip(new Tooltip(i.getTooltip()));
		
		final TextField targetField = new TextField();
		//set default value
		targetField.setText((String)i.getDefaultValue());
		
		Button add = new Button("Add");
		
		GridPane box = new GridPane();
		
		ColumnConstraints c1 = new ColumnConstraints();
		ColumnConstraints c2 = new ColumnConstraints();
		ColumnConstraints c3 = new ColumnConstraints();

		c1.setPercentWidth(24);
		c2.setPercentWidth(60);
		c3.setPercentWidth(6);
		
		box.getColumnConstraints().addAll(c1,c2,c3);
		
		box.setHgap(10);
		box.add(iLabel,0,0);
		box.add(targetField,1,1);
		box.add(add,2,1);
		module.add(box, 0,0);
		Button remove = new Button("Remove");

		TableView<ReportTableTuple> tableView = createTable();
		ObservableList<ReportTableTuple> resultTable = FXCollections.observableArrayList();
		tableView.setItems(resultTable);
		
		TableColumn<ReportTableTuple, ?> valueCol = new TableColumn<ReportTableTuple, String>("Value");
    	valueCol.setCellValueFactory(new PropertyValueFactory("Value"));  
    	tableView.getColumns().add(valueCol);
		
		add.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
            	
            	//addedTable.getChildren().add(new CurrentlyAdded (i.getClass().getSimpleName(), targetField.getText()));
            	try{
            		SimpleFormula simpleFormula = new SimpleFormula(targetField.getText());
            		new DefaultParser(simpleFormula.getLTLFormula()).parse();
            		
            		i.addValue(targetField.getText());
            		
            		
            		resultTable.clear();
            		
            		for(Object j : i.getSelectedValues()){
            			resultTable.add(new ReportTableTuple(j, i));
            		}
            		
            		System.out.println(i.getValues());
            	}catch (Exception ex)
            	{
            		Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Invalid Formula");
                    alert.setHeaderText("Invalid Formula");
                    alert.setContentText("You Inserted a invalid LTL Formula");
                    alert.showAndWait();
            	}
            };
        });
		
		remove.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
            	
            	try {
            		i.removeValue(tableView.getSelectionModel().getSelectedItem().getValue());

            		resultTable.clear();
            		
            		for(Object j : i.getSelectedValues()){
            			resultTable.add(new ReportTableTuple(j, i));
            		}
        		}catch(Exception ex){
            		System.out.println("No values in table!");
            	}
            };
        });
		

		tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));
		tableView.minHeightProperty().bind(tableView.prefHeightProperty());
		tableView.maxHeightProperty().bind(tableView.prefHeightProperty());
		
		module.add(tableView, 0, 1);
		module.add(remove, 0,2);
		
		//module.setMinHeight(module.getHeight());
		
		for( Object j : i.getSelectedValues()){
			resultTable.add(new ReportTableTuple(j, i));
		}
	}
	
	public SubmoduleGUI(final File_Path_Values i) {
		module = this; 
		
		setStyle();
		
		Label iLabel = new Label(i.getClass().getSimpleName());
		iLabel.setTooltip(new Tooltip(i.getTooltip()));
		
		Button add = new Button("Select file");
		
		GridPane box = new GridPane();
		box.setHgap(40);
		box.add(iLabel, 0,0);
		box.add(add, 1,0);
		
		module.add(box, 0,0);
		
		Button remove = new Button("Remove");

		TableView<ReportTableTuple> tableView = createTable();
		ObservableList<ReportTableTuple> resultTable = FXCollections.observableArrayList();
		tableView.setItems(resultTable);
		
		TableColumn<ReportTableTuple, ?> valueCol = new TableColumn<ReportTableTuple, String>("Value");
    	valueCol.setCellValueFactory(new PropertyValueFactory("Value"));  
    	tableView.getColumns().add(valueCol);
    	
		add.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
        	    FileChooser chooser = new FileChooser();
        	    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XES files (*.xes)", "*.xes");
        	    chooser.getExtensionFilters().add(extFilter);
        	    
        	    chooser.setTitle("Open File");
        	    File file = chooser.showOpenDialog(new Stage());
    	 
    	    	i.addValue(file.getPath());
    	    
        		resultTable.clear();
        		
        		for(Object j : i.getSelectedValues()){
        			resultTable.add(new ReportTableTuple(j, i));
        		}
        	 

        		//module.add(new Label(file.getAbsolutePath()), 0, 1, 3, 1);
        		//final TextField targetField = new TextField();
        		//module.add(targetField, 1, 0, 3, 1);
            };
        });
		
		remove.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
            	
            	try {
            		i.removeValue(tableView.getSelectionModel().getSelectedItem().getValue());

            		resultTable.clear();
            		
            		for(Object j : i.getSelectedValues()){
            			resultTable.add(new ReportTableTuple(j, i));
            		}
        		}catch(Exception ex){
            		System.out.println("No values in table!");
            	}
            };
        });
		
		tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));
		tableView.minHeightProperty().bind(tableView.prefHeightProperty());
		tableView.maxHeightProperty().bind(tableView.prefHeightProperty());
		
		module.add(tableView, 0, 1);
		module.add(remove,0, 2);
		
		//module.setMinHeight(module.getHeight());
		
		for( Object j : i.getSelectedValues()){
			resultTable.add(new ReportTableTuple(j, i));
		}
	}
	
	public SubmoduleGUI(final Directory_Path_Values i) {
		module = this;
		
		setStyle();
		
		Label iLabel = new Label(i.getClass().getSimpleName());
		iLabel.setTooltip(new Tooltip(i.getTooltip()));
		
		
		Button add = new Button("Select file");
		
		module.add(topBar(iLabel,null,null,add), 0,0);
		
		Button remove = new Button("Remove");

		TableView<ReportTableTuple> tableView = new TableView<ReportTableTuple>();
		ObservableList<ReportTableTuple> resultTable = FXCollections.observableArrayList();
		tableView.setItems(resultTable);
		
		TableColumn<ReportTableTuple, ?> valueCol = new TableColumn<ReportTableTuple, String>("Value");
    	valueCol.setCellValueFactory(new PropertyValueFactory("Value"));  
    	tableView.getColumns().add(valueCol);
    	
		add.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
        	    DirectoryChooser chooser = new DirectoryChooser();
        	    chooser.setTitle("Open File");
        	    File file = chooser.showDialog(new Stage());
        	    

        		resultTable.clear();
        		
        		for(Object j : i.getSelectedValues()){
        			resultTable.add(new ReportTableTuple(j, i));
        		}
    			
        		//module.add(new Label(file.getAbsolutePath()), 0, 1, 3, 1);
        		//final TextField targetField = new TextField();
        		//module.add(targetField, 1, 0, 3, 1);
            };
        });
		
		remove.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
            	
            	try {
            		i.removeValue(tableView.getSelectionModel().getSelectedItem().getValue());

            		resultTable.clear();
            		
            		for(Object j : i.getSelectedValues()){
            			resultTable.add(new ReportTableTuple(j, i));
            		}
            	}catch(Exception ex){
            		System.out.println("No values in table!");
            	}
            };
        });
		
		tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(1.01)));
		tableView.minHeightProperty().bind(tableView.prefHeightProperty());
		tableView.maxHeightProperty().bind(tableView.prefHeightProperty());
		
		module.add(tableView, 0, 1);
		module.add(remove, 0, 2);
		
		//module.setMinHeight(module.getHeight());
		
		for( Object j : i.getSelectedValues()){
			resultTable.add(new ReportTableTuple(j, i));
		}
	}	

}