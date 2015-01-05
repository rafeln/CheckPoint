package com.nunesrafael.android.checkpoint.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {
	
	public static final String PREFERENCES_FILE_NAME = "CheckPointPreferencesFile";
	public static final String ALARM_STATE = "alarmState";
	public static final String DEFAULT_TOTAL_PER_DAY_HOUR_OF_DAY = "defaultTotalPerDayHourOfDay";
	public static final String DEFAULT_TOTAL_PER_DAY_MINUTE = "defaultTotalPerDayMinute";
	public static final int DEFAULT_TOTAL_PER_DAY_HOUR_OF_DAY_VALUE = 8;
	public static final int DEFAULT_TOTAL_PER_DAY_MINUTE_VALUE = 0;
	
	public static boolean getAlarmState(Context context) {
		
	    SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, 0);
	    return settings.getBoolean(ALARM_STATE, false);
    }
	
	public static void setAlarmState(Context context, boolean state) {
		
	    SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    
	    editor.putBoolean(ALARM_STATE, state);
	    
	    editor.commit();
    }
	
	public static int getHourOfDayOfDefaultTotalPerDay(Context context) {
		
	    SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, 0);    
	    return settings.getInt(DEFAULT_TOTAL_PER_DAY_HOUR_OF_DAY, DEFAULT_TOTAL_PER_DAY_HOUR_OF_DAY_VALUE);
    }
	
	public static void setHourOfDayOfDefaultTotalPerDay(Context context, int defaultTotalPerDayHourOfDayValue) {
		
	    SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    
	    editor.putInt(DEFAULT_TOTAL_PER_DAY_HOUR_OF_DAY, defaultTotalPerDayHourOfDayValue);
	    
	    editor.commit();
    }
	
	public static int getMinuteOfDefaultTotalPerDay(Context context) {
		
	    SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, 0); 
	    return settings.getInt(DEFAULT_TOTAL_PER_DAY_MINUTE, DEFAULT_TOTAL_PER_DAY_MINUTE_VALUE);
    }
	
	public static void setMinuteOfDefaultTotalPerDay(Context context, int defaultTotalPerDayMinuteValue) {
		
	    SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    
	    editor.putInt(DEFAULT_TOTAL_PER_DAY_MINUTE, defaultTotalPerDayMinuteValue);
	    
	    editor.commit();
    }
}