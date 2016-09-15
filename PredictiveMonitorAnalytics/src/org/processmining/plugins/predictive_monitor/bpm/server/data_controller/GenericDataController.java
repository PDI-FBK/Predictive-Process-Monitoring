package org.processmining.plugins.predictive_monitor.bpm.server.data_controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.processmining.plugins.predictive_monitor.bpm.PredictiveMonitor;
import org.processmining.plugins.predictive_monitor.bpm.server.DataStructureSerializer;

import com.thoughtworks.xstream.XStream;

public class GenericDataController {
	protected long id;
	protected Semaphore config;
	protected Semaphore wait;
	protected String path;
	protected final XStream xstream = new DataStructureSerializer();
	protected Map<Long,Map<String,Object>> configurations;
	private final double TOLLERANCE = 0.00001;
	
	protected GenericDataController()
	{
		config = new Semaphore(1,true);
		wait = new Semaphore(1,true);
		path = PredictiveMonitor.rootDataStructureFolder;
		configurations = new HashMap<>();
	}
	
	protected void loadConfigurations(String path) throws IOException
	{
		File dir = new File(path);
		Long maxId = new Long(-1);
		if(!dir.mkdirs())
		{
			Collection<File> files = FileUtils.listFiles(
					  dir, 
					  new RegexFileFilter("^(.*?)"), 
					  DirectoryFileFilter.DIRECTORY
					);
			for(File f: files)
			{
				if(f.getName().equals("configuration.dat"))
				{
					String scId = f.getParent().replace(path+"/", "");
					Long cId = Long.parseLong(scId);
					if(maxId<cId)
					{
						maxId = cId;
					}
					
					InputStream is = new FileInputStream(f);
					
					configurations.put(cId, (Map<String,Object>)xstream.fromXML(is));
				}
			}
		}
		System.out.println(configurations);
		id = maxId+1;
	}

	protected boolean isCompatible(Map<String,Object> currentConfiguration, Long configurationIndex) throws InterruptedException
	{
		config.acquire();
		for(String key: configurations.get(configurationIndex).keySet() )
		{
			//System.out.print(key + ": "+currentConfiguration.get(key));
			//System.out.println("   ->   "+configurations.get(configurationIndex).get(key));
			if(configurations.get(configurationIndex).get(key) instanceof Number)
			{
				double a = ((Number)currentConfiguration.get(key)).doubleValue();
				double b = ((Number)configurations.get(configurationIndex).get(key)).doubleValue();
				
				if(Math.abs(a-b) >= TOLLERANCE){
					System.out.println("########### "+configurations.get(configurationIndex).get(key)+"  !=  "+currentConfiguration.get(key));
					config.release();
					return false;
				}
			}
			else if(key.equals("formulas"))
			{
				String a = ((String)currentConfiguration.get(key)).replace(" ", "");
				String b = ((String)configurations.get(configurationIndex).get(key)).replace(" ", "");
				if(!a.equals(b))
				{
					System.out.println("########### "+a+"  !=  "+b);
					return false;
				}	
			}
			else
			{
				if(!configurations.get(configurationIndex).get(key).equals(currentConfiguration.get(key))){
					System.out.println("########### "+configurations.get(configurationIndex).get(key)+"  !=  "+currentConfiguration.get(key));
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
}
