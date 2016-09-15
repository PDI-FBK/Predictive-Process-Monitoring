/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    RandomForest.java
 *    Copyright (C) 2001-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package weka_predictions.classifiers.trees;

import java.util.ArrayList;

import weka_predictions.classifiers.meta.Bagging;
import weka_predictions.classifiers.meta.Bagging_predictions;
import weka_predictions.core.Attribute;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;
import weka_predictions.core.Utils;


public class RandomForest_predictions extends RandomForest {
	
	private ArrayList<String> classLabels = new ArrayList<String>();
	  /** The bagger. */
	  //protected Bagging_predictions m_bagger = null;
	  
	 /**
	   * Classifies the given test instance. The instance has to belong to a dataset
	   * when it's being classified. Note that a classifier MUST implement either
	   * this or distributionForInstance().
	   * 
	   * @param instance the instance to be classified
	   * @return the predicted most likely class for the instance or
	   *         Utils.missingValue() if no prediction is made
	   * @exception Exception if an error occurred during the prediction
	   */
	  @Override
	  public double classifyInstance(Instance instance) throws Exception {

	    double[] dist = distributionForInstance(instance);
	    if (dist == null) {
	      throw new Exception("Null distribution predicted");
	    }
	    switch (instance.classAttribute().type()) {
	    case Attribute.NOMINAL:
	      double max = 0;
	      int maxIndex = 0;

	      for (int i = 0; i < dist.length; i++) {
	        if (dist[i] > max) {
	          maxIndex = i;
	          max = dist[i];
	        }
	      }
	      if (max > 0) {
	        return maxIndex;
	      } else {
	        return Utils.missingValue();
	      }
	    case Attribute.NUMERIC:
	    case Attribute.DATE:
	      return dist[0];
	    default:
	      return Utils.missingValue();
	    }
	  }

	  /**
	   * Predicts the class memberships for a given instance. If an instance is
	   * unclassified, the returned array elements must be all zero. If the class is
	   * numeric, the array must consist of only one element, which contains the
	   * predicted value. Note that a classifier MUST implement either this or
	   * classifyInstance().
	   * 
	   * @param instance the instance to be classified
	   * @return an array containing the estimated membership probabilities of the
	   *         test instance in each class or the numeric prediction
	   * @exception Exception if distribution could not be computed successfully
	   */
/*	  @Override
	  public double[] distributionForInstance(Instance instance) throws Exception {

	    double[] dist = new double[instance.numClasses()];
	    switch (instance.classAttribute().type()) {
	    case Attribute.NOMINAL:
	      double classification = classifyInstance(instance);
	      if (Utils.isMissingValue(classification)) {
	        return dist;
	      } else {
	        dist[(int) classification] = 1.0;
	      }
	      return dist;
	    case Attribute.NUMERIC:
	    case Attribute.DATE:
	      dist[0] = classifyInstance(instance);
	      return dist;
	    default:
	      return dist;
	    }
	  }*/

	  public ArrayList<double[]> distributionForInstanceArrayList(Instance instance) throws Exception {

		    return ((Bagging_predictions)m_bagger).distributionForInstanceArrayList(instance);
	  }
	  
	  public ArrayList<double[]> distributionForInstanceArrayListMax(Instance instance) throws Exception {

		    return ((Bagging_predictions)m_bagger).distributionForInstanceArrayListMax(instance);
	}
	  
	  /**
	   * Builds a classifier for a set of instances.
	   *
	   * @param data the instances to train the classifier with
	   * @throws Exception if something goes wrong
	   */
	  public void buildClassifier(Instances data) throws Exception {

	    // can classifier handle the data?
	    getCapabilities().testWithFail(data);

	    // remove instances with missing class
	    data = new Instances(data);
	    data.deleteWithMissingClass();
	    
	    m_bagger = new Bagging_predictions();
	    RandomTree rTree = new RandomTree_predictions();

	    // set up the random tree options
	    m_KValue = m_numFeatures;
	    if (m_KValue < 1) m_KValue = (int) Utils.log2(data.numAttributes())+1;
	    rTree.setKValue(m_KValue);
	    rTree.setMaxDepth(getMaxDepth());

	    // set up the bagger and build the forest
	    m_bagger.setClassifier(rTree);
	    m_bagger.setSeed(m_randomSeed);
	    m_bagger.setNumIterations(m_numTrees);
	    m_bagger.setCalcOutOfBag(true);
	    m_bagger.setNumExecutionSlots(m_numExecutionSlots);
	    m_bagger.buildClassifier(data);
	  }

	public ArrayList<String> getClassLabels() {
		return classLabels;
	}

	public void setClassLabels(ArrayList<String> classLabels) {
		this.classLabels = classLabels;
	}
	
	
 }



