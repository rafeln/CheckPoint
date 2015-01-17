package com.nunesrafael.android.checkpoint.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.service.AlarmIntentService;
import com.nunesrafael.android.checkpoint.sound.SoundPlayer;

public class StopAlarmActivity extends Activity {

	LinearLayout linearLayoutCancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stop_alarm);
		
		linearLayoutCancel = (LinearLayout)findViewById(R.id.stopAlarmLinearLayoutCancel);
		linearLayoutCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                stopAlarm();
                finish();
            }
        });
	}
	
	@Override
	protected void onDestroy() {
		
		stopAlarm();
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, @NonNull KeyEvent keyEvent) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
			
			stopAlarm();
		}
		
		return super.onKeyDown(keyCode, keyEvent);
	}
	
	public void stopAlarm() {
		
		AlarmIntentService.isCanceled = true;
		SoundPlayer.stopTimeToGoSound();
	}
}