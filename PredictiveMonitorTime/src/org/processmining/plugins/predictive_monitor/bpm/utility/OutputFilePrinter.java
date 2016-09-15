package org.processmining.plugins.predictive_monitor.bpm.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import org.processmining.plugins.predictive_monitor.bpm.configuration.ClientConfigurationClass;

public class OutputFilePrinter {
	PrintWriter pw;
	public enum OutputFileType {TXT_FILE, CSV_FILE};
	public OutputFilePrinter(String fileName)
	{
		File output=null;
		if(ClientConfigurationClass.outputFileType==OutputFileType.TXT_FILE)
		{
			output = new File(fileName+".txt");
		}
		else if(ClientConfigurationClass.outputFileType==OutputFileType.CSV_FILE)
		{
			output = new File(fileName+".csv");
		}
		try {
			pw = new PrintWriter(output);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(ClientConfigurationClass.outputFileType==OutputFileType.CSV_FILE)
		{
			printCompatibility(); //for windows users
		}
	}
	
	public void printParameterValue(String parameter,String value)
	{
		if(ClientConfigurationClass.outputFileType==OutputFileType.TXT_FILE)
		{
			pw.println(parameter+" = "+value);
		}
		else if(ClientConfigurationClass.outputFileType==OutputFileType.CSV_FILE)
		{
			pw.println("\""+parameter+"\",\""+value+"\"");
		}
	}
	public void printTableLine(List<String> entries)
	{
		if(ClientConfigurationClass.outputFileType==OutputFileType.TXT_FILE)
		{
			for(String entry:entries)
			{
				pw.print(entry+"\t\t");
			}
			pw.println();
		}
		else if(ClientConfigurationClass.outputFileType==OutputFileType.CSV_FILE)
		{
			for(String entry:entries)
			{
				pw.print("\""+entry+"\",");
			}
			pw.println();
		}
	}
	public void printEntry(String entry)
	{
		if(ClientConfigurationClass.outputFileType==OutputFileType.TXT_FILE)
		{
			pw.println(entry);
		}
		else if(ClientConfigurationClass.outputFileType==OutputFileType.CSV_FILE)
		{
			pw.println(entry);
		}
	}
	public void flush()
	{
		pw.flush();
	}
	public void close()
	{
		pw.close();
	}
	
	void printCompatibility(){
		pw.println("sep=,");
	}
}
