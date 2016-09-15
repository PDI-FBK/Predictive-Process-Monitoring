package org.processmining.plugins.predictive_monitor.bpm.operational_support;

public class ServerTrainingFile {
	private String fileName;
	private int id;
	
	public ServerTrainingFile(String fileName, int id)
	{
		this.fileName=fileName;
		this.id=id;
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public int getId()
	{
		return id;
	}
}
