package org.processmining.plugins.predictive_monitor.bpm.encoding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import weka_predictions.core.Attribute;
import weka_predictions.core.DenseInstance;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;

public class SequenceBasedEncoder extends Encoder{
	ArrayList<Attribute> attributes = new ArrayList();
	int maxSizeThreshold = 100; 
	HashMap<Integer, XTrace> traceMapping = new HashMap<Integer, XTrace>();
	Instances encodedTraces = null;
	int maxSize = 0;

	public SequenceBasedEncoder() {
	}
	
	public SequenceBasedEncoder(int maxSizeThreshold) {
		this.maxSizeThreshold = maxSizeThreshold;
	}
	
	
	public Instances encodeTraces(XLog logTracesToEncode){

		Vector attributeValues = new Vector();

		try {
			maxSize = 0;
			for (XTrace trace : logTracesToEncode) {
				for (XEvent event : trace) {
					XAttributeMap map = trace.getAttributes();
					String eventLabel = XConceptExtension.instance().extractName(event);				
					if (!attributeValues.contains(eventLabel))
						attributeValues.addElement(eventLabel);
				}
				if (trace.size()>maxSize) {
					if (trace.size()<maxSizeThreshold)
						maxSize  = trace.size();
					else 
						maxSize = maxSizeThreshold;
				}
			}
			
			for (int p = 0; p <= maxSize; p++) {
				Attribute attr = new Attribute("p_"+p, attributeValues);
				attributes.add(attr);
			}
			encodedTraces = new Instances("DATA", attributes, logTracesToEncode.size());
			int i = 0;
			for (XTrace trace : logTracesToEncode) {
				Instance instance = new DenseInstance(attributes.size());
				instance.setDataset(encodedTraces);
				int p = 0;
				//for (XEvent event : trace) {
				while (p<trace.size() && p<maxSize){
					XEvent event = trace.get(p);
					String eventLabel = XConceptExtension.instance().extractName(
							event);
					instance.setValue(p, eventLabel);
					p++;
				}
				encodedTraces.add(instance);
				traceMapping.put(i, trace);
				i++;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return encodedTraces;
		
	}
	
	

	
/*	public void encodeTraces(XLog logTracesToEncode) {
		int maxSize=0;
		for (XTrace trace : logTracesToEncode) {
			int p=0; 
			//ArrayList<String> attributeValues = new ArrayList<String>();
			for (XEvent event : trace) {
				String eventLabel = XConceptExtension.instance().extractName(event);
				if (!attributeValues.contains(eventLabel))
					attributeValues.add(eventLabel);
				if (attributes.size()-1<p){
					Attribute attr = new Attribute("p_"+p, p);
					attributes.add(attr);
				}
				p++;
			}
			if(p>maxSize)
				maxSize=p;
		}
		
		for (int p = 0; p <= maxSize; p++) {
			Attribute attr = new Attribute("p_"+p, attributeValues);
			attributes.add(attr);
		}
		encodedTraces = new Instances("DATA", attributes,
				logTracesToEncode.size());
		int i = 0;
		for (XTrace trace : logTracesToEncode) {
			//Instance instance = new DenseInstance(attributeValues.size());
			Instance instance = new DenseInstance(attributes.size());
			int p = 0;
			for (XEvent event : trace) {
				String eventLabel = XConceptExtension.instance().extractName(
						event);
				instance.setDataset(encodedTraces);
				instance.setValue(p, eventLabel);
				p++;
			}
			encodedTraces.add(instance);
			traceMapping.put(i, trace);
			i++;
		}
		
	}

	public ArrayList<String> getAttributeValues() {
		return attributeValues;
	}*/

	public HashMap<Integer, XTrace> getTraceMapping() {
		return traceMapping;
	}

	public Instances getEncodedTraces() {
		return encodedTraces;
	}
	
/*	public Instance encodeTrace(XTrace trace) {
		Instance instance = new DenseInstance(attributes.size());
		int p=0;
		for (Attribute attribute : attributes) {
			XEvent event = trace.get(p);
			String eventLabel = XConceptExtension.instance().extractName(event);
			if (eventLabel!=null)
				instance.setValue(p, eventLabel);
			else
				instance.setValue(p, null);
		}
		return instance;

	}*/
	
	public Instance encodeTrace(XTrace trace, int maxSize, ArrayList<Attribute> attributes, Instances encodedTraces) {
		Instance instance = new DenseInstance(attributes.size());
		instance.setDataset(encodedTraces);
		int p=0;
		//for (XEvent event : trace) {
		while (p<trace.size() && p<maxSize){
			XEvent event = trace.get(p);
			String eventLabel = XConceptExtension.instance().extractName(
					event);
			instance.setValue(p, eventLabel);
			p++;
		}		
		return instance;

	}

	public int getMaxSize() {
		return maxSize;
	}

	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}
	
	
	
}
