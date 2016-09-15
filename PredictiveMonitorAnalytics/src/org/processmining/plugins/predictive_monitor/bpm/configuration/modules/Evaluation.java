package org.processmining.plugins.predictive_monitor.bpm.configuration.modules;

import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values.PredictionRun;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.TrainingFile;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values.ClassProbability;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.file_path_values.TestingInputLogFile;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.ClassSupport;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.EvaluationGap;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.EvaluationStartPoint;

public class Evaluation implements Module {
	
	private TestingInputLogFile testingInputLogFile;
	private PredictionRun predictionForEvaluation;
	private ClassProbability classProbability; 
	private ClassSupport classSupport;
	private EvaluationGap evaluationGap;
	private EvaluationStartPoint evaluationStartPoint;

	public Evaluation() {
		testingInputLogFile = new TestingInputLogFile();
		predictionForEvaluation = new PredictionRun();
		classProbability = new ClassProbability();
		classSupport = new ClassSupport();
		evaluationGap = new EvaluationGap();
		evaluationStartPoint = new EvaluationStartPoint();
	}
	
	@Override
	public String getModuleName() {
		return "Prediction and Evalaution";
	}

	@Override
	public List<Parameter> getParameterList() {
		List <Parameter> retval = new ArrayList<Parameter>();
		retval.add(testingInputLogFile);		
		retval.add(predictionForEvaluation);
		retval.add(classProbability);
		retval.add(classSupport);
		retval.add(evaluationGap);
		retval.add(evaluationStartPoint);
		return retval;
	}

	public EvaluationStartPoint getEvaluationStartPoint() {
		return evaluationStartPoint;
	}

	public void setEvaluationStartPoint(EvaluationStartPoint evaluationStartPoint) {
		this.evaluationStartPoint = evaluationStartPoint;
	}

	public final TestingInputLogFile getTestingInputLogFile() {
		return testingInputLogFile;
	}

	public final void setTestingInputLogFile(TestingInputLogFile testingFilePath) {
		this.testingInputLogFile = testingFilePath;
	}

	public final PredictionRun getPredictionForEvaluation() {
		return predictionForEvaluation;
	}

	public final void setPredictionForEvaluation(
			PredictionRun predictionForEvaluation) {
		this.predictionForEvaluation = predictionForEvaluation;
	}

	public final ClassProbability getClassProbability() {
		return classProbability;
	}

	public final void setClassProbability(ClassProbability classProbability) {
		this.classProbability = classProbability;
	}

	public final ClassSupport getClassSupport() {
		return classSupport;
	}

	public final void setClassSupport(ClassSupport classSupport) {
		this.classSupport = classSupport;
	}

	public final EvaluationGap getEvaluationGap() {
		return evaluationGap;
	}

	public final void setEvaluationGap(EvaluationGap evaluationGap) {
		this.evaluationGap = evaluationGap;
	}
}
