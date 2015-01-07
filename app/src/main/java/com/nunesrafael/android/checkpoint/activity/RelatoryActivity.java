package com.nunesrafael.android.checkpoint.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.adapter.AllocationExpandableAdapter;
import com.nunesrafael.android.checkpoint.datasource.Repository;
import com.nunesrafael.android.checkpoint.font.Font;
import com.nunesrafael.android.checkpoint.model.Allocation;
import com.nunesrafael.android.checkpoint.popup.Popup;
import com.nunesrafael.android.checkpoint.util.CountingTime;
import com.nunesrafael.android.checkpoint.util.GenXls;
import com.nunesrafael.android.checkpoint.util.Util;

import org.joda.time.Interval;
import org.joda.time.Period;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RelatoryActivity extends Activity {

	public int RESOURCE_ID_HEADER = R.layout.header_listview_relatory;

    private static final SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("HH:mm");
	
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_export);
        fab.attachToListView(expandableListView);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Object[]> dataReport = new ArrayList<>();

                for (ArrayList<Allocation> allocArr : allocations) {
                    for (Allocation alloc : allocArr) {

                        boolean exitTimeIsNull = alloc.getExitTime() == null;

                        Period period = null;

                        if (!exitTimeIsNull) {
                            period = new Interval(alloc.getEntryTime().getTime(), alloc.getExitTime().getTime()).toPeriod();
                        }

                        dataReport.add(new Object[] {
                                alloc.getEntryTime(),
                                HOUR_FORMAT.format(alloc.getEntryTime()),
                                exitTimeIsNull ? "" : HOUR_FORMAT.format(alloc.getExitTime()),
                                exitTimeIsNull ? "" : period.getHours() + ":" + period.getMinutes()
                        });
                    }
                }

                GenXls.export(RelatoryActivity.this, new String[]{"Data", "Entrada", "Saída", "Total"}, dataReport);
            }
        });
        
        textViewNewRelatoryHint = (TextView)findViewById(R.id.relatoryTextViewNewRelatoryHint);
        
        // Changing the font
      	Typeface typeFace = Typeface.createFromAsset(getAssets(), Font.FONT_RESOURCE_PATH);
      	Font.applyFonts(getWindow().getDecorView().findViewById(android.R.id.content), typeFace);
    	
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
		
		if(isEntry == true) {
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
				
				if(isEntry == true) {
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
