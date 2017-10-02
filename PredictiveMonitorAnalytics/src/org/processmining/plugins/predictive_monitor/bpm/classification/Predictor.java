package org.processmining.plugins.predictive_monitor.bpm.classification;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.classifier.Classifier;
import org.processmining.plugins.predictive_monitor.bpm.utility.Print;

import weka_predictions.classifiers.trees.RandomForest;
import weka_predictions.classifiers.trees.RandomForest_predictions;
import weka_predictions.classifiers.trees.j48.J48_predictions;
import weka_predictions.core.Instance;
import weka_predictions.core.Instances;
import weka_predictions.data_predictions.Result;
import weka_predictions.data_predictions.ResultRandomForest;


public class Predictor {

	/**
	 *
	 *
	 * DECISION TREE
	 *
	 *
	 *
	 */

	private static Print print = new Print();
	public static J48_predictions trainDecisionTree(Instances inst,Classifier classifier){
		J48_predictions tree = new J48_predictions();
		 try {
			 	Thread.sleep(1000);
		 		//BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
				Instances train = inst;
				//reader.close();
				// setting class attribute
				train.setClassIndex(train.numAttributes() - 1);


				String[] options = new String[3];
				options[0] = "-p";
				options[1] = "-C";
				options[2] = "0.25";
		//		options[3]="-M";
		//		options[4] = "1";
			//	options[5] = "-R";
				 // new instance of tree
				tree.setOptions(options);     // set the options
				tree.setClassLabels((ArrayList<String>)classifier.getLabels());
				tree.buildClassifier(train);   // build classifier
//				String graphString = tree.graph();

				//System.out.println(tree.toString());


			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		 return tree;

	}


	public static Map<String, Result> makePredictionDecisionTree( J48_predictions tree, Vector<String> currentVariables, Map<String, String> variables){
		Map<String, Result> results = new HashMap<String, Result>();
		
		if(tree!=null){
		results = tree.computeConditionInfo(currentVariables, variables);
		}
		
//		print.thatln("SIMPLIFIED PRETTY STRING");
//		if (results!=null && !results.isEmpty()){
//			for (String string : results.keySet()) {
//				Result currResult = results.get(string);
//				print.thatln(string + " "+currResult.simplifiedPrettyString());
//			}
//		}
		 return results;
	}


	/***
	 *
	 * RANDOM FOREST
	 *
	 */

	public static Map<String, Result> makePredictionRandomForest(Instances inst, Instance instance){
		Map<String, Result> results = new HashMap<String, Result>();
		 try {
			 	Thread.sleep(1000);
				Instances train = inst;
				// setting class attribute
				train.setClassIndex(train.numAttributes() - 1);


/*				String[] options = new String[3];
				options[0] = "-p";
				options[1] = "-C";
				options[2] = "0.6";*/

				RandomForest rF = new RandomForest();
				//rF.setOptions(options);
				rF.setMaxDepth(0);
				rF.setNumFeatures(100);
				rF.setNumTrees(1000);
				rF.setSeed(1);
				//rF.setPrintTrees(true);


				rF.buildClassifier(train);

/*				Instances instances = new Instances(train, 1);
				instances.add(instance);
				ArffSaver saver = new ArffSaver();
				saver.setInstances(instances);
				saver.setFile(new File("./output/test.arff"));
				saver.setDestination(new File("./out/test.arff"));   // **not** necessary in 3.5.4 and later
				saver.writeBatch();*/

				instance.setDataset(train);

/*				instances = new Instances(train, 1);
				instances.add(instance);
				saver = new ArffSaver();
				saver.setInstances(instances);
				saver.setFile(new File("./output/test2.arff"));
				saver.setDestination(new File("./out/test2.arff"));   // **not** necessary in 3.5.4 and later
				saver.writeBatch();*/
/*				ArrayList<weka.core.Attribute> attrs = new ArrayList<weka.core.Attribute>();
				while( instance.enumerateAttributes().hasMoreElements()) {
					attrs.add((weka.core.Attribute) instance.enumerateAttributes().nextElement());
				}
				Instances instances = new Instances("I", attrs, 1);
				instances.add(instance);

				train.equalHeaders(instances);*/
				/*
				if(ServerConfigurationClass.printDebug){
					System.out.println(train.numAttributes()+" "+instance.numAttributes());

					for (int i = 0; i < train.numAttributes(); i++) {
						System.out.println(train.attribute(i).numValues()+" "+instance.attribute(i).numValues()+" "+instance.value(i));
					}				

				System.out.println("THE END***********************************");
				
				}*/


				double p =rF.classifyInstance(instance);
				double[] d =rF.distributionForInstance(instance);

				//rF.classifyInstance(instance)
				print.thatln(rF);

				for (int i = 0; i < d.length; i++) {
					double e = d[i];
					print.thatln(i+" "+e);

				}
				ResultRandomForest result = null;

				if (d[0]>=d[1])
					result = new ResultRandomForest(0., d[0], "yes");
				else
					result = new ResultRandomForest(0., d[1], "no");

				results.put("yes", result);

/*				MyJ48 tree = new MyJ48();     // new instance of tree
				tree.setOptions(options);     // set the options
				tree.buildClassifier(train);   // build classifier
				String graphString = tree.graph();


				results = tree.computeConditionInfo(currentVariables, variables);
				for (String string : results.keySet()) {
					System.out.println(string + " "+results.get(string).prettyString());
				}
				System.out. println("SIMPLIFIED PRETTY STRING");
				if (results!=null && !results.isEmpty()){
					for (String string : results.keySet()) {
						Result currResult = results.get(string);
						System.out.println(string + " "+currResult.simplifiedPrettyString());
					}
				}
*/
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		 return results;

	}


	public static Map<String, Result> makePredictionRandomForest(RandomForest_predictions rF, Instances inst, Instance instance){
		Map<String, Result> results = new HashMap<String, Result>();
		 try {
			 	//Thread.sleep(1000); //??
				Instances train = inst;
				// setting class attribute
				train.setClassIndex(train.numAttributes()-1);

				instance.setDataset(train);

				//double p =rF.classifyInstance(instance);
				ArrayList<double[]> dArray =rF.distributionForInstanceArrayList(instance);
				double[] d = dArray.get(0);
				double[] dValues = dArray.get(1);
				//double[] d = rF.distributionForInstance(instance);

				//rF.classifyInstance(instance)
				print.thatln(rF);
				int maxIndex = 0;
				for (int i = 0; i < d.length; i++) {
					double e = d[i];
					print.thatln(i+" "+e);
					if (d[i]>d[maxIndex]) 
						maxIndex=i;
				}
				ResultRandomForest result = null;
				
				result = new ResultRandomForest(dValues[maxIndex], d[maxIndex], rF.getClassLabels().get(maxIndex));

/*				if (d[0]>=d[1])
					result = new ResultRandomForest(dValues[0], d[0], "yes");
				else
					result = new ResultRandomForest(dValues[1], d[1], "no");*/
/*				if (d[0]>=d[1])
					result = new ResultRandomForest(0., d[0], true);
				else
					result = new ResultRandomForest(0., d[1], false);		*/

				results.put(rF.getClassLabels().get(maxIndex), result);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		 return results;

	}

	public static RandomForest_predictions trainRandomForest(Instances inst, Classifier classifier, int rFMaxDepth, int rFNumFeatures, int rFNumTrees, int rFSeed){
		Map<String, Result> results = new HashMap<String, Result>();

			RandomForest_predictions rF = new RandomForest_predictions();
			rF.setMaxDepth(rFMaxDepth);
			rF.setNumFeatures(rFNumFeatures);
			rF.setNumTrees(rFNumTrees);
			rF.setSeed(rFSeed);
			rF.setClassLabels((ArrayList<String>)classifier.getLabels());
		try {
			Thread.sleep(1000);

			Instances train = inst;
			// setting class attribute
			train.setClassIndex(train.numAttributes() - 1);


			//rF.setPrintTrees(true);


			rF.buildClassifier(train);
/*			int z = 0;
			for (Instance instance : train) {
				double re = rF.classifyInstance(instance);
				double[] aa = rF.distributionForInstance(instance);
				System.out.println(z+" "+re+" "+aa[0]+" "+aa[1]);
				z++;
			}*/
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return rF;

	}


/*
	public static RandomForest trainRandomForest2(String inputFilePath){
		Map<String, Result> results = new HashMap<String, Result>();
	
		RandomForest rF = new RandomForest();
		rF.setMaxDepth(ServerConfigurationClass.rFMaxDepth);
		rF.setNumFeatures(ServerConfigurationClass.rFNumFeatures);
		rF.setNumTrees(ServerConfigurationClass.rFNumTrees);
		rF.setSeed(ServerConfigurationClass.rFSeed);
		try {
			Thread.sleep(1000);
	
	 		BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
			Instances train = new Instances(reader);
			reader.close();
			// setting class attribute
			train.setClassIndex(train.numAttributes() - 1);
	
	
			//rF.setPrintTrees(true);
	
	
			rF.buildClassifier(train);
			print.thatln("ORIGINAL-----------------------------------");
			int z = 0;
			for (Instance instance : train) {
				double re = rF.classifyInstance(instance);
				double[] aa = rF.distributionForInstance(instance);
				print.thatln(z+" "+re+" "+aa[0]+" "+aa[1]);
				z++;
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rF;
	}
*/
}

