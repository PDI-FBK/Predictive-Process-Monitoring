package org.processmining.plugins.predictive_monitor.bpm.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.predictive_monitor.bpm.encoding.FrequencyBasedEncoder;
import org.processmining.plugins.predictive_monitor.bpm.utility.XLogReader;

import weka_predictions.classifiers.rules.DecisionTableHashKey;
import weka_predictions.core.Instances;
import weka_predictions.core.matrix.Matrix;

public class ModelBased {
	public double[][] mu;
	public List<double[][]> sigma;
	public double[][] dataMatrix;
	public int[] dataCluster;
	private static Instances traceInstances = null;

	private int numClusters = 2;
	private Instances clusterCentroids;
	private int maxIterations = 100;
	private int iterations = 0;
	
	public static void main(String[] args){
		System.out.println("here in model based");
		FrequencyBasedEncoder fbEncoder = new FrequencyBasedEncoder();
		String inputLogFilePath = "./input/old/BPI2011_20.xes";
		XLog trainingLog = null;
		try {
			trainingLog = XLogReader.openLog(inputLogFilePath);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		fbEncoder.encodeTraces(trainingLog);
		traceInstances = fbEncoder.getEncodedTraces();
		ModelBased mb = new ModelBased();
		
		try {
			mb.setNumClusters(20);
			mb.buildClusterer(traceInstances);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("done encoding");
	}
	
	public void setNumClusters(int n) throws Exception {
		if (n <= 0) {
			throw new Exception("Number of clusters must be > 0");
		}
		numClusters = n;
	}
	  
	public void buildClusterer(Instances instances) throws Exception{
		clusterCentroids = new Instances(instances, numClusters);
		int[] clusterAssignments = new int[instances.numInstances()];
		
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
				System.out.println("index i = "+ i + ", j = "+j);
				this.dataMatrix[i][j] = instances.instance(i).value(j);
			}
		}
		
		this.mu = generateMU(numClusters, instances.numAttributes(), instances);
		
		System.out.println("creating mu ");
		kMeansPlusPlus();
		System.out.println("ending of kmeans++sigma");
	}
	
	public void kMeansPlusPlus(){
		double[][] data = this.dataMatrix;
		double[][] mu = this.mu;
		
		System.out.println("Start of kmeans++");

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

		for(int i = 0; i < 100; i++){
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
					System.out.println("determinant is 0");
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
			for(int c : groupCount){
				System.out.println("group count = "+c);
				if(c == 0) {
					gc = 0;
					break;
				}
			}
			if(gc == 0){
				System.out.println("breaking iteration = no clusters are found");
				break;
			}
			
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
					double[][] dat = new double[groupCount[j]][data[0].length];
					int counter = 0;
					for(int k = 0; k < dataLength; k++){
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
							if(j == checkRandom){
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
			
			System.out.println("iteration #"+i);
		}
		
		this.mu = mu;
		this.sigma = sigma;
		this.dataCluster = maxValues;
		return;
	}
	
	public void calculateDistance(){
		
	}
	
	private static double[][] generateMU(int x, int y, Instances instances){

		double[][] matrix = new double[x][y];
		for(int i = 0; i < x; i++){
			Random r = new Random();
			double rangeMin = 0;
			double rangeMax = instances.size();
			double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			for(int j = 0; j < y; j++){
				matrix[i][j] = instances.instance((int) randomValue).value(j);
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
	
}
