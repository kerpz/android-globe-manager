package com.ohmnismart.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AccountModel extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "accountdb";
	private static final String ACCOUNT_TABLE = "account";
	private static final String ACCOUNT_ID = "id";
	private static final String ACCOUNT_BALANCE = "balance";
	private static final String ACCOUNT_DATA = "data";
	private static final String ACCOUNT_BALANCE_EXPIRE = "balance_expire";
	private static final String ACCOUNT_DATA_EXPIRE = "data_expire";
	private static final String ACCOUNT_AUTO_ENABLE = "auto_enable";

	//int id;
	Boolean auto_enable;
	String balance;
	String data;
	String balance_expire;
	String data_expire;

	public AccountModel(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_ACCOUNT_TABLE = "CREATE TABLE " + ACCOUNT_TABLE + "("
				+ ACCOUNT_ID + " INTEGER PRIMARY KEY,"
				+ ACCOUNT_AUTO_ENABLE + " INTEGER,"
				+ ACCOUNT_BALANCE + " TEXT,"
				+ ACCOUNT_DATA + " TEXT,"
				+ ACCOUNT_BALANCE_EXPIRE + " TEXT,"
				+ ACCOUNT_DATA_EXPIRE + " TEXT" + ")";
		db.execSQL(CREATE_ACCOUNT_TABLE);

		// Initial values
		ContentValues values = new ContentValues();
		values.put(ACCOUNT_ID, 0);
		values.put(ACCOUNT_AUTO_ENABLE, 0);
		values.put(ACCOUNT_BALANCE, "0.00");
		values.put(ACCOUNT_DATA, "0");
		values.put(ACCOUNT_BALANCE_EXPIRE, "1970-01-01 00:00:00");
		values.put(ACCOUNT_DATA_EXPIRE, "1970-01-01 00:00:00");

		db.insert(ACCOUNT_TABLE, null, values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
		//onCreate(db);
	}


	public Boolean getAutoEnable() {
		return this.auto_enable;
	}

	public void setAutoEnable(Boolean auto_enable) {
		this.auto_enable = auto_enable;
	}
	
	public String getBalance() {
		return this.balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}
	
	public String getData() {
		return this.data;
	}
	
	public void setData(String data) {
		this.data = data;
	}

	public String getBalanceExpire() {
		return this.balance_expire;
	}
	
	public void setBalanceExpire(String balance_expire) {
		this.balance_expire = balance_expire;
	}

	public String getDataExpire() {
		return this.data_expire;
	}
	
	public void setDataExpire(String data_expire) {
		this.data_expire = data_expire;
	}
	// Create
	/*
	public void addSIM(SIM sim) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ACCOUNT_BALANCE, sim.getBalance());
		values.put(ACCOUNT_DATA, sim.getData());
		values.put(ACCOUNT_BALANCE_EXPIRE, sim.getBalanceExpire());
		values.put(ACCOUNT_DATA_EXPIRE, sim.getDataExpire());

		db.insert(ACCOUNT_TABLE, null, values);
		db.close();
	}
	*/

	// Read
	public void readSync() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(ACCOUNT_TABLE, new String[] { ACCOUNT_ID,
				ACCOUNT_AUTO_ENABLE, ACCOUNT_BALANCE, ACCOUNT_DATA, ACCOUNT_BALANCE_EXPIRE, ACCOUNT_DATA_EXPIRE }, ACCOUNT_ID + "=?",
				new String[] { String.valueOf(0) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		//id = Integer.parseInt(cursor.getString(0));
		auto_enable = cursor.getInt(1) == 0 ? false : true;
		balance = cursor.getString(2);
		data = cursor.getString(3);
		balance_expire = cursor.getString(4);
		data_expire = cursor.getString(5);
	}
	
	// Write
	public int writeSync() {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ACCOUNT_AUTO_ENABLE, auto_enable);
		values.put(ACCOUNT_BALANCE, balance);
		values.put(ACCOUNT_DATA, data);
		values.put(ACCOUNT_BALANCE_EXPIRE, balance_expire);
		values.put(ACCOUNT_DATA_EXPIRE, data_expire);

		return db.update(ACCOUNT_TABLE, values, ACCOUNT_ID + " = ?",
				new String[] { String.valueOf(0) });
	}

	// Delete
	/*
	public void deleteSIM(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(ACCOUNT_TABLE, ACCOUNT_ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}


	public int getSIMCount() {
		String countQuery = "SELECT  * FROM " + ACCOUNT_TABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		return cursor.getCount();
	}
	 */
}
