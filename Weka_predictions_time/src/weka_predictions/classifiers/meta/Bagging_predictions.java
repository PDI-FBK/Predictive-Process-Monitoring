package weka_predictions.classifiers.meta;

import java.util.ArrayList;

import weka_predictions.classifiers.trees.RandomTree;
import weka_predictions.classifiers.trees.RandomTree_predictions;
import weka_predictions.core.Instance;
import weka_predictions.core.Utils;

public class Bagging_predictions extends Bagging {
	
	  /**
	   * Constructor.
	   */
	  public Bagging_predictions() {
	    m_Classifier = new weka_predictions.classifiers.trees.RandomTree_predictions();
	  }

	  public ArrayList<double[]> distributionForInstanceArrayList(Instance instance) throws Exception {

		    ArrayList<double[]> sumsArray = new ArrayList<double[]>(), newProbsArray;
		    double [] sums = new double [instance.numClasses()], newProbs; 
		    double [] sumsValues = new double [instance.numClasses()], newProbsValues; 
		    
		    for (int i = 0; i < m_NumIterations; i++) {
		      if (instance.classAttribute().isNumeric() == true) {
		    	  if (m_Classifiers[i] instanceof RandomTree_predictions){
		    		  RandomTree_predictions classifRT = (RandomTree_predictions) m_Classifiers[i];
		    		  sums[0] += classifRT.classifyInstanceArrayList(instance)[0];
		    		  sumsValues[0] += classifRT.classifyInstanceArrayList(instance)[1];
		    	  } else {
		    		  sums[0] += m_Classifiers[i].classifyInstance(instance);
		    		  sumsValues = null;
		    	  }
		      } else {
		    	  if (m_Classifiers[i] instanceof RandomTree_predictions){
		    		  RandomTree_predictions classifRT = (RandomTree_predictions) m_Classifiers[i];
		    		  newProbsArray = classifRT.distributionForInstanceArrayList(instance);
		    		  newProbs = newProbsArray.get(0);
		    		  newProbsValues = newProbsArray.get(1);
		    		  for (int j = 0; j < newProbs.length; j++){
		    				  sums[j] += newProbs[j];
		    				  sumsValues[j] += newProbsValues[j]; 
		    		  }	    		  
		    	  } else {
		    		  newProbs = m_Classifiers[i].distributionForInstance(instance);
		    		  for (int j = 0; j < newProbs.length; j++){
		    			  sums[j] += newProbs[j];
		    		   }
		    	  	newProbsValues = null;
		    	  }
		      }
		    }
		    if (instance.classAttribute().isNumeric() == true) {
		      sums[0] /= (double)m_NumIterations;
		      sumsValues[0] /= (double)m_NumIterations;
		      sumsArray.add(sums);
		      sumsArray.add(sumsValues);
		      return sumsArray;
		    } else if (Utils.eq(Utils.sum(sums), 0)) {
		    	sumsArray.add(sums);
			    sumsArray.add(sumsValues);
			    return sumsArray;
		    } else {
		      Utils.normalize(sums);
		      sumsArray.add(sums);
		      sumsArray.add(sumsValues);
		      return sumsArray;
		  }  
		 }
	  
	  public ArrayList<double[]> distributionForInstanceArrayListMax(Instance instance) throws Exception {

		    ArrayList<double[]> maxArray = new ArrayList<double[]>(), newProbsArray;
		    double [] max = new double [instance.numClasses()], newProbs; 
		    double [] maxValues = new double [instance.numClasses()], newProbsValues; 
		    
		    for (int i = 0; i < m_NumIterations; i++) {
			      if (instance.classAttribute().isNumeric() == true) {
			    	  if (m_Classifiers[i] instanceof RandomTree){
			    		  RandomTree_predictions classifRT = (RandomTree_predictions) m_Classifiers[i];
			    		  max[0] += classifRT.classifyInstanceArrayList(instance)[0];
			    		  maxValues[0] += classifRT.classifyInstanceArrayList(instance)[1];
			    	  } else {
			    		  max[0] += m_Classifiers[i].classifyInstance(instance);
			    		  maxValues = null;
			    	  }
			      } else {
		    	  if (m_Classifiers[i] instanceof RandomTree){
		    		  RandomTree_predictions classifRT = (RandomTree_predictions) m_Classifiers[i];
		    		  newProbsArray = classifRT.distributionForInstanceArrayList(instance);
		    		  newProbs = newProbsArray.get(0);
		    		  newProbsValues = newProbsArray.get(1);
		    		  //take the index of the highest class in the current distribution
		    		  int newProbMaxIndex = getHighestProbabilityClassIndex(newProbs);
		    		  int maxMaxIndex = getHighestProbabilityClassIndex(max);
		    		  if (newProbMaxIndex>maxMaxIndex){
			    		  for (int j = 0; j < newProbs.length; j++){
		    				  max[j] = newProbs[j];
		    				  maxValues[j] = newProbsValues[j];
			    		  }	    		  
		    		  }
		    	  } else {
		    		  newProbs = m_Classifiers[i].distributionForInstance(instance);
		    		  for (int j = 0; j < newProbs.length; j++){
		    			  if (newProbs[j]>max[j]){
		    				  max[j] = newProbs[j];
		    			  }
		    		   }
		    	  	newProbsValues = null;
		    	  }
		      }
		    }
		      maxArray.add(max);
		      maxArray.add(maxValues);
		      return maxArray;
	/*	    if (instance.classAttribute().isNumeric() == true) {
		      max[0] /= (double)m_NumIterations;
		      maxValues[0] /= (double)m_NumIterations;
		      maxArray.add(max);
		      maxArray.add(maxValues);
		      return maxArray;
		    } else if (Utils.eq(Utils.sum(max), 0)) {
		    	maxArray.add(max);
			    maxArray.add(maxValues);
			    return maxArray;
		    } else {
		      Utils.normalize(max);
		      maxArray.add(max);
		      maxArray.add(maxValues);
		      return maxArray;
		  } */ 
		 }
	  	
	  	private int getHighestProbabilityClassIndex(double[] probDistr){
	  		double max = probDistr[0];
	  		int maxIndex = 0;
			  for (int j = 0; j < probDistr.length; j++){
				  if (probDistr[j]>max){
					  maxIndex =j;
					  max = probDistr[j];
				  }
			  }	
			  return maxIndex;
	  		
	  	}

}
