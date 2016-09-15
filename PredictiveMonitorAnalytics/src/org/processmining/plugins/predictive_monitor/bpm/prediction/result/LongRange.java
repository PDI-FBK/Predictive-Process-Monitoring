package org.processmining.plugins.predictive_monitor.bpm.prediction.result;

import java.util.Date;

import bsh.org.objectweb.asm.Label;

public class LongRange extends ResultType{
	private long start;
	private long end;
	private final static long year = 1000l*60*60*24*30*12;
	private final static long month = 1000l*60*60*24*30;
	private final static long day = 1000l*60*60*24;
	private final static long hour = 1000l*60*60;
	private final static long minute = 1000l*60;
	private final static long second = 1000l;

	
	public LongRange(long start, long end)
	{
		this.start=start;
		this.end=end;
		label = toString();
	}
	
	public boolean contains(Long number)
	{
		return number>=start && number<=end;
	}
	
	public void translate(Long offset)
	{
		start-=offset;
		end-=offset;
		label = toString();
	}
	
	@Override
	public String toString()
	{
		return "from "+longToTime(start)+" to "+longToTime(end);
	}
	
	public static String longToTime(long value)
	{
		String s="";
		int printed = 0;
		
		
		
		if(value >= year)
		{
			int years = (int)Math.floor((double)value/year);
			s += years+"y ";
			value = value%year;
			printed++;
		}
		
		if(value >= month)
		{
			int months = (int)Math.floor((double)value/month);
			s += months +"M ";
			value = value%month;
			printed++;
		}
		
		if(value >= day)
		{
			int days = (int)Math.floor((double)value/day);
			s += days +"d ";
			value = value%day;
			printed++;
		}
		
		if(value >= hour && printed < 3)
		{
			int hours = (int)Math.floor((double)value/hour);
			s += hours +"d ";
			value = value%hour;
			printed++;
		}
		
		if(value >= minute && printed < 3)
		{
			int minutes = (int)Math.floor((double)value/minute);
			s += minutes +"d ";
			value = value%minute;
			printed++;
		}
		
		if(value >= second && printed < 3)
		{
			int seconds = (int)Math.floor((double)value/second);
			s += seconds +"d ";
			value = value%second;
			printed++;
		}
		
		if(value >0 && printed < 3)
		{
			s += value+"ms ";
			printed++;
		}
		
		if(printed == 0 && value == 0)
		{
			s += "0 ";
		}
		
		else if (printed == 0)
		{
			s+= "past ";
		}
		
		return s;
	}
	
}
