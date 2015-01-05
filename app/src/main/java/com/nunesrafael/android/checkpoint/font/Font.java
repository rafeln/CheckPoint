package com.nunesrafael.android.checkpoint.font;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class Font {
	
	public static final String FONT_RESOURCE_PATH = "fonts/Roboto-Regular.ttf";
		
	public static void changeFont(TextView textView) {
		Typeface typeface = Typeface.createFromAsset(textView.getContext().getAssets(), FONT_RESOURCE_PATH);
		textView.setTypeface(typeface);
	}
	
	public static void changeFont(EditText editText) {
		Typeface typeface = Typeface.createFromAsset(editText.getContext().getAssets(), FONT_RESOURCE_PATH);
		editText.setTypeface(typeface);
	}
	
	public static void applyFonts(final View view, Typeface typeFace)
	{
	    try {
	        if (view instanceof ViewGroup) {
	            ViewGroup viewGroup = (ViewGroup) view;
	            for (int i = 0; i < viewGroup.getChildCount(); i++) {
	                View child = viewGroup.getChildAt(i);
	                applyFonts(child, typeFace);
	            }
	        } else if (view instanceof TextView) {
	            ((TextView)view).setTypeface(typeFace);
	        } else if (view instanceof EditText) {
	            ((EditText)view).setTypeface(typeFace);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}