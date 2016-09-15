package org.processmining.plugins.predictive_monitor.bpm.configuration.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.TrainingFile;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.MaxPrefixLength;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.MinPrefixLength;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.PrefixGap;

public class TrainingTracesModule implements Module {
	
	private TrainingFile trainingFile;
	private MinPrefixLength minPrefixLength;
	private MaxPrefixLength maxPrefixLength;
	private PrefixGap prefixGap;
	
	public TrainingTracesModule(TrainingFile trainingFile) {
		this.trainingFile = trainingFile;
		minPrefixLength = new MinPrefixLength();
		maxPrefixLength = new MaxPrefixLength();
		prefixGap = new PrefixGap();
	}
	public TrainingTracesModule() {
		trainingFile = new TrainingFile();
		minPrefixLength = new MinPrefixLength();
		maxPrefixLength = new MaxPrefixLength();
		prefixGap = new PrefixGap();
	}
	
	@Override
	public String getModuleName() {
		return "TrainingTraces";
	}

	@Override
	public List<Parameter> getParameterList() {
		List <Parameter> retval = new ArrayList<Parameter>();
		retval.add(trainingFile);
		retval.add(minPrefixLength);
		retval.add(maxPrefixLength);
		retval.add(prefixGap);
		return retval;
	}

	public final TrainingFile getTrainingFile() {
		return trainingFile;
	}

	public final void setTrainingFile(TrainingFile testingFilePath) {
		this.trainingFile = testingFilePath;
	}
	
	public final void setAvailableTrainingFile(Set<String> availableVals){
		trainingFile.setPossibleValues(availableVals);
	}
	
	public final MinPrefixLength getMinPrefixLength() {
		return minPrefixLength;
	}

	public final void setMinPrefixLength(MinPrefixLength prefixLength) {
		this.minPrefixLength = prefixLength;
	}

	public MaxPrefixLength getMaxPrefixLength() {
		return maxPrefixLength;
	}

	public void setMaxPrefixLength(MaxPrefixLength maxPrefixLength) {
		this.maxPrefixLength = maxPrefixLength;
	}

	public PrefixGap getPrefixGap() {
		return prefixGap;
	}

	public void setPrefixGap(PrefixGap prefixGap) {
		this.prefixGap = prefixGap;
	}

}
