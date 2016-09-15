package org.processmining.plugins.predictive_monitor.bpm.pattern_mining.sequence_mining;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.utility.Print;
import org.processmining.plugins.predictive_monitor.bpm.utility.XLogReader;

import ca.pfv.spmf_predictions.algorithms.sequentialpatterns.BIDE_and_prefixspan.AlgoMaxSP;
import ca.pfv.spmf_predictions.algorithms.sequentialpatterns.BIDE_and_prefixspan_with_strings.AlgoPrefixSpan_with_Strings;
import ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.AlgoClaSP;
import ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.dataStructures.Sequences;
import ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.dataStructures.creators.AbstractionCreator;
import ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.dataStructures.creators.AbstractionCreator_Qualitative;
import ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.idlists.creators.IdListCreator;
import ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.idlists.creators.IdListCreatorStandard_Map;
import ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.AlgoCMSPADE;





public class SequenceMiner {
	
	private static Print print = new Print();
	
	public static void main(String[] args) {
		String inputLogFilePath = "./input/sellingProcess.mxml";
		String outputLogFilePath = "./output/output.txt";
		
		try {
			XLog log = XLogReader.openLog(inputLogFilePath);
			//List<Pattern> minedPatterns = mineFrequentPatternsMaxSPWithHoles(log, 2, 2);
			//List<Pattern> minedPatterns = mineFrequentPatternsPrefixWithoutHoles(log, 0.8, 2);
			//List<Pattern> minedPatterns = mineFrequentPatternsClasPWithHoles(log, 0.8, 2);
			List<Pattern> minedPatterns = mineFrequentPatternsCMSpadeWithHoles(log, 0.5, 1, 2);
			
			for (Pattern pattern : minedPatterns) {
					print.that(pattern.getItems());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
		
	}
	

	public static List<Pattern> mineFrequentPatternsMaxSPWithHoles (XLog log, double minSup, int minLength){
		List<Pattern> minedPatters = new ArrayList<Pattern>();
		
		int minAbsSup = (int) (minSup * log.size());

		StringSequenceDatabaseMaxSP sSequenceDB = convertIntoStringSequenceDatabaseMaxSP(log);
		ca.pfv.spmf_predictions.input.sequence_database_list_integers.SequenceDatabase sequenceDB = sSequenceDB.getSequenceDB();

		// Create an instance of the algorithm
		AlgoMaxSP algo  = new AlgoMaxSP();
			
		// execute the algorithm
		ca.pfv.spmf_predictions.algorithms.sequentialpatterns.BIDE_and_prefixspan.SequentialPatterns patterns;
		try {
			patterns = algo.runAlgorithm(sequenceDB, null, minAbsSup);
				
			for (int i = minLength; i < patterns.getLevelCount(); i++) {
				List<ca.pfv.spmf_predictions.algorithms.sequentialpatterns.BIDE_and_prefixspan.SequentialPattern> seqPatternsOfLengthX = patterns.getLevel(i); 
				for (ca.pfv.spmf_predictions.algorithms.sequentialpatterns.BIDE_and_prefixspan.SequentialPattern sequentialPattern : seqPatternsOfLengthX) {
					ArrayList<String> minedPatternList = new ArrayList<String>();
					for (ca.pfv.spmf_predictions.patterns.itemset_list_integers_without_support.Itemset itemset : sequentialPattern.getItemsets()) {
						String eventName = sSequenceDB.getMappedString(itemset.get(0).intValue());
						//System.out.print(eventName+" ");
						minedPatternList.add(eventName);
					}
					//System.out.println();
					Pattern minedPattern = new Pattern();
					minedPattern.setItems(minedPatternList);
					minedPatters.add(minedPattern);							
				}
			}

			algo.printStatistics(sequenceDB.size());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return minedPatters;

	}
	
	
	private static StringSequenceDatabaseMaxSP convertIntoStringSequenceDatabaseMaxSP(XLog log){
		
		Map<String, Integer> alphabetMap = new HashMap<String, Integer>();
		ca.pfv.spmf_predictions.input.sequence_database_list_integers.SequenceDatabase sequenceDB = new ca.pfv.spmf_predictions.input.sequence_database_list_integers.SequenceDatabase();
		int traceId = 0;
		for (XTrace trace : log) {
			ca.pfv.spmf_predictions.input.sequence_database_list_integers.Sequence sequence = new ca.pfv.spmf_predictions.input.sequence_database_list_integers.Sequence(traceId);

			for (XEvent event : trace) {
				String eventLabel = XConceptExtension.instance().extractName(event);
				Integer index = alphabetMap.get(eventLabel);
				if (index==null){
					index = alphabetMap.size();
					alphabetMap.put(eventLabel, index);
				}
				List<Integer> itemset = new ArrayList<Integer>();
				itemset.add(index);
				sequence.addItemset(itemset);			
			}
			sequenceDB.addSequence(sequence);
			traceId++;
		}
		StringSequenceDatabaseMaxSP sSequenceDB  = new StringSequenceDatabaseMaxSP(alphabetMap, sequenceDB);
		return sSequenceDB;
	}
	
	public static List<Pattern> mineFrequentPatternsPrefixWithoutHoles (final XLog log, double minSup, int minLength){
		int minSupAbs = (int) (minSup*log.size());	
		List<Pattern> frequentPatterns = mineFrequentPatternsPrefixString(log, minSupAbs, minLength);
		List<Pattern> filteredPatterns = filterPatterns(frequentPatterns, log, minSupAbs);
		/*if(ServerConfigurationClass.printDebug){
			System.out.println("************* FILTERED PATTERNS **********");
			for (Pattern pattern : filteredPatterns) {
				System.out.println(pattern.getItems().toString());
			}
			System.out.println("************* REDUCED PATTERNS **********");
			List<Pattern> reducedPatterns = removeSubPatterns(filteredPatterns);
			for (Pattern pattern : reducedPatterns) {
				System.out.println(pattern.getItems().toString());
			}
		}*/
		return filteredPatterns;
	}
	

	public static List<Pattern> mineFrequentPatternsPrefixString (XLog log, int minSupAbs, int minLength){
		List<Pattern> minedPatters = new ArrayList();
		
		ca.pfv.spmf_predictions.input.sequence_database_list_strings.SequenceDatabase sequenceDB = new ca.pfv.spmf_predictions.input.sequence_database_list_strings.SequenceDatabase();
		
			int traceNumber = 0;
			for (XTrace trace : log) {
				ca.pfv.spmf_predictions.input.sequence_database_list_strings.Sequence sequence = new ca.pfv.spmf_predictions.input.sequence_database_list_strings.Sequence(traceNumber);
				for (XEvent event : trace) {
					List<String> stringEvent = new ArrayList<String>();
					stringEvent.add(XConceptExtension.instance().extractName(event));
					sequence.addItemset(stringEvent);
					traceNumber++;
				}
				sequenceDB.addSequence(sequence);
				print.thatln(sequence);
			}
			AlgoPrefixSpan_with_Strings algo = new AlgoPrefixSpan_with_Strings(); 
		
			
			
			
			try {
				// execute the algorithm
				ca.pfv.spmf_predictions.algorithms.sequentialpatterns.BIDE_and_prefixspan_with_strings.SequentialPatterns patterns = algo.runAlgorithm(sequenceDB, null, minSupAbs);
				for (int i = minLength; i < patterns.getLevelCount(); i++) {
					List<ca.pfv.spmf_predictions.algorithms.sequentialpatterns.BIDE_and_prefixspan_with_strings.SequentialPattern> seqPatternsOfLengthX = patterns.getLevel(i); 
					for (ca.pfv.spmf_predictions.algorithms.sequentialpatterns.BIDE_and_prefixspan_with_strings.SequentialPattern sequentialPattern : seqPatternsOfLengthX) {
						ArrayList<String> minedItemSetList = new ArrayList<String>();
						for (ca.pfv.spmf_predictions.algorithms.sequentialpatterns.BIDE_and_prefixspan_with_strings.Itemset itemset : sequentialPattern.getItemsets()) {
							//if(ServerConfigurationClass.printDebug)System.out.print(itemset+" ");
							minedItemSetList.add(itemset.get(0));
						}
						Set<Integer> minedSequenceIdsList = new HashSet<Integer>();
						for (Integer sequenceID : sequentialPattern.getSequencesID()) {
							minedSequenceIdsList.add(getTraceIndex(sequenceDB, sequenceID));
						}
						//if(ServerConfigurationClass.printDebug)System.out.println();
						Pattern minedPattern = new Pattern();
						minedPattern.setItems(minedItemSetList);
						minedPattern.setSequencesID(minedSequenceIdsList);
						minedPatters.add(minedPattern);							
					}
		
					
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			algo.printStatistics(sequenceDB.size());			

		return minedPatters;

	}
	
	private static Integer getTraceIndex(ca.pfv.spmf_predictions.input.sequence_database_list_strings.SequenceDatabase sequenceDB, int sequenceID){
		Integer traceIndex = -1;
		int i = 0;
		while (i<sequenceDB.getSequenceIDs().size() && traceIndex<0){
			Integer currSequenceID = (Integer) sequenceDB.getSequenceIDs().toArray()[i];
			if (currSequenceID.intValue()==sequenceID)
				traceIndex = i;
			i++;
				
		}
		return traceIndex;
	}
	
	private static List<Pattern> filterPatterns(List<Pattern> frequentPatterns, XLog log, int minSupport) {
		List<Pattern> filteredPatterns = new ArrayList<Pattern>();
		for (Pattern pattern : frequentPatterns) {
			Set<Integer> traceIndexes = pattern.getSequencesID();
			int motifPatternTraces = traceIndexes.size();
			for (Iterator<Integer> iterator = traceIndexes.iterator(); iterator.hasNext() && motifPatternTraces>=minSupport;) {
				Integer traceIndex = (Integer) iterator.next();
				XTrace trace = log.get(traceIndex);
				if (!isMotifPatternTrace(pattern, trace))
					motifPatternTraces--;
			}
			if (motifPatternTraces>=minSupport)
				filteredPatterns.add(pattern);
		}
		return filteredPatterns;
	}	

	
	private static boolean isMotifPatternTrace(Pattern pattern, XTrace trace){
		boolean motifPatternTrace = false; 
		String firstItem = pattern.getItems().get(0);
		int tracePosition = 0;
		while (tracePosition<trace.size() && !motifPatternTrace){
			XEvent event = trace.get(tracePosition);
			if (XConceptExtension.instance().extractName(event).equals(firstItem) && tracePosition+pattern.getItems().size()<trace.size()){
				motifPatternTrace = isMotifPattern(pattern, trace, tracePosition);
			}
			tracePosition++;
		}
		return motifPatternTrace;
	}

	
	private static boolean isMotifPattern(Pattern pattern, XTrace trace, int tracePosition){
		boolean motifPattern = true; 
		for (String item : pattern.getItems()) {
			XEvent event = trace.get(tracePosition);
			if (!item.equals(XConceptExtension.instance().extractName(event)))
				motifPattern = false;
			tracePosition++;
		}
		return motifPattern;
	}
	
	private static List<Pattern> removeSubPatterns(List<Pattern> originalPatterns){
		List<Pattern> reducedPattern = new ArrayList<Pattern>();
		for (Pattern pattern1 : originalPatterns) {
			boolean subpattern= false;
			int indexPattern1 = originalPatterns.indexOf(pattern1);
			int i = indexPattern1+1;
			while (i<originalPatterns.size() && !subpattern){
				Pattern pattern2 = originalPatterns.get(i);
				subpattern = !isSubpattern(pattern1, pattern2);
				i++;
			}	
			if (!subpattern)
				reducedPattern.add(pattern1);
		}
		return reducedPattern;
	}

	private static boolean isSubpattern(Pattern pattern1, Pattern pattern2){
		boolean subpattern = true;
		int i = 0;
		for (Iterator<String> iterator =  pattern1.getItems().iterator(); iterator.hasNext() && subpattern;) {
			String item = (String) iterator.next();
			if (!item.equals(pattern2.getItems().get(i)))
				subpattern = false;
		}
		return subpattern;
	}
	
	
	
	public static List<Pattern> mineFrequentPatternsClasPWithHoles (XLog log, double minSup, int minLength){
		List<Pattern> minedPatters = new ArrayList<Pattern>();
		boolean keepPatterns = true;
		boolean verbose = true;
		boolean findClosedPatterns = true;
		boolean executePruningMethods = false;
		
		int minAbsSup = (int) (minSup * log.size());


		AbstractionCreator abstractionCreator = AbstractionCreator_Qualitative.getInstance();
		StringSequenceDatabaseClasP sSequenceDB = convertIntoStringSequenceDatabaseClasP(log, abstractionCreator, minSup);
		ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase sequenceDatabase = sSequenceDB.getSequenceDB();

        //ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase sequenceDatabase = new ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase(abstractionCreator, idListCreator);
        //sequenceDatabase.getSequences().get(0).get(0).
        //double relativeSupport = sequenceDatabase.loadFile(fileToPath("contextClaSP.txt"), support);
        //double relativeSupport = sequenceDatabase.loadFile(fileToPath("contextPrefixSpan.txt"), minSup);
        //double relativeSupport = sequenceDatabase.loadFile(fileToPath("gazelle.txt"), support);


		
        AlgoClaSP algorithm = new AlgoClaSP(sSequenceDB.getSupport(), abstractionCreator, findClosedPatterns, executePruningMethods);


        //System.out.println(sequenceDatabase.toString());
        print.thatln("Support: " + minSup);
       try {
    	   Sequences patterns =algorithm.runAlgorithm(sequenceDatabase, keepPatterns, verbose, null);
    	  // if(ServerConfigurationClass.printDebug)System.out.println(algorithm.getNumberOfFrequentPatterns() + " patterns found.");
        
        //List<TrieNode> nodes = algorithm.getFrequentAtoms(); 
    	
    	   
	        if (verbose && keepPatterns) {
	            System.out.println(algorithm.printStatistics());
	        }
    	           
        
		for (int i = minLength; i < patterns.getLevelCount(); i++) {
			List<ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.dataStructures.patterns.Pattern> seqPatternsOfLengthX = patterns.getLevel(i); 
			for (ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.dataStructures.patterns.Pattern sequentialPattern : seqPatternsOfLengthX) {
				ArrayList<String> minedPatternList = new ArrayList<String>();
				for (ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.dataStructures.abstracciones.ItemAbstractionPair item : sequentialPattern.getElements()) {
					String eventName = sSequenceDB.getMappedString(new Integer(item.getItem().toString()));
					//System.out.print(eventName+" ");
					minedPatternList.add(eventName);
					
				}
				//System.out.println();
				Pattern minedPattern = new Pattern();
				minedPattern.setItems(minedPatternList);
				minedPatters.add(minedPattern);		
				

			}
		}
        

        
        
        
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}


		return minedPatters;

	}

	private static StringSequenceDatabaseClasP convertIntoStringSequenceDatabaseClasP(XLog log, AbstractionCreator abstractionCreator, double minSup){
		
		Map<String, Integer> alphabetMap = new HashMap<String, Integer>();
        IdListCreator idListCreator = IdListCreatorStandard_Map.getInstance();
        StringSequenceDatabaseClasP sSequenceDB = null;

		ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase sequenceDB = new ca.pfv.spmf_predictions.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase(abstractionCreator, idListCreator);
        ArrayList<String[]> sequences = new ArrayList<String[]>();
		int traceId = 0;
		
        //SequenceDatabase sequenceDatabase = new SequenceDatabase(abstractionCreator, idListCreator);

        //double relativeSupport = sequenceDatabase.loadFile(fileToPath("contextClaSP.txt"), support);
		
		for (XTrace trace : log) {
			//ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.Sequence sequence = new ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.Sequence(traceId);
			String[] sequence = new String[trace.size()*2];
			int i = 0;
			for (XEvent event : trace) {
				String eventLabel = XConceptExtension.instance().extractName(event);
				Integer index = alphabetMap.get(eventLabel);
				if (index==null){
					index = alphabetMap.size();
					alphabetMap.put(eventLabel, index);
				}
				sequence[i]= String.valueOf(index);
				i++;
				if (i< (trace.size()*2)-1){
					sequence[i]= String.valueOf(-1);
				} else
					sequence[i]= String.valueOf(-2);
				i++;
				
			}
			print.that(sequence.toString());
			sequences.add(sequence);
			traceId++;
		}
        double relativeSupport;
		try {
			relativeSupport = sequenceDB.loadFile_H(sequences, minSup);
			sSequenceDB = new StringSequenceDatabaseClasP(alphabetMap, sequenceDB, relativeSupport);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		return sSequenceDB;
	}
	
	/**
	 * @param log
	 * @param minSup
	 * @param minLength
	 * @return
	 */
	public static List<Pattern> mineFrequentPatternsCMSpadeWithHoles (XLog log, double minSup){
		List<Pattern> minedPatters = new ArrayList<Pattern>();
        boolean keepPatterns = true;
        boolean verbose = true;
        boolean dfs=true;

        
        ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator abstractionCreator = ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator_Qualitative.getInstance();
        ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator candidateGenerator = ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator_Qualitative.getInstance();	        

        //ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator idListCreator = ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator_FatBitmap.getInstance();
        
		StringSequenceDatabaseCMSpade sSequenceDB = convertIntoStringSequenceDatabaseCMSpade(log, abstractionCreator, minSup);
		ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.database.SequenceDatabase sequenceDB = sSequenceDB.getSequenceDB();

		//if(ServerConfigurationClass.printDebug)System.out.println(sequenceDB.toString());
		
		// execute the algorithm
         AlgoCMSPADE algorithm = new AlgoCMSPADE(minSup,dfs,abstractionCreator);
        
         print.thatln("Minimum absolute support = "+minSup + " " + algorithm.minSupRelative);
        
        try {
        	ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.savers.Saver saver = algorithm.runAlgorithm(sequenceDB, candidateGenerator,keepPatterns,verbose,null);
        	ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.Sequences patterns = saver.getPatterns();

        	
        	print.thatln(algorithm.getNumberOfFrequentPatterns()+ " frequent patterns.");
    		
             for (int i = 0; i < patterns.getLevelCount(); i++) {
            	List<ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.patterns.Pattern> seqPatternsOfLengthX = patterns.getLevel(i); 
         		for (ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.patterns.Pattern sequentialPattern : seqPatternsOfLengthX) {
            		ArrayList<String> minedPatternList = new ArrayList<String>();
            		for (ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.abstractions.ItemAbstractionPair itemset : sequentialPattern.getElements()) {
            			int index = (Integer) itemset.getItem().getId();
            			String eventName = sSequenceDB.getMappedString(index);
            			print.that(eventName+" ");
            			minedPatternList.add(eventName);
            		}
            		print.thatln();
            		Pattern minedPattern = new Pattern();
            		minedPattern.setItems(minedPatternList);
            		minedPatters.add(minedPattern);							
         		}
            }

             print.thatln(algorithm.printStatistics());
             
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




		return minedPatters;

	}
	
	
	private static StringSequenceDatabaseCMSpade convertIntoStringSequenceDatabaseCMSpade (XLog log, ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator abstractionCreator, double minSup){
		
		Map<String, Integer> alphabetMap = new HashMap<String, Integer>();
        ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator idListCreator = ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator_FatBitmap.getInstance();
		ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.database.SequenceDatabase sequenceDB = new ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.database.SequenceDatabase(abstractionCreator,idListCreator);
		StringSequenceDatabaseCMSpade sSequenceDB= null;
		ArrayList<String[]> sequences = new ArrayList<String[]>();
		int traceId = 0;
		for (XTrace trace : log) {
			String[] sequence = new String[(trace.size()*2)+1];
			int i=0;
			for (XEvent event : trace) {
				String eventLabel = XConceptExtension.instance().extractName(event);
				Integer index = alphabetMap.get(eventLabel);
				if (index==null){
					index = alphabetMap.size();
					alphabetMap.put(eventLabel, index);
				}
				sequence[i]=String.valueOf(index);
				print.that(sequence[i]+" ");
				i++;
				sequence [i] = "-1";
				print.that(sequence[i]+" ");
				i++;
			}
			sequence[i]="-2";
			print.that(sequence[i]+" ");
			sequences.add(sequence);
			traceId++;
		}
		try {
			sequenceDB.loadFile_H(sequences, minSup);
			print.thatln(sequenceDB.toString());
			sSequenceDB  = new StringSequenceDatabaseCMSpade(alphabetMap, sequenceDB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sSequenceDB;
	}
	
	public static List<Pattern> mineFrequentPatternsCMSpadeWithHoles (final XLog log, double minSup, int minLength, int maxLength){
		List<Pattern> minedPatterns = mineFrequentPatternsCMSpadeWithHoles(log, minSup);
		List<Pattern> filteredPatterns = new ArrayList<Pattern>();
		for (Pattern pattern : minedPatterns) {
			if (pattern.getItems().size()>=minLength && pattern.getItems().size()<=maxLength)
				filteredPatterns.add(pattern);
		}
		return filteredPatterns;
	}

	
}
