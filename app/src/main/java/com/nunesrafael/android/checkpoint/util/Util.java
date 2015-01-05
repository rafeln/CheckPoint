package com.nunesrafael.android.checkpoint.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import com.nunesrafael.android.checkpoint.R;


public class Util {
	
	public static String getFormattedDate(Date date) {
		
		if(date == null)
			return "";
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt","BR"));
		return dateFormat.format(date);
	}
	
	public static String getSortedDate(Date date) {
		
		if(date == null)
			return "";
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("pt","BR"));
		return dateFormat.format(date);
	}
	
	public static String getHourOfDay(Date date) {
		
		if(date == null)
			return "";
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", new Locale("pt","BR"));
		return dateFormat.format(date);
	}
	
	public static String getMonth(Date date, Context context) {
		
		if(date == null)
			return "";
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM", new Locale("pt","BR"));
		int month = Integer.parseInt(dateFormat.format(date));
		
		String[] months = context.getResources().getStringArray(R.array.months_array);
		return months[month - 1];
	}
	
	public static String getDayOfWeek(Date date, Context context) {
		
		if(date == null)
			return "";
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		String[] daysOfWeek = context.getResources().getStringArray(R.array.days_of_week_array);
		return daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1];
	}
	
	public static Date getYesterday(Date date) {
		
		Calendar calendarYesterday = Calendar.getInstance();
   	 	calendarYesterday.setTime(date);
   	 	calendarYesterday.add(Calendar.DATE, -1);
   	 	
   	 	Date yesterday = new Date(calendarYesterday.getTimeInMillis());
		return yesterday;
	}
	
	public static Date getTomorrow(Date date) {
		
		Calendar calendarTomorrow = Calendar.getInstance();
		calendarTomorrow.setTime(date);
		calendarTomorrow.add(Calendar.DATE, 1);
   	 	
   	 	Date tomorrow = new Date(calendarTomorrow.getTimeInMillis());
   	 	return tomorrow;
	}
	
	public static Date stringToDate(String strDate/*2012-06-30*/, String strHour/*23:00*/) {
		
		Date date = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", new Locale("pt","BR"));
			date = dateFormat.parse(strDate + " " + strHour);
		}
		catch(Exception error) {
			Log.w("stringToDate", error.getMessage());
			return date;
		}
		return date;
	}
}