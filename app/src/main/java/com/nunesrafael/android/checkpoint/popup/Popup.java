package com.nunesrafael.android.checkpoint.popup;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import com.nunesrafael.android.checkpoint.R;

public class Popup {
	
	public static int RESOURCE_ID_POPUP_ALARM   = R.layout.popup_alarm;
	public static int RESOURCE_ID_POPUP_COMMENT = R.layout.popup_comment;
	public static int RESOURCE_ID_POPUP_DATE    = R.layout.popup_date;
	public static int RESOURCE_ID_POPUP_DELETE  = R.layout.popup_delete;
	public static int RESOURCE_ID_POPUP_OPTIONS = R.layout.popup_options;
	public static int RESOURCE_ID_POPUP_TIME    = R.layout.popup_time;
	
	public static PopupWindow popupWindow;
	
	public static View setPopupWindowLayout(Context context, int layoutResourceId) {
    	
    	// Inflate the popup_layout.xml
		LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View popupWindowView = layoutInflater.inflate(layoutResourceId, null);

		// Creating the PopupWindow
		popupWindow = new PopupWindow(context);
		popupWindow.setContentView(popupWindowView);
		popupWindow.setWidth(LayoutParams.MATCH_PARENT);
		popupWindow.setHeight(LayoutParams.MATCH_PARENT);
		popupWindow.setFocusable(true);
		
		popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.color.bg_popup));
		
		popupWindow.showAtLocation(popupWindowView, Gravity.NO_GRAVITY, 0, 0);
		
		return popupWindowView;
    }

    public static void removePopup() {
    	
    	if (popupWindow != null) {
        	popupWindow.dismiss();
        	popupWindow = null;
        }
    }
}
