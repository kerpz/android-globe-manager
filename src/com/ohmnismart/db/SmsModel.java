package com.ohmnismart.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SmsModel extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "smsdb";
	private static final String SMS_TABLE = "sms";
	private static final String SMS_ID = "id";
	private static final String SMS_DATE = "date";
	private static final String SMS_SENDER = "sender";
	private static final String SMS_CONTENT = "content";

	public SmsModel(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_SMS_TABLE = "CREATE TABLE " + SMS_TABLE + "("
				+ SMS_ID + " INTEGER PRIMARY KEY,"
				+ SMS_DATE + " TEXT,"
				+ SMS_SENDER + " TEXT,"
				+ SMS_CONTENT + " TEXT" + ")";
		db.execSQL(CREATE_SMS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		//db.execSQL("DROP TABLE IF EXISTS " + SMS_TABLE);
		// Create tables again
		//onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new data
	public void addSMS(Sms sms) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(SMS_DATE, sms.getDate());
		values.put(SMS_SENDER, sms.getSender());
		values.put(SMS_CONTENT, sms.getContent());

		// Inserting Row
		db.insert(SMS_TABLE, null, values);
		db.close(); // Closing database connection
	}

	// Getting single data
	Sms getSMS(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(SMS_TABLE, new String[] { SMS_ID,
				SMS_DATE, SMS_SENDER, SMS_CONTENT }, SMS_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Sms sms = new Sms(Integer.parseInt(cursor.getString(0)),
										   cursor.getString(1),
										   cursor.getString(2),
										   cursor.getString(3));
		// return contact
		return sms;
	}
	
	public ArrayList<Sms> getAllSMS() {
		ArrayList<Sms> smsList = new ArrayList<Sms>();
	//public ArrayList<Device> getAllSMS() {
	//	ArrayList<Device> smsList = new ArrayList<Device>();
		
		String selectQuery = "SELECT  * FROM " + SMS_TABLE;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Sms sms = new Sms();
				sms.setID(Integer.parseInt(cursor.getString(0)));
				sms.setDate(cursor.getString(1));
				sms.setSender(cursor.getString(2));
				sms.setContent(cursor.getString(3));
				// Adding contact to list
				smsList.add(sms);
				
			} while (cursor.moveToNext());
		}

		// return contact list
		return smsList;
	}

	// Updating single contact
	public int updateSMS(Sms sms) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(SMS_DATE, sms.getDate());
		values.put(SMS_SENDER, sms.getSender());
		values.put(SMS_CONTENT, sms.getContent());

		// updating row
		return db.update(SMS_TABLE, values, SMS_ID + " = ?",
				new String[] { String.valueOf(sms.getID()) });
	}

	// Deleting single contact
	public void deleteSMS(Sms sms) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(SMS_TABLE, SMS_ID + " = ?",
				new String[] { String.valueOf(sms.getID()) });
		db.close();
	}


	// Getting contacts Count
	public int getSMSCount() {
		String countQuery = "SELECT  * FROM " + SMS_TABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}

}
