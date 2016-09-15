package org.processmining.plugins.predictive_monitor.bpm.configuration.modules;

import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.NumberOfIntervals;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.string_values.Formulas;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values.TimeFromLastEvent;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.PartitionMethod;;

public class PredictionTypeModule implements Module{
	
	private org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.PredictionTypeModule predictionType;
	private PartitionMethod partitionMethod;
	private NumberOfIntervals numberOfIntervals;
	//ask Marco
	public final static long division[]={86400000l,864000000l,2592000000l,31536000000l,315360000000l}; //1 day, 10 days, 1 month, 1 year, 10 years
	private Formulas formulas;
	private TimeFromLastEvent jumpToCurrentIndex;
	
	public PredictionTypeModule() {
		predictionType = new org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.PredictionTypeModule();
		partitionMethod = new PartitionMethod();
		numberOfIntervals = new NumberOfIntervals();
		formulas = new Formulas();	
		jumpToCurrentIndex = new TimeFromLastEvent();
	}

	@Override
	public String getModuleName() {
		return "Prediction Type";
	}

	@Override
	public List<Parameter> getParameterList() {
		List <Parameter> retval = new ArrayList<Parameter>();
		retval.add(predictionType);
		retval.add(partitionMethod);
		retval.add(numberOfIntervals);
		retval.add(formulas);
		retval.add(jumpToCurrentIndex);
		return retval;
	}

	public final org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.PredictionTypeModule getPredictionType() {
		return predictionType;
	}

	public final void setPredictionType(
			org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.PredictionTypeModule predictionType) {
		this.predictionType = predictionType;
	}

	public final PartitionMethod getPartitionMethod() {
		return partitionMethod;
	}

	public final void setPartitionMethod(PartitionMethod partitionMethod) {
		this.partitionMethod = partitionMethod;
	}

	public final NumberOfIntervals getNumberOfIntervals() {
		return numberOfIntervals;
	}

	public final void setNumberOfIntervals(NumberOfIntervals numberOfIntervals) {
		this.numberOfIntervals = numberOfIntervals;
	}
	
	public final Formulas getFormulas() {
		return formulas;
	}

	public final void setFormulas(Formulas formulas) {
		this.formulas = formulas;
	}

	public final TimeFromLastEvent getJumpToCurrentIndex() {
		return jumpToCurrentIndex;
	}

	public final void setJumpToCurrentIndex(TimeFromLastEvent jumpToCurrentIndex) {
		this.jumpToCurrentIndex = jumpToCurrentIndex;
	}

}
/*
	public static enum PredictionOptions {FORMULA_SATISFACTION,FORMULA_SATISFACTION_TIME,ACTIVATION_VERIFICATION_FORMULA_TIME};
	//Prediction Type
		public final static PredictionOptions predictionType = PredictionOptions.ACTIVATION_VERIFICATION_FORMULA_TIME;
		
		//TimeClassifier
		public final static PartitionMethod partitionMethod = PartitionMethod.FIXED_SORTED_DIVISION;
		public final static int numberOfIntervals = 5;
		public final static long division[]={86400000l,864000000l,2592000000l,31536000000l,315360000000l}; //1 day, 10 days, 1 month, 1 year, 10 years
}
*/