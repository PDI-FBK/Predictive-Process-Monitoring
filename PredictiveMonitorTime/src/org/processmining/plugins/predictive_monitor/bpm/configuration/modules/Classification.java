package org.processmining.plugins.predictive_monitor.bpm.configuration.modules;

import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.ClassificationPatternType;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.ClassificationType;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values.ClassificationDiscriminativeMinimumSupport;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values.ClassificationPatternMinimumSupport;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.ClassificationDiscriminativePatternCount;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.ClassificationMaximumPatternLength;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.ClassificationMinimumPatternLength;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.ClassificationSameLengthDiscriminativePatternCount;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.RFMaxDepth;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.RFNumFeatures;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.RFNumTrees;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.RFSeed;

public class Classification implements Module{
	private ClassificationType classificationType;
	private ClassificationPatternType classificationPatternType;
	private ClassificationPatternMinimumSupport classificationPatternMinimumSupport;
	private ClassificationDiscriminativeMinimumSupport classificationDiscriminativeMinimumSupport;
	private ClassificationDiscriminativePatternCount classificationDiscriminativePatternCount;
	private ClassificationMaximumPatternLength classificationMaximumPatternLength;
	private ClassificationMinimumPatternLength classificationMinimumPatternLength;
	private ClassificationSameLengthDiscriminativePatternCount classificationSameLengthDiscriminativePatternCount;
	private RFMaxDepth rFMaxDepth;
	private RFNumFeatures rFNumFeatures;
	private RFNumTrees rFNumTrees;
	private RFSeed rfSeed;
	
	public Classification(){
		classificationType = new ClassificationType();
		classificationPatternType = new ClassificationPatternType();
		classificationPatternMinimumSupport = new ClassificationPatternMinimumSupport();
		classificationDiscriminativeMinimumSupport = new ClassificationDiscriminativeMinimumSupport();
		classificationDiscriminativePatternCount = new ClassificationDiscriminativePatternCount();
		classificationMaximumPatternLength = new ClassificationMaximumPatternLength();
		classificationMinimumPatternLength = new ClassificationMinimumPatternLength();
		classificationSameLengthDiscriminativePatternCount = new ClassificationSameLengthDiscriminativePatternCount();
		rFMaxDepth = new RFMaxDepth();
		rFNumFeatures = new RFNumFeatures();
		rFNumTrees = new RFNumTrees();
		rfSeed = new RFSeed();
	}

	@Override
	public String getModuleName() {
		return "Classification";
	}

	@Override
	public List<Parameter> getParameterList() {
		List <Parameter> retval = new ArrayList<Parameter>();
		retval.add(classificationType);
		retval.add(classificationPatternType);
		retval.add(classificationPatternMinimumSupport);
		retval.add(classificationDiscriminativeMinimumSupport);
		retval.add(classificationDiscriminativePatternCount);
		retval.add(classificationMaximumPatternLength);
		retval.add(classificationMinimumPatternLength);
		retval.add(classificationSameLengthDiscriminativePatternCount);
		retval.add(rFMaxDepth);
		retval.add(rFNumFeatures);
		retval.add(rFNumTrees);
		retval.add(rfSeed);
		return retval;
	}

	public ClassificationDiscriminativeMinimumSupport getClassificationDiscriminativeMinimumSupport() {
		return classificationDiscriminativeMinimumSupport;
	}

	public void setClassificationDiscriminativeMinimumSupport(
			ClassificationDiscriminativeMinimumSupport classificationDiscriminativeMinimumSupport) {
		this.classificationDiscriminativeMinimumSupport = classificationDiscriminativeMinimumSupport;
	}

	public final ClassificationType getClassificationType() {
		return classificationType;
	}

	public final void setClassificationType(ClassificationType classificationType) {
		this.classificationType = classificationType;
	}

	public final ClassificationPatternType getClassificationPatternType() {
		return classificationPatternType;
	}

	public final void setClassificationPatternType(
			ClassificationPatternType classificationPatternType) {
		this.classificationPatternType = classificationPatternType;
	}

	public final ClassificationPatternMinimumSupport getClassificationPatternMinimumSupport() {
		return classificationPatternMinimumSupport;
	}

	public final void setClassificationPatternMinimumSupport(
			ClassificationPatternMinimumSupport patternMinimumSupport) {
		this.classificationPatternMinimumSupport = patternMinimumSupport;
	}

	public final ClassificationDiscriminativePatternCount getClassificationDiscriminativePatternCount() {
		return classificationDiscriminativePatternCount;
	}

	public final void setClassificationDiscriminativePatternCount(
			ClassificationDiscriminativePatternCount classificationDiscriminativePatternCount) {
		this.classificationDiscriminativePatternCount = classificationDiscriminativePatternCount;
	}

	public final ClassificationMaximumPatternLength getClassificationMaximumPatternLength() {
		return classificationMaximumPatternLength;
	}

	public final void setClassificationMaximumPatternLength(
			ClassificationMaximumPatternLength classificationMaximumPatternLength) {
		this.classificationMaximumPatternLength = classificationMaximumPatternLength;
	}

	public final ClassificationMinimumPatternLength getClassificationMinimumPatternLength() {
		return classificationMinimumPatternLength;
	}

	public final void setClassificationMinimumPatternLength(
			ClassificationMinimumPatternLength classificationMinimumPatternLength) {
		this.classificationMinimumPatternLength = classificationMinimumPatternLength;
	}

	public final ClassificationSameLengthDiscriminativePatternCount getClassificationSameLengthDiscriminativePatternCount() {
		return classificationSameLengthDiscriminativePatternCount;
	}

	public final void setClassificationSameLengthDiscriminativePatternCount(
			ClassificationSameLengthDiscriminativePatternCount classificationSameLengthDiscriminativePatternCount) {
		this.classificationSameLengthDiscriminativePatternCount = classificationSameLengthDiscriminativePatternCount;
	}

	public final RFMaxDepth getrFMaxDepth() {
		return rFMaxDepth;
	}

	public final void setrFMaxDepth(RFMaxDepth rFMaxDepth) {
		this.rFMaxDepth = rFMaxDepth;
	}

	public final RFNumFeatures getrFNumFeatures() {
		return rFNumFeatures;
	}

	public final void setrFNumFeatures(RFNumFeatures rFNumFeatures) {
		this.rFNumFeatures = rFNumFeatures;
	}

	public final RFNumTrees getrFNumTrees() {
		return rFNumTrees;
	}

	public final void setrFNumTrees(RFNumTrees rFNumTrees) {
		this.rFNumTrees = rFNumTrees;
	}

	public final RFSeed getRfSeed() {
		return rfSeed;
	}

	public final void setRfSeed(RFSeed rfSeed) {
		this.rfSeed = rfSeed;
	}

}

/**/
/*
//missing getters and setters


public enum ClassificationType {DECISION_TREE, RANDOM_FOREST}

private final static ClassificationType classificationType= ClassificationType.RANDOM_FOREST;
public final static int rFMaxDepth = 0;
public final static int rFNumFeatures = 0;
public final static int rFNumTrees = 10;
public final static int rFSeed = 1;
*/