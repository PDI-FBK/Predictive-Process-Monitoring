package org.processmining.plugins.predictive_monitor.bpm.client_interface.settingsPage;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class ReportTableTuple {
	private Object value;

    private Button removeAction;

    public ReportTableTuple(Object selectedValue, Parameter i) {
    	value = selectedValue;
    	removeAction = new Button("Remove");
    	removeAction.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
            	if(!(i.removeValue(value)))
            		System.out.println("Something has gone wrong with parse and remove of: " + value + "in: " + i.getClass().getCanonicalName());
            }
        });
    }

	public String getValue() {
		return value.toString();
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Button getRemoveAction() {
		return removeAction;
	}

	public void setRemoveAction(Button removeAction) {
		this.removeAction = removeAction;
	}
}
