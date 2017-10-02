package org.processmining.plugins.predictive_monitor.bpm.encoding;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.KMPMatcher;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.utility.TraceConverter;

import weka_predictions.core.Attribute;
import weka_predictions.core.DenseInstance;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;

public class FrequencyBasedEncoder extends Encoder {
	// TraceClusterer
	Map<String, Integer> alphabetMap = new HashMap<String, Integer>();
	HashMap<Integer, XTrace> traceMapping = new HashMap<Integer, XTrace>();
	Instances encodedTraces = null;

	// TraceClusterer

	public HashMap<Integer, XTrace> getTraceMapping() {
		return traceMapping;
	}

	public Instances getEncodedTraces() {
		return encodedTraces;
	}

	public Map<String, Integer> getAlphabetMap() {
		return alphabetMap;
	}

	public void encodeTraces(XLog logTracesToEncode) {

		for (XTrace trace : logTracesToEncode) {
			for (XEvent event : trace) {
				String eventLabel = XConceptExtension.instance().extractName(
						event);
				Integer index = alphabetMap.get(eventLabel);
				if (index == null) {
					index = alphabetMap.size();
					alphabetMap.put(eventLabel, index);
				}
			}
		}

		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for (int i = 0; i < alphabetMap.size(); i++) {
			attributes.add(null);
		}
		for (String key : alphabetMap.keySet()) {
			Attribute attr = new Attribute(key, alphabetMap.get(key));
			// attributeArray[alphabetMap.get(key)] = attr;
			attributes.set(alphabetMap.get(key), attr);
		}

		encodedTraces = new Instances("DATA", attributes,
				logTracesToEncode.size());

		
		
		int i = 0;
		for (XTrace trace : logTracesToEncode) {
			Instance instance = new DenseInstance(alphabetMap.size());
			// initialize alphabet map with 0
			for(int j = 0; j < alphabetMap.size(); j++){
				instance.setValue(j, new Double(0));
			}
			for (XEvent event : trace) {
				String eventLabel = XConceptExtension.instance().extractName(
						event);
				Integer index = alphabetMap.get(eventLabel);
				Double value = instance.value(index);
				if (value.isNaN())
					instance.setValue(index, new Double(1));
				else
					instance.setValue(index, value + 1);
			}
			encodedTraces.add(instance);
			traceMapping.put(i, trace);
			i++;
		}

	}

	public void encodeTracesBasedOnPatternOccurrence(XLog logTracesToEncode,
			List<Pattern> patterns) {

		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for (int i = 0; i < patterns.size(); i++) {
			attributes.add(new Attribute(patterns.get(i).toString(), i));
		}

		encodedTraces = new Instances("DATA", attributes,
				logTracesToEncode.size());

		int i = 0;
		KMPMatcher kmpMatcher = new KMPMatcher();
		for (XTrace trace : logTracesToEncode) {
			Instance instance = new DenseInstance(patterns.size());
			List<String> traceAsList = new ArrayList<String>();
			for (XEvent event : trace) {
				String eventLabel = XConceptExtension.instance().extractName(
						event);
				traceAsList.add(eventLabel);
			}
			for (int index = 0; index < patterns.size(); index++) {
				if (kmpMatcher.match(traceAsList, patterns.get(index)
						.getItems()) >= 0) {
					instance.setValue(index, 1);
				} else {
					instance.setValue(index, 0);
				}
			}
			encodedTraces.add(instance);
			traceMapping.put(i, trace);
			i++;
		}

	}

	public void encodeTracesBasedOnPatternFrequency(XLog logTracesToEncode,
			List<Pattern> patterns) {

		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for (int i = 0; i < patterns.size(); i++) {
			attributes.add(new Attribute(patterns.get(i).toString(), i));
		}

		encodedTraces = new Instances("DATA", attributes,
				logTracesToEncode.size());

		int i = 0;
		KMPMatcher kmpMatcher = new KMPMatcher();
		for (XTrace trace : logTracesToEncode) {
			Instance instance = new DenseInstance(patterns.size());
			List<String> traceAsList = new ArrayList<String>();
			for (XEvent event : trace) {
				String eventLabel = XConceptExtension.instance().extractName(
						event);
				traceAsList.add(eventLabel);
			}
			for (int index = 0; index < patterns.size(); index++) {
				int matches = kmpMatcher.countOccurrencies(traceAsList,
						patterns.get(index).getItems());
				instance.setValue(index, matches);
			}
			encodedTraces.add(instance);
			traceMapping.put(i, trace);
			i++;
		}

	}
	
	public Map<String, Double> encodeTraceBasedOnEventAndPatternFrequency(
			XTrace trace, List<Pattern> patterns) {
		Map<String, Double> freqs = new HashMap<String, Double>();
		KMPMatcher kmpMatcher = new KMPMatcher();
		List<String> traceAsList = new ArrayList<String>();
		for (XEvent event : trace) {
			String eventLabel = XConceptExtension.instance().extractName(event);
			traceAsList.add(eventLabel);
			Double count = freqs.get(eventLabel + "|1");
			if (count == null) {
				freqs.put(eventLabel + "|1", 1.0);
			} else {
				freqs.put(eventLabel + "|1", count + 1);
			}
		}
		for (Pattern pattern : patterns) {
			if (pattern.getItems().size() > 1) {
				String patternStr = pattern.toString();
				String patternLabel = "";
				int matches = kmpMatcher.countOccurrencies(traceAsList,
						pattern.getItems());
				for (int i = 0; i < pattern.getItems().size(); i++) {
				patternLabel = patternStr + "|" + (i + 1);
				freqs.put(patternLabel, 1.0 * matches);
				}
			}
		}
		return freqs;
	}


	
	public void encodeTracesBasedOnEventAndPatternFrequency(XLog logTracesToEncode,
			List<Pattern> patterns) {
		
		for (XTrace trace : logTracesToEncode) {
			for (XEvent event : trace) {
				String eventLabel = XConceptExtension.instance().extractName(
						event);
				eventLabel += "|1";
				Integer index = alphabetMap.get(eventLabel);
				if (index == null) {
					index = alphabetMap.size();
					alphabetMap.put(eventLabel, index);
				}
			}
			
		}
		for (Pattern pattern : patterns) {
			String patternStr = pattern.toString();
			String patternLabel = "";
			for (int i = 0; i < pattern.getItems().size(); i++) {
				patternLabel = patternStr + "|" + (i + 1);
				Integer index = alphabetMap.get(patternLabel);
				if (index == null) {
					index = alphabetMap.size();
					alphabetMap.put(patternLabel, index);
				}
			}
			
		}

		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for (int i = 0; i < alphabetMap.size(); i++) {
			attributes.add(null);
		}
		for (String key : alphabetMap.keySet()) {
			Attribute attr = new Attribute(key, alphabetMap.get(key));
			attributes.set(alphabetMap.get(key), attr);
		}

		encodedTraces = new Instances("DATA", attributes,
				logTracesToEncode.size());

		int i = 0;
		KMPMatcher kmpMatcher = new KMPMatcher();
		for (XTrace trace : logTracesToEncode) {
			Instance instance = new DenseInstance(alphabetMap.size());
			List<String> traceAsList = new ArrayList<String>();
			for (XEvent event : trace) {
				String eventLabel = XConceptExtension.instance().extractName(
						event);
				traceAsList.add(eventLabel);
			}
			for (String key : alphabetMap.keySet()) {
				Integer index = alphabetMap.get(key);
				List<String> pattern = new ArrayList<String>(Arrays.asList(key.split("\\|")));
				if (NumberUtils.isNumber(pattern.get(pattern.size()-1))) {
				     pattern.remove(pattern.size()-1);
				    }
				int matches = kmpMatcher
						.countOccurrencies(traceAsList, pattern);
				instance.setValue(index, matches);
			}
			encodedTraces.add(instance);
			traceMapping.put(i, trace);
			i++;
		}

	}

	public void computeTraceMapping(XLog log) {
		int i = 0;
		for (XTrace trace : log) {
			traceMapping.put(i, trace);
			i++;
		}
	}

	public Instance encodeTrace(XTrace trace,
			Map<String, Integer> alphabetMap) {
		Instance instance = new DenseInstance(alphabetMap.size());
		// initialize alphabet map with 0
		for(int j = 0; j < alphabetMap.size(); j++){
			instance.setValue(j, new Double(0));
		}
		for (XEvent event : trace) {
			String eventLabel = XConceptExtension.instance().extractName(event);
			Integer index = alphabetMap.get(eventLabel);
			if(index != null)
			{
				Double value = instance.value(index);
				if (value.isNaN())
					instance.setValue(index, new Double(1));
				else
				instance.setValue(index, value + 1);
			}
		}
		return instance;

	}

	private String[] convertInstanceToArray(Instance encodedTrace) {
		int numAttributes = encodedTrace.numAttributes();
		String[] encodedTraceArray = new String[numAttributes];
		for (int i = 0; i < numAttributes; i++) {
			encodedTraceArray[i] = encodedTrace.stringValue(i);
		}
		return encodedTraceArray;
	}

	public String[] encodeTraceAsArray(XTrace trace) {
		return TraceConverter.getTraceAsStringArray(trace);

	}

	public void writeTraceInstances(XLog log, String filePath) {
		int max = 100;

		try {
			ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
			int maxLength = -1;
			int j = 0;
			for (XTrace trace : log) {
				if (j < max) {
					int i = 0;
					ArrayList<String> string = new ArrayList<String>();
					for (XEvent event : trace) {
						String eventLabel = XConceptExtension.instance()
								.extractName(event);
						Integer index = alphabetMap.get(eventLabel);
						if (index == null) {
							index = alphabetMap.size();
							alphabetMap.put(eventLabel, index);
						}
						string.add(index.toString());
						i++;
					}
					if (i > maxLength)
						maxLength = i;
					list.add(string);
					j++;
				}
			}
			FileWriter fW = new FileWriter(new File(filePath));
			for (int i = 0; i < maxLength; i++) {
				if (i > 0)
					fW.write(",");
				fW.write("A" + i);
			}
			fW.write("\n");
			for (ArrayList<String> string : list) {
				for (int i = 0; i < maxLength; i++) {
					if (i > 0)
						fW.write(",");
					if (i < string.size())
						fW.write(string.get(i));
				}
				fW.write("\n");
			}
			fW.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void writeLogFrequencies(Instances encodedTraces,
			String trainingFilePath) {
		try {
			FileWriter fW = new FileWriter(new File(trainingFilePath));

			Enumeration<Attribute> alphabetNames = encodedTraces
					.enumerateAttributes();
			boolean first = true;

			while (alphabetNames.hasMoreElements()) {
				if (!first)
					fW.write(";");
				Attribute alphabetElement = alphabetNames.nextElement();
				fW.write(alphabetElement.name());
				first = false;
			}
			fW.write("\n");

			int max = encodedTraces.numAttributes();
			first = true;
			for (Instance encodedTrace : encodedTraces) {
				for (int i = 0; i < max; i++) {
					if (!first)
						fW.write(";");
					fW.write(encodedTrace.toString(i));
					first = false;

				}
				fW.write("\n");
			}

			fW.flush();
			fW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*public void writeLogFrequencies(Instances encodedTraces) {
		writeLogFrequencies(encodedTraces,
				ServerConfigurationClass.defaultFrequencyTracesFilePath);
	}*/

	public Instance encodeTraceBasedOnEventAndPatternFrequency(XTrace trace,
			ArrayList<Pattern> patterns, Map<String, Integer> alphabetMap) {
		int i = 0;
		KMPMatcher kmpMatcher = new KMPMatcher();
		Instance instance = new DenseInstance(alphabetMap.size());
		List<String> traceAsList = new ArrayList<String>();
		for (XEvent event : trace) {
			String eventLabel = XConceptExtension.instance().extractName(
					event);
			traceAsList.add(eventLabel);
		}
		for (String key : alphabetMap.keySet()) {
			Integer index = alphabetMap.get(key);
			List<String> pattern = new ArrayList<String>(Arrays.asList(key.split("\\|")));
			if (NumberUtils.isNumber(pattern.get(pattern.size()-1))) {
			     pattern.remove(pattern.size()-1);
			    }
			int matches = kmpMatcher
					.countOccurrencies(traceAsList, pattern);
			instance.setValue(index, matches);
		}
		return instance;
		
	}

}
