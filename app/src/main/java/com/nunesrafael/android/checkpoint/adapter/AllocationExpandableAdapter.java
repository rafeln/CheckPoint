package com.nunesrafael.android.checkpoint.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.datasource.Repository;
import com.nunesrafael.android.checkpoint.font.Font;
import com.nunesrafael.android.checkpoint.model.Allocation;
import com.nunesrafael.android.checkpoint.popup.Popup;
import com.nunesrafael.android.checkpoint.util.CountingTime;
import com.nunesrafael.android.checkpoint.util.Util;

public class AllocationExpandableAdapter extends BaseExpandableListAdapter{

	private int layoutGroupResourceId;
	private int layoutChildResourceId;
	
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<ArrayList<Allocation>> allocations;
	
	private Calendar calendar = Calendar.getInstance();
	private TimePicker timePicker;
	private boolean isEntry;
	private EditText editTextComment;
	
	
	public AllocationExpandableAdapter(Context context, ArrayList<ArrayList<Allocation>> allocations, int layoutGroupResourceId, int layoutChildResourceId) {
		
		mContext = context;
		this.allocations = allocations;
		
		this.layoutGroupResourceId = layoutGroupResourceId;
		this.layoutChildResourceId = layoutChildResourceId;
		
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		
		return allocations.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		
		return allocations.get(groupPosition).get(childPosition).getId();
	}
	
	static class AllocationChildHolder {
		
		TextView textViewEntryTime;
		TextView textViewExitTime;
		TextView textViewDifference;		
		TextView textViewComment;
		
		Allocation allocation;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		
		AllocationChildHolder allocationChildHolder = new AllocationChildHolder();
		
		if(convertView == null) {
			convertView = mInflater.inflate(layoutChildResourceId, parent, false);
			
			// Changing the font
	 		Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(),Font.FONT_RESOURCE_PATH);
	 		Font.applyFonts(convertView, typeFace);
			
			allocationChildHolder = getChildRowLayout(convertView);
			
			convertView.setTag(allocationChildHolder);
			setChildRowEvents(convertView);
		}
		else {
			allocationChildHolder = (AllocationChildHolder)convertView.getTag();
		}
		setChildRowInformations(allocationChildHolder, groupPosition, childPosition);
				
		return convertView;
	}
	
	public AllocationChildHolder getChildRowLayout(View convertView) {
		
		AllocationChildHolder allocationChildHolder = new AllocationChildHolder();
		
		allocationChildHolder.textViewEntryTime = (TextView)convertView.findViewById(R.id.rowRelatoryChildTextViewEntryTime);
		allocationChildHolder.textViewExitTime = (TextView)convertView.findViewById(R.id.rowRelatoryChildTextViewExitTime);
		allocationChildHolder.textViewDifference = (TextView)convertView.findViewById(R.id.rowRelatoryChildTextViewDifference);
		allocationChildHolder.textViewComment = (TextView)convertView.findViewById(R.id.rowRelatoryChildTextViewComment);
		
		return allocationChildHolder;
	}
	
	public void setChildRowInformations(AllocationChildHolder allocationChildHolder, int groupPosition, int childPosition) {
		
		Date entryTime = allocations.get(groupPosition).get(childPosition).getEntryTime();
		Date exitTime = allocations.get(groupPosition).get(childPosition).getExitTime();
		
		allocationChildHolder.allocation = allocations.get(groupPosition).get(childPosition);
		
		allocationChildHolder.textViewEntryTime.setText(Util.getHourOfDay(entryTime));
		if(exitTime != null) {
			allocationChildHolder.textViewExitTime.setTextColor(mContext.getResources().getColor(R.color.text_green));
			allocationChildHolder.textViewExitTime.setText(Util.getHourOfDay(exitTime));
		}
		else {
			allocationChildHolder.textViewExitTime.setTextColor(mContext.getResources().getColor(R.color.text_red));
			allocationChildHolder.textViewExitTime.setText("00:00");
		}
		
		if(exitTime != null) {
			allocationChildHolder.textViewDifference.setTextColor(mContext.getResources().getColor(R.color.text_gray));
			allocationChildHolder.textViewDifference.setText(CountingTime.getDifferenceFormatted(exitTime, entryTime));
		}
		else {
			allocationChildHolder.textViewDifference.setTextColor(mContext.getResources().getColor(R.color.text_red));
			allocationChildHolder.textViewDifference.setText("00:00");
		}
		
		allocationChildHolder.textViewComment.setText(allocations.get(groupPosition).get(childPosition).getComment());
	}
	
	public void setChildRowEvents(View convertView) {
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				showPopupOptions(view);
				
			}
		});
	}
	
	public void showPopupOptions(View view) {
		
		AllocationChildHolder allocationChildHolder = (AllocationChildHolder)view.getTag();
		
		View popupWindowView = Popup.setPopupWindowLayout(mContext, Popup.RESOURCE_ID_POPUP_OPTIONS);
		
		TextView textViewEditEntryTime = (TextView)popupWindowView.findViewById(R.id.popupOptionsTextViewEditEntryTime);
		TextView textViewEditExitTime = (TextView)popupWindowView.findViewById(R.id.popupOptionsTextViewEditExitTime);
		TextView textViewEditComment = (TextView)popupWindowView.findViewById(R.id.popupOptionsTextViewEditComment);
		TextView textViewDelete = (TextView)popupWindowView.findViewById(R.id.popupOptionsTextViewDelete);
		TextView textViewRemoveFromListView = (TextView)popupWindowView.findViewById(R.id.popupOptionsTextViewRemoveFromListView);
		Button buttonCancel = (Button)popupWindowView.findViewById(R.id.popupOptionsButtonCancel);
		
		textViewEditEntryTime.setTag(allocationChildHolder.allocation);
		textViewEditExitTime.setTag(allocationChildHolder.allocation);
		textViewEditComment.setTag(allocationChildHolder.allocation);
		textViewDelete.setTag(allocationChildHolder.allocation);
		textViewRemoveFromListView.setTag(allocationChildHolder.allocation);
		
		
		textViewEditEntryTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Popup.removePopup();
				isEntry = true;
				Allocation allocation = (Allocation)view.getTag();
				showTimePopup(allocation);
			}
		});
		
		textViewEditExitTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Popup.removePopup();
				isEntry = false;
				Allocation allocation = (Allocation)view.getTag();
				showTimePopup(allocation);
			}
		});
		
		textViewEditComment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Popup.removePopup();
				Allocation allocation = (Allocation)view.getTag();
				showCommentPopup(allocation);
				
			}
		});
		
		textViewDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Popup.removePopup();
				Allocation allocation = (Allocation)view.getTag();
				showDeletePopup(allocation);
			}
		});
		
		textViewRemoveFromListView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Popup.removePopup();
				Allocation allocation = (Allocation)view.getTag();
				showRemovePopup(allocation);
				
			}
		});
		
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Popup.removePopup();
				
			}
		});
	}
	
	public void showTimePopup(Allocation allocation) {
		
		View popupWindowView = Popup.setPopupWindowLayout(mContext, Popup.RESOURCE_ID_POPUP_TIME);
		
		timePicker = (TimePicker)popupWindowView.findViewById(R.id.popupTimeTimePicker);
		timePicker.setIs24HourView(true);
		
		TextView textViewTitle = (TextView)popupWindowView.findViewById(R.id.popupTimeTextViewTitle);
		TextView textViewTimeText = (TextView)popupWindowView.findViewById(R.id.popupTimeTextViewTimeText);
		TextView textViewCheckPointTime = (TextView)popupWindowView.findViewById(R.id.popupTimeTextViewCheckPointTime);
		
		if(isEntry){
			textViewTitle.setText(mContext.getString(R.string.edit_entry_time));
			
			calendar.setTime(allocation.getEntryTime());
			timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		} else {
			textViewTitle.setText(mContext.getString(R.string.edit_exit_time));
			if(allocation.getExitTime() != null)
				calendar.setTime(allocation.getExitTime());
			else
				calendar.setTime(allocation.getEntryTime());
			timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		}
		
		textViewTimeText.setText("");
		setTextViewCheckPointTime(textViewCheckPointTime, allocation);
		
		Button buttonOk = (Button)popupWindowView.findViewById(R.id.popupTimeButtonOk);
		buttonOk.setTag(allocation);
		buttonOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Allocation allocation = (Allocation)view.getTag();
				
				calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
				calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
				
				if(isEntry) {
					if(new Repository(mContext).updateEntryTime(allocation.getId(), calendar.getTime()))
						allocation.setEntryTime(calendar.getTime());
				}
				else {
					if(new Repository(mContext).updateExitTime(allocation.getId(), calendar.getTime()))
						allocation.setExitTime(calendar.getTime());
				}
				
				
				Popup.removePopup();
				
				setHeaderInformations();
				notifyDataSetChanged();
			}
		});
		Button buttonCancel = (Button)popupWindowView.findViewById(R.id.popupTimeButtonCancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Popup.removePopup();
			}
		});
	}
	
	public void showCommentPopup(Allocation allocation) {
		
		View popupWindowView = Popup.setPopupWindowLayout(mContext, Popup.RESOURCE_ID_POPUP_COMMENT);
		
		TextView TextViewTitle = (TextView)popupWindowView.findViewById(R.id.popupCommentTextViewTitle);
		if( allocation.getComment() == null || (allocation.getComment().equalsIgnoreCase("")) )
			TextViewTitle.setText(mContext.getString(R.string.insert_comment));
		else
			TextViewTitle.setText(mContext.getString(R.string.edit_comment));
		
		TextView TextViewCheckPointTime = (TextView)popupWindowView.findViewById(R.id.popupCommentTextViewCheckPointTime);
		setTextViewCheckPointTime(TextViewCheckPointTime, allocation);
		
		editTextComment = (EditText)popupWindowView.findViewById(R.id.popupCommentEditText);
		editTextComment.setText(allocation.getComment());
		editTextComment.setTag(allocation);
		
		editTextComment.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					
					Allocation allocation = (Allocation)view.getTag();
					updateComment(allocation);
					
					return true;
		        }
		        return false;
		    }
		});
				
		Button buttonOk = (Button)popupWindowView.findViewById(R.id.popupCommentButtonOk);
		buttonOk.setTag(allocation);
		buttonOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Allocation allocation = (Allocation)view.getTag();
				updateComment(allocation);
			}
		});
		
		Button buttonCancel = (Button)popupWindowView.findViewById(R.id.popupCommentButtonCancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Popup.removePopup();
			}
		});
	}
	
	public void updateComment(Allocation allocation) {
		
		if(new Repository(mContext).updateComment(allocation.getId(), editTextComment.getText().toString()))
			allocation.setComment(editTextComment.getText().toString());
		
		Popup.removePopup();
		
		notifyDataSetChanged();
	}
	
	public void showDeletePopup(Allocation allocation) {
		
		View popupWindowView = Popup.setPopupWindowLayout(mContext, Popup.RESOURCE_ID_POPUP_DELETE);
		
		TextView TextViewCheckPointTime = (TextView)popupWindowView.findViewById(R.id.popupDeleteTextViewCheckPointTime);
		setTextViewCheckPointTime(TextViewCheckPointTime, allocation);
		
		Button buttonOk = (Button)popupWindowView.findViewById(R.id.popupDeleteButtonOk);
		buttonOk.setTag(allocation);
		buttonOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Allocation allocation = (Allocation)view.getTag();
				if( new Repository(mContext).deleteAllocation(allocation.getId()) )
					removeAllocationById(allocation);
				
				Popup.removePopup();
				
				setHeaderInformations();
				notifyDataSetChanged();
			}
		});
		
		Button buttonCancel = (Button)popupWindowView.findViewById(R.id.popupDeleteButtonCancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Popup.removePopup();
			}
		});
	}
	
	public void showRemovePopup(Allocation allocation) {
		
		View popupWindowView = Popup.setPopupWindowLayout(mContext, Popup.RESOURCE_ID_POPUP_DELETE);
		
		TextView TextViewTitle = (TextView)popupWindowView.findViewById(R.id.popupDeleteTextViewTitle);
		TextViewTitle.setText(mContext.getString(R.string.remove_from_listview));
		
		TextView TextViewCheckPointTime = (TextView)popupWindowView.findViewById(R.id.popupDeleteTextViewCheckPointTime);
		setTextViewCheckPointTime(TextViewCheckPointTime, allocation);
		
		TextView TextViewMessage = (TextView)popupWindowView.findViewById(R.id.popupDeleteTextViewMessage);
		TextViewMessage.setText(mContext.getString(R.string.remove_from_listview_confirmation));
		
		Button buttonOk = (Button)popupWindowView.findViewById(R.id.popupDeleteButtonOk);
		buttonOk.setTag(allocation);
		buttonOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Allocation allocation = (Allocation)view.getTag();
				removeAllocationById(allocation);
				
				Popup.removePopup();
				
				setHeaderInformations();
				notifyDataSetChanged();
			}
		});
		
		Button buttonCancel = (Button)popupWindowView.findViewById(R.id.popupDeleteButtonCancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Popup.removePopup();
			}
		});
	}
	
	public boolean removeAllocationById(Allocation allocation) {
		
		if(allocation == null) {
			Log.w("removeAllocationById", "The allocation is null");
			return false;
		}
		
		for (int i = 0; i < allocations.size(); i++) {
			for (int j = 0; j < allocations.get(i).size(); j++) {
				if(allocations.get(i).get(j).getId() == allocation.getId()) {
					
					allocations.get(i).remove(j);
					if(allocations.get(i).size() == 0)
						allocations.remove(i);
					
					notifyDataSetChanged();
					return true;
				}
			}
		}
		return false;
	}
	
	public void setTextViewCheckPointTime(TextView textViewCheckPointTime, Allocation allocation) {
		
		// CheckPoint Time Format: 01/01/2013(08:00 - 12:00)
		String checkPointTime = Util.getFormattedDate(allocation.getEntryTime());
		checkPointTime += " (" + Util.getHourOfDay(allocation.getEntryTime());
		if(allocation.getExitTime() == null)
			checkPointTime += ")";
		else
			checkPointTime = checkPointTime + " - " + Util.getHourOfDay(allocation.getExitTime()) + ")";	
		
		textViewCheckPointTime.setText(checkPointTime);
	}
	
	public void setHeaderInformations() {
		
		Activity activity = (Activity)mContext;
		
		TextView textViewInitDate = (TextView)activity.findViewById(R.id.listViewHeaderTextViewInitDate);
		TextView textViewEndDate = (TextView)activity.findViewById(R.id.listViewHeaderTextViewEndDate);
		TextView textViewTotal = (TextView)activity.findViewById(R.id.listViewHeaderTextViewTotal);
		
		if(allocations.size() > 0) {
			 
			 textViewInitDate.setText(Util.getFormattedDate(allocations.get(allocations.size() - 1).get(0).getEntryTime()));
			 textViewEndDate.setText(Util.getFormattedDate(allocations.get(0).get(0).getEntryTime()));
			 textViewTotal.setText(CountingTime.getTotalFromPeriodFormatted(allocations, mContext));
		 }
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		
		return allocations.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		
		return allocations.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		
		return allocations.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		
		return 0;
	}
	
	static class AllocationGroupHolder {
		
		TextView textViewDayOfWeek;
		TextView textViewDate;
		TextView textViewTotalOfDay;		
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,	View convertView, ViewGroup parent) {
		
		AllocationGroupHolder allocationGroupHolder = new AllocationGroupHolder();
		
		if(convertView == null) {
			convertView = mInflater.inflate(layoutGroupResourceId, parent, false);
			
			// Changing the font
	 		Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(),Font.FONT_RESOURCE_PATH);
	 		Font.applyFonts(convertView, typeFace);
			
			allocationGroupHolder = getGroupRowLayout(convertView);
			
			convertView.setTag(allocationGroupHolder);
		}
		else {
			allocationGroupHolder = (AllocationGroupHolder)convertView.getTag();
		}
		setGroupRowInformations(allocationGroupHolder, groupPosition);
				
		return convertView;
	}
	
	public AllocationGroupHolder getGroupRowLayout(View convertView) {
		
		AllocationGroupHolder allocationGroupHolder = new AllocationGroupHolder();
		
		allocationGroupHolder.textViewDayOfWeek = (TextView)convertView.findViewById(R.id.rowRelatoryGroupTextViewDayOfWeek);
		allocationGroupHolder.textViewDate = (TextView)convertView.findViewById(R.id.rowRelatoryGroupTextViewDate);
		allocationGroupHolder.textViewTotalOfDay = (TextView)convertView.findViewById(R.id.rowRelatoryGroupTextViewTotalOfDay);
		
		return allocationGroupHolder;
	}
	
	public void setGroupRowInformations(AllocationGroupHolder allocationGroupHolder, int groupPosition) {
		
		allocationGroupHolder.textViewDayOfWeek.setText(Util.getDayOfWeek(allocations.get(groupPosition).get(0).getEntryTime(), mContext));
		allocationGroupHolder.textViewDate.setText(Util.getSortedDate(allocations.get(groupPosition).get(0).getEntryTime()));
		allocationGroupHolder.textViewTotalOfDay.setText(CountingTime.getTotalFromDateFormatted((allocations.get(groupPosition).get(0).getEntryTime()), mContext));
	}
	
	@Override
	public boolean hasStableIds() {
		
		return false;
	}
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		
		return true;
	}
}