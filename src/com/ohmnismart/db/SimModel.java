package com.ohmnismart.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SimModel extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "simdb";
	private static final String SIM_TABLE = "sim";
	private static final String SIM_ID = "id";
	private static final String SIM_NUMBER = "number";
	private static final String SIM_EXPIRE = "expire";
	private static final String SIM_BALANCE = "balance";
	private static final String SIM_BALANCE_EXPIRE = "balance_expire";

	public SimModel(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_SIM_TABLE = "CREATE TABLE " + SIM_TABLE + "("
				+ SIM_ID + " INTEGER PRIMARY KEY,"
				+ SIM_NUMBER + " TEXT,"
				+ SIM_EXPIRE + " TEXT,"
				+ SIM_BALANCE + " TEXT,"
				+ SIM_BALANCE_EXPIRE + " TEXT" + ")";
		db.execSQL(CREATE_SIM_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//db.execSQL("DROP TABLE IF EXISTS " + SIM_TABLE);
		//onCreate(db);
	}


	// Create
	public void addSim(Sim sim) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(SIM_NUMBER, sim.getNumber());
		values.put(SIM_EXPIRE, sim.getExpire());
		values.put(SIM_BALANCE, sim.getBalance());
		values.put(SIM_BALANCE_EXPIRE, sim.getBalanceExpire());

		db.insert(SIM_TABLE, null, values);
		db.close();
	}

	// Read single row
	Sim getSim(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(SIM_TABLE, new String[] { SIM_ID,
				SIM_NUMBER, SIM_EXPIRE, SIM_BALANCE, SIM_BALANCE_EXPIRE }, SIM_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		/*
		Sim sim = new Sim(Integer.parseInt(cursor.getString(0)),
										cursor.getString(1),
										cursor.getString(2),
										cursor.getString(3),
										cursor.getString(4));
		*/
		Sim sim = new Sim();
		sim.setID(Integer.parseInt(cursor.getString(0)));
		sim.setNumber(cursor.getString(1));
		sim.setExpire(cursor.getString(2));
		sim.setBalance(cursor.getString(3));
		sim.setBalanceExpire(cursor.getString(4));

		return sim;
	}

	// Read all rows
	public ArrayList<Sim> getAllSim() {
		ArrayList<Sim> simList = new ArrayList<Sim>();
	//public ArrayList<Device> getAllSim() {
	//	ArrayList<Device> simList = new ArrayList<Device>();
		
		String selectQuery = "SELECT  * FROM " + SIM_TABLE;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Sim sim = new Sim();
				sim.setID(Integer.parseInt(cursor.getString(0)));
				sim.setNumber(cursor.getString(1));
				sim.setExpire(cursor.getString(2));
				sim.setBalance(cursor.getString(3));
				sim.setBalanceExpire(cursor.getString(4));
				simList.add(sim);
				/*
				Device device = new Device();
				device.setId(cursor.getInt(0));
				device.setHostName(cursor.getString(2));
				device.setMAC(cursor.getString(1));
				device.setIP(cursor.getString(3));
				simList.add(device);
				*/
				
			} while (cursor.moveToNext());
		}

		return simList;
	}
	
	// Update single row
	public int updateSim(Sim sim) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(SIM_NUMBER, sim.getNumber());
		values.put(SIM_EXPIRE, sim.getExpire());
		values.put(SIM_BALANCE, sim.getBalance());
		values.put(SIM_BALANCE_EXPIRE, sim.getBalanceExpire());

		return db.update(SIM_TABLE, values, SIM_ID + " = ?",
				new String[] { String.valueOf(sim.getID()) });
	}

	// Delete single row
	public void deleteSim(Sim sim) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(SIM_TABLE, SIM_ID + " = ?",
				new String[] { String.valueOf(sim.getID()) });
		db.close();
	}


	public int getSimCount() {
		String countQuery = "SELECT  * FROM " + SIM_TABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		return cursor.getCount();
	}
}
