package com.nunesrafael.android.checkpoint.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.font.Font;

/**
 * Own EditText class with custom typeface support
 */
public class EditTextPlus extends EditText {

    public EditTextPlus(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.EditTextPlus,
                0, 0);

        try {
            String fontName = a.getString(R.styleable.EditTextPlus_fontNameEditTextPlus);
            Font.changeFont(this, fontName);
        } finally {
            a.recycle();
        }
    }
}