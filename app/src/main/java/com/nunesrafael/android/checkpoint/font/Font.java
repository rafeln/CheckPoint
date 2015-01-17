package com.nunesrafael.android.checkpoint.font;

import android.graphics.Typeface;
import android.view.View;

import com.nunesrafael.android.checkpoint.widget.ButtonPlus;
import com.nunesrafael.android.checkpoint.widget.EditTextPlus;
import com.nunesrafael.android.checkpoint.widget.TextViewPlus;

public class Font {

	public static final String FONT_ROBOTO_PATH = "fonts/Roboto-Regular.ttf";
    public static final String FONT_ROBOTO_NAME = "roboto";
		
	public static void changeFont(View view, String fontName) {
        Typeface typeFace = Typeface.createFromAsset(view.getContext().getAssets(), getFontPath(fontName));
        if (view instanceof TextViewPlus) {
            ((TextViewPlus)view).setTypeface(typeFace);
        }
        else if (view instanceof EditTextPlus) {
            ((EditTextPlus)view).setTypeface(typeFace);
        }
        else if (view instanceof ButtonPlus) {
            ((ButtonPlus)view).setTypeface(typeFace);
        }
	}

    public static String getFontPath(String fontName) {
        if(fontName == null || "".equals(fontName)) {
            return FONT_ROBOTO_PATH;
        }
        // list of fonts
        if(FONT_ROBOTO_NAME.equals(fontName)) {
            return FONT_ROBOTO_PATH;
        }
        // if is not in the list return the default
        return FONT_ROBOTO_PATH;
    }
}