package com.nunesrafael.android.checkpoint.datasource;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;
import com.nunesrafael.android.checkpoint.R;
import com.nunesrafael.android.checkpoint.model.Allocation;
import com.nunesrafael.android.checkpoint.util.Util;

public class Repository {

	private String[] allColumns = { OpenDataBaseHelper.COLUMN_ID,
			OpenDataBaseHelper.COLUMN_DATE,
			OpenDataBaseHelper.COLUMN_ENTRY_HOUR,
			OpenDataBaseHelper.COLUMN_EXIT_HOUR,
			OpenDataBaseHelper.COLUMN_COMMENT,
			OpenDataBaseHelper.COLUMN_CATEGORY };
	
	private SQLiteDatabase dataBase;
	private OpenDataBaseHelper openDatabaseHelper;
	private Context mContext;
	
	public Repository(Context context)
	{
		mContext = context;
		openDatabaseHelper = new OpenDataBaseHelper(context);
	}
	
	public void open() throws SQLException {
		dataBase = openDatabaseHelper.getWritableDatabase();
	}
	
	public void close() {
		openDatabaseHelper.close();
	}
	
	public boolean checkTimeInconsistencies(Allocation allocation) {
		
		if(allocation == null) {
			Log.w("checkTimeInconsistencies", "newAllocation is NULL");
			return true;
		}
		
		if(allocation.getEntryTime() == null) {
			Log.w("checkTimeInconsistencies", "Entry Time is NULL");
			return true;
		}
		
		String strEntryTime = Util.getHourOfDay(allocation.getEntryTime());
		String strExitTime  = Util.getHourOfDay(allocation.getExitTime());
		
		// Entry is bigger than exit
		if (!strExitTime.equalsIgnoreCase("") && strEntryTime.compareTo(strExitTime) > 0)
		{
			if(mContext != null)
				Toast.makeText(mContext, mContext.getString(R.string.time_error_entry_gt_exit), Toast.LENGTH_LONG).show();
			return true;
		}
		
		ArrayList<Allocation> allocations = getAllocationsFromDate(allocation.getEntryTime());
		if(allocations.size() == 0)
			return false;
		
		int allocationPosition = getAllocationPositionById(allocations, allocation.getId());
		
		
		// ENTRY is less than last exit
		String strBeforeExitTime = "";
		if(allocationPosition == -1)
			strBeforeExitTime = Util.getHourOfDay(allocations.get(allocations.size() - 1).getExitTime());
		if(allocationPosition > 0)
			strBeforeExitTime = Util.getHourOfDay(allocations.get(allocationPosition - 1).getExitTime());
		
		if(!strBeforeExitTime.equalsIgnoreCase("") && strEntryTime.compareTo(strBeforeExitTime) < 0) {
			if(mContext != null)
				Toast.makeText(mContext, mContext.getString(R.string.time_error_entry_lt_last_exit), Toast.LENGTH_LONG).show();
			return true;
		}
				
		// EXIT is bigger than post entry
		String strPostEntryTime = "";
		if(allocationPosition != -1 && allocationPosition < allocations.size() - 1) {
			strPostEntryTime = Util.getHourOfDay(allocations.get(allocationPosition + 1).getEntryTime());
			if(strExitTime.compareTo(strPostEntryTime) > 0) {
				if(mContext != null)
					Toast.makeText(mContext, mContext.getString(R.string.time_error_exit_gt_post_entry), Toast.LENGTH_LONG).show();
				return true;
			}
		}
		return false;
	}
	
	public long insertAllocation(Allocation newAllocation) {
		try {
			
			if(checkTimeInconsistencies(newAllocation))
				return -1;
			
			String strSortedDate = Util.getSortedDate(newAllocation.getEntryTime());
			String strEntryTime = Util.getHourOfDay(newAllocation.getEntryTime());
			String strExitTime  = Util.getHourOfDay(newAllocation.getExitTime());
			String comment      = newAllocation.getComment();
			String category     = newAllocation.getCategory();
			
			ContentValues values = new ContentValues();
			values.put(OpenDataBaseHelper.COLUMN_DATE, strSortedDate);
			values.put(OpenDataBaseHelper.COLUMN_ENTRY_HOUR, strEntryTime);
			values.put(OpenDataBaseHelper.COLUMN_EXIT_HOUR, strExitTime);
			values.put(OpenDataBaseHelper.COLUMN_COMMENT, comment);
			values.put(OpenDataBaseHelper.COLUMN_CATEGORY, category);
			
			open();
			
			long id = dataBase.insert(OpenDataBaseHelper.TABLE_ALLOCATIONS, null, values);
			if(id == -1)
				Log.w("insertAllocation", "0 Row affected");
			
			close();
			return id;
		}
		catch(Exception exception) {
			exception.printStackTrace();
			return -1;
		}
		finally {
			close();
		}
	}
	
	public boolean updateEntryTime(long allocationId, Date entryTime) {
		try {
			
			if(entryTime == null)
			{
				Log.w("updateEntryTime", "entryTime is NULL");
				return false;
			}
			
			Allocation allocation = getAllocationById(allocationId);
			if(allocation == null)
			{
				Log.w("updateEntryTime", "allocation is NULL, allocationId = " + allocationId);
				return false;
			}
			allocation.setEntryTime(entryTime);
			
			if(checkTimeInconsistencies(allocation))
				return false;
			
			String strEntryTime = Util.getHourOfDay(entryTime);
			
			ContentValues values = new ContentValues();
			values.put(OpenDataBaseHelper.COLUMN_ENTRY_HOUR, strEntryTime);			
			
			open();
			
			boolean result = false;
			if( dataBase.update(OpenDataBaseHelper.TABLE_ALLOCATIONS, values, OpenDataBaseHelper.COLUMN_ID + " = " + allocationId, null) > 0)
				result = true;
			
			close();
			return result;
			
		}
		catch(Exception exception) {
			exception.printStackTrace();
			return false;
		}
		finally {
			close();
		}
	}
	
	public boolean updateExitTime(long allocationId, Date exitTime) {
		try {
			
			if(exitTime == null)
			{
				Log.w("updateExitTime", "entryTime is NULL");
				return false;
			}
			
			Allocation allocation = getAllocationById(allocationId);
			if(allocation == null)
			{
				Log.w("updateExitTime", "allocation is NULL, allocationId = " + allocationId);
				return false;
			}
			allocation.setExitTime(exitTime);
			
			if(checkTimeInconsistencies(allocation))
				return false;
			
			String strExitTime = Util.getHourOfDay(exitTime);
			
			
			ContentValues values = new ContentValues();
			values.put(OpenDataBaseHelper.COLUMN_EXIT_HOUR, strExitTime);			
			
			open();
			
			boolean result = false;
			if( dataBase.update(OpenDataBaseHelper.TABLE_ALLOCATIONS, values, OpenDataBaseHelper.COLUMN_ID + " = " + allocationId, null) > 0)
				result = true;
			
			close();
			return result;
			
		}
		catch(Exception exception) {
			exception.printStackTrace();
			return false;
		}
		finally {
			close();
		}
	}
	
	public boolean updateComment(long allocationId, String comment) {
		try {
			
			boolean result = false;
						
			ContentValues values = new ContentValues();
			values.put(OpenDataBaseHelper.COLUMN_COMMENT, comment);			
			
			open();
			
			if( dataBase.update(OpenDataBaseHelper.TABLE_ALLOCATIONS, values, OpenDataBaseHelper.COLUMN_ID + " = " + allocationId, null) > 0)
				result = true;
			else
				Log.w("updateComment", "0 Row affected, allocationId = " + allocationId);
			
			close();
			return result;
			
		}
		catch(Exception exception) {
			exception.printStackTrace();
			return false;
		}
		finally {
			close();
		}
	}
	
	public ArrayList<ArrayList<Allocation>> findComment(String comment) {
		
		ArrayList<ArrayList<Allocation>> allocations = new ArrayList<ArrayList<Allocation>>();
		try {
			
			open();
			
			Cursor cursor = dataBase.query(OpenDataBaseHelper.TABLE_ALLOCATIONS, allColumns,
					OpenDataBaseHelper.COLUMN_COMMENT + " LIKE '%" + comment + "%' ", null,
					OpenDataBaseHelper.COLUMN_DATE, null, OpenDataBaseHelper.COLUMN_DATE + " DESC");
			
			while (cursor.moveToNext()) {
				Allocation allocation = cursorToAllocation(cursor);
				allocations.add(getCommentsFromDate(comment,allocation.getEntryTime()));
			}
			
			cursor.close();
			close();
			
		} catch(Exception exception) {
			exception.printStackTrace();
			return allocations;
		}
		finally {
			close();
		}
		return allocations;
	}
	
	public ArrayList<Allocation> getCommentsFromDate(String comment, Date date) {
		
		ArrayList<Allocation> allocations = new ArrayList<Allocation>();
		
		try {
			
			String strSortedDate = Util.getSortedDate(date);
			
			open();
			
			Cursor cursor = dataBase.query(OpenDataBaseHelper.TABLE_ALLOCATIONS, allColumns,
					OpenDataBaseHelper.COLUMN_DATE + " = '" + strSortedDate + "' AND " + OpenDataBaseHelper.COLUMN_COMMENT + " LIKE '%" + comment + "%' ",
					null, null, null, OpenDataBaseHelper.COLUMN_ENTRY_HOUR);
			
			while (cursor.moveToNext()) {
				Allocation allocation = cursorToAllocation(cursor);
				allocations.add(allocation);
			}
			
			cursor.close();
			close();
			
		} catch(Exception exception) {
			exception.printStackTrace();
			return allocations;
		}
		finally {
			close();
		}
		return allocations;
	}
	
	public boolean deleteAllocation(long allocationId) {
		try {
			boolean result = false;
			
			if(allocationId == -1)
			{
				Log.w("deleteAllocation", "allocationId = " + allocationId);
				return result;
			}
			
			open();
			
			if(dataBase.delete(OpenDataBaseHelper.TABLE_ALLOCATIONS, OpenDataBaseHelper.COLUMN_ID + " = " + allocationId, null) > 0)
				result = true;
			else
				Log.w("deleteAllocation", "0 Row affected, allocationId = " + allocationId);
			
			close();
			return result;
			
		}
		catch(Exception exception) {
			exception.printStackTrace();
			return false;
		}
		finally {
			close();
		}
	}
	
	public ArrayList<ArrayList<Allocation>> getRelatory(Date initDate, Date endDate) {
		
		ArrayList<ArrayList<Allocation>> allocations = new ArrayList<ArrayList<Allocation>>();
		
		try {
			
			String strSortedInitDate = Util.getSortedDate(initDate);
			String strSortedEndDate = Util.getSortedDate(endDate);
			
			open();
			
			Cursor cursor = dataBase.query(OpenDataBaseHelper.TABLE_ALLOCATIONS, allColumns,
					OpenDataBaseHelper.COLUMN_DATE + " BETWEEN '" + strSortedInitDate + "' AND '" + strSortedEndDate + "'"
					, null, OpenDataBaseHelper.COLUMN_DATE, null, OpenDataBaseHelper.COLUMN_DATE + " ASC");
			
			while (cursor.moveToNext()) {
				Allocation allocation = cursorToAllocation(cursor);
				allocations.add(getAllocationsFromDate(allocation.getEntryTime()));
			}
			
			cursor.close();
			close();
			
		} catch(Exception exception) {
			exception.printStackTrace();
			return allocations;
		}
		finally {
			close();
		}
		return allocations;
	}
	
	public ArrayList<Allocation> getAllocationsFromDate(Date date) {
		
		ArrayList<Allocation> allocations = new ArrayList<Allocation>();
		
		try {
			
			String strSortedDate = Util.getSortedDate(date);
			
			open();
			
			Cursor cursor = dataBase.query(OpenDataBaseHelper.TABLE_ALLOCATIONS, allColumns,
					OpenDataBaseHelper.COLUMN_DATE + " = '" + strSortedDate + "' ", null, null, null, OpenDataBaseHelper.COLUMN_ENTRY_HOUR);
			
			while (cursor.moveToNext()) {
				Allocation allocation = cursorToAllocation(cursor);
				allocations.add(allocation);
			}
			
			cursor.close();
			close();
			
		} catch(Exception exception) {
			exception.printStackTrace();
			return allocations;
		}
		finally {
			close();
		}
		return allocations;
	}
	
	public Allocation cursorToAllocation(Cursor cursor) {
		
		Allocation allocation = new Allocation();
		allocation.setId(cursor.getInt(cursor.getColumnIndex(OpenDataBaseHelper.COLUMN_ID)));
		
		String strDate      = cursor.getString(cursor.getColumnIndex(OpenDataBaseHelper.COLUMN_DATE));
		String strEntryHour = cursor.getString(cursor.getColumnIndex(OpenDataBaseHelper.COLUMN_ENTRY_HOUR));
		String strExitHour  = cursor.getString(cursor.getColumnIndex(OpenDataBaseHelper.COLUMN_EXIT_HOUR));
		
		if(!strEntryHour.equalsIgnoreCase(""))
		{
			Date entryTime = Util.stringToDate(strDate, strEntryHour);
			allocation.setEntryTime(entryTime);
		}
		else
			allocation.setEntryTime(null);
		if(!strExitHour.equalsIgnoreCase(""))
		{
			Date exitTime = Util.stringToDate(strDate, strExitHour);
			allocation.setExitTime(exitTime);
		}
		else
			allocation.setExitTime(null);
		String comment = cursor.getString(cursor.getColumnIndex(OpenDataBaseHelper.COLUMN_COMMENT));
		String category = cursor.getString(cursor.getColumnIndex(OpenDataBaseHelper.COLUMN_CATEGORY));
		allocation.setComment(comment);
		allocation.setCategory(category);
		
		return allocation;
	}
	
	public Allocation getAllocationById(long allocationId)
	{
		try {
			open();
			
			Cursor cursor = dataBase.query(OpenDataBaseHelper.TABLE_ALLOCATIONS, 
					allColumns, OpenDataBaseHelper.COLUMN_ID + " = " + allocationId, null, null, null, null);
			
			if (cursor.moveToFirst()) {
				Allocation allocation = cursorToAllocation(cursor);
				return allocation;
			}
			cursor.close();
			
			close();
			
		} catch(Exception exception) {
			exception.printStackTrace();
			return null;
		}
		finally {		
			close();
		}
		
		Log.w("getAllocationById", "AllocationId = " + allocationId + " not found in database, returned NULL");
		return null;
	}
	
	public int getAllocationPositionById(ArrayList<Allocation> allocations, long allocationId)
	{	
		for (int position = 0; position < allocations.size(); position++) {
			if(allocations.get(position).getId() == allocationId)
				return position;
		}
		Log.w("getAllocationPositionById", "AllocationId = " + allocationId + " not found in Array, returned -1");
		return -1;
	}
}