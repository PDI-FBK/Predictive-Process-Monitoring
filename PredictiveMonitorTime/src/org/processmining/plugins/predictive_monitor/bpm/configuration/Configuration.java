package org.processmining.plugins.predictive_monitor.bpm.configuration;

import java.util.Set;
import java.util.TreeSet;

import org.processmining.plugins.predictive_monitor.bpm.configuration.modules.*;

public class Configuration {
	
	
	/**/
	//TODO: modify the conf into single configuration items instead of set of modules!!
	/**/
	

		Classification classification;
		Clustering clustering;
		TrainingTracesModule inputPath;
		LogOption logOption;
		//PatternMining patternMining;
		PredictionTypeModule predictionType;
		Run run;
		
		public Configuration() {
			classification = new Classification();
			clustering = new Clustering();
			inputPath = new TrainingTracesModule();
			logOption = new LogOption();
			//patternMining = new PatternMining();
			predictionType = new PredictionTypeModule();
		}

		Set<Module> getConfiguration(){
			Set<Module> conf = new TreeSet<Module>();
			conf.add(classification);
			conf.add(clustering);
			conf.add(inputPath);
			conf.add(logOption);
			//conf.add(patternMining);
			conf.add(predictionType);
			return conf;
		}
		
		public Classification getClassification() {
			return classification;
		}

		public void setClassification(Module module) {
			this.classification = (Classification) module;
		}

		public Clustering getClustering() {
			return clustering;
		}

		public void setClustering(Module module) {
			this.clustering = (Clustering) module;
		}
		
		public TrainingTracesModule getInputPath() {
			return inputPath;
		}

		public void setInputPath(Module module) {
			this.inputPath = (TrainingTracesModule) module;
		}

		public LogOption getLogOption() {
			return logOption;
		}

		public void setLogOption(Module module) {
			this.logOption = (LogOption) module;
		}

		/*public PatternMining getPatternMining() {
			return patternMining;
		}
		
		public void setPatternMining(Module module) {
			this.patternMining = (PatternMining) module;
		}*/

		public PredictionTypeModule getPredictionType() {
			return predictionType;
		}

		public void setPredictionType(Module module) {
			this.predictionType = (PredictionTypeModule) module;
		}
	
	
}
