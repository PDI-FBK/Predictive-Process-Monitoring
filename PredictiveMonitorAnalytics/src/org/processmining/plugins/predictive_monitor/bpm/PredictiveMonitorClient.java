package org.processmining.plugins.predictive_monitor.bpm;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.plugins.predictive_monitor.bpm.client_interface.GUI;

/**
 * @author willo
 *
 */
public class PredictiveMonitorClient {
	@Plugin(
		name = "Predictive Monitor Client", 
		parameterLabels = {}, 
		returnLabels = {"Predictive Monitor Client"}, 
		returnTypes = { GUI.class }, 
		userAccessible = true, 
		help = "Constructs the Predictive monitor client interface"
	)
	@UITopiaVariant(
		affiliation = "FBK", 
		author = "Williams Rizzi, Marco Federici", 
		email = "wrizzi@fbk.eu, federici@fbk.eu"
	)
	public static Object helloWorlds(PluginContext context) {//		context.getProgress().setMaximum(number);
		context.getProgress().setCaption("Constructing client interface string");
		context.getProgress().setIndeterminate(false);
		
		new Thread() {
            @Override
            public void run() {
                javafx.application.Application.launch(GUI.class);
            }
        }.start();
        GUI gui = GUI.waitForStartUpTest();
        gui.printSomething();

        context.getFutureResult(0).setLabel("Predictive Monitor Client Interface created successfully!");
        
		return gui;
	}
}