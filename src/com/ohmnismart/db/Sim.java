package com.ohmnismart.db;

import android.os.Parcel;
import android.os.Parcelable;

public class Sim implements Parcelable {
	
	int id;
	String number;
	String expire = "1970-01-01 00:00:00";
	String balance = "0.0";
	String balance_expire = "1970-01-01 00:00:00";
	
	public Sim() {
	}
/*
	public Sim(int id, String number, String expire, String balance, String balance_expire) {
		this.id = id;
		this.number = number;
		this.expire = expire;
		this.balance = balance;
		this.balance_expire = balance_expire;
	}

	public Sim(String number, String expire, String balance, String balance_expire) {
		this.number = number;
		this.expire = expire;
		this.balance = balance;
		this.balance_expire = balance_expire;
	}
*/
	private Sim(Parcel in) {
		this.id = in.readInt();
		this.number = in.readString();
		this.expire = in.readString();
		this.balance = in.readString();
		this.balance_expire = in.readString();
	}

	public int getID() {
		return this.id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getExpire() {
		return this.expire;
	}

	public void setExpire(String expire) {
		this.expire = expire;
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

	@Override
	public String toString() {
		return "Sim [id=" + id + ", number=" + number + ", expire="
				+ expire + ", balance=" + balance + ", balance_expire=" + balance_expire + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sim other = (Sim) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(getID());
		parcel.writeString(getNumber());
		parcel.writeString(getExpire());
		parcel.writeString(getBalance());
		parcel.writeString(getBalanceExpire());
	}

	public static final Parcelable.Creator<Sim> CREATOR = new Parcelable.Creator<Sim>() {
		public Sim createFromParcel(Parcel in) {
			return new Sim(in);
		}

		public Sim[] newArray(int size) {
			return new Sim[size];
		}
	};

}
