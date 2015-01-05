package com.nunesrafael.android.checkpoint.sound;

import android.content.Context;
import android.media.MediaPlayer;
import com.nunesrafael.android.checkpoint.R;

public class SoundPlayer {
	
	
	public static int soundTime  = 22000; // 22 seconds ( 4x timeToGo + 2 silence )
	public static MediaPlayer mediaPlayer;
	
	public static void playTimeToGoSound(Context context) {
		
		mediaPlayer = MediaPlayer.create(context, R.raw.sound_time_to_go);
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
	}
	
	public static void stopTimeToGoSound() {
		
		if(mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;		
		}
	}
}