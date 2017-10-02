package org.processmining.plugins.predictive_monitor.bpm.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.predictive_monitor.bpm.encoding.FrequencyBasedEncoder;
import org.processmining.plugins.predictive_monitor.bpm.utility.XLogReader;

import weka_predictions.classifiers.rules.DecisionTableHashKey;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;
import weka_predictions.core.matrix.Matrix;
import weka_predictions.filters.Filter;
import weka_predictions.filters.unsupervised.attribute.ReplaceMissingValues;

public class ModelBased {
	public double[][] mu;
	public List<double[][]> sigma;
	public double[][] dataMatrix;
	public int[] dataCluster;
	private static Instances traceInstances = null;

	private int numClusters = 2;
	private Instances clusterCentroids;
	private int maxIterations = 10;
	
	public void setNumClusters(int n) throws Exception {
		if (n <= 0) {
			throw new Exception("Number of clusters must be > 0");
		}
		numClusters = n;
	}
	  
	public void buildClusterer(Instances instances) throws Exception{
		clusterCentroids = new Instances(instances, numClusters);
		int[] clusterAssignments = new int[instances.numInstances()];
		
		ReplaceMissingValues replaceMissingFilter = new ReplaceMissingValues();
	    replaceMissingFilter.setInputFormat(instances);
	    instances = Filter.useFilter(instances, replaceMissingFilter);
	    
		/*initialization of the centroids */
		
		Random randomO = new Random();
		int instIndex;
		Instances initInstances = instances;
		HashMap initC = new HashMap();
		DecisionTableHashKey hk = null;
		
		for (int j = initInstances.numInstances() - 1; j >= 0; j--) {
	        instIndex = randomO.nextInt(j + 1);
	        hk = new DecisionTableHashKey(initInstances.instance(instIndex),
	            initInstances.numAttributes(), true);
	        if (!initC.containsKey(hk)) {
	          clusterCentroids.add(initInstances.instance(instIndex));
	          initC.put(hk, null);
	        }
	        initInstances.swap(j, instIndex);

	        if (clusterCentroids.numInstances() == numClusters) {
	          break;
	        }
	    }
		System.out.println("done building clusterer : "+instances.numAttributes());
		
		this.dataMatrix = new double[instances.size()][instances.numAttributes()];
		//populate dataMatrix
		for(int i = 0; i < instances.size(); i++){
			for(int j = 0; j < instances.numAttributes(); j++){
				//convert 0 to -1 to prevent determinant of 0 value
				if(instances.instance(i).value(j) == 0){
					this.dataMatrix[i][j] = -1.0;
				}
				else{
					this.dataMatrix[i][j] = instances.instance(i).value(j);
				}
			}
		}
		
		this.mu = generateMU(numClusters, instances.numAttributes(), instances);
		
		kMeansPlusPlus();
		System.out.println("ending of kmeans++sigma - model based clustering");
	}
	
	public void kMeansPlusPlus(){
		double[][] data = this.dataMatrix;
		double[][] mu = this.mu;
		
		System.out.println("Start of kmeans++ model based clustering");

//		Matrix2 mat = new Matrix2();
		int muLength = mu.length;
		int diagLength = mu[0].length;
		int dataLength = data.length;
		
		int[] maxValues = new int[dataLength];
		
		List<double[][]> sigma = new ArrayList<double[][]>();
		for(int i = 0; i < muLength; i++){
			sigma.add(generateDiag(diagLength));
		}

		double[][] p = generateMatrix(dataLength, muLength);
		int gc = 1;

		for(int i = 0; i < this.maxIterations; i++){
			// Step 1: Find the closest centers
			System.out.println("Step 1: Find the closest centers");
			p = generateMatrix(dataLength, muLength);
			for(int j = 0; j < muLength; j++){
				System.out.println("iteration j = "+j);
				// Compute log likelihoods for clusters.
				double[][] sigmaTemp = sigma.get(j); 
				
				Matrix m = new Matrix(sigmaTemp);
				double determinant =  m.det();
				if(determinant == 0){
					m = new Matrix(generateDiag(diagLength));
					determinant =  m.det();
//					System.out.println("determinant is 0");
				}
				
				for(int k = 0; k < dataLength; k++){
					int col =  mu[j].length;
					double[][] values = new double[col][col];
					for(int n = 0; n < mu[j].length && n < data[k].length; n++){
						values[0][n] = data[k][n]-mu[j][n];
					}
//					System.out.println("iteration k = "+k);				
					double val = 1.0/(2.0 * Math.PI * determinant);
					Matrix valMat = new Matrix(values);
					Matrix matrixMuxResult1 = valMat.times(m.inverse());			
					double[][] matrixMuxResult = matrixMuxResult1.times(valMat.transpose()).getArray();

					double matrixMulValue = matrixMuxResult[0][0];
					
					double exponentValue = Math.exp((-1.0/2.0)*matrixMulValue);
					p[k][j] = val*exponentValue;
				}
			}
			
			System.out.println("Step 1b: Find maximum value");
			// get which index with the maximum value
			int[] groupCount = new int[muLength];
			for(int j = 0; j < maxValues.length; j++){
				maxValues[j] = 1;
				double maxPoint = 0;
				int maxIndex = 0;
				for(int k = 0; k < muLength; k++){
					if(maxPoint < p[j][k]){
						maxPoint = p[j][k];
						maxValues[j] = k;
						maxIndex = k;
					}
				}
				groupCount[maxIndex]++;
			}
			
			// check if each group is represented
			int z = 0; 
			for(int c : groupCount){
				System.out.println("group count of "+ (z++) +" = "+c);
			}
//			if(gc == 0){
//				System.out.println("breaking iteration = no clusters are found");
//				break;
//			}
			
			System.out.println("Step 2: Recalibrate parameters");
			// Step 2: Recalibrate parameters
			// update point (center)
			int checkRandom = -1;
			for(int j = 0; j < muLength; j++){
				double[] sum = new double[data[0].length];
				for(int k = 0; k < dataLength; k++){
					if(j == maxValues[k]){
						for(int n = 0; n < mu[j].length && n < data[k].length; n++){
							sum[n] += data[k][n];
						}
					}
				}
				for(int n = 0; n < mu[j].length; n++){
					if(groupCount[j] > 0){
						mu[j][n] = sum[n]/groupCount[j];
					}
					else{
						checkRandom = j;
//						mu[j][n] = generateRandomDouble();
					}
				}
			}
			
			System.out.println("Update Sigma");
			// Update Sigma
			for(int j = 0; j < muLength; j++){
				if(groupCount[j] > 0){
					//create matrix for data per column
					int limit = groupCount[j];
					double[][] dat = new double[limit][data[0].length];
					int counter = 0;
					for(int k = 0; k < dataLength && counter < limit; k++){
						if(j == maxValues[k]){
							for(int n = 0; n < mu[j].length && n < data[k].length; n++){
								dat[counter][n] = data[k][n] - mu[j][n];
							}
							counter++;
						}
					}
					Matrix dataMat = new Matrix(dat);
					Matrix dataMat2 = new Matrix(dat);
					Matrix transposed = dataMat.transpose();
					double[][] matmul = transposed.times(dataMat2).getArray(); 
					for(int k = 0; k < matmul.length; k++){
						for(int n = 0; n < matmul[0].length; n++){
							if(j == checkRandom || groupCount[j] == 0){
								if(k == n) matmul[k][n] = 1;
								else matmul[k][n] = 0;
							}
							else{
								matmul[k][n] = matmul[k][n]/groupCount[j];
							}
						}					
					}
					sigma.set(j, matmul);					
				}
			}
			
//			System.out.println("iteration #"+i);
		}
		
		this.mu = mu;
		this.sigma = sigma;
		this.dataCluster = maxValues;
		return;
	}
	
	private static double[][] generateMU(int x, int y, Instances instances){

		double[][] matrix = new double[x][y];
		List<Integer> indexes = new ArrayList<Integer>();
		for(int i = 0; i < x; i++){
			Random r = new Random();
			double rangeMin = 0;
			double rangeMax = instances.size();
			double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			if(indexes.contains((int) randomValue)){
				//re-iterate again to get new index
				i--;
				continue;
			}
			else{
				indexes.add((int) randomValue);
			}
			indexes.add((int) randomValue);
			for(int j = 0; j < y; j++){
				double valueToStore = instances.instance((int) randomValue).value(j);
				if(Double.isNaN(valueToStore)){
					matrix[i][j] = -1.0; //assign negative 1 to Nan
				}
				else{
					matrix[i][j] = valueToStore;
				}	
			}
		}
		return matrix;
	}
	
	private static double[][] generateDiag(int x){
		double[][] diagMatrix = new double[x][x];
		for(int i = 0; i < x; i++){
			for(int j = 0; j < x; j++){
				if(j == i)
					diagMatrix[i][j] = 1.0;
				else
					diagMatrix[i][j] = 0;
			}
		}
		
		return diagMatrix;
	}
	
	private static double[][] generateMatrix(int x, int y){
		
		double[][] matrix = new double[x][y];
		for(int i = 0; i < x; i++){
			for(int j = 0; j < y; j++){
				matrix[i][j] = 0;
			}
		}
		return matrix;
	}

	public int clusterInstance(Instance encodedTrace) {

		double[] trace = new double[encodedTrace.numAttributes()];
		for(int j = 0; j < encodedTrace.numAttributes(); j++){
			if(encodedTrace.value(j) == 0){
				trace[j] = -1.0;
			}
			else{
				trace[j] = encodedTrace.value(j);
			}
		}
		
		double[] scores = new double[this.mu.length]; 

		//calculate the scores
		for(int j = 0; j < this.mu.length; j++){
			
			double[][] sigmaTemp = sigma.get(j); 
			
			Matrix m = new Matrix(sigmaTemp);
			double determinant =  m.det();
			if(determinant == 0){
				m = new Matrix(generateDiag(this.mu[0].length));
				determinant =  m.det();
//				System.out.println("determinant is 0");
			}
			
			int col =  this.mu[j].length;
			double[][] values = new double[col][col];
			for(int n = 0; n < this.mu[j].length && n < trace.length; n++){
				values[0][n] = trace[n]-this.mu[j][n];
			}

			double val = 1.0/(2.0 * Math.PI * determinant);
			Matrix valMat = new Matrix(values);
			Matrix matrixMuxResult1 = valMat.times(m.inverse());			
			double[][] matrixMuxResult = matrixMuxResult1.times(valMat.transpose()).getArray();

			double matrixMulValue = matrixMuxResult[0][0];
			
			double exponentValue = Math.exp((-1.0/2.0)*matrixMulValue);
			
			scores[j] = val*exponentValue;
		}
		
		// get which index with the maximum value
		int maxIndex = 0;
		double maxPoint = 0;
		for(int j = 0; j < this.mu.length; j++){
			if(maxPoint < scores[j]){
				maxPoint = scores[j];
				maxIndex = j;
			}
		}
		return maxIndex;
	}
	
}
