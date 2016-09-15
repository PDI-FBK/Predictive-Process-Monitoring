package weka_predictions.classifiers.trees;

import java.util.ArrayList;
import java.util.Random;

import weka_predictions.classifiers.trees.RandomTree.Tree;
import weka_predictions.core.Attribute;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;
import weka_predictions.core.Utils;

public class RandomTree_predictions extends RandomTree{
	  /** The Tree object */
	  //protected Tree_predictions m_Tree = null;
	
	protected class Tree_predictions extends Tree {
	    /** The subtrees appended to this tree. */
	    //protected Tree_predictions[] m_Successors;
		
	    /**
	     * Recursively generates a tree.
	     * 
	     * @param data the data to work with
	     * @param classProbs the class distribution
	     * @param attIndicesWindow the attribute window to choose attributes from
	     * @param random random number generator for choosing random attributes
	     * @param depth the current depth
	     * @throws Exception if generation fails
	     */
	    protected void buildTree(Instances data, double[] classProbs,
	        int[] attIndicesWindow, Random random, int depth) throws Exception {

	      // Make leaf if there are no training instances
	      if (data.numInstances() == 0) {
	        m_Attribute = -1;
	        m_ClassDistribution = null;
	        m_Prop = null;
	        return;
	      }

	      // Check if node doesn't contain enough instances or is pure
	      // or maximum depth reached
	      m_ClassDistribution = classProbs.clone();

	      if (Utils.sum(m_ClassDistribution) < 2 * m_MinNum
	          || Utils.eq(m_ClassDistribution[Utils.maxIndex(m_ClassDistribution)],
	              Utils.sum(m_ClassDistribution))
	          || ((getMaxDepth() > 0) && (depth >= getMaxDepth()))) {
	        // Make leaf
	        m_Attribute = -1;
	        m_Prop = null;
	        return;
	      }

	      // Compute class distributions and value of splitting
	      // criterion for each attribute
	      double val = -Double.MAX_VALUE;
	      double split = -Double.MAX_VALUE;
	      double[][] bestDists = null;
	      double[] bestProps = null;
	      int bestIndex = 0;

	      // Handles to get arrays out of distribution method
	      double[][] props = new double[1][0];
	      double[][][] dists = new double[1][0][0];

	      // Investigate K random attributes
	      int attIndex = 0;
	      int windowSize = attIndicesWindow.length;
	      int k = m_KValue;
	      boolean gainFound = false;
	      while ((windowSize > 0) && (k-- > 0 || !gainFound)) {

	        int chosenIndex = random.nextInt(windowSize);
	        attIndex = attIndicesWindow[chosenIndex];

	        // shift chosen attIndex out of window
	        attIndicesWindow[chosenIndex] = attIndicesWindow[windowSize - 1];
	        attIndicesWindow[windowSize - 1] = attIndex;
	        windowSize--;

	        double currSplit = distribution(props, dists, attIndex, data);
	        double currVal = gain(dists[0], priorVal(dists[0]));

	        if (Utils.gr(currVal, 0))
	          gainFound = true;

	        if ((currVal > val) || ((currVal == val) && (attIndex < bestIndex))) {
	          val = currVal;
	          bestIndex = attIndex;
	          split = currSplit;
	          bestProps = props[0];
	          bestDists = dists[0];
	        }
	      }

	      // Find best attribute
	      m_Attribute = bestIndex;

	      // Any useful split found?
	      if (Utils.gr(val, 0)) {

	        // Build subtrees
	        m_SplitPoint = split;
	        m_Prop = bestProps;
	        Instances[] subsets = splitData(data);
	        m_Successors = new Tree_predictions[bestDists.length];
	        for (int i = 0; i < bestDists.length; i++) {
	          m_Successors[i] = new Tree_predictions();
	          m_Successors[i].buildTree(subsets[i], bestDists[i], attIndicesWindow,
	              random, depth + 1);
	        }

	        // If all successors are non-empty, we don't need to store the class
	        // distribution
	        boolean emptySuccessor = false;
	        for (int i = 0; i < subsets.length; i++) {
	          if (m_Successors[i].m_ClassDistribution == null) {
	            emptySuccessor = true;
	            break;
	          }
	        }
	        if (!emptySuccessor) {
	          m_ClassDistribution = null;
	        }
	      } else {

	        // Make leaf
	        m_Attribute = -1;
	      }
	    }

		
		
	    public ArrayList<double[]> distributionForInstanceArraylist(Instance instance) throws Exception {
	    	  ArrayList<double[]> distribution = new ArrayList<double[]>();
	    	    if (m_zeroR != null) {
	    	    	distribution.set(0, m_zeroR.distributionForInstance(instance));
	    	      return distribution;
	    	    } else {
	    	      return ((Tree_predictions)m_Tree).distributionForInstanceArrayList(instance);
	    	    }
	      }
	
	      
	      public ArrayList<double[]> distributionForInstanceArrayList(Instance instance) throws Exception {
	          ArrayList<double[]> returned = new ArrayList<double[]>();
	          double[] returnedDist = null;
	          double[] returnedSupp = null;
	
	          if (m_Attribute > -1) {
	
	            // Node is not a leaf
	            if (instance.isMissing(m_Attribute)) {
	
	              // Value is missing
	              returnedDist = new double[m_Info.numClasses()];
	              returnedSupp = new double[m_Info.numClasses()];
	
	              // Split instance up
	              for (int i = 0; i < m_Successors.length; i++) {
	            	  ArrayList<double[]> doubleHelp =  ((Tree_predictions)m_Successors[i]).distributionForInstanceArrayList(instance);
	            	  if (!doubleHelp.isEmpty()) {
	  	              double[] help =doubleHelp.get(0);
	  	              double[] helpSupp = doubleHelp.get(1);
	  	              if (help != null) {
	  	                for (int j = 0; j < help.length; j++) {
	  	                  returnedDist[j] += m_Prop[i] * help[j];
	  	                  returnedSupp[j] += m_Prop[i] * helpSupp[j];
	  	                }
	  	              }
	  	            }
	  	            returned.add(returnedDist);
	  	            returned.add(returnedSupp);
	              }
	            } else if (m_Info.attribute(m_Attribute).isNominal()) {
	            		// For nominal attributes
	            		returned = ((Tree_predictions)m_Successors[(int) instance.value(m_Attribute)])
	                        .distributionForInstanceArrayList(instance);
	            	} else {
	    	
	    		          // For numeric attributes
	    		          if (instance.value(m_Attribute) < m_SplitPoint) {
	    		        	   returned = ((Tree_predictions)m_Successors[0]).distributionForInstanceArrayList(instance); 
	    		            
	    		          } else {
	    		        	 returned  = ((Tree_predictions)m_Successors[1]).distributionForInstanceArrayList(instance);
	    		          }
	    	        }
	          }
	
	          // Node is a leaf or successor is empty?
	          if ((m_Attribute == -1) || returned == null || returned.isEmpty() || (returned.get(0) == null)) {
	
	            // Is node empty?
	            if (m_ClassDistribution == null) {
	              if (getAllowUnclassifiedInstances()) {
	            	returned.add(new double[m_Info.numClasses()]);
	            	returned.add(new double[m_Info.numClasses()]);
	                return returned;
	              } else {
	                return returned;
	              }
	            }
	
	            // Else return normalized distribution
	            double[] normalizedDistribution = m_ClassDistribution.clone();
	            Utils.normalize(normalizedDistribution);
	            returned.add(normalizedDistribution);
	            returned.add( m_ClassDistribution);
	            return returned;
	          } else {
	            return returned;
	          }
	        }    
	}
	 public ArrayList<double[]> distributionForInstanceArrayList(Instance instance) throws Exception {

		    if (m_zeroR != null) {
		    	ArrayList<double[]> m_zeroArray = new ArrayList<double[]>();
		    	m_zeroArray.add( m_zeroR.distributionForInstance(instance));
		    	return m_zeroArray;
		    } else {
		      return ((Tree_predictions)m_Tree).distributionForInstanceArrayList(instance);
		    }
		  }  
	  
	  public double[] classifyInstanceArrayList(Instance instance) throws Exception {
		  	ArrayList<double[]> distArray = distributionForInstanceArrayList(instance);
			double[] maxArray = new double[2];
		    if (distArray==null || distArray.isEmpty() || distArray.get(0) == null) {
			      throw new Exception("Null distribution predicted");
		    }
		    double [] dist = distArray.get(0);
		    double [] distValues = distArray.get(1);

		    switch (instance.classAttribute().type()) {
		    case Attribute.NOMINAL:
		      double max = 0;
		      int maxIndex = 0;
		      double maxValue = 0.0;
		    		  
		      for (int i = 0; i < dist.length; i++) {
		        if (dist[i] > max) {
		          maxIndex = i;
		          max = dist[i];
		          maxValue = distValues[i];
		        }
		      }
		      if (max > 0) {
		    	  maxArray[0] = maxIndex;
		    	  maxArray[1] = maxValue;
		      } else {
		        maxArray[0] = Utils.missingValue();
		        maxArray[1] = Utils.missingValue();
		      }
		    case Attribute.NUMERIC:
		    	maxArray[0] = dist[0];
		        maxArray[1] = distValues[0];

		    default:
		        maxArray[0] = Utils.missingValue();
		        maxArray[1] = Utils.missingValue();
		    }
		    return maxArray;
		  }
	  
	  /**
	   * Builds classifier.
	   * 
	   * @param data the data to train with
	   * @throws Exception if something goes wrong or the data doesn't fit
	   */
	  @Override
	  public void buildClassifier(Instances data) throws Exception {

	    // Make sure K value is in range
	    if (m_KValue > data.numAttributes() - 1)
	      m_KValue = data.numAttributes() - 1;
	    if (m_KValue < 1)
	      m_KValue = (int) Utils.log2(data.numAttributes()) + 1;

	    // can classifier handle the data?
	    getCapabilities().testWithFail(data);

	    // remove instances with missing class
	    data = new Instances(data);
	    data.deleteWithMissingClass();

	    // only class? -> build ZeroR model
	    if (data.numAttributes() == 1) {
	      System.err
	          .println("Cannot build model (only class attribute present in data!), "
	              + "using ZeroR model instead!");
	      m_zeroR = new weka_predictions.classifiers.rules.ZeroR();
	      m_zeroR.buildClassifier(data);
	      return;
	    } else {
	      m_zeroR = null;
	    }

	    // Figure out appropriate datasets
	    Instances train = null;
	    Instances backfit = null;
	    Random rand = data.getRandomNumberGenerator(m_randomSeed);
	    if (m_NumFolds <= 0) {
	      train = data;
	    } else {
	      data.randomize(rand);
	      data.stratify(m_NumFolds);
	      train = data.trainCV(m_NumFolds, 1, rand);
	      backfit = data.testCV(m_NumFolds, 1);
	    }

	    // Create the attribute indices window
	    int[] attIndicesWindow = new int[data.numAttributes() - 1];
	    int j = 0;
	    for (int i = 0; i < attIndicesWindow.length; i++) {
	      if (j == data.classIndex())
	        j++; // do not include the class
	      attIndicesWindow[i] = j++;
	    }

	    // Compute initial class counts
	    double[] classProbs = new double[train.numClasses()];
	    for (int i = 0; i < train.numInstances(); i++) {
	      Instance inst = train.instance(i);
	      classProbs[(int) inst.classValue()] += inst.weight();
	    }

	    // Build tree
	    m_Tree = new Tree_predictions();
	    m_Info = new Instances(data, 0);
	    m_Tree.buildTree(train, classProbs, attIndicesWindow, rand, 0);

	    // Backfit if required
	    if (backfit != null) {
	      m_Tree.backfitData(backfit);
	    }
	  }

}
