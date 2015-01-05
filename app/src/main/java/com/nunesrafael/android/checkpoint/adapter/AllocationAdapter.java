package com.nunesrafael.android.checkpoint.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.datasource.Repository;
import com.nunesrafael.android.checkpoint.font.Font;
import com.nunesrafael.android.checkpoint.model.Allocation;
import com.nunesrafael.android.checkpoint.popup.Popup;
import com.nunesrafael.android.checkpoint.util.CountingTime;
import com.nunesrafael.android.checkpoint.util.Util;

public class AllocationAdapter extends BaseAdapter {
	
	private int layoutResourceId;
	
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<Allocation> allocations;
	
	private Calendar calendar = Calendar.getInstance();
	private TimePicker timePicker;
	private boolean isEntry;
	private EditText editTextComment;
	
	
	public AllocationAdapter(Context context, ArrayList<Allocation> allocations, int layoutResourceId, Calendar calendar) {
		
		mContext = context;
		this.allocations = allocations;
		this.layoutResourceId = layoutResourceId;
		this.calendar = calendar;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return allocations.size();
	}

	public Object getItem(int position) {
		return allocations.get(position);
	}

	public long getItemId(int position) {
		return allocations.get(position).getId();
	}
	
	public ArrayList<Allocation> getAllocations() {
		return allocations;
	}
	
	public void setAllocations(ArrayList<Allocation> allocations) {
		this.allocations = allocations;
	}
	
	static class AllocationHolder {
		
		LinearLayout linearLayoutEntryTime;
		ImageView imageButtonEntry;
		TextView textViewEntryTime;
		
		LinearLayout linearLayoutExitTime;
		ImageView imageButtonExit;
		TextView textViewExitTime;
		
		ImageView imageButtonComment;
		ImageView imageViewCategory;
		ImageView imageButtonDelete;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		AllocationHolder allocationHolder;
		
		if(convertView == null) {
			convertView = mInflater.inflate(layoutResourceId, parent, false);
			
			// Changing the font
	 		Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(),Font.FONT_RESOURCE_PATH);
	 		Font.applyFonts(convertView, typeFace);
			
			allocationHolder = getRowLayout(convertView);
			setRowEvents(allocationHolder);
			
			convertView.setTag(allocationHolder);
		}
		else {
			allocationHolder = (AllocationHolder)convertView.getTag();
		}
		
		setRowInformations(allocationHolder, position);
		setRowTags(allocationHolder, position);
				
		return convertView;
	}
	
	public AllocationHolder getRowLayout(View convertView) {
		
		AllocationHolder allocationHolder = new AllocationHolder();
		
		allocationHolder.linearLayoutEntryTime = (LinearLayout)convertView.findViewById(R.id.rowAllocationLinearLayoutEntryTime);
		allocationHolder.imageButtonEntry = (ImageView)convertView.findViewById(R.id.rowAllocationImageButtonEntryTime);
		allocationHolder.textViewEntryTime = (TextView)convertView.findViewById(R.id.rowAllocationTextViewEntryTime);
		
		allocationHolder.linearLayoutExitTime = (LinearLayout)convertView.findViewById(R.id.rowAllocationLinearLayoutExitTime);
		allocationHolder.imageButtonExit = (ImageView)convertView.findViewById(R.id.rowAllocationImageButtonExitTime);
		allocationHolder.textViewExitTime = (TextView)convertView.findViewById(R.id.rowAllocationTextViewExitTime);
		
		allocationHolder.imageButtonComment = (ImageView)convertView.findViewById(R.id.rowAllocationImageButtonComment);
		allocationHolder.imageViewCategory = (ImageView)convertView.findViewById(R.id.rowAllocationImageViewCategory);
		allocationHolder.imageButtonDelete = (ImageView)convertView.findViewById(R.id.rowAllocationImageButtonDelete);
				
		return allocationHolder;
	}
	
	public void setRowInformations(AllocationHolder allocationHolder, int position) {
		
		Allocation allocation = allocations.get(position);
		
		// Entry time
		if(allocation.getEntryTime() != null) {
			allocationHolder.imageButtonEntry.setBackgroundResource(R.drawable.ic_triangle_entry);
		}
		else
			allocationHolder.imageButtonEntry.setBackgroundResource(R.drawable.ic_triangle_gray_right);
		
		allocationHolder.textViewEntryTime.setText(Util.getHourOfDay(allocation.getEntryTime()));
		
		// Exit Time
		if(allocation.getExitTime() != null) {
			allocationHolder.imageButtonExit.setBackgroundResource(R.drawable.ic_triangle_exit);
		}
		else {
			allocationHolder.imageButtonExit.setBackgroundResource(R.drawable.ic_triangle_gray_left);
		}
		
		allocationHolder.textViewExitTime.setText(Util.getHourOfDay(allocation.getExitTime()));
		if(allocation.getExitTime() == null) {
			//Reminder exit time on past
			Date showingDate = new Date();
			String strShowingDate = Util.getFormattedDate(showingDate);
			String entryDate = Util.getFormattedDate(allocation.getEntryTime());
			if( (entryDate.compareTo(strShowingDate)) < 0 && (allocation.getId() != -1) ) {
				allocationHolder.textViewExitTime.setTextColor(mContext.getResources().getColor(R.color.text_red));
				allocationHolder.textViewExitTime.setText("00:00");
			}
		}
		else
			allocationHolder.textViewExitTime.setTextColor(mContext.getResources().getColor(R.color.text_gray));
		
		// Comment
		if(allocation.getComment() != null && !allocation.getComment().equalsIgnoreCase(""))
			allocationHolder.imageButtonComment.setBackgroundResource(R.drawable.selector_comment_filled);
		else
			allocationHolder.imageButtonComment.setBackgroundResource(R.drawable.selector_comment_empty);
	}
	
	public void setRowTags(AllocationHolder allocationHolder, int position) {
		
		allocationHolder.linearLayoutEntryTime.setTag(allocations.get(position));
		allocationHolder.linearLayoutExitTime.setTag(allocations.get(position));
		allocationHolder.imageButtonComment.setTag(allocations.get(position));
		allocationHolder.imageViewCategory.setTag(allocations.get(position));
		allocationHolder.imageButtonDelete.setTag(allocations.get(position));
	}
	
	public void setRowEvents(AllocationHolder allocationHolder) {
		
		allocationHolder.linearLayoutEntryTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Allocation allocation = (Allocation)view.getTag();
				if(allocation.getEntryTime() == null && CountingTime.isToday(calendar.getTime()) == true) {
					insertEntryTime(allocation);
				}
				else {
					isEntry = true;
					showTimePopup(allocation);
				}
			}
		});
		
		allocationHolder.linearLayoutExitTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Allocation allocation = (Allocation)view.getTag();
				if(isEmptyCheckPoint(allocation)) {
					Toast.makeText(mContext, mContext.getString(R.string.insert_entry_first), Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(allocation.getExitTime() == null && CountingTime.isToday(calendar.getTime()) == true) {
					insertExitTime(allocation);
				}
				else {
					isEntry = false;
					showTimePopup(allocation);
				}
			}
		});
		
		allocationHolder.imageButtonComment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Allocation allocation = (Allocation)view.getTag();
				if(isEmptyCheckPoint(allocation))
					Toast.makeText(mContext, mContext.getString(R.string.checkpoint_empty), Toast.LENGTH_SHORT).show();
				else
					showCommentPopup(allocation);
			}
		});
		
		allocationHolder.imageViewCategory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
			}
		});
		
		allocationHolder.imageButtonDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Allocation allocation = (Allocation)view.getTag();
				if(isEmptyCheckPoint(allocation))
					Toast.makeText(mContext, mContext.getString(R.string.checkpoint_empty), Toast.LENGTH_SHORT).show();
				else
					showDeletePopup(allocation);
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
			if(allocation.getEntryTime() != null)
				calendar.setTime(allocation.getEntryTime());
			timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		} else {
			textViewTitle.setText(mContext.getString(R.string.edit_exit_time));
			if(allocation.getExitTime() != null)
				calendar.setTime(allocation.getExitTime());
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
					if(allocation.getEntryTime() == null)
						insertEntryTime(allocation);
					else {
						if(new Repository(mContext).updateEntryTime(allocation.getId(), calendar.getTime()))
							allocation.setEntryTime(calendar.getTime());
					}
				}
				else {
					if(allocation.getExitTime() == null)
						insertExitTime(allocation);
					else {
						if(new Repository(mContext).updateExitTime(allocation.getId(), calendar.getTime()))
							allocation.setExitTime(calendar.getTime());
					}
				}
				Popup.removePopup();
				
				setTime();
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
					allocations.remove(allocation);
				addEmptyAllocationIfNecessary();
				
				Popup.removePopup();
				
				setTime();
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
	
	public void setTextViewCheckPointTime(TextView textViewCheckPointTime, Allocation allocation) {
		
		// CheckPoint Time Format: 01/01/2013(08:00 - 12:00)
		String checkPointTime;
		if(allocation.getEntryTime() == null) {
			checkPointTime = Util.getFormattedDate(calendar.getTime());
			
		}
		else {
			checkPointTime = Util.getFormattedDate(allocation.getEntryTime());
			checkPointTime += " (" + Util.getHourOfDay(allocation.getEntryTime());
			if(allocation.getExitTime() == null)
				checkPointTime += ")";
			else
				checkPointTime = checkPointTime + " - " + Util.getHourOfDay(allocation.getExitTime()) + ")";
		}
		textViewCheckPointTime.setText(checkPointTime);
	}
	
	public boolean isEmptyCheckPoint(Allocation allocation) {
		if(allocation.getId() == -1)
			return true;
		return false;
	}
	
	public void addEmptyAllocationIfNecessary() {
		if(allocations.size() == 0 || allocations.get(allocations.size() - 1).getExitTime() != null)
        	allocations.add(new Allocation(-1));
	}
	
	public void insertEntryTime(Allocation allocation) {
		
		if(allocation.getEntryTime() == null && CountingTime.isToday(calendar.getTime()) == true) {
			calendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
		}
		else {
			calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
			calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
		}
		
		allocation.setEntryTime(calendar.getTime());
		long id = new Repository(mContext).insertAllocation(allocation);
		if(id == -1)
			allocation.setEntryTime(null);
		allocation.setId(id);
		
		setTime();
		notifyDataSetChanged();
	}
	
	public void insertExitTime(Allocation allocation) {
		
		if(allocation.getExitTime() == null && CountingTime.isToday(calendar.getTime()) == true) {
			calendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
		}
		else {
			calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
			calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
		}
		
		if(new Repository(mContext).updateExitTime(allocation.getId(), calendar.getTime()))
			allocation.setExitTime(calendar.getTime());
		
		addEmptyAllocationIfNecessary();
		
		setTime();
		notifyDataSetChanged();
	}
	
	public void updateComment(Allocation allocation) {
		
		if(new Repository(mContext).updateComment(allocation.getId(), editTextComment.getText().toString()))
			allocation.setComment(editTextComment.getText().toString());
		
		Popup.removePopup();
		
		notifyDataSetChanged();
	}
	
	public void setTime() {
		
		Activity activity = (Activity)mContext;
		
		TextView textViewTotal = (TextView)activity.findViewById(R.id.mainTextViewTotal);
		String instantTotal = CountingTime.getInstantTotalFromDateFormatted(calendar.getTime(), mContext);
		textViewTotal.setText(instantTotal);
		
		TextView textViewBalanceTime = (TextView)activity.findViewById(R.id.listViewFooterTextViewBalanceTime);
		String instantBalance = CountingTime.getInstantBalanceFormatted(calendar.getTime(), mContext);
		textViewBalanceTime.setText(instantBalance);
		
		if(instantBalance.charAt(0) == '-') {
    		textViewBalanceTime.setTextColor(mContext.getResources().getColor(R.color.text_red));
    	}
    	else
    		textViewBalanceTime.setTextColor(mContext.getResources().getColor(R.color.text_green));
		
		LinearLayout linearLayoutAlarm = (LinearLayout)activity.findViewById(R.id.listViewFooterLinearLayoutAlarm);
		if(CountingTime.isToday(calendar.getTime()) && CountingTime.isTheLastExitOpenned(calendar.getTime(), mContext))
			linearLayoutAlarm.setVisibility(View.VISIBLE);
		else
			linearLayoutAlarm.setVisibility(View.INVISIBLE);
		
		TextView textViewTimeToGo = (TextView)activity.findViewById(R.id.listViewFooterTextViewTimeToGo);
    	String timeToGo = CountingTime.getExitTimeFormatted(allocations.get(0).getEntryTime(), mContext);
    	textViewTimeToGo.setText(timeToGo);
	}
}