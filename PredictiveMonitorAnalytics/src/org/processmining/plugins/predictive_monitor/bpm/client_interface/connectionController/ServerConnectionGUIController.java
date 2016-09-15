package org.processmining.plugins.predictive_monitor.bpm.client_interface.connectionController;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.processmining.plugins.predictive_monitor.bpm.client_interface.GUI;
import org.processmining.plugins.predictive_monitor.bpm.operational_support.Connection;
import org.processmining.plugins.predictive_monitor.bpm.replayer.ConfigurationSender;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ServerConnectionGUIController implements Initializable {
	@FXML
	private GridPane mainGrid;
	@FXML
	private TextField IP0;
	@FXML
	private TextField IP1;
	@FXML
	private TextField IP2;
	@FXML
	private TextField IP3;
	@FXML
	private TextField portNumber;
	@FXML
	private Button testConnection;

	/**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
		testConnection.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	        public void handle(MouseEvent e) {	        	
	        	Integer ip0 = null;
    			Integer ip1 = null;
    			Integer ip2 = null;
    			Integer ip3 = null;
    			Integer port = null;
	        	try{
        			ip0 = Integer.parseInt(IP1.getText());
        			ip1 = Integer.parseInt(IP2.getText());
        			ip2 = Integer.parseInt(IP3.getText());
        			ip3 = Integer.parseInt(IP3.getText());
        			port = Integer.parseInt(portNumber.getText());
        		}catch(Exception ex){
        			System.out.println("Please enter a valid integer!");
        		}
        		
        		if(ip0 != null && ip1 != null && ip2 != null && ip3 != null && port != null
        			&& ip0 >= 0 && ip0 <= 255 && ip1 >= 0 && ip1 <= 255 && ip2 >= 0 && ip2 <= 255 && ip3 >= 0 && ip3 <= 255){
        			String ip = new String(IP0.getText().concat("."+IP1.getText().concat("."+IP2.getText().concat("."+IP3.getText()))));
        			
        			/*Verify and connect to server*/
        			Connection.setPort(port);
        			Connection.setServerIp(ip);
        			List<String> availableTrainingFiles = null;
        			try{
        				availableTrainingFiles = Connection.connect();
    				}catch (Exception exc){
    					exc.printStackTrace();
					}
        			
        			GUI.trainingFilePath.addAll(availableTrainingFiles);
        			GUI.configurationSender = new ConfigurationSender();
        			
		        	if (availableTrainingFiles != null){
		        		Stage settingsStage = new Stage();	        		
		        		
		        		FXMLLoader loader = new FXMLLoader();
		                loader.setLocation(this.getClass().getResource("../settingsPage/Settings.fxml"));
		                
		                loader.setClassLoader(this.getClass().getClassLoader());
	
		                Parent parent = null;
						try {
							parent = (Parent) loader.load();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
		                
		                Scene scene = new Scene(parent);
		                settingsStage.setScene(scene);
		                settingsStage.getIcons().add(new Image(getClass().getResourceAsStream("../mockup1.jpg")));
		                settingsStage.setTitle("Predictive Monitoring");
		                settingsStage.show();
		                
		                ((Node)(e.getSource())).getScene().getWindow().hide();
		                ((Node)(e.getSource())).getScene().getWindow().setOnCloseRequest(e1 -> System.exit(0));
		        	}else{
		        		System.out.println("Please check server status");
		        	}
		        }
	        }
		});
	}
}
