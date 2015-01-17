package com.nunesrafael.android.checkpoint.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.font.Font;

/**
 * Own Button class with custom typeface support
 */
public class ButtonPlus extends Button {

    public ButtonPlus(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ButtonPlus,
                0, 0);

        try {
            String fontName = a.getString(R.styleable.ButtonPlus_fontNameButtonPlus);
            Font.changeFont(this, fontName);
        } finally {
            a.recycle();
        }
    }
}
