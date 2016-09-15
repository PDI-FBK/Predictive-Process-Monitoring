package org.processmining.plugins.predictive_monitor.bpm.client_interface.resultPage;

public class MyBoolean{
	
	private boolean mybool;
	
	public MyBoolean (boolean val){
		mybool = val;
	}
	
	public void setValue(boolean val){
		mybool = val;
	}
	
	public boolean getValue(){
		return mybool;
	}
}
