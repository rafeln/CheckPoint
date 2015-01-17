package com.nunesrafael.android.checkpoint.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.popup.Popup;
import com.nunesrafael.android.checkpoint.preference.Preference;

public class ConfigurationActivity extends Activity {
	
	private TimePicker timePicker;
	private TextView textViewDefaultTotalPerDay;
	private TextView textViewAlarmTurning;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        
        textViewDefaultTotalPerDay = (TextView)findViewById(R.id.configurationTextViewDefaulTotalPerDay);
        textViewDefaultTotalPerDay.setText(getFormattedTimeOfDefaultTotalPerDay());
        
        textViewAlarmTurning = (TextView)findViewById(R.id.configurationTextViewAlarmTurning);
        setTextViewAlarmTurningText(Preference.getAlarmState(this));
	}
	
	public void backToHome(View view) {
    	
		Popup.removePopup();
		finish();
    }
	
	public void goToFindComment(View view) {
		
		Popup.removePopup();
		Intent intentFindComment = new Intent(ConfigurationActivity.this, FindCommentActivity.class);
    	startActivity(intentFindComment);
	}
	
	public void goToRelatory(View view) {
		
		Popup.removePopup();
		Intent intentRelatory = new Intent(ConfigurationActivity.this, RelatoryActivity.class);
    	startActivity(intentRelatory);
	}
	
	public void goToAbout(View view) {
		
		Popup.removePopup();
		Intent intentAbout = new Intent(ConfigurationActivity.this, AboutActivity.class);
    	startActivity(intentAbout);
	}
	
	public String getFormattedTimeOfDefaultTotalPerDay() {
		
		int hourOfDay = Preference.getHourOfDayOfDefaultTotalPerDay(this);
		int minute = Preference.getMinuteOfDefaultTotalPerDay(this);
		
		String strHourOfDay = (hourOfDay < 10) ? "0" + hourOfDay : "" + hourOfDay;
		String strMinute = (minute < 10) ? "0" + minute : "" + minute;
		
		return strHourOfDay + ":" + strMinute;
	}
	
	public void setAlarmTurningText(View view) {
		
		Preference.setAlarmState(this, !Preference.getAlarmState(ConfigurationActivity.this));
		setTextViewAlarmTurningText(Preference.getAlarmState(ConfigurationActivity.this));	
	}
	
	public void setTextViewAlarmTurningText(boolean alarmState) {
		if(alarmState)
        	textViewAlarmTurning.setText(getString(R.string.on));
        else
        	textViewAlarmTurning.setText(getString(R.string.off));
	}
	
	public void setDefaultTotalPerDay(View view) {
		
		View popupWindowView = Popup.setPopupWindowLayout(this, Popup.RESOURCE_ID_POPUP_TIME);
		
		timePicker = (TimePicker)popupWindowView.findViewById(R.id.popupTimeTimePicker);
		timePicker.setIs24HourView(true);
		
		timePicker.setCurrentHour(Preference.getHourOfDayOfDefaultTotalPerDay(view.getContext()));
		timePicker.setCurrentMinute(Preference.getMinuteOfDefaultTotalPerDay(view.getContext()));
		
		TextView textViewTitle = (TextView)popupWindowView.findViewById(R.id.popupTimeTextViewTitle);
		TextView textViewTimeText = (TextView)popupWindowView.findViewById(R.id.popupTimeTextViewTimeText);
		TextView textViewCheckPointTime = (TextView)popupWindowView.findViewById(R.id.popupTimeTextViewCheckPointTime);
		
		textViewTitle.setText(getString(R.string.default_total_per_day));
		textViewTimeText.setVisibility(View.GONE);
		textViewCheckPointTime.setVisibility(View.GONE);
		
		Button buttonOk = (Button)popupWindowView.findViewById(R.id.popupTimeButtonOk);
		buttonOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				int hourOfDay = timePicker.getCurrentHour();
				int minute = timePicker.getCurrentMinute();
				if((hourOfDay == 0) && (minute == 0)) {
					
					Toast.makeText(ConfigurationActivity.this, getString(R.string.default_total_per_day_zero), Toast.LENGTH_SHORT).show();
					
				}
				else {
					
					Preference.setHourOfDayOfDefaultTotalPerDay(ConfigurationActivity.this, hourOfDay);
					Preference.setMinuteOfDefaultTotalPerDay(ConfigurationActivity.this, minute);
					
					textViewDefaultTotalPerDay.setText(getFormattedTimeOfDefaultTotalPerDay());
					
					Popup.removePopup();
				}
			}
		});
		
		Button buttonCancel = (Button)popupWindowView.findViewById(R.id.popupTimeButtonCancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Popup.removePopup();
			}
		});
	}
}