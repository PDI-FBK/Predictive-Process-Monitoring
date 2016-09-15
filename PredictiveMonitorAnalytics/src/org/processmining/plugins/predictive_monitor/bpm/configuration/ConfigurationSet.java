package org.processmining.plugins.predictive_monitor.bpm.configuration;

import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.predictive_monitor.bpm.configuration.modules.*;

public class ConfigurationSet {
	Classification classification;
	Clustering clustering;
	Evaluation evaluation;
	TrainingTracesModule trainingTraces;
	LogOption logOption;
	PredictionTypeModule predictionType;
	
	public ConfigurationSet() {
		classification = new Classification();
		clustering = new Clustering();
		evaluation = new Evaluation();
		trainingTraces = new TrainingTracesModule();
		logOption = new LogOption();
		predictionType = new PredictionTypeModule();
	}

	public List<Module> getConfiguration(){
		List<Module> conf = new ArrayList<Module>();
		conf.add(classification);
		conf.add(clustering);
		conf.add(evaluation);
		conf.add(trainingTraces);
		conf.add(logOption);
		conf.add(predictionType);
		return conf;
	}

	public final Classification getClassification() {
		return classification;
	}

	public final void setClassification(Module classification) {
		this.classification = (Classification) classification;
	}

	public final Clustering getClustering() {
		return clustering;
	}

	public final void setClustering(Module clustering) {
		this.clustering = (Clustering) clustering;
	}

	public final Evaluation getEvaluation() {
		return evaluation;
	}

	public final void setEvaluation(Module evaluation) {
		this.evaluation = (Evaluation) evaluation;
	}

	public final TrainingTracesModule getTrainingTraces() {
		return trainingTraces;
	}

	public final void setTrainingTraces(Module inputOutputPath) {
		this.trainingTraces = (TrainingTracesModule) inputOutputPath;
	}

	public final LogOption getLogOption() {
		return logOption;
	}

	public final void setLogOption(Module logOption) {
		this.logOption = (LogOption) logOption;
	}

	public final PredictionTypeModule getPredictionType() {
		return predictionType;
	}

	public final void setPredictionType(Module predictionType) {
		this.predictionType = (PredictionTypeModule) predictionType;
	}

}