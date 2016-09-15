package org.processmining.plugins.predictive_monitor.bpm.client_interface;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.processmining.plugins.predictive_monitor.bpm.replayer.ConfigurationSender;
import org.processmining.plugins.predictive_monitor.bpm.replayer.GlobalResultListener;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Willo
 */
public class GUI extends Application {
	
	public static Set<String> trainingFilePath;
	public static ConfigurationSender configurationSender;
	public static GlobalResultListener globalResultListener;
	public static Set<String> runIDKeySet;
	public static Map <String, Map<String, Object>> unfoldedValues;
    
    @Override
    public void start(Stage stage) throws Exception {
    	
    	trainingFilePath = new TreeSet<String>();
    	
    	FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("connectionController/ServerConnectionGUI.fxml"));
        
        loader.setClassLoader(this.getClass().getClassLoader());
        
        Parent parent = loader.load();
        
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("mockup1.jpg")));
        stage.setTitle("Predictive Monitoring");
        /*Should kill application on stage close*/
        stage.setOnCloseRequest(e -> System.exit(0));
        
        stage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
