package com.nunesrafael.android.checkpoint.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.font.Font;

/**
 * Own ImageButton class with custom typeface support
 */
public class ImageButtonPlus extends ImageButton {

    public ImageButtonPlus(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ImageButtonPlus,
                0, 0);

        try {
            String fontName = a.getString(R.styleable.ImageButtonPlus_fontNameImageButtonPlus);
            Font.changeFont(this, fontName);
        } finally {
            a.recycle();
        }
    }
}