package weka_predictions.classifiers.pmml.consumer;

import weka_predictions.classifiers.evaluation.EvaluationUtils;
import weka_predictions.classifiers.pmml.consumer.PMMLClassifier;
import weka_predictions.core.Attribute;
import weka_predictions.core.FastVector;
import weka_predictions.core.Instances;
import weka_predictions.core.pmml.PMMLFactory;
import weka_predictions.core.pmml.PMMLModel;
import weka_predictions.test.Regression;

import java.io.*;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

public abstract class AbstractPMMLClassifierTest extends TestCase {

  protected FastVector m_modelNames = new FastVector();
  protected FastVector m_dataSetNames = new FastVector();

  public AbstractPMMLClassifierTest(String name) { 
    super(name); 
  }

  public Instances getData(String name) {
    Instances elnino = null;
    try {
      elnino = 
        new Instances(new BufferedReader(new InputStreamReader(
          ClassLoader.getSystemResourceAsStream("weka/classifiers/pmml/data/" + name))));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return elnino;
  }

  public PMMLClassifier getClassifier(String name) {
    PMMLClassifier regression = null;
    try {
      PMMLModel model = 
        PMMLFactory.getPMMLModel(new BufferedInputStream(ClassLoader.getSystemResourceAsStream(
                  "weka/classifiers/pmml/data/" + name)));

      regression = (PMMLClassifier)model;

    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return regression;
  }

  public void testRegression() throws Exception {

    PMMLClassifier classifier = null;
    Instances testData = null;
    EvaluationUtils evalUtils = null; 
    weka_predictions.test.Regression reg = new weka_predictions.test.Regression(this.getClass());

    FastVector predictions = null;
    boolean success = false;
    for (int i = 0; i < m_modelNames.size(); i++) {
      classifier = getClassifier((String)m_modelNames.elementAt(i));
      testData = getData((String)m_dataSetNames.elementAt(i));
      evalUtils = new EvaluationUtils();

      try {
        String  className = classifier.getMiningSchema().getFieldsAsInstances().classAttribute().name();
        Attribute classAtt = testData.attribute(className);
        testData.setClass(classAtt);
        predictions = evalUtils.getTestPredictions(classifier, testData);
        success = true;
        String predsString = weka_predictions.classifiers.AbstractClassifierTest.predictionsToString(predictions);
        reg.println(predsString);
      } catch (Exception ex) {
        ex.printStackTrace();
        String msg = ex.getMessage().toLowerCase();
        if (msg.indexOf("not in classpath") > -1) {
          return;
        }
      }
    }

    if (!success) {
      fail("Problem during regression testing: no successful predictions generated");
    }

    try {
      String diff = reg.diff();
      if (diff == null) {
        System.err.println("Warning: No reference available, creating."); 
      } else if (!diff.equals("")) {
        fail("Regression test failed. Difference:\n" + diff);
      }
    }  catch (java.io.IOException ex) {
      fail("Problem during regression testing.\n" + ex);
    }    
  }
}