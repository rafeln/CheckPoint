package com.nunesrafael.android.checkpoint.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.font.Font;

/**
 * Own TextView class with custom typeface support
 */
public class TextViewPlus extends TextView {

    public TextViewPlus(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TextViewPlus,
                0, 0);

        try {
            String fontName = a.getString(R.styleable.TextViewPlus_fontNameTextViewPlus);
            Font.changeFont(this, fontName);
        } finally {
            a.recycle();
        }
    }
}