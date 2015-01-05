package com.nunesrafael.android.checkpoint.activity;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.adapter.AllocationExpandableAdapter;
import com.nunesrafael.android.checkpoint.datasource.Repository;
import com.nunesrafael.android.checkpoint.font.Font;
import com.nunesrafael.android.checkpoint.model.Allocation;
import com.nunesrafael.android.checkpoint.popup.Popup;
import com.nunesrafael.android.checkpoint.util.CountingTime;
import com.nunesrafael.android.checkpoint.util.Util;

public class FindCommentActivity extends Activity {

	public int RESOURCE_ID_HEADER = R.layout.header_listview_relatory;
	
	private EditText editTextComment;
	
	private TextView textViewComment;
	private LinearLayout linearLayoutNotFound;
	
	private ExpandableListView expandableListView;
	private AllocationExpandableAdapter allocationExpandableAdapter;
	private ArrayList<ArrayList<Allocation>> allocations;
	
	private TextView textViewDateInit;
	private TextView textViewDateEnd;
	private TextView textViewTotal;
	
	// Header
	private View header;
	
	private String comment = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    	
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_find_comment);
	     
	    editTextComment = (EditText)findViewById(R.id.findCommentEditTextComment);
	    expandableListView = (ExpandableListView)findViewById(R.id.findCommentExpandableListView);
	    
	    addHeader();
	    	    
	    textViewComment = (TextView)findViewById(R.id.findCommentTextViewComment);
	    linearLayoutNotFound = (LinearLayout)findViewById(R.id.findCommentLinearLayoutNotFound);
	    linearLayoutNotFound.setVisibility(View.INVISIBLE);
	     
	    editTextComment.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					findComment(view);
					return true;
			    }
			    return false;
			 }
		});
	    
	    // Changing the font
	    Typeface typeFace = Typeface.createFromAsset(getAssets(), Font.FONT_RESOURCE_PATH);
	  	Font.applyFonts(getWindow().getDecorView().findViewById(android.R.id.content), typeFace);
	 }
	
	 public void addHeader() {
		 
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
	    header = inflater.inflate(RESOURCE_ID_HEADER, null, false);
	    textViewDateInit = (TextView)header.findViewById(R.id.listViewHeaderTextViewInitDate);
	    textViewDateEnd = (TextView)header.findViewById(R.id.listViewHeaderTextViewEndDate);
	    textViewTotal = (TextView)header.findViewById(R.id.listViewHeaderTextViewTotal);
	    
	    expandableListView.addHeaderView(header, null, false);
	 }
	 
	 public void findComment(View view) {
		
		 comment = editTextComment.getText().toString();
		 if(comment.equalsIgnoreCase("")) {
			 Toast.makeText(FindCommentActivity.this, getString(R.string.fill_comment), Toast.LENGTH_SHORT).show();
			 return;
		 }
		
		 allocations = new Repository(FindCommentActivity.this).findComment(comment);
		 if(allocations.size() == 0) {
			 textViewComment.setText(comment);
			 linearLayoutNotFound.setVisibility(View.VISIBLE);
			 header.setVisibility(View.INVISIBLE);
			 
		 }
		 else {
			 
			 // DESC Order By
			 Date dateInit = allocations.get(allocations.size() - 1).get(0).getEntryTime();
			 textViewDateInit.setText(Util.getFormattedDate(dateInit));
			 
			 Date dateEnd = allocations.get(0).get(0).getEntryTime();
			 textViewDateEnd.setText(Util.getFormattedDate(dateEnd));
			 
			 textViewTotal.setText(CountingTime.getTotalFromPeriodFormatted(allocations, FindCommentActivity.this));
			 
			 linearLayoutNotFound.setVisibility(View.INVISIBLE);
			 header.setVisibility(View.VISIBLE);
		 }
		
		 // hide virtual keyboard
		 InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		 inputMethodManager.hideSoftInputFromWindow(editTextComment.getWindowToken(), 0);
		 
		 allocationExpandableAdapter = new AllocationExpandableAdapter(FindCommentActivity.this, allocations, R.layout.row_relatory_group, R.layout.row_relatory_child);
		 expandableListView.setAdapter(allocationExpandableAdapter);
		 
		// open all
	    for(int groupPosition = 0; groupPosition < allocations.size(); groupPosition++)
	        expandableListView.expandGroup(groupPosition);
	 }
	 
	 public void back(View view) {
		 
		 Popup.removePopup();
		 finish();
	 }
}