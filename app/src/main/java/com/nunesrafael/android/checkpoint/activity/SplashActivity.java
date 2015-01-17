package com.nunesrafael.android.checkpoint.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import com.nunesrafael.android.checkpoint.R;

public class SplashActivity extends Activity {
	
	private boolean isBackButtonPressed = false;
	private int splashTime = 1000;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
        	
            @Override
            public void run() {
            	
                finish();
                
                if(!isBackButtonPressed) {
                	
                	Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                	startActivity(intent);
                }
            }
        }, splashTime);
    }
	
	@Override
	public boolean onKeyDown(int keyCode,@NonNull KeyEvent keyEvent) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK)
			isBackButtonPressed = true;
		
		return super.onKeyDown(keyCode, keyEvent);
	}
}