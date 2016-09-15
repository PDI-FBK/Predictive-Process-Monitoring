package org.processmining.plugins.predictive_monitor.bpm.pattern_mining.data_structures;

public class TracePrefix implements Comparable<TracePrefix> {
	private double similarity;
	private String prefix;
	private int traceId;
	
	
	public TracePrefix(double similarity, String prefix, int traceId){
		this.similarity= similarity;
		this.prefix = prefix;
		this.traceId = traceId;
	}
	
	public double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	

	public int getTraceId() {
		return traceId;
	}

	public void setTraceId(int traceId) {
		this.traceId = traceId;
	}

	public int compareTo(TracePrefix toCompare) {
		
		Double tocomp = new Double(toCompare.getSimilarity());
		Double thi = new Double(this.similarity);
		
		return tocomp.compareTo(thi);
	}
	
	

}
