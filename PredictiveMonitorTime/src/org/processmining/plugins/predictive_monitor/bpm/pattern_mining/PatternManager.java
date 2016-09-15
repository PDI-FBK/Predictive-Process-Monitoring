package org.processmining.plugins.predictive_monitor.bpm.pattern_mining;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.in.XMxmlGZIPParser;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.clustering.EditDistance;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.TracePrefix;

public class PatternManager {

	private static double similThresh = 0.5;
	private static double minNumbTraces = 30;
	
	public static ArrayList<Pattern> orderPatternsByEarliness(ArrayList<Pattern> unorderedPatterns, XLog log){
		ArrayList<Pattern> orderedPatterns = new ArrayList<Pattern>();
		HashMap<Integer, Double> earlinessMap = new HashMap<Integer, Double>();
		HashMap<Integer, Double> frequencyMap = new HashMap<Integer, Double>();
		for(XTrace trace : log){
			int pos = 0;
			HashMap<Integer,Integer> patternsBeingAnalyzed = new HashMap<Integer,Integer>();
			ArrayList<Integer> patternsAlreadyAnalyzed = new ArrayList<Integer>();

			for(XEvent event : trace){
				String eventName = XConceptExtension.instance().extractName(event);
				for(Pattern pattern : unorderedPatterns){
					if(!patternsAlreadyAnalyzed.contains(unorderedPatterns.indexOf(pattern))){
						if(pattern.getItems().get(0).equals(eventName)){
							patternsBeingAnalyzed.put(unorderedPatterns.indexOf(pattern),0);
						}
					}
				}
				for(Integer id : patternsBeingAnalyzed.keySet()){
					if(!patternsAlreadyAnalyzed.contains(id)){
						if(unorderedPatterns.get(id).getItems().get(patternsBeingAnalyzed.get(id)).equals(eventName)){
							int newIndex = patternsBeingAnalyzed.get(id)+1;
							patternsBeingAnalyzed.put(id, newIndex);
							if(newIndex==unorderedPatterns.get(id).getItems().size()){
								patternsAlreadyAnalyzed.add(id);
								if(!earlinessMap.containsKey(id)){
									earlinessMap.put(id, (double)pos-newIndex+1);
									frequencyMap.put(id, 1.);
								}else{
									earlinessMap.put(id, earlinessMap.get(id)+pos-newIndex+1);
									frequencyMap.put(id, frequencyMap.get(id)+1.);
								}
							}
						}						
					}
				}
				pos++;
			}
		}
		for(int id : earlinessMap.keySet()){
			earlinessMap.put(id, earlinessMap.get(id)/frequencyMap.get(id));
		}
		for(int id : earlinessMap.keySet()){
			boolean found = false;
			Pattern patternToAdd = unorderedPatterns.get(id);
			patternToAdd.setEarlinessDegree(earlinessMap.get(id));
			for(Pattern pattern : orderedPatterns){
				if(pattern.getEarlinessDegree()>earlinessMap.get(id)){
					orderedPatterns.add(orderedPatterns.indexOf(pattern)+1, patternToAdd);
					found = true;
					break;
				}
			}
			if(!found){
				orderedPatterns.add(patternToAdd);
			}
		}
		return orderedPatterns;
	}




	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String inputLogFileName = "C:\\Users\\Fabrizio\\Desktop\\SellingProcess.mxml";
		XLog log = null;

		if(inputLogFileName.toLowerCase().contains("mxml.gz")){
			XMxmlGZIPParser parser = new XMxmlGZIPParser();
			if(parser.canParse(new File(inputLogFileName))){
				try {
					log = parser.parse(new File(inputLogFileName)).get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else if(inputLogFileName.toLowerCase().contains("mxml")){
			XMxmlParser parser = new XMxmlParser();
			if(parser.canParse(new File(inputLogFileName))){
				try {
					log = parser.parse(new File(inputLogFileName)).get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(inputLogFileName.toLowerCase().contains("xes.gz")){
			XesXmlGZIPParser parser = new XesXmlGZIPParser();
			if(parser.canParse(new File(inputLogFileName))){
				try {
					log = parser.parse(new File(inputLogFileName)).get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else if(inputLogFileName.toLowerCase().contains("xes")){
			XesXmlParser parser = new XesXmlParser();
			if(parser.canParse(new File(inputLogFileName))){
				try {
					log = parser.parse(new File(inputLogFileName)).get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		ArrayList<Pattern> unorderedPatterns = new ArrayList<Pattern>();
		Pattern p = new Pattern();
		ArrayList<String> items = new ArrayList<String>();
		items.add("Receive Order");
		items.add("Ship Products");
		p.setItems(items);
		unorderedPatterns.add(p);

		p = new Pattern();
		items = new ArrayList<String>();
		items.add("Ship Products");
		items.add("Receive Payment");
		p.setItems(items);
		unorderedPatterns.add(p);

		p = new Pattern();
		items = new ArrayList<String>();
		items.add("Send Invoice");
		items.add("Archive Order");
		p.setItems(items);
		unorderedPatterns.add(p);
		ArrayList<Pattern> orderedPatterns = PatternManager.orderPatternsByEarliness(unorderedPatterns, log);
//		if(ServerConfigurationClass.printDebug){
//			for(Pattern pattern : orderedPatterns){
//				for(String item : pattern.getItems()){
//					System.out.print(item+ " ");
//				}
//				System.out.println(pattern.getEarlinessDegree());
//			}
//		}
		p = new Pattern();
		items = new ArrayList<String>();
		items.add("Send Invoice");
		items.add("Ship Products");
		p.setItems(items);
//		if(ServerConfigurationClass.printDebug)System.out.println(XConceptExtension.instance().extractName(PatternManager.getTracesContainingPattern(p, log).get(0)));

	}

	public static ArrayList<XTrace> getTracesContainingPattern(Pattern pattern, XLog log){
		ArrayList<XTrace> output = new ArrayList<XTrace>();
		System.out.println("SIZE:"+pattern.getItems().size());
		for(XTrace trace : log){
			boolean underAnalysis = false;
			int pos = -1;
			for(XEvent event : trace){
				String eventName = XConceptExtension.instance().extractName(event);
				if(underAnalysis){
					pos++;
					System.out.println(pos);
					if(!pattern.getItems().get(pos).equals(eventName)){
						underAnalysis = false;
					}
					if(pattern.getItems().get(pattern.getItems().size()-1).equals(eventName)){
						underAnalysis = false;
						output.add(trace);
						break;
					}
				}
				if(pattern.getItems().get(0).equals(eventName)){
					pos = 0;
					underAnalysis = true;
				}
			}
		}
		return output;
	}
	
	public static ArrayList<XTrace> getTracesContainingPatternWithHoles(Pattern pattern, XLog log){
		ArrayList<XTrace> output = new ArrayList<XTrace>();
		for(XTrace trace : log){
			boolean underAnalysis = false;
			int pos = -1;
			for(XEvent event : trace){
				String eventName = XConceptExtension.instance().extractName(event);
				if(underAnalysis){
					if(pattern.getItems().get(pos+1).equals(eventName)){
						pos++;
					}
	/*				if(pattern.getItems().get(pattern.getItems().size()-1).equals(eventName)){
						underAnalysis = false;
						inTrace = true;;
						break;
					}*/
					if((pattern.getItems().size()-1)==pos){
						underAnalysis = false;
						output.add(trace);
						break;
					}
				}
				if(pattern.getItems().get(0).equals(eventName) && pos==-1){
					pos = 0;
					underAnalysis = true;
				}
			}
		}
		return output;
	}

	

	public static TracePrefix getSimilarPrefix(XTrace lastTrace, XTrace toCompare, int traceIndex){
		ArrayList<String> requiredPrefix = new ArrayList<String>();
		for(XEvent event : lastTrace){
			requiredPrefix.add(XConceptExtension.instance().extractName(event));
		}
		double maxSimil = -1;
		String checkPref = "";
		String closestPrefix = "";
		ArrayList<String> chPrefixList = new ArrayList<String>();
		for(XEvent e: toCompare){
			String label = ((XAttributeLiteral) e.getAttributes().get("concept:name")).getValue();
			checkPref = checkPref+label+";";
			chPrefixList.add(label);
			//EditDistance ed = new EditDistance(requiredPrefix, chPrefixList);
			//double similarity = ed.computeNormalizedSimilarity();
			EditDistance ed = new EditDistance();
			double similarity = ed.computeNormalizedSimilarity(requiredPrefix, chPrefixList);
			if(similarity>maxSimil){
				maxSimil = similarity;
				closestPrefix = checkPref;
			}	
		}
		TracePrefix prefix = new TracePrefix(maxSimil,closestPrefix,traceIndex); 
		return prefix;
	}
	
	private static boolean isPatternInTrace(Pattern pattern, XTrace trace){
		boolean inTrace = false;
		boolean underAnalysis = false;
		int pos = -1;
		for(XEvent event : trace){
			String eventName = XConceptExtension.instance().extractName(event);
			if(underAnalysis){
				pos++;
				if(!pattern.getItems().get(pos).equals(eventName)){
					underAnalysis = false;
				}
				if(pattern.getItems().get(pattern.getItems().size()-1).equals(eventName)){
					underAnalysis = false;
					inTrace = true;;
					break;
				}
			}
			if(pattern.getItems().get(0).equals(eventName)){
				pos = 0;
				underAnalysis = true;
			}
		}
		return inTrace;
	}
	
	private static boolean isPatternInTraceWithHoles(Pattern pattern, XTrace trace){
		boolean inTrace = false;
		boolean underAnalysis = false;
		int pos = -1;
		for(XEvent event : trace){
			String eventName = XConceptExtension.instance().extractName(event);
			if(underAnalysis){
				if((pattern.getItems().size()>pos+1) && pattern.getItems().get(pos+1).equals(eventName)){
					pos++;
				}
/*				if(pattern.getItems().get(pattern.getItems().size()-1).equals(eventName)){
					underAnalysis = false;
					inTrace = true;;
					break;
				}*/
				if((pattern.getItems().size()-1)==pos){
					underAnalysis = false;
					inTrace = true;;
					break;
				}
			}
			if(pattern.getItems().get(0).equals(eventName) && pos==-1){
				pos = 0;
				underAnalysis = true;
			}
		}
		return inTrace;
	}
	
	public static ArrayList<Integer> getPatternsContainedInTrace(XTrace trace, ArrayList<Pattern> patterns){
		ArrayList<Integer> inTracePatterns = new ArrayList<Integer>();
		
		for (Pattern pattern : patterns) {
			if (isPatternInTraceWithHoles(pattern, trace))
				inTracePatterns.add(1);
			else
				inTracePatterns.add(0);
		}
		
		return inTracePatterns;
	}
}
