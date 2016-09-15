package org.processmining.plugins.predictive_monitor.bpm.configuration.modules;

import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.Parameter;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.boolean_values.UseVotingForClustering;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.ClusteringPatternType;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.ClusteringType;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.ConfidenceAndSupportVotingStrategy;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.HierarchicalDistanceMetrics;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.discrete_values.ModelClusteringFrom;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values.ClusteringDiscriminativeMinimumSupport;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values.ClusteringDiscriminativePatternMinimumSupport;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values.ClusteringPatternMinimumSupport;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.double_values.DBscanEpsilon;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.ClusterNumber;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.ClusteringDiscriminativePatternCount;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.ClusteringMaximumPatternLength;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.ClusteringMinimumPatternLength;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.ClusteringSameLengthDiscriminativePatternCount;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.DBScanMinPoints;
import org.processmining.plugins.predictive_monitor.bpm.configuration.data_structure.integer_values.Voters;

	public class Clustering implements Module {
		
		private ClusteringType clusteringType;
		private ClusteringPatternType clusteringPatternType;
		private ClusteringDiscriminativePatternCount clusteringDiscriminativePatternCount;
		private ClusteringDiscriminativeMinimumSupport clusteringDiscriminativeMinimumSupport;
		private ClusteringMaximumPatternLength clusteringMaximumPatternLength;
		private ClusteringMinimumPatternLength clusteringMinimumPatternLength;
		private ClusteringSameLengthDiscriminativePatternCount clusteringSameLengthDiscriminativePatternCount;
		private HierarchicalDistanceMetrics hierarchicalDistanceMetrics;
		private ModelClusteringFrom modelClusteringFrom; 
		private ClusterNumber clusterNumber;
		private ClusteringPatternMinimumSupport clusteringPatternMinimumSupport;
		private ClusteringDiscriminativePatternMinimumSupport clusteringDiscriminativePatternMinimumSupport;
		private DBscanEpsilon dBscanEpsilon;
		private DBScanMinPoints dBScanMinPoints;
		private ConfidenceAndSupportVotingStrategy confidenceAndSupportVotingStrategy;
		private UseVotingForClustering useVotingForClustering;
		private Voters voters;
		
		public Clustering(){
			clusteringType = new ClusteringType();
			clusteringPatternType = new ClusteringPatternType();
			clusteringDiscriminativePatternCount = new ClusteringDiscriminativePatternCount();
			clusteringDiscriminativeMinimumSupport = new ClusteringDiscriminativeMinimumSupport();
			clusteringMaximumPatternLength = new ClusteringMaximumPatternLength();
			clusteringMinimumPatternLength = new ClusteringMinimumPatternLength();
			clusteringSameLengthDiscriminativePatternCount = new ClusteringSameLengthDiscriminativePatternCount();
			hierarchicalDistanceMetrics = new HierarchicalDistanceMetrics();
			modelClusteringFrom = new ModelClusteringFrom(); 
			clusterNumber = new ClusterNumber();
			clusteringPatternMinimumSupport = new ClusteringPatternMinimumSupport();
			clusteringDiscriminativePatternMinimumSupport = new ClusteringDiscriminativePatternMinimumSupport();
			dBscanEpsilon = new DBscanEpsilon();
			dBScanMinPoints = new DBScanMinPoints();
			confidenceAndSupportVotingStrategy = new ConfidenceAndSupportVotingStrategy();
			useVotingForClustering = new UseVotingForClustering();
			voters = new Voters();
		}

		@Override
		public String getModuleName() {
			return "Clustering";
		}

		@Override
		public List<Parameter> getParameterList() {
			List <Parameter> retval = new ArrayList<Parameter>();
			retval.add(clusteringType);
			retval.add(clusteringPatternType);
			retval.add(clusteringDiscriminativePatternCount);
			retval.add(clusteringDiscriminativeMinimumSupport);
			retval.add(clusteringMaximumPatternLength);
			retval.add(clusteringMinimumPatternLength);
			retval.add(clusteringSameLengthDiscriminativePatternCount);
			retval.add(hierarchicalDistanceMetrics);
			retval.add(modelClusteringFrom);
			retval.add(clusterNumber);
			retval.add(clusteringPatternMinimumSupport);
			retval.add(clusteringDiscriminativePatternMinimumSupport);
			retval.add(dBscanEpsilon);
			retval.add(dBScanMinPoints);
			retval.add(confidenceAndSupportVotingStrategy);
			retval.add(useVotingForClustering);
			retval.add(voters);
			return retval;
		}

		public ClusteringDiscriminativeMinimumSupport getClusteringDiscriminativeMinimumSupport() {
			return clusteringDiscriminativeMinimumSupport;
		}

		public void setClusteringDiscriminativeMinimumSupport(
				ClusteringDiscriminativeMinimumSupport classificationDiscriminativeMinimumSupport) {
			this.clusteringDiscriminativeMinimumSupport = classificationDiscriminativeMinimumSupport;
		}

		public final ClusteringType getClusteringType() {
			return clusteringType;
		}

		public final void setClusteringType(ClusteringType clusteringType) {
			this.clusteringType = clusteringType;
		}

		public final ClusteringPatternType getClusteringPatternType() {
			return clusteringPatternType;
		}

		public final void setClusteringPatternType(
				ClusteringPatternType clusteringPatternType) {
			this.clusteringPatternType = clusteringPatternType;
		}

		public final ClusteringDiscriminativePatternCount getClusteringDiscriminativePatternCount() {
			return clusteringDiscriminativePatternCount;
		}

		public final void setClusteringDiscriminativePatternCount(
				ClusteringDiscriminativePatternCount clusteringDiscriminativePatternCount) {
			this.clusteringDiscriminativePatternCount = clusteringDiscriminativePatternCount;
		}

		public final ClusteringMaximumPatternLength getClusteringMaximumPatternLength() {
			return clusteringMaximumPatternLength;
		}

		public final void setClusteringMaximumPatternLength(
				ClusteringMaximumPatternLength clusteringMaximumPatternLength) {
			this.clusteringMaximumPatternLength = clusteringMaximumPatternLength;
		}

		public final ClusteringMinimumPatternLength getClusteringMinimumPatternLength() {
			return clusteringMinimumPatternLength;
		}

		public final void setClusteringMinimumPatternLength(
				ClusteringMinimumPatternLength clusteringMinimumPatternLength) {
			this.clusteringMinimumPatternLength = clusteringMinimumPatternLength;
		}

		public final ClusteringSameLengthDiscriminativePatternCount getClusteringSameLengthDiscriminativePatternCount() {
			return clusteringSameLengthDiscriminativePatternCount;
		}

		public final void setClusteringSameLengthDiscriminativePatternCount(
				ClusteringSameLengthDiscriminativePatternCount clusteringSameLengthDiscriminativePatternCount) {
			this.clusteringSameLengthDiscriminativePatternCount = clusteringSameLengthDiscriminativePatternCount;
		}

		public final HierarchicalDistanceMetrics getHierarchicalDistanceMetrics() {
			return hierarchicalDistanceMetrics;
		}

		public final void setHierarchicalDistanceMetrics(
				HierarchicalDistanceMetrics hierarchicalDistanceMetrics) {
			this.hierarchicalDistanceMetrics = hierarchicalDistanceMetrics;
		}

		public final ModelClusteringFrom getModelClusteringFrom() {
			return modelClusteringFrom;
		}

		public final void setModelClusteringFrom(ModelClusteringFrom modelClusteringFrom) {
			this.modelClusteringFrom = modelClusteringFrom;
		}

		public final ClusterNumber getClusterNumber() {
			return clusterNumber;
		}

		public final void setClusterNumber(ClusterNumber clusterNumber) {
			this.clusterNumber = clusterNumber;
		}

		public final ClusteringPatternMinimumSupport getClusteringPatternMinimumSupport() {
			return clusteringPatternMinimumSupport;
		}

		public final void setClusteringPatternMinimumSupport(
				ClusteringPatternMinimumSupport clusteringPatternMinimumSupport) {
			this.clusteringPatternMinimumSupport = clusteringPatternMinimumSupport;
		}

		public final ClusteringDiscriminativePatternMinimumSupport getClusteringDiscriminativePatternMinimumSupport() {
			return clusteringDiscriminativePatternMinimumSupport;
		}

		public final void setClusteringDiscriminativePatternMinimumSupport(
				ClusteringDiscriminativePatternMinimumSupport clusteringDiscriminativePatternMinimumSupport) {
			this.clusteringDiscriminativePatternMinimumSupport = clusteringDiscriminativePatternMinimumSupport;
		}

		public final DBscanEpsilon getdBscanEpsilon() {
			return dBscanEpsilon;
		}

		public final void setdBscanEpsilon(DBscanEpsilon dBscanEpsilon) {
			this.dBscanEpsilon = dBscanEpsilon;
		}

		public final DBScanMinPoints getdBScanMinPoints() {
			return dBScanMinPoints;
		}

		public final void setdBScanMinPoints(DBScanMinPoints dBScanMinPoints) {
			this.dBScanMinPoints = dBScanMinPoints;
		}
		
		public final ConfidenceAndSupportVotingStrategy getConfidenceAndSupportVotingStrategy() {
			return confidenceAndSupportVotingStrategy;
		}

		public final void setConfidenceAndSupportVotingStrategy(
				ConfidenceAndSupportVotingStrategy confidenceAndSupportVotingStrategy) {
			this.confidenceAndSupportVotingStrategy = confidenceAndSupportVotingStrategy;
		}
		
		public final UseVotingForClustering getUseVotingForClustering() {
			return useVotingForClustering;
		}

		public final void setUseVotingForClustering(
				UseVotingForClustering useVotingForClustering) {
			this.useVotingForClustering = useVotingForClustering;
		}

		public final Voters getVoters() {
			return voters;
		}

		public final void setVoters(Voters voters) {
			this.voters = voters;
		}

	
//Ora come ora il nome di questi log varia in base alle configurazioni con cui viene avviato il client, quindi in teoria lo user non dovrebbe avervi accesso o sbaglio?
/*	
	//Model Clustering Parameters
	//public final static String toCheckInput ="./input/BPI2011_80_prefixes_gap10.txt";
	public final static String frequencyTracesFilePath = "./input/BPI2011_80_gap" + prefixGap + "_max" + maxPrefixLength + logPart + "_freqs.txt";
	//public final static String frequencyTracesFilePath = "./input/BPI2011_80_gap" + prefixGap + "_max" + maxPrefixLength + "_k" + discriminativePatternCount + "_kprim" + sameLengthDiscriminativePatternCount + "_supp" + (int) (1000 * discriminativePatternMinimumSupport) + "_freqs" + logPart + ".txt";
	public final static String defaultFrequencyTracesFilePath = "./input/BPI2011_80_freqs.txt";
	//public final static String clusteredTracesFilePath = "./input/BPI2011_80_mclust_clustering_k30_kprim29_supp100_gap10_max21_combined.txt";
	//public final static String clusterMeansFilePath = "./input/BPI2011_80_mclust_clusters_mean_k30_kprim29_supp100_gap10_max21_combined.txt";	

	//public final static String clusteredTracesFilePath = "./input/BPI2011_80_mclust_clustering_k" + discriminativePatternCount + "_kprim" + sameLengthDiscriminativePatternCount + "_supp" + (int) (1000 * discriminativePatternMinimumSupport) + "_gap" + prefixGap + "_max" + maxPrefixLength + logPart + ".txt";
	//public final static String clusterMeansFilePath = "./input/BPI2011_80_mclust_clusters_mean_k" + discriminativePatternCount + "_kprim" + sameLengthDiscriminativePatternCount + "_supp" + (int) (1000 * discriminativePatternMinimumSupport) + "_gap" + prefixGap + "_max" + maxPrefixLength + logPart + ".txt";	
	public final static String clusteredTracesFilePath = "./input/BPI2011_80_mclust_gap" + prefixGap + "_max" + maxPrefixLength + logPart + "_clusters_";
	public final static String clusterMeansFilePath = "./input/BPI2011_80_mclust_gap" + prefixGap + "_max" + maxPrefixLength + logPart + "_clusters_mean_";

	private final static String frequencyTracesFilePath = "./input/BPI2011_80_gap" + prefixGap + "_max" + maxPrefixLength + logPart + "_freqs.txt";
	private final static String defaultFrequencyTracesFilePath = "./input/BPI2011_80_freqs.txt";
	
	private final static String clusteredTracesFilePath = "./input/BPI2011_80_mclust_gap" + prefixGap + "_max" + maxPrefixLength + logPart + "_clusters_";
	private final static String clusterMeansFilePath = "./input/BPI2011_80_mclust_gap" + prefixGap + "_max" + maxPrefixLength + logPart + "_clusters_mean_";
*/
}
