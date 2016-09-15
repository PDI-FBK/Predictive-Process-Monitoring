package org.processmining.plugins.predictive_monitor.bpm.pattern_mining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures.Pattern;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.discriminative_pattern_mining.DiscriminativePatternMiner;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.sequence_mining.DiscriminativeSequenceMiner;
import org.processmining.plugins.predictive_monitor.bpm.pattern_mining.sequence_mining.SequenceMiner;
import org.processmining.plugins.predictive_monitor.bpm.utility.Print;

public class PatternController {

	private static Print print = new Print();
	public static ArrayList<Pattern> generateSequentialPatternsWithHoles(
			final XLog log, double patternMinimumSupport, int minimumPatternLength,
			int maximumPatternLength) {
		return (ArrayList<Pattern>) SequenceMiner
				.mineFrequentPatternsCMSpadeWithHoles(log,
						patternMinimumSupport, minimumPatternLength,
						maximumPatternLength);
	}

	public static ArrayList<Pattern> generateSequentialPatternsWithoutHoles(
			final XLog log, double patternMinimumSupport, int minimumPatternLength) {
		return (ArrayList<Pattern>) SequenceMiner
				.mineFrequentPatternsPrefixWithoutHoles(log,
						patternMinimumSupport, minimumPatternLength);
	}

	public static ArrayList<Pattern> generateDiscriminativeSequentialPatternsWithHoles(
			final XLog log, double patternMinimumSupport, int minimumPatternLength,
			int maximumPatternLength,
			HashMap<String, String> histTraceFormulaSatisfaction,
			double discriminativeMinimumSupport) {
		ArrayList<Pattern> patterns = generateSequentialPatternsWithHoles(log,
				patternMinimumSupport, minimumPatternLength,
				maximumPatternLength);
		return (ArrayList<Pattern>) DiscriminativeSequenceMiner
				.mineDiscriminativePatterns(log, patterns,
						histTraceFormulaSatisfaction,
						discriminativeMinimumSupport,
						discriminativeMinimumSupport);
	}

	public static ArrayList<Pattern> generateDiscriminativeSequentialPatternsWithoutHoles(
			final XLog log, double patternMinimumSupport, int minimumPatternLength,
			HashMap<String, String> histTraceFormulaSatisfaction,
			double discriminativeMinimumSupport) {
		ArrayList<Pattern> patterns = generateSequentialPatternsWithoutHoles(
				log, patternMinimumSupport, minimumPatternLength);
		return (ArrayList<Pattern>) DiscriminativeSequenceMiner
				.mineDiscriminativePatterns(log, patterns,
						histTraceFormulaSatisfaction,
						discriminativeMinimumSupport,
						discriminativeMinimumSupport);
	}

	public static ArrayList<Pattern> generateSequentialPatternsWithHoles(
			XLog log, double patternMinimumSupport, int minimumPatternLength,
			int maximumPatternLength, String filePath) {
		ArrayList<Pattern> patterns = generateSequentialPatternsWithHoles(log,
				patternMinimumSupport, minimumPatternLength,
				maximumPatternLength);
		printPatterns(patterns, filePath);
		return patterns;
	}

	public static ArrayList<Pattern> generateSequentialPatternsWithoutHoles(
			XLog log, double patternMinimumSupport, int minimumPatternLength,
			String filePath) {
		ArrayList<Pattern> patterns = generateSequentialPatternsWithoutHoles(
				log, patternMinimumSupport, minimumPatternLength);
		printPatterns(patterns, filePath);
		return patterns;

	}

	public static ArrayList<Pattern> generateDiscriminativeSequentialPatternsWithHoles(
			XLog log, double patternMinimumSupport, int minimumPatternLength,
			int maximumPatternLength,
			HashMap<String, String> histTraceFormulaSatisfaction,
			double discriminativeMinimumSupport, String filePath) {
		ArrayList<Pattern> patterns = generateDiscriminativeSequentialPatternsWithHoles(
				log, patternMinimumSupport, minimumPatternLength,
				maximumPatternLength, histTraceFormulaSatisfaction,
				discriminativeMinimumSupport);
		printPatterns(patterns, filePath);
		return patterns;

	}

	public static ArrayList<Pattern> generateDiscriminativeSequentialPatternsWithoutHoles(
			XLog log, double patternMinimumSupport, int minimumPatternLength,
			HashMap<String, String> histTraceFormulaSatisfaction,
			double discriminativeMinimumSupport, String filePath) {
		ArrayList<Pattern> patterns = generateDiscriminativeSequentialPatternsWithoutHoles(
				log, patternMinimumSupport, minimumPatternLength,
				histTraceFormulaSatisfaction, discriminativeMinimumSupport);
		printPatterns(patterns, filePath);
		return patterns;

	}

	public static ArrayList<Pattern> generateDiscriminativePatterns(final XLog log, HashMap<String, String> labels,
			double patternMinimumSupport, int patternCount,
			int sameLengthPatternCount) {
		DiscriminativePatternMiner miner = new DiscriminativePatternMiner(log, labels);
		return (ArrayList<Pattern>) miner.selectTopKFeatures(patternCount,
				sameLengthPatternCount, patternMinimumSupport);

	}

	public static ArrayList<Pattern> generateDiscriminativePatterns(final XLog log, HashMap<String, String> labels,
			double patternMinimumSupport, int patternCount,
			int sameLengthPatternCount, String filePath) {
		DiscriminativePatternMiner miner = new DiscriminativePatternMiner(log, labels);
		ArrayList<Pattern> patterns = miner.selectTopKFeatures(patternCount,
				sameLengthPatternCount, patternMinimumSupport);
		printPatternsWithLengths(patterns, filePath);
		return patterns;
	}

	private static void printPatterns(ArrayList<Pattern> patterns,
			String filePath) {
		try {
			FileWriter fW = new FileWriter(new File(filePath));
			for (Pattern pattern : patterns) {
				for (String item : pattern.getItems()) {
					fW.write(item + " ");
					print.that(item + " ");
				}
				fW.write("\n");
				print.thatln();

			}
			fW.flush();
			fW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void printPatternsWithLengths(ArrayList<Pattern> patterns,
			String filePath) {
		try {
			FileWriter fW = new FileWriter(new File(filePath));
			for (Pattern pattern : patterns) {
				fW.write(pattern.toString());
				fW.write(";" + pattern.getItems().size() + "\n");
			}
			fW.flush();
			fW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<Pattern> readPatternsFromFile(
			String discriminativepatternfilepath) {
		List<Pattern> patterns = new ArrayList<Pattern>();
		try (BufferedReader br = new BufferedReader(new FileReader(discriminativepatternfilepath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(";");
				String[] events = parts[0].split("\\|");
				Pattern pattern = new Pattern();
				pattern.setItems(new ArrayList<String>(Arrays.asList(events)));
				patterns.add(pattern);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return patterns;
	}

}
