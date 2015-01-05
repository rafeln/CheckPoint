package com.nunesrafael.android.checkpoint.util;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.util.Log;
import com.nunesrafael.android.checkpoint.datasource.Repository;
import com.nunesrafael.android.checkpoint.model.Allocation;
import com.nunesrafael.android.checkpoint.preference.Preference;

public class CountingTime {
	
	public static long getTotalFromDate(Date date, Context context) {
		
		if(date == null) {
			Log.w("getTotalFromDate", "date is NULL");
			return 0;
		}
		
		long totalFromDate = 0;
		
		ArrayList<Allocation> allocations = new Repository(context).getAllocationsFromDate(date);
		for (Allocation allocation : allocations) {
			if(allocation.getExitTime() != null) {
				totalFromDate = totalFromDate + (allocation.getExitTime().getTime() - allocation.getEntryTime().getTime());
			}
		}
		return totalFromDate;
	}
	
	public static String getTotalFromDateFormatted(Date date, Context context) {
		
		if(date == null) {
			Log.w("getTotalFromDateFormatted", "date is NULL");
			return "";
		}
		
		long total = getTotalFromDate(date, context);
		
		String strTotal = milisecondsToFormattedTime(total);
		return strTotal;
	}
	
	public static long getInstantTotalFromDate(Date date, Context context) {
		
		if(date == null) {
			Log.w("getInstantTotalFromDate", "date is NULL");
			return 0;
		}
		
		if(isToday(date) == false) {
			return getTotalFromDate(date, context);
		}
		
		long instantTotalFromDate = 0;
		
		ArrayList<Allocation> allocations = new Repository(context).getAllocationsFromDate(date);
		for (int i = 0; i < allocations.size(); i++) {
			Allocation allocation = allocations.get(i);
			if(allocation.getExitTime() != null) {
				instantTotalFromDate = instantTotalFromDate + (allocation.getExitTime().getTime() - allocation.getEntryTime().getTime());
			}
			else if(i == allocations.size() - 1) {
				instantTotalFromDate = instantTotalFromDate + (new Date().getTime() - allocation.getEntryTime().getTime());
			}
		}
		return instantTotalFromDate;
	}
	
	public static String getInstantTotalFromDateFormatted(Date date, Context context) {
		
		if(date == null) {
			Log.w("getInstantTotalFromDateFormatted", "date is NULL");
			return "";
		}
		
		long instantTotal = getInstantTotalFromDate(date, context);
		
		if(instantTotal <= 0)
			instantTotal = 0;
		
		String strInstantTotal = milisecondsToFormattedTime(instantTotal);
		return strInstantTotal;
	}
	
	public static long getInstantBalance(Date date, Context context) {
		
		if(date == null) {
			Log.w("getInstantBalance", "date is NULL");
			return 0;
		}
		
		long instantTotal = getInstantTotalFromDate(date, context);
		long defaultTotalPerDay = getDefaultTotalPerDay(context);
		
		long instantBalance = instantTotal - defaultTotalPerDay;
		return instantBalance;
	}
	
	public static String getInstantBalanceFormatted(Date date, Context context) {
		
		if(date == null) {
			Log.w("getInstantBalanceFormatted", "date is NULL");
			return "";
		}
		
		long instantBalance = getInstantBalance(date, context);
			
		String strInstantBalance = milisecondsToFormattedTime(instantBalance);
		return strInstantBalance;
	}
	
	public static long getExitTimeDateInMiliSeconds(Date date, Context context) {
		
		if(date == null) {
			Log.w("getExitTimeDateInMiliSeconds", "date is NULL");
			return 0;
		}
		
		if(isTheLastExitOpenned(date, context) == false) {
			return 0;
		}
		
		ArrayList<Allocation> allocations = new Repository(context).getAllocationsFromDate(date);
		if(allocations.size() == 0) {
			return 0;
		}
		
		long defaultTotalPerDay = getDefaultTotalPerDay(context);
		long instantTotalFromDate = 0;
		long instantLack = 0;
		long exitTime = 0;
		Date lastExitDate = new Date();
		
		for(int i = 0; i < allocations.size(); i++) {
			Allocation allocation = allocations.get(i);
			if(allocation.getExitTime() != null) {
				lastExitDate = allocation.getExitTime();				
			}
			else {
				lastExitDate = new Date();
			}
			instantTotalFromDate = instantTotalFromDate + (lastExitDate.getTime() - allocation.getEntryTime().getTime());
			instantLack = defaultTotalPerDay - instantTotalFromDate;
			if(instantLack <= 0) {
				exitTime = lastExitDate.getTime() + instantLack;
				return exitTime;
			}
		}
		
		exitTime = lastExitDate.getTime() + instantLack;
		return exitTime;
	}
	
	public static String getExitTimeFormatted(Date date, Context context) {
		
		if(date == null) {
			Log.w("getExitTimeFormatted", "date is NULL");
			return "";
		}
		
		long exitTime = getExitTimeDateInMiliSeconds(date, context);
		if(exitTime == 0) {
			return "";
		}
		
		Date exitTimeDate = new Date(exitTime);
		
		String strExitTime = Util.getHourOfDay(exitTimeDate);
		return strExitTime;
	}
	
	public static long getDifference(Date exitTime, Date entryTime) {
		
		if(exitTime == null) {
			Log.w("getDifference", "exitTime is NULL");
			return 0;
		}
		
		if(entryTime == null) {
			Log.w("getDifference", "entryTime is NULL");
			return 0;
		}
		
		long difference = exitTime.getTime() - entryTime.getTime();
		return difference;
	}
	
	public static String getDifferenceFormatted(Date exitTime, Date entryTime) {
		
		if(exitTime == null) {
			Log.w("getDifference", "exitTime is NULL");
			return "";
		}
		
		if(entryTime == null) {
			Log.w("getDifference", "entryTime is NULL");
			return "";
		}
		
		long lDifference = getDifference(exitTime, entryTime);
		
		String difference = milisecondsToFormattedTime(lDifference);			
		return difference;
	}
	
	public static String milisecondsToFormattedTime(long miliSeconds) {
		
		String strTotal = "";
		if(miliSeconds < 0) {
			strTotal = "-";
			miliSeconds = miliSeconds * (-1);
		}		
		
		long hour = miliSeconds / (60 * 60 * 1000);
		String strHour = (hour < 10) ? "0" + hour : "" + hour;
		
		
		long minutePart = miliSeconds % (60 * 60 * 1000);
		long minute = minutePart / (60 * 1000);
		String strMinute = (minute < 10) ? "0" + minute : "" + minute;
		
		strTotal = strTotal + strHour + ":" + strMinute;
		if(strTotal.equalsIgnoreCase("-00:00"))
				strTotal = "00:00";
		
		return strTotal;
	}

	public static long getDefaultTotalPerDay(Context context) {
		
		int hourOfDay = Preference.getHourOfDayOfDefaultTotalPerDay(context);
		int minute = Preference.getMinuteOfDefaultTotalPerDay(context);
		
		long defaultTotalPerDay = ((hourOfDay * 3600) + (minute * 60)) * 1000;
		return defaultTotalPerDay;
	}
	
	public static long getTotalFromPeriod(ArrayList<ArrayList<Allocation>> allocations, Context context) {
		
		if(allocations == null) {
			
			Log.w("getTotalFromPeriod", "allocations is NULL");
			return 0;
		}
		
		long total = 0;
		
		for(int groupPosition = 0; groupPosition < allocations.size(); groupPosition++) {
			
			for(int childPosition = 0; childPosition < allocations.get(groupPosition).size(); childPosition++) {
				
				Allocation allocation = allocations.get(groupPosition).get(childPosition);
				
				if(allocation.getExitTime() != null) {
					total = total + (allocation.getExitTime().getTime() - allocation.getEntryTime().getTime());
				}
			}
		}
		return total;
	}
	
	public static String getTotalFromPeriodFormatted(ArrayList<ArrayList<Allocation>> allocations, Context context) {
		
		long total = getTotalFromPeriod(allocations, context);
		String strTotal = milisecondsToFormattedTime(total);
		return strTotal;
	}
	
	public static boolean isToday(Date date) {
		
		if(date == null) {
			Log.w("isToday", "date is NULL");
			return false;
		}
		
		Date today = new Date();
		String strToday = Util.getFormattedDate(today);
		String strDate = Util.getFormattedDate(date);
		
		if(strDate.compareTo(strToday) == 0)
			return true;
		return false;
	}

	public static boolean isTheLastExitOpenned(Date date, Context context) {
		
		 ArrayList<Allocation> allocations = new Repository(context).getAllocationsFromDate(date);
		 if(allocations.size() == 0)
			 return false;
		 if(allocations.get(allocations.size() - 1).getExitTime() == null)
			 return true;
		 return false;
	}
}