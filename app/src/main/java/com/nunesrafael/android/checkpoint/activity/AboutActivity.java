package com.nunesrafael.android.checkpoint.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.font.Font;

public class AboutActivity extends Activity {
	
	private final String developerWebPage = "http://www.nunesrafael.com";
	private final String designerWebPage = "http://www.criativassa.com.br";
	private final String googlePlayAppWebPage = "http://play.google.com/store/apps/details?id=com.nunesrafael.android.checkpoint"; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		// Changing the font
		Typeface typeFace = Typeface.createFromAsset(getAssets(), Font.FONT_RESOURCE_PATH);
		Font.applyFonts(getWindow().getDecorView().findViewById(android.R.id.content), typeFace);
	}
	
	public void back(View view) {
    	
		finish();
	}
	
	public void goToDeveloperWebPage(View view) {
		
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(developerWebPage));
		startActivity(browserIntent);
	}
	
	public void goToDesignerWebPage(View view) {
		
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(designerWebPage));
		startActivity(browserIntent);
	}
	
	public void goToGooglePlayAppWebPage(View view) {
		
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayAppWebPage));
		startActivity(browserIntent);
	}
}