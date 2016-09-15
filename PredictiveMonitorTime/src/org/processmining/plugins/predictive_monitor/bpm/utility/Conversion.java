package org.processmining.plugins.predictive_monitor.bpm.utility;

public class Conversion {
	public static String longToTime(Long number)
	{
		if(number <=0 )
		{
			return number+" ";
		}
		String s="";
		boolean written=false;
		int count=0;
		
		

		long years=(long)number/((long)60*60*24*12*30*1000);
		if(years>0)
		{
			s+=years+" years ";
			written=true;
		}
		if(written)
		{
			count++;
		}
		number-=years*((long)60*60*24*12*30*1000);
		
		long months=number/((long)60*60*24*30*1000);
		if(months>0)
		{
			s+=months+" months ";
			written=true;
		}	
		if(written)
		{
			count++;
		}
		number-=months*((long)60*60*24*30*1000);
		
		long days=number/((long)60*60*24*1000);
		if(days>0)
		{
			s+=days+" days ";
			written=true;
		}
		if(written)
		{
			count++;
		}
		if(count==2)
		{
			return s;
		}
		number-=days*((long)60*60*24*1000);
		
		long hours=number/((long)60*60*1000);
		if(hours>0)
		{
			s+=hours+" hours ";
			written=true;
		}	
		if(written)
		{
			count++;
		}
		if(count==2)
		{
			return s;
		}
		number-=hours*((long)60*60*1000);
		
		long minutes=number/((long)60*1000);
		if(minutes>0) 
		{
			s+=minutes+" minutes ";
			written=true;
		}
		if(written)
		{
			count++;
		}
		if(count==2)
		{
			return s;
		}
		number-=minutes*((long)60*60*1000);
		
		long seconds=number/((long)60*1000);
		if(seconds>0) // maximum precision allowed
		{
			s+=seconds+" seconds ";
			written=true;
		}
		number-=seconds*((long)60*1000);
		
		if(!written)
		{
			if(number==0)
			{
				s+="now ";
			}
			else
			{
				s+=number+" milliseconds ";
			}
		}
		return s;
	}
}
