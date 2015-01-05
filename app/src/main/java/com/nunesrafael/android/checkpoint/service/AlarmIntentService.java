package com.nunesrafael.android.checkpoint.service;

import java.util.Date;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.nunesrafael.android.checkpoint.activity.MainActivity.AlarmBroadcastReceiver;
import com.nunesrafael.android.checkpoint.activity.StopAlarmActivity;
import com.nunesrafael.android.checkpoint.notification.AlarmNotification;
import com.nunesrafael.android.checkpoint.preference.Preference;
import com.nunesrafael.android.checkpoint.sound.SoundPlayer;
import com.nunesrafael.android.checkpoint.util.CountingTime;
import com.nunesrafael.android.checkpoint.util.Util;

public class AlarmIntentService extends IntentService {
	
	public static final String SERVICE_NAME = "com.nunesrafael.android.checkpoint.timeToGoService";
	public static final String APP_NAME = "com.nunesrafael.android.checkpoint";
	
	public static final String TOTAL = "total";
	public static final String BALANCE = "balance";
	public static final String TIME_TO_GO = "timeToGo";
	
	private String instantTotal = "";
	private String instantBalance = "";
	private String todayTimeToGo = "";
	private String yesterdayTimeToGo = "";
	private String strNow;
	
	private Date now;
	private Date today;
	private Date yesterday;
	
	private int sleep             = 5000;  // 05 seconds
	public static boolean isCanceled;
	
	
	public AlarmIntentService() {
		super(SERVICE_NAME);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		today = new Date();
    	yesterday = Util.getYesterday(today);
		
	    while (isThisServiceNecessary()) {
	        synchronized (this) {
	            try {
	                
	            	SystemClock.sleep(sleep);
	            	today = new Date();
	            	yesterday = Util.getYesterday(today);
	            	
	            	while(CountingTime.isToday(today)) {
	            		
	            		now = new Date();
            			strNow = Util.getHourOfDay(now);
	            		
		            	// Calculate
		            	instantTotal = CountingTime.getInstantTotalFromDateFormatted(now, getApplicationContext());
		            	instantBalance = CountingTime.getInstantBalanceFormatted(now, getApplicationContext());
		            	todayTimeToGo = CountingTime.getExitTimeFormatted(now, getApplicationContext());
		            	yesterdayTimeToGo = CountingTime.getExitTimeFormatted(yesterday, getApplicationContext());
		            	
		            	Intent broadcastIntent = new Intent();
		                broadcastIntent.setAction(AlarmBroadcastReceiver.ACTION_RESP);
		                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		                broadcastIntent.putExtra(TOTAL, instantTotal);
		                broadcastIntent.putExtra(BALANCE, instantBalance);
		                broadcastIntent.putExtra(TIME_TO_GO, todayTimeToGo);
		                sendBroadcast(broadcastIntent);
		            	
	            		if(Preference.getAlarmState(getApplicationContext()) == true) {
	            			
            				if(yesterdayTimeToGo.equalsIgnoreCase(strNow)) {
	            				alarmAction(yesterday);
	            			}
	            			
	            			if(todayTimeToGo.equalsIgnoreCase(strNow)) {
	            				alarmAction(today);
	            			}
	            		}
	            		
		                SystemClock.sleep(sleep);
	            	}
	            	
	            	if(CountingTime.isTheLastExitOpenned(yesterday, getApplicationContext())) {
	            		AlarmNotification.sendReminderExitTimeNotification(yesterday, getApplicationContext());
	            	}
	                
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }
	        }
	    }
	}
	
	public boolean isThisServiceNecessary() {
		
		if(isMyAppRunning(getApplicationContext()) == true) {
			return true;
		}
		
		if(todayTimeToGo.equalsIgnoreCase("") && yesterdayTimeToGo.equalsIgnoreCase("")) {
			Log.d("isThisServiceNecessary", "Leaving the service: todayTimeToGo = \"\" and yesterdayTimeToGo =  \"\" ");
			return false;
		}
		return true;
	}

	public boolean isMyAppRunning(Context context) {
		
		ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> runningAppProcessInfo = activityManager.getRunningAppProcesses();
	    for(int i = 0; i < runningAppProcessInfo.size(); i++){
	        if(runningAppProcessInfo.get(i).processName.equals(APP_NAME)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void alarmAction(Date date) {
		
		isCanceled = false;
		
		playTimeToGoSound();
		showStopAlarmActivity();
		
		long endTime = System.currentTimeMillis() + SoundPlayer.soundTime;
		while( (isCanceled == false) && (System.currentTimeMillis() < endTime) ); // {} playing sound
			            		
		stopTimeToGoSound();
		
		if(isCanceled == false) {
			
			AlarmNotification.sendTimeToGoNotification(date, getApplicationContext());
		}
		
		long minutePart = 60000 - (System.currentTimeMillis() % 60000);
		SystemClock.sleep(minutePart);
	}
	
	public void showStopAlarmActivity() {
		
		Intent intent = new Intent(this, StopAlarmActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(intent);
	}
	
	public void playTimeToGoSound() {
		
		SoundPlayer.playTimeToGoSound(getApplicationContext());
	}
	
	public void stopTimeToGoSound() {
		
		SoundPlayer.stopTimeToGoSound();
	}
	
	public void sendReminderExitTimeNotification(Date date) {
		
		AlarmNotification.sendReminderExitTimeNotification(date, getApplicationContext());
	}
	
	public static boolean isMyServiceRunning(Context context) {
		
	    ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
	        if (SERVICE_NAME.equals(runningServiceInfo.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}