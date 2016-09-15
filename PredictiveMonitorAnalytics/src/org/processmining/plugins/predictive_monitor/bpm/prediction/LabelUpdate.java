package org.processmining.plugins.predictive_monitor.bpm.prediction;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.processmining.plugins.predictive_monitor.bpm.utility.Print;

public class LabelUpdate {
	
	public static String computeShiftedLabel(String label, String currentDateString,String startDateString)
	{
		Print print = new Print();
		String s="";
		Date currentDate=null;
		Date startDate=null;
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		try {
			currentDate = (Date) format.parseObject(currentDateString);
			startDate = (Date) format.parseObject(startDateString);
			print.thatln("StartDate:"+startDate+" CurrentDate:"+currentDate);
			
			long current=currentDate.getTime()-startDate.getTime();

			if(label.split("-").length==2)
			{
				long start=Long.parseLong(label.split("-")[0]);
				long end=Long.parseLong(label.split("-")[1]);
				print.thatln("start:"+start+" end:"+end+" current:"+current);
				start-=current;
				end-=current;
				if(start<0)start=0;
				if(end<0)end=0;
				s=start+"-"+end;
			}
			else
			{
				long time=Long.parseLong(label);
				print.thatln("time:"+time+" current:"+current);
				time-=current;
				s=""+time;
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
}
