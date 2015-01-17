package com.nunesrafael.android.checkpoint.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.adapter.AllocationExpandableAdapter;
import com.nunesrafael.android.checkpoint.datasource.Repository;
import com.nunesrafael.android.checkpoint.model.Allocation;
import com.nunesrafael.android.checkpoint.popup.Popup;
import com.nunesrafael.android.checkpoint.util.CountingTime;
import com.nunesrafael.android.checkpoint.util.Util;

public class RelatoryActivity extends Activity {

	public int RESOURCE_ID_HEADER = R.layout.header_listview_relatory;
	
	private ExpandableListView expandableListView;
	private AllocationExpandableAdapter allocationExpandableAdapter;
	private ArrayList<ArrayList<Allocation>> allocations;
	
	// Popup
	private DatePicker datePicker;
	private Calendar calendarInit = Calendar.getInstance();
	private Calendar calendarEnd = Calendar.getInstance();
	private boolean isEntry;
	
	private TextView textViewNewRelatoryHint;
	private View hearder;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatory);
        
        expandableListView = (ExpandableListView)findViewById(R.id.relatoryExpandableListView);
        addHeader();
        
        textViewNewRelatoryHint = (TextView)findViewById(R.id.relatoryTextViewNewRelatoryHint);
    	
    	expandableListView.post(new Runnable() {
    		public void run() {
    		
    			showPopups(expandableListView);
    		}
    	});
	}
	
	public void addHeader() {
		
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
        hearder = inflater.inflate(RESOURCE_ID_HEADER, null, false);
        expandableListView.addHeaderView(hearder, null, false);
	}
	
	public void showPopups(View view) {
		
		isEntry = true;
		Popup.removePopup();
		showDatePopup(view);
	}
	
	public void showDatePopup(View view) {
		
		View popupWindowView = Popup.setPopupWindowLayout(view.getContext(), Popup.RESOURCE_ID_POPUP_DATE);
		
		TextView textViewTitle = (TextView)popupWindowView.findViewById(R.id.popupDateTextViewTitle);
		datePicker = (DatePicker)popupWindowView.findViewById(R.id.popupDateDatePicker);
		
		if(isEntry) {
			textViewTitle.setText(getString(R.string.relatory_init));
			datePicker.init(calendarInit.get(Calendar.YEAR), calendarInit.get(Calendar.MONTH), calendarInit.get(Calendar.DATE), null);
		}
		else {
			textViewTitle.setText(getString(R.string.relatory_end));
			datePicker.init(calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DATE), null);
		}
		
		Button buttonOk = (Button)popupWindowView.findViewById(R.id.popupDateButtonOk);
		buttonOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				int year = datePicker.getYear();
				int month = datePicker.getMonth();
				int dayOfMonth = datePicker.getDayOfMonth();
				
				if(isEntry) {
					calendarInit.set(year, month, dayOfMonth);
					isEntry = false;
					Popup.removePopup();
					showDatePopup(expandableListView);
				}
				else {
					calendarEnd.set(year, month, dayOfMonth);
					Popup.removePopup();
					getRelatory();
				}
			}
		});
		
		Button buttonCancel = (Button)popupWindowView.findViewById(R.id.popupDateButtonCancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Popup.removePopup();
			}
		});
	}
	
	public void getRelatory() {
		
		Date dateInit = new Date();
		Date dateEnd = new Date();
		
		dateInit.setTime(calendarInit.getTimeInMillis());
		dateEnd.setTime(calendarEnd.getTimeInMillis());
		
		String strDateInit = Util.getFormattedDate(dateInit);
		String strDateEnd = Util.getFormattedDate(dateEnd);
		
		if(strDateInit.compareTo(strDateEnd) > 0) {
			Toast.makeText(RelatoryActivity.this, getString(R.string.date_error), Toast.LENGTH_SHORT).show();
			return;
		}
		
		TextView textViewInitDate = (TextView)findViewById(R.id.listViewHeaderTextViewInitDate);
		TextView textViewEndDate = (TextView)findViewById(R.id.listViewHeaderTextViewEndDate);
		
		textViewInitDate.setText(strDateInit);
		textViewEndDate.setText(strDateEnd);
		
        allocations = new Repository(RelatoryActivity.this).getRelatory(dateInit, dateEnd);
        allocationExpandableAdapter = new AllocationExpandableAdapter(RelatoryActivity.this, allocations, R.layout.row_relatory_group, R.layout.row_relatory_child);
        expandableListView.setAdapter(allocationExpandableAdapter);
        
        TextView textViewTotal = (TextView)findViewById(R.id.listViewHeaderTextViewTotal);
        textViewTotal.setText(CountingTime.getTotalFromPeriodFormatted(allocations, RelatoryActivity.this));
        
        // open all
        for(int groupPosition = 0; groupPosition < allocations.size(); groupPosition++)
        	expandableListView.expandGroup(groupPosition);
        
        
    	if(allocations.size() == 0) {
    		hearder.setVisibility(View.INVISIBLE);
    		textViewNewRelatoryHint.setText(getString(R.string.relatory_empty) + "\n" + getString(R.string.new_relatory_hint));
    	}
    	else {
    		hearder.setVisibility(View.VISIBLE);
    		textViewNewRelatoryHint.setText("");
    	}
	}
	
	public void back(View view) {
    	
		Popup.removePopup();
		finish();
    }
}
