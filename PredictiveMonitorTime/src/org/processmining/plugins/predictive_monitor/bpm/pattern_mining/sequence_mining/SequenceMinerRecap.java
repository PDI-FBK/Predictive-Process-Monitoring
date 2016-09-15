package org.processmining.plugins.predictive_monitor.bpm.pattern_mining.sequence_mining;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.utility.Print;
import org.processmining.plugins.predictive_monitor.bpm.utility.XLogReader;

import ca.pfv.spmf_predictions.algorithms.sequentialpatterns.BIDE_and_prefixspan.AlgoMaxSP;
import ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.AlgoCMSPADE;





public class SequenceMinerRecap {
	
	private static Print print = new Print();
	public static void main(String[] args) {
		String inputLogFilePath = "./input/sellingProcess.mxml";
		String outputLogFilePath = "./output/output.txt";
		
		try {
			XLog log = XLogReader.openLog(inputLogFilePath);
			//List<Pattern> minedPatterns = mineFrequentPatternsPrefixString(inputLogFilePath, outputLogFilePath,2);
			//List<Pattern> minedPatterns = mineFrequentPatternsFV08(inputLogFilePath, outputLogFilePath,2);
			//List<Pattern> minedPatterns = mineFrequentPatternsMaxSPWithHoles(log, 2, 2);
			List<Pattern> minedPatterns = mineFrequentPatternsCMSpadeWithoutHoles(log, 0.8, 2);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
		
	}
	
/*	public static List<Pattern> mineFrequentPatternsPrefixString (String inputLogFileName, String outputLogFileName, int minLength){
		List<Pattern> minedPatters = new ArrayList();

		ca.pfv.spmf.input.sequence_database_list_strings.SequenceDatabase sequenceDB = new ca.pfv.spmf.input.sequence_database_list_strings.SequenceDatabase();
		
		try {
			XLog log = XLogReader.openLog(inputLogFileName);
			int traceNumber = 0;
			for (XTrace trace : log) {
				ca.pfv.spmf.input.sequence_database_list_strings.Sequence sequence = new ca.pfv.spmf.input.sequence_database_list_strings.Sequence(traceNumber);
				for (XEvent event : trace) {
					List<String> stringEvent = new ArrayList<String>();
					stringEvent.add(XConceptExtension.instance().extractName(event));
					sequence.addItemset(stringEvent);
					traceNumber++;
				}
				sequenceDB.addSequence(sequence);
				System.out.println(sequence);
			}
			AlgoPrefixSpan_with_Strings algo = new AlgoPrefixSpan_with_Strings(); 
			
			int minsup = 2; // we use a minimum support of 2 sequences.
			
			// execute the algorithm
			ca.pfv.spmf.algorithms.sequentialpatterns.BIDE_and_prefixspan_with_strings.SequentialPatterns patterns = algo.runAlgorithm(sequenceDB, null, minsup);
			for (int i = minLength; i < patterns.getLevelCount(); i++) {
				List<SequentialPattern> seqPatternsOfLengthX = patterns.getLevel(i); 
				for (SequentialPattern sequentialPattern : seqPatternsOfLengthX) {
					ArrayList<String> minedPatternList = new ArrayList<String>();
					for (ca.pfv.spmf.algorithms.sequentialpatterns.BIDE_and_prefixspan_with_strings.Itemset itemset : sequentialPattern.getItemsets()) {
						System.out.print(itemset+" ");
						minedPatternList.add(itemset.get(0));
					}
					System.out.println();
					Pattern minedPattern = new Pattern();
					minedPattern.setItems(minedPatternList);
					minedPatters.add(minedPattern);							
				}
	
				
			}

			algo.printStatistics(sequenceDB.size());			

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return minedPatters;

	}
	
	public static <E> List<Pattern> mineFrequentPatternsSPADE (String inputLogFileName, String outputLogFileName){
		List<Pattern> minedPatters = new ArrayList();

        // Load a sequence database
        double support = 0.5;

        boolean keepPatterns = true;
        boolean verbose = false;
		
        AbstractionCreator abstractionCreator = AbstractionCreator_Qualitative.getInstance();
        boolean dfs=true;
        IdListCreator idListCreator =IdListCreator_StandardMap.getInstance();
        
        CandidateGenerator candidateGenerator = CandidateGenerator_Qualitative.getInstance();
        
        SequenceDatabase sequenceDB = new SequenceDatabase(abstractionCreator, idListCreator);

		
		try {
			XLog log = XLogReader.openLog(inputLogFileName);
			int traceNumber = 0;
			for (XTrace trace : log) {
				String[] sequence = new String[trace.size()];
				for (XEvent event : trace) {
					sequence[traceNumber] = XConceptExtension.instance().extractName(event);
					traceNumber++;
				}
				sequenceDB.addSequence(sequence);
				System.out.println(sequence);
			}


	        
	        System.out.println(sequenceDB.toString());

	        AlgoSPADE algorithm = new AlgoSPADE(support,dfs,abstractionCreator);
	        
	        System.out.println("Minimum absolute support = "+support);
	        algorithm.runAlgorithm(sequenceDB, candidateGenerator,keepPatterns,verbose,outputLogFileName);
	        System.out.println(algorithm.getNumberOfFrequentPatterns()+ " frequent patterns.");
	        
	        System.out.println(algorithm.printStatistics());

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return minedPatters;

	}
	
	public static List<Pattern> mineFrequentPatternsFV08 (String inputLogFileName, String outputLogFileName, int minLength){
		List<Pattern> minedPatters = new ArrayList();
		
		try {
			XLog log = XLogReader.openLog(inputLogFileName);
			StringSequenceDatabaseFV08 sSequenceDB = convertIntoStringSequenceDatabaseFV08(log);
			ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.SequenceDatabase sequenceDB = sSequenceDB.getSequenceDB();

			// Create an instance of the algorithm
			AlgoFournierViger08 algo 
			  = new AlgoFournierViger08(0.55,
					0, 2, 0, 2, null, false, false);
			
			// execute the algorithm
			ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.Sequences patterns = algo.runAlgorithm(sequenceDB);
					
			for (int i = 0; i < patterns.getLevelCount(); i++) {
				List<ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.Sequence> seqPatternsOfLengthX = patterns.getLevel(i); 
				for (ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.Sequence sequentialPattern : seqPatternsOfLengthX) {
					ArrayList<String> minedPatternList = new ArrayList<String>();
					for (ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.Itemset itemset : sequentialPattern.getItemsets()) {
						String eventName = sSequenceDB.getMappedString(itemset.get(0).getId());
						System.out.print(eventName+" ");
						minedPatternList.add(eventName);
					}
					System.out.println();
					Pattern minedPattern = new Pattern();
					minedPattern.setItems(minedPatternList);
					minedPatters.add(minedPattern);							
				}
	
				 
			}
			
			
			
			algo.printResult(sequenceDB.size());
			

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return minedPatters;

	}
	
	
	private static StringSequenceDatabaseFV08 convertIntoStringSequenceDatabaseFV08(XLog log){
		
		Map<String, Integer> alphabetMap = new HashMap<String, Integer>();
		ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.SequenceDatabase sequenceDB = new ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.SequenceDatabase();
		int traceId = 0;
		for (XTrace trace : log) {
			ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.Sequence sequence = new ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.Sequence(traceId);

			for (XEvent event : trace) {
				String eventLabel = XConceptExtension.instance().extractName(event);
				Integer index = alphabetMap.get(eventLabel);
				if (index==null){
					index = alphabetMap.size();
					alphabetMap.put(eventLabel, index);
				}
				ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.ItemSimple item = new ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.ItemSimple(index.intValue());
				Date ts = XTimeExtension.instance().extractTimestamp(event);
				sequence.addItemset(new ca.pfv.spmf.algorithms.sequentialpatterns.fournier2008_seqdim.Itemset(item, ts.getTime()));			
			}
			sequenceDB.addSequence(sequence);
			System.out.println(sequence);
			traceId++;
		}
		StringSequenceDatabaseFV08 sSequenceDB  = new StringSequenceDatabaseFV08(alphabetMap, sequenceDB);
		return sSequenceDB;
	}*/
	
	
	

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
						print.that(eventName+" ");
						minedPatternList.add(eventName);
					}
					print.thatln();
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
			print.thatln(sequence);
			traceId++;
		}
		StringSequenceDatabaseMaxSP sSequenceDB  = new StringSequenceDatabaseMaxSP(alphabetMap, sequenceDB);
		return sSequenceDB;
	}
	
	public static List<Pattern> mineFrequentPatternsCMSpadeWithoutHoles (XLog log, double minSup, int minLength){
		return mineFrequentPatternsCMSpade(log, minSup, minLength);
	}

	/**
	 * @param log
	 * @param minSup
	 * @param minLength
	 * @return
	 */
	public static List<Pattern> mineFrequentPatternsCMSpade (XLog log, double minSup, int minLength){
		List<Pattern> minedPatters = new ArrayList<Pattern>();
        boolean keepPatterns = true;
        boolean verbose = true;
        boolean dfs=true;

        
        ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator abstractionCreator = ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator_Qualitative.getInstance();
        ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator candidateGenerator = ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator_Qualitative.getInstance();	        
        
		StringSequenceDatabaseCMSpade sSequenceDB = convertIntoStringSequenceDatabaseCMSpade(log, abstractionCreator, minSup);
		ca.pfv.spmf_predictions.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.database.SequenceDatabase sequenceDB = sSequenceDB.getSequenceDB();

		print.thatln(sequenceDB.toString());
		
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
	
}
