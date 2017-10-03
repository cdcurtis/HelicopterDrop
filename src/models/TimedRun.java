package models;

import java.text.DecimalFormat;

public class TimedRun implements Comparable<TimedRun>{
	public Long milliseconds;
	public Integer runID;
	
	public TimedRun(Integer id)
	{
		this.milliseconds = (long) 0 ;
		this.runID = id;
	}
	
	public TimedRun(Integer id, Long milli)
	{
		this.milliseconds = milli;
		this.runID = id;
	}
	
	public String toDisplay()
	{
		Long seconds = (milliseconds/1000);
		return seconds.toString() + ":" + String.format("%03d", milliseconds%1000) ;
	}
	public String toFile()
	{
		return runID.toString() + "," + milliseconds.toString() + "\n";
	}
	
	public String toString()
	{
		return milliseconds.toString();
	}

	public int compareTo(TimedRun o) {
		// TODO Auto-generated method stub
		return this.milliseconds.compareTo(o.milliseconds);
	}



}
