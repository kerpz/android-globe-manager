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
	private static final String ACCOUNT_BALANCE_EXPIRE = "balance_expire";
	private static final String ACCOUNT_DATA = "data";
	private static final String ACCOUNT_DATA_EXPIRE = "data_expire";
	private static final String ACCOUNT_POINT = "point";
	private static final String ACCOUNT_POINT_EXPIRE = "point_expire";
	private static final String ACCOUNT_AUTO_REGISTER_ENABLE = "auto_register_enable";
	private static final String ACCOUNT_AUTO_REGISTER_DATE = "auto_register_date";

	//int id;
	String balance;
	String balance_expire;
	String data;
	String data_expire;
	String point;
	String point_expire;
	Boolean auto_register_enable;
	String auto_register_date;

	public AccountModel(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_ACCOUNT_TABLE = "CREATE TABLE " + ACCOUNT_TABLE + "("
				+ ACCOUNT_ID + " INTEGER PRIMARY KEY,"
				+ ACCOUNT_BALANCE + " TEXT,"
				+ ACCOUNT_BALANCE_EXPIRE + " TEXT,"
				+ ACCOUNT_DATA + " TEXT,"
				+ ACCOUNT_DATA_EXPIRE + " TEXT,"
				+ ACCOUNT_POINT + " TEXT,"
				+ ACCOUNT_POINT_EXPIRE + " TEXT,"
				+ ACCOUNT_AUTO_REGISTER_ENABLE + " INTEGER,"
				+ ACCOUNT_AUTO_REGISTER_DATE + " TEXT" + ")";
		db.execSQL(CREATE_ACCOUNT_TABLE);

		// Initial values
		ContentValues values = new ContentValues();
		values.put(ACCOUNT_ID, 0);
		values.put(ACCOUNT_BALANCE, "0.00");
		values.put(ACCOUNT_BALANCE_EXPIRE, "1970-01-01 00:00:00");
		values.put(ACCOUNT_DATA, "0");
		values.put(ACCOUNT_DATA_EXPIRE, "1970-01-01 00:00:00");
		values.put(ACCOUNT_POINT, "0");
		values.put(ACCOUNT_POINT_EXPIRE, "1970-01-01 00:00:00");
		values.put(ACCOUNT_AUTO_REGISTER_ENABLE, 0);
		values.put(ACCOUNT_AUTO_REGISTER_DATE, "1970-01-01 00:00:00");

		db.insert(ACCOUNT_TABLE, null, values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
		//onCreate(db);
	}


	public String getBalance() {
		return this.balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}
	
	public String getBalanceExpire() {
		return this.balance_expire;
	}
	
	public void setBalanceExpire(String balance_expire) {
		this.balance_expire = balance_expire;
	}

	public String getData() {
		return this.data;
	}
	
	public void setData(String data) {
		this.data = data;
	}

	public String getDataExpire() {
		return this.data_expire;
	}
	
	public void setDataExpire(String data_expire) {
		this.data_expire = data_expire;
	}

	public String getPoint() {
		return this.point;
	}
	
	public void setPoint(String point) {
		this.point = point;
	}

	public String getPointExpire() {
		return this.point_expire;
	}
	
	public void setPointExpire(String point_expire) {
		this.point_expire = point_expire;
	}

	public Boolean getAutoRegisterEnable() {
		return this.auto_register_enable;
	}

	public void setAutoRegisterEnable(Boolean auto_register_enable) {
		this.auto_register_enable = auto_register_enable;
	}
	
	public String getAutoRegisterDate() {
		return this.auto_register_date;
	}
	
	public void setAutoRegisterDate(String auto_register_date) {
		this.auto_register_date = auto_register_date;
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
				ACCOUNT_BALANCE, ACCOUNT_BALANCE_EXPIRE,
				ACCOUNT_DATA, ACCOUNT_DATA_EXPIRE,
				ACCOUNT_POINT, ACCOUNT_POINT_EXPIRE,
				ACCOUNT_AUTO_REGISTER_ENABLE, ACCOUNT_AUTO_REGISTER_DATE }, ACCOUNT_ID + "=?",
				new String[] { String.valueOf(0) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		//id = Integer.parseInt(cursor.getString(0));
		balance = cursor.getString(1);
		balance_expire = cursor.getString(2);
		data = cursor.getString(3);
		data_expire = cursor.getString(4);
		point = cursor.getString(5);
		point_expire = cursor.getString(6);
		auto_register_enable = cursor.getInt(7) == 0 ? false : true;
		auto_register_date = cursor.getString(8);
	}
	
	// Write
	public int writeSync() {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ACCOUNT_BALANCE, balance);
		values.put(ACCOUNT_BALANCE_EXPIRE, balance_expire);
		values.put(ACCOUNT_DATA, data);
		values.put(ACCOUNT_DATA_EXPIRE, data_expire);
		values.put(ACCOUNT_POINT, point);
		values.put(ACCOUNT_POINT_EXPIRE, point_expire);
		values.put(ACCOUNT_AUTO_REGISTER_ENABLE, auto_register_enable);
		values.put(ACCOUNT_AUTO_REGISTER_DATE, auto_register_date);

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
