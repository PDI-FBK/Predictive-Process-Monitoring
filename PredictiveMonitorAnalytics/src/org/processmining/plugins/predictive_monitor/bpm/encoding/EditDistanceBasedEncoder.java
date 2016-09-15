package org.processmining.plugins.predictive_monitor.bpm.encoding;

import java.util.ArrayList;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class EditDistanceBasedEncoder extends Encoder{
	private XLog log;
	
	public void encodeTraces(XLog log){
		
	}
	
	public XLog getLog(){
		return log;
	}
	
	public ArrayList<String> encodeTrace(XTrace trace){
		ArrayList<String> encodedTrace = new ArrayList<String>();
		for (XEvent e : trace) {
			encodedTrace.add(XConceptExtension.instance().extractName(e));
		}
		return encodedTrace;
	}
	
}
