package org.processmining.plugins.predictive_monitor.bpm.classification;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.DatatypeConverter;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.PatternManager;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.ActivationVerificationGapClassifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.SatisfactionClassifier;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.TimeClassifier;
import org.processmining.plugins.predictive_monitor.bpm.utility.Print;

import weka_predictions.core.Attribute;
import weka_predictions.core.DenseInstance;
import weka_predictions.core.FastVector;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;
import weka_predictions.core.converters.ArffSaver;

@SuppressWarnings("deprecation")
public class ArffBuilder {
	private static HashMap<String, ArrayList<String>> attributeTypes = null;
	private static Print print = new Print();

	public static HashMap<String, String> generateDataValues(XTrace trace, HashMap<String, ArrayList<String>> attributeTypes){
		HashMap<String, String>dataValues = new HashMap<String, String>();
		XAttributeMap traceAttr = trace.getAttributes();
		for (String attribute : attributeTypes.keySet()) {
			dataValues.put(attribute,null);
			if (traceAttr.containsKey(attribute)){
				//it's a trace attribute
				if(!attribute.equals("Activity code") &&!attribute.equals("creator")&&!attribute.contains(":")&& !attribute.equals("description")){
					dataValues.put(attribute, traceAttr.get(attribute).toString());
				}
			} else {
				for (XEvent xEvent : trace) {
					XAttributeMap eventAttr = xEvent.getAttributes();
					if (eventAttr.containsKey(attribute))
						if(!attribute.equals("Activity code")&&!attribute.contains(":")){
							dataValues.put(attribute, eventAttr.get(attribute).toString());
					}
				}
			}	
		}
		return dataValues;
	}

	public static ArrayList<Integer> generatePatternVector(XTrace trace, ArrayList<Pattern> patterns){
		ArrayList<Integer> patternVector = PatternManager.getPatternsContainedInTrace(trace, patterns);
		return patternVector;
	}

	public static HashMap<String, ArrayList<String>> generateAttributeTypes(XTrace t, HashMap<String, ArrayList<String>> attributeTypes){
		HashMap<String, ArrayList<String>> tempAttributeTypes = new HashMap<String, ArrayList<String>>();

		XAttributeMap traceAttr = t.getAttributes();
		for(String attribute : traceAttr.keySet()){
			if(!attribute.equals("Activity code") &&!attribute.equals("creator")&&!attribute.contains(":")&& !attribute.equals("description")){
				ArrayList<String> value = null;
				if(attributeTypes.get(attribute)==null){
					try{
						if(!attribute.equals("Activity code")){
							new Integer(traceAttr.get(attribute).toString());
							value = new ArrayList<String>();
							value.add("numeric");
						}
					}catch(NumberFormatException ex){
						try{
							if(!attribute.equals("Activity code")){
								new Double(traceAttr.get(attribute).toString());
								value = new ArrayList<String>();
								value.add("numeric");
							}
						}catch(NumberFormatException exc){
							try {
								DatatypeConverter.parseDateTime(traceAttr.get(attribute).toString());
								value = new ArrayList<String>();
								value.add("date");
							} catch (IllegalArgumentException e) {
								value = new ArrayList<String>();
								value.add(traceAttr.get(attribute).toString());
							}

						}
					}
				}else{
					value = attributeTypes.get(attribute);
					if(!value.contains(traceAttr.get(attribute).toString())){
						value.add(traceAttr.get(attribute).toString());
					}
				}
				attributeTypes.put(attribute, value);

			}
		}
		for(XEvent e : t){

			String label = ((XAttributeLiteral) e.getAttributes().get("concept:name")).getValue();
			XAttributeMap eventAttr = e.getAttributes();


			for(String attribute : eventAttr.keySet()){
				if(!attribute.equals("Activity code")&&!attribute.contains(":")){
					ArrayList<String> value = null;
					if(attributeTypes.get(attribute)==null){
						try{
							if(!attribute.equals("Activity code")){
								new Integer(eventAttr.get(attribute).toString());
								value = new ArrayList<String>();
								value.add("numeric");
							}
						}catch(NumberFormatException ex){
							try{
								if(!attribute.equals("Activity code")){
									new Double(eventAttr.get(attribute).toString());
									value = new ArrayList<String>();
									value.add("numeric");
								}
							}catch(NumberFormatException exc){
								try {
									DatatypeConverter.parseDateTime(eventAttr.get(attribute).toString());
									value = new ArrayList<String>();
									value.add("date");
								} catch (IllegalArgumentException exce) {
									value = new ArrayList<String>();
									value.add(eventAttr.get(attribute).toString());
								}
							}
						}
					}else{
						value = attributeTypes.get(attribute);
						if(!value.contains(eventAttr.get(attribute).toString())){
							value.add(eventAttr.get(attribute).toString());
						}
					}
					tempAttributeTypes.put(attribute, value);
				}
				for(String attrib : tempAttributeTypes.keySet()){
					attributeTypes.put(attrib, tempAttributeTypes.get(attrib));
				}
			}
		}
		return attributeTypes;

	}

	public static HashMap<String, ArrayList<String>> getAttributeTypes() {
		return attributeTypes;
	}
	
	public static Instances instancesGenerator(XLog prefixTraceLog, ArrayList<Pattern> patterns, Classifier classifier){
		Instances dataSet = null;
		try 
		{	
			attributeTypes = new HashMap<String, ArrayList<String>>();
			for (XTrace prefixTrace : prefixTraceLog) {
				attributeTypes = generateAttributeTypes(prefixTrace, attributeTypes);
			}

			Instance instance;	
			HashMap<String, String> dataValues = null;
			ArrayList<Attribute> attributes;
			List<Instance> instances = new ArrayList<Instance>();

			for (XTrace histPrefixTrace : prefixTraceLog){
				dataValues = generateDataValues(histPrefixTrace, attributeTypes);
				ArrayList<Integer> patternVector = generatePatternVector(histPrefixTrace, patterns);
				
				instance = createInstance(dataValues, histPrefixTrace, patternVector, classifier);
				//missing satisfied value
				instances.add(instance);
			}
			attributes = createAttributes(dataValues, patterns,classifier);
			dataSet = new Instances("data", attributes, prefixTraceLog.size());
			
			for(Instance inst : instances){
				dataSet.add(inst);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSet;
	}
	
	public static Instances instancesGenerator(XLog prefixTraceLog, Classifier classifier){
		Instances dataSet = null;
		try 
		{	
			attributeTypes = new HashMap<String, ArrayList<String>>();
			for (XTrace prefixTrace : prefixTraceLog) {
				attributeTypes = generateAttributeTypes(prefixTrace, attributeTypes);
			}
			Instance instance;	
			HashMap<String, String> dataValues = null;
			ArrayList<Attribute> attributes;
			List<Instance> instances = new ArrayList<Instance>();

			for (XTrace histPrefixTrace : prefixTraceLog){
				dataValues = generateDataValues(histPrefixTrace, attributeTypes);
				instance = createInstance(dataValues, histPrefixTrace, classifier);
				instances.add(instance);
			}
			
			attributes = createAttributes(dataValues,classifier);
			dataSet = new Instances("data", attributes, prefixTraceLog.size());
			
			for(Instance inst : instances){
				dataSet.add(inst);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSet;
	}
	
	public static ArrayList<Attribute> createAttributes (HashMap<String, String> dataValues,Classifier classifier){
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		FastVector values;
		int index = 0;
		for (String attribute : attributeTypes.keySet()) {
			String attrValue = dataValues.get(attribute);
			switch(attributeTypes.get(attribute).get(0)){
			case("numeric") : 
				attributes.add(new Attribute(attribute,index));
			break;
			case("date") : 
				attributes.add(new Attribute(attribute,"yyyy-MM-dd'T'HH:mm:ssXXX",index));
			break;
			default :
				values = new FastVector();
				for (String enumType : attributeTypes.get(attribute)) {
					values.addElement(enumType);
				}
				attributes.add(new Attribute(attribute,values,index));
				break;
			}
			index++;
		}
		List lastValues = new ArrayList<String>();

		for(String label:classifier.getLabels())
		{
			lastValues.add(label);
		}
		if(classifier instanceof SatisfactionClassifier)
		{
			attributes.add(new Attribute("conformant", lastValues, index));
		}
		else if(classifier instanceof TimeClassifier)
		{
			attributes.add(new Attribute("conformantTime", lastValues, index));
		}
		else if(classifier instanceof ActivationVerificationGapClassifier)
		{
			attributes.add(new Attribute("ActivationVerificationGap", lastValues, index));
		}
		return attributes;
	}
	
	public static ArrayList<Attribute> createAttributes (HashMap<String, String> dataValues, ArrayList<Pattern> patterns, Classifier classifier){
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		FastVector values;
		int index = 0;
		for (String attribute : attributeTypes.keySet()) {
			String attrValue = dataValues.get(attribute);
			switch(attributeTypes.get(attribute).get(0)){
			case("numeric") : 
				attributes.add(new Attribute(attribute,index));
			break;
			case("date") : 
				attributes.add(new Attribute(attribute,"yyyy-MM-dd'T'HH:mm:ssXXX",index));
			break;
			default :
				values = new FastVector();
				for (String enumType : attributeTypes.get(attribute)) {
					values.addElement(enumType);
				}
				attributes.add(new Attribute(attribute,values,index));
				break;
			}
			index++;
		}
		
		ArrayList <String> tmp = new ArrayList<String>();
		tmp.add("true");
		tmp.add("false");
		
		for (int i = 0; i < patterns.size(); i++) {
			attributes.add(new Attribute("P"+i, tmp, index++));
		}

		
		List lastValues = new ArrayList<String>();

		for(String label:classifier.getLabels())
		{
			lastValues.add(label);
		}
		if(classifier instanceof SatisfactionClassifier)
		{
			attributes.add(new Attribute("conformant", lastValues, index));
		}
		else if(classifier instanceof TimeClassifier)
		{
			attributes.add(new Attribute("conformantTime", lastValues, index));
		}
		else if(classifier instanceof ActivationVerificationGapClassifier)
		{
			attributes.add(new Attribute("ActivationVerificationGap", lastValues, index));
		}		
		return attributes;
	}
	
	public static Instance createInstance (HashMap<String, String> dataValues, XTrace histPrefixTrace, Classifier classifier){
		Instance instance = new DenseInstance(attributeTypes.keySet().size()+1);
		Attribute attr=null;
		int index = 0;
		for (String attribute : attributeTypes.keySet()) {
			String attrValue = dataValues.get(attribute);
			switch(attributeTypes.get(attribute).get(0)){	
				case("numeric"):
					attr = new Attribute(attribute,index);
				if (attrValue!=null)
					instance.setValue(attr, Double.parseDouble(attrValue));
				else 
					instance.setMissing(attr);
				break;
				case("date"):
					attr = new Attribute(attribute,"yyyy-MM-dd'T'HH:mm:ssXXX",index);
				if (attrValue != null)
					try {
						instance.setValue(attr, attr.parseDate(attrValue));
					}catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
					instance.setMissing(attr);
				break;
				default :
					FastVector values = new FastVector();
					for (String enumType : attributeTypes.get(attribute)) {
						values.addElement(enumType);
					}
					if (attrValue!=null && !values.contains(attrValue)) {
						attrValue = null;
					}
					attr = new Attribute(attribute,values,index);	
					if (attrValue!=null){
						instance.setValue(attr, attrValue);
					}else {
						instance.setMissing(attr);
					}
					break;
			}
			index++;
		}
		FastVector values = new FastVector();
		for(String label:classifier.getLabels())
		{
			values.addElement(label);
		}
		if(classifier instanceof SatisfactionClassifier)
		{
			attr = new Attribute("conformant", values, index);
		}
		else if(classifier instanceof TimeClassifier)
		{
			attr = new Attribute("conformantTime", values, index);
		}
		else if(classifier instanceof ActivationVerificationGapClassifier)
		{
			attr = new Attribute("ActivationVerificationGap", values, index);
		}
		if(classifier.getClassification().get(XConceptExtension.instance().extractName(histPrefixTrace)) != null){
			instance.setValue(attr, classifier.getClassification().get(XConceptExtension.instance().extractName(histPrefixTrace)));
		}else { 
			instance.setMissing(attr);
		}
		return instance;
	}
	
	public static Instance createInstance (HashMap<String, String> dataValues, XTrace histPrefixTrace, ArrayList<Integer> patternVector, Classifier classifier){
		Instance instance = new DenseInstance(attributeTypes.keySet().size()+1);
		Attribute attr=null;
		int index = 0;
		for (String attribute : attributeTypes.keySet()) {
			String attrValue = dataValues.get(attribute);
			switch(attributeTypes.get(attribute).get(0)){	
				case("numeric"):
					attr = new Attribute(attribute,index);
				if (attrValue!=null)
					instance.setValue(attr, Double.parseDouble(attrValue));
				else 
					instance.setMissing(attr);
				break;
				case("date"):
					attr = new Attribute(attribute,"yyyy-MM-dd'T'HH:mm:ssXXX",index);
				if (attrValue != null)
					try {
						instance.setValue(attr, attr.parseDate(attrValue));
					}catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
					instance.setMissing(attr);
				break;
				default :
					FastVector values = new FastVector();
					for (String enumType : attributeTypes.get(attribute)) {
						values.addElement(enumType);
					}
					if (attrValue!=null && !values.contains(attrValue)) {
						attrValue = null;
					}
					attr = new Attribute(attribute,values,index);	
					if (attrValue!=null){
						instance.setValue(attr, attrValue);
					}else {
						instance.setMissing(attr);
					}
					break;
			}
			index++;
		}
		//not sure it works
		int i = 0;
		for(Integer pattern : patternVector){
			if (pattern > 0)
				attr = new Attribute("p"+i++, "true", index++);
			else 
				attr = new Attribute("p"+i++, "false", index++);
			instance.setValue(attr, attr.name());
		}
		
		String sat = classifier.getClassification().get(XConceptExtension.instance().extractName(histPrefixTrace));
		FastVector values = new FastVector();
		for(String label:classifier.getLabels())
		{
			values.addElement(label);
		}
		if(classifier instanceof SatisfactionClassifier)
		{
			attr = new Attribute("conformant", values, index);
		}
		else if(classifier instanceof TimeClassifier)
		{
			attr = new Attribute("conformantTime", values, index);
		}
		else if(classifier instanceof ActivationVerificationGapClassifier)
		{
			attr = new Attribute("ActivationVerificationGap", values, index);
		}
		if(classifier.getClassification().get(XConceptExtension.instance().extractName(histPrefixTrace)) != null){
			instance.setValue(attr, classifier.getClassification().get(XConceptExtension.instance().extractName(histPrefixTrace)));
		}else { 
			instance.setMissing(attr);
		}
		return instance;	
	}
	
	public static Instance getTraceInstance(XTrace currentTrace, ArrayList<Pattern> patterns, HashMap<String, ArrayList<String>> attributeTypes, HashMap<String, String> variables){
		Instance instance = new DenseInstance(attributeTypes.size()+patterns.size()+1);
		//Instance instance = new Instance(attributeTypes.size()+patterns.size()+1);
		
		HashMap<String, String> dataValues = new HashMap<String, String>();
		for (String attribute : attributeTypes.keySet()) {
			if (variables.get(attribute)!=null)
				dataValues.put(attribute, variables.get(attribute));
			else
				dataValues.put(attribute, null);
		}

		
		
		int index = 0;
		for (String attribute : attributeTypes.keySet()) {
			print.thatln(instance.numAttributes()+" "+instance);
			
			String attrValue = dataValues.get(attribute);

			if (attributeTypes.get(attribute).get(0).equalsIgnoreCase("numeric")){
				Attribute attr = new Attribute(attribute,index);
				if (attrValue!=null)
					instance.setValue(attr, Double.parseDouble(attrValue));
				else 
					instance.setMissing(attr);

			}
			else {
				if (attributeTypes.get(attribute).get(0).equalsIgnoreCase("date")){
					Attribute attr = new Attribute(attribute,"yyyy-MM-dd'T'HH:mm:ssXXX",index);						
					if (attrValue!=null){
						try {
							instance.setValue(attr, attr.parseDate(attrValue));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else {
					//List<String> values = new ArrayList();
					FastVector values = new FastVector();
					for (String enumType : attributeTypes.get(attribute)) {
						//values.add(enumType);
						values.addElement(enumType);
					}
					print.thatln(values.size()+" "+values);
					print.thatln("ATTR_VALUE "+attrValue);
					if (attrValue!=null && !values.contains(attrValue)) {
						//values.add(attrValue);
						System.out.println("MISSING ENUM "+attrValue);
						attrValue = null;
					}
					Attribute attr = new Attribute(attribute,values,index);			
					if (attrValue!=null)
						instance.setValue(attr, attrValue);
					else 
						instance.setMissing(attr);
				}
			}
			index++;
		}
		ArrayList<Integer> patternVector = generatePatternVector(currentTrace, patterns);
		for (int i = 0; i < patternVector.size(); i++) {
			

			Integer pattern = patternVector.get(i);
			String patternId = "P"+i;
			String attrValue;
			if (pattern>0)
				attrValue="true";
			else
				attrValue="false";
			FastVector values = new FastVector();
			values.addElement("true");
			values.addElement("false");
			

			Attribute attr = new Attribute(patternId,values,index);			
			if (attrValue!=null)
				instance.setValue(attr, attrValue);

			index++;
		}
		
		
		print.thatln(instance.numAttributes()+" "+instance);
		return instance;

	}
	
	private static XTrace getPrefixedTrace(XTrace completeTrace, String prefixString){
		String[] prefixes = prefixString.split(";");
		Vector<String> prefixVector = new Vector<String>();
		for (int i = 0; i < prefixes.length; i++) {
			prefixVector.add(prefixes[i]);
		}
		 XFactory fR = XFactoryRegistry.instance().currentDefault();
		XTrace prefixTrace = fR.createTrace();
		prefixTrace.setAttributes(completeTrace.getAttributes());
		Iterator<XEvent> it = completeTrace.iterator();
		boolean end = false;
		while (it.hasNext() && !end){
			XEvent event = it.next();
			if (prefixVector.contains(XConceptExtension.instance().extractName(event))){
				XEvent pEvent = (XEvent) event.clone();
				prefixTrace.add(pEvent);
			} else 
				end = true;
		}
		return prefixTrace;

	}
	
	public static boolean isUmbalanced(XLog log, HashMap<String, Boolean> histTraceSatisfaction, double threshold){
		boolean umbalanced = false;
		int positive = 0;
		int negative = 0;
		for (XTrace trace : log) {
			String key = XConceptExtension.instance().extractName(trace);
			if (histTraceSatisfaction.get(key))
				positive++;
			else 
				negative++;
		}
		int tot = positive + negative;
		double max = (double)Math.max(positive, negative);

		if (max/tot>=threshold) 
			umbalanced = true;
		return umbalanced;
	}
	
	public static List<XLog> filterClusterLog(List<XLog> logs, HashMap<String, Boolean> histTraceSatisfaction, double threshold){
		ArrayList<XLog> filteredClusterLogs = new ArrayList<XLog>();
		for (XLog log : logs) {
			if (!isUmbalanced(log, histTraceSatisfaction, threshold)){
				filteredClusterLogs.add(log);
			}
			
		}
		return filteredClusterLogs;
	}

}
