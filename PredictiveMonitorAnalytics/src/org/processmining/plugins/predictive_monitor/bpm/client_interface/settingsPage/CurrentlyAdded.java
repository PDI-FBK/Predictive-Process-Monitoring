package org.processmining.plugins.predictive_monitor.bpm.client_interface.settingsPage;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CurrentlyAdded extends HBox {
	
	Label type;
	Label value;
	static Button remove = new Button("Remove");
	
	HBox actual;
	
	//sould i get the reference of the value added?
	//fix design =)
	public CurrentlyAdded(String type, String value){
		super();
		getChildren().add(new Label (type));
		getChildren().add(new Label(value));
		getChildren().add(remove);
		System.out.println("Adding values");
		this.setPadding(new Insets(5));
	}

}
