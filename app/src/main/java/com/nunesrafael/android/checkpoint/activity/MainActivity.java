package com.nunesrafael.android.checkpoint.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.adapter.AllocationAdapter;
import com.nunesrafael.android.checkpoint.datasource.Repository;
import com.nunesrafael.android.checkpoint.model.Allocation;
import com.nunesrafael.android.checkpoint.popup.Popup;
import com.nunesrafael.android.checkpoint.preference.Preference;
import com.nunesrafael.android.checkpoint.service.AlarmIntentService;
import com.nunesrafael.android.checkpoint.util.CountingTime;
import com.nunesrafael.android.checkpoint.util.Util;

public class MainActivity extends Activity {
	
	public static final String SHOWING_DATE_LONG_PARAMETER = "showingDateLong";
	
	public int RESOURCE_ID_FOOTER = R.layout.footer_listview_main;
	public int RESOURCE_ID_ROW_ALLOCATION = R.layout.row_allocation;
		
	private Date showingDate = new Date();
	private Calendar calendar = Calendar.getInstance();
	private DatePicker datePicker;
	private TextView textViewShowingDate;
	
	private ListView listView;
	private AllocationAdapter allocationAdapter;
	private View footer;
	
	//Alarm
	private ImageView imageViewAlarm;
	private AlarmBroadcastReceiver alarmBroadcastReceiver;
	
	private TextView textViewTotal;
	//Footer
	private TextView textViewBalanceTime;
	private LinearLayout linearLayoutAlarm;
	private TextView textViewTimeToGo;
	
	private String instantTotal = "";
	private String instantBalance = "";
	private String timeToGo = "";
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		if(Preference.getAlarmState(this))
			imageViewAlarm.setImageResource(R.drawable.ic_alarm_on);
		else
			imageViewAlarm.setImageResource(R.drawable.ic_alarm_off);
		
		PopulateScreen();
	}
	
	@Override
    public void onDestroy() {
		
		if(alarmBroadcastReceiver != null)
			unregisterReceiver(alarmBroadcastReceiver);
        super.onDestroy();
    }
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        Intent intent = getIntent();
        if(intent != null) {
        	long miliseconds = intent.getLongExtra(SHOWING_DATE_LONG_PARAMETER, 0);
        	if(miliseconds != 0) {
        		showingDate.setTime(miliseconds);
        	}
        }
        
        textViewShowingDate = (TextView)findViewById(R.id.mainTextViewShowingDate);
        listView = (ListView)findViewById(R.id.mainListView);
        textViewTotal = (TextView)findViewById(R.id.mainTextViewTotal);
        
        addFooter();

        if(!AlarmIntentService.isMyServiceRunning(this)) {
	        startAlarmService();
        }
        startBroadcastReceiver();
        
        PopulateScreen();
    }

    public void addFooter() {
    	
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = inflater.inflate(RESOURCE_ID_FOOTER, null, false);
        listView.addFooterView(footer, null, false);
        
        textViewBalanceTime = (TextView)findViewById(R.id.listViewFooterTextViewBalanceTime);
        linearLayoutAlarm = (LinearLayout)findViewById(R.id.listViewFooterLinearLayoutAlarm);
        imageViewAlarm = (ImageView)findViewById(R.id.listViewFooterImageViewAlarm);
        textViewTimeToGo = (TextView)findViewById(R.id.listViewFooterTextViewTimeToGo);
    }
    
    public void startAlarmService() {
    	
    	Intent intentService = new Intent(MainActivity.this, AlarmIntentService.class);
        startService(intentService);
    }
    
    public void startBroadcastReceiver() {
    	
        IntentFilter intentFilter = new IntentFilter(AlarmBroadcastReceiver.ACTION_RESP);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        alarmBroadcastReceiver = new AlarmBroadcastReceiver();
        registerReceiver(alarmBroadcastReceiver, intentFilter);
    }
    
    public class AlarmBroadcastReceiver extends BroadcastReceiver {
    	
        public static final String ACTION_RESP = "com.nunesrafael.android.checkpoint.broadcastreceiver.alarm";
        
        @Override
        public void onReceive(Context context, Intent intent) {
        	
        	// if not today, do not calculate the lack, total and time to go
        	if(!CountingTime.isToday(showingDate))
        		return;
        	
        	instantTotal = intent.getStringExtra(AlarmIntentService.TOTAL);
        	instantBalance = intent.getStringExtra(AlarmIntentService.BALANCE);
        	timeToGo = intent.getStringExtra(AlarmIntentService.TIME_TO_GO);
        	
        	textViewTotal.setText(instantTotal);
        	textViewBalanceTime.setText(instantBalance);
        	textViewTimeToGo.setText(timeToGo);
        }
    }
    
    public void PopulateScreen() {
    	
    	setHeaderTextViews();
    	setFooterInformations();
    	
    	ArrayList<Allocation> allocations = new Repository(this).getAllocationsFromDate(showingDate);
    	if(allocations.size() == 0 || allocations.get(allocations.size() - 1).getExitTime() != null)
        	allocations.add(new Allocation(-1));
    	
    	// Adapter
        allocationAdapter = new AllocationAdapter(this, allocations, RESOURCE_ID_ROW_ALLOCATION, calendar);
        listView.setAdapter(allocationAdapter);        
    }
    
    public void setHeaderTextViews() {
    	
    	String dayOfWeek = Util.getDayOfWeek(showingDate, this);
    	String formattedDate = Util.getFormattedDate(showingDate);
    	textViewShowingDate.setText(dayOfWeek + ", " + formattedDate);
    	
    	instantTotal = CountingTime.getInstantTotalFromDateFormatted(showingDate, this);
    	textViewTotal.setText(instantTotal);
    }
    
    public void setFooterInformations() {
    	
    	instantBalance = CountingTime.getInstantBalanceFormatted(showingDate, this);
    	textViewBalanceTime.setText(instantBalance);
    	
    	if(instantBalance.charAt(0) == '-') {
    		textViewBalanceTime.setTextColor(getResources().getColor(R.color.text_red));
    	}
    	else
    		textViewBalanceTime.setTextColor(getResources().getColor(R.color.text_green));
    	
    	timeToGo = CountingTime.getExitTimeFormatted(showingDate, this);
    	textViewTimeToGo.setText(timeToGo);
    	
		if(CountingTime.isToday(showingDate) && CountingTime.isTheLastExitOpenned(showingDate, this))
			linearLayoutAlarm.setVisibility(View.VISIBLE);
		else
			linearLayoutAlarm.setVisibility(View.GONE);
    }
    
    public void goToYesterday(View view) {
    	
    	showingDate = Util.getYesterday(showingDate);
    	calendar.setTime(showingDate);
    	PopulateScreen();
    }
    
    public void goToTomorrow(View view) {
    	
    	showingDate = Util.getTomorrow(showingDate);
    	calendar.setTime(showingDate);
    	PopulateScreen();
    }
    
    public void showDatePopup(View view) {
    	
    	View popupWindowView = Popup.setPopupWindowLayout(view.getContext(), Popup.RESOURCE_ID_POPUP_DATE);
    	
		datePicker = (DatePicker)popupWindowView.findViewById(R.id.popupDateDatePicker);
		datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), null);
				
		Button buttonOk = (Button)popupWindowView.findViewById(R.id.popupDateButtonOk);
		buttonOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				int year = datePicker.getYear();
				int month = datePicker.getMonth();
				int dayOfMonth = datePicker.getDayOfMonth();
				
				calendar.set(year, month, dayOfMonth);
				showingDate.setTime(calendar.getTimeInMillis());
				
				Popup.removePopup();
				
				PopulateScreen();
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
    
    public void showAlarmPopup(View view) {
    	
    	View popupWindowView = Popup.setPopupWindowLayout(view.getContext(), Popup.RESOURCE_ID_POPUP_ALARM);
    	
    	TextView textViewExitTime = (TextView)popupWindowView.findViewById(R.id.popupAlarmTextViewExitTime);
    	textViewExitTime.setText(timeToGo);
    	
    	Button buttonTurnOff = (Button)popupWindowView.findViewById(R.id.popupAlarmButtonTurnOff);
		buttonTurnOff.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Preference.setAlarmState(view.getContext(), false);
				imageViewAlarm.setImageResource(R.drawable.ic_alarm_off);
				
				Popup.removePopup();
			}
		});
    	
		Button buttonTurnOn = (Button)popupWindowView.findViewById(R.id.popupAlarmButtonTurnOn);
		buttonTurnOn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Preference.setAlarmState(view.getContext(), true);
				imageViewAlarm.setImageResource(R.drawable.ic_alarm_on);
				
				Popup.removePopup();
			}
		});
    }
    
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
    	
    	if (keyCode == KeyEvent.KEYCODE_MENU) {
    		goToConfiguration(null);
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
    public void goToConfiguration(View view) {
    	
    	Popup.removePopup();
    	Intent intentConfiguration = new Intent(MainActivity.this, ConfigurationActivity.class);
    	startActivity(intentConfiguration);
    }
}