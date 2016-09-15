package org.processmining.plugins.predictive_monitor.bpm.server.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import org.processmining.plugins.declareminer.Watch;
import org.processmining.plugins.predictive_monitor.bpm.PredictiveMonitor;
//import org.processmining.plugins.predictive_monitor.bpm.server.DataStructureSerializer;
import org.processmining.plugins.predictive_monitor.bpm.trace_labeling.data_structures.SimpleFormula;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import com.thoughtworks.xstream.XStream;
import com.sun.webkit.Utilities;

public abstract class GenericDataStructure {
	protected Map<String,Object> configuration;
	private final double TOLLERANCE = 0.00001;
	protected final static XStream xstream = new XStream();//DataStructureSerializer();
	protected long initTime;
	protected Watch w;
	
	protected long id;
	
	public GenericDataStructure(Semaphore config, long id) throws InterruptedException
	{
		config.acquire();
		configuration = new HashMap<String, Object>();
		w = new Watch();
		this.id=id;
	}
	
	protected Object getObject(Map<String,Object> configuration, String name)
	{
		Object object = configuration.get(name);
		//configuration.remove(name);
		this.configuration.put(name, object);
		return object;
	}
	
	
	public boolean isCompatible(Map<String,Object> configuration, Semaphore config, Semaphore wait) throws InterruptedException
	{
		config.acquire();
		for(String key: this.configuration.keySet() )
		{
			//System.out.print(key + ": "+this.configuration.get(key));
			//System.out.println("   ->   "+configuration.get(key));
			if(configuration.get(key) instanceof Number)
			{
				double a = ((Number)configuration.get(key)).doubleValue();
				double b = ((Number)this.configuration.get(key)).doubleValue();
				
				if(Math.abs(a-b) >= TOLLERANCE){
					//System.out.println("########### "+configuration.get(key)+"  !=  "+this.configuration.get(key));
					config.release();
					return false;
				}
			}
			else
			{
				if(configuration.get(key)!=this.configuration.get(key)){
					//System.out.println("########### "+configuration.get(key)+"  !=  "+this.configuration.get(key));
					config.release();
					return false;
				}
			}
		}
		config.release();
		wait.acquire();
		wait.release();
		return true;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(object instanceof GenericDataStructure)
		{
			GenericDataStructure structure = (GenericDataStructure)object;
			return structure.getConfiguration().equals(configuration);
		}
		else
		{
			return false;
		}
	}
	
	public Map<String,Object> getConfiguration()
	{
		return configuration;
	}
	
	public long getId()
	{
		return id;
	}
	
	protected void writeConfiguration(String path) throws FileNotFoundException
	{
		new File(path).mkdirs();
		File configurationFile = new File(path+"/configuration.dat");
		PrintWriter pw = new PrintWriter(configurationFile);
        pw.print(xstream.toXML(configuration));
		pw.flush();
		pw.close();
	}
	
	public long getInitTime()
	{
		return initTime;
	}
}
